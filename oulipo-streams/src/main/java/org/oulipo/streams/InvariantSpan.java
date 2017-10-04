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

/**
 * 
 * The content in this range is invariant (or immutable). This class defines a
 * start position and width within the IStream.
 * 
 * @see <a href="http://xanadu.com/tech/">IStream reference</a>
 *
 */
public final class InvariantSpan extends Span {

	private InvariantSpan() {
	}

	/**
	 * Creates an <code>InvariantSpan</code> with the specified start and width
	 * 
	 * @param start
	 * @param width
	 * @throws MalformedSpanException
	 */
	public InvariantSpan(long start, long width) throws MalformedSpanException {
		super(start, width);
	}

	public InvariantSpan(long start, long width, String tumbler) throws MalformedSpanException {
		super(start, width, tumbler);
		this.homeDocument = tumbler;
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
	public InvariantSpanPartition split(long cutPoint) throws MalformedSpanException {
		if (start + width <= cutPoint) {
			throw new IndexOutOfBoundsException("Cut point too high. Can't split this span: cutPoint = " + cutPoint
					+ ", max allowed = " + (start + width - 1));
		}

		if (cutPoint <= start) {
			throw new IndexOutOfBoundsException("Cut point too low. Can't split this span: cutPoint = " + cutPoint
					+ ", min allowed = " + (start + 1));
		}
		return new InvariantSpanPartition(new InvariantSpan(start, cutPoint - start, homeDocument),
				new InvariantSpan(cutPoint, start + width - cutPoint, homeDocument));
	}
	
	

}
