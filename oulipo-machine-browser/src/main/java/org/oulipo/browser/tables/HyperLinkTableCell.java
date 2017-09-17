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
package org.oulipo.browser.tables;

import java.io.IOException;

import org.controlsfx.control.HyperlinkLabel;
import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.net.TumblerAddress;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.TreeTableCell;

public class HyperLinkTableCell<U> extends TreeTableCell<U, String> {

	private AddressBarController addressBarController;
	private BrowserContext ctx;
	private boolean openTab;

	public HyperLinkTableCell(BrowserContext ctx, boolean openTab, AddressBarController addressBarController) {
		this.ctx = ctx;
		this.openTab = openTab;
		this.addressBarController = addressBarController;
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		HyperlinkLabel label = new HyperlinkLabel("[" + item + "]");
		label.setOnAction(event -> {
			Hyperlink link = (Hyperlink) event.getSource();
			String address = link == null ? "" : link.getText();
			try {
				TumblerAddress tumbler = TumblerAddress.create(address);
				// ctx.getTabManager().
				if (openTab) {
					ctx.getTabManager().addTabWithAddressBar(address, tumbler.toTumblerFields());
				} else {
					addressBarController.show(address);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		});
		this.setGraphic(label);
	}

}
