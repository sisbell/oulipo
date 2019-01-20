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
import org.oulipo.browser.pages.SearchController;
import org.oulipo.streams.MalformedSpanException;

@Scheme("search")
public class SearchPageRouter implements PageRouter {

	@Override
	public Page getPage(String tumbler, String body) throws IOException, MalformedSpanException {
		return new Page(new SearchController(), new View("/org/oulipo/browser/pages/SearchView.fxml"));
	}

}
