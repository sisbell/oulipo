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

public class PutInvariantSpanOpTest {

	@Test
	public void encodeDecode() throws Exception {
		PutInvariantSpanOp op = new PutInvariantSpanOp(100, 50, 1, 0);
		byte[] data = op.encode();
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

		assertEquals(Op.PUT_INVARIANT_SPAN, dis.readByte());

		PutInvariantSpanOp decoded = new PutInvariantSpanOp(dis);

		assertEquals(op, decoded);
	}

	@Test
	public void equalsFalse() throws Exception {
		PutInvariantSpanOp op1 = new PutInvariantSpanOp(100, 50, 1, 0);
		PutInvariantSpanOp op2 = new PutInvariantSpanOp(101, 50, 1, 0);
		assertFalse(op1.equals(op2));
		assertFalse(op2.equals(op1));
	}

	@Test
	public void equalsTrue() throws Exception {
		PutInvariantSpanOp op1 = new PutInvariantSpanOp(100, 50, 1, 0);
		PutInvariantSpanOp op2 = new PutInvariantSpanOp(100, 50, 1, 0);
		assertEquals(op1, op2);
		assertEquals(op2, op1);
	}

	@Test
	public void hashFalse() throws Exception {
		PutInvariantSpanOp op1 = new PutInvariantSpanOp(100, 50, 1, 0);
		PutInvariantSpanOp op2 = new PutInvariantSpanOp(101, 50, 1, 0);
		assertFalse(op1.hashCode() == op2.hashCode());
		;
	}

	@Test
	public void hashTrue() throws Exception {
		PutInvariantSpanOp op1 = new PutInvariantSpanOp(100, 50, 1, 0);
		PutInvariantSpanOp op2 = new PutInvariantSpanOp(100, 50, 1, 0);
		assertEquals(op1.hashCode(), op2.hashCode());
		;
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void homeDocumentOutOfBounds() throws Exception {
		new PutInvariantSpanOp(100, 50, 1, -1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void invariantStartOutOfBounds() throws Exception {
		new PutInvariantSpanOp(100, 0, 1, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void toOutOfBound() throws Exception {
		new PutInvariantSpanOp(0, 50, 1, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void widthOutOfBounds() throws Exception {
		new PutInvariantSpanOp(100, 50, 0, 0);
	}
}
