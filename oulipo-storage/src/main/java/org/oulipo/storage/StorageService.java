/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *******************************************************************************/
package org.oulipo.storage;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import com.google.common.base.Strings;

/**
 * Service for loading and storing objects
 *
 */
public final class StorageService {

	private static String getId(Object obj, Field[] fields) throws StorageException {
		for (Field field : fields) {
			if (hasId(field)) {
				field.setAccessible(true);
				try {
					return (String) field.get(obj);
				} catch (Exception e) {
					throw new StorageException(e);
				}
			}
		}
		return null;
	}

	private static boolean hasId(Field field) {
		return field.isAnnotationPresent(Id.class);
	}

	private DB db;

	protected StorageService(DB db) {
		this.db = db;
	}

	public StorageService(String name) throws IOException {
		Options options = new Options();
		options.createIfMissing(true);
		db = factory.open(new File(name), options);
	}

	public void close() {
		try {
			db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public <T> void delete(String id, Class<T> clazz) throws StorageException {

		if (Strings.isNullOrEmpty(id)) {
			throw new IllegalArgumentException("Id is null");
		}

		if (clazz == null) {
			throw new IllegalArgumentException("Class is null");
		}

		String key = id + "!" + clazz.getName();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			db.delete(bytes(key + "!" + field.getName()));
		}
	}

	public byte[] get(byte[] key) {
		return db.get(key);
	}

	/**
	 * Gets all objects of the specified class type
	 *
	 * @param clazz
	 * @return
	 * @throws ClassNotFoundException
	 * @throws StorageException
	 * @throws IOException
	 */
	public <T> Collection<T> getAll(Class<T> clazz) throws ClassNotFoundException, StorageException, IOException {
		List<T> c = new ArrayList<>();
		Map<String, String> ids = new HashMap<>();
		try (DBIterator it = db.iterator()) {
			it.seekToFirst();

			String prevId = null;
			while (it.hasNext()) {
				String key = new String(it.next().getKey());
				String[] tokens = key.split("!");

				String id = tokens[0];
				String className = tokens[1];
				if (!id.equals(prevId)) {
					if (clazz.getName().equals(className)) {
						ids.put(id, className);
					}
					prevId = id;
				}
			}
			for (Map.Entry<String, String> id : ids.entrySet()) {
				T o = (T) load(id.getKey(), Class.forName(id.getValue()));
				c.add(o);
			}
		}
		return c;
	}

	public <T> T load(String id, Class<T> clazz) throws StorageException {

		if (Strings.isNullOrEmpty(id)) {
			throw new IllegalArgumentException("Id is null");
		}

		if (clazz == null) {
			throw new IllegalArgumentException("Class is null");
		}

		T object;
		try {
			object = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new StorageException("Unable to create entity", e);
		}

		String key = id + "!" + clazz.getName();
		boolean exists = false;
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			String value = asString(db.get(bytes(key + "!" + field.getName())));
			if (Strings.isNullOrEmpty(value) || "null".equals(value)) {
				continue;
			}
			exists = true;

			try {
				if (field.getType().equals(String.class)) {
					field.set(object, value);
				} else if (field.getType().equals(Date.class) && value != null) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
					field.set(object, df.parse(value));
				} else if (field.getType().equals(Integer.class)) {
					field.set(object, Integer.valueOf(value));
				} else if (field.getType().equals(Long.class)) {
					field.set(object, Long.valueOf(value));
				} else if (field.getType().equals(Boolean.class)) {
					field.set(object, Boolean.valueOf(value));
				}
			} catch (IllegalArgumentException | IllegalAccessException | ParseException e) {
				throw new StorageException("Unreconizable field", e);
			}
		}

		if (!exists) {
			throw new StorageException("Entity not found: " + id);
		}
		return object;
	}

	public void put(byte[] key, byte[] value) {
		db.put(key, value);
	}

	private void put(String id, Object object, Field field) throws StorageException {

		String className = object.getClass().getName();

		field.setAccessible(true);
		Object obj;
		try {
			obj = field.get(object);
		} catch (Exception e) {
			throw new StorageException(e);
		}

		if (obj instanceof String) {
			db.put(bytes(id + "!" + className + "!" + field.getName()), bytes((String) obj));
		} else if (obj instanceof Date) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			String date = df.format((Date) obj).toString();
			db.put(bytes(id + "!" + className + "!" + field.getName()), bytes(date));
		} else {
			db.put(bytes(id + "!" + className + "!" + field.getName()), bytes(String.valueOf(obj)));
		}
	}

	public void save(Object entity) throws StorageException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity is null");
		}
		Class<?> clazz = entity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		String id = getId(entity, fields);
		if (Strings.isNullOrEmpty(id)) {
			throw new StorageException("Object id is null");
		}
		for (Field field : fields) {
			put(id, entity, field);
		}
	}

}
