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

import static org.junit.Assert.assertEquals;
import static org.oulipo.streams.impl.NodeFactory.getE;
import static org.oulipo.streams.impl.NodeFactory.getF;
import static org.oulipo.streams.impl.NodeFactory.getK;

import org.junit.Test;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.impl.RopeVariantStream.Node;
import org.oulipo.streams.impl.RopeVariantStream.NodePartition;
import org.oulipo.streams.types.SpanElement;

/**
 * Internal tests for Rope Node
 */
public class RopeVariantStreamNodeTest {

	public static final TumblerAddress homeDocument = TumblerAddress.createWithNoException("ted://1.2.0.2.0.23.1.1");

	@Test
	public void createParition() throws Exception {
		Node<SpanElement> A = new Node<SpanElement>(2);
		Node<SpanElement> B = new Node<>(1);
		Node<SpanElement> C = new Node<>(1, new SpanElement(4, 1, homeDocument));
		Node<SpanElement> D = new Node<>(1, new SpanElement(2, 1, homeDocument));
		Node<SpanElement> E = new Node<>(1, new SpanElement(3, 1, homeDocument));
		
		A.tag = "A";
		B.tag = "B";
		C.tag = "C";
		D.tag = "D";
		E.tag = "E";
		
		A.left = B;
		A.right = C;		
		B.left = D;
		B.right =E;
		
		B.parent = A;
		C.parent = A;
		D.parent = B;
		E.parent = B;	
			
		NodePartition<SpanElement> result = RopeVariantStream.createPartition(3, A);
		
		System.out.println(result.left + ":" + result.right);
	}

	@Test
	public void split13() throws Exception {
		Node<SpanElement> result = getK().split(2);
		assertEquals(new SpanElement(300, 2, homeDocument), result.left.value);
		assertEquals(new SpanElement(302, 2, homeDocument), result.right.value);
	}

	@Test
	public void splitEdge() throws Exception {
		Node<SpanElement> result = getE().split(4);
		assertEquals(new SpanElement(100, 4, homeDocument), result.left.value);
		assertEquals(new SpanElement(104, 2, homeDocument), result.right.value);
	}

	@Test
	public void splitF() throws Exception {
		Node<SpanElement> result = getF().split(2);
		assertEquals(new SpanElement(200, 2, homeDocument), result.left.value);
		assertEquals(new SpanElement(202, 1, homeDocument), result.right.value);
	}
	
	@Test
	public void splitMinEdge() throws Exception {
		Node<SpanElement> result = getE().split(1);
		assertEquals(new SpanElement(100, 1, homeDocument), result.left.value);
		assertEquals(new SpanElement(101, 5, homeDocument), result.right.value);
	}

	

}
