/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
 *
 * You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for 
 * additional information regarding copyright ownership. 
 *******************************************************************************/
package org.oulipo.streams.opcodes;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Puts an invariant span into a variant stream.
 */
public final class PutInvariantSpanOp extends Op {

	/**
	 * Index in tumbler pool of this span's tumbler address
	 */
	public final int homeDocumentIndex;

	/**
	 * Start position within the invariant stream
	 */
	public final long invariantStart;

	/**
	 * The variant position to insert text
	 */
	public final long to;

	/**
	 * Width of the specified span
	 */
	public final long width;

	/**
	 * Creates a <code>PutInvariantSpan</code> from the specified stream
	 * 
	 * @param dis
	 *            the input to read the op code from
	 * @throws IOException
	 *             if I/O exception reading the stream
	 */
	public PutInvariantSpanOp(DataInputStream dis) throws IOException {
		this(dis.readLong(), dis.readLong(), dis.readLong(), dis.readInt());
	}

	/**
	 * Creates a <code>PutInvariantSpanOp</code> with the specified position and
	 * indexes
	 * 
	 * @param to
	 *            the variant position to insert text
	 * @param invariantStart
	 *            the start position within the invariant stream
	 * @param width
	 *            the width of span
	 * @param homeDocumentIndex
	 *            the index in tumbler pool of this span's tumbler address
	 * @throws IndexOutOfBoundsException
	 *             if specified to position is less than 1
	 */
	public PutInvariantSpanOp(long to, long invariantStart, long width, int homeDocumentIndex) {
		super(Op.PUT_INVARIANT_SPAN);
		this.to = to;
		if (to < 1) {
			throw new IndexOutOfBoundsException("to position must be greater than 0");
		}

		if (invariantStart < 1) {
			throw new IndexOutOfBoundsException("invariantStart must be greater than 0");
		}

		if (width < 1) {
			throw new IndexOutOfBoundsException("Width must be greater than 0");
		}

		if (homeDocumentIndex < 0) {
			throw new IndexOutOfBoundsException("homeDocumentIndex position must be non-negative");
		}

		this.invariantStart = invariantStart;
		this.width = width;
		this.homeDocumentIndex = homeDocumentIndex;
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.PUT_INVARIANT_SPAN);
			dos.writeLong(to);
			dos.writeLong(invariantStart);
			dos.writeLong(width);
			dos.writeInt(homeDocumentIndex);
		}
		os.flush();
		return os.toByteArray();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PutInvariantSpanOp other = (PutInvariantSpanOp) obj;
		if (homeDocumentIndex != other.homeDocumentIndex)
			return false;
		if (invariantStart != other.invariantStart)
			return false;
		if (to != other.to)
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + homeDocumentIndex;
		result = prime * result + (int) (invariantStart ^ (invariantStart >>> 32));
		result = prime * result + (int) (to ^ (to >>> 32));
		result = prime * result + (int) (width ^ (width >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "PutInvariantSpanOp [start=" + invariantStart + ", width=" + width + ", homeDocumentIndex="
				+ homeDocumentIndex + ", to=" + to + "]";
	}

}
