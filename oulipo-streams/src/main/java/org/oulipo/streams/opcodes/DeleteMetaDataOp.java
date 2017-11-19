package org.oulipo.streams.opcodes;

import java.io.IOException;

public final class DeleteMetaDataOp extends Op {

	public DeleteMetaDataOp(int subjectIndex, int predicateIndex, int objectIndex) {
		super(Op.DELETE_META_DATA);
	}

	@Override
	public byte[] encode() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
