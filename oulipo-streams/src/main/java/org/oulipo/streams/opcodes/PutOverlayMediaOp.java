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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.oulipo.net.MalformedSpanException;

/**
 * Overlays a media object with styling and links.
 */
public final class PutOverlayMediaOp extends Op {
	
	public final int hash;
	
	public final Set<Integer> linkTypes;
	
	public final int mediaAddress;

	public final long to;

	public PutOverlayMediaOp(DataInputStream dis) throws MalformedSpanException, IOException {
		super(Op.PUT_OVERLAY_MEDIA);
		to = dis.readLong();
		hash = dis.readInt();
		mediaAddress = dis.readInt();
		
		int length = dis.readInt();
		Set<Integer> links = new HashSet<>(length);

		for (int i = 0; i < length; i++) {
			int index = dis.readInt();
			if (index < 0) {
				throw new IOException("Tumbler pool index must be 0 or greater: " + i);
			}
			links.add(index);
		}
		linkTypes = Collections.unmodifiableSet(links);
	}
	
	public PutOverlayMediaOp(long to, int hash, int mediaAddress, Set<Integer> linkTypes) {
		super(Op.PUT_OVERLAY_MEDIA);
		this.to = to;
		this.hash = hash;
		this.mediaAddress = mediaAddress;
		this.linkTypes = Collections.unmodifiableSet(linkTypes);
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.PUT_OVERLAY_MEDIA);
			dos.writeLong(to);
			dos.writeInt(hash);
			dos.writeInt(mediaAddress);
			dos.writeInt(linkTypes.size());
			for (Integer i : linkTypes) {
				dos.writeInt(i);
			}
		}
		os.flush();
		return os.toByteArray();
	}

}
