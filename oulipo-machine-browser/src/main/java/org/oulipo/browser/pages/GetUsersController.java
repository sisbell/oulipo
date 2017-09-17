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
package org.oulipo.browser.pages;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.routers.ViewSourcePageRouter;
import org.oulipo.browser.tables.HyperLinkTableCell;
import org.oulipo.browser.tables.UserTree;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.resources.model.User;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class GetUsersController extends BaseTableController<User, UserTree> {

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void populateTable(BrowserContext ctx, List<User> users) {
		ObservableList<UserTree> usersObs = FXCollections.observableArrayList();
		for (User node : users) {
			usersObs.add(new UserTree(node));
		}

		final TreeItem<UserTree> root = new RecursiveTreeItem<>(usersObs, RecursiveTreeObject::getChildren);
		JFXTreeTableView<UserTree> treeView = new JFXTreeTableView<>(root);
		treeView.setShowRoot(false);
		treeView.setFocusModel(null);
		treeView.setSelectionModel(null);

		JFXTreeTableColumn<UserTree, String> addressColumn = new JFXTreeTableColumn<>("Address");
		addressColumn.setPrefWidth(150);
		addressColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<UserTree, String> param) -> {
			if (addressColumn.validateValue(param)) {
				return param.getValue().getValue().address;
			} else {
				return addressColumn.getComputedValue(param);
			}
		});
		addressColumn
				.setCellFactory(new Callback<TreeTableColumn<UserTree, String>, TreeTableCell<UserTree, String>>() {
					@Override
					public TreeTableCell<UserTree, String> call(TreeTableColumn<UserTree, String> param) {
						return new HyperLinkTableCell<UserTree>(ctx, false, addressBarController);
					}
				});

		JFXTreeTableColumn<UserTree, String> pkColumn = new JFXTreeTableColumn<>("Public Key");
		pkColumn.setPrefWidth(250);
		pkColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<UserTree, String> param) -> {
			if (addressColumn.validateValue(param)) {
				return param.getValue().getValue().publicKey;
			} else {
				return addressColumn.getComputedValue(param);
			}
		});

		JFXTreeTableColumn<UserTree, String> nameColumn = new JFXTreeTableColumn<>("Name");
		nameColumn.setPrefWidth(150);
		nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<UserTree, String> param) -> {
			if (addressColumn.validateValue(param)) {
				return param.getValue().getValue().name;
			} else {
				return addressColumn.getComputedValue(param);
			}
		});

		treeView.getColumns().setAll(addressColumn, pkColumn, nameColumn);

		VBox.setVgrow(treeView, Priority.ALWAYS);
		String header = null;
		try {
			header = "View Users for Node " + address.toTumblerFields();
		} catch (MalformedTumblerException e) {
			e.printStackTrace();
		}
		addressBarController.addContent(treeView, header);

		ViewSourcePageRouter.showPageSource(ctx.getTabManager(), address, treeView);

	}

	@Override
	public void show(AddressBarController controller) throws MalformedTumblerException, IOException {
		super.show(controller);
		tumblerService.getUsers(address.toTumblerAuthority(), address.getQueryParams(),
				new retrofit2.Callback<List<User>>() {

					@Override
					public void onFailure(Call<List<User>> arg0, Throwable arg1) {

					}

					@Override
					public void onResponse(Call<List<User>> arg0, Response<List<User>> response) {
						if (response.isSuccessful()) {
							List<User> users = response.body();
							Platform.runLater(() -> {
								populateTable(ctx, users);
							});
						}
					}
				});
	}

}
