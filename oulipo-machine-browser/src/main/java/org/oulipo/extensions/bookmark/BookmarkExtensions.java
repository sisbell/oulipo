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
package org.oulipo.extensions.bookmark;

import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Extension;
import org.oulipo.browser.api.bookmark.Bookmark;
import org.oulipo.browser.api.tabs.OulipoTab;
import org.oulipo.storage.StorageException;

import javafx.scene.control.MenuItem;

/**
 * Provides extension for creating bookmarks
 */
public class BookmarkExtensions implements Extension {

	@Override
	public void init(BrowserContext ctx) {
		MenuItem item = new MenuItem();
		item.setText("Bookmark This Page");
		item.setOnAction(e -> {
			OulipoTab tab = ctx.getTabManager().getSelectedTab();
			Bookmark bookmark = new Bookmark();
			bookmark.title =  tab.getTitle();
			bookmark.id = tab.getTumblerAddress();
			try {
				ctx.getBookmarkManager().add(bookmark);
			} catch (StorageException e1) {
				e1.printStackTrace();
			}	
		});
		ctx.getMenuContext().getBookmarkMenu().getItems().add(1, item);	
	}
	
}
