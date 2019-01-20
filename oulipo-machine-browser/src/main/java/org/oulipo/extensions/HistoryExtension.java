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

import java.util.Date;

import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Extension;
import org.oulipo.browser.api.history.History;
import org.oulipo.storage.StorageException;

public class HistoryExtension implements Extension {

	@Override
	public void init(BrowserContext ctx) {
		ctx.getHistoryManager().registerListener(tab -> {
			if (tab.hasAddress()) {
				History history = new History();
				history.id = tab.getTitle();
				history.title = tab.getTitle();
				history.created = new Date();
				try {
					ctx.getHistoryRepository().add(history);
				} catch (StorageException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
