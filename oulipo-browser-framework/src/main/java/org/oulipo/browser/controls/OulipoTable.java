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
package org.oulipo.browser.controls;

import java.util.HashMap;

import com.google.common.base.Strings;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public final class OulipoTable extends GridPane {

	private HashMap<String, JFXTextField> map = new HashMap<>();

	private int row;

	public OulipoTable(int columnWidth, int column2Width) {
		getColumnConstraints().add(new ColumnConstraints(columnWidth));
		getColumnConstraints().add(new ColumnConstraints(column2Width));
	}

	public OulipoTable addActions(Button... button) {
		HBox box = new HBox();
		box.getChildren().addAll(button);
		add(box, 1, row++);
		return this;
	}

	public OulipoTable addCheckBox(String name, boolean value) {
		JFXCheckBox check = new JFXCheckBox();
		check.setSelected(value);
		check.setDisable(true);

		Label field = new Label(name);
		field.setId("oulipo-table-row");

		add(field, 0, row);
		add(check, 1, row++);
		return this;

	}

	public OulipoTable addEditText(String name, String value) {
		addEditText(name, value, true);
		return this;
	}

	public OulipoTable addEditText(String name, String value, boolean isEnabled) {
		Label left = new Label(name);
		left.setId("oulipo-table-row");

		add(left, 0, row);
		JFXTextField field = new JFXTextField(value);
		field.setId("oulipo-table-row");
		if (!isEnabled) {
			field.setDisable(true);
		}
		add(field, 1, row++);
		map.put(name, field);
		return this;
	}

	public OulipoTable addText(String name, String value) {
		if (!Strings.isNullOrEmpty(value)) {
			Label left = new Label(name);
			Label right = new Label(value);

			left.setId("oulipo-table-row");
			right.setId("oulipo-table-row");

			add(left, 0, row);
			add(right, 1, row++);
		}
		return this;
	}

	public String getValue(String key) {
		return map.get(key).getText();
	}

	public OulipoTable title(String title) {
		Label label = new Label(title);
		label.setId("table-title");
		add(label, 0, row++);
		return this;
	}
}
