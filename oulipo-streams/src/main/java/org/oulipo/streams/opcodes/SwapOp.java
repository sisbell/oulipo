/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.  
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
import java.io.DataOutputStream;
import java.io.IOException;

import org.oulipo.streams.VariantSpan;

public final class SwapOp extends Op<SwapOp.Data>{

	public SwapOp(Data data) {
		super(Op.SWAP, data);
	}
	
	public SwapOp(VariantSpan v1, VariantSpan v2) {
		this(new Data(v1, v2));
	}

	public static class Data {

		public final VariantSpan v1;
		
		public final VariantSpan v2;
		
		public Data(VariantSpan v1, VariantSpan v2) {
			this.v1 = v1;
			this.v2 = v2;
		}
	}

	@Override
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.SWAP);
			dos.writeLong(getData().v1.start);
			dos.writeLong(getData().v1.width);
			dos.writeLong(getData().v2.start);
			dos.writeLong(getData().v2.width);
		}
		os.flush();
		return os.toByteArray();
	}


}
