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
package org.oulipo.browser.routers;

import java.io.IOException;

import org.oulipo.browser.api.Page;
import org.oulipo.browser.api.PageRouter;
import org.oulipo.browser.api.Scheme;
import org.oulipo.browser.api.tabs.TabManager;
import org.oulipo.browser.pages.ViewSourceController;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

@Scheme("view-source")
public class ViewSourcePageRouter implements PageRouter {

	public static void showPageSource(TabManager tabManager, TumblerAddress address, Node content) {
		final ContextMenu contextMenu = new ContextMenu();
		MenuItem viewSource = new MenuItem("View Page Source");
		viewSource.setOnAction(e -> {
			try {
				tabManager.addTabWithAddressBar("view-source:" + address.toExternalForm(),
						"view-source:" + address.toTumblerFields());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		contextMenu.getItems().addAll(viewSource);

		content.setOnMousePressed(event -> {
			// if (event.isSecondaryButtonDown()) {
			contextMenu.show(content, event.getScreenX(), event.getScreenY());
			// }

		});

	}

	@Override
	public Page getPage(String tumbler, String body) throws IOException, MalformedSpanException {
		return new Page(new ViewSourceController(tumbler));
	}

}
