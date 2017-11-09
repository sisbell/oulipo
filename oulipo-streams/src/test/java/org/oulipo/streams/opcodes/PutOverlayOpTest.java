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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.oulipo.streams.VariantSpan;

public class PutOverlayOpTest {

	@Test
	public void encodeDecode() throws Exception {
		PutOverlayOp op = new PutOverlayOp(new VariantSpan(100, 1), Sets.newSet(10, 5));
		byte[] data = op.encode();
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

		assertEquals(Op.PUT_OVERLAY, dis.readByte());

		PutOverlayOp decoded = new PutOverlayOp(dis);

		assertEquals(op, decoded);
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullLinks() throws Exception {
		new PutOverlayOp(new VariantSpan(200, 1), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullSpan() throws Exception {
		new PutOverlayOp(null, Sets.newSet(10, 5));
	}

}
