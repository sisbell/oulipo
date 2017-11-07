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

public class InsertTextOpTest {

	@Test
	public void readWrite() throws Exception {
		InsertTextOp op = new InsertTextOp(5, "Hello Xanadu");
		byte[] b = op.encode();
		byte[] c = new byte[b.length - 1];
		System.arraycopy(b, 1, c, 0, b.length - 1);
		InsertTextOp result = new InsertTextOp(new DataInputStream(new ByteArrayInputStream(c)));

		assertEquals("Hello Xanadu", result.text);

	}
}
