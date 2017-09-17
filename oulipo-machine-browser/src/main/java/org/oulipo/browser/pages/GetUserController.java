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
import org.oulipo.resources.model.User;

import javafx.application.Platform;
import javafx.scene.layout.HBox;
import retrofit2.Call;
import retrofit2.Response;

public class GetUserController extends BaseController {

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void show(AddressBarController controller) throws MalformedTumblerException, IOException {
		super.show(controller);
		tumblerService.getUser(address.toTumblerAuthority(), new retrofit2.Callback<User>() {

			@Override
			public void onFailure(Call<User> arg0, Throwable arg1) {
				arg1.printStackTrace();
			}

			@Override
			public void onResponse(Call<User> arg0, Response<User> response) {
				if (response.isSuccessful()) {
					final User user = response.body();

					Platform.runLater(() -> {
						try {
							OulipoTable table = new OulipoTable(300, 350)
									.addText("Tumbler Address", address.toTumblerFields())
									.addText("Public Key", user.publicKey).addText("Xandle", user.xandle);

							HBox box = new HBox();
							if (ctx.ownsResource(user.publicKey)) {
								// ctx.getAccountManager().
								box.getChildren().add(ButtonsCreator.signin(addressBarController, ctx,
										address.toExternalForm(), user.xandle));
							}
							addressBarController.addRightAddressBar(box);

							ViewSourcePageRouter.showPageSource(ctx.getTabManager(), address, table);
							addressBarController.addContent(table, "View User");

						} catch (MalformedTumblerException e1) {
							e1.printStackTrace();
							return;
						}
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
