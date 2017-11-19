package org.oulipo.streams.opcodes;

import java.io.IOException;

/**
 * Puts meta-data into <code>DocumentFile</code>
 */
public final class PutMetaDataOp extends Op {

	/**
	 * Constructs <code>PutMetaDataOp</code> with specified indexes
	 * 
	 */
	public PutMetaDataOp(int subjectIndex, int predicateIndex, int objectIndex, byte objectType) {
		super(Op.PUT_META_DATA);
	}

	@Override
	public byte[] encode() throws IOException {
		return null;
	}

}
