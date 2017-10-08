package org.oulipo.streams.impl;

import java.io.File;

import org.junit.Test;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.Span;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.VariantStream;

public class DefaultStreamsLoaderTest {

	@Test
	public void a() throws Exception {
		String spec = "maximumSize=10000,expireAfterWrite=10m";
		File testDir = new File("test-streams");
		System.out.println(testDir.getAbsolutePath());
		StreamLoader streamLoader = new DefaultStreamLoader(testDir, spec);
		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1924.1.1");
		VariantStream vs = streamLoader.openVariantStream(homeDocument);
		vs.put(1, new Span(1, 10));
		streamLoader.flushVariantCache();
	}
}
