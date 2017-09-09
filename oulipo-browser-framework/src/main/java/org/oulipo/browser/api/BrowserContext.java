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
import java.util.ResourceBundle;

import org.oulipo.browser.api.bookmark.BookmarkManager;
import org.oulipo.browser.api.history.HistoryManager;
import org.oulipo.browser.api.history.HistoryRepository;
import org.oulipo.browser.api.people.Account;
import org.oulipo.browser.api.people.AccountManager;
import org.oulipo.browser.api.storage.RemoteStorage;
import org.oulipo.browser.api.storage.SessionStorage;
import org.oulipo.browser.api.tabs.TabManager;
import org.oulipo.browser.framework.MenuContext;
import org.oulipo.browser.framework.PageRouter;
import org.oulipo.browser.framework.StorageContext;
import org.oulipo.browser.framework.impl.AccountManagerImpl;
import org.oulipo.browser.framework.impl.BookmarkManagerImpl;
import org.oulipo.browser.framework.impl.DummyHistoryRepository;
import org.oulipo.browser.framework.impl.HistoryRepositoryImpl;
import org.oulipo.browser.framework.impl.IpfsRemoteStorage;
import org.oulipo.browser.framework.impl.TabManagerImpl;
import org.oulipo.browser.framework.toolbar.ToolbarController;
import org.oulipo.client.services.DocuverseService;
import org.oulipo.client.services.ServiceBuilder;
import org.oulipo.security.keystore.FileStorage;
import org.oulipo.storage.StorageException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * The context attached to each instance (or window) of the browser. Context
 * will be different per instance.
 */
public final class BrowserContext {

	private AccountManager accountManager;

	private ApplicationContext applicationContext;

	private BookmarkManagerImpl bookmarkManager;

	private StackPane contentArea;

	private HistoryManager historyManager = new HistoryManager();

	private HistoryRepository historyRepository;

	private FileStorage keyStorage;

	private FXMLLoader loader;

	private final MenuContext menuContext;

	private IpfsRemoteStorage remoteStorage;

	private SessionStorage sessionStorage;

	private StorageContext storageContext;

	private TabManager tabManager;

	/**
	 * Constructs a browser context
	 * 
	 * @param loader
	 * @param storageContext
	 * @param menuContext
	 * @throws IOException
	 * @throws StorageException
	 */
	public BrowserContext(ApplicationContext applicationContext, FXMLLoader loader, StackPane contentArea,
			StorageContext storageContext, MenuContext menuContext, PageRouter router) throws IOException, StorageException {
		this.applicationContext = applicationContext;
		this.loader = loader;
		this.contentArea = contentArea;
		this.storageContext = storageContext;
		this.menuContext = menuContext;
		this.remoteStorage = new IpfsRemoteStorage();
		this.sessionStorage = new SessionStorage(storageContext.getSessionStorage());
		this.keyStorage = new FileStorage(storageContext.getKeystoreStorage());
		this.bookmarkManager = new BookmarkManagerImpl(menuContext.getBookmarkMenu(),
				storageContext.getBookmarkStorage());
		this.historyRepository = new HistoryRepositoryImpl(menuContext.getHistoryMenu(),
				storageContext.getHistoryStorage());
		this.tabManager = new TabManagerImpl(this, router, storageContext.getTabStorage(), menuContext.getTabs());
		this.accountManager = new AccountManagerImpl(menuContext.getPeopleMenu(), this, sessionStorage,
				storageContext.getAccountsStorage(), keyStorage, remoteStorage);
	}

	/**
	 * Closes the context and cleans up resources
	 */
	public void closeContext() {
		storageContext.close();
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public BookmarkManager getBookmarkManager() {
		return bookmarkManager;
	}

	public StackPane getContentArea() {
		return contentArea;
	}

	public DocuverseService getDocuverseService() throws StorageException {
		Account account = accountManager.getActiveAccount();
		String token = accountManager.getTokenFor(account);
		return new ServiceBuilder("http://localhost:4567/docuverse/")
				.publicKey(account.publicKey).sessionToken(token)
				.build(DocuverseService.class);
		
	}

	public HistoryManager getHistoryManager() {
		return historyManager;
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

	public RemoteStorage getRemoteStorage() {
		return remoteStorage;
	}

	public SessionStorage getSessionStorage() {
		return sessionStorage;
	}
	
	public TabManager getTabManager() {
		return tabManager;
	}

	/**
	 * Launches a new toolbar in a different window.
	 * 
	 * @param isIncognito
	 * @throws IOException
	 * @throws StorageException
	 */
	public void launchNewToolbar(boolean isIncognito, String publicKey) throws IOException, StorageException {
		Stage stage = new Stage();
		applicationContext.putStage(publicKey, stage);

		RadioMenuItem item = new RadioMenuItem();
		item.setText(publicKey);
		item.setUserData(stage);
		item.setSelected(true);
		menuContext.getWindowMenu().getItems().add(item);

		FXMLLoader loader = getLoader();
		loader.setLocation(getClass().getResource("/org/oulipo/browser/framework/toolbar/ToolbarView.fxml"));
        loader.setResources(ResourceBundle.getBundle("bundles.browser"));

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

	public void showToolbar(String id) {
		Stage stage = applicationContext.getStage(id);
		if (stage != null) {
			stage.show();
		}
	}
}
