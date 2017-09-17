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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.api.Page;
import org.oulipo.client.services.TedRouter;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.services.responses.ErrorResponse;
import org.oulipo.storage.StorageException;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import retrofit2.Response;

public class ViewSourceController implements Page.Controller {

	private final String address;

	private AddressBarController addressBarController;

	private ObjectMapper mapper = new ObjectMapper();

	public ViewSourceController(String address) {
		this.address = address;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void show(AddressBarController addressBarController) throws MalformedTumblerException, IOException {
		this.addressBarController = addressBarController;

		String iri = address.substring(address.indexOf(":") + 1, address.length());

		try {
			TedRouter router = new TedRouter(addressBarController.getContext().getDocuverseService());
			router.routeGetRequest(TumblerAddress.create(iri), this::showResponses);
		} catch (StorageException | MalformedSpanException | URISyntaxException e) {
			e.printStackTrace();
		}

	}

	private void showResponses(Response<?> response) {
		try {
			if (response.isSuccessful()) {
				String text = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.body());
				// TODO: regex all IRIs and place in brackets to make linkable
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							ScrollPane scroll = new ScrollPane();

							Label label = new Label(text);
							label.setStyle("-fx-background-color: white");
							scroll.setContent(label);
							addressBarController.addContent(scroll, "View Source");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} else {
				ErrorResponse error = mapper.readValue(response.errorBody().bytes(), ErrorResponse.class);
				addressBarController.addContent(
						new Label(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(error)), "View Source");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
