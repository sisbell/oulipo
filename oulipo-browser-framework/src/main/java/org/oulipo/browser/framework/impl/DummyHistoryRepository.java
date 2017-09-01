package org.oulipo.browser.framework.impl;

import java.io.IOException;
import java.util.ArrayList;

import org.oulipo.browser.api.history.History;
import org.oulipo.browser.api.history.HistoryRepository;
import org.oulipo.storage.StorageException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DummyHistoryRepository implements HistoryRepository {

	@Override
	public void add(History history) throws StorageException {
		
	}

	@Override
	public void delete(History history) throws StorageException {
		
	}

	@Override
	public History get(String id) throws StorageException {
		return null;
	}

	@Override
	public ObservableList<History> getAll() throws StorageException, IOException {
		return FXCollections.observableArrayList(new ArrayList<>());
	}

}
