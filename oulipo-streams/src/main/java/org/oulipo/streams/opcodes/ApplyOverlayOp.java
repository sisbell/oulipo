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
import org.oulipo.streams.VariantSpan;

/**
 * Applies an overlay over a region of text or media.
 * 
 */
public final class ApplyOverlayOp extends Op {

	public final Set<Integer> linkTypeIndicies;

	public final VariantSpan variantSpan;

	public ApplyOverlayOp(DataInputStream dis) throws MalformedSpanException, IOException {
		super(Op.APPLY_OVERLAY);
		if(dis == null) {
			
		}
		variantSpan = new VariantSpan(dis);
		int length = dis.readInt();
		Set<Integer> links = new HashSet<>(length);

		for(int i = 0; i < length; i++) {
			int index = dis.readInt();
			if (index < 0) {
				throw new IOException("Tumbler pool index must be 0 or greater: " + i);
			}
			links.add(index);	
		}		
		linkTypeIndicies = Collections.unmodifiableSet(links);
	}
	
	public ApplyOverlayOp(VariantSpan variantSpan, Set<Integer> linkTypeIndicies) {
		super(Op.APPLY_OVERLAY);
		this.variantSpan = variantSpan;
		this.linkTypeIndicies = Collections.unmodifiableSet(linkTypeIndicies);
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.APPLY_OVERLAY);
			dos.writeLong(variantSpan.start);
			dos.writeLong(variantSpan.width);
			dos.writeInt(linkTypeIndicies.size());
			for (Integer i : linkTypeIndicies) {
				if (i < 0) {
					throw new IOException("Tumbler pool index must be 0 or greater: " + i);
				}
				dos.writeInt(i);
			}
		}

		os.flush();
		return os.toByteArray();
	}

}
