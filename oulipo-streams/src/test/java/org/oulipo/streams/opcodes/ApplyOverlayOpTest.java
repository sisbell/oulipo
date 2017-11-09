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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.junit.Test;
import org.oulipo.streams.VariantSpan;

import com.google.common.collect.Sets;

public class ApplyOverlayOpTest {

	@Test
	public void encode() throws Exception {
		ApplyOverlayOp op = new ApplyOverlayOp(new VariantSpan(1, 100), Sets.newHashSet(1, 10));
		byte[] data = op.encode();

		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		assertEquals(Op.APPLY_OVERLAY, dis.read());
		assertEquals(1, dis.readLong());
		assertEquals(100, dis.readLong());
		assertEquals(2, dis.readInt());
		assertEquals(1, dis.readInt());
		assertEquals(10, dis.readInt());
	}

	@Test
	public void encodeDecode() throws Exception {
		ApplyOverlayOp op = new ApplyOverlayOp(new VariantSpan(1, 100), Sets.newHashSet(1, 10));
		byte[] data = op.encode();
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

		assertEquals(Op.APPLY_OVERLAY, dis.readByte());

		ApplyOverlayOp decoded = new ApplyOverlayOp(dis);

		assertEquals(op, decoded);
	}

	@Test
	public void equalsLinkTypesFalse() throws Exception {
		ApplyOverlayOp op1 = new ApplyOverlayOp(new VariantSpan(1, 100), Sets.newHashSet(1));
		ApplyOverlayOp op2 = new ApplyOverlayOp(new VariantSpan(1, 100), Sets.newHashSet(1, 10));
		assertFalse(op1.equals(op2));
		assertFalse(op2.equals(op1));
	}

	@Test
	public void equalsTrue() throws Exception {
		ApplyOverlayOp op1 = new ApplyOverlayOp(new VariantSpan(1, 100), Sets.newHashSet(1, 10));
		ApplyOverlayOp op2 = new ApplyOverlayOp(new VariantSpan(1, 100), Sets.newHashSet(1, 10));
		assertTrue(op1.equals(op2));
		assertTrue(op2.equals(op1));
	}

	@Test
	public void equalsVariantsFalse() throws Exception {
		ApplyOverlayOp op1 = new ApplyOverlayOp(new VariantSpan(1, 100), Sets.newHashSet(1, 10));
		ApplyOverlayOp op2 = new ApplyOverlayOp(new VariantSpan(2, 100), Sets.newHashSet(1, 10));
		assertFalse(op1.equals(op2));
		assertFalse(op2.equals(op1));
	}

	@Test
	public void hashTrue() throws Exception {
		ApplyOverlayOp op1 = new ApplyOverlayOp(new VariantSpan(1, 100), Sets.newHashSet(1, 10));
		ApplyOverlayOp op2 = new ApplyOverlayOp(new VariantSpan(1, 100), Sets.newHashSet(1, 10));
		assertEquals(op1.hashCode(), op2.hashCode());
		;
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullLinkTypes() throws Exception {
		new ApplyOverlayOp(new VariantSpan(1, 100), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullVariantSpan() throws Exception {
		new ApplyOverlayOp(null, Sets.newHashSet(1, 10));
	}
}
