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
package org.oulipo.streams.types;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.StreamElementPartition;

import com.google.common.base.Strings;

public final class InvariantMedia implements Invariant {

	public String hash;

	public TumblerAddress mediaAddress;

	public InvariantMedia() {
		super();
	}

	/**
	 * Constructs an <code>InvariantMedia</code>. Unlike InvariantSpan, the
	 * homeDocument is the complete tumbler address of the media (extending the
	 * homeDocument).
	 * 
	 * @param mediaAddress
	 *            the complete tumbler address of the media
	 * @throws MalformedSpanException
	 * @throws MalformedTumblerException
	 */
	public InvariantMedia(String hash, String mediaAddress) throws MalformedSpanException, MalformedTumblerException {
		this(hash, TumblerAddress.create(mediaAddress));
	}

	public InvariantMedia(String hash, TumblerAddress mediaAddress) throws MalformedSpanException {
		if(Strings.isNullOrEmpty(hash)) {
			throw new IllegalArgumentException("hash is empty");
		}
		if(mediaAddress == null) {
			throw new IllegalArgumentException("media address is null");
		}
		this.hash = hash;
		this.mediaAddress = mediaAddress;
	}

	@Override
	public StreamElement copy() throws MalformedSpanException {
		return new InvariantMedia(hash, mediaAddress);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InvariantMedia other = (InvariantMedia) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		if (mediaAddress == null) {
			if (other.mediaAddress != null)
				return false;
		} else if (!mediaAddress.equals(other.mediaAddress))
			return false;
		return true;
	}

	@Override
	public long getWidth() {
		return 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result + ((mediaAddress == null) ? 0 : mediaAddress.hashCode());
		return result;
	}

	@Override
	public void setWidth(long width) {

	}

	@Override
	public StreamElementPartition<InvariantMedia> split(long cutPoint) throws MalformedSpanException {
		throw new UnsupportedOperationException("Cannot split an invariant media");
	}

	@Override
	public String toString() {
		return "InvariantMedia [hash=" + hash + ", mediaAddress=" + mediaAddress + "]";
	}

}
