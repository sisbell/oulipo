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
package org.oulipo.browser.pages;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.textfield.CustomTextField;
import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.api.Page;
import org.oulipo.browser.tables.ButtonsCreator;
import org.oulipo.net.MalformedTumblerException;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class SearchController implements Page.Controller {

	@FXML
	AnchorPane pane;

	@FXML
	CustomTextField searchField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		pane.setStyle("-fx-background-color: white");
	}

	@Override
	public void show(AddressBarController addressBarController) throws MalformedTumblerException, IOException {
		ImageView searchView = new ImageView();
		searchView.setFitWidth(20);
		searchView.setFitHeight(20);
		searchView.setImage(new Image("/images/ic_search_black_24dp_1x.png"));
		addressBarController.addLeftAddressBar(searchView);

		searchField.setText(addressBarController.getAddressBoxText());
		searchField.setRight(ButtonsCreator.search(addressBarController.getContext(), searchField));
		searchField.setOnAction(e -> {

		});
	}

}
