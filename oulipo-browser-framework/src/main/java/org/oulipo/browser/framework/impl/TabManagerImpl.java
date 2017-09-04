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

import org.oulipo.browser.api.tabs.OulipoTab;
import org.oulipo.browser.api.tabs.TabManager;
import org.oulipo.storage.StorageService;

import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabManagerImpl implements TabManager {

	private final TabPane tabs;
	
	private final StorageService tabStorage;

	public TabManagerImpl(StorageService tabStorage, TabPane tabs) {
		this.tabs = tabs;
		this.tabStorage = tabStorage;
	}
	
	@Override
	public void add(OulipoTab tab) {
		tabs.getTabs().add(tab);
	}
	
	public void removeSelectedTab() {
		remove(getSelectedTab());
	}
	
	public void remove(OulipoTab tab) {
		tabs.getTabs().remove(tab);
	}

	@Override
	public OulipoTab getSelectedTab() {
		SingleSelectionModel<Tab> selectionModel = tabs.getSelectionModel();
		return (OulipoTab) selectionModel.getSelectedItem();
	}

	@Override
	public void insert(int position, OulipoTab tab) {
		tabs.getTabs().add(position, tab);
	}

	@Override
	public void selectTab(OulipoTab tab) {
		SingleSelectionModel<Tab> selectionModel = tabs.getSelectionModel();
		selectionModel.select(tab);
	}

	@Override
	public int size() {
		return tabs.getTabs().size();
	}

}