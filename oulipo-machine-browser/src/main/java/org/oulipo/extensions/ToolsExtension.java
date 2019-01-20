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

import org.oulipo.browser.api.BaseExtension;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Extension;
import org.oulipo.browser.api.MenuContext.Type;
import org.oulipo.browser.api.tabs.OulipoTab;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ToolsExtension extends BaseExtension implements Extension {

	@Override
	public void init(BrowserContext ctx) {
		addMenuItem(ctx, "IPFS Web Console", Type.TOOLS, e -> {
			WebView webView = new WebView();
			final WebEngine webEngine = webView.getEngine();
			webEngine.load("http://localhost:5001/webui");
			webEngine.javaScriptEnabledProperty().set(true);
			OulipoTab tab = new OulipoTab("IPFS Console");
			tab.setContent(webView);
			ctx.getTabManager().insert(ctx.getTabManager().size(), tab);
			ctx.getTabManager().selectTab(tab);
		});
	}

}
