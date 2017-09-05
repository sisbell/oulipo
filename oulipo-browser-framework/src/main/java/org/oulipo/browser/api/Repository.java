package org.oulipo.browser.api;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.oulipo.storage.StorageException;

public interface Repository<T> {

	void add(T item) throws StorageException, IOException;
	
	String addItemImage(T item, File file) throws IOException, StorageException;
	
	void delete(T item) throws StorageException;
	
	T get(String id) throws StorageException, IOException ;
	
	Collection<T> getAll() throws StorageException, IOException ;
}
