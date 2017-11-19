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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.StreamElementPartition;
import org.oulipo.streams.overlays.Overlay;

public class OverlayStream implements StreamElement {

	public Set<Overlay> linkTypes;

	public String tag;

	protected long width;

	protected OverlayStream() {
	}

	/**
	 * Constructs an overlay with unspecified link types
	 * 
	 * @param width
	 *            the width of the overlay
	 * @param homeDocument
	 *            the home document of this overlay
	 * @throws MalformedSpanException
	 */
	public OverlayStream(long width) throws MalformedSpanException {
		this.width = width;
		this.linkTypes = new HashSet<>();
	}

	public OverlayStream(long width, Overlay... linkTypes) throws MalformedSpanException {
		this.linkTypes = new HashSet<Overlay>(Arrays.asList(linkTypes));
	}

	/**
	 * Constructs an overlay with the specified link types
	 * 
	 * @param width
	 *            the width of the overlay
	 * @param homeDocument
	 * @param linkTypes
	 * @throws MalformedSpanException
	 */
	public OverlayStream(long width, Set<Overlay> linkTypes) throws MalformedSpanException {
		this.width = width;
		this.linkTypes = linkTypes;
	}

	public OverlayStream(long width, String tag) throws MalformedSpanException {
		this.width = width;
		this.tag = tag;
		this.linkTypes = new HashSet<>();
	}

	public void addLinkType(Overlay linkType) {
		linkTypes.add(linkType);
	}

	public void addLinkTypes(Collection<Overlay> linkTypes) {
		this.linkTypes.addAll(linkTypes);
	}

	@Override
	public OverlayStream copy() throws MalformedSpanException {
		return new OverlayStream(width, new HashSet<>(linkTypes));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OverlayStream other = (OverlayStream) obj;
		if (linkTypes == null) {
			if (other.linkTypes != null)
				return false;
		} else if (!linkTypes.equals(other.linkTypes))
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	@Override
	public long getWidth() {
		return width;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((linkTypes == null) ? 0 : linkTypes.hashCode());
		result = prime * result + (int) (width ^ (width >>> 32));
		return result;
	}

	public boolean hasLinks() {
		return !linkTypes.isEmpty();
	}

	public boolean hasLinkType(Overlay link) {
		return linkTypes.contains(link);
	}

	public int linkCount() {
		return linkTypes.size();
	}

	public void removeLinkType(Overlay linkType) {
		linkTypes.remove(linkType);
	}

	@Override
	public void setWidth(long width) {
		this.width = width;
	}

	@Override
	public StreamElementPartition<OverlayStream> split(long cutPoint) throws MalformedSpanException {
		long w = width - cutPoint;
		return new StreamElementPartition<OverlayStream>(new OverlayStream(cutPoint, new HashSet<>(linkTypes)),
				new OverlayStream(w, new HashSet<>(linkTypes)));
	}

	@Override
	public String toString() {
		return "Overlay [linkTypes=" + linkTypes + ", width=" + width + "]";
	}

}
