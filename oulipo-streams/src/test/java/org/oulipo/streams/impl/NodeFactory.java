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
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.impl.RopeVariantStream.Node;
import org.oulipo.streams.types.SpanElement;

final class NodeFactory {

	public static final TumblerAddress homeDocument = TumblerAddress.createWithNoException("ted://1.2.0.2.0.23.1.1");

	private static Node<SpanElement> createTestNode() throws MalformedSpanException {
		Node<SpanElement> A = new Node<>(22);
		Node<SpanElement> B = new Node<>(9);
		Node<SpanElement> C = new Node<>(6);
		Node<SpanElement> D = new Node<>(6);
		Node<SpanElement> E = new Node<>(6, new SpanElement(100, 6, homeDocument));
		Node<SpanElement> F = new Node<>(3, new SpanElement(200, 3, homeDocument));
		Node<SpanElement> G = new Node<>(2);
		Node<SpanElement> H = new Node<>(1);
		Node<SpanElement> J = new Node<>(2, new SpanElement(250, 2, homeDocument));
		Node<SpanElement> K = new Node<>(4, new SpanElement(300, 4, homeDocument));
		Node<SpanElement> M = new Node<>(1, new SpanElement(350, 1, homeDocument));
		Node<SpanElement> N = new Node<>(6, new SpanElement(360, 6, homeDocument));

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

	public static Node<SpanElement> createTestNode2() throws MalformedSpanException {
		Node<SpanElement> A = new Node<>(3);
		Node<SpanElement> B = new Node<>(3, new SpanElement(200, 3, homeDocument));
		Node<SpanElement> C = new Node<>(6);
		Node<SpanElement> G = getG();
		Node<SpanElement> H = getH();

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

	static Node<SpanElement> getA() throws MalformedSpanException {
		return createTestNode();
	}

	static Node<SpanElement> getB() throws MalformedSpanException {
		return createTestNode().left;
	}

	static Node<SpanElement> getE() throws MalformedSpanException {
		return createTestNode().left.left.left;
	}

	static Node<SpanElement> getF() throws MalformedSpanException {
		return createTestNode().left.left.right;
	}

	static Node<SpanElement> getG() throws MalformedSpanException {
		return createTestNode().left.right.left;
	}

	static Node<SpanElement> getH() throws MalformedSpanException {
		return createTestNode().left.right.right;
	}

	static Node<SpanElement> getK() throws MalformedSpanException {
		return createTestNode().left.right.left.right;
	}

}
