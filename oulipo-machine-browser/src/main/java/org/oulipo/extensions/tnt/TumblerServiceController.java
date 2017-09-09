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
package org.oulipo.extensions.tnt;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.oulipo.client.services.DocuverseService;
import org.oulipo.client.services.ServiceBuilder;
import org.oulipo.client.services.TedRouter;
import org.oulipo.client.services.TumblerService;
import org.oulipo.net.TumblerAddress;
import org.oulipo.services.responses.ErrorResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import retrofit2.Response;

public class TumblerServiceController implements Initializable {

	@FXML
	private JFXTextArea bodyResponse;

	@FXML
	private JFXRadioButton deleteOp;

	@FXML
	private JFXRadioButton getOp;

	private ObjectMapper mapper = new ObjectMapper();

	@FXML
	private JFXTabPane messageBodyTabs;

	@FXML
	private JFXRadioButton postOp;

	@FXML
	private JFXTextField publicKey;

	@FXML
	private JFXRadioButton putOp;

	@FXML
	private JFXTextArea rawPayloadData;

	@FXML
	private JFXTextField sessionToken;

	@FXML
	private JFXTextField tedAddress;

	private Pattern tedPattern = Pattern.compile("\"ted://.*?\"");

	private TedRouter tedRouter;

	@FXML
	private JFXButton tumbleButton;

	private TumblerService tumblerService;

	@FXML
	private void delete(ActionEvent event) {
		messageBodyTabs.setVisible(false);
	}

	@FXML
	private void get(ActionEvent event) {
		messageBodyTabs.setVisible(false);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		messageBodyTabs.managedProperty().bind(messageBodyTabs.visibleProperty());
		DocuverseService service = new ServiceBuilder("http://localhost:4567/docuverse/")
				.publicKey("1GNHSBPgd7x4AosHDys7x2tbFinLz4Qq5Z").sessionToken("i9Kpn6mvkImat7Jm7T4HL7OUjlgO0lr7")
				.build(DocuverseService.class);
		tumblerService = new TumblerService(service);
		tedRouter = new TedRouter(tumblerService);

		getOp.setSelected(true);
		messageBodyTabs.setVisible(false);
	}

	@FXML
	public void makeRequest() {
		// getResource();
	}

	@FXML
	private void post(ActionEvent event) {
		messageBodyTabs.setVisible(true);
	}

	@FXML
	private void put(ActionEvent event) {
		messageBodyTabs.setVisible(true);
	}

	public void showResponses(Response<?> response) {
		try {
			if (response.isSuccessful()) {
				String text = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.body());

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							bodyResponse.setText(text);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

			} else {
				ErrorResponse error = mapper.readValue(response.errorBody().bytes(), ErrorResponse.class);
				writeErrorMessage(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(error));
			}
		} catch (Exception e) {
			writeErrorMessage(e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	public void tumble() {
		try {
			TumblerAddress address = TumblerAddress.create(tedAddress.getText());
			if (getOp.isSelected()) {
				tedRouter.routeGetRequest(address, this::showResponses);
			} else if (putOp.isSelected()) {
				tedRouter.routePutRequest(address, rawPayloadData.getText(), this::showResponses);
			} else if (postOp.isSelected()) {
				tedRouter.postRequest(tedAddress.getText(), rawPayloadData.getText(), this::showResponses);
			} else if (deleteOp.isSelected()) {
				tedRouter.deleteRequest(tedAddress.getText(), this::showResponses);
			}
		} catch (Exception e) {
			writeErrorMessage(e.getMessage());
			e.printStackTrace();
		}
	}

	private void writeErrorMessage(String message) {
		bodyResponse.setText(message);
	}

}
