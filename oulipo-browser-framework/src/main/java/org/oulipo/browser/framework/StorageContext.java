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
package org.oulipo.browser.framework;

import java.io.IOException;

import org.oulipo.storage.StorageService;

/**
 * Provides access to system level storage services
 */
public final class StorageContext {

	private final StorageService accountsStorage;

	private final StorageService bookmarkStorage;

	private final StorageService historyStorage;

	private final StorageService keystoreStorage;

	private final StorageService sessionStorage;

	private final StorageService tabStorage;

	/**
	 * Creates a StorageContext
	 * 
	 * @throws IOException
	 *             if there was an I/O Exception in the underlying storage
	 */
	public StorageContext() throws IOException {
		this.historyStorage = new StorageService("history");
		this.bookmarkStorage = new StorageService("bookmark");
		this.tabStorage = new StorageService("tab");
		this.accountsStorage = new StorageService("accounts-local");
		this.keystoreStorage = new StorageService("keystore");
		this.sessionStorage = new StorageService("session");
	}

	/**
	 * Close storage access. Any attempt to access storage after this method has
	 * been invoked will result in an error.
	 */
	public void close() {
		this.historyStorage.close();
		this.bookmarkStorage.close();
		this.tabStorage.close();
		this.accountsStorage.close();
		this.keystoreStorage.close();
		this.sessionStorage.close();
	}

	public StorageService getAccountsStorage() {
		return accountsStorage;
	}

	public StorageService getBookmarkStorage() {
		return bookmarkStorage;
	}

	public StorageService getHistoryStorage() {
		return historyStorage;
	}

	public StorageService getKeystoreStorage() {
		return keystoreStorage;
	}

	public StorageService getSessionStorage() {
		return sessionStorage;
	}

	public StorageService getTabStorage() {
		return tabStorage;
	}
}
