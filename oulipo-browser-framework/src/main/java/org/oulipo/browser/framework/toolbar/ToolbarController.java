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
package org.oulipo.browser.framework.toolbar;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.oulipo.browser.api.ApplicationContext;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.MenuContext;
import org.oulipo.browser.framework.ExtensionLoader;
import org.oulipo.browser.framework.StorageContext;
import org.oulipo.security.session.CodeGenerator;
import org.oulipo.storage.StorageException;

import com.google.inject.Inject;

import de.endrullis.draggabletabs.DraggableTabPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The toolbar that displays at the top of the browser. By default the toolbar
 * will attempt to display in the System menubar, otherwise it will display
 * directly within the application.
 */
public class ToolbarController implements Initializable {

	@Inject
	ApplicationContext applicationContext;

	@FXML
	public Menu bookmarkMenu;

	private BrowserContext context;

	@FXML
	public Menu fileMenu;

	@FXML
	public Menu historyMenu;

	private boolean isIcognitoMode;

	@Inject
	FXMLLoader loader;

	@FXML
	public Menu managerMenu;

	@FXML
	MenuBar menuBar;

	@FXML
	public Label name;

	/**
	 * The tab pane that contains the browser tabs
	 */
	@FXML
	public DraggableTabPane navigationTabs;

	@FXML
	Menu peopleMenu;

	@FXML
	public StackPane stack;

	@Inject
	StorageContext storageContext;

	@FXML
	public Menu toolsMenu;

	@FXML
	public Menu windowMenu;

	@FXML
	public void closeTab() {
		context.getTabManager().removeSelectedTab();
	}

	/**
	 * Gets the <code>BrowserContext</code> for this instance of the toolbar
	 * 
	 * @return
	 */
	public BrowserContext getContext() {
		return context;
	}

	/**
	 * Launches an incognito version of this toolbar. The user history will not be
	 * maintained. Boomkarks by the user, however, will be persisted.
	 */
	@FXML
	public void incognito() {
		try {
			context.launchNewToolbar(true, "Incognito-" + CodeGenerator.generateCode(4));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the BrowserContext and loads all extensions
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MenuContext menuContext = new MenuContext(windowMenu, peopleMenu, managerMenu, toolsMenu, fileMenu,
				bookmarkMenu, historyMenu, navigationTabs);
		try {
			context = new BrowserContext(applicationContext, loader, stack, storageContext, menuContext, name);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (StorageException e1) {
			e1.printStackTrace();
		}

		menuBar.useSystemMenuBarProperty().set(true);
		try {
			ExtensionLoader.loadExtensions(context);
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			e.printStackTrace();
		}

		ToggleGroup group = new ToggleGroup();
		for (Map.Entry<String, Stage> entry : applicationContext.getStages().entrySet()) {
			RadioMenuItem item = new RadioMenuItem();
			item.setText(entry.getKey());
			item.setUserData(entry.getValue());
			item.setToggleGroup(group);
			item.setOnAction(e -> {
				Stage s = (Stage) item.getUserData();
				s.show();

			});
			menuContext.getWindowMenu().getItems().add(item);
		}

	}

	public void setIncognitoMode() {
		this.isIcognitoMode = true;
	}
}
