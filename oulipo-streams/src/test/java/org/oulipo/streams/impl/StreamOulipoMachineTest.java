package org.oulipo.streams.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.InvariantSpan;

public class StreamOulipoMachineTest {

	private static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			for (String file : dir.list()) {
				deleteDir(new File(dir, file));
			}
		}
		dir.delete();
	}

	private DefaultStreamLoader streamLoader;

	private File testDir;

	@Test
	public void append() throws Exception {
		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1924.1.1");

		StreamOulipoMachine som = StreamOulipoMachine.create(streamLoader, homeDocument, false);
		InvariantSpan span = som.append("Hello");
		assertEquals(span.start, 1);
		assertEquals(span.width, 5);

		span = som.append("World");
		assertEquals(span.start, 6);
		assertEquals(span.width, 5);
	}

	@After
	public void cleanup() {
		deleteDir(new File("target/test-streams"));
	}

	@Test
	public void getText() throws Exception {
		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1925.1.1");

		StreamOulipoMachine som = StreamOulipoMachine.create(streamLoader, homeDocument, false);
		som.append("Hello");
		som.append("World");
		String result = som.getText(new InvariantSpan(5, 5));
		assertEquals("oWorl", result);
	}

	@Before
	public void setup() {
		String spec = "maximumSize=10000,expireAfterWrite=10m";
		testDir = new File("target/test-streams");
		System.out.println(testDir.getAbsolutePath());
		streamLoader = new DefaultStreamLoader(testDir, spec);
	}
}
