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

import org.oulipo.net.MalformedSpanException;
import org.oulipo.streams.InvariantSpan;
import org.oulipo.streams.impl.RopeVariantStream.Node;

final class NodeFactory {

	static Node getA() throws MalformedSpanException {
		return createTestNode();
	}
	
	static Node getB() throws MalformedSpanException {
		return createTestNode().left;
	}
	
	static Node getK() throws MalformedSpanException {
		return createTestNode().left.right.left.right;
	}
	
	static Node getE() throws MalformedSpanException {
		return createTestNode().left.left.left;
	}
	
	static Node getF() throws MalformedSpanException {
		return createTestNode().left.left.right;
	}
	
	static Node getH() throws MalformedSpanException {
		return createTestNode().left.right.right;
	}
	
	static Node getG() throws MalformedSpanException {
		return createTestNode().left.right.left;
	}
	
	public static Node createTestNode2() throws MalformedSpanException {
		Node A = new Node(3);
		Node B = new Node(3, 200, 3);
		Node C = new Node(6);
		Node G = getG();
		Node H = getH();
		
		A.tag = "A";
		B.tag = "B";
		C.tag = "C";
		
		B.parent = A;
		A.left = B;
		C.parent = A;
		A.right = C;
		C.left = G;
		C.right = H;
		G.parent = C;
		H.parent = C;
		
		return A;
	}
	
	private static Node createTestNode() throws MalformedSpanException {
		Node A = new Node(22);
		Node B = new Node(9);
		Node C = new Node(6);
		Node D = new Node(6);
		Node E = new Node(6, new InvariantSpan(100, 6));
		Node F = new Node(3, new InvariantSpan(200, 3));
		Node G = new Node(2);
		Node H = new Node(1);
		Node J = new Node(2, new InvariantSpan(250, 2));
		Node K = new Node(4, new InvariantSpan(300, 4));
		Node M = new Node(1, new InvariantSpan(350, 1));
		Node N = new Node(6, new InvariantSpan(360, 6));
		
		A.tag = "A";
		B.tag = "B";
		C.tag = "C";
		D.tag = "D";
		E.tag = "E";
		F.tag = "F";
		G.tag = "G";
		H.tag = "H";
		J.tag = "J";
		K.tag = "K";
		M.tag = "M";
		N.tag = "N";
		
		A.left = B;
		B.parent = A;
		B.left = C;
		B.right = D;
		C.parent = B;
		D.parent = B;
		C.left = E;
		C.right = F;
		E.parent = C;
		F.parent = C;
		D.left = G;
		D.right = H;
		G.parent = D;
		H.parent = D;
		G.left = J;
		G.right = K;
		J.parent = G;
		K.parent = G;
		H.left = M;
		H.right = N;
		M.parent = H;
		N.parent = H;
		return A;
	}
	
}
