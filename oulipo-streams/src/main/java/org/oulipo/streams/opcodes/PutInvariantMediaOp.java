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

/**
 * Puts a media object in the document.
 */
public final class PutInvariantMediaOp extends Op {
	
	/**
	 * Index in media pool of this media's hash value
	 */
	public final int mediaPoolIndex;
	
	/**
	 * Index in tumbler pool of this media's tumbler address
	 */
	public final int mediaTumblerIndex;
	
	/**
	 * Variant position in document to put media object
	 */
	public final long to;
	
	public PutInvariantMediaOp(DataInputStream dis) throws IOException {
		this(dis.readLong(), dis.readInt(), dis.readInt());
	}

	/**
	 * 
	 * @param to the variant position 
	 * @param mediaPoolIndex the index of the hash
	 * @param mediaTumblerIndex the index of the media tumbler address
	 */
	public PutInvariantMediaOp(long to, int mediaPoolIndex, int mediaTumblerIndex) {
		super(Op.PUT_INVARIANT_MEDIA);
		this.to = to;
		this.mediaPoolIndex = mediaPoolIndex;
		this.mediaTumblerIndex = mediaTumblerIndex;
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.PUT_INVARIANT_MEDIA);
			dos.writeLong(to);
			dos.writeInt(mediaPoolIndex);
			dos.writeInt(mediaTumblerIndex);
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
		PutInvariantMediaOp other = (PutInvariantMediaOp) obj;
		if (mediaTumblerIndex != other.mediaTumblerIndex)
			return false;
		if (mediaPoolIndex != other.mediaPoolIndex)
			return false;
		if (to != other.to)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + mediaTumblerIndex;
		result = prime * result + mediaPoolIndex;
		result = prime * result + (int) (to ^ (to >>> 32));
		return result;
	}
}
