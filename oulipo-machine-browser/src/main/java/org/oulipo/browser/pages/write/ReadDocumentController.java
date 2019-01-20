package org.oulipo.browser.pages.write;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.pages.BaseController;

import javafx.scene.control.Label;

public class ReadDocumentController extends BaseController {

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void show(AddressBarController controller) throws IOException {
		super.show(controller);

		addressBarController.addContent(new Label(), "Read Document");

		// TODO: pull from document file
		/*
		 * tumblerService.getVirtual(address.value, null, new
		 * retrofit2.Callback<Virtual>() {
		 *
		 * @Override public void onFailure(Call<Virtual> arg0, Throwable arg1) {
		 * arg1.printStackTrace(); }
		 *
		 * @Override public void onResponse(Call<Virtual> arg0, Response<Virtual>
		 * response) { if (response.isSuccessful()) { final Virtual virtual =
		 * response.body();
		 *
		 * Platform.runLater(() -> { Label label = new Label(); StringBuilder sb = new
		 * StringBuilder(); for (VirtualContent vc : virtual.content) {
		 * sb.append(vc.content); } label.setText(sb.toString());
		 * ViewSourcePageRouter.showPageSource(ctx.getTabManager(), address, label);
		 * addressBarController.addContent(label, "View Document");
		 *
		 * }); } else { //address.setScheme("edit"); Platform.runLater(() -> { try {
		 * addressBarController.show(address.value); } catch (IOException e) {
		 * e.printStackTrace(); } }); } } });
		 */

	}

}
