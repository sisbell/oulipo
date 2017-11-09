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

import org.oulipo.net.MalformedSpanException;
import org.oulipo.streams.VariantSpan;

/**
 * Applies an overlay over a region of text or media. An overlay can be
 * formatting (bold, italic), jump links or other types of links.
 * 
 */
public final class ApplyOverlayOp extends Op {

	/**
	 * The overlay types such as styling and jump links. These are indexes into the
	 * tumblerPool.
	 */
	public final Set<Integer> linkTypes;

	/**
	 * The span of text to apply the overlay on
	 */
	public final VariantSpan variantSpan;

	/**
	 * Constructs a ApplyOverlayOp from the specified <code>DataInputStream</code>
	 * 
	 * @param dis
	 *            the input to read the op code from
	 * @throws IOException
	 *             if I/O exception reading the stream
	 * @throws MalformedSpanException
	 *             if variant span read from stream is malformed
	 */
	public ApplyOverlayOp(DataInputStream dis) throws MalformedSpanException, IOException {
		super(Op.APPLY_OVERLAY);
		if (dis == null) {
			throw new IOException("stream is null");
		}
		variantSpan = new VariantSpan(dis);
		int length = dis.readInt();
		Set<Integer> links = new HashSet<>(length);

		for (int i = 0; i < length; i++) {
			int index = dis.readInt();
			if (index < 0) {
				throw new IOException("Tumbler pool index must be 0 or greater: " + i);
			}
			links.add(index);
		}
		linkTypes = Collections.unmodifiableSet(links);
	}

	/**
	 * Constructs an <code>ApplyOverlayOp</code> with the specified variant span and
	 * link types
	 * 
	 * @param variantSpan
	 *            the region of text to apply an overlay on
	 * @param linkTypes
	 *            the overlay types such as styling and jump links. These are
	 *            indexes into the tumblerPool. This may be an empty set but not
	 *            null.
	 * @throws IllegalArgumentException
	 *             if variantSpan or linkTypes is null
	 * 
	 */
	public ApplyOverlayOp(VariantSpan variantSpan, Set<Integer> linkTypes) {
		super(Op.APPLY_OVERLAY);
		if (variantSpan == null) {
			throw new IllegalArgumentException("null variant span");
		}

		if (linkTypes == null) {
			throw new IllegalArgumentException("linkTypes is null");
		}

		this.variantSpan = variantSpan;
		this.linkTypes = Collections.unmodifiableSet(linkTypes);
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.APPLY_OVERLAY);
			dos.writeLong(variantSpan.start);
			dos.writeLong(variantSpan.width);
			dos.writeInt(linkTypes.size());
			for (Integer i : linkTypes) {
				if (i < 0) {
					throw new IOException("Tumbler pool index must be 0 or greater: " + i);
				}
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
		ApplyOverlayOp other = (ApplyOverlayOp) obj;
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

	@Override
	public String toString() {
		return "ApplyOverlayOp [linkTypeIndicies=" + linkTypes + ", variantSpan=" + variantSpan + "]";
	}

}
