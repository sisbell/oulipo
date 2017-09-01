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
package org.oulipo.browser.framework.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.oulipo.browser.api.bookmark.Bookmark;
import org.oulipo.browser.api.bookmark.BookmarkCategory;
import org.oulipo.browser.api.bookmark.BookmarkManager;
import org.oulipo.storage.StorageException;
import org.oulipo.storage.StorageService;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public final class BookmarkManagerImpl implements BookmarkManager {

	private static MenuItem createMenuItemFrom(Bookmark bookmark) {
		MenuItem item = new MenuItem();
		item.setText(bookmark.title);
		item.setUserData(bookmark);
		return item;
	}
	
	private final Menu menu;

	private final StorageService storageService;
	
	public BookmarkManagerImpl(Menu menu, StorageService storageService) throws StorageException, IOException {
		this.menu = menu;
		this.storageService = storageService;
		try {
			load();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void add(Bookmark bookmark) throws StorageException {
		storageService.save(bookmark);
		menu.getItems().add(createMenuItemFrom(bookmark));
	}
	
	@Override
	public void add(BookmarkCategory category) throws StorageException {
		storageService.save(category);
	}
	
	@Override
	public void deleteBookmark(String id) throws StorageException {
		storageService.delete(id, Bookmark.class);
		Iterator<MenuItem> it = menu.getItems().iterator();
		while(it.hasNext()) {
			MenuItem item = it.next();
			if(item.getText().equals(id)) {
				it.remove();
				break;
			}
		}
	}
	
	@Override
	public void deleteBookmarkCategory(String id) throws StorageException {
		storageService.delete(id, BookmarkCategory.class);
	}
	
	@Override
	public Collection<BookmarkCategory> getBookmarkCategories() throws StorageException, IOException {
		try {
			return storageService.getAll(BookmarkCategory.class);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Collection<Bookmark> getBookmarks() throws ClassNotFoundException, StorageException, IOException {
		return storageService.getAll(Bookmark.class);
	}
	
	private void load() throws ClassNotFoundException, StorageException, IOException {
		for(Bookmark bookmark : getBookmarks()) {
			menu.getItems().add(createMenuItemFrom(bookmark));
		}
	}
}
