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

import org.junit.Test;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.types.OverlayElement;
import org.oulipo.streams.types.SpanElement;

import com.google.common.collect.Sets;

/**
 * 
 * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
 */
public class RopeVariantStreamTest {

	public static final TumblerAddress homeDocument = TumblerAddress.createWithNoException("ted://1.2.0.2.0.23.1.1");

	@Test
	public void applyOverlays() throws Exception {
		VariantStream<OverlayElement> stream = new RopeVariantStream<>(homeDocument);
		stream.put(1, new OverlayElement(10, homeDocument));

		stream.applyOverlays(new VariantSpan(1, 5), Sets.newHashSet(TumblerAddress.BOLD));

		List<OverlayElement> results = stream.getStreamElements();
		assertEquals(2, results.size());
		
		assertTrue(results.get(0).hasLinkType(TumblerAddress.BOLD));
		assertFalse(results.get(1).hasLinkType(TumblerAddress.BOLD));
	}

	@Test
	public void boldAndItalicOverlap() throws Exception {
		VariantStream<OverlayElement> stream = new RopeVariantStream<>(homeDocument);
		stream.put(1, new OverlayElement(10, homeDocument));
		
		stream.applyOverlays(new VariantSpan(1, 5), Sets.newHashSet(TumblerAddress.BOLD));
		stream.applyOverlays(new VariantSpan(1, 5), Sets.newHashSet(TumblerAddress.ITALIC));

		List<OverlayElement> results = stream.getStreamElements();
		
		assertEquals(2, results.size());
		assertTrue(results.get(0).hasLinkType(TumblerAddress.BOLD));
		assertTrue(results.get(0).hasLinkType(TumblerAddress.ITALIC));

		assertFalse(results.get(1).hasLinkType(TumblerAddress.BOLD));
		assertFalse(results.get(1).hasLinkType(TumblerAddress.ITALIC));
	}
	
	@Test
	public void boldAndItalicPartition() throws Exception {
		VariantStream<OverlayElement> stream = new RopeVariantStream<>(homeDocument);
		stream.put(1, new OverlayElement(10, homeDocument));

		stream.applyOverlays(new VariantSpan(1, 5), Sets.newHashSet(TumblerAddress.BOLD));
		List<OverlayElement> results2 = stream.getStreamElements();
		System.out.println(results2);

		stream.applyOverlays(new VariantSpan(2, 9), Sets.newHashSet(TumblerAddress.ITALIC));

		List<OverlayElement> results = stream.getStreamElements();
	
		assertEquals(3, results.size());
		System.out.println(results);
		assertEquals(1, results.get(0).linkCount());
		assertTrue(results.get(0).hasLinkType(TumblerAddress.BOLD));
		assertEquals(1, results.get(0).getWidth());

		assertEquals(2, results.get(1).linkCount());
		assertTrue(results.get(1).hasLinkType(TumblerAddress.BOLD));
		assertTrue(results.get(1).hasLinkType(TumblerAddress.ITALIC));
		assertEquals(4, results.get(1).getWidth());

		assertTrue(results.get(2).hasLinkType(TumblerAddress.ITALIC));	
		assertEquals(5, results.get(2).getWidth());

	}

	@Test
	public void boldItalicSmallPartitions() throws Exception {
		VariantStream<OverlayElement> stream = new RopeVariantStream<>(homeDocument);
		stream.put(1, new OverlayElement(1, homeDocument));
		stream.put(2, new OverlayElement(1, homeDocument));
		stream.put(3, new OverlayElement(1, homeDocument));
		stream.put(4, new OverlayElement(1, homeDocument));
		stream.put(5, new OverlayElement(1, homeDocument));
		stream.put(6, new OverlayElement(1, homeDocument));
		stream.put(7, new OverlayElement(1, homeDocument));
		stream.put(8, new OverlayElement(1, homeDocument));
		
		stream.applyOverlays(new VariantSpan(3, 4), Sets.newHashSet(TumblerAddress.BOLD));
		stream.applyOverlays(new VariantSpan(4, 2), Sets.newHashSet(TumblerAddress.ITALIC));

		for (OverlayElement seg : stream.getStreamElements()) {
			System.out.println(seg);
		}
	}

	@Test
	public void copy() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.copy(2, new VariantSpan(12, 3));
		List<SpanElement> spans = stream.getStreamElements();
		System.out.println(spans);
	}

	
	@Test
	public void delete() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.delete(new VariantSpan(12, 4));
		List<SpanElement> spans = stream.getStreamElements();

		assertEquals(new SpanElement(100, 6, homeDocument), spans.get(0));
		assertEquals(new SpanElement(200, 3, homeDocument), spans.get(1));
		assertEquals(new SpanElement(250, 2, homeDocument), spans.get(2));
		assertEquals(new SpanElement(350, 1, homeDocument), spans.get(3));
		assertEquals(new SpanElement(360, 6, homeDocument), spans.get(4));

		assertEquals(5, spans.size());
	}
	
	@Test
	public void delete2() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.delete(new VariantSpan(12, 5));
		List<SpanElement> spans = stream.getStreamElements();

		assertEquals(new SpanElement(100, 6, homeDocument), spans.get(0));
		assertEquals(new SpanElement(200, 3, homeDocument), spans.get(1));
		assertEquals(new SpanElement(250, 2, homeDocument), spans.get(2));
		assertEquals(new SpanElement(360, 6, homeDocument), spans.get(3));
		assertEquals(4, spans.size());
	}

	@Test
	public void delete3() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);
		List<SpanElement> spans = Arrays.asList(new SpanElement(1, 11, homeDocument));
		stream.putElements(1, spans);

		stream.delete(new VariantSpan(1, 5));

		List<SpanElement> results = stream.getStreamElements();
		assertEquals(new SpanElement(6, 6, homeDocument), results.get(0));

	}

	@Test
	public void delete6To19() throws Exception {// 6, 19
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.delete(new VariantSpan(6, 19));
		List<SpanElement> spans = stream.getStreamElements();

		assertEquals(new SpanElement(100, 5, homeDocument), spans.get(0));
		assertEquals(1, spans.size());
	}

	@Test
	public void deleteAll() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.delete(new VariantSpan(1, 22));
		List<SpanElement> spans = stream.getStreamElements();
		assertEquals(0, spans.size());
	}

	@Test
	public void deleteBeyondRangeOk() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.delete(new VariantSpan(12, 100));
		List<SpanElement> spans = stream.getStreamElements();

		assertEquals(new SpanElement(100, 6, homeDocument), spans.get(0));
		assertEquals(new SpanElement(200, 3, homeDocument), spans.get(1));
		assertEquals(new SpanElement(250, 2, homeDocument), spans.get(2));
		assertEquals(3, spans.size());
	}

	@Test
	public void deleteLastCharacter() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);
		List<SpanElement> spans = Arrays.asList(new SpanElement(1, 4, homeDocument));
		stream.putElements(1, spans);

		stream.delete(new VariantSpan(3, 1));

		List<SpanElement> results = stream.getStreamElements();
		assertEquals(new SpanElement(1, 3, homeDocument), results.get(0));
	}

	@Test
	public void deleteOneByte() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.delete(new VariantSpan(12, 1));
		List<SpanElement> spans = stream.getStreamElements();

		assertEquals(new SpanElement(100, 6, homeDocument), spans.get(0));
		assertEquals(new SpanElement(200, 3, homeDocument), spans.get(1));
		assertEquals(new SpanElement(250, 2, homeDocument), spans.get(2));
		assertEquals(new SpanElement(301, 3, homeDocument), spans.get(3));
		assertEquals(new SpanElement(350, 1, homeDocument), spans.get(4));
		assertEquals(new SpanElement(360, 6, homeDocument), spans.get(5));

		assertEquals(6, spans.size());
	}

	@Test
	public void deleteOneByteInFirstElement() throws Exception {// 6, 19
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.delete(new VariantSpan(6, 1));
		List<SpanElement> spans = stream.getStreamElements();

		assertEquals(new SpanElement(100, 5, homeDocument), spans.get(0));
		assertEquals(new SpanElement(200, 3, homeDocument), spans.get(1));
		assertEquals(new SpanElement(250, 2, homeDocument), spans.get(2));
		assertEquals(new SpanElement(300, 4, homeDocument), spans.get(3));
		assertEquals(new SpanElement(350, 1, homeDocument), spans.get(4));
		assertEquals(new SpanElement(360, 6, homeDocument), spans.get(5));

		assertEquals(6, spans.size());
	}

	@Test
	public void deleteOverlay() throws Exception {
		VariantStream<OverlayElement> stream = new RopeVariantStream<>(homeDocument);
		stream.put(1, new OverlayElement(1, homeDocument, Sets.newHashSet(TumblerAddress.BOLD)));
		stream.put(2, new OverlayElement(1, homeDocument, Sets.newHashSet(TumblerAddress.ITALIC)));
		stream.put(3, new OverlayElement(1, homeDocument, Sets.newHashSet(TumblerAddress.STRIKE_THROUGH)));
		stream.put(4, new OverlayElement(1, homeDocument, Sets.newHashSet(TumblerAddress.UNDERLINE)));

		stream.delete(new VariantSpan(2, 2));
	
		List<OverlayElement> results = stream.getStreamElements();
	
		assertEquals(2, results.size());

		assertEquals(results.get(0), new OverlayElement(1, homeDocument, Sets.newHashSet(TumblerAddress.BOLD)));
		assertEquals(results.get(1), new OverlayElement(1, homeDocument, Sets.newHashSet(TumblerAddress.UNDERLINE)));
	}

	@Test
	public void deleteSomeBytes() throws Exception {
		// * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.delete(new VariantSpan(8, 7));
		List<SpanElement> spans = stream.getStreamElements();
		assertEquals(new SpanElement(100, 6, homeDocument), spans.get(0));
		assertEquals(new SpanElement(200, 1, homeDocument), spans.get(1));
		assertEquals(new SpanElement(303, 1, homeDocument), spans.get(2));
		assertEquals(new SpanElement(350, 1, homeDocument), spans.get(3));
		assertEquals(new SpanElement(360, 6, homeDocument), spans.get(4));

		assertEquals(5, spans.size());
	}

	@Test
	public void deleteSpan() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);
		stream.put(1, new SpanElement(1, 1, homeDocument));
		stream.put(2, new SpanElement(2, 1, homeDocument));
		stream.put(3, new SpanElement(3, 1, homeDocument));
		stream.put(4, new SpanElement(4, 1, homeDocument));

		stream.delete(new VariantSpan(2, 2));
	
		List<SpanElement> results = stream.getStreamElements();
	
		assertEquals(2, results.size());
		assertTrue(results.contains(new SpanElement(1, 1, homeDocument)));
		assertTrue(results.contains(new SpanElement(4, 1, homeDocument)));
	}

	@Test
	public void deleteWithSplit() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.delete(new VariantSpan(12, 6));
		List<SpanElement> spans = stream.getStreamElements();

		assertEquals(new SpanElement(100, 6, homeDocument), spans.get(0));
		assertEquals(new SpanElement(200, 3, homeDocument), spans.get(1));
		assertEquals(new SpanElement(250, 2, homeDocument), spans.get(2));
		assertEquals(new SpanElement(361, 5, homeDocument), spans.get(3));
		assertEquals(4, spans.size());
	}

	@Test
	public void getInvariantSpan() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);
		List<SpanElement> spans = Arrays.asList(new SpanElement(1, 11, homeDocument));
		stream.putElements(1, spans);

		List<SpanElement> results = stream.getStreamElements(new VariantSpan(5, 6));

		assertEquals(new SpanElement(6, 6, homeDocument), results.get(0));
	}

	@Test
	public void getInvariantSpansBeyondWidthOk() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		List<SpanElement> spans = stream.getStreamElements(new VariantSpan(10, 700));
		assertEquals(new SpanElement(250, 2, homeDocument), spans.get(0));
		assertEquals(new SpanElement(300, 4, homeDocument), spans.get(1));
		assertEquals(new SpanElement(350, 1, homeDocument), spans.get(2));
		assertEquals(new SpanElement(360, 6, homeDocument), spans.get(3));
	}

	@Test
	public void getInvariantSpansMiddle() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		List<SpanElement> spans = stream.getStreamElements(new VariantSpan(10, 7));

		assertEquals(new SpanElement(250, 2, homeDocument), spans.get(0));
		assertEquals(new SpanElement(300, 4, homeDocument), spans.get(1));
		assertEquals(new SpanElement(350, 1, homeDocument), spans.get(2));
	}

	@Test
	public void getInvariantSpansMiddle2() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		List<SpanElement> spans = stream.getStreamElements(new VariantSpan(10, 6, homeDocument));
		assertEquals(new SpanElement(250, 2, homeDocument), spans.get(0));
		assertEquals(new SpanElement(300, 4, homeDocument), spans.get(1));
	}

	@Test
	public void getInvariantSpansRangeEdgeLeft() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		List<SpanElement> spans = stream.getStreamElements(new VariantSpan(1, 14));
		assertEquals(new SpanElement(100, 6, homeDocument), spans.get(0));
		assertEquals(new SpanElement(200, 3, homeDocument), spans.get(1));
		assertEquals(new SpanElement(250, 2, homeDocument), spans.get(2));
		assertEquals(new SpanElement(300, 3, homeDocument), spans.get(3));
		assertEquals(4, spans.size());
	}

	@Test
	public void getInvariantSpansSmallWidthLeft() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		List<SpanElement> spans = stream.getStreamElements(new VariantSpan(1, 1));
		assertEquals(new SpanElement(100, 1, homeDocument), spans.get(0));
	}

	@Test
	public void getInvariantSpansSmallWidthRight() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		List<SpanElement> spans = stream.getStreamElements(new VariantSpan(21, 1));
		assertEquals(new SpanElement(365, 1, homeDocument), spans.get(0));
	}

	@Test
	public void getVariantSpansSingle() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);
		List<SpanElement> spans = Arrays.asList(new SpanElement(7, 5, homeDocument), new SpanElement(4, 3, homeDocument),
				new SpanElement(1, 3, homeDocument));
		stream.putElements(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new SpanElement(7, 5, homeDocument));
		assertEquals(result.get(0), new VariantSpan(1, 5, homeDocument.toExternalForm()));

		SpanElement invariantSpan = stream.getStreamElements(new VariantSpan(1, 5)).get(0);
		assertEquals(new SpanElement(7, 5, homeDocument), invariantSpan);
	}

	@Test
	public void getVariantSpansTwo() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);
		List<SpanElement> spans = Arrays.asList(new SpanElement(7, 5, homeDocument), new SpanElement(4, 3, homeDocument),
				new SpanElement(1, 3, homeDocument));
		stream.putElements(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new SpanElement(1, 6, homeDocument));
		assertEquals(2, result.size());
		assertEquals(new VariantSpan(6, 3, homeDocument), result.get(0));
		assertEquals(new VariantSpan(9, 3, homeDocument), result.get(1));

		System.out.println("Actual:" + result);
		System.out.println("Expected:" + new VariantSpan(6, 6));

		List<SpanElement> invariantSpans = stream.getStreamElements(new VariantSpan(6, 6));
		System.out.println(invariantSpans);
	}

	@Test
	public void getVariantSpansTwoIntersect() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);
		List<SpanElement> spans = Arrays.asList(new SpanElement(7, 5, homeDocument), new SpanElement(4, 3, homeDocument),
				new SpanElement(1, 3, homeDocument));
		stream.putElements(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new SpanElement(2, 5, homeDocument));
		assertEquals(2, result.size());
		assertEquals(new VariantSpan(6, 3, homeDocument), result.get(0));
		assertEquals(new VariantSpan(10, 2, homeDocument), result.get(1));
	}

	@Test
	public void getVariantSpansTwoIntersect2() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);
		List<SpanElement> spans = Arrays.asList(new SpanElement(7, 5, homeDocument), new SpanElement(4, 3, homeDocument),
				new SpanElement(1, 3, homeDocument));
		stream.putElements(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new SpanElement(1, 4, homeDocument));
		assertEquals(2, result.size());
		assertEquals(new VariantSpan(6, 1, homeDocument), result.get(0));
		assertEquals(new VariantSpan(9, 3, homeDocument), result.get(1));
	}

	@Test
	public void index() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		SpanElement span = stream.index(12);
		assertEquals(new SpanElement(300, 4, homeDocument), span);
	}

	@Test
	public void index11() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		SpanElement span = stream.index(11);
		assertEquals(new SpanElement(250, 2, homeDocument), span);
		span = stream.index(10);
		assertEquals(new SpanElement(250, 2, homeDocument), span);

	}

	@Test
	public void indexNull() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		SpanElement span = stream.index(100);
		assertNull(span);
	}

	@Test
	public void indexStart() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.move(2, new VariantSpan(12, 3));
		List<SpanElement> spans = stream.getStreamElements();
		System.out.println(spans);
	}
/*
	@Test
	public void overlay() throws Exception {
		OverlayElement os1 = new OverlayElement(1, homeDocument, Sets.newHashSet(TumblerAddress.BOLD));
		OverlayElement os2 = new OverlayElement(1, homeDocument, Sets.newHashSet(TumblerAddress.ITALIC));
		OverlayElement os3 = new OverlayElement(1, homeDocument, Sets.newHashSet(TumblerAddress.STRIKE_THROUGH));
		OverlayElement os4 = new OverlayElement(1, homeDocument, Sets.newHashSet(TumblerAddress.UNDERLINE));
	
		VariantStream<OverlayElement> stream = new RopeVariantStream<>(homeDocument);
		stream.put(1, os1);
		stream.put(2, os2);
		stream.put(3, os3);
		stream.put(4, os4);

		stream.toggleOverlay(new VariantSpan(2, 2), TumblerAddress.BOLD);

		List<OverlayElement> results = stream.getStreamElements();
	
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
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.put(12, new SpanElement(500, 34, homeDocument));
		List<SpanElement> spans = stream.getStreamElements();
		System.out.println(spans);
	}

	@Test
	public void putSmallParitions() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);
		stream.put(1, new SpanElement(1, 1, homeDocument));
		stream.put(2, new SpanElement(2, 1, homeDocument));
		stream.put(3, new SpanElement(3, 1, homeDocument));
		stream.put(4, new SpanElement(4, 1, homeDocument));
		stream.put(5, new SpanElement(5, 1, homeDocument));
		stream.put(6, new SpanElement(6, 1, homeDocument));
		stream.put(7, new SpanElement(7, 1, homeDocument));
		stream.put(8, new SpanElement(8, 1, homeDocument));

		List<SpanElement> spans = stream.getStreamElements(new VariantSpan(3, 4));

		assertEquals(new SpanElement(3, 1, homeDocument), spans.get(0));
		assertEquals(new SpanElement(4, 1, homeDocument), spans.get(1));
		assertEquals(new SpanElement(5, 1, homeDocument), spans.get(2));
		assertEquals(new SpanElement(6, 1, homeDocument), spans.get(3));

		assertEquals(4, spans.size());

	}

	@Test
	public void putSmallParitions2() throws Exception {
		RopeVariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);
		stream.put(1, new SpanElement(1, 1, homeDocument));
		stream.put(2, new SpanElement(2, 1, homeDocument));
		stream.put(3, new SpanElement(3, 1, homeDocument));
		stream.put(4, new SpanElement(4, 1, homeDocument));
		stream.put(5, new SpanElement(5, 1, homeDocument));
		stream.put(6, new SpanElement(6, 1, homeDocument));
		stream.put(7, new SpanElement(7, 1, homeDocument));
		stream.put(8, new SpanElement(8, 1, homeDocument));

		List<SpanElement> spans = stream.getStreamElements(new VariantSpan(4, 2));

		assertEquals(new SpanElement(4, 1, homeDocument), spans.get(0));
		assertEquals(new SpanElement(5, 1, homeDocument), spans.get(1));

		assertEquals(2, spans.size());

	}

	@Test
	public void putSpans() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);
		List<SpanElement> spans = Arrays.asList(new SpanElement(7, 5, homeDocument), new SpanElement(4, 3, homeDocument),
				new SpanElement(1, 3, homeDocument));
		
		stream.putElements(1, spans);
		List<SpanElement> results = stream.getStreamElements();
		System.out.println(stream.getStreamElements());

		assertEquals(results.get(0), spans.get(0));
		assertEquals(results.get(1), spans.get(1));
		assertEquals(results.get(2), spans.get(2));
	}

	@Test
	public void putSpansSequence() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);

		stream.put(1, new SpanElement(1, 1, homeDocument));
		stream.put(2, new SpanElement(2, 1, homeDocument));
		stream.put(3, new SpanElement(3, 1, homeDocument));

		List<SpanElement> results = stream.getStreamElements();

		assertEquals(results.get(0), new SpanElement(1, 1, homeDocument));
		assertEquals(results.get(1), new SpanElement(2, 1, homeDocument));
		assertEquals(results.get(2), new SpanElement(3, 1, homeDocument));
	}

	@Test
	public void putSpansSequence2() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument);

		stream.put(1, new SpanElement(1, 1, homeDocument));
		stream.put(2, new SpanElement(2, 1, homeDocument));
		stream.put(1, new SpanElement(3, 1, homeDocument));

		stream.put(2, new SpanElement(4, 1, homeDocument));

		List<SpanElement> results = stream.getStreamElements();

		assertEquals(results.get(0), new SpanElement(3, 1, homeDocument));// 3
		assertEquals(results.get(1), new SpanElement(4, 1, homeDocument));// 1
		assertEquals(results.get(2), new SpanElement(1, 1, homeDocument));// 4
		assertEquals(results.get(3), new SpanElement(2, 1, homeDocument));// 2

	}

	@Test
	public void putWithSplit() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.put(5, new SpanElement(500, 34, homeDocument));
		List<SpanElement> spans = stream.getStreamElements();
		System.out.println(spans);
	}

	@Test
	public void swap() throws Exception {
		VariantStream<SpanElement> stream = new RopeVariantStream<>(homeDocument, getA());
		stream.swap(new VariantSpan(1, 3), new VariantSpan(12, 3));
		List<SpanElement> spans = stream.getStreamElements();
		System.out.println(spans);

		assertEquals(new SpanElement(300, 3, homeDocument), spans.get(0));
		assertEquals(new SpanElement(103, 3, homeDocument), spans.get(1));
		assertEquals(new SpanElement(200, 3, homeDocument), spans.get(2));
		assertEquals(new SpanElement(250, 2, homeDocument), spans.get(3));
		assertEquals(new SpanElement(100, 3, homeDocument), spans.get(4));
		assertEquals(new SpanElement(303, 1, homeDocument), spans.get(5));
		assertEquals(new SpanElement(350, 1, homeDocument), spans.get(6));
		assertEquals(new SpanElement(360, 6, homeDocument), spans.get(7));

		assertEquals(8, spans.size());

		// [Span [start=300, width=3], Span [start=103, width=3],
		// Span [start=200, width=3], Span [start=250, width=2],
		// Span [start=303, width=1], Span [start=100, width=3],
		// Span [start=303, width=1], Span [start=350, width=1],
		// Span [start=360, width=6]]

	}

	@Test
	public void toggleOnOff() throws Exception {
		VariantStream<OverlayElement> stream = new RopeVariantStream<>(homeDocument);
		stream.put(1, new OverlayElement(1, homeDocument));
		stream.put(2, new OverlayElement(1, homeDocument));
		stream.put(3, new OverlayElement(1, homeDocument));
		stream.put(4, new OverlayElement(1, homeDocument));

		stream.toggleOverlay(new VariantSpan(2, 2), TumblerAddress.BOLD);
		stream.toggleOverlay(new VariantSpan(2, 2), TumblerAddress.BOLD);

		List<OverlayElement> results = stream.getStreamElements();
		assertEquals(4, results.size());
		
		for (OverlayElement seg : results) {
			assertFalse(seg.hasLinks());
		}
	}

}
