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
import org.oulipo.resources.model.Document;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import retrofit2.Call;
import retrofit2.Response;

public class UpdateDocumentController extends BaseController {

	private void attachNewAction(Button button, Document document, OulipoTable table) {
		button.setOnAction(e -> {
			// TODO: implement newVersion client interface
		});
	}

	private void attachSubmitAction(Button button, Document document, OulipoTable table) {
		button.setOnAction(e -> {
			document.title = table.getValue("Title");
			document.description = table.getValue("Description");
			try {
				tumblerService.createOrUpdateDocument(document, new retrofit2.Callback<Document>() {

					@Override
					public void onFailure(Call<Document> arg0, Throwable arg1) {

					}

					@Override
					public void onResponse(Call<Document> arg0, Response<Document> arg1) {
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
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void show(AddressBarController controller) throws MalformedTumblerException, IOException {
		super.show(controller);

		tumblerService.getDocument(address.toTumblerAuthority(), new retrofit2.Callback<Document>() {

			@Override
			public void onFailure(Call<Document> arg0, Throwable arg1) {
				arg1.printStackTrace();
			}

			@Override
			public void onResponse(Call<Document> arg0, Response<Document> response) {
				JFXButton submit = new JFXButton("Update");
				JFXButton newVersion = new JFXButton("New Version");

				if (response.isSuccessful()) {
					final Document document = response.body();

					Platform.runLater(() -> {
						try {
							OulipoTable table = new OulipoTable(300, 350)
									.addMaterialEditText("Tumbler Address", address.toTumblerFields(), false)
									.addMaterialEditText("Title", document.title)
									.addMaterialEditText("Description", document.description).addActions(newVersion, submit);
							attachSubmitAction(submit, document, table);

							// TODO: Does user own this document?
							HBox box = new HBox();
							box.getChildren().add(ButtonsCreator.writeDocument(addressBarController, address));
							addressBarController.addRightAddressBar(box);

							ViewSourcePageRouter.showPageSource(ctx.getTabManager(), address, table);
							addressBarController.addContent(table, "Update Document");

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
						table.addMaterialEditText("Title", "").addMaterialEditText("Description", "").addActions(submit);

						Document document = new Document();
						document.resourceId = address;

						attachSubmitAction(submit, document, table);

						addressBarController.addContent(table, "Create Document");

					});

				}
			}
		});
	}

}
