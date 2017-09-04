package org.oulipo.extensions.meta;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Extension;
import org.oulipo.browser.api.tabs.OulipoTab;
import org.oulipo.client.services.DocuverseService;
import org.oulipo.client.services.ServiceBuilder;
import org.oulipo.client.services.TedRouter;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import org.oulipo.resources.model.Node;

import com.jfoenix.controls.JFXListView;

public class MetaExtension implements Extension {

	@Override
	public void init(BrowserContext ctx) {
		DocuverseService service = new ServiceBuilder("http://localhost:4567/docuverse/")
				.publicKey("1GNHSBPgd7x4AosHDys7x2tbFinLz4Qq5Z").sessionToken("i9Kpn6mvkImat7Jm7T4HL7OUjlgO0lr7")
				.build(DocuverseService.class);
		TedRouter tedRouter = new TedRouter(service);

		MenuItem item = new MenuItem();
		item.setText("Show nodes");
		item.setOnAction(e -> {
			try {
				tedRouter.routeGetRequest(TumblerAddress.create("1/nodes"), response -> {
					if (response.isSuccessful()) {
						List<Node> nodes = (List<Node>) response.body();
						Platform.runLater(() -> {
							OulipoTab tab = new OulipoTab();
							tab.setTitle("Nodes");
							tab.setText("Nodes");
							JFXListView<Label> list = new JFXListView<Label>();
							AnchorPane pane = new AnchorPane();
							pane.getChildren().add(list);
							for (Node node : nodes) {
								Label label = new Label(node.resourceId.value);
								label.setOnMouseClicked(c -> {
									try {
										tedRouter
										.routeGetRequest(TumblerAddress.create(node.resourceId.value), c1 -> {
										//	c1.body()
										});
									} catch (IOException | MalformedSpanException | URISyntaxException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								});
								list.getItems().add(label);
							}
							tab.setContent(pane);
							int tabCount = ctx.getTabManager().size();
							ctx.getTabManager().insert(tabCount - 1, tab);
							ctx.getTabManager().selectTab(tab);
						});
					}
				});
			} catch (IOException | MalformedSpanException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		});
		ctx.getMenuContext().getFileMenu().getItems().add(0, item);
	}

}
