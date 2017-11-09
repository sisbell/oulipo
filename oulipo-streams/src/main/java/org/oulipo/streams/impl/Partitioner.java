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

import java.util.ArrayList;
import java.util.List;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.streams.types.StreamElement;

/**
 * Partitions a node into two separate nodes
 */
public class Partitioner {

	/**
	 * Attaches the specified child node to the parent node
	 * 
	 * @param child
	 * @param parent
	 */
	private static <T extends StreamElement> void attachParent(Node<T> child, Node<T> parent, boolean isRight) {
		child.parent = parent;
		if (isRight) {
			parent.right = child;
		} else {
			parent.left = child;
		}
	}

	/**
	 * Partition the specified node at the cut point.
	 * 
	 * A cutPoint greater than or equal to the node weight means that we are
	 * attempting to cut past the node in the stream. In this case, we push the
	 * entire node to the left and leave the right null.
	 * 
	 * A cutPoint is equal to zero means that we aren't partitioning the node. We
	 * push the entire node to the right partition and leave the left null.
	 * 
	 * @param leftPartitionWidth
	 *            the point to cut the node
	 * @param x
	 *            the node to cut
	 * @return NodePartition
	 * @throws MalformedSpanException
	 *             if the cut partition is malformed
	 * @throws IllegalStateException
	 *             if specified node is null
	 */
	public static <T extends StreamElement> NodePartition<T> createNodePartition(long leftPartitionWidth, Node<T> x)
			throws MalformedSpanException {
		if (x == null) {
			throw new IllegalStateException("Attempting to partition a null node");
		}

		if (leftPartitionWidth < 0) {
			throw new IndexOutOfBoundsException(
					"Width of left partition must be non-negative: width = " + leftPartitionWidth);
		}

		if (leftPartitionWidth >= x.characterCount()) {
			return new NodePartition<T>(x, null);
		} else if (leftPartitionWidth == 0) {
			return new NodePartition<T>(null, x);
		} else if (x.isLeaf() && x.isRoot()) {
			Node<T> split = x.split(leftPartitionWidth);
			split.left.parent = null;
			split.right.parent = null;
			return new NodePartition<T>(split.left, split.right);
		} else {
			NodeIndex<T> nodeIndex = RopeUtils.index(leftPartitionWidth, x, 0);

			if (nodeIndex.displacement > leftPartitionWidth) {
				throw new IllegalStateException("Width of left partition can't be less than displacement: width = "
						+ leftPartitionWidth + ", Displacement = " + nodeIndex.displacement);
			}

			Node<T> indexNode = nodeIndex.node;
			if (isSplitInMiddle(nodeIndex, leftPartitionWidth)) {
				Node<T> splitIndexNode = indexNode.split(leftPartitionWidth - nodeIndex.displacement);
				attachParent(splitIndexNode, indexNode.parent, indexNode.isRightNode());
				splitIndexNode.weight = splitIndexNode.left.weight;
				indexNode = splitIndexNode.left;
			}

			List<Node<T>> orphans = pruneIndexNode(indexNode, leftPartitionWidth, nodeIndex.displacement);

			if (orphans.isEmpty()) {
				throw new IllegalStateException(
						"No partition available after cut: partitionWidth = " + leftPartitionWidth);
			}
			return new NodePartition<T>(x, RopeUtils.concat(orphans));
		}
	}

	private static <T extends StreamElement> boolean isSplitInMiddle(NodeIndex<T> nodeIndex, long leftPartitionWidth) {
		long adjWidth = leftPartitionWidth - nodeIndex.displacement;
		return adjWidth >= 1 && nodeIndex.node.weight > adjWidth;
	}

	public static <S extends StreamElement> List<Node<S>> pruneIndexNode(Node<S> indexNode, long leftPartitionWidth,
			long displacement) {
		if (indexNode.isRightNode()) {
			if (indexNode.parent.parent != null) {
				// leftPartitionWidth -= indexNode.weight;
				if (indexNode.parent.left != null) {
					displacement -= indexNode.parent.left.weight;
					indexNode.parent.parent.isRed = false;
				}
				return pruneTree(indexNode.parent.parent, 0, leftPartitionWidth, displacement,
						indexNode.parent.parent.isRightNode());
			}
			return new ArrayList<>();
		} else {
			return pruneTree(indexNode.parent, 0, leftPartitionWidth, displacement, true);
		}
	}

	/**
	 * Prunes nodes to the right of the specified cut.
	 * 
	 * The side-affects of the method are: the weight of x will be adjusted AND
	 * right leaning nodes will be removed from x.
	 * 
	 * 
	 * @param x
	 *            the node to prune
	 * @param orphanedWidth
	 *            the total width of currently pruned nodes
	 * @param cut
	 * @param isChildRightLeaningNode
	 * 
	 * @return list of nodes to the right that have been pruned from the specified
	 *         node
	 */

	private static <S extends StreamElement> List<Node<S>> pruneTree(Node<S> x, long orphanedWidth, long leftWidth,
			long disp, boolean isChildRightLeaningNode) {
		if (x == null) {
			return new ArrayList<>();
		}

		if (!isChildRightLeaningNode) {
			x.weight -= orphanedWidth;
		}

		if (x.isRed) {
			x.isRed = false;

			if (disp != 0) {
				disp -= x.weight;
				if (disp < 0) {
					throw new IllegalStateException("disp must be non-negative: " + disp + ", Node = " + x);
				}
			}
		}

		List<Node<S>> orphans = new ArrayList<>();
		if (x.right != null && x.weight + disp >= leftWidth) {
			orphanedWidth += x.right.characterCount();
			RopeUtils.cutRightNode(x, orphans);
		}

		orphans.addAll(pruneTree(x.parent, orphanedWidth, leftWidth, disp, x.isRightNode()));
		return orphans;
	}
}
