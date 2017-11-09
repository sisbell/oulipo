/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
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

import org.oulipo.streams.types.StreamElement;

/**
 * A two-part partition of a stream element. It has left and right elements
 * along a cut point.
 */
public final class StreamElementPartition<T extends StreamElement> {

	/**
	 * Left half of partition
	 */
	private final T left;

	/**
	 * Right half of partition
	 */
	private final T right;

	/**
	 * Constructs a partition containing a left and right stream elements.
	 * 
	 * @param left
	 *            left half of partition. Must not be null
	 * @param right
	 *            right half of partition. Must not be null.
	 */
	public StreamElementPartition(T left, T right) {
		if (left == null) {
			throw new IllegalArgumentException("left span is null");
		}

		if (right == null) {
			throw new IllegalArgumentException("right span is null");
		}

		this.left = left;
		this.right = right;

	}

	/**
	 * Gets left half of partition
	 * 
	 * @return
	 */
	public T getLeft() {
		return left;
	}

	/**
	 * Gets right half of partition
	 * 
	 * @return
	 */
	public T getRight() {
		return right;
	}

	@Override
	public String toString() {
		return "StreamElementPartition [left=" + left + ", right=" + right + "]";
	}

}
