/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.  
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
package org.oulipo.machine.browser;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.oulipo.client.services.DocuverseService;
import org.oulipo.client.services.ServiceBuilder;
import org.oulipo.client.services.TedRouter;
import org.oulipo.client.services.TumblerService;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Thing;
import org.oulipo.resources.responses.ErrorResponseDto;
import org.oulipo.resources.transforms.ThingToString;

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
	private JFXRadioButton getOp;

	@FXML
	private JFXRadioButton putOp;

	@FXML
	private JFXRadioButton postOp;

	@FXML
	private JFXRadioButton deleteOp;

	@FXML
	private JFXTextField sessionToken;

	@FXML
	private JFXTextField publicKey;

	@FXML
	private JFXTextArea jsonOutput;

	@FXML
	private JFXTextArea tuplesOutput;

	@FXML
	private JFXTextArea jsonPayload;

	@FXML
	private JFXTextArea tuplesPayload;

	@FXML
	private JFXTextField tedAddress;

	@FXML
	private JFXTabPane messageBodyTabs;

	@FXML
	private JFXButton tumbleButton;

	private TumblerService tumblerService;

	private TedRouter tedRouter;

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
	public void tumble() {
		try {
			TumblerAddress address = TumblerAddress.create(tedAddress.getText());
			if (getOp.isSelected()) {
				tedRouter.routeGetRequest(address, this::showResponses);
			} else if (putOp.isSelected()) {
				tedRouter.routePutRequest(address, jsonPayload.getText(), this::showResponses);
			} else if (postOp.isSelected()) {
				tedRouter.postRequest(tedAddress.getText(), jsonPayload.getText(), this::showResponses);
			} else if (deleteOp.isSelected()) {
				tedRouter.deleteRequest(tedAddress.getText(), this::showResponses);
			}
		} catch (Exception e) {
			writeErrorMessage(e.getMessage());
			e.printStackTrace();
		}
	}

	private Pattern tedPattern = Pattern.compile("\"ted://.*?\"");

	private ObjectMapper mapper = new ObjectMapper();

	public void showResponses(Response<?> response) {
		try {
			if (response.isSuccessful()) {
				String text = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.body());

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							jsonOutput.setText(text);
							if (response.body() instanceof List) {
								tuplesOutput.setText(ThingToString.asStrings((List<Thing>) response.body()));
							} else {
								tuplesOutput.setText(ThingToString.asString((Thing) response.body()));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

			} else {
				ErrorResponseDto error = mapper.readValue(response.errorBody().bytes(), ErrorResponseDto.class);
				writeErrorMessage(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(error));
			}
		} catch (Exception e) {
			writeErrorMessage(e.getMessage());
			e.printStackTrace();
		}
	}


	private void writeErrorMessage(String message) {
		jsonOutput.setText(message);
		tuplesOutput.setText(message);
	}

	@FXML
	private void post(ActionEvent event) {
		messageBodyTabs.setVisible(true);
	}

	@FXML
	private void delete(ActionEvent event) {
		messageBodyTabs.setVisible(false);
	}

	@FXML
	private void put(ActionEvent event) {
		messageBodyTabs.setVisible(true);
	}

	@FXML
	private void get(ActionEvent event) {
		messageBodyTabs.setVisible(false);
	}

}
