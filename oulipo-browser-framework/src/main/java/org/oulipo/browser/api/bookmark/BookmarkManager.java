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
package org.oulipo.browser.api.bookmark;

import java.io.IOException;
import java.util.Collection;

import org.oulipo.storage.StorageException;

/**
 * Service for adding, deleting and getting bookmarks.
 */
public interface BookmarkManager {

	void add(Bookmark bookmark) throws StorageException;

	void add(BookmarkCategory category) throws StorageException;

	void deleteBookmark(String id) throws StorageException;

	void deleteBookmarkCategory(String id) throws StorageException;

	Collection<BookmarkCategory> getBookmarkCategories() throws StorageException, IOException;

	Collection<Bookmark> getBookmarks() throws ClassNotFoundException, StorageException, IOException;

}