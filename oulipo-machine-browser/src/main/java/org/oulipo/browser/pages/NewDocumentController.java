package org.oulipo.browser.pages;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.controls.OulipoTable;
import org.oulipo.browser.routers.ViewSourcePageRouter;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.resources.model.Document;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.scene.control.Button;
import retrofit2.Call;
import retrofit2.Response;

public class NewDocumentController extends BaseController {

	private void attachAction(Button button, Document document, OulipoTable table) {
		button.setOnAction(e -> {
			document.title = table.getValue("Title");
			document.description = table.getValue("Description");
			try {
				tumblerService.createOrUpdateDocument(document, new retrofit2.Callback<Document>() {

					@Override
					public void onFailure(Call<Document> arg0, Throwable arg1) {
						arg1.printStackTrace();
					}

					@Override
					public void onResponse(Call<Document> arg0, Response<Document> arg1) {
						address.setScheme("ted");
						Platform.runLater(() -> {
							try {
								addressBarController.show(address.toExternalForm());
							} catch (IOException e) {
								e.printStackTrace();
							}
						});

					}

				});
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void show(AddressBarController controller) throws MalformedTumblerException, IOException {
		super.show(controller);

		tumblerService.newDocument(address.toTumblerAuthority(), new retrofit2.Callback<Document>() {

			@Override
			public void onFailure(Call<Document> arg0, Throwable arg1) {
				arg1.printStackTrace();
			}

			@Override
			public void onResponse(Call<Document> arg0, Response<Document> response) {
				JFXButton submit = new JFXButton("Update");

				if (response.isSuccessful()) {
					final Document document = response.body();

					Platform.runLater(() -> {
						OulipoTable table = new OulipoTable(300, 350)
								.addEditText("Tumbler Address", document.resourceId.value, false)
								.addEditText("Title", document.title).addEditText("Description", document.description)
								.addActions(submit);
						attachAction(submit, document, table);

						ViewSourcePageRouter.showPageSource(ctx.getTabManager(), address, table);
						addressBarController.addContent(table, "New Document");

					});
				}
			}
		});
	}

}
