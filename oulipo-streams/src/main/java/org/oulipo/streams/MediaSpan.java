package org.oulipo.streams;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;

public class MediaSpan extends Span {

	public String hash;

	public MediaSpan(long start, String homeDocument, String hash) throws MalformedSpanException {
		super(start, 1, homeDocument);
	}

	public MediaSpan(long start, TumblerAddress homeDocument, String hash) throws MalformedSpanException {
		super(start, 1, homeDocument);
		this.hash = hash;
	}

	@Override
	public SpanPartition split(long cutPoint) throws MalformedSpanException {
		throw new UnsupportedOperationException("split method not supported for media");
	}

	@Override
	public Span copy() throws MalformedSpanException {
		return new MediaSpan(start, homeDocument, hash);
	}

	@Override
	public String toString() {
		return "MediaSpan [hash=" + hash + ", homeDocument=" + homeDocument + ", start=" + start + ", width=" + width
				+ "]";
	}
}
