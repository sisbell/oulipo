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

import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.types.InvariantSpan;

public final class NodeFactory {

	public static final String documentHash = "fakeHash";

	private static Node<InvariantSpan> createTestNode() throws MalformedSpanException {
		Node<InvariantSpan> A = new Node<>(22);
		Node<InvariantSpan> B = new Node<>(9);
		Node<InvariantSpan> C = new Node<>(6);
		Node<InvariantSpan> D = new Node<>(6);
		Node<InvariantSpan> E = new Node<>(new InvariantSpan(100, 6, documentHash));
		Node<InvariantSpan> F = new Node<>(new InvariantSpan(200, 3, documentHash));
		Node<InvariantSpan> G = new Node<>(2);
		Node<InvariantSpan> H = new Node<>(1);
		Node<InvariantSpan> J = new Node<>(new InvariantSpan(250, 2, documentHash));
		Node<InvariantSpan> K = new Node<>(new InvariantSpan(300, 4, documentHash));
		Node<InvariantSpan> M = new Node<>(new InvariantSpan(350, 1, documentHash));
		Node<InvariantSpan> N = new Node<>(new InvariantSpan(360, 6, documentHash));

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

	public static Node<InvariantSpan> createTestNode2() throws MalformedSpanException {
		Node<InvariantSpan> A = new Node<>(3);
		Node<InvariantSpan> B = new Node<>(new InvariantSpan(200, 3, documentHash));
		Node<InvariantSpan> C = new Node<>(6);
		Node<InvariantSpan> G = getG();
		Node<InvariantSpan> H = getH();

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

	public static Node<InvariantSpan> getA() throws MalformedSpanException {
		return createTestNode();
	}

	public static Node<InvariantSpan> getB() throws MalformedSpanException {
		return createTestNode().left;
	}

	public static Node<InvariantSpan> getE() throws MalformedSpanException {
		return createTestNode().left.left.left;
	}

	public static Node<InvariantSpan> getF() throws MalformedSpanException {
		return createTestNode().left.left.right;
	}

	public static Node<InvariantSpan> getG() throws MalformedSpanException {
		return createTestNode().left.right.left;
	}

	public static Node<InvariantSpan> getH() throws MalformedSpanException {
		return createTestNode().left.right.right;
	}

	public static Node<InvariantSpan> getK() throws MalformedSpanException {
		return createTestNode().left.right.left.right;
	}

}
