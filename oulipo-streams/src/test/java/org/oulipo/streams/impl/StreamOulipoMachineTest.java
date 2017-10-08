package org.oulipo.streams.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.Span;
import org.oulipo.streams.OpCodeReader;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.opcodes.Op;

import com.google.common.io.BaseEncoding;

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
	public void pushCode() throws Exception {
		String base64Body = "";
		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1924.1.1");

		StreamOulipoMachine om = StreamOulipoMachine.create(streamLoader, homeDocument, false);
		byte[] bodyBytes = BaseEncoding.base64Url().decode(base64Body);
		OpCodeReader reader = new OpCodeReader(new DataInputStream(new ByteArrayInputStream(bodyBytes)));
		Iterator<Op<?>> codes = reader.iterator();
		while (codes.hasNext()) {
			om.push(codes.next());
		}
		om.flush();
		reader.close();
	}
	
	@Test
	public void append() throws Exception {
		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1924.1.1");

		StreamOulipoMachine som = StreamOulipoMachine.create(streamLoader, homeDocument, false);
		Span span = som.append("Hello");
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
		String result = som.getText(new Span(5, 5));
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
