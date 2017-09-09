/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
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
package org.oulipo.browser.api.storage;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.oulipo.browser.api.Repository;
import org.oulipo.storage.Id;
import org.oulipo.storage.StorageException;
import org.oulipo.storage.StorageService;

public final class SessionStorage implements Repository<org.oulipo.browser.api.storage.SessionStorage.Property> {

	private StorageService service;

	public SessionStorage(StorageService service) {
		this.service = service;
	}

	public static class Property {

		@Id
		public String key;

		public String value;

		public Property() {
		}

		public Property(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString() {
			return "Property [key=" + key + ", value=" + value + "]";
		}
	}

	@Override
	public void add(Property item) throws StorageException, IOException {
		service.save(item);
	}

	@Override
	public String addItemImage(Property item, File file) throws IOException, StorageException {
		return null;
	}

	@Override
	public void delete(Property item) throws StorageException {
		service.delete(item.key, Property.class);
	}

	@Override
	public Property get(String id) throws StorageException, IOException {
		return service.load(id, Property.class);
	}

	@Override
	public Collection<Property> getAll() throws StorageException, IOException {
		try {
			return service.getAll(Property.class);
		} catch (ClassNotFoundException e) {
			throw new StorageException(e.getMessage());
		}
	}

}
