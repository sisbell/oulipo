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
import org.oulipo.browser.api.tabs.OulipoTab;
import org.oulipo.browser.framework.MenuContext.Type;
import org.oulipo.browser.pages.RegisterNodeController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class ManagerExtension extends BaseExtension implements Extension {

	@Override
	public void init(BrowserContext ctx) {
		addMenuItem(ctx, "Register Node", Type.MANAGER, e -> {
			FXMLLoader loader = ctx.getLoader();
			loader.setLocation(getClass().getResource("/org/oulipo/browser/pages/RegisterNodeView.fxml"));

			OulipoTab tab = new OulipoTab("Register Node");
			try {
				tab.setContent((Node) loader.load());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			RegisterNodeController controller = loader.getController();
			controller.setContext(ctx);

			ctx.getTabManager().add(tab);
			ctx.getTabManager().selectTab(tab);
		});
	}

}
