package org.oulipo.browser.routers;

import java.io.IOException;

import org.oulipo.browser.api.Page;
import org.oulipo.browser.api.PageRouter;
import org.oulipo.browser.api.Scheme;
import org.oulipo.browser.pages.write.PublisherController;
import org.oulipo.net.MalformedSpanException;

@Scheme("write")
public class WritePageRouter implements PageRouter {

	@Override
	public Page getPage(String tumbler, String body) throws IOException, MalformedSpanException {
		// return new Page(new WriteDocumentController());
		return new Page(new PublisherController());

	}

}
