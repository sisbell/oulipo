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

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.scene.layout.HBox;
import retrofit2.Call;
import retrofit2.Response;

public class GetNodeController extends BaseController {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void show(AddressBarController controller) throws MalformedTumblerException, IOException {
		super.show(controller);
		tumblerService.getNode(address.toTumblerAuthority(), new retrofit2.Callback<Node>() {

			@Override
			public void onFailure(Call<Node> arg0, Throwable arg1) {

			}

			@Override
			public void onResponse(Call<Node> arg0, Response<Node> response) {
				if (response.isSuccessful()) {
					final Node node = response.body();

					Platform.runLater(() -> {
						try {
							OulipoTable table = new OulipoTable(300, 300)
									.addText("Tumbler Address", address.toTumblerFields())
									.addText("Public Key", node.publicKey).addText("Node Name", node.nodeName)
									.addText("Organization Name", node.organizationName).addText("Email", node.email)
									.addCheckBox("Allow User Creation", node.allowUserToCreateAccount);

							HBox box = new HBox();
							if (ctx.ownsResource(node.publicKey)) {
								address.setScheme("edit");
								box.getChildren()
										.add(ButtonsCreator.editNode(addressBarController, address.toExternalForm()));
								box.getChildren().add(ButtonsCreator.addUser(ctx, address.toExternalForm(), ""));
							}
							box.getChildren().add(ButtonsCreator.showUsers(ctx, address.toExternalForm(), "", false,
									addressBarController));
							addressBarController.addRightAddressBar(box);

							ViewSourcePageRouter.showPageSource(ctx.getTabManager(), address, table);
							addressBarController.addContent(table, "View Node");

						} catch (MalformedTumblerException e) {
							e.printStackTrace();
						}
						/*
						 * if (node.allowUserToCreateAccount) { try { Button addUserBtn =
						 * ButtonsCreator.addUser(ctx, address.toTumblerFields(),
						 * address.toTumblerFields()); addressBarController.addRightAddressBar(addUserBtn);
						 * } catch (MalformedTumblerException e) { e.printStackTrace(); } }
						 */
					});
				} else {
					address.setScheme("edit");
					Platform.runLater(() -> {
						try {
							addressBarController.show(address.toExternalForm());
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				}
			}
		});
	}

}
