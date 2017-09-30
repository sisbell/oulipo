package org.oulipo.browser.pages.write;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.controls.OulipoTable;
import org.oulipo.browser.pages.BaseController;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.resources.model.Document;
import org.oulipo.streams.VirtualContent;

import com.jfoenix.controls.JFXButton;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteDocumentController extends BaseController {

	private void attachAction(Button button, Document document, OulipoTable table) {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void show(AddressBarController controller) throws MalformedTumblerException, IOException {
		super.show(controller);

		Document doc = new Document();
		doc.resourceId = address.getDocumentAddress();
		TextArea textBox = new TextArea();

		JFXButton submit = new JFXButton("Submit");
		submit.setOnAction(e -> {
			try {
				tumblerService.insert(doc, 1L, textBox.getText(), new Callback<VirtualContent>() {

					@Override
					public void onFailure(Call<VirtualContent> arg0, Throwable arg1) {
						arg1.printStackTrace();

					}

					@Override
					public void onResponse(Call<VirtualContent> arg0, Response<VirtualContent> arg1) {
						System.out.println("Call: " + arg1.body().content);
						// TODO Auto-generated method stub

					}

				});
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		VBox vbox = new VBox();
		vbox.getChildren().addAll(textBox, submit);
		addressBarController.addContent(vbox, "Write Document");

	}

}
