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

import org.oulipo.browser.api.AddressController;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Page;
import org.oulipo.client.services.DocuverseService;
import org.oulipo.client.services.TumblerService;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Node;
import org.oulipo.storage.StorageException;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.fxml.FXML;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterNodeController implements Page.Controller {

	@FXML
	public JFXCheckBox createAccounts;

	BrowserContext ctx;

	@FXML
	public JFXTextField nodeField;

	@FXML
	public JFXTextField nodeName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void setContext(BrowserContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void show(AddressController addressController) throws MalformedTumblerException, IOException {

	}

	@FXML
	public void submit() {

		DocuverseService service;
		try {
			service = ctx.getDocuverseService();
		} catch (StorageException e2) {
			e2.printStackTrace();
			return;
		}
		Node node = new Node();
		node.nodeName = nodeName.getText();
		node.allowUserToCreateAccount = createAccounts.isSelected();

		try {
			node.resourceId = TumblerAddress.create("1." + nodeField.getText());
		} catch (MalformedTumblerException e1) {
			e1.printStackTrace();
		}

		TumblerService tumblerService = new TumblerService(service);
		try {
			tumblerService.createOrUpdateNode(node, new Callback<Node>() {

				@Override
				public void onFailure(Call<Node> arg0, Throwable arg1) {
					Platform.runLater(() -> {
						JFXSnackbar bar = new JFXSnackbar(ctx.getContentArea());
						bar.enqueue(new SnackbarEvent("Failed to create node"));
					});
				}

				@Override
				public void onResponse(Call<Node> node, Response<Node> response) {
					System.out.println(response.body());
					Platform.runLater(() -> {
						JFXSnackbar bar = new JFXSnackbar(ctx.getContentArea());
						if (response.isSuccessful()) {
							bar.enqueue(new SnackbarEvent("Registered Node: " + response.body().resourceId.value));
						} else {
							bar.enqueue(new SnackbarEvent(response.message()));
						}
					});
				}
			});
		} catch (Exception e) {
			JFXSnackbar bar = new JFXSnackbar(ctx.getContentArea());
			bar.enqueue(new SnackbarEvent("Failed to create node"));
			e.printStackTrace();
		}
	}

}
