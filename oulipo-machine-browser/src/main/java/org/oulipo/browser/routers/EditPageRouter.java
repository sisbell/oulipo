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
import org.oulipo.browser.api.Page.View;
import org.oulipo.browser.api.PageRouter;
import org.oulipo.browser.api.Scheme;
import org.oulipo.browser.pages.NewDocumentController;
import org.oulipo.browser.pages.NewUserController;
import org.oulipo.browser.pages.RegisterNodeController;
import org.oulipo.browser.pages.UpdateDocumentController;
import org.oulipo.browser.pages.UpdateNodeController;
import org.oulipo.browser.pages.UpdateUserController;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Routes a tumbler address to the corresponding <code>TumblerService</code>
 * call.
 */
@Scheme("edit")
public final class EditPageRouter implements PageRouter {

	@Inject
	private Injector injector;

	/**
	 * Maps objects to and from JSON
	 */
	private ObjectMapper mapper = new ObjectMapper();

	public EditPageRouter() {
	}

	@Override
	public Page getPage(String tumbler, String body) throws MalformedSpanException, IOException {
		Page page = putRouteController(tumbler, body);
		if (page != null) {
			injector.injectMembers(page.controller);
		}
		return page;
	}

	/**
	 * Makes PUT request to the specified tumbler
	 * 
	 * @param tumbler
	 *            the tumbler to make the request
	 * @param body
	 *            the message body to post to the tumbler resource
	 * @param callback
	 *            the async callback
	 * @throws IOException
	 *             if there is I/O exception in making the network request
	 */
	public Page putRouteController(String url, String body) throws IOException {
		TumblerAddress tumbler = TumblerAddress.create(url);
		String path = tumbler.getPath();

		if (tumbler.isNetworkTumbler()) {
			return new Page(new RegisterNodeController(), new View("/org/oulipo/browser/pages/RegisterNodeView.fxml"));
		} else if (tumbler.isNodeTumbler()) {
			if (path.equals("/users")) {
				return new Page(new NewUserController());
			} else {
				return new Page(new UpdateNodeController());
			}
		} else if (tumbler.isUserTumbler()) {
			if (path.equals("/documents")) {
				return new Page(new NewDocumentController());
			} else {
				return new Page(new UpdateUserController());
			}
		} else if (tumbler.isDocumentTumbler()) {
			return new Page(new UpdateDocumentController());
		} else if (tumbler.isElementTumbler() && tumbler.isLinkElement()) {

		}
		return null;
	}
}
