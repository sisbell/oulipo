package org.oulipo.extensions.experimental;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.oulipo.client.services.DocuverseService;
import org.oulipo.client.services.ServiceBuilder;
import org.oulipo.client.services.TedRouter;
import org.oulipo.client.services.TumblerService;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Node;

import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNodeController implements Initializable {

	@FXML
	public JFXTextField nodeField;
	
	@FXML
	public JFXTextField nodeName;

	
	@FXML
	public void submit() {
		DocuverseService service = new ServiceBuilder("http://localhost:4567/docuverse/")
				.publicKey("1GNHSBPgd7x4AosHDys7x2tbFinLz4Qq5Z").sessionToken("i9Kpn6mvkImat7Jm7T4HL7OUjlgO0lr7")
				.build(DocuverseService.class);
		Node node = new Node();
		node.nodeName = nodeName.getText();
		try {
			node.resourceId = TumblerAddress.create("1." + nodeField.getText());
		} catch (MalformedTumblerException e1) {
			e1.printStackTrace();
		}
		
		TumblerService tumblerService = new TumblerService(service);
		try {
			tumblerService.createOrUpdateNode(node, new Callback<Node>() {

				@Override
				public void onFailure(Call<Node> arg0, Throwable arg1) {
					
				}

				@Override
				public void onResponse(Call<Node> node, Response<Node> arg1) {
					System.out.println(arg1.body());
				}	
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

}
