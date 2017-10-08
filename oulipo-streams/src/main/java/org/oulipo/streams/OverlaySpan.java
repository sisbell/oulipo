package org.oulipo.streams;

import java.util.ArrayList;
import java.util.List;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;

public final class OverlaySpan extends Span {

	private List<TumblerAddress> linkTypes = new ArrayList<>();

	public OverlaySpan() {
		super();
	}

	public OverlaySpan(long start, long width, String homeDocument, List<TumblerAddress> linkTypes) throws MalformedSpanException {
		super(start, width, homeDocument);
		this.linkTypes = linkTypes;
	}

	public OverlaySpan(long start, long width) throws MalformedSpanException {
		super(start, width);
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
	public Span copy() throws MalformedSpanException {
		return new OverlaySpan(start, width, homeDocument, linkTypes);
	}
}
