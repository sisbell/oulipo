package org.oulipo.streams.opcodes;

public class OpFactory {

	public static InsertTextOp createInsertTextOp(byte[] message) {
		return new InsertTextOp(new InsertTextOp.Data(0, ""));
	}
}
