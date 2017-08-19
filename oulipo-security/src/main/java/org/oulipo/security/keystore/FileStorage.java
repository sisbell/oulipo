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
package org.oulipo.security.keystore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

import org.bitcoinj.core.ECKey;
import org.oulipo.storage.StorageService;

import com.google.common.base.Strings;
import com.google.common.collect.ObjectArrays;

public class FileStorage implements Storage {

	private StorageService service;

	private static final byte[] key = "allAliases".getBytes();

	public FileStorage(StorageService service) {
		this.service = service;
	}

	public ECKey getECKey(String alias) throws UnsupportedEncodingException {
		if(Strings.isNullOrEmpty(alias)) {
			throw new IllegalArgumentException("alias is null");
		}
		byte[] pk = service.get(toKey(alias, "pk"));
		if(pk == null) {
			return null;
		}
		return ECKey.fromPrivate(pk);
	}
	
	@Override
	public void add(String alias, String address, byte[] privateKey,
			byte[] publicKey) throws IOException {

		byte[] value = service.get(key);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (value != null) {
			ByteArrayInputStream is = new ByteArrayInputStream(value);
			ObjectInputStream in = new ObjectInputStream(is);
			try {
				String[] values = (String[]) in.readObject();
				values = ObjectArrays.concat(alias, values);
				try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
					out.writeObject(values);
					out.flush();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			in.close();
		} else {
			try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
				out.writeObject(new String[] { alias });
				out.flush();
			}
		}

		service.put(key, baos.toByteArray());

		service.put(toKey(alias, "address"), address.getBytes("UTF-8"));
		service.put(toKey(alias, "pk"), privateKey);
		service.put(toKey(alias, "pub"), publicKey);
	}

	private static byte[] toKey(String alias, String field)
			throws UnsupportedEncodingException {
		return (alias + "!" + field).getBytes("UTF-8");
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public String[] getAliases() throws IOException {
		byte[] value = service.get(key);
		if (value == null) {
			return new String[0];
		}
		try (ObjectInputStream in = new ObjectInputStream(
				new ByteArrayInputStream(value))) {
			return (String[]) in.readObject();
		} catch (ClassNotFoundException e) {
			return new String[0];
		}
	}
}
