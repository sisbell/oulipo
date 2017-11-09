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
 * Swaps the text and media in one document region with the text and media of
 * another document region.
 */
public final class SwapVariantOp extends Op {

	public final VariantSpan v1;

	public final VariantSpan v2;

	/**
	 * 
	 * @param dis
	 * @throws MalformedSpanException
	 * @throws IOException
	 */
	public SwapVariantOp(DataInputStream dis) throws MalformedSpanException, IOException {
		this(new VariantSpan(dis), new VariantSpan(dis));
	}

	/**
	 * Creates a <code>SwapVariantOp</code> with the specified variant spans. This
	 * operation swaps v1 with position v2. The variantSpans can't intersect each
	 * other.
	 * 
	 * @param v1
	 *            the variant span to swap
	 * @param v2
	 *            the variant span to swap
	 */
	public SwapVariantOp(VariantSpan v1, VariantSpan v2) {
		super(Op.SWAP);
		if (v1 == null) {
			throw new IllegalArgumentException("variant span 1 is null");
		}

		if (v2 == null) {
			throw new IllegalArgumentException("variant span 2 is null");
		}

		this.v1 = v1;
		this.v2 = v2;
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.SWAP);
			dos.writeLong(v1.start);
			dos.writeLong(v1.width);
			dos.writeLong(v2.start);
			dos.writeLong(v2.width);
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
		SwapVariantOp other = (SwapVariantOp) obj;
		if (!v1.equals(other.v1))
			return false;
		if (!v2.equals(other.v2))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + v1.hashCode();
		result = prime * result + v2.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "SwapVariantOp [v1=" + v1 + ", v2=" + v2 + "]";
	}
}
