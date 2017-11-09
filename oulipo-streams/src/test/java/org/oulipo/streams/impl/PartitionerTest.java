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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.oulipo.streams.impl.NodeFactory.getA;

import java.util.List;

import org.junit.Test;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.types.InvariantSpan;

public class PartitionerTest {

	public static final TumblerAddress homeDocument = TumblerAddress.createWithNoException("ted://1.2.0.2.0.23.1.1");

	@Test
	public void createNodeParitionAlongRightCut() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 10, homeDocument));
		Node<InvariantSpan> right = new Node<>(new InvariantSpan(1, 20, homeDocument));

		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(10).left(left).right(right).build();
		NodePartition<InvariantSpan> part = Partitioner.createNodePartition(10, root);

		assertEquals(new InvariantSpan(1, 10, homeDocument), part.left.left.value);
		assertEquals(new InvariantSpan(1, 20, homeDocument), part.right.value);
	}

	@Test
	public void createNodePartitionLeafRoot() throws Exception {
		Node<InvariantSpan> root = new Node<InvariantSpan>(new InvariantSpan(1, 10, homeDocument));
		NodePartition<InvariantSpan> part = Partitioner.createNodePartition(5, root);
		assertEquals(new InvariantSpan(1, 5, homeDocument), part.left.value);
		assertEquals(new InvariantSpan(6, 5, homeDocument), part.right.value);
	}

	@Test
	public void createNodePartitionLinearList() throws Exception {

		Node<InvariantSpan> a = new Node<>(new InvariantSpan(1, 1, homeDocument));
		Node<InvariantSpan> b = new Node<>(new InvariantSpan(2, 1, homeDocument));
		Node<InvariantSpan> c = new Node<>(new InvariantSpan(3, 1, homeDocument));
		Node<InvariantSpan> d = new Node<>(new InvariantSpan(4, 1, homeDocument));
		Node<InvariantSpan> e = new Node<>(new InvariantSpan(5, 1, homeDocument));
		Node<InvariantSpan> f = new Node<>(new InvariantSpan(6, 1, homeDocument));
		Node<InvariantSpan> g = new Node<>(new InvariantSpan(7, 1, homeDocument));
		Node<InvariantSpan> h = new Node<>(new InvariantSpan(8, 1, homeDocument));

		Node<InvariantSpan> ab = new Node.Builder<InvariantSpan>(1).left(a).right(b).build();
		Node<InvariantSpan> ac = new Node.Builder<InvariantSpan>(2).left(ab).right(c).build();
		Node<InvariantSpan> ad = new Node.Builder<InvariantSpan>(3).left(ac).right(d).build();

		Node<InvariantSpan> ae = new Node.Builder<InvariantSpan>(4).left(ad).right(e).build();
		Node<InvariantSpan> af = new Node.Builder<InvariantSpan>(5).left(ae).right(f).build();
		Node<InvariantSpan> ag = new Node.Builder<InvariantSpan>(6).left(af).right(g).build();
		Node<InvariantSpan> ah = new Node.Builder<InvariantSpan>(7).left(ag).right(h).build();

		// Node<InvariantSpan> rebalance = RopeUtils.rebalance(ah);
		// System.out.println(rebalance);

		NodePartition<InvariantSpan> part = Partitioner.createNodePartition(2, ah);

		// assertEquals(new InvariantSpan(1, 10, homeDocument), part.left.left.value);
		// assertEquals(new InvariantSpan(1, 20, homeDocument), part.right.value);
	}

	@Test
	public void createNodePartitionLinearList2() throws Exception {

		Node<InvariantSpan> a = new Node<>(new InvariantSpan(1, 1, homeDocument));
		Node<InvariantSpan> b = new Node<>(new InvariantSpan(2, 1, homeDocument));
		Node<InvariantSpan> c = new Node<>(new InvariantSpan(3, 1, homeDocument));
		Node<InvariantSpan> d = new Node<>(new InvariantSpan(4, 1, homeDocument));

		Node<InvariantSpan> ab = new Node.Builder<InvariantSpan>(1).left(a).right(b).build();
		Node<InvariantSpan> ac = new Node.Builder<InvariantSpan>(2).left(ab).right(c).build();
		Node<InvariantSpan> ac_dummy = new Node.Builder<InvariantSpan>(3).left(ac).build();

		Node<InvariantSpan> ad = new Node.Builder<InvariantSpan>(3).left(ac_dummy).right(d).build();
		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(4).left(ad).build();

		NodeIndex<InvariantSpan> e = RopeUtils.index(3, root, 0);
		List<Node<InvariantSpan>> orphans = Partitioner.pruneIndexNode(e.node, 3, e.displacement);
	}

	@Test
	public void createNodePartitionMiddle() throws Exception {
		Node<InvariantSpan> a = new Node<>(new InvariantSpan(1, 1, homeDocument));
		Node<InvariantSpan> b = new Node<>(new InvariantSpan(2, 1, homeDocument));
		Node<InvariantSpan> c = new Node<>(new InvariantSpan(3, 1, homeDocument));
		Node<InvariantSpan> d = new Node<>(new InvariantSpan(4, 1, homeDocument));

		Node<InvariantSpan> ab = new Node.Builder<InvariantSpan>(1).left(a).right(b).build();
		Node<InvariantSpan> abc = new Node.Builder<InvariantSpan>(2).left(ab).right(c).build();
		Node<InvariantSpan> abcd = new Node.Builder<InvariantSpan>(3).left(abc).right(d).build();

		NodePartition<InvariantSpan> part = Partitioner.createNodePartition(1, abcd);

		Node<InvariantSpan> leftNode = RopeUtils.index(1, part.left, 0).node;
		assertEquals(new InvariantSpan(1, 1, homeDocument), leftNode.value);
		assertEquals(new InvariantSpan(2, 1, homeDocument), RopeUtils.index(1, part.right, 0).node.value);
		assertEquals(new InvariantSpan(3, 1, homeDocument), RopeUtils.index(2, part.right, 0).node.value);
		assertEquals(new InvariantSpan(4, 1, homeDocument), RopeUtils.index(3, part.right, 0).node.value);
	}

	@Test
	public void createNodePartitionRootBadPosition() throws Exception {
		NodePartition<InvariantSpan> part = Partitioner.createNodePartition(0,
				new Node<InvariantSpan>(new InvariantSpan(1, 10, homeDocument)));
		assertEquals(new InvariantSpan(1, 10, homeDocument), part.right.value);
		assertNull(part.left);
	}

	@Test
	public void createNodePartitionRootBeyondRangePutLeft() throws Exception {
		NodePartition<InvariantSpan> part = Partitioner.createNodePartition(12,
				new Node<InvariantSpan>(new InvariantSpan(1, 10, homeDocument)));
		assertEquals(new InvariantSpan(1, 10, homeDocument), part.left.value);
		assertNull(part.right);
	}

	@Test
	public void createNodePartitionWithRightLeaningIndexNode() throws Exception {
		Node<InvariantSpan> a = new Node<>(new InvariantSpan(2, 1, homeDocument));
		Node<InvariantSpan> b = new Node<>(new InvariantSpan(3, 1, homeDocument));
		Node<InvariantSpan> c = new Node<>(new InvariantSpan(4, 1, homeDocument));

		Node<InvariantSpan> ab = new Node.Builder<InvariantSpan>(1).left(a).right(b).build();
		Node<InvariantSpan> abc = new Node.Builder<InvariantSpan>(2).left(ab).right(c).build();

		NodePartition<InvariantSpan> part = Partitioner.createNodePartition(2, abc);

		assertEquals(new InvariantSpan(2, 1, homeDocument), RopeUtils.index(1, part.left, 0).node.value);
		assertEquals(new InvariantSpan(3, 1, homeDocument), RopeUtils.index(2, part.left, 0).node.value);
		assertEquals(new InvariantSpan(4, 1, homeDocument), RopeUtils.index(3, part.right, 0).node.value);
	}

	@Test
	public void createPartition() throws Exception {
		Node<InvariantSpan> A = new Node<InvariantSpan>(2);
		Node<InvariantSpan> B = new Node<>(1);
		Node<InvariantSpan> C = new Node<>(new InvariantSpan(4, 1, homeDocument));
		Node<InvariantSpan> D = new Node<>(new InvariantSpan(2, 1, homeDocument));
		Node<InvariantSpan> E = new Node<>(new InvariantSpan(3, 1, homeDocument));

		A.tag = "A";
		B.tag = "B";
		C.tag = "C";
		D.tag = "D";
		E.tag = "E";

		A.left = B;
		A.right = C;
		B.left = D;
		B.right = E;

		B.parent = A;
		C.parent = A;
		D.parent = B;
		E.parent = B;

		NodePartition<InvariantSpan> result = Partitioner.createNodePartition(2, A);

		// System.out.println(result.left + ":" + result.right);
	}

	@Test(expected = IllegalStateException.class)
	public void partitionNullNode() throws Exception {
		Partitioner.createNodePartition(2, null);
	}

	@Test(expected = IllegalStateException.class)
	public void partitionNullRoot() throws Exception {
		Partitioner.createNodePartition(2, null);
	}

	@Test
	public void prune() throws Exception {
		Node<InvariantSpan> a = new Node<>(new InvariantSpan(300, 4, homeDocument));
		Node<InvariantSpan> b = new Node<>(new InvariantSpan(350, 1, homeDocument));
		Node<InvariantSpan> c = new Node<>(new InvariantSpan(360, 1, homeDocument));
		Node<InvariantSpan> d = new Node<>(new InvariantSpan(361, 5, homeDocument));
		d.tag = "d";

		Node<InvariantSpan> cd = new Node.Builder<InvariantSpan>(1).left(c).right(d).tag("cd").build();
		Node<InvariantSpan> bcd = new Node.Builder<InvariantSpan>(1).left(b).right(cd).tag("bcd").build();
		Node<InvariantSpan> abcd = new Node.Builder<InvariantSpan>(4).left(a).right(bcd).tag("abcd").build();

		NodeIndex<InvariantSpan> cIndex = RopeUtils.index(6, abcd, 0);
		List<Node<InvariantSpan>> orphans = Partitioner.pruneIndexNode(cIndex.node, 6, cIndex.displacement);

		assertEquals(1, orphans.size());
		assertEquals("d", orphans.get(0).tag);
	}

	@Test
	public void prune11() throws Exception {
		Node<InvariantSpan> root = getA();
		NodeIndex<InvariantSpan> j = RopeUtils.index(11, root, 0);
		List<Node<InvariantSpan>> orphans = Partitioner.pruneIndexNode(j.node, 11, j.displacement);

		assertEquals(2, orphans.size());
		assertEquals("K", orphans.get(0).tag);
		assertEquals("H", orphans.get(1).tag);

		Node<InvariantSpan> resultRoot = RopeUtils.findRoot(j.node);
		assertEquals(9, resultRoot.left.weight);
		assertEquals(11, resultRoot.weight);
	}

	@Test
	public void prune6() throws Exception {
		Node<InvariantSpan> root = getA();
		NodeIndex<InvariantSpan> e = RopeUtils.index(6, root, 0);
		List<Node<InvariantSpan>> orphans = Partitioner.pruneIndexNode(e.node, 6, e.displacement);

		assertEquals(2, orphans.size());
		assertEquals("F", orphans.get(0).tag);
		assertEquals("D", orphans.get(1).tag);
		assertEquals(6, root.left.weight);
		assertEquals(6, root.weight);
	}

	@Test
	public void prune9() throws Exception {
		Node<InvariantSpan> root = getA();
		NodeIndex<InvariantSpan> f = RopeUtils.index(9, root, 0);
		List<Node<InvariantSpan>> orphans = Partitioner.pruneIndexNode(f.node, 9, f.displacement);

		assertEquals(1, orphans.size());
		assertEquals("D", orphans.get(0).tag);
		assertEquals(9, root.weight);
	}

	@Test
	public void pruneNullLeft() throws Exception {
		Node<InvariantSpan> right = new Node<>(new InvariantSpan(2, 1, homeDocument));
		new Node.Builder<InvariantSpan>(0).right(right).build();

		List<Node<InvariantSpan>> orphans = Partitioner.pruneIndexNode(right, 1, 0);
		assertEquals(0, orphans.size());
	}

	@Test
	public void pruneNullRight() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 1, homeDocument));
		new Node.Builder<InvariantSpan>(1).left(left).build();

		List<Node<InvariantSpan>> orphans = Partitioner.pruneIndexNode(left, 1, 0);

		assertEquals(0, orphans.size());
	}

	@Test
	public void pruneRight() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 1, homeDocument));

		Node<InvariantSpan> right = new Node<>(1);
		right.left = new Node<InvariantSpan>(new InvariantSpan(50, 1, homeDocument));
		right.right = new Node<InvariantSpan>(new InvariantSpan(100, 1, homeDocument));
		right.tag = "RGHT";

		new Node.Builder<InvariantSpan>(1).left(left).right(right).build();

		List<Node<InvariantSpan>> orphans = Partitioner.pruneIndexNode(left, 1, 0);

		assertEquals(1, orphans.size());
		assertEquals("RGHT", orphans.get(0).tag);
	}

	@Test
	public void pruneRight2() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 4, homeDocument));
		Node<InvariantSpan> right = new Node<>(new InvariantSpan(1, 20, homeDocument));

		new Node.Builder<InvariantSpan>(4).left(left).right(right).build();

		List<Node<InvariantSpan>> orphans = Partitioner.pruneIndexNode(left, 4, 0);

		assertEquals(1, orphans.size());
		assertEquals(new InvariantSpan(1, 20, homeDocument), orphans.get(0).value);
	}
	/*
	 * @Test public void cutBranch() throws Exception { Node<InvariantSpan> left =
	 * new Node<>(new InvariantSpan(1, 1, homeDocument)); Node<InvariantSpan> right
	 * = new Node<>(new InvariantSpan(2, 3, homeDocument));
	 * 
	 * Node<InvariantSpan> root = new
	 * Node.Builder<InvariantSpan>(1).left(left).right(right).build();
	 * List<Node<InvariantSpan>> orphans = new ArrayList<>(); Node<InvariantSpan>
	 * split = Partitioner.splitBranch(2, root, orphans);
	 * 
	 * // assertEquals(new InvariantSpan(1,1, homeDocument), split.left.value);
	 * assertEquals(new InvariantSpan(2,3, homeDocument), split.right.value); }
	 * 
	 * 
	 * @Test public void cutBranchFirstCharacter() throws Exception {
	 * Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 1, homeDocument));
	 * Node<InvariantSpan> right = new Node<>(new InvariantSpan(2, 3,
	 * homeDocument));
	 * 
	 * Node<InvariantSpan> root = new
	 * Node.Builder<InvariantSpan>(1).left(left).right(right).build();
	 * List<Node<InvariantSpan>> orphans = new ArrayList<>(); Node<InvariantSpan>
	 * split = Partitioner.splitBranch(1, root, orphans);
	 * 
	 * assertEquals(new InvariantSpan(1,1, homeDocument), split.left.value);
	 * assertEquals(new InvariantSpan(2,3, homeDocument), orphans.get(0).value); }
	 */

	@Test
	public void pruneRightWithNoChildLeft() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 1, homeDocument));

		Node<InvariantSpan> right = new Node<>(1);
		right.right = new Node<InvariantSpan>(new InvariantSpan(100, 1, homeDocument));
		right.tag = "RGHT";

		new Node.Builder<InvariantSpan>(1).left(left).right(right).build();

		List<Node<InvariantSpan>> orphans = Partitioner.pruneIndexNode(left, 1, 0);

		assertEquals(1, orphans.size());
		assertEquals("RGHT", orphans.get(0).tag);
		assertNull(orphans.get(0).left);
		assertNotNull(orphans.get(0).right);

	}

	@Test
	public void pruneSimpleRight() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 1, homeDocument));
		Node<InvariantSpan> right = new Node<>(new InvariantSpan(2, 1, homeDocument));

		new Node.Builder<InvariantSpan>(1).left(left).right(right).build();

		List<Node<InvariantSpan>> orphans = Partitioner.pruneIndexNode(right, 2, 1);

		assertEquals(0, orphans.size());
	}

	/*
	 * 
	 * @Test public void splitBranchSplitLeft() throws Exception {
	 * Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 10,
	 * homeDocument)); Node<InvariantSpan> right = new Node<>(new InvariantSpan(1,
	 * 20, homeDocument));
	 * 
	 * Node<InvariantSpan> root = new
	 * Node.Builder<InvariantSpan>(10).left(left).right(right).build();
	 * List<Node<InvariantSpan>> orphans = new ArrayList<>();
	 * Partitioner.splitBranch(4, root, orphans);
	 * 
	 * assertNull(root.right); //assertEquals(4, root.weight); assertEquals(new
	 * InvariantSpan(1, 4, homeDocument), root.left.value);
	 * 
	 * assertEquals(2, orphans.size()); assertEquals(new InvariantSpan(5, 6,
	 * homeDocument), orphans.get(0).value); assertEquals(new InvariantSpan(1, 20,
	 * homeDocument), orphans.get(1).value); }
	 */

}
