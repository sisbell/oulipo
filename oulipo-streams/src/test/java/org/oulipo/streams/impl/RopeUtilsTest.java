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

import org.junit.Test;
import org.oulipo.streams.types.InvariantSpan;

public class RopeUtilsTest {

	public static final String documentHash = "fakeHash";

	@Test
	public void addWeightsOfRightLeaningChildNodesChained() throws Exception {
		Node<InvariantSpan> right = new Node<>(new InvariantSpan(1, 10, documentHash));
		Node<InvariantSpan> right2 = new Node<>(new InvariantSpan(1, 20, documentHash));
		right.right = right2;

		Node<InvariantSpan> x = new Node.Builder<InvariantSpan>(0).right(right).build();
		assertEquals(30, RopeUtils.addWeightsOfRightLeaningChildNodes(x));
	}

	@Test
	public void addWeightsOfRightLeaningChildNodesLeftOnly() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 1, documentHash));
		Node<InvariantSpan> x = new Node.Builder<InvariantSpan>(1).left(left).build();

		assertEquals(0, RopeUtils.addWeightsOfRightLeaningChildNodes(x));
	}

	@Test
	public void addWeightsOfRightLeaningChildNodesNoChildren() throws Exception {
		Node<InvariantSpan> x = new Node<>(new InvariantSpan(1, 1, documentHash));
		assertEquals(0, RopeUtils.addWeightsOfRightLeaningChildNodes(x));
	}

	@Test
	public void addWeightsOfRightLeaningChildNodesNullNode() throws Exception {
		assertEquals(0, RopeUtils.addWeightsOfRightLeaningChildNodes(null));
	}

	@Test
	public void addWeightsOfRightLeaningChildNodesSimple() throws Exception {
		Node<InvariantSpan> right = new Node<>(new InvariantSpan(1, 10, documentHash));
		Node<InvariantSpan> x = new Node.Builder<InvariantSpan>(0).right(right).build();

		assertEquals(10, RopeUtils.addWeightsOfRightLeaningChildNodes(x));
	}

	@Test
	public void addWeightsOfRightLeaningParentNodesChainedRight() throws Exception {
		Node<InvariantSpan> right2 = new Node<>(new InvariantSpan(1, 3, documentHash));
		Node<InvariantSpan> right = new Node.Builder<InvariantSpan>(1).right(right2).build();
		new Node.Builder<InvariantSpan>(1).right(right).build();
		assertEquals(2, RopeUtils.addWeightsOfRightLeaningParentNodes(right2));
	}

	@Test
	public void addWeightsOfRightLeaningParentNodesLeftNodeChild() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 1, documentHash));
		new Node.Builder<InvariantSpan>(1).left(left).build();
		assertEquals(0, RopeUtils.addWeightsOfRightLeaningParentNodes(left));
	}

	@Test(expected = IllegalStateException.class)
	public void addWeightsOfRightLeaningParentNodesNull() throws Exception {
		RopeUtils.addWeightsOfRightLeaningParentNodes(null);
	}

	@Test
	public void addWeightsOfRightLeaningParentNodesNullParent() throws Exception {
		Node<InvariantSpan> x = new Node<>(new InvariantSpan(1, 1, documentHash));
		assertEquals(0, RopeUtils.addWeightsOfRightLeaningParentNodes(x));
	}

	@Test
	public void addWeightsOfRightLeaningParentNodesRightNodeChild() throws Exception {
		Node<InvariantSpan> right = new Node<>(new InvariantSpan(1, 1, documentHash));
		new Node.Builder<InvariantSpan>(1).right(right).build();
		assertEquals(1, RopeUtils.addWeightsOfRightLeaningParentNodes(right));
	}

	@Test
	public void characterCount() throws Exception {
		Node<InvariantSpan> right = new Node<>(new InvariantSpan(1, 10, documentHash));
		Node<InvariantSpan> right2 = new Node<>(new InvariantSpan(1, 20, documentHash));
		right.right = right2;

		Node<InvariantSpan> x = new Node.Builder<InvariantSpan>(10).right(right).build();

		assertEquals(40, RopeUtils.characterCount(x));
	}

	@Test
	public void characterCountLeafNode() throws Exception {
		assertEquals(3, RopeUtils.characterCount(new Node<>(new InvariantSpan(1, 3, documentHash))));
	}

	@Test
	public void characterCountNullNode() throws Exception {
		assertEquals(0, RopeUtils.characterCount(null));
	}

	@Test
	public void concatLeft() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 10, documentHash));
		Node<InvariantSpan> parent = RopeUtils.concat(left, null);

		assertEquals(10, parent.weight);
		assertNotNull(left.parent);
	}

	@Test
	public void concatNullLeftNullRight() throws Exception {
		assertEquals(0, RopeUtils.concat(null, null).weight);
	}

	@Test
	public void concatRight() throws Exception {
		Node<InvariantSpan> right = new Node<>(new InvariantSpan(1, 10, documentHash));
		Node<InvariantSpan> parent = RopeUtils.concat(null, right);
		assertEquals(0, parent.weight);
		assertNotNull(right.parent);

	}

	@Test
	public void findSearchNode() throws Exception {
		// RopeUtils.findSearchNode(x, weight, root)
	}

	@Test
	public void indexDisplacement() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 1, documentHash));
		Node<InvariantSpan> right = new Node<>(new InvariantSpan(2, 3, documentHash));

		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(1).left(left).right(right).build();
		NodeIndex<InvariantSpan> index = RopeUtils.index(2, root, 0);

		assertEquals(1, index.displacement);
	}

	@Test
	public void indexLeft() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 10, documentHash));
		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(10).left(left).build();
		NodeIndex<InvariantSpan> index = RopeUtils.index(5, root, 0);
		assertEquals(new InvariantSpan(1, 10, documentHash), index.node.value);
		assertEquals(0, index.displacement);
	}

	@Test
	public void indexLeftLeftChain() throws Exception {
		Node<InvariantSpan> left2 = new Node<>(new InvariantSpan(100, 10, documentHash));
		Node<InvariantSpan> right2 = new Node<>(new InvariantSpan(200, 5, documentHash));
		Node<InvariantSpan> left1 = new Node.Builder<InvariantSpan>(10).left(left2).right(right2).build();

		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(15).left(left1).build();
		NodeIndex<InvariantSpan> index = RopeUtils.index(10, root, 0);

		assertEquals(new InvariantSpan(100, 10, documentHash), index.node.value);
		assertEquals(0, index.displacement);
	}

	@Test
	public void indexLeftLower() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 10, documentHash));
		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(10).left(left).build();
		NodeIndex<InvariantSpan> index = RopeUtils.index(1, root, 0);

		assertEquals(new InvariantSpan(1, 10, documentHash), index.node.value);
		assertEquals(0, index.displacement);
	}

	@Test
	public void indexLeftRightChain() throws Exception {
		Node<InvariantSpan> left2 = new Node<>(new InvariantSpan(100, 10, documentHash));
		Node<InvariantSpan> right2 = new Node<>(new InvariantSpan(200, 5, documentHash));
		Node<InvariantSpan> left1 = new Node.Builder<InvariantSpan>(10).left(left2).right(right2).build();

		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(15).left(left1).build();
		NodeIndex<InvariantSpan> index = RopeUtils.index(11, root, 0);

		assertEquals(new InvariantSpan(200, 5, documentHash), index.node.value);
		assertEquals(10, index.displacement);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void indexLeftTooHigh() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 10, documentHash));
		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(10).left(left).build();
		RopeUtils.index(11, root, 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void indexLeftTooLow() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 10, documentHash));
		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(10).left(left).build();
		RopeUtils.index(0, root, 0);
	}

	@Test
	public void indexLeftUpper() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 10, documentHash));
		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(10).left(left).build();
		NodeIndex<InvariantSpan> index = RopeUtils.index(10, root, 0);

		assertEquals(new InvariantSpan(1, 10, documentHash), index.node.value);
		assertEquals(0, index.displacement);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void indexOutOutBoundsRight() throws Exception {
		Node<InvariantSpan> left2 = new Node<>(new InvariantSpan(100, 10, documentHash));
		Node<InvariantSpan> right2 = new Node<>(new InvariantSpan(200, 5, documentHash));

		Node<InvariantSpan> left1 = new Node.Builder<InvariantSpan>(10).left(left2).right(right2).build();

		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(15).left(left1).build();
		RopeUtils.index(20, root, 0);
	}

	@Test
	public void indexRight() throws Exception {
		Node<InvariantSpan> left = new Node<>(new InvariantSpan(1, 10, documentHash));
		Node<InvariantSpan> right = new Node<>(new InvariantSpan(1, 20, documentHash));

		Node<InvariantSpan> root = new Node.Builder<InvariantSpan>(10).left(left).right(right).build();
		NodeIndex<InvariantSpan> index = RopeUtils.index(15, root, 0);

		assertEquals(new InvariantSpan(1, 20, documentHash), index.node.value);
		assertEquals(10, index.displacement);
	}

}
