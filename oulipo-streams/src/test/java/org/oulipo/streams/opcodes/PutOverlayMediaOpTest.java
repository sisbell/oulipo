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
import org.mockito.internal.util.collections.Sets;

public class PutOverlayMediaOpTest {

	@Test
	public void encodeDecode() throws Exception {
		// long to, int hash, int mediaAddress, Set<Integer> linkTypes
		PutOverlayMediaOp op = new PutOverlayMediaOp(1, 1, Sets.newSet(10, 5));
		byte[] data = op.encode();
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

		assertEquals(Op.PUT_OVERLAY_MEDIA, dis.readByte());

		PutOverlayMediaOp decoded = new PutOverlayMediaOp(dis);
		assertEquals(op, decoded);
	}

	@Test
	public void equalsFalse() throws Exception {
		PutOverlayMediaOp op1 = new PutOverlayMediaOp(1, 1, Sets.newSet(10, 5));
		PutOverlayMediaOp op2 = new PutOverlayMediaOp(2, 1, Sets.newSet(10, 5));
		assertFalse(op1.equals(op2));
		assertFalse(op2.equals(op1));
	}

	@Test
	public void equalsTrue() throws Exception {
		PutOverlayMediaOp op1 = new PutOverlayMediaOp(1, 1, Sets.newSet(10, 5));
		PutOverlayMediaOp op2 = new PutOverlayMediaOp(1, 1, Sets.newSet(10, 5));
		assertEquals(op1, op2);
		assertEquals(op2, op1);
	}

	@Test
	public void hashFalse() throws Exception {
		PutOverlayMediaOp op1 = new PutOverlayMediaOp(1, 1, Sets.newSet(10, 5));
		PutOverlayMediaOp op2 = new PutOverlayMediaOp(2, 1, Sets.newSet(10, 5));
		assertFalse(op1.hashCode() == op2.hashCode());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void hashOutOfBounds() throws Exception {
		new PutOverlayMediaOp(1, -1, Sets.newSet(10, 5));
	}

	@Test
	public void hashTrue() throws Exception {
		PutOverlayMediaOp op1 = new PutOverlayMediaOp(1, 0, Sets.newSet(10, 5));
		PutOverlayMediaOp op2 = new PutOverlayMediaOp(1, 0, Sets.newSet(10, 5));
		assertEquals(op1.hashCode(), op2.hashCode());
		;
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullLinks() throws Exception {
		new PutOverlayMediaOp(1, 1, null);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void positionOutOfBounds() throws Exception {
		new PutOverlayMediaOp(0, 1, Sets.newSet(10, 5));
	}

}
