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
package org.oulipo.browser.api.people;

import org.oulipo.browser.api.MenuManager;
import org.oulipo.browser.api.Repository;
import org.oulipo.storage.StorageException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface AccountManager extends MenuManager<Account>, Repository<Account> {

	Account getActiveAccount();;

	CurrentUser getCurrentUserAddress() throws StorageException;;

	String getTokenFor(Account account) throws StorageException;

	void login(Account account, String uri) throws UnsupportedEncodingException, StorageException;

	Account newAccount() throws IOException, StorageException;

	void setCurrentUserAddress(CurrentUser user) throws StorageException;
}
