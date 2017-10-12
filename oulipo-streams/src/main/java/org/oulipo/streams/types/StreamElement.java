package org.oulipo.streams.types;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;

import com.google.common.base.Strings;

public abstract class StreamElement {

	TumblerAddress homeDocument;

	/**
	 * Number of characters in the element
	 */
	protected long width;
	
	protected StreamElement() { }

	public StreamElement(long width, String homeDocument) throws MalformedSpanException, MalformedTumblerException {
		this(width, Strings.isNullOrEmpty(homeDocument) ? null : TumblerAddress.create(homeDocument));
	}

	public StreamElement(long width, TumblerAddress homeDocument) throws MalformedSpanException {
		if (width < 1) {
			throw new MalformedSpanException("Width must be greater than 0");
		}
		this.width = width;
		this.homeDocument = homeDocument;
	}

	public abstract <T extends StreamElement> T copy() throws MalformedSpanException;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StreamElement other = (StreamElement) obj;
		if (homeDocument == null) {
			if (other.homeDocument != null)
				return false;
		} else if (!homeDocument.equals(other.homeDocument))
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	public TumblerAddress getHomeDocument() {
		return homeDocument;
	}

	public long getWidth() {
		return width;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((homeDocument == null) ? 0 : homeDocument.hashCode());
		result = prime * result + (int) (width ^ (width >>> 32));
		return result;
	}

	public void setHomeDocument(TumblerAddress homeDocument) {
		this.homeDocument = homeDocument;
	}

	public void setWidth(long width) {
		this.width = width;
	}

	public <T extends StreamElement> StreamElementPartition<T> split(long cutPoint) throws MalformedSpanException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return "StreamElement [width=" + width + ", homeDocument=" + homeDocument + "]";
	}
}
