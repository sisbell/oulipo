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
package org.oulipo.browser.pages;

import java.io.IOException;

import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Page;
import org.oulipo.client.services.DocuverseService;
import org.oulipo.storage.StorageException;
import org.oulipo.streams.IRI;

public abstract class BaseController implements Page.Controller {

	protected IRI address;

	protected AddressBarController addressBarController;

	protected BrowserContext ctx;

	protected DocuverseService docService;

	@Override
	public void show(AddressBarController controller) throws IOException {
		this.ctx = controller.getContext();
		this.addressBarController = controller;
		this.address = addressBarController.getAddress();
		controller.setTabTitle(address.value);
		try {
			docService = ctx.getDocuverseService();
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}

}
