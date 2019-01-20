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
package org.oulipo.browser.framework;

import org.oulipo.browser.api.AddressBarController;
import javafx.scene.control.Button;
import org.controlsfx.control.textfield.CustomTextField;

/**
 * Controls forward and backward history views on an AddressBarController
 */
public class HistoryController {

	CustomTextField addressBox;

	private Button backBtn;

	private AddressBarController controller;

	private Button forwardBtn;

	private HistoryTraverser history = new HistoryTraverser();

	public HistoryController(Button forwardBtn, Button backBtn, CustomTextField addressBox,
			AddressBarController controller) {
		this.forwardBtn = forwardBtn;
		this.backBtn = backBtn;
		this.addressBox = addressBox;
		this.controller = controller;
	}

	public void back() {
		if (history.hasPrevious()) {
			addressBox.setText(history.previous());
			controller.refresh();
			enableButtons();
		}
	}

	private void enableButtons() {
		forwardBtn.setDisable(!history.hasNext());
		backBtn.setDisable(!history.hasPrevious());
	}

	public void forward() {
		if (history.hasNext()) {
			addressBox.setText(history.next());
			controller.refresh();
			enableButtons();
		}
	}

	public void visit(String address) {
		history.add(address);
		enableButtons();
	}

}
