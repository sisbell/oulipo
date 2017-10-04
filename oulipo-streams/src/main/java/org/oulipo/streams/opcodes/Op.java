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

import java.io.IOException;

public abstract class Op<Data> {

	public static final byte COPY = 0x2;

	public static final byte DELETE = 0x3;

	public static final byte INSERT_TEXT = 0x4;

	public static final byte MOVE = 0x5;

	public static final byte PUT = 0x1;

	public static final byte SWAP = 0x0;

	private final int code;

	private final Data data;

	public Op(int code, Data data) {
		this.data = data;
		this.code = code;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Op other = (Op) obj;
		if (code != other.code)
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	public int getCode() {
		return code;
	}

	public Data getData() {
		return data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	public abstract byte[] toBytes() throws IOException;

	@Override
	public String toString() {
		return "Op [code=" + code + ", data=" + data + "]";
	}

}
