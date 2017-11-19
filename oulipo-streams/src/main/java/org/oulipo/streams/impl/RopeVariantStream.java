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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.overlays.Overlay;
import org.oulipo.streams.types.InvariantSpan;
import org.oulipo.streams.types.OverlayStream;
import org.oulipo.streams.types.StreamElement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/**
 * Rope implementation of the <code>VariantStream</code>
 */
public final class RopeVariantStream<T extends StreamElement> implements VariantStream<T> {

	/**
	 * Home document of invariant spans
	 */
	private final String documentHash;

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
	public RopeVariantStream(String documentHash) {
		this.documentHash = documentHash;
	}

	/**
	 * Constructs a <code>RopeVariantStream</code> with a pre-constructed tree.
	 * 
	 * @param homeDocument
	 *            the home document of invariant spans
	 * @param root
	 *            the root node
	 */
	public RopeVariantStream(String documentHash, Node<T> root) {
		this.documentHash = documentHash;
		this.root = root;
	}

	private boolean addOverlay(Overlay link, List<T> overlays) {
		for (T overlaySpan : overlays) {
			if ((overlaySpan instanceof OverlayStream) && !((OverlayStream) overlaySpan).hasLinkType(link)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void copy(long characterPosition, VariantSpan variantSpan) throws MalformedSpanException, IOException {
		putElements(characterPosition, getStreamElements(variantSpan));
	}

	@Override
	public void delete(VariantSpan variantSpan) throws MalformedSpanException {
		if (variantSpan == null) {
			throw new MalformedSpanException("Variant span is null for delete operation");
		}
		deleteRange(variantSpan);
	}

	/*
	 * Delete InvariantSpans in the variant span range. This partitions the variant
	 * span twice. First along the start position of the variant span. Then it takes
	 * the right half of the partition and splits again at the point
	 * variantSpan.width + 1.
	 * 
	 * @return returns Node containing deleted portion
	 * 
	 */
	private Node<T> deleteRange(VariantSpan variantSpan) throws MalformedSpanException {
		NodePartition<T> partI = null;

		if (variantSpan.start == RopeUtils.characterCount(root)) {
			partI = Partitioner.createNodePartition(variantSpan.start - 1, root);
			root = partI.left;
			return partI.right;
		}

		/**
		 * Shift partition index left by one, to push target span to right partition
		 */
		partI = Partitioner.createNodePartition(variantSpan.start - 1, root);

		if (partI.right == null) {
			return null;
		}

		NodePartition<T> partJ = Partitioner.createNodePartition(variantSpan.width, partI.right);

		if (partI.left == null && partJ.right == null) {
			root = null;
			return partI.right;
		} else if (partI.left == null) {
			root = partJ.right;
		} else if (partJ.right == null) {
			root = partI.left;
		} else {
			root = RopeUtils.concat(partI.left, partJ.right);
		}

		return partJ.left;
	}

	/**
	 * Gets leaf nodes with invariant span values
	 * 
	 * @return
	 * @throws MalformedSpanException
	 */
	Iterator<Node<T>> getAllLeafNodes() throws MalformedSpanException {
		Queue<Node<T>> queue = new LinkedList<Node<T>>();
		RopeUtils.collectLeafNodes(root, queue);
		return queue.iterator();
	}

	@Override
	public String getDocumentHash() {
		return documentHash;
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
		if (root == null) {
			throw new IllegalStateException("Stream is empty");
		}
		Node<T> loNode = RopeUtils.index(variantSpan.start, root, 0).node;
		Node<T> searchNode = RopeUtils.findSearchNode(loNode, variantSpan.start + variantSpan.width - 1, root);

		InvariantSpanCollector<T> collector = new InvariantSpanCollector<T>(
				RopeUtils.addWeightsOfRightLeaningParentNodes(searchNode));
		collector.collect(searchNode, variantSpan.start, variantSpan.start + variantSpan.width);

		List<T> elements = new ArrayList<>();

		Iterator<T> it = collector.iterator();
		while (it.hasNext()) {
			T is = it.next();
			elements.add(is);
		}
		return elements;
	}

	@Override
	public List<VariantSpan> getVariantSpans(InvariantSpan targetSpan) throws MalformedSpanException {
		// TODO: scans very inefficient
		long index = 1;
		List<VariantSpan> vspans = new ArrayList<>();
		List<T> spans = getStreamElements();
		for (int i = 0; i < spans.size(); i++) {
			T element = spans.get(i);
			long targetStart = targetSpan.getStart();
			long targetEnd = targetStart + targetSpan.getWidth();

			if (!(element instanceof InvariantSpan)) {
				// InvariantMedia media = (InvariantMedia) element;
				continue;
			}
			InvariantSpan span = (InvariantSpan) element;
			long start = span.getStart();
			long end = start + span.getWidth();

			if (RopeUtils.intersects(start, end, targetStart, targetEnd)) {
				long a = Math.max(0, targetStart - start);
				long b = Math.max(0, end - targetEnd);

				VariantSpan vs = new VariantSpan(index + a, span.getWidth() - b - a);
				vs.documentHash = documentHash;// TODO: transcluded
				vspans.add(vs);
			}

			index += span.getWidth();
		}
		return vspans;
	}

	@Override
	public T index(long characterPosition) {
		if (root == null) {
			throw new IllegalStateException("Stream is empty");
		}
		return RopeUtils.index(characterPosition, root, 0).node.value;
	}

	/**
	 * Inserts specified node x at the specified position 'i'. If 'i' is greater
	 * than the number of characters in the rope, then the node is concatenated to
	 * the root node.
	 * 
	 * If i is less than the number of characters in the rope, then two partition
	 * and two concat operations are performed as part of the insert.
	 * 
	 * @param i
	 *            the position to insert to
	 * @param x
	 *            the node to insert
	 * @throws MalformedSpanException
	 * @throws IndexOutOfBounds
	 *             if rope is empty and the specified node's weight < 1 OR if i >
	 *             rope.characterCount + 1
	 * @throws IllegalArgumentException
	 *             if rope is empty AND either one of the conditions is true : i !=
	 *             1 OR x.value == null
	 */
	private void insert(long i, Node<T> x) throws MalformedSpanException {
		if (x == null) {
			throw new IllegalStateException("Inserting a null node");
		}
		long charCount = RopeUtils.characterCount(root);
		if (i > charCount + 1) {
			throw new IndexOutOfBoundsException("Attempting to insert in illegal range: Current Max = " + charCount
					+ ", Attempted insert = " + i + " ,Node" + x);
		}

		if (root == null) {
			if (i != 1) {
				throw new IllegalArgumentException("First node must be inserted at position 1");
			}
			if (x.value == null) {
				throw new IllegalArgumentException("First inserted node must have a span value");
			}
			if (x.weight < 1) {
				throw new IndexOutOfBoundsException("Attempting to assign root with weight less than 1");
			}
			root = x;
		} else if (i == charCount + 1) {
			root = RopeUtils.concat(root, x);
		} else {
			NodePartition<T> partition = Partitioner.createNodePartition(i - 1, root);
			Node<T> tmp = partition.left != null ? RopeUtils.concat(partition.left, x) : x;
			root = (partition.right == null) ? tmp : RopeUtils.concat(tmp, partition.right);
		}
	}

	@Override
	public void move(long to, VariantSpan v1) throws MalformedSpanException {
		Node<T> deletedRange = deleteRange(v1);
		if (v1.start < to) {
			to -= v1.width;
		}
		insert(to, deletedRange);
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

		insert(characterPosition, new Node<T>(val));
	}

	@Override
	public void rebalance() throws MalformedSpanException {
		root = RopeUtils.rebalance(root);
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
	public void toggleOverlay(VariantSpan variantSpan, Overlay linkType) throws MalformedSpanException, IOException {
		List<T> overlays = getStreamElements(variantSpan);
		if (addOverlay(linkType, overlays)) {
			for (StreamElement span : overlays) {
				OverlayStream overlayStream = (OverlayStream) span;
				overlayStream.addLinkType(linkType);
			}
		} else {
			for (StreamElement span : overlays) {
				OverlayStream overlayStream = (OverlayStream) span;
				overlayStream.removeLinkType(linkType);
			}
		}

		delete(variantSpan);
		putElements(variantSpan.start, overlays);
	}
}
