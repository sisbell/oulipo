package org.oulipo.browser.pages.write;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.pages.BaseController;
import org.oulipo.browser.routers.ViewSourcePageRouter;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.resources.model.Virtual;
import org.oulipo.streams.VirtualContent;

import javafx.application.Platform;
import javafx.scene.control.Label;
import retrofit2.Call;
import retrofit2.Response;

public class ReadDocumentController extends BaseController {

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@Override
	public void show(AddressBarController controller) throws MalformedTumblerException, IOException {
		super.show(controller);

		addressBarController.addContent(new Label(), "Read Document");

		tumblerService.getVirtual(address.toTumblerAuthority(), null, new retrofit2.Callback<Virtual>() {

			@Override
			public void onFailure(Call<Virtual> arg0, Throwable arg1) {
				arg1.printStackTrace();
			}

			@Override
			public void onResponse(Call<Virtual> arg0, Response<Virtual> response) {
				if (response.isSuccessful()) {
					final Virtual virtual = response.body();

					Platform.runLater(() -> {
						Label label = new Label();
						StringBuilder sb = new StringBuilder();
						for (VirtualContent vc : virtual.content) {
							sb.append(vc.content);
						}
						label.setText(sb.toString());
						ViewSourcePageRouter.showPageSource(ctx.getTabManager(), address, label);
						addressBarController.addContent(label, "View Document");

					});
				} else {
					address.setScheme("edit");
					Platform.runLater(() -> {
						try {
							addressBarController.show(address.toExternalForm());
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				}
			}
		});

	}

}
