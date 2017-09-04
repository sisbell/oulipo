package org.oulipo.extensions.experimental;

import java.io.IOException;

import org.oulipo.browser.api.BaseExtension;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.framework.MenuContext.Type;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class ManagerExtension extends BaseExtension {

	@Override
	public void init(BrowserContext ctx) {
		addMenuItem(ctx, "Register Node", Type.MANAGER, e -> {
			FXMLLoader loader = ctx.getLoader();
			loader.setLocation(getClass().getResource("/org/oulipo/extensions/experimental/AddNodeView.fxml"));

			JFXDialogLayout layout = new JFXDialogLayout();
			try {
				layout.setBody((Node) loader.load());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			JFXDialog dialog = new JFXDialog(ctx.getContentArea(), layout, DialogTransition.CENTER);
			dialog.show();
		});
	}

}
