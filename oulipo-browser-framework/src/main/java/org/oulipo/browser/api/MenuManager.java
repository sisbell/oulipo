package org.oulipo.browser.api;

import java.io.IOException;
import java.util.Collection;

import org.oulipo.storage.StorageException;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public interface MenuManager<T> {

	default void addToMenu(T item) throws StorageException, IOException {
		getMenu().getItems().add(createMenuItemFrom(item));
	}

	MenuItem createMenuItemFrom(T item) throws StorageException, IOException;

	Collection<T> getAll() throws StorageException, IOException;

	Menu getMenu();

	/**
	 * Loads items in menu
	 * 
	 * @throws StorageException
	 * @throws IOException
	 */
	default void load() throws StorageException, IOException {
		for (T item : getAll()) {
			addToMenu(item);
		}
	}

}
