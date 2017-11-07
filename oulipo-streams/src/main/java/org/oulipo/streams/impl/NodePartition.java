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

import org.oulipo.streams.types.StreamElement;

/**
 * Holds the partition of a node into left and right halves
 *
 * @param <S>
 */
public final class NodePartition<S extends StreamElement> {

	public final Node<S> left;

	public final Node<S> right;

	/**
	 * Constructs a <code>NodePartition</code>.
	 * 
	 * @param left
	 *            the left node partition. May be null if right is not null
	 * @param right
	 *            the right node partition. May be null if left is not null.
	 */
	public NodePartition(Node<S> left, Node<S> right) {
		if (right == null && left == null) {
			throw new IllegalArgumentException("both left and right can't be null in a partition");
		}
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		return "NodePartition [left=" + left + ", right=" + right + "]";
	}
}
