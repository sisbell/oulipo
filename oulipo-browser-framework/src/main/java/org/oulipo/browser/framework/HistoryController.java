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

import java.util.Iterator;
import java.util.LinkedList;

import org.controlsfx.control.textfield.CustomTextField;
import org.oulipo.browser.api.AddressController;

import javafx.scene.control.Button;

/**
 * Controls forward and backward history views on an AddressController
 */
public class HistoryController {

	private static class HistoryList implements Iterable<String> {

		private class Node {

			String item;

			Node next;

			Node previous;
		}

		private Node current;

		private Node first;

		private Node last;

		private int N;

		public void add(String item) {
			Node tmp = new Node();
			tmp.item = item;

			if (last != null) {
				last.next = tmp;
			}
			tmp.previous = last;

			last = tmp;
			N++;
		}

		public boolean isEmpty() {
			return N == 0;
		}

		@Override
		public Iterator<String> iterator() {
			return null;
		}

		public int size() {
			return N;
		}

	}

	CustomTextField addressBox;

	private Button backBtn;

	private AddressController controller;

	private Button forwardBtn;

	private LinkedList<String> history = new LinkedList<>();

	public HistoryController(Button forwardBtn, Button backBtn, CustomTextField addressBox,
			AddressController controller) {
		this.forwardBtn = forwardBtn;
		this.backBtn = backBtn;
		forwardBtn.setDisable(true);
		backBtn.setDisable(true);
		this.addressBox = addressBox;
		this.controller = controller;
	}

	public void back() {
		if (!history.isEmpty()) {
			history.removeLast();
			if (!history.isEmpty()) {
				addressBox.setText(history.getLast());
				controller.refresh();
			}
		}
	}

	public void forward() {

	}

	public void visit(String address) {
		history.add(address);
	}

}
