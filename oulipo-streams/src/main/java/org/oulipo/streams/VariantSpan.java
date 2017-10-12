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
import org.oulipo.net.TumblerAddress;

/**
 * This span is the public interface that clients see. As an example, if the
 * variant span is 1.10~1.50, then this represents the content in the document
 * between bytes 10 and 60. The content in this range is variant (or mutable)
 * and can be different after user edits.
 *
 */
public final class VariantSpan  {

	/**
	 * Constructs a <code>VariantSpan</code> starting at the specified start
	 * position with the specified width
	 * 
	 * @param start
	 *            the character position this span starts at. Must be greater than
	 *            0.
	 * @param width
	 *            the number of characters in this span. Must be greater than 0
	 * 
	 * @throws MalformedSpanException
	 *             if the start or width is an illegal value
	 */	

	public String homeDocument;

	/**
	 * Start byte position
	 */
	public long start;


	/**
	 * Number of characters in the span
	 */
	public long width;

	private VariantSpan() {
	}

	public VariantSpan(long start, long width) throws MalformedSpanException {
		this(start, width, "");
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
	public VariantSpan(long start, long width, String homeDocument) throws MalformedSpanException {
		this.start = start;
		this.width = width;
		if (start < 1) {
			throw new MalformedSpanException("Start position must be greater than 0");
		}
		if (width < 1) {
			throw new MalformedSpanException("Width must be greater than 0");
		}
			this.homeDocument = homeDocument;
	}
	
	public VariantSpan(long start, long width, TumblerAddress homeDocument) throws MalformedSpanException {
		this(start, width, homeDocument.value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariantSpan other = (VariantSpan) obj;
		if (homeDocument == null) {
			if (other.homeDocument != null)
				return false;
		} else if (!homeDocument.equals(other.homeDocument))
			return false;
		if (start != other.start)
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((homeDocument == null) ? 0 : homeDocument.hashCode());
		result = prime * result + (int) (start ^ (start >>> 32));
		result = prime * result + (int) (width ^ (width >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "VariantSpan [homeDocument=" + homeDocument + ", start=" + start + ", width=" + width + "]";
	}
	
	
}
