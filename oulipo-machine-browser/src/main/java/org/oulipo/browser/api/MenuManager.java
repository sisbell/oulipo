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
package org.oulipo.browser.api;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.oulipo.storage.StorageException;

import java.io.IOException;
import java.util.Collection;

public interface MenuManager<T> {

	default void addToMenu(T item) throws StorageException, IOException {
		getMenu().getItems().add(createMenuItemFrom(item));
	}

	MenuItem createMenuItemFrom(T item) throws StorageException, IOException;

	Collection<T> getAll() throws StorageException, IOException;

	Menu getMenu();

	/**
	 * Loads items in menu
	 *
	 * @throws StorageException
	 * @throws IOException
	 */
	default void load() throws StorageException, IOException {
		for (T item : getAll()) {
			addToMenu(item);
		}
	}

}
