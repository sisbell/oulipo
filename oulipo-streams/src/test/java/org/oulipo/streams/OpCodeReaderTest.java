package org.oulipo.streams;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Iterator;

import org.junit.Test;
import org.oulipo.streams.opcodes.Op;

import com.google.common.io.BaseEncoding;

public class OpCodeReaderTest {

	@Test
	public void a() throws Exception {
		String body = "BAAAAAAAAAAAABJkZnNzZGZzZGZhc2RmYXNkZmE=";
		byte[] bodyBytes = BaseEncoding.base64Url().decode(body);

		OpCodeReader reader = new OpCodeReader(new DataInputStream(new ByteArrayInputStream(bodyBytes)));
		Iterator<Op<?>> codes = reader.iterator();
		while (codes.hasNext()) {
			System.out.println(codes.next().getData());
		}

	}
}
