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
package org.oulipo.browser.framework.impl;

import java.io.IOException;

import org.oulipo.browser.api.history.History;
import org.oulipo.browser.api.history.HistoryRepository;
import org.oulipo.storage.StorageException;
import org.oulipo.storage.StorageService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public final class HistoryRepositoryImpl implements HistoryRepository {

	private ObservableList<History> list;

	private final StorageService storageService;

	private Menu menu;

	private static MenuItem createMenuItemFrom(History history) {
		MenuItem item = new MenuItem();
		item.setText(history.title);
		item.setUserData(history);
		return item;
	}

	
	public HistoryRepositoryImpl(Menu menu, StorageService storageService) throws StorageException, IOException {
		this.storageService = storageService;
		this.menu = menu;
		try {
			list = FXCollections.observableArrayList(storageService.getAll(History.class));
		} catch (ClassNotFoundException e) {

		}
	}

	@Override
	public void add(History history) throws StorageException {
		list.add(history);
		menu.getItems().add(createMenuItemFrom(history));
		storageService.save(history);
	}

	@Override
	public void delete(History history) throws StorageException {
		list.remove(history);
		storageService.delete(history.id, History.class);
	}

	@Override
	public History get(String id) throws StorageException {
		return storageService.load(id, History.class);
	}

	@Override
	public ObservableList<History> getAll() throws StorageException, IOException {
		return list;
	}
}
