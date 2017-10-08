package org.oulipo.streams.impl;

import java.io.File;

import org.junit.Test;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.Spans;
import org.oulipo.streams.InvariantStream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileInvariantStreamTest {

	@Test
	public void streamLoadable() throws Exception {
		File file = new File("target/streams-junit/FileInvariantStream-" + System.currentTimeMillis() + ".txt");
		InvariantStream stream = new FileInvariantStream(file);
		stream.append("Hello");
		stream.append("Xanadu");

		ObjectMapper mapper = new ObjectMapper();
		Spans spans = mapper.readValue(file, Spans.class);

		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1925.1.1");
		RopeVariantStream rope = new RopeVariantStream(homeDocument);
		rope.load(spans);

	}
}
