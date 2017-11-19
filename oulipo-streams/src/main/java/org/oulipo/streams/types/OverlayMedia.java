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

import java.util.HashSet;
import java.util.Set;

import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.StreamElementPartition;
import org.oulipo.streams.overlays.Overlay;

/**
 * Link types will be used to style and to specify to the type of media
 */
public final class OverlayMedia extends OverlayStream {

	// TODO: override the addLinkType method to filter address that can't apply to
	// the media stream

	public final String hash;

	public OverlayMedia(String hash, Overlay... linkTypes) throws MalformedSpanException {
		super(1, linkTypes);
		this.hash = hash;
	}

	/**
	 * Constructs an <code>OverlayMedia</code>
	 * 
	 * @param linkTypes
	 *            the link types for styling and tagging this media
	 * @throws MalformedSpanException
	 */
	public OverlayMedia(String hash, Set<Overlay> linkTypes) throws MalformedSpanException {
		super(1, linkTypes);
		this.hash = hash;
	}

	@Override
	public OverlayMedia copy() throws MalformedSpanException {
		return new OverlayMedia(hash, new HashSet<Overlay>(linkTypes));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		OverlayMedia other = (OverlayMedia) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		return result;
	}

	@Override
	public StreamElementPartition<OverlayStream> split(long cutPoint) throws MalformedSpanException {
		throw new UnsupportedOperationException("Cannot split a media overlay");
	}

}
