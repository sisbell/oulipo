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

import org.oulipo.streams.types.SpanElement;
import org.oulipo.streams.types.StreamElement;

public final class PutOp<T extends StreamElement> extends Op<PutOp.Data<T>> {

	public static class Data<T> {

		public final T streamElement;

		public final long to;

		public Data(long to, T streamElement) {
			this.to = to;
			this.streamElement = streamElement;
		}
	}

	public PutOp(Data<T> data) {
		super(Op.PUT, data);
	}

	public PutOp(long to, T span) {
		this(new Data<T>(to, span));
	}

	@Override
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.PUT);
			dos.writeLong(getData().to);
			if(getData().streamElement instanceof SpanElement) {
				SpanElement invariant = (SpanElement) getData().streamElement;
				dos.writeLong(invariant.getStart());
			} else {
				dos.writeLong(-1);
			}
			dos.writeLong(getData().streamElement.getWidth());
		}
		os.flush();
		return os.toByteArray();
	}
}
