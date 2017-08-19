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

import static org.junit.Assert.*;
import static org.oulipo.streams.impl.NodeFactory.getA;

import java.util.List;

import org.junit.Test;
import org.oulipo.streams.InvariantSpan;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.impl.RopeVariantStream;

/**
 * 
 * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
 */
public class RopeVariantStreamTest {
	
	@Test
	public void copy() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.copy(2, new VariantSpan(12, 3));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);		
	}

	
	@Test
	public void move() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.move(2, new VariantSpan(12, 3));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);		
	}
	
	//@Test
	//TODO: fix swap
	public void swap() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.swap(new VariantSpan(1, 3), new VariantSpan(12, 3));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);		

		assertEquals(new InvariantSpan(300, 3), spans.get(0));
		assertEquals(new InvariantSpan(103, 3), spans.get(1));
		assertEquals(new InvariantSpan(200, 3), spans.get(2));
		assertEquals(new InvariantSpan(250, 2), spans.get(3));
		assertEquals(new InvariantSpan(100, 3), spans.get(5));
		assertEquals(new InvariantSpan(303, 1), spans.get(4));
		assertEquals(new InvariantSpan(350, 1), spans.get(6));
		assertEquals(new InvariantSpan(360, 6), spans.get(7));

		assertEquals(8, spans.size());

		//[Span [start=300, width=3], Span [start=103, width=3], 
		//Span [start=200, width=3], Span [start=250, width=2], 
		//Span [start=303, width=1], Span [start=100, width=3], 
		//Span [start=303, width=1], Span [start=350, width=1], 
		//Span [start=360, width=6]]

	}

	
	@Test
	public void put() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.put(12, new InvariantSpan(500, 34));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);
		
	}
	@Test
	public void putWithSplit() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.put(5, new InvariantSpan(500, 34));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);
		
	}
	@Test
	public void delete() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.delete(new VariantSpan(12, 4));//have  orphan in list which shouldn't be there [M]
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);
		assertEquals(new InvariantSpan(100, 6), spans.get(0));
		assertEquals(new InvariantSpan(200, 3), spans.get(1));
		assertEquals(new InvariantSpan(250, 2), spans.get(2));
		assertEquals(new InvariantSpan(350, 1), spans.get(3));
		assertEquals(new InvariantSpan(360, 6), spans.get(4));

		assertEquals(5, spans.size());
	}
	
	@Test
	public void delete2() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.delete(new VariantSpan(12, 5));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);
		assertEquals(new InvariantSpan(100, 6), spans.get(0));
		assertEquals(new InvariantSpan(200, 3), spans.get(1));
		assertEquals(new InvariantSpan(250, 2), spans.get(2));
		assertEquals(new InvariantSpan(360, 6), spans.get(3));
		assertEquals(4, spans.size());
	}
	
	@Test
	public void delete6To19() throws Exception {//6, 19
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.delete(new VariantSpan(6, 19));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);
		assertEquals(new InvariantSpan(100, 5), spans.get(0));
		assertEquals(1, spans.size());
	}
	
	@Test
	public void deleteAll() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.delete(new VariantSpan(1, 22));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		assertEquals(0, spans.size());
	}
	
	@Test
	public void deleteBeyondRangeOk() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.delete(new VariantSpan(12, 100));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);

		assertEquals(new InvariantSpan(100, 6), spans.get(0));
		assertEquals(new InvariantSpan(200, 3), spans.get(1));
		assertEquals(new InvariantSpan(250, 2), spans.get(2));
		assertEquals(3, spans.size());
	}
	
	@Test
	public void deleteOneByte() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.delete(new VariantSpan(12, 1));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);
		assertEquals(new InvariantSpan(100, 6), spans.get(0));
		assertEquals(new InvariantSpan(200, 3), spans.get(1));
		assertEquals(new InvariantSpan(250, 2), spans.get(2));
		assertEquals(new InvariantSpan(301, 3), spans.get(3));
		assertEquals(new InvariantSpan(350, 1), spans.get(4));
		assertEquals(new InvariantSpan(360, 6), spans.get(5));

		assertEquals(6, spans.size());
	}
	
	@Test
	public void deleteOneByteInFirstElement() throws Exception {//6, 19
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.delete(new VariantSpan(6, 1));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);
		assertEquals(new InvariantSpan(100, 5), spans.get(0));
		assertEquals(new InvariantSpan(200, 3), spans.get(1));
		assertEquals(new InvariantSpan(250, 2), spans.get(2));
		assertEquals(new InvariantSpan(300, 4), spans.get(3));
		assertEquals(new InvariantSpan(350, 1), spans.get(4));
		assertEquals(new InvariantSpan(360, 6), spans.get(5));

		assertEquals(6, spans.size());
	}

	@Test
	public void deleteSomeBytes() throws Exception {
		// * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.delete(new VariantSpan(8, 7));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		assertEquals(new InvariantSpan(100, 6), spans.get(0));
		assertEquals(new InvariantSpan(200, 1), spans.get(1));
		assertEquals(new InvariantSpan(303, 1), spans.get(2));
		assertEquals(new InvariantSpan(350, 1), spans.get(3));
		assertEquals(new InvariantSpan(360, 6), spans.get(4));

		assertEquals(5, spans.size());
	}

	
	@Test
	public void deleteWithSplit() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		stream.delete(new VariantSpan(12, 6));
		List<InvariantSpan> spans = stream.getInvariantSpans().getInvariantSpans();
		System.out.println(spans);
		assertEquals(new InvariantSpan(100, 6), spans.get(0));
		assertEquals(new InvariantSpan(200, 3), spans.get(1));
		assertEquals(new InvariantSpan(250, 2), spans.get(2));
		assertEquals(new InvariantSpan(361, 5), spans.get(3));
		assertEquals(4, spans.size());
	}

	
	@Test
	public void getInvariantSpansBeyondWidthOk() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		List<InvariantSpan> spans = stream.getInvariantSpans(new VariantSpan(10, 700)).getInvariantSpans();
		assertEquals(new InvariantSpan(250, 2), spans.get(0));
		assertEquals(new InvariantSpan(300, 4), spans.get(1));
		assertEquals(new InvariantSpan(350, 1), spans.get(2));
		assertEquals(new InvariantSpan(360, 6), spans.get(3));
	}

	@Test
	public void getInvariantSpansMiddle() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		List<InvariantSpan> spans = stream.getInvariantSpans(new VariantSpan(10, 7)).getInvariantSpans();
		assertEquals(new InvariantSpan(250, 2), spans.get(0));
		assertEquals(new InvariantSpan(300, 4), spans.get(1));
		assertEquals(new InvariantSpan(350, 1), spans.get(2));
	}

	@Test
	public void getInvariantSpansMiddle2() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		List<InvariantSpan> spans = stream.getInvariantSpans(new VariantSpan(10, 6)).getInvariantSpans();
		assertEquals(new InvariantSpan(251, 1), spans.get(0));
		assertEquals(new InvariantSpan(300, 4), spans.get(1));
		assertEquals(new InvariantSpan(350, 1), spans.get(2));
	}
	
	@Test
	public void getInvariantSpansRangeEdgeLeft() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		List<InvariantSpan> spans = stream.getInvariantSpans(new VariantSpan(1, 14)).getInvariantSpans();
		assertEquals(new InvariantSpan(100, 6), spans.get(0));
		assertEquals(new InvariantSpan(200, 3), spans.get(1));
		assertEquals(new InvariantSpan(250, 2), spans.get(2));
		assertEquals(new InvariantSpan(300, 3), spans.get(3));
		assertEquals(4, spans.size());
	}


	@Test
	public void getInvariantSpansSmallWidthLeft() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		List<InvariantSpan> spans = stream.getInvariantSpans(new VariantSpan(1, 1)).getInvariantSpans();
		assertEquals(new InvariantSpan(100, 1), spans.get(0));
	}

	@Test
	public void getInvariantSpansSmallWidthRight() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		List<InvariantSpan> spans = stream.getInvariantSpans(new VariantSpan(21, 1)).getInvariantSpans();
		assertEquals(new InvariantSpan(365, 1), spans.get(0));
	}

	@Test
	public void index() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		InvariantSpan span = stream.index(12);
		assertEquals(new InvariantSpan(300, 4), span);
	}

	@Test
	public void index11() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		InvariantSpan span = stream.index(11);
		assertEquals(new InvariantSpan(250, 2), span);
		span = stream.index(10);
		assertEquals(new InvariantSpan(250, 2), span);

	}

	@Test
	public void indexNull() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		InvariantSpan span = stream.index(100);
		assertNull(span);
	}

	@Test
	public void indexStart() throws Exception {
		VariantStream stream = new RopeVariantStream(null, getA());
		InvariantSpan span = stream.index(1);
		assertEquals(new InvariantSpan(100, 6), span);
	}
}
