package org.oulipo.browser.framework.impl;

import java.io.File;
import java.io.IOException;

import org.oulipo.browser.api.storage.RemoteStorage;
import org.oulipo.client.services.IpfsFileManager;

public final class IpfsRemoteStorage implements RemoteStorage {

	private final IpfsFileManager fileManager;

	public IpfsRemoteStorage() {
		this.fileManager = new IpfsFileManager();
	}
	
	@Override
	public String add(File file) throws IOException {
		return fileManager.add(file);
	}

	@Override
	public byte[] get(String hash) throws IOException {
		return fileManager.get(hash);
	}

}
