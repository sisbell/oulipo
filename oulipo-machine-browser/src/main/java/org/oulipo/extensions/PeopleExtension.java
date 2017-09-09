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
package org.oulipo.extensions;

import java.io.IOException;

import org.oulipo.browser.api.BaseExtension;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Extension;
import org.oulipo.browser.api.people.Account;
import org.oulipo.browser.api.tabs.OulipoTab;
import org.oulipo.browser.framework.MenuContext.Type;
import org.oulipo.storage.StorageException;

public class PeopleExtension extends BaseExtension implements Extension {

	@Override
	public void init(BrowserContext ctx) {
		addSeparator(ctx, Type.PEOPLE);
		addMenuItem(ctx, "Add Person", Type.PEOPLE, e -> {
			try {
				Account newAccount = ctx.getAccountManager().newAccount();
				ctx.getAccountManager().login(newAccount, "http://localhost:4567/auth");
				OulipoTab tab = new OulipoTab(newAccount.xandle);

				ctx.getTabManager().add(tab);
				ctx.getTabManager().selectTab(tab);

			} catch (IOException | StorageException e1) {
				e1.printStackTrace();
			}
		});

	}

}
