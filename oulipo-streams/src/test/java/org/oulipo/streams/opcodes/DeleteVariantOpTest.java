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
import org.oulipo.streams.VariantSpan;

public class DeleteVariantOpTest {

	@Test
	public void encode() throws Exception {
		DeleteVariantOp op = new DeleteVariantOp(new VariantSpan(1, 100));
		byte[] data = op.encode();
		
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		assertEquals(Op.DELETE, dis.read());
		assertEquals(1, dis.readLong());
		assertEquals(100, dis.readLong());
	}
}
