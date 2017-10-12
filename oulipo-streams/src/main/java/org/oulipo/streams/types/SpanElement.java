package org.oulipo.streams.types;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;

public class SpanElement extends StreamElement {

	/**
	 * Start byte position
	 */
	protected long start;
	
	protected SpanElement() { }
	
	public SpanElement(long start, long width, String homeDocument)
			throws MalformedSpanException, MalformedTumblerException {
		super(width, homeDocument);
		if (width < 1) {
			throw new MalformedSpanException("Width must be greater than 0");
		}
		this.start = start;
	}

	public SpanElement(long start, long width, TumblerAddress homeDocument) throws MalformedSpanException {
		super(width, homeDocument);
		if (start < 1) {
			throw new MalformedSpanException("Start must be greater than 0");
		}
	
		this.start = start;
	}

	@Override
	public SpanElement copy() throws MalformedSpanException {
		return new SpanElement(start, width, homeDocument);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpanElement other = (SpanElement) obj;
		if (start != other.start)
			return false;
		return true;
	}

	public long getStart() {
		return start;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (start ^ (start >>> 32));
		return result;
	}

	public void setStart(long start) {
		this.start = start;
	}

	/**
	 * Splits this invariant span into two parts at the specified position. The cut
	 * point is relative, or to the right of, this spans start value.
	 * 
	 * @param cutPoint
	 *            cut point to split this invariant span
	 * @return an invariant span partition
	 * @throws MalformedSpanException
	 *             if the left or right partition of this span has a start < 1 ||
	 *             width < 1
	 * @throws IndexOutOfBoundsException
	 *             if the position is out of range (position >= start + width ||
	 *             position <= start)
	 */
	@Override
	public StreamElementPartition<SpanElement> split(long cutPoint) throws MalformedSpanException {
		if (start + width <= cutPoint) {
			throw new IndexOutOfBoundsException("Cut point too high. Can't split this span: cutPoint = " + cutPoint
					+ ", max allowed = " + (start + width - 1));
		}

		if (cutPoint <= start) {
			throw new IndexOutOfBoundsException("Cut point too low. Can't split this span: cutPoint = " + cutPoint
					+ ", min allowed = " + (start + 1));
		}
		return new StreamElementPartition<SpanElement>(new SpanElement(start, cutPoint - start, homeDocument),
				new SpanElement(cutPoint, start + width - cutPoint, homeDocument));
	}

	@Override
	public String toString() {
		return "SpanStreamElement [start=" + start + ", homeDocument=" + homeDocument + ", width=" + width + "]";
	}
	
	
}
