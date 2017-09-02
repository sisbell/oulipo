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
package org.oulipo.browser.api;

import java.io.IOException;

import org.oulipo.browser.api.bookmark.BookmarkManager;
import org.oulipo.browser.api.history.HistoryManager;
import org.oulipo.browser.api.history.HistoryRepository;
import org.oulipo.browser.api.storage.RemoteStorage;
import org.oulipo.browser.api.tabs.TabManager;
import org.oulipo.browser.framework.MenuContext;
import org.oulipo.browser.framework.StorageContext;
import org.oulipo.browser.framework.impl.BookmarkManagerImpl;
import org.oulipo.browser.framework.impl.DummyHistoryRepository;
import org.oulipo.browser.framework.impl.HistoryRepositoryImpl;
import org.oulipo.browser.framework.impl.IpfsRemoteStorage;
import org.oulipo.browser.framework.impl.TabManagerImpl;
import org.oulipo.browser.framework.toolbar.ToolbarController;
import org.oulipo.storage.StorageException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The context attached to each instance (or window) of the browser. Context
 * will be different per instance.
 */
public final class BrowserContext {

	private BookmarkManagerImpl bookmarkManager;

	private HistoryRepository historyRepository;

	private FXMLLoader loader;

	private final MenuContext menuContext;

	private StorageContext storageContext;

	private TabManager tabManager;

	private HistoryManager historyManager = new HistoryManager();

	private IpfsRemoteStorage remoteStorage;

	private StackPane contentArea;

	/**
	 * Constructs a browser context
	 * 
	 * @param loader
	 * @param storageContext
	 * @param menuContext
	 * @throws IOException
	 * @throws StorageException
	 */
	public BrowserContext(FXMLLoader loader, StackPane contentArea, StorageContext storageContext, MenuContext menuContext)
			throws IOException, StorageException {
		this.loader = loader;
		this.contentArea = contentArea;
		this.storageContext = storageContext;
		this.menuContext = menuContext;
		this.bookmarkManager = new BookmarkManagerImpl(menuContext.getBookmarkMenu(),
				storageContext.getBookmarkStorage());
		this.historyRepository = new HistoryRepositoryImpl(menuContext.getHistoryMenu(),
				storageContext.getHistoryStorage());
		this.tabManager = new TabManagerImpl(storageContext.getTabStorage(), menuContext.getTabs());
		this.remoteStorage = new IpfsRemoteStorage();
	}

	/**
	 * Closes the context and cleans up resources
	 */
	public void closeContext() {
		storageContext.close();
	}

	public BookmarkManager getBookmarkManager() {
		return bookmarkManager;
	}

	public HistoryRepository getHistoryRepository() {
		return historyRepository;
	}

	public FXMLLoader getLoader() {
		loader.setController(null);
		loader.setLocation(null);
		loader.setRoot(null);
		return loader;
	}

	public MenuContext getMenuContext() {
		return menuContext;
	}

	public TabManager getTabManager() {
		return tabManager;
	}

	public HistoryManager getHistoryManager() {
		return historyManager;
	}
	
	public RemoteStorage getRemoteStorage() {
		return remoteStorage;
	}
	
	public StackPane getContentArea() {
		return contentArea;
	}

	/**
	 * Launches a new toolbar in a different window.
	 * 
	 * @param isIncognito
	 * @throws IOException
	 * @throws StorageException
	 */
	public void launchNewToolbar(boolean isIncognito) throws IOException, StorageException {
		Stage stage = new Stage();

		FXMLLoader loader = getLoader();
		loader.setLocation(getClass().getResource("/org/oulipo/browser/framework/toolbar/ToolbarView.fxml"));
		Parent browser = loader.load();
		Scene scene = new Scene(browser);
		ToolbarController controller = loader.getController();

		if (isIncognito) {
			scene.getStylesheets().clear();
			scene.setUserAgentStylesheet(null);
			scene.getStylesheets().add(getClass().getResource("material.css").toExternalForm());
			historyRepository = new DummyHistoryRepository();
			controller.setIncognitoMode();
		}
		stage.setScene(scene);
		stage.show();
	}
}
