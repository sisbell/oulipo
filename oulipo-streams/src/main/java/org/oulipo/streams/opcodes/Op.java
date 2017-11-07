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

import java.io.IOException;

/**
 * Base operations class
 */
public abstract class Op {

	public static final byte APPLY_OVERLAY = 0x10;

	/**
	 * Copies text and media of a region to another region
	 */
	public static final byte COPY = 0x2;

	/**
	 * Operation for deleting text and media
	 */
	public static final byte DELETE = 0x3;

	public static final byte INSERT_TEXT = 0x5;

	/**
	 * Operation for moving text and media from one document region to another region
	 */
	public static final byte MOVE = 0x7;
	
	/**
	 * Operation for putting a media object into a stream
	 */
	public static final byte PUT_INVARIANT_MEDIA = 0x6;

	/**
	 * Operation for putting an invariant span of text into a stream
	 */
	public static final byte PUT_INVARIANT_SPAN = 0x1;
	
	/**
	 * Operation for putting an overlay onto a region of a stream
	 */
	public static final byte PUT_OVERLAY = 0x12;
	
	/**
	 * Operation for putting an overlay on a media object
	 */
	public static final byte PUT_OVERLAY_MEDIA = 0x13;
	
	/**
	 * Operation for swapping text and media
	 */
	public static final byte SWAP = 0x0;

	public static final byte TOGGLE_OVERLAY = 0x11;

	/**
	 * Operation code or type
	 */
	private final byte code;

	/**
	 * Constructs an Op with specific type
	 * 
	 * @param code the op type
	 */
	public Op(byte code) {
		this.code = code;
	}

	/**
	 * Encodes the operation as byte array
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract byte[] encode() throws IOException;
	
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
		return true;
	}

	/**
	 * Gets op type
	 * 
	 * @return op type
	 */
	public int getCode() {
		return code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		return result;
	}

	@Override
	public String toString() {
		return "Op [code=" + code +  "]";
	}
}
