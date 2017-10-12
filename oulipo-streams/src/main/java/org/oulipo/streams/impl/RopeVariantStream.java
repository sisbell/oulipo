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
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.types.OverlayElement;
import org.oulipo.streams.types.SpanElement;
import org.oulipo.streams.types.StreamElement;
import org.oulipo.streams.types.StreamElementPartition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/**
 * Rope implementation of the <code>VariantStream</code>
 */
public final class RopeVariantStream<T extends StreamElement> implements VariantStream<T> {

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
	private static class InvariantSpanCollector<S extends StreamElement> {

		private long position = 1;

		/**
		 * Ordered collection of <code>StreamElement<code>s.
		 */
		final Queue<S> queue = new LinkedList<>();

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
		void collect(Node<S> x, long lo, long hi) throws MalformedSpanException {
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
						S copy = x.value.copy();
						copy.setWidth(b - a);

						if (x.value instanceof SpanElement) {
							SpanElement span = (SpanElement) copy;
							SpanElement value = (SpanElement) x.value;
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
	}

	/**
	 * Internal node of rope
	 */
	public static class Node<T extends StreamElement> {

		public Node<T> left;

		Node<T> parent;

		public Node<T> right;

		public String tag;

		public final T value;

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

		/*
		 * Node(long weight, long start, long width, TumblerAddress homeDocument) throws
		 * MalformedSpanException { this(weight, new StreamElement(start, width,
		 * homeDocument)); }
		 */

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
		Node(long weight, T value) {
			if (weight < 0) {
				throw new IndexOutOfBoundsException("weight must be greater than -1");
			}
			if (value != null && weight != value.getWidth()) {
				throw new IllegalArgumentException(
						"weight must equal span width : weight = " + weight + ", width = " + value.getWidth());
			}
			this.weight = weight;
			this.value = value;
		}

		private void addWeightsOfRightLeaningChildNodes(Node<T> x, Displacement weight) {
			if (x == null || x.right == null) {
				return;
			}
			weight.add(x.right.weight);
			addWeightsOfRightLeaningChildNodes(x.right, weight);
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
		Node<T> copy() {
			Node<T> node = new Node<T>(weight, value);
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
		Node<T> split(long variantPosition) throws MalformedSpanException {
			if (!isLeaf()) {
				throw new MalformedSpanException("Can only split a leaf node");
			}
			
			long cutPoint = (value instanceof SpanElement) ? ((SpanElement) value).getStart() + variantPosition
					: variantPosition;
			Node<T> parent = new Node<T>(variantPosition);

			StreamElementPartition<T> spans = value.split(cutPoint);
			// TODO: add start- need to know where this value starts
			parent.left = new Node<T>(spans.getLeft().getWidth(), spans.getLeft());
			parent.right = new Node<T>(spans.getRight().getWidth(), spans.getRight());

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
	static class NodePartition<S extends StreamElement> {

		final Node<S> left;

		final Node<S> right;

		NodePartition(Node<S> left, Node<S> right) {
			this.left = left;
			this.right = right;
		}

	}

	/**
	 * Prunes all branches to the right of the cutPoint and puts them in orphan
	 * list. A pruner will not split any leaf nodes, as this needs to be handled
	 * prior to pruning.
	 */
	static class Pruner<S extends StreamElement> {

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
		final List<Node<S>> orphans;

		/**
		 * Width of pruned leaf nodes. As each node is pruned, this value increases. The
		 * wid will be used to adjust the weight of nodes to the left of the cutPoint
		 */
		Wid wid;

		Pruner(long cutPoint, Wid wid, Displacement disp, List<Node<S>> orphans) {
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

		void prune(Node<S> x) {
			if (x == null) {
				return;
			}

			if (disp.getValue() < 0) {
				throw new IllegalStateException("Disp value can't be negative: " + disp.getValue() + ", " + x);
			}

			if (wid.value < 0) {
				throw new IllegalStateException("Wid value can't be negative: " + wid.value + ", " + x);
			}

			if (wid.isBlack) {
				disp.add(-x.weight);
			}

			long adjValue = x.weight + disp.getValue();

			if (!wid.isBlack) {
				x.weight -= wid.value;
			}

			if (x.right != null && adjValue >= cutPoint) {
				wid.value += x.right.characterCount();
				x.right.parent = null;
				orphans.add(x.right);
				x.right = null;
			}

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
	static <T extends StreamElement> Node<T> concat(Node<T> left, Node<T> right) {
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
	static <T extends StreamElement> NodePartition<T> createPartition(long cutPoint, Node<T> x)
			throws MalformedSpanException {
		if (x == null) {
			throw new IllegalStateException("Attempting to partition a null node");
		}
		if (cutPoint > x.characterCount()) {
			return new NodePartition<T>(x, null);
		} else if (cutPoint == 1) {
			return new NodePartition<T>(null, x);
		}

		List<Node<T>> orphans = new ArrayList<>();
		split(cutPoint, x, orphans);

		if (orphans.isEmpty()) {
			throw new IllegalStateException("No partition available after cut");
		}

		Iterator<Node<T>> it = orphans.iterator();

		Node<T> orphan = it.next();

		while (it.hasNext()) {
			orphan = concat(orphan, it.next());
		}
		return new NodePartition<T>(x, orphan);
	}

	/**
	 * Cuts the right branch of specified node, if it exists, and adds it to the
	 * orphans list
	 * 
	 * @param x
	 * @param orphans
	 */
	private static <T extends StreamElement> void cutRightNode(Node<T> x, List<Node<T>> orphans) {
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
	static <T extends StreamElement> Node<T> index(long characterPosition, Node<T> x, Displacement disp) {
		if (x == null) {
			return new Node<T>(characterPosition);
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
	static <T extends StreamElement> void split(long cutPoint, Node<T> x, List<Node<T>> orphans)
			throws MalformedSpanException {
		if (x == null) {
			return;
		}

		if (cutPoint < 1) {
			throw new IndexOutOfBoundsException("cutPoint must be greater than 0: cutPoint = " + cutPoint);
		}

		Wid wid = new Wid();
		Displacement disp = new Displacement();
		Node<T> indexNode = index(cutPoint, x, disp);

		Node<T> indexParent = indexNode.parent;

		if (disp.getValue() > cutPoint) {
			throw new MalformedSpanException("CutPoint can't be less than displacement: cutPoint = " + cutPoint
					+ ", Displacement = " + disp.getValue());
		}

		if (disp.getValue() != cutPoint - 1) {
			Node<T> splitNode = indexNode.split(cutPoint - disp.getValue() - 1);
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
				indexParent.left = null;
				wid.isBlack = false;
			}
		}

		wid.value = indexNode.weight;

		if (indexParent != null) {
			Pruner<T> pruner = new Pruner<T>(cutPoint, wid, disp, orphans);
			pruner.prune(indexParent);
		}
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
	Node<T> root;

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
	public RopeVariantStream(TumblerAddress homeDocument, Node<T> root) {
		this.homeDocument = homeDocument;
		this.root = root;
	}

	private boolean addOverlay(TumblerAddress link, List<T> overlays) {
		for (T overlaySpan : overlays) {
			if ((overlaySpan instanceof OverlayElement) && !((OverlayElement) overlaySpan).hasLinkType(link)) {
				return true;
			}
		}
		return false;
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
	private void addWeightsOfRightLeaningChildNodes(Node<T> x, Displacement weight) {
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
	 * @see #getStreamElements(VariantSpan)
	 * 
	 * @param child
	 *            the child node from which to start adding.
	 * @param disp
	 *            the displacement of the child node
	 */
	private void addWeightsOfRightLeaningParentNodes(Node<T> child, Displacement disp) {
		if (child.parent == null) {
			return;
		}

		if (child.isRightNode()) {
			disp.add(child.parent.weight);
		}

		addWeightsOfRightLeaningParentNodes(child.parent, disp);
	}

	/**
	 * Get the number of characters in this rope.
	 * 
	 * @return the number of characters in this rope
	 */
	public long characterCount() {
		if (root == null) {
			return 0;
		}
		if (root.right == null && root.left == null) {
			return root.value.getWidth();
		}
		Displacement w = new Displacement();
		addWeightsOfRightLeaningChildNodes(root, w);
		return root.weight + w.getValue();
	}

	/**
	 * In-Order traversal and collection of all leaf nodes under the specified node.
	 * 
	 * @param x
	 *            - the node to traverse
	 * @param queue
	 *            the queue to collect leaf nodes
	 */
	private void collectLeafNodes(Node<T> x, Queue<Node<T>> queue) {
		if (x == null) {
			return;
		}

		if (x.value != null) {
			queue.add(x);
		}

		collectLeafNodes(x.left, queue);
		collectLeafNodes(x.right, queue);
	}

	@Override
	public void copy(long characterPosition, VariantSpan variantSpan) throws MalformedSpanException, IOException {
		putElements(characterPosition, getStreamElements(variantSpan));
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
	private NodePartition<T> createPartition(long cutPoint) throws MalformedSpanException {
		if (root == null) {
			throw new IllegalStateException("No root of this tree");
		}

		if (cutPoint < 1) {
			throw new IllegalArgumentException("Split position must be greater than 0");
		}

		if (cutPoint > characterCount() + 1) {
			throw new IndexOutOfBoundsException("Attempting to split in illegal position: cutpoint = " + cutPoint
					+ ", count = " + characterCount());
		}

		if (root.left == null && root.right == null) {
			if (cutPoint >= root.characterCount()) {
				return new NodePartition<T>(root, null);
			} else if (cutPoint == 1) {
				return new NodePartition<T>(null, root);
			} else {
				root = root.split(cutPoint);
				return new NodePartition<T>(root.left, root.right);
			}
		} else {
			return createPartition(cutPoint, root);
		}
	}

	@Override
	public void delete(VariantSpan variantSpan) throws MalformedSpanException {
		if (variantSpan == null) {
			throw new MalformedSpanException("Variant span is null for delete operation");
		}
		deleteRange(variantSpan);
	}

	/*
	 * Delete InvariantSpans in the variant span range.
	 * 
	 * @return returns Node containing deleted portion
	 * 
	 */
	private Node<T> deleteRange(VariantSpan variantSpan) throws MalformedSpanException {
		NodePartition<T> partI = createPartition(variantSpan.start);
		if (partI.right == null) {
			return null;
		}

		NodePartition<T> partJ = createPartition(variantSpan.width + 1, partI.right);
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
	protected Node<T> findSearchNode(Node<T> x, long width) {
		if (x == null) {
			return root;
		}
		if (x.weight > width) {
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
	Iterator<Node<T>> getAllLeafNodes() throws MalformedSpanException {
		Queue<Node<T>> queue = new LinkedList<Node<T>>();
		collectLeafNodes(root, queue);
		return queue.iterator();
	}

	@Override
	public TumblerAddress getHomeDocument() {
		return homeDocument;
	}

	@Override
	public List<T> getStreamElements() throws MalformedSpanException {
		List<T> elements = new ArrayList<>();
		Iterator<Node<T>> it = getAllLeafNodes();
		while (it.hasNext()) {
			Node<T> node = it.next();
			if (node.value != null) {
				elements.add(node.value);
			}
		}
		return elements;
	}

	@Override
	public List<T> getStreamElements(VariantSpan variantSpan) throws MalformedSpanException {
		Node<T> loNode = index(variantSpan.start, root, null);
		Node<T> searchNode = findSearchNode(loNode, variantSpan.start + variantSpan.width - 1);

		Displacement disp = new Displacement();
		addWeightsOfRightLeaningParentNodes(searchNode, disp);

		InvariantSpanCollector<T> collector = new InvariantSpanCollector<T>(disp.getValue());
		collector.collect(searchNode, variantSpan.start, variantSpan.start + variantSpan.width);

		List<T> elements = new ArrayList<>();

		Iterator<T> it = collector.queue.iterator();
		while (it.hasNext()) {
			T is = it.next();
			is.setHomeDocument(homeDocument);
			elements.add(is);
		}
		return elements;
	}

	@Override
	public List<VariantSpan> getVariantSpans(SpanElement targetSpan) throws MalformedSpanException {
		// TODO: scans very inefficient
		long index = 1;
		List<VariantSpan> vspans = new ArrayList<>();
		List<T> spans = getStreamElements();
		for (int i = 0; i < spans.size(); i++) {
			SpanElement span = (SpanElement) spans.get(i);
			long start = span.getStart();
			long end = start + span.getWidth();
			long start2 = targetSpan.getStart();
			long end2 = start2 + targetSpan.getWidth();
			if (intersects(start, end, start2, end2)) {
				long a = Math.max(0, start2 - start);
				long b = Math.max(0, end - end2);

				System.out.println("[" + a + "," + b + "]");
				System.out.println("Index = " + index + ", span width = " + span.getWidth());
				VariantSpan vs = new VariantSpan(index + a, span.getWidth() - b - a);
				vs.homeDocument = homeDocument.value;// TODO: transcluded
				vspans.add(vs);
			}

			index += span.getWidth();
		}
		return vspans;
	}

	@Override
	public T index(long characterPosition) {
		Node<T> idx = index(characterPosition, root, null);
		return idx == null ? null : idx.value;
	}

	private void insert(long i, Node<T> x) throws MalformedSpanException {
		if (i > characterCount() + 1) {
			throw new IndexOutOfBoundsException("Attempting to insert in illegal range: Current Max = "
					+ characterCount() + ", Attempted insert = " + i + " ,Node" + x);
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
		} else if (i == characterCount() + 1) {
			root = concat(root, x);
		} else {
			NodePartition<T> partition = createPartition(i);
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
	public void put(long characterPosition, T val) throws MalformedSpanException {
		if (val == null) {
			throw new IllegalArgumentException("invariant span is null");
		}

		if (characterPosition < 1) {
			throw new IndexOutOfBoundsException("put position must be greater than 0");
		}

		if (val.getWidth() < 1) {
			throw new MalformedSpanException("invariant span must have a width greater than 0");
		}

		insert(characterPosition, new Node<T>(val.getWidth(), val));
	}

	public void save(OutputStream os) throws MalformedSpanException, IOException {
		mapper.writeValue(os, Lists.newArrayList(getAllLeafNodes()));
	}

	@Override
	public void swap(VariantSpan v1, VariantSpan v2) throws MalformedSpanException {
		// assume v1 < v2, no overlap

		Node<T> from = deleteRange(v1);
		from.parent = null;

		long startV2 = v2.start - v1.width;

		Node<T> to = deleteRange(new VariantSpan(startV2, v2.width));
		to.parent = null;

		insert(v1.start, to);
		insert(v2.start, from);
	}

	@Override
	public void toggleOverlay(VariantSpan variantSpan, TumblerAddress linkType)
			throws MalformedSpanException, IOException {
		List<T> overlays = getStreamElements(variantSpan);
		if (addOverlay(linkType, overlays)) {
			for (StreamElement span : overlays) {
				OverlayElement overlay = (OverlayElement) span;
				overlay.addLinkType(linkType);
			}
		} else {
			for (StreamElement span : overlays) {
				OverlayElement overlay = (OverlayElement) span;
				overlay.removeLinkType(linkType);
			}
		}

		delete(variantSpan);
		putElements(variantSpan.start, overlays);
	}

}
