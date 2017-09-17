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
import org.oulipo.browser.tables.ButtonsCreator;
import org.oulipo.browser.tables.HyperLinkTableCell;
import org.oulipo.browser.tables.NodeTree;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.resources.model.Node;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class GetNodesController extends BaseTableController<Node, NodeTree> {

	private class ButtonsTableCell extends TreeTableCell<NodeTree, NodeTree> {

		public ButtonsTableCell() {
		}

		@Override
		protected void updateItem(NodeTree item, boolean empty) {
			super.updateItem(item, empty);

			if (item == null) {
				return;
			}

			HBox buttonBox = new HBox();
			Button usersBtn = ButtonsCreator.showUsers(ctx, item.address.getValue(), item.name.getValueSafe(), false,
					addressBarController);
			buttonBox.getChildren().add(usersBtn);

			if (item != null && item.canCreateUsers.get()) {
				Button addUserBtn = ButtonsCreator.addUser(ctx, item.address.getValue(), item.name.getValueSafe());
				buttonBox.getChildren().add(addUserBtn);
			}
			setGraphic(buttonBox);

			String publicKey = ctx.getAccountManager().getActiveAccount().publicKey;
			// if (item.publicKey.getValue().equals(publicKey)) {
			// paddedButton.getChildren().add(new Button("Edit"));
			// }
		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void populateTable(BrowserContext ctx, List<Node> nodes) {
		ObservableList<NodeTree> nodesObs = FXCollections.observableArrayList();
		for (Node node : nodes) {
			nodesObs.add(new NodeTree(node));
		}

		final TreeItem<NodeTree> root = new RecursiveTreeItem<>(nodesObs, RecursiveTreeObject::getChildren);
		JFXTreeTableView<NodeTree> treeView = new JFXTreeTableView<>(root);
		treeView.setShowRoot(false);
		treeView.setFocusModel(null);
		treeView.setSelectionModel(null);

		JFXTreeTableColumn<NodeTree, String> addressColumn = new JFXTreeTableColumn<>("Address");
		addressColumn.setPrefWidth(150);
		addressColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<NodeTree, String> param) -> {
			if (addressColumn.validateValue(param)) {
				return param.getValue().getValue().address;
			} else {
				return addressColumn.getComputedValue(param);
			}
		});
		addressColumn
				.setCellFactory(new Callback<TreeTableColumn<NodeTree, String>, TreeTableCell<NodeTree, String>>() {
					@Override
					public TreeTableCell<NodeTree, String> call(TreeTableColumn<NodeTree, String> param) {
						return new HyperLinkTableCell<NodeTree>(ctx, false, addressBarController);
					}
				});

		JFXTreeTableColumn<NodeTree, String> pkColumn = new JFXTreeTableColumn<>("Public Key");
		pkColumn.setPrefWidth(250);
		pkColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<NodeTree, String> param) -> {
			if (addressColumn.validateValue(param)) {
				return param.getValue().getValue().publicKey;
			} else {
				return addressColumn.getComputedValue(param);
			}
		});

		JFXTreeTableColumn<NodeTree, String> nameColumn = new JFXTreeTableColumn<>("Name");
		nameColumn.setPrefWidth(150);
		nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<NodeTree, String> param) -> {
			if (addressColumn.validateValue(param)) {
				return param.getValue().getValue().name;
			} else {
				return addressColumn.getComputedValue(param);
			}
		});

		JFXTreeTableColumn<NodeTree, NodeTree> actionColumn = new JFXTreeTableColumn<>("");
		actionColumn.setPrefWidth(150);
		actionColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<NodeTree, NodeTree> param) -> {
			return new SimpleObjectProperty<NodeTree>(param.getValue().getValue());
		});

		actionColumn
				.setCellFactory(new Callback<TreeTableColumn<NodeTree, NodeTree>, TreeTableCell<NodeTree, NodeTree>>() {
					@Override
					public TreeTableCell<NodeTree, NodeTree> call(TreeTableColumn<NodeTree, NodeTree> param) {
						return new ButtonsTableCell();
					}

				});

		treeView.getColumns().setAll(addressColumn, pkColumn, nameColumn, actionColumn);

		VBox.setVgrow(treeView, Priority.ALWAYS);
		String header = address.isMainNet() ? "MainNet Nodes" : "TestNet Nodes";
		addressBarController.addContent(treeView, header);

		ViewSourcePageRouter.showPageSource(ctx.getTabManager(), address, treeView);

	}

	@Override
	public void show(AddressBarController controller) throws MalformedTumblerException, IOException {
		super.show(controller);
		tumblerService.getNodes(address.toTumblerAuthority(), address.getQueryParams(),
				new retrofit2.Callback<List<Node>>() {

					@Override
					public void onFailure(Call<List<Node>> arg0, Throwable arg1) {

					}

					@Override
					public void onResponse(Call<List<Node>> arg0, Response<List<Node>> response) {
						if (response.isSuccessful()) {
							List<Node> nodes = response.body();
							Platform.runLater(() -> {
								populateTable(ctx, nodes);
							});
						}
					}
				});
	}

}
