package org.oulipo.streams;

import java.util.HashSet;
import java.util.Set;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;

public final class OverlaySpan extends Span {

	public Set<TumblerAddress> linkTypes = new HashSet<>();

	public OverlaySpan() {
		super();
	}

	public OverlaySpan(long start, long width, String homeDocument) throws MalformedSpanException {
		super(start, width, homeDocument);
	}

	public OverlaySpan(long start, long width, String homeDocument, Set<TumblerAddress> linkTypes)
			throws MalformedSpanException {
		super(start, width, homeDocument);
		this.linkTypes = linkTypes;
	}

	public OverlaySpan(long start, long width, TumblerAddress homeDocument) throws MalformedSpanException {
		super(start, width, homeDocument);
	}

	public OverlaySpan(long start, long width, TumblerAddress homeDocument, Set<TumblerAddress> linkTypes)
			throws MalformedSpanException {
		super(start, width, homeDocument);
		this.linkTypes = linkTypes;
	}

	@Override
	public Span copy() throws MalformedSpanException {
		return new OverlaySpan(start, width, homeDocument, new HashSet<>(linkTypes));
	}

	public boolean hasLinkType(TumblerAddress link) {
		return linkTypes.contains(link);
	}

	@Override
	public SpanPartition split(long cutPoint) throws MalformedSpanException {
		if (start + width <= cutPoint) {
			throw new IndexOutOfBoundsException("Cut point too high. Can't split this span: cutPoint = " + cutPoint
					+ ", max allowed = " + (start + width - 1));
		}

		if (cutPoint <= start) {
			throw new IndexOutOfBoundsException("Cut point too low. Can't split this span: cutPoint = " + cutPoint
					+ ", min allowed = " + (start + 1));
		}
		return new SpanPartition(new OverlaySpan(start, cutPoint - start, homeDocument, linkTypes),
				new OverlaySpan(cutPoint, start + width - cutPoint, homeDocument, linkTypes));
	}

	@Override
	public String toString() {
		return "OverlaySpan [linkTypes=" + linkTypes + ", homeDocument=" + homeDocument + ", start=" + start
				+ ", width=" + width + "]";
	}

}
