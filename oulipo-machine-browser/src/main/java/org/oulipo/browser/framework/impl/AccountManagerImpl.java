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

import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.people.Account;
import org.oulipo.browser.api.people.AccountManager;
import org.oulipo.browser.api.people.CurrentUser;
import org.oulipo.browser.api.people.UserSession;
import org.oulipo.browser.api.storage.SessionStorage;
import com.google.common.base.Strings;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.oulipo.browser.api.storage.SessionStorage.Property;
import org.oulipo.client.services.AuthServiceBuilder;
import org.oulipo.security.auth.SessionResponse;
import org.oulipo.security.auth.TempTokenResponse;
import org.oulipo.security.auth.XanAuthUri;
import org.oulipo.security.keystore.FileStorage;
import org.oulipo.storage.StorageException;
import org.oulipo.storage.StorageService;
import org.oulipo.streams.RemoteFileManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

public final class AccountManagerImpl implements AccountManager {

	private ToggleGroup accountGroup = new ToggleGroup();

	private final StorageService accountStorage;

	private Account activeAccount;

	private final BrowserContext ctx;

	private final RemoteFileManager fileManager;

	private FileStorage keyStorage;

	private final Menu menu;

	private SessionStorage sessionStorage;

	public AccountManagerImpl(Menu menu, BrowserContext ctx, SessionStorage sessionStorage,
                              StorageService accountStorage, FileStorage keys, RemoteFileManager fileManager)
			throws StorageException, IOException {
		this.menu = menu;
		this.ctx = ctx;
		this.sessionStorage = sessionStorage;
		this.accountStorage = accountStorage;
		this.fileManager = fileManager;
		this.keyStorage = keys;
		load();
	}

	@Override
	public void add(Account account) throws StorageException, IOException {
		accountStorage.save(account);
		addAccountToMenu(account, account.publicKey);
	}

	private void addAccountToMenu(final Account account, String activePublicKey) throws IOException {
		RadioMenuItem item = new RadioMenuItem();
		item.setText(account.xandle);
		item.setToggleGroup(accountGroup);
		byte[] image = fileManager.get("QmVsQeC6Xk5J2AyizHhh441RDTYZf3gTEj9krV2gp8uoJu");
		ImageView iv = new ImageView(new Image(new ByteArrayInputStream(image), 48, 48, false, false));
		item.setGraphic(iv);
		item.setOnAction(e -> {
			activeAccount = account;
			try {
				sessionStorage.add(new Property("activeUser", account.publicKey));
			} catch (StorageException | IOException e1) {
				e1.printStackTrace();
			}
			try {
				ctx.launchNewToolbar(false, account.publicKey);
			} catch (IOException | StorageException e1) {
				e1.printStackTrace();
			}

		});
		if (!Strings.isNullOrEmpty(activePublicKey) && activePublicKey.equals(account.publicKey)) {
			item.setSelected(true);
		}
		getMenu().getItems().add(item);
	}

	@Override
	public String addItemImage(Account account, File file) throws IOException, StorageException {
		String hash = fileManager.add(file);
		account.imageHash = hash;
		accountStorage.save(account);
		return hash;
	}

	@Override
	public MenuItem createMenuItemFrom(Account account) throws StorageException, IOException {
		return null;
	}

	@Override
	public void delete(Account item) throws StorageException {
		accountStorage.delete(item.publicKey, Account.class);
	}

	@Override
	public Account get(String publicKey) throws StorageException, IOException {
		return accountStorage.load(publicKey, Account.class);
	}

	@Override
	public Account getActiveAccount() {
		return activeAccount;
	}

	@Override
	public Collection<Account> getAll() throws StorageException, IOException {
		try {
			return accountStorage.getAll(Account.class);
		} catch (ClassNotFoundException e) {
			throw new StorageException(e.getMessage());
		}
	}

	@Override
	public CurrentUser getCurrentUserAddress() throws StorageException {
		try {
			return accountStorage.load("current_user", CurrentUser.class);
		} catch (Exception e) {
			e.printStackTrace();// TODO: launch new user page
		}
		return new CurrentUser();//return dummy for now
	}

	@Override
	public Menu getMenu() {
		return menu;
	}

	@Override
	public String getTokenFor(Account account) throws StorageException {
		return accountStorage.load(account.publicKey, UserSession.class).sessionToken;
	}

	@Override
	public void load() throws StorageException, IOException {
		Property prop = null;
		try {
			prop = sessionStorage.get("activeUser");
		} catch (Exception e) {
		}

		String publicKey = prop != null ? prop.value : null;
		if (publicKey != null) {
			this.activeAccount = get(publicKey);
		}

		for (Account account : getAll()) {
			addAccountToMenu(account, publicKey);
		}
	}

	@Override
	public void login(Account account, String uri) throws UnsupportedEncodingException, StorageException {
		AuthServiceBuilder builder = new AuthServiceBuilder("http://localhost:4567/");
		builder.build().getTempToken().enqueue(new Callback<TempTokenResponse>() {

			@Override
			public void onFailure(Call<TempTokenResponse> arg0, Throwable arg1) {
				arg1.printStackTrace();
			}

			@Override
			public void onResponse(Call<TempTokenResponse> arg0, Response<TempTokenResponse> response) {
				try {
					ECKey key = keyStorage.getECKey(account.xandle);
					XanAuthUri xanAuth = new XanAuthUri.Builder().uri(response.body().xanauth).key(key).build();

					SessionResponse sessionResponse = xanAuth.makeRequest();
					UserSession session = new UserSession();
					session.publicKey = account.publicKey;
					session.sessionToken = sessionResponse.masterToken;
					accountStorage.save(session);
					try {
						sessionStorage.add(new Property("activeUser", account.publicKey));
					} catch (StorageException | IOException e1) {
						e1.printStackTrace();
					}

					System.out.println(session);
				} catch (UnsupportedEncodingException | StorageException e) {
					e.printStackTrace();
				}
			}

		});

	}

	@Override
	public Account newAccount() throws IOException, StorageException {
		ECKey key = new ECKey();
		Account account = new Account();
		account.publicKey = key.toAddress(MainNetParams.get()).toString();
		account.xandle = "NewUser-" + account.publicKey;
		keyStorage.add(account.xandle, account.publicKey, key.getPrivKeyBytes(), key.getPubKey());
		add(account);
		activeAccount = account;
		return account;
	}

	@Override
	public void setCurrentUserAddress(CurrentUser user) throws StorageException {
		accountStorage.save(user);
	}
}
