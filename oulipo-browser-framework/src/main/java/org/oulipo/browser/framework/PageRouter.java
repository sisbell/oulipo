package org.oulipo.browser.framework;

import java.io.IOException;

import org.oulipo.browser.api.Page;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;

public interface PageRouter {

	Page getPage(String tumbler) throws IOException, MalformedSpanException;
}
