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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class InsertTextOp extends Op {

	public final String text;

	public final long to;

	public InsertTextOp(DataInputStream dis) throws IOException {
		this(dis.readLong(), dis.readUTF());
	}

	/**
	 * Constructs an <code>InsertTextOp</code>
	 * 
	 * @param to variant insertion point
	 * @param text the text to insert
	 */
	public InsertTextOp(long to, String text) {
		super(Op.INSERT_TEXT);
		this.to = to;
		this.text = text;
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.INSERT_TEXT);
			dos.writeLong(to);
			dos.writeUTF(text);//TODO: needs reference to textArea
		}
		os.flush();
		return os.toByteArray();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		InsertTextOp other = (InsertTextOp) obj;
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
		int result = super.hashCode();
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + (int) (to ^ (to >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "InsertTextOp [text=" + text + ", to=" + to + "]";
	}
	
}
