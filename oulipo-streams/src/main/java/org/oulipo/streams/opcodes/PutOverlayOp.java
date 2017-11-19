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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.VariantSpan;

/**
 * Adds an overlay over a region of text and media. The linkTypes reference
 * things like styling and links to apply to the region.
 */
public final class PutOverlayOp extends Op {

	/**
	 * The overlay types such as styling and jump links. These are indexes into the
	 * tumblerPool.
	 */
	public final Set<Integer> linkTypes;

	/**
	 * The variant span of the document to put the overly
	 */
	public final VariantSpan variantSpan;

	/**
	 * Constructs a <code>PutOverlayOp</code> from the specified stream
	 * 
	 * @param dis
	 *            the input to read the op code from
	 * @throws MalformedSpanException
	 *             if variant span read from stream is malformed
	 * @throws IOException
	 *             if I/O exception reading the stream
	 * @throws IndexOutOfBoundsException
	 *             if any tumbler pool index in the stream is non-negative
	 */
	public PutOverlayOp(DataInputStream dis) throws MalformedSpanException, IOException {
		super(Op.PUT_OVERLAY);

		variantSpan = new VariantSpan(dis);
		int length = dis.readInt();
		linkTypes = new HashSet<>(length);

		for (int i = 0; i < length; i++) {
			int index = dis.readInt();
			if (index < 0) {
				throw new IndexOutOfBoundsException("Tumbler pool index must be 0 or greater: " + i);
			}
			linkTypes.add(index);
		}
	}

	/**
	 * Constructs a PutOverlayOp with the specified variantSpan and linkTypes. The
	 * linkTypes may be empty (but not null). If the set is empty, then this is the
	 * default value for a span of text and the client can assign its standard
	 * default.
	 * 
	 * @param variantSpan
	 *            the span of text to apply the overlay
	 * @param linkTypes
	 *            the linkTypes for this overlay. May not be null.
	 * @throws IllegalArgumentException
	 *             if any argument is null
	 */
	public PutOverlayOp(VariantSpan variantSpan, Set<Integer> linkTypes) {
		super(Op.PUT_OVERLAY);
		if (variantSpan == null) {
			throw new IllegalArgumentException("null variant span");
		}

		if (linkTypes == null) {
			throw new IllegalArgumentException("null linkTypes");
		}

		this.variantSpan = variantSpan;
		this.linkTypes = Collections.unmodifiableSet(linkTypes);
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.PUT_OVERLAY);
			dos.writeLong(variantSpan.start);
			dos.writeLong(variantSpan.width);
			dos.writeInt(linkTypes.size());
			for (Integer i : linkTypes) {
				dos.writeInt(i);
			}
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
		PutOverlayOp other = (PutOverlayOp) obj;
		if (!linkTypes.equals(other.linkTypes))
			return false;
		if (!variantSpan.equals(other.variantSpan))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + linkTypes.hashCode();
		result = prime * result + variantSpan.hashCode();
		return result;
	}

}
