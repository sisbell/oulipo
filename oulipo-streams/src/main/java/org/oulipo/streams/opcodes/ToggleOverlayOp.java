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

import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.VariantSpan;

/**
 * Toggles one overlay of a region by flipping the overlay on/off.
 */
public final class ToggleOverlayOp extends Op {

	/**
	 * Index in tumbler pool of this overlay's type
	 */
	public final int linkTypeIndex;

	/**
	 * The variant span of the region to toggle
	 */
	public final VariantSpan variantSpan;

	/**
	 * 
	 * @param dis
	 * @throws MalformedSpanException
	 * @throws IOException
	 */
	public ToggleOverlayOp(DataInputStream dis) throws MalformedSpanException, IOException {
		this(new VariantSpan(dis), dis.readInt());
	}

	/**
	 * Create operation for toggling an overlay
	 * 
	 * @param variantSpan
	 * @param linkTypeIndex
	 *            the index of the linkType
	 */
	public ToggleOverlayOp(VariantSpan variantSpan, int linkTypeIndex) {
		super(Op.TOGGLE_OVERLAY);
		if (variantSpan == null) {
			throw new IllegalArgumentException("null variant span");
		}
		if (linkTypeIndex < 0) {
			throw new IndexOutOfBoundsException("linkTypeIndex must be non-negative: " + linkTypeIndex);
		}
		this.variantSpan = variantSpan;
		this.linkTypeIndex = linkTypeIndex;
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.TOGGLE_OVERLAY);
			dos.writeLong(variantSpan.start);
			dos.writeLong(variantSpan.width);
			dos.writeInt(linkTypeIndex);
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
		ToggleOverlayOp other = (ToggleOverlayOp) obj;
		if (linkTypeIndex != other.linkTypeIndex)
			return false;
		if (variantSpan == null) {
			if (other.variantSpan != null)
				return false;
		} else if (!variantSpan.equals(other.variantSpan))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + linkTypeIndex;
		result = prime * result + ((variantSpan == null) ? 0 : variantSpan.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "ToggleOverlayOp [variantSpan=" + variantSpan + ", tumblerPoolIndex=" + linkTypeIndex + "]";
	}

}
