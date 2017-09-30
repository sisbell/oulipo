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
package org.oulipo.streams.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.InvariantSpan;
import org.oulipo.streams.InvariantSpanPartition;
import org.oulipo.streams.InvariantSpans;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/**
 * Rope implementation of the <code>VariantStream</code>
 */
public final class RopeVariantStream implements VariantStream {

	/**
	 * The number of characters to the left of a node
	 */
	private static class Displacement {

		/**
		 * Number of characters to the left of a node
		 */
		private long value;

		/**
		 * Adds to the current displacement. Negative values can be added but the total
		 * displacement after adding must be 0 or greater.
		 * 
		 * @param disp
		 * 
		 * @throws IllegalStateException
		 *             if displacement value after adding is less than 0
		 */
		void add(long disp) {
			value += disp;
			if (value < 0) {
				throw new IllegalStateException("Disp value can't be negative: new " + value + ", previous = "
						+ (value - disp) + ", delta = " + disp);
			}
		}

		/**
		 * Gets displacement value. Always positive
		 * 
		 * @return displacement value
		 */
		long getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "Displacement [value=" + value + "]";
		}
	}

	/**
	 * Collects invariant spans between two variant points: lo to hi
	 */
	private static class InvariantSpanCollector {

		private long position = 1;

		/**
		 * Ordered collection of <code>InvariantSpan<code>s.
		 */
		final Queue<InvariantSpan> queue = new LinkedList<>();

		/**
		 * Constructs a collector. The character position is the variant position of the
		 * initial node x called in the <code>collect</code> method.
		 * 
		 * @param characterPosition
		 */
		InvariantSpanCollector(long characterPosition) {
			this.position = characterPosition != 0 ? characterPosition : 1;
		}

		/**
		 * Collects <code>InvariantSpan</code>s between the specified lo and hi
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
		void collect(Node x, long lo, long hi) throws MalformedSpanException {
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
						long invariantStart = queue.isEmpty() && position + x.weight <= hi
								? x.value.start + x.value.width - (b - a)
								: x.value.start;
						queue.add(new InvariantSpan(invariantStart, b - a));
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
	}

	/**
	 * Internal node of rope
	 */
	public static class Node {

		public Node left;

		Node parent;

		public Node right;

		public String tag;

		public final InvariantSpan value;

		public long weight;

		/**
		 * Constructs branch node
		 * 
		 * @param weight
		 *            the number of characters in the left branch of this node.
		 */
		Node(long weight) {
			this(weight, null);
		}

		/**
		 * Constructs leaf node
		 * 
		 * @param weight
		 *            the weight of the leaf node. Weight is equal to
		 *            invarianSpan.value.width.
		 * @param value
		 *            the invariantSpan
		 * @throws IllegalArgumentException
		 *             if the weight != value.width
		 */
		Node(long weight, InvariantSpan value) {
			if (weight < 0) {
				throw new IndexOutOfBoundsException("weight must be greater than -1");
			}
			if (value != null && weight != value.width) {
				throw new IllegalArgumentException(
						"weight must equal span width : weight = " + weight + ", width = " + value.width);
			}
			this.weight = weight;
			this.value = value;
		}

		Node(long weight, long start, long width) throws MalformedSpanException {
			this(weight, new InvariantSpan(start, width));
		}

		/**
		 * Gives max number of characters under this node
		 * 
		 * @return
		 */
		long characterCount() {
			Displacement w = new Displacement();
			addWeightsOfRightLeaningChildNodes(this, w);
			return weight + w.getValue();
		}

		/**
		 * Creates shallow copy of this node
		 * 
		 * @return
		 */
		Node copy() {
			Node node = new Node(weight, value);
			node.right = right;
			node.left = left;
			node.parent = parent;
			node.tag = tag;
			return node;
		}

		/**
		 * Is this node a leaf node, meaning it has an invariant value attached
		 * 
		 * @return
		 */
		boolean isLeaf() {
			return value != null;
		}

		/**
		 * If this node is right node of parent
		 * 
		 * @return
		 */
		private boolean isRightNode() {
			return parent != null && parent.right != null && parent.right.equals(this);
		}

		/**
		 * Is this node the root of the tree
		 * 
		 * @return
		 */
		boolean isRoot() {
			return parent == null;
		}

		/**
		 * Splits node along it's variant span position. The weight of the returned
		 * parent node will equal the specified variantPosition.
		 * 
		 * @param variantPosition
		 *            the variant span position
		 * @return the parent node of the partitioned invariant span (leaf) nodes
		 * @throws MalformedSpanException
		 *             if this is a branch node or if unable to split the invariantSpan
		 *             for a leaf node.
		 */
		Node split(long variantPosition) throws MalformedSpanException {
			if (!isLeaf()) {
				throw new MalformedSpanException("Can only split a leaf node");
			}

			long cutPoint = value.start + variantPosition;
			Node parent = new Node(variantPosition);

			InvariantSpanPartition spans = value.split(cutPoint);
			parent.left = new Node(spans.getLeft().width, spans.getLeft());
			parent.right = new Node(spans.getRight().width, spans.getRight());

			parent.left.parent = parent;
			parent.right.parent = parent;
			return parent;
		}

		@Override
		public String toString() {
			return "Node [tag=" + tag + ", value=" + value + ", weight=" + weight + "]";
		}
	}

	/**
	 * Partition of node into left and right halves
	 */
	static class NodePartition {

		final Node left;

		final Node right;

		NodePartition(Node left, Node right) {
			this.left = left;
			this.right = right;
		}

	}

	/**
	 * Prunes all branches to the right of the cutPoint and puts them in orphan
	 * list. A pruner will not split any leaf nodes, as this needs to be handled
	 * prior to pruning.
	 */
	static class Pruner {

		/**
		 * Pruning or cut point
		 */
		final long cutPoint;

		/**
		 * The number of characters to the left of the current node
		 */
		final Displacement disp;

		/**
		 * List of nodes that have been cut from the main branch
		 */
		final List<Node> orphans;

		/**
		 * Width of pruned leaf nodes. As each node is pruned, this value increases. The
		 * wid will be used to adjust the weight of nodes to the left of the cutPoint
		 */
		Wid wid;

		Pruner(long cutPoint, Wid wid, Displacement disp, List<Node> orphans) {
			if (cutPoint < 1) {
				throw new IllegalArgumentException("Cut point must be positive: " + cutPoint);
			}
			if (wid.value < 0) {
				throw new IllegalArgumentException("Wid must be positive: " + wid.value);
			}
			if (disp.value < 0) {
				throw new IllegalArgumentException("Displacement must be positive: " + disp.value);
			}
			this.cutPoint = cutPoint;
			this.wid = wid;
			this.disp = disp;
			this.orphans = orphans;
		}

		void prune(Node x) {
			if (x == null) {
				return;
			}

			if (disp.getValue() < 0) {
				throw new IllegalStateException("Disp value can't be negative: " + disp.getValue() + ", " + x);
			}

			if (wid.value < 0) {
				throw new IllegalStateException("Wid value can't be negative: " + wid.value + ", " + x);
			}

			System.out.println(x + ", " + disp + ", cutPoint = " + cutPoint + "," + wid);

			if (wid.isBlack) {
				System.out.println(
						"Is Black: adjusting disp: old = " + disp.getValue() + ",new =" + (disp.getValue() - x.weight));
				disp.add(-x.weight);
			}

			long adjValue = x.weight + disp.getValue();

			if (!wid.isBlack) {
				System.out.println("IsRed: adjusting weight: old =" + x.weight + ", new = " + (x.weight - wid.value));
				x.weight -= wid.value;
			}

			System.out.println("Adjusted values: " + x + ", " + disp + ", cutPoint = " + cutPoint + ",adjValue="
					+ adjValue + "," + wid);
			if (x.right != null && adjValue >= cutPoint) {
				wid.value += x.right.characterCount();
				x.right.parent = null;
				orphans.add(x.right);
				System.out.println("cut off: " + x.right);
				x.right = null;
			}

			System.out.println("Finish Node: " + x + ", " + wid);
			System.out.println();
			wid.isBlack = x.isRightNode();
			prune(x.parent);
		}

		@Override
		public String toString() {
			return "Pruner [cutPoint=" + cutPoint + ", disp=" + disp + ", wid=" + wid + ", orphans=" + orphans + "]";
		}
	}

	/**
	 * Stores width of pruned nodes
	 */
	static class Wid {

		boolean isBlack = true;

		/**
		 * Cumulative width of all pruned nodes
		 */
		long value;

		@Override
		public String toString() {
			return "Wid [value=" + value + ", isBlack=" + isBlack + "]";
		}
	}

	/**
	 * Adds up the weights of all right nodes branching from the specified node. If
	 * this is done from the root node, it will give the character count of the
	 * rope, otherwise it gives all the character count in the specified node.
	 * 
	 * @param x
	 * @param weight
	 *            the displacement weights
	 */
	private static void addWeightsOfRightLeaningChildNodes(Node x, Displacement weight) {
		if (x == null || x.right == null) {
			return;
		}
		weight.add(x.right.weight);
		addWeightsOfRightLeaningChildNodes(x.right, weight);
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
	 * @see #getInvariantSpans(VariantSpan)
	 * 
	 * @param child
	 *            the child node from which to start adding.
	 * @param disp
	 *            the displacement of the child node
	 */
	private static void addWeightsOfRightLeaningParentNodes(Node child, Displacement disp) {
		if (child.parent == null) {
			return;
		}

		if (child.isRightNode()) {
			disp.add(child.parent.weight);
		}

		addWeightsOfRightLeaningParentNodes(child.parent, disp);
	}

	/**
	 * In-Order traversal and collection of all leaf nodes under the specified node.
	 * 
	 * @param x
	 *            - the node to traverse
	 * @param queue
	 *            the queue to collect leaf nodes
	 */
	private static void collectLeafNodes(Node x, Queue<Node> queue) {
		if (x == null) {
			return;
		}

		if (x.value != null) {
			queue.add(x);
		}

		collectLeafNodes(x.left, queue);
		collectLeafNodes(x.right, queue);
	}

	/**
	 * Creates a parent node that attaches the left and right nodes. The
	 * parent.weight is equivalent to the number of characters under the left node
	 * or 0 is the left node is null
	 * 
	 * @param left
	 *            the left node to attach
	 * @param right
	 *            the right node to attach
	 * @return parent node with attached left and right nodes
	 */
	private static Node concat(Node left, Node right) {
		Node parent = new Node(left != null ? left.characterCount() : 0);
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
	 * Partition the specified node at the cut point.
	 * 
	 * If the cutPoint is greater than or equal to the node weight, then a node
	 * partition is returned with a null left node and a right node equal to node x.
	 * 
	 * if the cutPoint is equal to one, then a node partition is returned with a
	 * left node equal to node x and a null right node.
	 * 
	 * 
	 * @param cutPoint
	 *            the point to cut the node
	 * @param x
	 *            the node to cut
	 * @return NodePartition
	 * @throws MalformedSpanException
	 *             if the cut partition is malformed
	 */
	static NodePartition createPartition(long cutPoint, Node x) throws MalformedSpanException {
		if (cutPoint >= x.characterCount()) {
			return new NodePartition(x, null);
		} else if (cutPoint == 1) {
			return new NodePartition(null, x);
		}

		List<Node> orphans = new ArrayList<>();
		split(cutPoint, x, orphans);

		if (orphans.isEmpty()) {
			throw new IllegalStateException("No partition available after cut");
		}

		Iterator<Node> it = orphans.iterator();

		Node orphan = it.next();

		while (it.hasNext()) {
			orphan = concat(orphan, it.next());
		}
		return new NodePartition(x, orphan);
	}

	/**
	 * Cuts the right branch of specified node, if it exists, and adds it to the
	 * orphans list
	 * 
	 * @param x
	 * @param orphans
	 */
	private static void cutRightNode(Node x, List<Node> orphans) {
		System.out.println("cut off right: " + x.right);
		if (x.right != null) {
			orphans.add(x.right);
			x.right = null;
		}
	}

	/**
	 * Get node at the specified position
	 * 
	 * @param characterPosition
	 * @param x
	 *            the node to search from
	 * @return
	 */
	static Node index(long characterPosition, Node x, Displacement disp) {
		if (x == null) {
			return new Node(characterPosition);
		}

		if (disp != null && !x.isLeaf() && characterPosition > x.weight) {
			disp.add(x.weight);
		}
		if (x.weight < characterPosition) {
			return x.right == null ? x : index(characterPosition - x.weight, x.right, disp);
		} else {
			return x.left == null ? x : index(characterPosition, x.left, disp);
		}
	}

	/**
	 * Splits node x at specified cutPoint and collects all nodes that have been
	 * cutoff into the specified orphans list.
	 * 
	 * This method modifies the parent weights and should not be used by read-only
	 * methods.
	 * 
	 * @param cutPoint
	 *            the point to cut the node
	 * @param x
	 *            the node to cut
	 * @param orphans
	 *            the collector for nodes which have been cut off (nodes greater
	 *            than cutPoint)
	 * @throws MalformedSpanException
	 *             if any cut node has a malformed span
	 */
	static void split(long cutPoint, Node x, List<Node> orphans) throws MalformedSpanException {
		if (x == null) {
			return;
		}

		if (cutPoint < 1) {
			throw new IndexOutOfBoundsException("cutPoint must be greater than 0: cutPoint = " + cutPoint);
		}

		Wid wid = new Wid();
		Displacement disp = new Displacement();
		Node indexNode = index(cutPoint, x, disp);
		Node indexParent = indexNode.parent;
		if (indexParent == null) {
			// throw new IllegalStateException("Nodes parent is null: " + x.toString() + ",
			// Index Node = " + indexNode
			// + ", cutPoint = " + cutPoint + ", disp = " + disp);
		}

		if (disp.getValue() > cutPoint) {
			throw new MalformedSpanException("CutPoint can't be less than displacement: cutPoint = " + cutPoint
					+ ", Displacement = " + disp.getValue());
		}

		if (disp.getValue() != cutPoint - 1) {
			Node splitNode = indexNode.split(cutPoint - disp.getValue() - 1);
			splitNode.parent = indexNode.parent;
			if (indexParent != null) {// TODO: investigate this condition
				if (indexNode.isRightNode()) {
					indexParent.right = splitNode;
				} else {
					indexParent.left = splitNode;
					wid.isBlack = false;
				}
			}

			indexNode = splitNode.right;
			cutRightNode(splitNode, orphans);
		} else {
			if (indexNode.isRightNode()) {
				cutRightNode(indexParent, orphans);
			} else {
				orphans.add(indexParent.left);
				wid.isBlack = false;
			}
		}

		wid.value = indexNode.weight;

		Pruner pruner = new Pruner(cutPoint, wid, disp, orphans);
		pruner.prune(indexParent);

		// TODO: re-balance tree
	}

	/**
	 * Home document of invariant spans
	 */
	private final TumblerAddress homeDocument;

	/**
	 * Mapper for reading and writing to/from JSON
	 */
	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Root node of this rope structure
	 */
	private Node root;

	/**
	 * Constructs a <code>RopeVariantStream</code>
	 * 
	 * @param homeDocument
	 *            the home document of invariant spans
	 */
	public RopeVariantStream(TumblerAddress homeDocument) {
		this.homeDocument = homeDocument;
	}

	/**
	 * Constructs a <code>RopeVariantStream</code> with a pre-constructed tree.
	 * 
	 * @param homeDocument
	 *            the home document of invariant spans
	 * @param root
	 *            the root node
	 */
	public RopeVariantStream(TumblerAddress homeDocument, Node root) {
		this.homeDocument = homeDocument;
		this.root = root;
	}

	/**
	 * Get the number of characters in this rope.
	 * 
	 * @return the number of characters in this rope
	 */
	public long characterCount() {
		if (root == null) {
			return 1;
		}
		Displacement w = new Displacement();
		addWeightsOfRightLeaningChildNodes(root, w);
		return root.weight + w.getValue();
	}

	@Override
	public void copy(long characterPosition, VariantSpan variantSpan) throws MalformedSpanException, IOException {
		List<InvariantSpan> spans = getInvariantSpans(variantSpan).getInvariantSpans();
		put(characterPosition, spans);
	}

	/**
	 * Creates partition of this tree along the cutPoint. This method modifies
	 * structure.
	 * 
	 * @param cutPoint
	 * @return
	 * @throws MalformedSpanException
	 * @throws IndexOutOfBoundsException
	 *             if cut point is out of range
	 */
	private NodePartition createPartition(long cutPoint) throws MalformedSpanException {
		if (root == null) {
			throw new IllegalStateException("No root of this tree");
		}

		if (cutPoint < 1) {
			throw new IllegalArgumentException("Split position must be greater than 0");
		}

		if (cutPoint > characterCount()) {
			throw new IndexOutOfBoundsException("Attempting to split in illegal position");
		}

		return createPartition(cutPoint, root);
	}

	@Override
	public void delete(VariantSpan variantSpan) throws MalformedSpanException {
		deleteRange(variantSpan);
	}

	/*
	 * Delete InvariantSpans in the variant span range.
	 * 
	 * @return returns Node containing deleted portion
	 * 
	 */
	private Node deleteRange(VariantSpan variantSpan) throws MalformedSpanException {
		NodePartition partI = createPartition(variantSpan.start);
		System.out.println("=============");
		NodePartition partJ = createPartition(variantSpan.width + 1, partI.right);
		if (partI.left == null && partJ.right == null) {
			root = null;
			return partI.right;
		} else if (partI.left == null) {
			root = partJ.right;
		} else if (partJ.right == null) {
			root = partI.left;
		} else {
			root = concat(partI.left, partJ.right);
		}

		return partJ.left.left;
	}

	/**
	 * Finds the first parent node of x that has a weight greater than or equal to
	 * the specified width. The returned node will then be the lowest level node
	 * that is a parent of x and of all nodes within the specified width.
	 * 
	 * @param x
	 * @param width
	 * @return
	 */
	private Node findSearchNode(Node x, long width) {
		if (x == null) {
			return root;
		}
		if (x.weight >= width) {
			return x;
		}
		return findSearchNode(x.parent, width);
	}

	/**
	 * Gets leaf nodes with invariant span values
	 * 
	 * @return
	 * @throws MalformedSpanException
	 */
	Iterator<Node> getAllLeafNodes() throws MalformedSpanException {
		Queue<Node> queue = new LinkedList<Node>();
		collectLeafNodes(root, queue);
		return queue.iterator();
	}

	@Override
	public InvariantSpans getInvariantSpans() throws MalformedSpanException {
		InvariantSpans spans = new InvariantSpans();
		Iterator<Node> it = getAllLeafNodes();
		while (it.hasNext()) {
			Node node = it.next();
			if (node.value != null) {
				spans.getInvariantSpans().add(node.value);
			}
		}
		return spans;
	}

	@Override
	public InvariantSpans getInvariantSpans(VariantSpan variantSpan) throws MalformedSpanException {
		Node loNode = index(variantSpan.start, root, null);
		Node searchNode = findSearchNode(loNode, variantSpan.width);

		Displacement disp = new Displacement();
		addWeightsOfRightLeaningParentNodes(searchNode, disp);

		InvariantSpanCollector collector = new InvariantSpanCollector(disp.getValue());
		collector.collect(searchNode, variantSpan.start, variantSpan.start + variantSpan.width);

		InvariantSpans spans = new InvariantSpans();

		Iterator<InvariantSpan> it = collector.queue.iterator();
		while (it.hasNext()) {
			InvariantSpan is = it.next();
			try {
				is.homeDocument = homeDocument.toExternalForm();
			} catch (MalformedTumblerException e) {
				e.printStackTrace();// TODO: throw
			}
			spans.getInvariantSpans().add(is);
		}
		return spans;
	}

	@Override
	public List<VariantSpan> getVariantSpans(InvariantSpan invariantSpan) throws MalformedSpanException {
		// TODO: scans very inefficient
		long index = 1;
		List<VariantSpan> vspans = new ArrayList<>();
		List<InvariantSpan> ispans = getInvariantSpans().getInvariantSpans();
		for (int i = 0; i < ispans.size(); i++) {
			InvariantSpan ispan = ispans.get(i);
			long start = ispan.start;
			long end = start + ispan.width;
			long start2 = invariantSpan.start;
			long end2 = start2 + invariantSpan.width;
			if (intersects(start, end, start2, end2)) {
				System.out.println("Intersect: " + ispan);
				long a = Math.max(0, start2 - start);
				long b = Math.max(0, end - end2);

				System.out.println("[" + a + "," + b + "]");
				System.out.println("Index = " + index + ", span width = " + ispan.width);
				VariantSpan vs = new VariantSpan(index + a, ispan.width - b - a);
				vs.homeDocument = homeDocument.value;// TODO: transcluded
				vspans.add(vs);
			}

			index += ispan.width;
		}
		return vspans;
	}

	@Override
	public InvariantSpan index(long characterPosition) {
		Node idx = index(characterPosition, root, null);
		return idx == null ? null : idx.value;
	}

	private void insert(long i, Node x) throws MalformedSpanException {
		if (i > characterCount()) {
			throw new IndexOutOfBoundsException("Attempting to insert in illegal range: Current Max = "
					+ characterCount() + ", Attempted insert = " + i);
		}

		if (root == null) {
			if (i != 1) {
				throw new IllegalArgumentException("First node must be inserted at position 1");
			}
			if (x.value == null) {
				throw new IllegalArgumentException("First inserted node must have an invariant span value");
			}
			if (x.weight < 1) {
				throw new IndexOutOfBoundsException("Attempting to assign root with weight less than 1");
			}
			root = x;
		} else if (i == characterCount()) {
			root = concat(root, x);
		} else {
			NodePartition partition = createPartition(i);
			root = concat(concat(partition.left, x), partition.right);
		}
	}

	private boolean intersects(long start, long end, long start2, long end2) {
		return end > start2 && end2 > start;
	}

	@Override
	public void move(long to, VariantSpan v1) throws MalformedSpanException {
		insert(to, deleteRange(v1));
	}

	@Override
	public void put(long characterPosition, InvariantSpan val) throws MalformedSpanException {
		if (val == null) {
			throw new IllegalArgumentException("invariant span is null");
		}

		if (characterPosition < 1) {
			throw new IndexOutOfBoundsException("put position must be greater than 0");
		}

		if (val.width < 1) {
			throw new MalformedSpanException("invariant span must have a width greater than 0");
		}

		insert(characterPosition, new Node(val.width, val));
	}

	public void save(OutputStream os) throws MalformedSpanException, IOException {
		mapper.writeValue(os, Lists.newArrayList(getAllLeafNodes()));
	}

	@Override
	public void swap(VariantSpan v1, VariantSpan v2) throws MalformedSpanException {
		// assume v1 < v2, no overlap

		Node from = deleteRange(v1);
		from.parent = null;

		long startV2 = v2.start - v1.width;

		Node to = deleteRange(new VariantSpan(startV2, v2.width));
		to.parent = null;

		insert(v1.start, to);
		insert(v2.start, from);
	}

}
