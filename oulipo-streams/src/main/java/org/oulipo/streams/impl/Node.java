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

import org.oulipo.net.MalformedSpanException;
import org.oulipo.streams.StreamElementPartition;
import org.oulipo.streams.types.StreamElement;

/*
 * Nodes form a binary tree, with leaf nodes containing a stream element value. Nodes track the
 * weight or number of characters to the left of the given node.
 */
public class Node<T extends StreamElement> {

	/**
	 * Builds a node
	 *
	 * @param <S>
	 */
	public static class Builder<S extends StreamElement> {

		private Node<S> left;

		private Node<S> parent;

		private Node<S> right;

		private String tag;

		private final S value;

		/**
		 * The number of characters to the left of this node
		 */
		private long weight;

		/**
		 * Constructs branch node with specified weight
		 * 
		 * @param weight
		 *            the number of characters to the left of this node
		 */
		public Builder(long weight) {
			this.weight = weight;
			value = null;
		}

		/**
		 * Constructs builder with leaf node
		 * 
		 * @param value
		 *            the stream element value of leaf node
		 */
		public Builder(S value) {
			this.weight = value.getWidth();
			this.value = value;
		}

		/**
		 * Builds a <code>Node</code> with the configured values. This method attaches
		 * the parent node (if it exists) to the left and right nodes (if they exist)
		 * 
		 * @return a <code>Node</code> with the configured values
		 */
		public Node<S> build() {
			Node<S> node = value != null ? new Node<S>(value) : new Node<S>(weight);
			node.left = left;
			node.right = right;
			node.parent = parent;
			node.tag = tag;

			if (left != null) {
				left.parent = node;
			}

			if (right != null) {
				right.parent = node;
			}
			return node;
		}

		public Builder<S> left(Node<S> left) {
			this.left = left;
			return this;
		}

		public Builder<S> parent(Node<S> parent) {
			this.parent = parent;
			return this;
		}

		public Builder<S> right(Node<S> right) {
			this.right = right;
			return this;
		}

		public Builder<S> tag(String tag) {
			this.tag = tag;
			return this;
		}
	}

	public boolean isRed;

	/**
	 * Left child node
	 */
	public Node<T> left;

	/**
	 * Parent of this node. If parent node is null, then this node is a root node,
	 * otherwise it is a child node.
	 */
	public Node<T> parent;

	/**
	 * Right child node
	 */
	public Node<T> right;

	/**
	 * Descriptive tag for this node (useful for debugging)
	 */
	public String tag;

	/**
	 * The stream element value of this node. A value can only exist for a leaf node
	 */
	public final T value;

	/**
	 * The number of characters to the left of this node
	 */
	public long weight;

	/**
	 * Constructs branch node
	 * 
	 * @param weight
	 *            the number of characters in the left branch of this node.
	 */
	public Node(long weight) {
		if (weight < 0) {
			throw new IndexOutOfBoundsException("weight must be greater than -1");
		}
		this.weight = weight;
		this.value = null;
	}

	/**
	 * Constructs leaf node
	 * 
	 * @param value
	 *            the stream element
	 * @throws IllegalArgumentException
	 *             if the weight != value.width for leaf node
	 * @throws IndexOutOfBoundsException
	 *             if weight is negative
	 */
	public Node(T value) {
		this.weight = value.getWidth();
		this.value = value;
	}

	/**
	 * Gives number of characters under this node
	 * 
	 * @return
	 */
	public long characterCount() {
		return RopeUtils.characterCount(this);
	}

	/**
	 * Is this node a leaf node, meaning it has a value attached
	 * 
	 * @return
	 */
	public boolean isLeaf() {
		return value != null;
	}

	/**
	 * If this node is right node of parent
	 * 
	 * @return
	 */
	public boolean isRightNode() {
		return parent != null && parent.right != null && parent.right.equals(this);
	}

	/**
	 * Is this node the root of the tree
	 * 
	 * @return
	 */
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * Splits leaf node value along it's variant span position. The weight of the
	 * returned parent node will equal the specified variant position. This is
	 * because the weight is the number of characters to the left of the node and we
	 * are splitting and inserting a node to right of the variant position.
	 * 
	 * @param leftPartitionWidth
	 *            the variant span position
	 * @return the parent node of the partitioned invariant span (leaf) nodes
	 * @throws MalformedSpanException
	 *             if this is a branch node or if unable to split the invariantSpan
	 *             for a leaf node.
	 * @throws IllegalStateException
	 *             if the constructed node being returned doesn't have a weight
	 *             equal to the specified variant position
	 */
	public Node<T> split(long leftPartitionWidth) throws MalformedSpanException {
		if (leftPartitionWidth < 1) {
			throw new IllegalArgumentException("leftPartitionWidth must be greater than 0");
		}
		if (!isLeaf()) {
			throw new MalformedSpanException("Can only split a leaf node");
		}

		// long cutPoint = (value instanceof InvariantSpan) ? ((InvariantSpan)
		// value).getStart() + leftPartitionWidth
		// : leftPartitionWidth;
		Node<T> parent = new Node<T>(leftPartitionWidth);

		StreamElementPartition<T> spans = (StreamElementPartition<T>) value.split(leftPartitionWidth);
		parent.left = new Node<T>(spans.getLeft());
		parent.right = new Node<T>(spans.getRight());

		parent.left.parent = parent;
		parent.right.parent = parent;
		if (parent.weight != leftPartitionWidth) {
			throw new IllegalStateException("node weight must equal variant position cutpoint");
		}
		return parent;
	}

	@Override
	public String toString() {
		return "Node [tag=" + tag + ", value=" + value + ", weight=" + weight + "]";
	}
}
