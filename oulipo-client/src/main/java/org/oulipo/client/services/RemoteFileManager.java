package org.oulipo.client.services;

import java.io.File;
import java.io.IOException;

public interface RemoteFileManager {

	String add(File file) throws IOException;

	byte[] get(String hash) throws IOException;

}