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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.junit.Test;
import org.oulipo.streams.VariantSpan;

public class CopyVariantOpTest {

	@Test
	public void encodeDecode() throws Exception {
		CopyVariantOp op = new CopyVariantOp(100, new VariantSpan(50, 75));
		byte[] data = op.encode();
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

		assertEquals(Op.COPY, dis.readByte());

		CopyVariantOp decoded = new CopyVariantOp(dis);

		assertEquals(op, decoded);
	}

	@Test
	public void equalsPositionsFalse() throws Exception {
		CopyVariantOp op1 = new CopyVariantOp(1, new VariantSpan(1, 100));
		CopyVariantOp op2 = new CopyVariantOp(2, new VariantSpan(1, 100));
		assertFalse(op1.equals(op2));
		assertFalse(op2.equals(op1));
	}

	@Test
	public void equalsTrue() throws Exception {
		CopyVariantOp op1 = new CopyVariantOp(1, new VariantSpan(1, 100));
		CopyVariantOp op2 = new CopyVariantOp(1, new VariantSpan(1, 100));
		assertEquals(op1, op2);
		assertEquals(op2, op1);
	}

	@Test
	public void equalsVariantsFalse() throws Exception {
		CopyVariantOp op1 = new CopyVariantOp(1, new VariantSpan(1, 100));
		CopyVariantOp op2 = new CopyVariantOp(1, new VariantSpan(2, 100));
		assertFalse(op1.equals(op2));
		assertFalse(op2.equals(op1));
	}

	@Test
	public void hashTrue() throws Exception {
		CopyVariantOp op1 = new CopyVariantOp(1, new VariantSpan(1, 100));
		CopyVariantOp op2 = new CopyVariantOp(1, new VariantSpan(1, 100));
		assertEquals(op1.hashCode(), op2.hashCode());
		;
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void negativePosition() throws Exception {
		new CopyVariantOp(-1, new VariantSpan(50, 75));
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullVariantSpan() throws Exception {
		new CopyVariantOp(1, null);
	}
}
