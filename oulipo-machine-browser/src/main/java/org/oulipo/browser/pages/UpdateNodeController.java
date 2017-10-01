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

import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.controls.OulipoTable;
import org.oulipo.browser.routers.ViewSourcePageRouter;
import org.oulipo.browser.tables.ButtonsCreator;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.resources.model.Node;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.scene.control.Button;
import retrofit2.Call;
import retrofit2.Response;

public class UpdateNodeController extends BaseController {

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void show(AddressBarController controller) throws MalformedTumblerException, IOException {
		super.show(controller);
		// Case 1: Node does not exist
		// Case 2: Node exists but is not owned by user
		// Case 3: Node exists and is owned by user
		tumblerService.getNode(address.toTumblerAuthority(), new retrofit2.Callback<Node>() {

			@Override
			public void onFailure(Call<Node> arg0, Throwable arg1) {
				arg1.printStackTrace();
			}

			@Override
			public void onResponse(Call<Node> arg0, Response<Node> response) {
				if (response.isSuccessful()) {
					final Node node = response.body();

					Platform.runLater(() -> {
						try {
							JFXButton submit = new JFXButton("Update");

							OulipoTable table = new OulipoTable(300, 350)
									.addMaterialEditText("Tumbler Address", address.toTumblerFields(), false)
									.addMaterialEditText("Public Key", node.publicKey, false)
									.addMaterialEditText("Node Name", node.nodeName)
									.addMaterialEditText("Organization Name", node.organizationName)
									.addMaterialEditText("Email", node.email)
									.addCheckBox("Allow User Creation", node.allowUserToCreateAccount)
									.addActions(submit);

							submit.setOnAction(e -> {
								node.nodeName = table.getValue("Node Name");
								node.organizationName = table.getValue("Organization Name");
								node.email = table.getValue("Email");
								try {
									tumblerService.createOrUpdateNode(node, new retrofit2.Callback<Node>() {

										@Override
										public void onFailure(Call<Node> arg0, Throwable arg1) {

										}

										@Override
										public void onResponse(Call<Node> arg0, Response<Node> arg1) {
											address.setScheme("ted");
											Platform.runLater(() -> {
												try {
													addressBarController.show(address.toExternalForm());
												} catch (IOException e) {
													e.printStackTrace();
												}
											});

										}

									});
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							});

							ViewSourcePageRouter.showPageSource(ctx.getTabManager(), address, table);
							addressBarController.addContent(table, "Update Node");

							if (node.allowUserToCreateAccount) {
								Button addUserBtn = ButtonsCreator.addUser(ctx, address.toTumblerFields(),
										address.toTumblerFields());
								addressBarController.addRightAddressBar(addUserBtn);
							}

						} catch (MalformedTumblerException e1) {
							e1.printStackTrace();
							return;
						}
					});
				} else {
					Platform.runLater(() -> {
						OulipoTable table = new OulipoTable(300, 350);
						try {
							table.addMaterialEditText("Tumbler Address", address.toTumblerFields(), false);
						} catch (MalformedTumblerException e) {
							e.printStackTrace();
						}
						table.addMaterialEditText("Public Key", ctx.getAccountManager().getActiveAccount().publicKey, false)
								.addMaterialEditText("Node Name", "").addMaterialEditText("Organization Name", "")
								.addMaterialEditText("Email", "").addCheckBox("Allow User Creation", false);

						addressBarController.addContent(table, "Create Node");

					});

				}
			}
		});
	}

}
