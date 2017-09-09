package org.oulipo.browser.framework;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Page;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;

import com.google.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AddressController implements Initializable {

	@FXML
	TextField addressBox;

	@FXML
	VBox content;

	private BrowserContext ctx;

	private PageRouter router;

	 @Inject
	public AddressController(PageRouter router) {
		this.router = router;
	}
	
	public void addContent(Node node) {
		content.getChildren().clear();
		content.getChildren().add(node);
	}

	@FXML
	public void back() {

	}

	@FXML
	public void forward() {

	}

	public BrowserContext getContext() {
		return ctx;
	}

	public TumblerAddress getTumbler() throws MalformedTumblerException {
		return TumblerAddress.create(addressBox.getText());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addressBox.setOnAction(e-> {
			refresh();
		});
	}

	@FXML
	public void refresh() {
		content.getChildren().clear();
		try {
			Page page = router.getPage(addressBox.getText());
			page.present(this);
		} catch (IOException e) {
			// Toast message
			e.printStackTrace();
		} catch (MalformedSpanException e) {
			e.printStackTrace();
		}
	}

	public void setTumbler(TumblerAddress tumbler) {
		addressBox.setText(tumbler.value);
		refresh();
	}

	public void show(String address, BrowserContext ctx) throws MalformedTumblerException, IOException {
		this.ctx = ctx;
		addressBox.setText(address);
		addressBox.requestFocus();
		refresh();
	}
}
