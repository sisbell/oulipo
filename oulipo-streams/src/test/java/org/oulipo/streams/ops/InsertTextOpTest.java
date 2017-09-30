package org.oulipo.streams.ops;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.oulipo.streams.opcodes.InsertTextOp;

public class InsertTextOpTest {

	@Test
	public void readWrite() throws Exception {
		InsertTextOp op = new InsertTextOp(new InsertTextOp.Data(5, "Hello Xanadu"));
		byte[] b = op.toBytes();
		byte[] c = new byte[b.length - 1];
		System.arraycopy(b, 1, c, 0, b.length - 1);
		InsertTextOp result = InsertTextOp.read(c);

		assertEquals(op.getData(), result.getData());
		assertEquals("Hello Xanadu", result.getData().text);

	}
}
