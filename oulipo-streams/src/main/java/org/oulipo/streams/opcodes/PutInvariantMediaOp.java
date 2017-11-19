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
 * Puts a media object in the document. This operation will reference the index
 * of hash of a media object (like an image). The hash is stored in a mediaPool
 * within the document.
 * 
 * The invariant media may have a different document address than the host
 * documentFile.
 */
public final class PutInvariantMediaOp extends Op {

	/**
	 * Index in media pool of this media's hash value
	 */
	public final int ripIndex;

	/**
	 * Variant position in document to put media object
	 */
	public final long to;

	/**
	 * Constructs a PutInvariantMediaOp from the specified
	 * <code>DataInputStream</code>
	 * 
	 * @param dis
	 *            the input to read the op code from
	 * @throws IOException
	 *             if I/O exception reading the stream
	 * @throws IndexOutOfBoundsException
	 *             if to < 1 or mediaPoolIndex/mediaTumblerIndex are negative
	 */
	public PutInvariantMediaOp(DataInputStream dis) throws IOException {
		this(dis.readLong(), dis.readInt());
	}

	/**
	 * Creates a <code>PutInvariantMediaOp</code> with the specified positions and
	 * indexes
	 * 
	 * @param to
	 *            the variant position. Must be greater than 0.
	 * @param ripIndex
	 *            the index of the hash. Must be non-negative.
	 * @throws IndexOutOfBoundsException
	 *             if to < 1 or mediaPoolIndex is negative
	 */
	public PutInvariantMediaOp(long to, int ripIndex) {
		super(Op.PUT_INVARIANT_MEDIA);
		this.to = to;
		if (to < 1) {
			throw new IndexOutOfBoundsException("to position must be greater than 0");
		}

		if (ripIndex < 0) {
			throw new IndexOutOfBoundsException("mediaPoolIndex position must be non-negative");
		}

		this.ripIndex = ripIndex;
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.PUT_INVARIANT_MEDIA);
			dos.writeLong(to);
			dos.writeInt(ripIndex);
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
		PutInvariantMediaOp other = (PutInvariantMediaOp) obj;
		if (ripIndex != other.ripIndex)
			return false;
		if (to != other.to)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ripIndex;
		result = prime * result + (int) (to ^ (to >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "PutInvariantMediaOp [mediaPoolIndex=" + ripIndex + ", to=" + to + "]";
	}

}
