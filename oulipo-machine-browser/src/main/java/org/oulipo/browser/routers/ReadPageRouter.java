package org.oulipo.browser.routers;

import java.io.IOException;

import org.oulipo.browser.api.Page;
import org.oulipo.browser.api.PageRouter;
import org.oulipo.browser.api.Scheme;
import org.oulipo.browser.pages.write.ReadDocumentController;
import org.oulipo.streams.MalformedSpanException;

@Scheme("read")
public class ReadPageRouter implements PageRouter {

	@Override
	public Page getPage(String tumbler, String body) throws IOException, MalformedSpanException {
		return new Page(new ReadDocumentController());
	}
}
