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

import org.oulipo.net.MalformedTumblerException;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

public class Page {

	public static interface Controller extends Initializable {

		void show(AddressBarController addressBarController) throws MalformedTumblerException, IOException;

	}

	public static class View {

		private String location;

		public View(String location) {
			this.location = location;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

	}

	public Controller controller;

	public View view;

	public Page(Controller controller) {
		this(controller, null);
	}

	public Page(Controller controller, View view) {
		this.controller = controller;
		this.view = view;
	}

	public void present(AddressBarController addressBarController) throws MalformedTumblerException, IOException {
		if (view != null) {
			FXMLLoader loader = addressBarController.getContext().getLoader();
			loader.setController(controller);
			loader.setLocation(getClass().getResource(view.getLocation()));
			Node node = loader.load();
			addressBarController.addContent(node);
		}

		controller.show(addressBarController);

	}
}
