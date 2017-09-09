package org.oulipo.browser.api;

import java.io.IOException;

import org.oulipo.browser.framework.AddressController;
import org.oulipo.net.MalformedTumblerException;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

public class Page {

	public static class View {

		private String location;
		
		public View(String location) {
			this.location = location;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}
		
	}

	public static interface Controller extends Initializable {

		void show(AddressController addressController) throws MalformedTumblerException, IOException;

	}

	public Controller controller;

	public View view;


	public Page(Controller controller) {
		this(controller, null);
	}
	
	public Page(Controller controller, View view) {
		this.controller = controller;
		this.view = view;
	}

	public void present(AddressController addressController) throws MalformedTumblerException, IOException {
		if (view != null) {	
			FXMLLoader loader = addressController.getContext().getLoader();
			loader.setController(controller);
			loader.setLocation(getClass().getResource(view.getLocation()));
			Node node = loader.load();
			addressController.addContent(node);
		}

		controller.show(addressController);

	}
}
