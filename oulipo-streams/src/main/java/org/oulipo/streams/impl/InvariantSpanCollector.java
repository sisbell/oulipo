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
package org.oulipo.streams.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.types.InvariantSpan;
import org.oulipo.streams.types.StreamElement;

/**
 * Collects invariant spans between two variant points: lo to hi
 */
public final class InvariantSpanCollector<S extends StreamElement> {

	private long position = 1;

	/**
	 * Ordered collection of <code>StreamElement<code>s.
	 */
	private final Queue<S> queue = new LinkedList<>();

	/**
	 * Constructs a collector. The character position is the variant position of the
	 * initial node x called in the <code>collect</code> method.
	 * 
	 * @param characterPosition
	 */
	public InvariantSpanCollector(long characterPosition) {
		this.position = characterPosition != 0 ? characterPosition : 1;
	}

	/**
	 * Collects <code>StreamElement</code>s between the specified lo and hi
	 * characters.
	 * 
	 * The specified node x is the first parent node of the node at position lo.
	 * 
	 * @see RopeVariantStream#findSearchNode(Node, long)
	 * 
	 * @param x
	 *            the initial node to search from
	 * @param lo
	 * @param hi
	 * @throws MalformedSpanException
	 */
	public void collect(Node<S> x, long lo, long hi) throws MalformedSpanException {
		if (x == null || position > hi) {
			return;
		}

		if (x.value != null) {
			long start = position;
			long end = position + x.weight;
			if (intersects(start, end, lo, hi)) {
				long a = start <= lo ? lo : start;
				long b = end <= hi ? end : hi;

				if (a != b) {
					S copy = (S) x.value.copy();
					copy.setWidth(b - a);

					if (x.value instanceof InvariantSpan) {
						InvariantSpan span = (InvariantSpan) copy;
						InvariantSpan value = (InvariantSpan) x.value;
						long invariantStart = queue.isEmpty() && position + x.weight - 1 <= hi
								? value.getStart() + value.getWidth() - (b - a)
								: value.getStart();
						span.setStart(invariantStart);
					}
					queue.add(copy);
				}
			}
			position += x.weight;
		}

		collect(x.left, lo, hi);
		collect(x.right, lo, hi);
	}

	private boolean intersects(long start, long end, long lo, long hi) {
		return end >= lo && hi >= start;
	}

	public Iterator<S> iterator() {
		return queue.iterator();
	}
}
