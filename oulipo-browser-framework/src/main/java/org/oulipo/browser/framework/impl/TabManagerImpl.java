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
import java.util.HashMap;
import java.util.Map;

import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.tabs.OulipoTab;
import org.oulipo.browser.api.tabs.TabManager;
import org.oulipo.storage.StorageService;

import com.google.common.base.Strings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabManagerImpl implements TabManager {

	private Map<OulipoTab, AddressBarController> controllers = new HashMap<>();

	private final BrowserContext ctx;

	private final TabPane tabs;

	private final StorageService tabStorage;

	public TabManagerImpl(BrowserContext ctx, StorageService tabStorage, TabPane tabs) {
		this.ctx = ctx;
		this.tabs = tabs;
		this.tabStorage = tabStorage;
	}

	@Override
	public void add(OulipoTab tab) {
		tabs.getTabs().add(tab);
	}

	@Override
	public OulipoTab addTabWithAddressBar(String address, String title) throws IOException {
		if (Strings.isNullOrEmpty(title)) {
			title = address;
		}
		OulipoTab tab = new OulipoTab(title);
		tab.setTumblerAddress(address);
		
		AddressBarController addressBarController = new AddressBarController(ctx.getApplicationContext());

		FXMLLoader loader = ctx.getLoader();
		loader.setLocation(getClass().getResource("/org/oulipo/browser/api/AddressBar.fxml"));
		loader.setController(addressBarController);

		Node node = loader.load();
		tab.setContent(node);
		add(tab);
		selectTab(tab);

		addressBarController.show(address, tab, ctx);
		return tab;
	}

	@Override
	public void backward(OulipoTab tab) {
		AddressBarController controller = controllers.get(tab);
		if (controller != null) {
			controller.back();
		}
	}

	@Override
	public void forward(OulipoTab tab) {
		AddressBarController controller = controllers.get(tab);
		if (controller != null) {
			controller.forward();
			;
		}
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
	public void remove(OulipoTab tab) {
		tabs.getTabs().remove(tab);
	}

	@Override
	public void removeSelectedTab() {
		remove(getSelectedTab());
	}

	@Override
	public void selectTab(OulipoTab tab) {
		SingleSelectionModel<Tab> selectionModel = tabs.getSelectionModel();
		selectionModel.select(tab);
	}

	@Override
	public void showInTab(OulipoTab tab, String address, String title) throws IOException {
		AddressBarController controller = controllers.get(tab);
		tab.setTumblerAddress(address);
		if (controller != null) {
			controller.show(address, tab, ctx);
		}
	}

	@Override
	public int size() {
		return tabs.getTabs().size();
	}

}
