package org.oulipo.streams.types;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;

/**
 * Link types will be used to style and to specify to the type of media
 */
public final class MediaOverlayElement extends OverlayElement {

	//TODO: override the addLinkType method to filter address that can't apply to the media stream
	
	private String hash;

	private TumblerAddress mediaAddress;

	public MediaOverlayElement(String hash, TumblerAddress mediaAddress, TumblerAddress homeDocument,
			TumblerAddress... linkTypes) throws MalformedSpanException {
		super(1, homeDocument, linkTypes);
		this.hash = hash;
		this.mediaAddress = mediaAddress;
	}

	@Override
	public MediaOverlayElement copy() throws MalformedSpanException {
		return new MediaOverlayElement(hash, mediaAddress, homeDocument);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MediaOverlayElement other = (MediaOverlayElement) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		return true;
	}

	public String getHash() {
		return hash;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		return result;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		return "MediaStreamElement [hash=" + hash + ", homeDocument=" + homeDocument + ", width=" + width + "]";
	}

}
