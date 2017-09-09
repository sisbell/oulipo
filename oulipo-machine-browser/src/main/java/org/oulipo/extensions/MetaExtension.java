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

import javafx.scene.control.MenuItem;

public class MetaExtension implements Extension {

	@Override
	public void init(BrowserContext ctx) {
		MenuItem item = new MenuItem();
		item.setText("Show Nodes");
		item.setOnAction(e -> {
			try {
				ctx.getTabManager().addTabWithAddressBar("ted://1/nodes", "Nodes");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});
		ctx.getMenuContext().getManagerMenu().getItems().add(0, item);
		/**
		 * int tabCount = ctx.getTabManager().size();
		 * ctx.getTabManager().insert(tabCount - 1, tab);
		 * ctx.getTabManager().selectTab(tab);
		 * 
		 */
	}
}
