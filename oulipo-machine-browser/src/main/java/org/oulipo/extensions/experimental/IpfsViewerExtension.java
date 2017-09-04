package org.oulipo.extensions.experimental;

import org.oulipo.browser.api.BaseExtension;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Extension;
import org.oulipo.browser.api.tabs.OulipoTab;
import org.oulipo.browser.framework.MenuContext.Type;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class IpfsViewerExtension extends BaseExtension implements Extension {

	@Override
	public void init(BrowserContext ctx) {
		addMenuItem(ctx, "IPFS Web Console", Type.TOOLS, e -> {
			WebView webView = new WebView();
	        final WebEngine webEngine = webView.getEngine();
	        webEngine.load("http://localhost:5001/webui");
	        webEngine.javaScriptEnabledProperty().set(true);
	        OulipoTab tab = new OulipoTab();
	        tab.setText("IPFS Console");
	        tab.setContent(webView);
			ctx.getTabManager().insert(ctx.getTabManager().size(), tab);
			ctx.getTabManager().selectTab(tab);
		});
	}

}
