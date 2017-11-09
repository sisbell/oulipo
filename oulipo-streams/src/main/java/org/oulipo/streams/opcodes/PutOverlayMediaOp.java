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

	/**
	 * Index of the hash for this media
	 */
	public final int hash;

	/**
	 * The overlay types such as styling and jump links. These are indexes into the
	 * tumblerPool.
	 */
	public final Set<Integer> linkTypes;

	/**
	 * Index in tumbler pool of this media's tumbler address
	 */
	public final int mediaTumblerIndex;

	/**
	 * Variant position in document to put the media overlay
	 */
	public final long to;

	/**
	 * 
	 * @param dis
	 * @throws MalformedSpanException
	 * @throws IOException
	 */
	public PutOverlayMediaOp(DataInputStream dis) throws MalformedSpanException, IOException {
		super(Op.PUT_OVERLAY_MEDIA);
		to = dis.readLong();
		hash = dis.readInt();
		mediaTumblerIndex = dis.readInt();

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

	/**
	 * 
	 * @param to
	 *            variant position in document to put the media overlay
	 * @param hash
	 * @param mediaTumblerIndex
	 * @param linkTypes
	 */
	public PutOverlayMediaOp(long to, int hash, int mediaTumblerIndex, Set<Integer> linkTypes) {
		super(Op.PUT_OVERLAY_MEDIA);
		if (to < 1) {
			throw new IndexOutOfBoundsException("to position must be greater than 0");
		}

		if (hash < 0) {
			throw new IndexOutOfBoundsException("hash index must be non-negative");
		}

		if (mediaTumblerIndex < 0) {
			throw new IndexOutOfBoundsException("mediaTumblerIndex position must be non-negative");
		}

		if (linkTypes == null) {
			throw new IllegalArgumentException("linkTypes is null");
		}

		this.to = to;
		this.hash = hash;
		this.mediaTumblerIndex = mediaTumblerIndex;
		this.linkTypes = Collections.unmodifiableSet(linkTypes);
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.PUT_OVERLAY_MEDIA);
			dos.writeLong(to);
			dos.writeInt(hash);
			dos.writeInt(mediaTumblerIndex);
			dos.writeInt(linkTypes.size());
			for (Integer i : linkTypes) {
				dos.writeInt(i);
			}
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
		PutOverlayMediaOp other = (PutOverlayMediaOp) obj;
		if (hash != other.hash)
			return false;
		if (!linkTypes.equals(other.linkTypes))
			return false;
		if (mediaTumblerIndex != other.mediaTumblerIndex)
			return false;
		if (to != other.to)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + hash;
		result = prime * result + ((linkTypes == null) ? 0 : linkTypes.hashCode());
		result = prime * result + mediaTumblerIndex;
		result = prime * result + (int) (to ^ (to >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "PutOverlayMediaOp [hash=" + hash + ", linkTypes=" + linkTypes + ", mediaAddress=" + mediaTumblerIndex
				+ ", to=" + to + "]";
	}
}
