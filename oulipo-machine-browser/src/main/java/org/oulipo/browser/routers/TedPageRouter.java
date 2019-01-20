/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *******************************************************************************/
package org.oulipo.browser.routers;

import java.io.IOException;

import org.oulipo.browser.api.Page;
import org.oulipo.browser.api.PageRouter;
import org.oulipo.browser.api.Scheme;
import org.oulipo.streams.MalformedSpanException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Routes a tumbler address to the corresponding <code>TumblerService</code>
 * call.
 */
@Scheme("ted")
public final class TedPageRouter implements PageRouter {

	@Inject
	private Injector injector;

	/**
	 * Maps objects to and from JSON
	 */
	private ObjectMapper mapper = new ObjectMapper();

	public TedPageRouter() {
	}

	@Override
	public Page getPage(String tumbler, String body) throws MalformedSpanException, IOException {
		Page page = getRouteController(tumbler);
		if (page != null) {
			injector.injectMembers(page.getController());
		}
		return page;
	}

	private Page getRouteController(String url) throws MalformedSpanException, IOException {
		/*
		 * TumblerAddress tumbler = TumblerAddress.create(url); if
		 * (!Strings.isNullOrEmpty(tumbler.getPath())) { String path =
		 * tumbler.getPath(); if (path.equals("/users")) { if (tumbler.isNodeTumbler())
		 * { return new Page(new GetUsersController()); } else { throw new
		 * IOException("Malformed request: " + path + ", " + tumbler.toExternalForm());
		 * } } else if (path.equals("/documents")) { if (tumbler.isUserTumbler()) {
		 * return new Page(new GetDocumentsController()); } else { throw new
		 * IOException("Malformed request: " + path); } } } else { if
		 * (tumbler.isNetworkTumbler()) { ///
		 * service.getNetwork(tumbler.toTumblerAuthority(), callback); } else if
		 * (tumbler.isNodeTumbler()) { if (tumbler.isSystemAddress()) { //
		 * service.getSystemNodes(queryParams, callback); } else { return new Page(new
		 * GetNodeController());// , new //
		 * View("/org/oulipo/org.oulipo.browser/pages/GetNodeView.fxml")); } } else if
		 * (tumbler.isUserTumbler()) { if (tumbler.isSystemAddress()) { //
		 * service.getSystemUsers(queryParams, callback); } else { return new Page(new
		 * GetUserController()); } } else if (tumbler.isDocumentTumbler()) { if
		 * (tumbler.isSystemAddress()) { // service.getSystemDocuments(queryParams,
		 * callback); } else { return new Page(new GetDocumentController()); } } }
		 */
		return null;

	}
}
