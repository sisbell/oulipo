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
import org.oulipo.streams.Span;
import org.oulipo.streams.impl.RopeVariantStream.Node;

/**
 * Internal tests for Rope Node
 */
public class RopeVariantStreamNodeTest {

	@Test
	public void split13() throws Exception {
		Node result = getK().split(2);
		assertEquals(new Span(300, 2), result.left.value);
		assertEquals(new Span(302, 2), result.right.value);
	}

	@Test
	public void splitEdge() throws Exception {
		Node result = getE().split(4);
		assertEquals(new Span(100, 4), result.left.value);
		assertEquals(new Span(104, 2), result.right.value);
	}

	@Test
	public void splitF() throws Exception {
		Node result = getF().split(2);
		assertEquals(new Span(200, 2), result.left.value);
		assertEquals(new Span(202, 1), result.right.value);
	}

	@Test
	public void splitMinEdge() throws Exception {
		Node result = getE().split(1);
		assertEquals(new Span(100, 1), result.left.value);
		assertEquals(new Span(101, 5), result.right.value);
	}
}
