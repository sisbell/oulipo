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
 * A two-part partition of an invariant span. It has left and right spans along
 * a cut point.
 */
public final class InvariantSpanPartition {

	/**
	 * Left half of partition
	 */
	private final InvariantSpan left;

	/**
	 * Right half of partition
	 */
	private final InvariantSpan right;

	/**
	 * Constructs a span partition containing a left and right span. The edges of
	 * the spans touch (left.start + left.width == right.start)
	 * 
	 * @param left
	 *            left half of partition. Must not be null
	 * @param right
	 *            right half of partition. Must not be null.
	 * @throws MalformedSpanException
	 *             if the edges of the spans do not touch (left.start + left.width
	 *             == right.start)
	 */
	public InvariantSpanPartition(InvariantSpan left, InvariantSpan right) throws MalformedSpanException {
		if (left == null) {
			throw new IllegalArgumentException("left span is null");
		}

		if (right == null) {
			throw new IllegalArgumentException("right span is null");
		}

		if (left.start + left.width != right.start) {
			throw new MalformedSpanException("edges of left and right spans do not touch");
		}

		this.left = left;
		this.right = right;

	}

	/**
	 * Gets left half of partition
	 * 
	 * @return
	 */
	public InvariantSpan getLeft() {
		return left;
	}

	/**
	 * Gets right half of partition
	 * 
	 * @return
	 */
	public InvariantSpan getRight() {
		return right;
	}
}
