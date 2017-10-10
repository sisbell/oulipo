/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.  
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
package org.oulipo.streams;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;

/**
 * Covers a text area of the docuverse. For example, 1.10~1.50 element would
 * have a span starting a 10 with a width of 50.
 *
 */
public class Span {

	public String homeDocument;

	/**
	 * Start byte position
	 */
	public long start;

	/**
	 * Number of characters in the span
	 */
	public long width;

	/**
	 * Default constructor
	 */
	public Span() {
	}

	/**
	 * Creates span
	 * 
	 * @param start
	 *            start byte position of span
	 * @param width
	 *            number of characters in span
	 * @param homeDocument
	 *            the home document of the span
	 * @throws MalformedSpanException
	 *             if start < 1 || width < 1
	 */
	public Span(long start, long width, String homeDocument) throws MalformedSpanException {
		this.start = start;
		this.width = width;
		if (start < 1) {
			throw new MalformedSpanException(this, "Start position must be greater than 0");
		}
		if (width < 1) {
			throw new MalformedSpanException(this, "Width must be greater than 0");
		}
			this.homeDocument = homeDocument;
	}
	
	public Span(long start, long width, TumblerAddress homeDocument) throws MalformedSpanException {
		this(start, width, homeDocument.value);
	}

	/**
	 * Adds this span to the specified document address to create a tumbler
	 * 
	 * @param documentAddress
	 *            root document address of this span
	 * @return
	 * @throws MalformedTumblerException
	 */
	public final TumblerAddress addToTumbler(TumblerAddress documentAddress) throws MalformedTumblerException {
		return new TumblerAddress.Builder(documentAddress.getDocumentAddress()).element("1." + start).width(width)
				.build();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Span other = (Span) obj;
		if (start != other.start)
			return false;
		if (homeDocument == null) {
			if (other.homeDocument != null)
				return false;
		} else if (!homeDocument.equals(other.homeDocument))
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (start ^ (start >>> 32));
		result = prime * result + ((homeDocument == null) ? 0 : homeDocument.hashCode());
		result = prime * result + (int) (width ^ (width >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "Span [start=" + start + ", width=" + width + "]";
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
	public SpanPartition split(long cutPoint) throws MalformedSpanException {
		if (start + width <= cutPoint) {
			throw new IndexOutOfBoundsException("Cut point too high. Can't split this span: cutPoint = " + cutPoint
					+ ", max allowed = " + (start + width - 1));
		}

		if (cutPoint <= start) {
			throw new IndexOutOfBoundsException("Cut point too low. Can't split this span: cutPoint = " + cutPoint
					+ ", min allowed = " + (start + 1));
		}
		return new SpanPartition(new Span(start, cutPoint - start, homeDocument),
				new Span(cutPoint, start + width - cutPoint, homeDocument));
	}
	
	public Span copy() throws MalformedSpanException {
		return new Span(start, width, homeDocument);
	}

}
