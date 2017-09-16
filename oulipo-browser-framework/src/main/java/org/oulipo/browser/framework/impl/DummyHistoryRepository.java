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
import java.util.ArrayList;

import org.oulipo.browser.api.history.History;
import org.oulipo.browser.api.history.HistoryRepository;
import org.oulipo.storage.StorageException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DummyHistoryRepository implements HistoryRepository {

	@Override
	public void add(History history) throws StorageException {

	}

	@Override
	public void delete(History history) throws StorageException {

	}

	@Override
	public History get(String id) throws StorageException {
		return null;
	}

	@Override
	public ObservableList<History> getAll() throws StorageException, IOException {
		return FXCollections.observableArrayList(new ArrayList<>());
	}

}
