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
package org.oulipo.extensions;

import java.io.IOException;

import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Extension;
import org.oulipo.browser.api.tabs.OulipoTab;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;

public class NewBrowseTabExtension implements Extension {

	private class CreateEventHandler implements EventHandler<Event> {

		@Override
		public void handle(Event event) {
			addViewTab();
		}
	}

	private BrowserContext ctx;

	FXMLLoader loader;

	private void addViewTab() {
		try {
			ctx.getTabManager().addTabWithAddressBar("", "New Tab");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void init(BrowserContext ctx) {
		this.ctx = ctx;
		this.loader = ctx.getLoader();

		MenuItem item = new MenuItem();
		item.setText("New Browse Tab");
		item.setOnAction(e -> {
			addViewTab();
		});

		ctx.getMenuContext().getFileMenu().getItems().add(0, item);

		OulipoTab tab = new OulipoTab("");
		tab.setImage("/images/ic_add_black_24dp_1x.png");
		ctx.getTabManager().add(tab);
		tab.setOnSelectionChanged(new CreateEventHandler());

		addViewTab();

	}

}
