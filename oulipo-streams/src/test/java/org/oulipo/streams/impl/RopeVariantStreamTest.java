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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.oulipo.streams.impl.NodeFactory.getA;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.OverlaySpan;
import org.oulipo.streams.Span;
import org.oulipo.streams.Spans;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;

/**
 * 
 * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
 */
public class RopeVariantStreamTest {

	public static final TumblerAddress homeDocument = TumblerAddress.createWithNoException("ted://1.2.0.2.0.23.1.1");

	@Test
	public void applyOverlays() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		stream.put(new OverlaySpan(1, 10, homeDocument));

		stream.applyOverlays(new VariantSpan(1, 5), Arrays.asList(TumblerAddress.BOLD));

		Set<OverlaySpan> results = stream.getOverlaySpans();

		assertEquals(2, results.size());
		OverlaySpan os1 = new OverlaySpan(1, 5, homeDocument);
		OverlaySpan os2 = new OverlaySpan(6, 5, homeDocument);
		assertTrue(results.contains(os1));
		assertTrue(results.contains(os2));
		for(OverlaySpan span : results) {
			if(span.equals(os1)) {
				assertEquals(1, span.linkTypes.size());
				assertTrue(span.hasLinkType(TumblerAddress.BOLD));
			} else if(span.equals(os2)) {
				assertEquals(0, span.linkTypes.size());
			} 
		}
	}

	@Test
	public void boldAndItalicOverlap() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		stream.put(new OverlaySpan(1, 10, homeDocument));
		
		stream.applyOverlays(new VariantSpan(1, 5), Arrays.asList(TumblerAddress.BOLD));
		stream.applyOverlays(new VariantSpan(1, 5), Arrays.asList(TumblerAddress.ITALIC));

		Set<OverlaySpan> results = stream.getOverlaySpans();
		
		assertEquals(2, results.size());
		
		OverlaySpan os1 = new OverlaySpan(1, 5, homeDocument);
		OverlaySpan os2 = new OverlaySpan(6, 5, homeDocument);
		assertTrue(results.contains(os1));
		assertTrue(results.contains(os2));
		
		for(OverlaySpan span : results) {
			if(span.equals(os1)) {
				assertEquals(2, span.linkTypes.size());
				assertTrue(span.hasLinkType(TumblerAddress.BOLD));
				assertTrue(span.hasLinkType(TumblerAddress.ITALIC));
			} else if(span.equals(os2)) {
				assertEquals(0, span.linkTypes.size());
			} 
		}
	}
	
	@Test
	public void boldAndItalicPartition() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		stream.put(new OverlaySpan(1, 10, homeDocument));

		stream.applyOverlays(new VariantSpan(1, 5), Arrays.asList(TumblerAddress.BOLD));
		stream.applyOverlays(new VariantSpan(2, 9), Arrays.asList(TumblerAddress.ITALIC));

		Set<OverlaySpan> results = stream.getOverlaySpans();
	
		assertEquals(3, results.size());

		OverlaySpan os1 = new OverlaySpan(1, 1, homeDocument);
		OverlaySpan os2 = new OverlaySpan(2, 4, homeDocument);
		OverlaySpan os3 = new OverlaySpan(6, 5, homeDocument);
		
		assertTrue(results.contains(os1));
		assertTrue(results.contains(os2));
		assertTrue(results.contains(os3));

		for(OverlaySpan span : results) {
			if(span.equals(os1)) {
				assertEquals(1, span.linkTypes.size());
				assertTrue(span.hasLinkType(TumblerAddress.BOLD));
			} else if(span.equals(os2)) {
				assertEquals(2, span.linkTypes.size());
				assertTrue(span.hasLinkType(TumblerAddress.BOLD));
				assertTrue(span.hasLinkType(TumblerAddress.ITALIC));
			} else if(span.equals(os3)) {
				assertTrue(span.hasLinkType(TumblerAddress.ITALIC));
			}
		}
	}

	@Test
	public void boldItalicSmallPartitions() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		stream.put(new OverlaySpan(1, 1, homeDocument));
		stream.put(new OverlaySpan(2, 1, homeDocument));
		stream.put(new OverlaySpan(3, 1, homeDocument));
		stream.put(new OverlaySpan(4, 1, homeDocument));
		stream.put(new OverlaySpan(5, 1, homeDocument));
		stream.put(new OverlaySpan(6, 1, homeDocument));
		stream.put(new OverlaySpan(7, 1, homeDocument));
		stream.put(new OverlaySpan(8, 1, homeDocument));

		stream.applyOverlays(new VariantSpan(3, 4), Arrays.asList(TumblerAddress.BOLD));
		stream.applyOverlays(new VariantSpan(4, 2), Arrays.asList(TumblerAddress.ITALIC));

		for (OverlaySpan seg : stream.getOverlaySpans()) {
			System.out.println(seg);
		}
	}

	@Test
	public void copy() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.copy(2, new VariantSpan(12, 3));
		List<Span> spans = stream.getSpans().getSpans();
		System.out.println(spans);
	}

	
	@Test
	public void delete() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.delete(new VariantSpan(12, 4));// have orphan in list which shouldn't be there [M]
		List<Span> spans = stream.getSpans().getSpans();

		assertEquals(new Span(100, 6, homeDocument), spans.get(0));
		assertEquals(new Span(200, 3, homeDocument), spans.get(1));
		assertEquals(new Span(250, 2, homeDocument), spans.get(2));
		assertEquals(new Span(350, 1, homeDocument), spans.get(3));
		assertEquals(new Span(360, 6, homeDocument), spans.get(4));

		assertEquals(5, spans.size());
	}
	
	@Test
	public void delete2() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.delete(new VariantSpan(12, 5));
		List<Span> spans = stream.getSpans().getSpans();

		assertEquals(new Span(100, 6, homeDocument), spans.get(0));
		assertEquals(new Span(200, 3, homeDocument), spans.get(1));
		assertEquals(new Span(250, 2, homeDocument), spans.get(2));
		assertEquals(new Span(360, 6, homeDocument), spans.get(3));
		assertEquals(4, spans.size());
	}

	@Test
	public void delete3() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		List<Span> spans = Arrays.asList(new Span(1, 11, homeDocument));
		stream.put(1, spans);

		stream.delete(new VariantSpan(1, 5));

		List<Span> results = stream.getSpans().getSpans();
		assertEquals(new Span(6, 6, homeDocument), results.get(0));

	}

	@Test
	public void delete6To19() throws Exception {// 6, 19
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.delete(new VariantSpan(6, 19));
		List<Span> spans = stream.getSpans().getSpans();

		assertEquals(new Span(100, 5, homeDocument), spans.get(0));
		assertEquals(1, spans.size());
	}

	@Test
	public void deleteAll() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.delete(new VariantSpan(1, 22));
		List<Span> spans = stream.getSpans().getSpans();
		assertEquals(0, spans.size());
	}

	@Test
	public void deleteBeyondRangeOk() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.delete(new VariantSpan(12, 100));
		List<Span> spans = stream.getSpans().getSpans();

		assertEquals(new Span(100, 6, homeDocument), spans.get(0));
		assertEquals(new Span(200, 3, homeDocument), spans.get(1));
		assertEquals(new Span(250, 2, homeDocument), spans.get(2));
		assertEquals(3, spans.size());
	}

	@Test
	public void deleteLastCharacter() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		List<Span> spans = Arrays.asList(new Span(1, 4, homeDocument));
		stream.put(1, spans);

		stream.delete(new VariantSpan(3, 1));

		List<Span> results = stream.getSpans().getSpans();
		assertEquals(new Span(1, 3, homeDocument), results.get(0));
	}

	@Test
	public void deleteOneByte() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.delete(new VariantSpan(12, 1));
		List<Span> spans = stream.getSpans().getSpans();

		assertEquals(new Span(100, 6, homeDocument), spans.get(0));
		assertEquals(new Span(200, 3, homeDocument), spans.get(1));
		assertEquals(new Span(250, 2, homeDocument), spans.get(2));
		assertEquals(new Span(301, 3, homeDocument), spans.get(3));
		assertEquals(new Span(350, 1, homeDocument), spans.get(4));
		assertEquals(new Span(360, 6, homeDocument), spans.get(5));

		assertEquals(6, spans.size());
	}

	@Test
	public void deleteOneByteInFirstElement() throws Exception {// 6, 19
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.delete(new VariantSpan(6, 1));
		List<Span> spans = stream.getSpans().getSpans();

		assertEquals(new Span(100, 5, homeDocument), spans.get(0));
		assertEquals(new Span(200, 3, homeDocument), spans.get(1));
		assertEquals(new Span(250, 2, homeDocument), spans.get(2));
		assertEquals(new Span(300, 4, homeDocument), spans.get(3));
		assertEquals(new Span(350, 1, homeDocument), spans.get(4));
		assertEquals(new Span(360, 6, homeDocument), spans.get(5));

		assertEquals(6, spans.size());
	}

	@Test
	public void deleteOverlay() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		stream.put(new OverlaySpan(1, 1, homeDocument));
		stream.put(new OverlaySpan(2, 1, homeDocument));
		stream.put(new OverlaySpan(3, 1, homeDocument));
		stream.put(new OverlaySpan(4, 1, homeDocument));

		stream.delete(new VariantSpan(2, 2));
	
		Set<OverlaySpan> results = stream.getOverlaySpans();
	
		assertEquals(2, results.size());
		assertTrue(results.contains(new OverlaySpan(1, 1, homeDocument)));
		assertTrue(results.contains(new OverlaySpan(4, 1, homeDocument)));
	}

	@Test
	public void deleteSomeBytes() throws Exception {
		// * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.delete(new VariantSpan(8, 7));
		List<Span> spans = stream.getSpans().getSpans();
		assertEquals(new Span(100, 6, homeDocument), spans.get(0));
		assertEquals(new Span(200, 1, homeDocument), spans.get(1));
		assertEquals(new Span(303, 1, homeDocument), spans.get(2));
		assertEquals(new Span(350, 1, homeDocument), spans.get(3));
		assertEquals(new Span(360, 6, homeDocument), spans.get(4));

		assertEquals(5, spans.size());
	}

	@Test
	public void deleteSpan() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		stream.put(1, new Span(1, 1, homeDocument));
		stream.put(2, new Span(2, 1, homeDocument));
		stream.put(3, new Span(3, 1, homeDocument));
		stream.put(4, new Span(4, 1, homeDocument));

		stream.delete(new VariantSpan(2, 2));
	
		List<Span> results = stream.getSpans().getSpans();
	
		assertEquals(2, results.size());
		assertTrue(results.contains(new Span(1, 1, homeDocument)));
		assertTrue(results.contains(new Span(4, 1, homeDocument)));
	}

	@Test
	public void deleteWithSplit() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.delete(new VariantSpan(12, 6));
		List<Span> spans = stream.getSpans().getSpans();

		assertEquals(new Span(100, 6, homeDocument), spans.get(0));
		assertEquals(new Span(200, 3, homeDocument), spans.get(1));
		assertEquals(new Span(250, 2, homeDocument), spans.get(2));
		assertEquals(new Span(361, 5, homeDocument), spans.get(3));
		assertEquals(4, spans.size());
	}

	@Test
	public void getInvariantSpan() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		List<Span> spans = Arrays.asList(new Span(1, 11, homeDocument));
		stream.put(1, spans);

		List<Span> results = stream.getSpans(new VariantSpan(5, 6)).getSpans();

		assertEquals(new Span(6, 6, homeDocument.toExternalForm()), results.get(0));
	}

	@Test
	public void getInvariantSpansBeyondWidthOk() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		List<Span> spans = stream.getSpans(new VariantSpan(10, 700)).getSpans();
		assertEquals(new Span(250, 2, homeDocument.toExternalForm()), spans.get(0));
		assertEquals(new Span(300, 4, homeDocument.toExternalForm()), spans.get(1));
		assertEquals(new Span(350, 1, homeDocument.toExternalForm()), spans.get(2));
		assertEquals(new Span(360, 6, homeDocument.toExternalForm()), spans.get(3));
	}

	@Test
	public void getInvariantSpansMiddle() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		List<Span> spans = stream.getSpans(new VariantSpan(10, 7)).getSpans();

		assertEquals(new Span(250, 2, homeDocument.toExternalForm()), spans.get(0));
		assertEquals(new Span(300, 4, homeDocument.toExternalForm()), spans.get(1));
		assertEquals(new Span(350, 1, homeDocument.toExternalForm()), spans.get(2));
	}

	@Test
	public void getInvariantSpansMiddle2() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		List<Span> spans = stream.getSpans(new VariantSpan(10, 6, homeDocument.toExternalForm())).getSpans();
		assertEquals(new Span(250, 2, homeDocument.toExternalForm()), spans.get(0));
		assertEquals(new Span(300, 4, homeDocument.toExternalForm()), spans.get(1));
	}

	@Test
	public void getInvariantSpansRangeEdgeLeft() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		List<Span> spans = stream.getSpans(new VariantSpan(1, 14)).getSpans();
		assertEquals(new Span(100, 6, homeDocument.toExternalForm()), spans.get(0));
		assertEquals(new Span(200, 3, homeDocument.toExternalForm()), spans.get(1));
		assertEquals(new Span(250, 2, homeDocument.toExternalForm()), spans.get(2));
		assertEquals(new Span(300, 3, homeDocument.toExternalForm()), spans.get(3));
		assertEquals(4, spans.size());
	}

	@Test
	public void getInvariantSpansSmallWidthLeft() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		List<Span> spans = stream.getSpans(new VariantSpan(1, 1)).getSpans();
		assertEquals(new Span(100, 1, homeDocument.toExternalForm()), spans.get(0));
	}

	@Test
	public void getInvariantSpansSmallWidthRight() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		List<Span> spans = stream.getSpans(new VariantSpan(21, 1)).getSpans();
		assertEquals(new Span(365, 1, homeDocument.toExternalForm()), spans.get(0));
	}

	@Test
	public void getVariantSpansSingle() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		List<Span> spans = Arrays.asList(new Span(7, 5, homeDocument), new Span(4, 3, homeDocument),
				new Span(1, 3, homeDocument));
		stream.put(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new Span(7, 5, homeDocument));
		assertEquals(result.get(0), new VariantSpan(1, 5, homeDocument.toExternalForm()));

		Span invariantSpan = stream.getSpans(new VariantSpan(1, 5)).getSpans().get(0);
		assertEquals(new Span(7, 5, homeDocument.toExternalForm()), invariantSpan);
	}

	@Test
	public void getVariantSpansTwo() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		List<Span> spans = Arrays.asList(new Span(7, 5, homeDocument), new Span(4, 3, homeDocument),
				new Span(1, 3, homeDocument));
		stream.put(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new Span(1, 6, homeDocument));
		assertEquals(2, result.size());
		assertEquals(new VariantSpan(6, 3, homeDocument.toExternalForm()), result.get(0));
		assertEquals(new VariantSpan(9, 3, homeDocument.toExternalForm()), result.get(1));

		System.out.println("Actual:" + result);
		System.out.println("Expected:" + new VariantSpan(6, 6));

		Spans invariantSpans = stream.getSpans(new VariantSpan(6, 6));
		System.out.println(invariantSpans.getSpans());
	}

	@Test
	public void getVariantSpansTwoIntersect() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		List<Span> spans = Arrays.asList(new Span(7, 5, homeDocument), new Span(4, 3, homeDocument),
				new Span(1, 3, homeDocument));
		stream.put(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new Span(2, 5, homeDocument));
		assertEquals(2, result.size());
		assertEquals(new VariantSpan(6, 3, homeDocument.toExternalForm()), result.get(0));
		assertEquals(new VariantSpan(10, 2, homeDocument.toExternalForm()), result.get(1));
	}

	@Test
	public void getVariantSpansTwoIntersect2() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		List<Span> spans = Arrays.asList(new Span(7, 5, homeDocument), new Span(4, 3, homeDocument),
				new Span(1, 3, homeDocument));
		stream.put(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new Span(1, 4, homeDocument));
		assertEquals(2, result.size());
		assertEquals(new VariantSpan(6, 1, homeDocument.toExternalForm()), result.get(0));
		assertEquals(new VariantSpan(9, 3, homeDocument.toExternalForm()), result.get(1));
	}

	@Test
	public void index() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		Span span = stream.index(12);
		assertEquals(new Span(300, 4, homeDocument), span);
	}

	@Test
	public void index11() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		Span span = stream.index(11);
		assertEquals(new Span(250, 2, homeDocument), span);
		span = stream.index(10);
		assertEquals(new Span(250, 2, homeDocument), span);

	}

	@Test
	public void indexNull() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		Span span = stream.index(100);
		assertNull(span);
	}

	@Test
	public void indexStart() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		Span span = stream.index(1);
		assertEquals(new Span(100, 6, homeDocument), span);
	}

	@Test
	public void move() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.move(2, new VariantSpan(12, 3));
		List<Span> spans = stream.getSpans().getSpans();
		System.out.println(spans);
	}

	@Test
	public void overlay() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		stream.put(new OverlaySpan(1, 1, homeDocument));
		stream.put(new OverlaySpan(2, 1, homeDocument));
		stream.put(new OverlaySpan(3, 1, homeDocument));
		stream.put(new OverlaySpan(4, 1, homeDocument));

		stream.toggleOverlay(new VariantSpan(2, 2), TumblerAddress.BOLD);

		Set<OverlaySpan> results = stream.getOverlaySpans();
	
		OverlaySpan os1 = new OverlaySpan(1, 1, homeDocument);
		OverlaySpan os2 = new OverlaySpan(2, 1, homeDocument);
		OverlaySpan os3 = new OverlaySpan(3, 1, homeDocument);
		OverlaySpan os4 = new OverlaySpan(4, 1, homeDocument);
	
		assertEquals(4, results.size());
		assertTrue(results.contains(os1));
		assertTrue(results.contains(os2));
		
		for(OverlaySpan span : results) {
			if(span.equals(os1)) {
				assertEquals(0, span.linkTypes.size());
			} else if(span.equals(os2)) {
				assertEquals(1, span.linkTypes.size());
				assertTrue(span.hasLinkType(TumblerAddress.BOLD));
			}  else if(span.equals(os3)) {
				assertEquals(1, span.linkTypes.size());
				assertTrue(span.hasLinkType(TumblerAddress.BOLD));
			} else if(span.equals(os4)) {
				assertEquals(0, span.linkTypes.size());
			}
		}
	}
	/*
	@Test
	public void overlayWithInsert() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		stream.put(new OverlaySpan(1, 1, homeDocument));
		stream.put(new OverlaySpan(2, 1, homeDocument));
		stream.put(new OverlaySpan(3, 1, homeDocument));
		stream.put(new OverlaySpan(4, 1, homeDocument));

		stream.toggleOverlay(new VariantSpan(2, 2), TumblerAddress.BOLD);
		stream.put(new OverlaySpan(1, 2, homeDocument));

		Set<OverlaySpan> results = stream.getOverlaySpans();
		for(OverlaySpan span : results) {
			System.out.println(span);
		}
		
		OverlaySpan os1 = new OverlaySpan(1, 1, homeDocument);
		OverlaySpan os2 = new OverlaySpan(2, 1, homeDocument);
		OverlaySpan os3 = new OverlaySpan(3, 1, homeDocument);
		OverlaySpan os4 = new OverlaySpan(4, 1, homeDocument);
	
		assertEquals(4, results.size());
		assertTrue(results.contains(os1));
		assertTrue(results.contains(os2));
		
		for(OverlaySpan span : results) {
			if(span.equals(os1)) {
				assertEquals(0, span.linkTypes.size());
			} else if(span.equals(os2)) {
				assertEquals(1, span.linkTypes.size());
				assertTrue(span.hasLinkType(TumblerAddress.BOLD));
			}  else if(span.equals(os3)) {
				assertEquals(1, span.linkTypes.size());
				assertTrue(span.hasLinkType(TumblerAddress.BOLD));
			} else if(span.equals(os4)) {
				assertEquals(0, span.linkTypes.size());
			}
		}
	}
	*/

	@Test
	public void put() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.put(12, new Span(500, 34, homeDocument));
		List<Span> spans = stream.getSpans().getSpans();
		System.out.println(spans);
	}

	@Test
	public void putSmallParitions() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		stream.put(1, new Span(1, 1, homeDocument));
		stream.put(2, new Span(2, 1, homeDocument));
		stream.put(3, new Span(3, 1, homeDocument));
		stream.put(4, new Span(4, 1, homeDocument));
		stream.put(5, new Span(5, 1, homeDocument));
		stream.put(6, new Span(6, 1, homeDocument));
		stream.put(7, new Span(7, 1, homeDocument));
		stream.put(8, new Span(8, 1, homeDocument));

		List<Span> spans = stream.getSpans(new VariantSpan(3, 4)).getSpans();

		assertEquals(new Span(3, 1, homeDocument.toExternalForm()), spans.get(0));
		assertEquals(new Span(4, 1, homeDocument.toExternalForm()), spans.get(1));
		assertEquals(new Span(5, 1, homeDocument.toExternalForm()), spans.get(2));
		assertEquals(new Span(6, 1, homeDocument.toExternalForm()), spans.get(3));

		assertEquals(4, spans.size());

	}

	@Test
	public void putSmallParitions2() throws Exception {
		RopeVariantStream stream = new RopeVariantStream(homeDocument);
		stream.put(1, new Span(1, 1, homeDocument));
		stream.put(2, new Span(2, 1, homeDocument));
		stream.put(3, new Span(3, 1, homeDocument));
		stream.put(4, new Span(4, 1, homeDocument));
		stream.put(5, new Span(5, 1, homeDocument));
		stream.put(6, new Span(6, 1, homeDocument));
		stream.put(7, new Span(7, 1, homeDocument));
		stream.put(8, new Span(8, 1, homeDocument));

		List<Span> spans = stream.getSpans(new VariantSpan(4, 2)).getSpans();

		assertEquals(new Span(4, 1, homeDocument.toExternalForm()), spans.get(0));
		assertEquals(new Span(5, 1, homeDocument.toExternalForm()), spans.get(1));

		assertEquals(2, spans.size());

	}

	@Test
	public void putSpans() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		List<Span> spans = Arrays.asList(new Span(7, 5, homeDocument), new Span(4, 3, homeDocument),
				new Span(1, 3, homeDocument));
		stream.put(1, spans);
		List<Span> results = stream.getSpans().getSpans();
		System.out.println(stream.getSpans().getSpans());

		assertEquals(results.get(0), spans.get(0));
		assertEquals(results.get(1), spans.get(1));
		assertEquals(results.get(2), spans.get(2));
	}

	@Test
	public void putSpansSequence() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);

		stream.put(1, new Span(1, 1, homeDocument));
		stream.put(2, new Span(2, 1, homeDocument));
		stream.put(3, new Span(3, 1, homeDocument));

		List<Span> results = stream.getSpans().getSpans();

		assertEquals(results.get(0), new Span(1, 1, homeDocument));
		assertEquals(results.get(1), new Span(2, 1, homeDocument));
		assertEquals(results.get(2), new Span(3, 1, homeDocument));
	}

	@Test
	public void putSpansSequence2() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);

		stream.put(1, new Span(1, 1, homeDocument));
		stream.put(2, new Span(2, 1, homeDocument));
		stream.put(1, new Span(3, 1, homeDocument));

		stream.put(2, new Span(4, 1, homeDocument));

		List<Span> results = stream.getSpans().getSpans();

		assertEquals(results.get(0), new Span(3, 1, homeDocument));// 3
		assertEquals(results.get(1), new Span(4, 1, homeDocument));// 1
		assertEquals(results.get(2), new Span(1, 1, homeDocument));// 4
		assertEquals(results.get(3), new Span(2, 1, homeDocument));// 2

	}

	@Test
	public void putWithSplit() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.put(5, new Span(500, 34, homeDocument));
		List<Span> spans = stream.getSpans().getSpans();
		System.out.println(spans);
	}

	@Test
	public void swap() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument, getA());
		stream.swap(new VariantSpan(1, 3), new VariantSpan(12, 3));
		List<Span> spans = stream.getSpans().getSpans();
		System.out.println(spans);

		assertEquals(new Span(300, 3, homeDocument), spans.get(0));
		assertEquals(new Span(103, 3, homeDocument), spans.get(1));
		assertEquals(new Span(200, 3, homeDocument), spans.get(2));
		assertEquals(new Span(250, 2, homeDocument), spans.get(3));
		assertEquals(new Span(100, 3, homeDocument), spans.get(4));
		assertEquals(new Span(303, 1, homeDocument), spans.get(5));
		assertEquals(new Span(350, 1, homeDocument), spans.get(6));
		assertEquals(new Span(360, 6, homeDocument), spans.get(7));

		assertEquals(8, spans.size());

		// [Span [start=300, width=3], Span [start=103, width=3],
		// Span [start=200, width=3], Span [start=250, width=2],
		// Span [start=303, width=1], Span [start=100, width=3],
		// Span [start=303, width=1], Span [start=350, width=1],
		// Span [start=360, width=6]]

	}

	@Test
	public void toggleOnOff() throws Exception {
		VariantStream stream = new RopeVariantStream(homeDocument);
		stream.put(new OverlaySpan(1, 1, homeDocument.toExternalForm()));
		stream.put(new OverlaySpan(2, 1, homeDocument.toExternalForm()));
		stream.put(new OverlaySpan(3, 1, homeDocument.toExternalForm()));
		stream.put(new OverlaySpan(4, 1, homeDocument.toExternalForm()));

		stream.toggleOverlay(new VariantSpan(2, 2), TumblerAddress.BOLD);
		stream.toggleOverlay(new VariantSpan(2, 2), TumblerAddress.BOLD);

		Set<OverlaySpan> results = stream.getOverlaySpans();
		assertEquals(4, results.size());
		
		for (OverlaySpan seg : results) {
			assertTrue(seg.linkTypes.isEmpty());
		}
	}

}
