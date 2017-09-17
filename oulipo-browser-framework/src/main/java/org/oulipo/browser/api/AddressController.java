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
package org.oulipo.browser.api;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.textfield.CustomTextField;
import org.oulipo.browser.api.tabs.OulipoTab;
import org.oulipo.browser.framework.HistoryController;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * One instance of this class per open tab
 */
public class AddressController implements Initializable {

	CustomTextField addressBox;
	
	@FXML
	StackPane addressPane;

	private ApplicationContext applicationContext;

	@FXML
	JFXButton backBtn;
	
	@FXML
	VBox content;
	
	private BrowserContext ctx;
	
	@FXML
	JFXButton forwardBtn;

	private Node headerNode;

	private HistoryController historyController;

	private OulipoTab tab;

	@Inject
	public AddressController(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public void addContent(Node node) {
		content.getChildren().clear();
		if (headerNode != null) {
			content.getChildren().add(headerNode);
		}

		content.getChildren().add(node);
	}

	public void addContent(Node node, Node headerNode) {
		this.headerNode = headerNode;
		content.getChildren().clear();
		content.getChildren().add(headerNode);
		content.getChildren().add(node);
	}

	public void addContent(Node node, String header) {
		content.getChildren().clear();
		Label label = new Label(header);
		label.setId("table-title");
		headerNode = label;
		content.getChildren().add(headerNode);
		content.getChildren().add(node);
	}
	

	public void addLeftAddressBar(Node node) {
		addressBox.setLeft(node);
	}

	public void addRightAddressBar(Node node) {
		addressBox.setRight(node);
	}

	@FXML
	public void back() {
		historyController.back();
	}
	
	@FXML
	public void forward() {
		historyController.forward();
	}

	public String getAddressBoxText() {
		return addressBox.getText();
	}

	public BrowserContext getContext() {
		return ctx;
	}

	public TumblerAddress getTumbler() throws MalformedTumblerException {
		return TumblerAddress.create(addressBox.getText());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		content.setStyle("-fx-background-color: white");
		addressBox = new CustomTextField();
		historyController = new HistoryController(forwardBtn, backBtn, addressBox, this);

		addressBox.setOnAction(e -> {
			historyController.visit(addressBox.getText());
			refresh();
		});
		addressBox.requestFocus();
		addressPane.getChildren().add(addressBox);
		

	}

	@FXML
	public void refresh() {	
		content.getChildren().clear();
		addressPane.getChildren().clear();
		if (headerNode != null) {
			content.getChildren().add(headerNode);
		}
		try {
			String iri = addressBox.getText();
			addressBox.requestFocus();

			if (Strings.isNullOrEmpty(iri)) {
				iri = "search://";
			}
			addressPane.getChildren().add(addressBox);

			int index = iri.indexOf(":");

			String scheme = index != -1 ? iri.substring(0, index) : "ted";
			Page page = null;
			try {
				page = applicationContext.getRouter(scheme).getPage(iri, null);
			} catch (Exception e1) {

			}
			
			if(page == null) {
				page = applicationContext.getRouter("search").getPage(iri, null);
			}
			
			page.present(this);
		} catch (Exception e) {
			// Toast message
			e.printStackTrace();
		}
	}


	public void setTabTitle(String title) {
		this.tab.setText(title);
	}

	public void setTumbler(TumblerAddress tumbler) {
		addressBox.setText(tumbler.value);
		refresh();
	}
	
	public void show(String address) throws MalformedTumblerException, IOException {
		historyController.visit(address);
		this.headerNode = null;
		addressBox.setText(address);
		addressBox.requestFocus();
		refresh();
	}

	public void show(String address, OulipoTab tab, BrowserContext ctx) throws MalformedTumblerException, IOException {
		historyController.visit(address);
		this.ctx = ctx;
		this.tab = tab;
		this.headerNode = null;
		addressBox.setText(address);
		addressBox.requestFocus();
		refresh();
	}
}
