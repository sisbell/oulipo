package org.oulipo.streams.types;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;

public class MediaInvariantSpanElement extends SpanElement {

	public MediaInvariantSpanElement(long start, long width, TumblerAddress homeDocument)
			throws MalformedSpanException {
		super(start, width, homeDocument);
	}

}
