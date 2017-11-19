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

import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.streams.IRI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;

import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public final class ButtonsCreator {

	public static Button addUser(BrowserContext ctx, String address, String tabTitle) {
		JFXButton addUserBtn = new JFXButton();
		addUserBtn.setGraphic(new ImageView(new Image("/images/ic_person_add_black_24dp_1x.png")));
		addUserBtn.setTooltip(new Tooltip("Add New User To This Node"));

		addUserBtn.setOnAction(e -> {
			try {
				ctx.getTabManager().addTabWithAddressBar("edit://" + address + "/users", "Create User - " + tabTitle);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		return addUserBtn;
	}

	public static Button editNode(AddressBarController addressBarController, String address) {
		JFXButton btn = new JFXButton();
		btn.setGraphic(new ImageView(new Image("/images/ic_mode_edit_black_24dp_1x.png")));
		btn.setTooltip(new Tooltip("Edit This Node"));
		btn.setOnAction(e -> {
			try {
				addressBarController.show(address);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		return btn;
	}

	public static Button search(BrowserContext ctx, TextField textField) {
		final Glow glow = new Glow();
		glow.setLevel(0.0);

		JFXButton btn = new JFXButton();
		btn.setGraphic(new ImageView(new Image("/images/ic_mic_black_24dp_1x.png")));
		btn.setTooltip(new Tooltip("Search by voice"));
		btn.setOnAction(e -> {
			btn.setEffect(glow);

			final Timeline timeline = new Timeline();
			timeline.setCycleCount(Animation.INDEFINITE);
			timeline.setAutoReverse(true);
			final KeyValue kv = new KeyValue(glow.levelProperty(), 1.0);
			final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
			timeline.getKeyFrames().add(kf);
			timeline.play();

			LiveSpeechRecognizer recognizer = ctx.getApplicationContext().getRecognizer();

			recognizer.startRecognition(true);
			SpeechResult result = recognizer.getResult();
			textField.setText(result.getHypothesis());
			recognizer.stopRecognition();
			// timeline.stop();

		});
		return btn;
	}

	public static Button showUsers(BrowserContext ctx, String address, String tabTitle, boolean openTab,
			AddressBarController controller) {
		JFXButton usersBtn = new JFXButton();
		usersBtn.setGraphic(new ImageView(new Image("/images/ic_people_black_24dp_1x.png")));
		usersBtn.setButtonType(ButtonType.RAISED);
		usersBtn.setTooltip(new Tooltip("Show Users Of This Node"));

		usersBtn.setOnAction(e -> {
			try {
				if (openTab) {
					ctx.getTabManager().addTabWithAddressBar(address + "/users", "Users - " + tabTitle);
				} else {
					controller.show(address + "/users");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		return usersBtn;
	}

	public static Button signin(AddressBarController addressBarController, BrowserContext ctx, String address,
			String xandle) {
		JFXButton btn = new JFXButton();
		btn.setGraphic(new ImageView(new Image("/images/sign_in.png")));
		btn.setTooltip(new Tooltip("Sign In"));
		btn.setOnAction(e -> {
			ctx.setUserName(address, xandle);
		});
		return btn;
	}

	public static Button writeDocument(AddressBarController addressBarController, IRI address) {
		JFXButton btn = new JFXButton();
		btn.setGraphic(new ImageView(new Image("/images/fa_file_text.png")));
		btn.setTooltip(new Tooltip("Write this document"));
		btn.setOnAction(e -> {
			try {
				// address.setScheme("write");
				addressBarController.show(address.value);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		return btn;
	}
}
