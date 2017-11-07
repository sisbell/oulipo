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

import org.oulipo.net.MalformedSpanException;
import org.oulipo.streams.VariantSpan;

/**
 * Movies a region of text or media from one location to another location.
 */
public final class MoveVariantOp extends Op {

	/**
	 * Variant position in document to put specified variant span
	 */
	public final long to;

	public final VariantSpan variantSpan;

	public MoveVariantOp(DataInputStream dis) throws IOException, MalformedSpanException {
		this(dis.readLong(), new VariantSpan(dis));
	}
	
	/**
	 * Moves the variant span to the specified 'to' position
	 * 
	 * @param to the position to move the text to
	 * @param variantSpan the region of text to move
	 */
	public MoveVariantOp(long to, VariantSpan variantSpan) {
		super(Op.MOVE);
		this.to = to;
		this.variantSpan = variantSpan;
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.MOVE);
			dos.writeLong(to);
			dos.writeLong(variantSpan.start);
			dos.writeLong(variantSpan.width);
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
		MoveVariantOp other = (MoveVariantOp) obj;
		if (to != other.to)
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
		result = prime * result + (int) (to ^ (to >>> 32));
		result = prime * result + ((variantSpan == null) ? 0 : variantSpan.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "MoveVariantOp [to=" + to + ", variantSpan=" + variantSpan + "]";
	}
}
