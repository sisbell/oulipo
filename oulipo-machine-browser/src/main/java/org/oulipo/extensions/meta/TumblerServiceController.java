package org.oulipo.extensions.meta;

import org.oulipo.client.services.DocuverseService;
import org.oulipo.client.services.ServiceBuilder;
import org.oulipo.client.services.TedRouter;

public class TumblerServiceController {

	private TedRouter tedRouter;

	public TumblerServiceController() {
		DocuverseService service = new ServiceBuilder("http://localhost:4567/docuverse/")
				.publicKey("1GNHSBPgd7x4AosHDys7x2tbFinLz4Qq5Z").sessionToken("i9Kpn6mvkImat7Jm7T4HL7OUjlgO0lr7")
				.build(DocuverseService.class);
		tedRouter = new TedRouter(service);
	}
}
