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

public class SwapVariantOpTest {

	@Test
	public void encodeDecode() throws Exception {
		SwapVariantOp op = new SwapVariantOp(new VariantSpan(100, 1), new VariantSpan(200, 1));
		byte[] data = op.encode();
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

		assertEquals(Op.SWAP, dis.readByte());

		SwapVariantOp decoded = new SwapVariantOp(dis);

		assertEquals(op, decoded);
	}

	@Test
	public void equalsFalse() throws Exception {
		SwapVariantOp op1 = new SwapVariantOp(new VariantSpan(100, 1), new VariantSpan(200, 1));
		SwapVariantOp op2 = new SwapVariantOp(new VariantSpan(100, 10), new VariantSpan(200, 1));
		assertFalse(op1.equals(op2));
		assertFalse(op2.equals(op1));
	}

	@Test
	public void equalsTrue() throws Exception {
		SwapVariantOp op1 = new SwapVariantOp(new VariantSpan(100, 1), new VariantSpan(200, 1));
		SwapVariantOp op2 = new SwapVariantOp(new VariantSpan(100, 1), new VariantSpan(200, 1));
		assertEquals(op1, op2);
		assertEquals(op2, op1);
	}

	@Test
	public void hashFalse() throws Exception {
		SwapVariantOp op1 = new SwapVariantOp(new VariantSpan(100, 10), new VariantSpan(200, 1));
		SwapVariantOp op2 = new SwapVariantOp(new VariantSpan(100, 1), new VariantSpan(200, 1));
		assertFalse(op1.hashCode() == op2.hashCode());
		;
	}

	@Test
	public void hashTrue() throws Exception {
		SwapVariantOp op1 = new SwapVariantOp(new VariantSpan(100, 1), new VariantSpan(200, 1));
		SwapVariantOp op2 = new SwapVariantOp(new VariantSpan(100, 1), new VariantSpan(200, 1));
		assertEquals(op1.hashCode(), op2.hashCode());
		;
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullSpan1() throws Exception {
		new SwapVariantOp(null, new VariantSpan(200, 1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullSpan2() throws Exception {
		new SwapVariantOp(new VariantSpan(200, 1), null);
	}
}
