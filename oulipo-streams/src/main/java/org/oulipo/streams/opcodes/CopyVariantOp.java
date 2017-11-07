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
 * Copies a region of text or media to a new location.
 */
public final class CopyVariantOp extends Op {

	public final long to;

	/**
	 * The variant span within the document to copy
	 */
	public final VariantSpan variantSpan;

	public CopyVariantOp(DataInputStream dis) throws IOException, MalformedSpanException {
		this(dis.readLong(), new VariantSpan(dis));
	}
	
	public CopyVariantOp(long to, VariantSpan variantSpan) {
		super(Op.COPY);
		this.to = to;
		this.variantSpan = variantSpan;
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.COPY);
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
		CopyVariantOp other = (CopyVariantOp) obj;
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
		return "CopyVariantOp [to=" + to + ", variantSpan=" + variantSpan + "]";
	}
}
