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
import org.oulipo.streams.Span;
import org.oulipo.streams.impl.RopeVariantStream.Node;
import org.oulipo.streams.impl.RopeVariantStream.NodePartition;

/**
 * Internal tests for Rope Node
 */
public class RopeVariantStreamNodeTest {

	public static final TumblerAddress homeDocument = TumblerAddress.createWithNoException("ted://1.2.0.2.0.23.1.1");

	@Test
	public void split13() throws Exception {
		Node result = getK().split(2);
		assertEquals(new Span(300, 2, homeDocument), result.left.value);
		assertEquals(new Span(302, 2, homeDocument), result.right.value);
	}

	@Test
	public void splitEdge() throws Exception {
		Node result = getE().split(4);
		assertEquals(new Span(100, 4, homeDocument), result.left.value);
		assertEquals(new Span(104, 2, homeDocument), result.right.value);
	}

	@Test
	public void splitF() throws Exception {
		Node result = getF().split(2);
		assertEquals(new Span(200, 2, homeDocument), result.left.value);
		assertEquals(new Span(202, 1, homeDocument), result.right.value);
	}

	@Test
	public void splitMinEdge() throws Exception {
		Node result = getE().split(1);
		assertEquals(new Span(100, 1, homeDocument), result.left.value);
		assertEquals(new Span(101, 5, homeDocument), result.right.value);
	}
	
	@Test
	public void createParition() throws Exception {
		Node A = new Node(2);
		Node B = new Node(1);
		Node C = new Node(1, new Span(4, 1, homeDocument));
		Node D = new Node(1, new Span(2, 1, homeDocument));
		Node E = new Node(1, new Span(3, 1, homeDocument));
		
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
			
		NodePartition result = RopeVariantStream.createPartition(3, A);
		
		System.out.println(result.left + ":" + result.right);
	}

	

}
