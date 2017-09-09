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

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TntApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// TED Network tool
		Parent tnt = FXMLLoader
				.load(getClass().getResource("/org/oulipo/browser/extensions/tnt/TumblerServiceView.fxml"));

		Parent dv = FXMLLoader
				.load(getClass().getResource("/org/oulipo/browser/extensions/tnt/drawer/DrawerView.fxml"));

		JFXHamburger h1 = new JFXHamburger();
		HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(h1);
		burgerTask.setRate(-1);

		FlowPane main = new FlowPane();
		main.setVgap(20);
		main.setHgap(20);
		main.getChildren().add(h1);
		main.getChildren().add(tnt);

		JFXDrawersStack drawersStack = new JFXDrawersStack();
		drawersStack.setContent(main);

		JFXDrawer leftDrawer = new JFXDrawer();
		StackPane leftDrawerPane = new StackPane();
		leftDrawerPane.getStyleClass().add("red-400");
		leftDrawerPane.getChildren().add(dv);
		leftDrawer.setSidePane(leftDrawerPane);
		leftDrawer.setDefaultDrawerSize(150);
		leftDrawer.setOverLayVisible(false);
		leftDrawer.setResizableOnDrag(true);

		h1.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
			drawersStack.toggle(leftDrawer);
			burgerTask.setRate(burgerTask.getRate() * -1);
			burgerTask.play();

		});

		Scene scene = new Scene(drawersStack);

		stage.setScene(scene);
		stage.show();

	}
}
