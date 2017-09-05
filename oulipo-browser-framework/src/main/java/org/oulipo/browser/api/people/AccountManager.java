package org.oulipo.browser.api.people;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.oulipo.browser.api.MenuManager;
import org.oulipo.browser.api.Repository;
import org.oulipo.client.services.RemoteFileManager;
import org.oulipo.security.auth.SessionResponse;
import org.oulipo.security.auth.XanAuthUri;
import org.oulipo.security.keystore.FileStorage;
import org.oulipo.storage.StorageException;
import org.oulipo.storage.StorageService;

import com.google.common.base.Strings;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

public final class AccountManager implements MenuManager<Account>, Repository<Account> {

	private final RemoteFileManager fileManager;

	private FileStorage keyStorage;

	private final Menu menu;

	private final StorageService storageService;

	public AccountManager(Menu menu, StorageService storageService, StorageService keys, RemoteFileManager fileManager)
			throws StorageException, IOException {
		this.menu = menu;
		this.storageService = storageService;
		this.fileManager = fileManager;
		this.keyStorage = new FileStorage(keys);
		load();
	}

	@Override
	public void add(Account account) throws StorageException, IOException {
		storageService.save(account);
		addToMenu(account);
	}

	@Override
	public String addItemImage(Account account, File file) throws IOException, StorageException {
		String hash = fileManager.add(file);
		account.imageHash = hash;
		storageService.save(account);
		return hash;
	}

	@Override
	public MenuItem createMenuItemFrom(Account account) throws StorageException, IOException {
		MenuItem item = new MenuItem();
		item.setText(account.xandle);
		if (!Strings.isNullOrEmpty(account.imageHash)) {
			// byte[] image = fileManager.get(account.imageHash);
			item.setGraphic(new ImageView());
		}
		item.setUserData(account);
		return item;
	}

	@Override
	public void delete(Account item) throws StorageException {
		storageService.delete(item.publicKey, Account.class);
	}

	@Override
	public Account get(String publicKey) throws StorageException, IOException {
		return storageService.load(publicKey, Account.class);
	}

	@Override
	public Collection<Account> getAll() throws StorageException, IOException {
		try {
			return storageService.getAll(Account.class);
		} catch (ClassNotFoundException e) {
			throw new StorageException(e.getMessage());
		}
	}

	@Override
	public Menu getMenu() {
		return menu;
	}

	public void login(Account account, String uri, ECKey key) throws UnsupportedEncodingException, StorageException {
		XanAuthUri xanAuth = new XanAuthUri.Builder().uri(uri).key(key).build();
		SessionResponse response = xanAuth.makeRequest();
		UserSession session = new UserSession();
		session.publicKey = account.publicKey;
		session.sessionToken = response.masterToken;
		storageService.save(session);
	}

	public Account newAccount() throws IOException, StorageException {
		ECKey key = new ECKey();
		Account account = new Account();
		account.publicKey = key.toAddress(MainNetParams.get()).toString();
		account.xandle = "NewUser-" + account.publicKey;
		keyStorage.add(account.xandle, account.publicKey, key.getPrivKeyBytes(), key.getPubKey());
		storageService.save(account);
		return account;
	}
}
