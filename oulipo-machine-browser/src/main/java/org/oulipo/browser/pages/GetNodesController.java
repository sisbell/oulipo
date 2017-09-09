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

import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Page;
import org.oulipo.browser.framework.AddressController;
import org.oulipo.client.services.TumblerService;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Node;
import org.oulipo.storage.StorageException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class GetNodesController implements Page.Controller {

	private class ButtonsCell extends TreeTableCell<NodeTree, NodeTree> {

		final StackPane paddedButton = new StackPane();

		public ButtonsCell() {
		}

		@Override
		protected void updateItem(NodeTree item, boolean empty) {
			super.updateItem(item, empty);
			String publicKey = ctx.getAccountManager().getActiveAccount().publicKey;

			JFXButton usersBtn = new JFXButton();
			usersBtn.setGraphic(new ImageView(new Image("/images/ic_people_black_24dp_1x.png")));
			usersBtn.setButtonType(ButtonType.RAISED);
			// usersBtn.getStyleClass().add("button-raised");

			usersBtn.setOnAction(e -> {
				try {
					ctx.getTabManager().addTabWithAddressBar(item.address.getValue() + "/users",
							"Users - " + item.name.getValueSafe());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				// ViewerTab tab = new ViewerTab(ctx, item.name.getValue(),
				// item.address.getValue() +"/users");
			});
			paddedButton.getChildren().add(usersBtn);

			// if (item.publicKey.getValue().equals(publicKey)) {
			// paddedButton.getChildren().add(new Button("Edit"));
			// }
			this.setGraphic(paddedButton);
		}

	}

	private static final class NodeTree extends RecursiveTreeObject<NodeTree> {

		public final StringProperty address;

		public final StringProperty name;

		public final StringProperty publicKey;

		public NodeTree(Node node) {
			this.address = new SimpleStringProperty(node.resourceId.value);
			this.publicKey = new SimpleStringProperty(node.publicKey);
			this.name = new SimpleStringProperty(node.nodeName);
		}
	}

	private TumblerAddress address;
	private AddressController addressController;

	private BrowserContext ctx;

	private TumblerService tumblerService;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	private void populateTable(BrowserContext ctx, List<Node> nodes) {
		ObservableList<NodeTree> nodesObs = FXCollections.observableArrayList();
		for (Node node : nodes) {
			nodesObs.add(new NodeTree(node));
		}

		final TreeItem<NodeTree> root = new RecursiveTreeItem<>(nodesObs, RecursiveTreeObject::getChildren);
		JFXTreeTableView<NodeTree> treeView = new JFXTreeTableView<>(root);
		treeView.setShowRoot(false);

		JFXTreeTableColumn<NodeTree, String> addressColumn = new JFXTreeTableColumn<>("Address");
		addressColumn.setPrefWidth(150);
		addressColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<NodeTree, String> param) -> {
			if (addressColumn.validateValue(param)) {
				return param.getValue().getValue().address;
			} else {
				return addressColumn.getComputedValue(param);
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

						return new ButtonsCell();
					}

				});

		treeView.getColumns().setAll(addressColumn, pkColumn, nameColumn, actionColumn);

		VBox.setVgrow(treeView, Priority.ALWAYS);
		addressController.addContent(treeView);
	}

	@Override
	public void show(AddressController controller) throws MalformedTumblerException, IOException {
		this.ctx = controller.getContext();
		this.addressController = controller;
		this.address = addressController.getTumbler();
		try {
			this.tumblerService = new TumblerService(ctx.getDocuverseService());
		} catch (StorageException e) {
			e.printStackTrace();
		}

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
