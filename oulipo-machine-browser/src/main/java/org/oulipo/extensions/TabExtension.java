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

import org.oulipo.browser.api.BaseExtension;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Extension;
import org.oulipo.browser.api.MenuContext;
import org.oulipo.browser.api.people.Account;
import org.oulipo.browser.api.people.CurrentUser;
import org.oulipo.browser.api.tabs.OulipoTab;
import org.oulipo.net.TumblerAddress;

import javafx.event.Event;
import javafx.event.EventHandler;

public class TabExtension extends BaseExtension implements Extension {

	private class CreateEventHandler implements EventHandler<Event> {

		@Override
		public void handle(Event event) {
			addNewTab();
		}
	}

	private BrowserContext ctx;

	private void addEditDocTab() {
		CurrentUser user = ctx.getCurrentUser();
		if (user != null) {
			try {
				TumblerAddress address = TumblerAddress.create(user.address);
				address.setScheme("edit");
				ctx.getTabManager().addTabWithAddressBar(address.toExternalForm() + "/documents", "New Document");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void addNewTab() {
		try {
			ctx.getTabManager().addTabWithAddressBar("", "New Tab");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void init(BrowserContext ctx) {
		this.ctx = ctx;
		addMenuItem(ctx, "New Browse Tab", MenuContext.Type.FILE, e -> {
			addNewTab();
		});

		OulipoTab tab = new OulipoTab("");
		tab.setImage("/images/ic_add_black_24dp_1x.png");
		ctx.getTabManager().add(tab);
		tab.setOnSelectionChanged(new CreateEventHandler());

		Account currentAccount = ctx.getAccountManager().getActiveAccount();
		if (currentAccount != null) {
			// currentAccount.
			addMenuItem(ctx, "New Document Tab", MenuContext.Type.FILE, e -> {
				addEditDocTab();
			});

			OulipoTab editTab = new OulipoTab("");
			editTab.setImage("/images/ic_mode_edit_black_24dp_1x.png");
			ctx.getTabManager().add(editTab);
			tab.setOnSelectionChanged(e -> {
				addEditDocTab();
			});

		}

		addNewTab();

	}

}
