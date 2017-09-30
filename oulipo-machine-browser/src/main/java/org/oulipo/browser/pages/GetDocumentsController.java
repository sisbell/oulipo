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
import org.oulipo.browser.tables.DocumentTree;
import org.oulipo.browser.tables.HyperLinkTableCell;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.resources.model.Document;

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

public class GetDocumentsController extends BaseTableController<Document, DocumentTree> {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	@Override
	public void populateTable(BrowserContext ctx, List<Document> documents) {
		ObservableList<DocumentTree> usersObs = FXCollections.observableArrayList();
		for (Document document : documents) {
			usersObs.add(new DocumentTree(document));
		}

		final TreeItem<DocumentTree> root = new RecursiveTreeItem<>(usersObs, RecursiveTreeObject::getChildren);
		JFXTreeTableView<DocumentTree> treeView = new JFXTreeTableView<>(root);
		treeView.setShowRoot(false);
		treeView.setFocusModel(null);
		treeView.setSelectionModel(null);

		JFXTreeTableColumn<DocumentTree, String> addressColumn = new JFXTreeTableColumn<>("Address");
		addressColumn.setPrefWidth(350);
		addressColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<DocumentTree, String> param) -> {
			if (addressColumn.validateValue(param)) {
				return param.getValue().getValue().address;
			} else {
				return addressColumn.getComputedValue(param);
			}
		});
		addressColumn.setCellFactory(
				new Callback<TreeTableColumn<DocumentTree, String>, TreeTableCell<DocumentTree, String>>() {
					@Override
					public TreeTableCell<DocumentTree, String> call(TreeTableColumn<DocumentTree, String> param) {
						return new HyperLinkTableCell<DocumentTree>(ctx, false, addressBarController);
					}
				});

		JFXTreeTableColumn<DocumentTree, String> titleColumn = new JFXTreeTableColumn<>("Title");
		titleColumn.setPrefWidth(250);
		titleColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<DocumentTree, String> param) -> {
			if (addressColumn.validateValue(param)) {
				return param.getValue().getValue().title;
			} else {
				return addressColumn.getComputedValue(param);
			}
		});

		JFXTreeTableColumn<DocumentTree, String> descriptionColumn = new JFXTreeTableColumn<>("Description");
		descriptionColumn.setPrefWidth(150);
		descriptionColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<DocumentTree, String> param) -> {
			if (addressColumn.validateValue(param)) {
				return param.getValue().getValue().description;
			} else {
				return addressColumn.getComputedValue(param);
			}
		});

		treeView.getColumns().setAll(addressColumn, titleColumn, descriptionColumn);

		VBox.setVgrow(treeView, Priority.ALWAYS);
		String header = null;
		try {
			header = "View Documents for User " + address.toTumblerFields();
		} catch (MalformedTumblerException e) {
			e.printStackTrace();
		}
		addressBarController.addContent(treeView, header);

		ViewSourcePageRouter.showPageSource(ctx.getTabManager(), address, treeView);

	}

	@Override
	public void show(AddressBarController controller) throws MalformedTumblerException, IOException {
		super.show(controller);
		tumblerService.getDocuments(address.toTumblerAuthority(), address.getQueryParams(),
				new retrofit2.Callback<List<Document>>() {

					@Override
					public void onFailure(Call<List<Document>> arg0, Throwable arg1) {

					}

					@Override
					public void onResponse(Call<List<Document>> arg0, Response<List<Document>> response) {
						if (response.isSuccessful()) {
							List<Document> documents = response.body();
							Platform.runLater(() -> {
								populateTable(ctx, documents);
							});
						}
					}
				});
	}

}
