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
import org.oulipo.browser.api.MenuContext.Type;
import org.oulipo.browser.api.people.CurrentUser;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class ManagerExtension extends BaseExtension implements Extension {

	@Override
	public void init(BrowserContext ctx) {

		Menu showNodes = addMenu(ctx, "Show Nodes", Type.MANAGER);
		MenuItem mainNetNodes = new MenuItem();
		mainNetNodes.setText("MainNet");
		mainNetNodes.setOnAction(e -> {
			try {
				ctx.getTabManager().addTabWithAddressBar("ted://1/nodes", "MainNet Nodes");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});
		MenuItem testNetNodes = new MenuItem();
		testNetNodes.setText("TestNet");
		testNetNodes.setOnAction(e -> {
			try {
				ctx.getTabManager().addTabWithAddressBar("ted://2/nodes", "TestNet Nodes");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});

		showNodes.getItems().addAll(mainNetNodes, testNetNodes);

		addMenuItem(ctx, "My Documents", Type.MANAGER, e -> {
			try {
				CurrentUser currentUser = ctx.getCurrentUser();
				if (currentUser != null) {
					ctx.getTabManager().addTabWithAddressBar(currentUser.address + "/documents", "My Documents");

				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});

		addMenuItem(ctx, "New Document", Type.MANAGER, e -> {

		});

		addMenuItem(ctx, "Register Node", Type.MANAGER, e -> {
			try {
				ctx.getTabManager().addTabWithAddressBar("edit://1", "Nodes");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

	}

}
