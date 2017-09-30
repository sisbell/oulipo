package org.oulipo.client.services;

import java.io.File;

import org.junit.Test;

public class RemoteFileManagerTest {

	@Test
	public void add() throws Exception {
		RemoteFileManager m = new IpfsFileManager();
		String hash = m.add(new File("Sample.txt"));
		System.out.println(hash);

		byte[] content = m.get(hash);
		System.out.println(new String(content));
	}
}
