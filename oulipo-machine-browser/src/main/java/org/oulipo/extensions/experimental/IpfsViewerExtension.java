package org.oulipo.extensions.experimental;

import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Extension;
import org.oulipo.browser.api.tabs.OulipoTab;

import javafx.scene.control.MenuItem;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class IpfsViewerExtension implements Extension {

	@Override
	public void init(BrowserContext ctx) {
		MenuItem item = new MenuItem();
		item.setText("IPFS Web Console");
		item.setOnAction(e -> {
			WebView webView = new WebView();
	        final WebEngine webEngine = webView.getEngine();
	        webEngine.load("http://localhost:5001/webui");
	        
	        OulipoTab tab = new OulipoTab();
	        tab.setText("IPFS Console");
	        tab.setContent(webView);
			ctx.getTabManager().insert(ctx.getTabManager().size(), tab);
			ctx.getTabManager().selectTab(tab);
		});
		ctx.getMenuContext().getToolsMenu().getItems().add(item);
		
	
	}

}
