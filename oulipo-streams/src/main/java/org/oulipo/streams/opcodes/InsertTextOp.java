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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class InsertTextOp extends Op<InsertTextOp.Data> {

	public static class Data {

		public final String text;

		public final long to;

		public Data(long to, String text) {
			this.to = to;
			this.text = text;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Data other = (Data) obj;
			if (text == null) {
				if (other.text != null)
					return false;
			} else if (!text.equals(other.text))
				return false;
			if (to != other.to)
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((text == null) ? 0 : text.hashCode());
			result = prime * result + (int) (to ^ (to >>> 32));
			return result;
		}

		@Override
		public String toString() {
			return "Data [text=" + text + ", to=" + to + "]";
		}
	}

	public static InsertTextOp read(byte[] message) throws IOException {
		return read(new DataInputStream(new ByteArrayInputStream(message)));
	}

	public static InsertTextOp read(DataInputStream dis) throws IOException {
		long to = dis.readLong();
		return new InsertTextOp(new Data(to, dis.readUTF()));
	}

	public InsertTextOp(Data data) {
		super(Op.INSERT_TEXT, data);
	}

	public InsertTextOp(long to, String text) {
		this(new Data(to, text));
	}

	@Override
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.INSERT_TEXT);
			dos.writeLong(getData().to);
			dos.writeUTF(getData().text);
		}
		os.flush();
		return os.toByteArray();

	}
}
