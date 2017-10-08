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

import org.oulipo.streams.Span;

public final class PutOp extends Op<PutOp.Data> {

	public static class Data {

		public final Span invariantSpan;

		public final long to;

		public Data(long to, Span invariantSpan) {
			this.to = to;
			this.invariantSpan = invariantSpan;
		}
	}

	public PutOp(Data data) {
		super(Op.PUT, data);
	}

	public PutOp(long to, Span invariantSpan) {
		this(new Data(to, invariantSpan));
	}

	@Override
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.PUT);
			dos.writeLong(getData().to);
			dos.writeLong(getData().invariantSpan.start);
			dos.writeLong(getData().invariantSpan.width);
		}
		os.flush();
		return os.toByteArray();
	}
}
