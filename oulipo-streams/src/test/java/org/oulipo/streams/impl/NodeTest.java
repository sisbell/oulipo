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
import static org.junit.Assert.assertTrue;
import static org.oulipo.streams.impl.NodeFactory.getE;
import static org.oulipo.streams.impl.NodeFactory.getF;
import static org.oulipo.streams.impl.NodeFactory.getK;

import org.junit.Test;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.types.InvariantSpan;

public class NodeTest {

	public static final TumblerAddress homeDocument = TumblerAddress.createWithNoException("ted://1.2.0.2.0.23.1.1");

	@Test
	public void isRight() throws Exception {
		Node<InvariantSpan> right = new Node<InvariantSpan>(new InvariantSpan(1, 10, homeDocument));
		new Node.Builder<InvariantSpan>(0).right(right).build();
		assertTrue(right.isRightNode());
		assertTrue(right.isLeaf());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void negativeWeight() {
		new Node<InvariantSpan>(-1);
	}

	@Test
	public void root() throws Exception {
		assertTrue(new Node<InvariantSpan>(10).isRoot());
	}

	@Test
	public void simpleSplit() throws Exception {
		Node<InvariantSpan> node = new Node<InvariantSpan>(new InvariantSpan(1, 10, homeDocument));

		Node<InvariantSpan> result = node.split(5);
		assertNotNull(result.left);
		assertNotNull(result.right);

		assertEquals(new InvariantSpan(1, 5, homeDocument), result.left.value);
		assertEquals(new InvariantSpan(6, 5, homeDocument), result.right.value);
	}

	@Test
	public void split13() throws Exception {
		Node<InvariantSpan> result = getK().split(2);
		assertEquals(new InvariantSpan(300, 2, homeDocument), result.left.value);
		assertEquals(new InvariantSpan(302, 2, homeDocument), result.right.value);
	}

	@Test(expected = MalformedSpanException.class)
	public void splitBranch() throws Exception {
		new Node<InvariantSpan>(10).split(5);
	}

	@Test
	public void splitEdge() throws Exception {
		Node<InvariantSpan> result = getE().split(4);
		assertEquals(new InvariantSpan(100, 4, homeDocument), result.left.value);
		assertEquals(new InvariantSpan(104, 2, homeDocument), result.right.value);
	}

	@Test
	public void splitF() throws Exception {
		Node<InvariantSpan> result = getF().split(2);
		assertEquals(new InvariantSpan(200, 2, homeDocument), result.left.value);
		assertEquals(new InvariantSpan(202, 1, homeDocument), result.right.value);
	}

	@Test
	public void splitMinEdge() throws Exception {
		Node<InvariantSpan> result = getE().split(1);
		assertEquals(new InvariantSpan(100, 1, homeDocument), result.left.value);
		assertEquals(new InvariantSpan(101, 5, homeDocument), result.right.value);
	}

	@Test(expected = IllegalArgumentException.class)
	public void splitNegative() throws Exception {
		Node<InvariantSpan> node = new Node<InvariantSpan>(new InvariantSpan(1, 10, homeDocument));
		node.split(-1);
	}

}
