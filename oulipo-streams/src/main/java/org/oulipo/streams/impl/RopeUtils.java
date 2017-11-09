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

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.types.StreamElement;

public final class RopeUtils {

	/**
	 * Adds up the weights of all right nodes branching from the specified node. If
	 * this is done from the root node, it will give the character count of the
	 * rope, otherwise it gives all the character count in the specified node.
	 * 
	 * @param x
	 * @param weight
	 *            the displacement weights
	 */
	public static <T extends StreamElement> long addWeightsOfRightLeaningChildNodes(Node<T> x) {
		if (x == null || x.right == null) {
			return 0;
		}
		return x.right.weight + addWeightsOfRightLeaningChildNodes(x.right);
	}

	/**
	 * Adds the parent weights of right leaning children. If a child node is not
	 * right leaning its parent weight will not be added.
	 * 
	 * The count is added from bottom to top.
	 * 
	 * This method is used to determine the displacement of the specified child
	 * node.
	 * 
	 * @see #getStreamElements(VariantSpan)
	 * 
	 * @param child
	 *            the child node from which to start adding.
	 * @param disp
	 *            the displacement of the child node
	 * @throws IllegalStateException
	 *             if specified node is null
	 */
	public static <T extends StreamElement> long addWeightsOfRightLeaningParentNodes(Node<T> child) {
		if (child == null) {
			throw new IllegalStateException("child node can't be null");
		}
		if (child.parent == null) {
			return 0;
		}
		return (child.isRightNode() ? child.parent.weight : 0) + addWeightsOfRightLeaningParentNodes(child.parent);
	}

	public static <T extends StreamElement> void adjustWeightOfLeftLeaningParents(Node<T> startNode, long weight) {
		if (startNode == null) {
			return;
		}
		if (!startNode.isRightNode()) {
			startNode.weight -= weight;
			if (startNode.weight < 0) {
				throw new IllegalStateException("Can't adjust negative node weight" + startNode);
			}
		}
		adjustWeightOfLeftLeaningParents(startNode.parent, weight);
	}

	/**
	 * Get the number of characters in the specified node. If the specified node is
	 * the root, then this method returns number of characters in rope. If the node
	 * is a leaf node, then this method returns the width of the node.
	 * 
	 * @return the number of characters in the specified node
	 */
	public static <T extends StreamElement> long characterCount(Node<T> x) {
		if (x == null) {
			return 0;
		}
		if (x.isLeaf()) {
			return x.value.getWidth();
		}
		return x.weight + addWeightsOfRightLeaningChildNodes(x);
	}

	/**
	 * In-Order traversal and collection of all leaf nodes under the specified node.
	 * 
	 * @param x
	 *            - the node to traverse
	 * @param queue
	 *            the queue to collect leaf nodes
	 */
	public static <T extends StreamElement> void collectLeafNodes(Node<T> x, Queue<Node<T>> queue) {
		if (x == null) {
			return;
		}

		if (x.value != null) {
			queue.add(x);
		}

		collectLeafNodes(x.left, queue);
		collectLeafNodes(x.right, queue);
	}

	public static <T extends StreamElement> Node<T> concat(List<Node<T>> orphans) {
		Iterator<Node<T>> it = orphans.iterator();
		// TODO: concat balanced list
		Node<T> orphan = it.next();
		while (it.hasNext()) {
			orphan = RopeUtils.concat(orphan, it.next());
		}
		return orphan;

	}

	/**
	 * Creates a parent node that attaches the left and right nodes. The
	 * parent.weight is equivalent to the number of characters under the left node
	 * or 0 if the left node is null
	 * 
	 * @param left
	 *            the left node to attach
	 * @param right
	 *            the right node to attach
	 * @return parent node with attached left and right nodes
	 */
	public static <T extends StreamElement> Node<T> concat(Node<T> left, Node<T> right) {
		Node<T> parent = new Node<T>(left != null ? left.characterCount() : 0);
		if (left != null) {
			parent.left = left;
			parent.left.parent = parent;
		}

		if (right != null) {
			parent.right = right;
			parent.right.parent = parent;
		}
		return parent;
	}

	/**
	 * Cuts the left branch of specified node, if it exists, and adds it to the
	 * orphans list
	 * 
	 * @param x
	 * @param orphans
	 */
	public static <T extends StreamElement> void cutLeftNode(Node<T> x, List<Node<T>> orphans) {
		if (x.left != null) {
			orphans.add(x.left);
			x.left.parent = null;
			x.left = null;
		}
	}

	/**
	 * Cuts the right branch of specified node, if it exists, and adds it to the
	 * orphans list
	 * 
	 * @param x
	 * @param orphans
	 */
	public static <T extends StreamElement> void cutRightNode(Node<T> x, List<Node<T>> orphans) {
		if (x.right != null) {
			orphans.add(x.right);
			x.right.parent = null;
			x.right = null;
		}
	}

	public static <T extends StreamElement> Node<T> findRoot(Node<T> child) {
		while (child.parent != null) {
			return findRoot(child.parent);
		}
		return child;
	}

	/**
	 * Finds the first parent node of x that has a weight greater than or equal to
	 * the specified weight. The returned node will then be the lowest level node
	 * that is a parent of x and of all nodes within the specified weight.
	 * 
	 * If the specified x node is null, then the specified root node is returned.
	 * 
	 * This method is used in conjunction with an
	 * <code>InvariantSpanCollector</code> to get all nodes within a specified
	 * range. The returned search node (or it's part on a split) would be the first
	 * node in the collected nodes.
	 * 
	 * @param x
	 * @param weight
	 * @param root
	 * @return
	 * @throws IllegalArgumentException
	 *             if root is null
	 */
	public static <T extends StreamElement> Node<T> findSearchNode(Node<T> x, long weight, Node<T> root) {
		if (root == null) {
			throw new IllegalArgumentException("root is null");
		}

		if (x == null) {
			return root;
		}
		if (x.weight > weight) {
			return x;
		}
		return findSearchNode(x.parent, weight, root);
	}

	/**
	 * Get node at the specified position. Optionally, the caller can pass in a
	 * displacement to calculate the number of characters to the left of the
	 * returned node.
	 * 
	 * If the number of characters to the left of the node is less than the
	 * specified characterPosition, look to the right. If the right node is null,
	 * then the index node has been found. It covers the character position with its
	 * width. If the right node is not null, subtract the node.weight from the
	 * characterPosition and recursively continue the search for the index node.
	 * 
	 * If the number of characters to the left of the node is greater than the
	 * specified character position, then we know the index node is somewhere to the
	 * left if it is a branch node. If the node is a leaf node, then then index node
	 * has been found. For a branch node, recursively continue looking to the left.
	 * 
	 * 
	 * @param characterPosition
	 *            the character position of the desired node. There will be only one
	 *            node per character position.
	 * @param x
	 *            the node to search from
	 * @param disp
	 *            the displacement (number of characters to the left) of the
	 *            returned node.
	 * @return
	 */
	public static <T extends StreamElement> NodeIndex<T> index(long characterPosition, Node<T> x, long disp) {
		if (x == null) {
			throw new IllegalArgumentException("node is null");
		}

		if (characterPosition < 1) {
			throw new IndexOutOfBoundsException("characterPosition must be greater than 0");
		}

		if (x.isLeaf()) {
			return new NodeIndex<T>(x, disp);
		}

		if (x.weight < characterPosition) {
			if (x.right == null) {
				throw new IndexOutOfBoundsException("Can't find node at position =  " + (characterPosition + disp));
			}
			if (x.weight > 0) {
				disp += x.weight;
				x.isRed = true;
			}
			return index(characterPosition - x.weight, x.right, disp);
		} else {
			if (x.left == null) {
				throw new IndexOutOfBoundsException("Can't find node at position =  " + (characterPosition + disp));
			}
			return index(characterPosition, x.left, disp);
		}
	}

	public static boolean intersects(long start, long end, long start2, long end2) {
		return end > start2 && end2 > start;
	}

	public static <T extends StreamElement> Node<T> rebalance(Node<T> x) {
		Queue<Node<T>> output = new ArrayDeque<Node<T>>();
		Queue<Node<T>> input = new ArrayDeque<Node<T>>();
		collectLeafNodes(x, input);

		while (output.size() != 1) {
			while (true) {
				Node<T> n1 = input.poll();
				if (n1 == null) {
					break;
				}
				Node<T> n2 = input.poll();
				output.add(RopeUtils.concat(n1, n2));
				if (n2 == null) {
					break;
				}
			}
			input = output;
		}
		return output.remove();
	}
}
