package org.oulipo.streams.impl;

import java.io.File;

import org.junit.Test;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.InvariantStream;

public class FileInvariantStreamTest {

	public static final TumblerAddress homeDocument = TumblerAddress.createWithNoException("ted://1.2.0.2.0.23.1.1");

	@Test
	public void streamLoadable() throws Exception {
		File file = new File("target/streams-junit/FileInvariantStream-" + System.currentTimeMillis() + ".txt");
		InvariantStream stream = new FileInvariantStream(file, homeDocument);
		stream.append("Hello");
		stream.append("Xanadu");


	}
}
