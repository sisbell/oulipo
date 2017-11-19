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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.oulipo.streams.impl.NodeFactory.getA;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.overlays.PresenterOverlay;
import org.oulipo.streams.types.InvariantSpan;
import org.oulipo.streams.types.OverlayStream;

import com.google.common.collect.Sets;

/**
 * 
 * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
 */
public class RopeVariantStreamTest {

	public static final String documentHash = "fakeHash";

	@Test
	public void applyOverlays() throws Exception {
		VariantStream<OverlayStream> stream = new RopeVariantStream<>(documentHash);
		stream.put(1, new OverlayStream(10));

		stream.applyOverlays(new VariantSpan(1, 5), Sets.newHashSet(PresenterOverlay.BOLD_OVERLAY));

		List<OverlayStream> results = stream.getStreamElements();

		assertEquals(2, results.size());

		assertTrue(results.get(0).hasLinkType(PresenterOverlay.BOLD_OVERLAY));
		assertFalse(results.get(1).hasLinkType(PresenterOverlay.BOLD_OVERLAY));
	}

	@Test
	public void boldAndItalicOverlap() throws Exception {
		VariantStream<OverlayStream> stream = new RopeVariantStream<>(documentHash);
		stream.put(1, new OverlayStream(10));

		stream.applyOverlays(new VariantSpan(1, 5), Sets.newHashSet(PresenterOverlay.BOLD_OVERLAY));
		stream.applyOverlays(new VariantSpan(1, 5), Sets.newHashSet(PresenterOverlay.ITALIC_OVERLAY));

		List<OverlayStream> results = stream.getStreamElements();

		assertEquals(2, results.size());
		assertTrue(results.get(0).hasLinkType(PresenterOverlay.BOLD_OVERLAY));
		assertTrue(results.get(0).hasLinkType(PresenterOverlay.ITALIC_OVERLAY));

		assertFalse(results.get(1).hasLinkType(PresenterOverlay.BOLD_OVERLAY));
		assertFalse(results.get(1).hasLinkType(PresenterOverlay.ITALIC_OVERLAY));
	}

	@Test
	public void boldAndItalicPartition() throws Exception {
		VariantStream<OverlayStream> stream = new RopeVariantStream<>(documentHash);
		stream.put(1, new OverlayStream(10));

		stream.applyOverlays(new VariantSpan(1, 5), Sets.newHashSet(PresenterOverlay.BOLD_OVERLAY));
		stream.applyOverlays(new VariantSpan(2, 9), Sets.newHashSet(PresenterOverlay.ITALIC_OVERLAY));

		List<OverlayStream> results = stream.getStreamElements();

		assertEquals(3, results.size());

		assertEquals(1, results.get(0).linkCount());
		assertTrue(results.get(0).hasLinkType(PresenterOverlay.BOLD_OVERLAY));
		assertEquals(1, results.get(0).getWidth());

		assertEquals(2, results.get(1).linkCount());
		assertTrue(results.get(1).hasLinkType(PresenterOverlay.BOLD_OVERLAY));
		assertTrue(results.get(1).hasLinkType(PresenterOverlay.ITALIC_OVERLAY));
		assertEquals(4, results.get(1).getWidth());

		assertTrue(results.get(2).hasLinkType(PresenterOverlay.ITALIC_OVERLAY));
		assertEquals(5, results.get(2).getWidth());

	}

	@Test
	public void boldItalicSmallPartitions() throws Exception {
		VariantStream<OverlayStream> stream = new RopeVariantStream<>(documentHash);
		stream.put(1, new OverlayStream(1, "a"));
		stream.put(2, new OverlayStream(1, "b"));
		stream.put(3, new OverlayStream(1, "c"));
		stream.put(4, new OverlayStream(1, "d"));
		stream.put(5, new OverlayStream(1, "e"));
		stream.put(6, new OverlayStream(1, "f"));
		stream.put(7, new OverlayStream(1, "g"));
		stream.put(8, new OverlayStream(1, "h"));

		stream.applyOverlays(new VariantSpan(3, 4), Sets.newHashSet(PresenterOverlay.BOLD_OVERLAY));// 3,4,5,6

		List<OverlayStream> overlays = stream.getStreamElements();
		assertEquals(PresenterOverlay.BOLD_OVERLAY, overlays.get(2).linkTypes.iterator().next());
		assertEquals(PresenterOverlay.BOLD_OVERLAY, overlays.get(3).linkTypes.iterator().next());
		assertEquals(PresenterOverlay.BOLD_OVERLAY, overlays.get(4).linkTypes.iterator().next());
		assertEquals(PresenterOverlay.BOLD_OVERLAY, overlays.get(5).linkTypes.iterator().next());

		stream.applyOverlays(new VariantSpan(4, 2), Sets.newHashSet(PresenterOverlay.ITALIC_OVERLAY));// 4,5
		overlays = stream.getStreamElements();

		assertEquals(PresenterOverlay.BOLD_OVERLAY, overlays.get(2).linkTypes.iterator().next());
		assertEquals(2, overlays.get(3).linkTypes.size());
		assertEquals(2, overlays.get(4).linkTypes.size());
		assertEquals(PresenterOverlay.BOLD_OVERLAY, overlays.get(5).linkTypes.iterator().next());
	}

	@Test
	public void boldItalicSmallPartitionsSimple() throws Exception {
		VariantStream<OverlayStream> stream = new RopeVariantStream<>(documentHash);
		stream.put(1, new OverlayStream(1, "a"));
		stream.put(2, new OverlayStream(1, "b"));

		stream.applyOverlays(new VariantSpan(2, 1), Sets.newHashSet(PresenterOverlay.BOLD_OVERLAY));
		List<OverlayStream> overlays = stream.getStreamElements();

		assertEquals(0, overlays.iterator().next().linkTypes.size());

		Iterator<OverlayStream> it = overlays.iterator();
		it.next();
		assertEquals(PresenterOverlay.BOLD_OVERLAY, it.next().linkTypes.iterator().next());
	}

	@Test
	public void copy() throws Exception {
		// * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
		// Expected: 100, 300, 301, 302, 101, 102, 103, 104, 105, 200, 201, 202, 250,
		// 251, 303, 350, 360], 361....
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.copy(2, new VariantSpan(12, 3));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(100, 1, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(301, 3, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(3));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(4));
		assertEquals(new InvariantSpan(300, 4, documentHash), spans.get(5));
		assertEquals(new InvariantSpan(350, 1, documentHash), spans.get(6));
		assertEquals(new InvariantSpan(360, 6, documentHash), spans.get(7));

	}

	@Test
	public void delete() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.delete(new VariantSpan(12, 4));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(100, 6, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(350, 1, documentHash), spans.get(3));
		assertEquals(new InvariantSpan(360, 6, documentHash), spans.get(4));

		assertEquals(5, spans.size());
	}

	@Test
	public void delete2() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.delete(new VariantSpan(12, 5));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(100, 6, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(360, 6, documentHash), spans.get(3));
		assertEquals(4, spans.size());
	}

	@Test
	public void delete3() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		List<InvariantSpan> spans = Arrays.asList(new InvariantSpan(1, 11, documentHash));
		stream.putElements(1, spans);

		stream.delete(new VariantSpan(1, 5));

		List<InvariantSpan> results = stream.getStreamElements();
		assertEquals(new InvariantSpan(6, 6, documentHash), results.get(0));
	}

	@Test
	public void delete6To19() throws Exception {// 6, 19
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.delete(new VariantSpan(6, 19));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(100, 5, documentHash), spans.get(0));
		assertEquals(1, spans.size());
	}

	@Test
	public void deleteAll() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.delete(new VariantSpan(1, 22));
		List<InvariantSpan> spans = stream.getStreamElements();
		assertEquals(0, spans.size());
	}

	@Test
	public void deleteBeyondRangeOk() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.delete(new VariantSpan(12, 100));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(100, 6, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(2));
		assertEquals(3, spans.size());
	}

	@Test
	public void deleteFirstCharacter() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		List<InvariantSpan> spans = Arrays.asList(new InvariantSpan(1, 4, documentHash));
		stream.putElements(1, spans);

		stream.delete(new VariantSpan(1, 1));

		List<InvariantSpan> results = stream.getStreamElements();
		assertEquals(new InvariantSpan(2, 3, documentHash), results.get(0));
	}

	@Test
	public void deleteLastCharacter() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		List<InvariantSpan> spans = Arrays.asList(new InvariantSpan(1, 4, documentHash));
		stream.putElements(1, spans);

		stream.delete(new VariantSpan(4, 1));

		List<InvariantSpan> results = stream.getStreamElements();
		assertEquals(new InvariantSpan(1, 3, documentHash), results.get(0));
	}

	@Test
	public void deleteMiddle() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		List<InvariantSpan> spans = Arrays.asList(new InvariantSpan(1, 4, documentHash));
		stream.putElements(1, spans);

		stream.delete(new VariantSpan(2, 1));

		List<InvariantSpan> results = stream.getStreamElements();

		assertEquals(new InvariantSpan(1, 1, documentHash), results.get(0));
		assertEquals(new InvariantSpan(3, 2, documentHash), results.get(1));
	}

	@Test
	public void deleteOneByte() throws Exception {
		// * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
		// 100, 101, 102, 103, 104, 105, 200, 201, 202, 250, 251, [300], 301, 302] 303

		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.delete(new VariantSpan(12, 1));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(100, 6, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(301, 3, documentHash), spans.get(3));
		assertEquals(new InvariantSpan(350, 1, documentHash), spans.get(4));
		assertEquals(new InvariantSpan(360, 6, documentHash), spans.get(5));

		assertEquals(6, spans.size());
	}

	@Test
	public void deleteOneByteInFirstElement() throws Exception {// 6, 19
		// * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
		// 100, 101, 102, 103, 104, [105] 200, 201, 202, 250, 251, 300, 301, 302] 303

		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.delete(new VariantSpan(6, 1));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(100, 5, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(300, 4, documentHash), spans.get(3));
		assertEquals(new InvariantSpan(350, 1, documentHash), spans.get(4));
		assertEquals(new InvariantSpan(360, 6, documentHash), spans.get(5));

		assertEquals(6, spans.size());
	}

	@Test
	public void deleteOverlay() throws Exception {
		VariantStream<OverlayStream> stream = new RopeVariantStream<>(documentHash);
		stream.put(1, new OverlayStream(1, Sets.newHashSet(PresenterOverlay.BOLD_OVERLAY)));
		stream.put(2, new OverlayStream(1, Sets.newHashSet(PresenterOverlay.ITALIC_OVERLAY)));
		stream.put(3, new OverlayStream(1, Sets.newHashSet(PresenterOverlay.STRIKE_THROUGH_OVERLAY)));
		stream.put(4, new OverlayStream(1, Sets.newHashSet(PresenterOverlay.UNDERLINE_OVERLAY)));

		stream.delete(new VariantSpan(2, 2));

		List<OverlayStream> results = stream.getStreamElements();

		assertEquals(2, results.size());

		assertEquals(results.get(0), new OverlayStream(1, Sets.newHashSet(PresenterOverlay.BOLD_OVERLAY)));
		assertEquals(results.get(1), new OverlayStream(1, Sets.newHashSet(PresenterOverlay.UNDERLINE_OVERLAY)));
	}

	@Test
	public void deleteSomeBytes() throws Exception {
		// * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
		// Expected: 100, 101, 102, 103, 104, 105, 200, [201, 202, 250, 251, 300, 301,
		// 302] 303
		// Actual: 100, 101, 102, 103, 104, 105, [200, 201, 202, 250, 251, 300, 301],
		// 302, 303...
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.delete(new VariantSpan(8, 7));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(100, 6, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(200, 1, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(303, 1, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(350, 1, documentHash), spans.get(3));
		assertEquals(new InvariantSpan(360, 6, documentHash), spans.get(4));

		assertEquals(5, spans.size());
	}

	@Test
	public void deleteSpan() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		stream.put(1, new InvariantSpan(1, 1, documentHash));
		stream.put(2, new InvariantSpan(2, 1, documentHash));
		stream.put(3, new InvariantSpan(3, 1, documentHash));
		stream.put(4, new InvariantSpan(4, 1, documentHash));

		stream.delete(new VariantSpan(2, 2));

		List<InvariantSpan> results = stream.getStreamElements();

		assertEquals(2, results.size());
		assertTrue(results.contains(new InvariantSpan(1, 1, documentHash)));
		assertTrue(results.contains(new InvariantSpan(4, 1, documentHash)));
	}

	@Test
	public void deleteWithSplit() throws Exception {
		// * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
		// Expected: 100, 101, 102, 103, 104, 105, 200, 201, 202, 250, 251 [300, 301,
		// 302, 303, 350, 360], 361....
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.delete(new VariantSpan(12, 6));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(100, 6, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(361, 5, documentHash), spans.get(3));

		assertEquals(4, spans.size());
	}

	@Test
	public void getInvariantSpan() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		List<InvariantSpan> spans = Arrays.asList(new InvariantSpan(1, 11, documentHash));
		stream.putElements(1, spans);

		List<InvariantSpan> results = stream.getStreamElements(new VariantSpan(5, 6));

		assertEquals(new InvariantSpan(6, 6, documentHash), results.get(0));
	}

	@Test
	public void getInvariantSpansBeyondWidthOk() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		List<InvariantSpan> spans = stream.getStreamElements(new VariantSpan(10, 700));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(300, 4, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(350, 1, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(360, 6, documentHash), spans.get(3));
	}

	@Test
	public void getInvariantSpansMiddle() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		List<InvariantSpan> spans = stream.getStreamElements(new VariantSpan(10, 7));

		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(300, 4, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(350, 1, documentHash), spans.get(2));
	}

	@Test
	public void getInvariantSpansMiddle2() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		List<InvariantSpan> spans = stream.getStreamElements(new VariantSpan(10, 6, documentHash));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(300, 4, documentHash), spans.get(1));
	}

	@Test
	public void getInvariantSpansRangeEdgeLeft() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		List<InvariantSpan> spans = stream.getStreamElements(new VariantSpan(1, 14));
		assertEquals(new InvariantSpan(100, 6, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(300, 3, documentHash), spans.get(3));
		assertEquals(4, spans.size());
	}

	@Test
	public void getInvariantSpansSmallWidthLeft() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		List<InvariantSpan> spans = stream.getStreamElements(new VariantSpan(1, 1));
		assertEquals(new InvariantSpan(100, 1, documentHash), spans.get(0));
	}

	@Test
	public void getInvariantSpansSmallWidthRight() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		List<InvariantSpan> spans = stream.getStreamElements(new VariantSpan(21, 1));
		assertEquals(new InvariantSpan(365, 1, documentHash), spans.get(0));
	}

	@Test
	public void getVariantSpansSingle() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		List<InvariantSpan> spans = Arrays.asList(new InvariantSpan(7, 5, documentHash),
				new InvariantSpan(4, 3, documentHash), new InvariantSpan(1, 3, documentHash));
		stream.putElements(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new InvariantSpan(7, 5, documentHash));
		assertEquals(result.get(0), new VariantSpan(1, 5, documentHash));

		InvariantSpan invariantSpan = stream.getStreamElements(new VariantSpan(1, 5)).get(0);
		assertEquals(new InvariantSpan(7, 5, documentHash), invariantSpan);
	}

	@Test
	public void getVariantSpansTwo() throws Exception {
		// [7,5]. [4,3], [1,3]
		// 7, 8, 9, 10, 11, [4, 5, 6, 1, 2, 3]
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		List<InvariantSpan> spans = Arrays.asList(new InvariantSpan(7, 5, documentHash),
				new InvariantSpan(4, 3, documentHash), new InvariantSpan(1, 3, documentHash));
		stream.putElements(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new InvariantSpan(1, 6, documentHash));
		assertEquals(2, result.size());
		assertEquals(new VariantSpan(6, 3, documentHash), result.get(0));
		assertEquals(new VariantSpan(9, 3, documentHash), result.get(1));

		List<InvariantSpan> invariantSpans = stream.getStreamElements(new VariantSpan(6, 6));
		assertEquals(new InvariantSpan(4, 3, documentHash), invariantSpans.get(0));
		assertEquals(new InvariantSpan(1, 3, documentHash), invariantSpans.get(1));
	}

	@Test
	public void getVariantSpansTwoIntersect() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		List<InvariantSpan> spans = Arrays.asList(new InvariantSpan(7, 5, documentHash),
				new InvariantSpan(4, 3, documentHash), new InvariantSpan(1, 3, documentHash));
		stream.putElements(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new InvariantSpan(2, 5, documentHash));
		assertEquals(2, result.size());
		assertEquals(new VariantSpan(6, 3, documentHash), result.get(0));
		assertEquals(new VariantSpan(10, 2, documentHash), result.get(1));
	}

	@Test
	public void getVariantSpansTwoIntersect2() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		List<InvariantSpan> spans = Arrays.asList(new InvariantSpan(7, 5, documentHash),
				new InvariantSpan(4, 3, documentHash), new InvariantSpan(1, 3, documentHash));
		stream.putElements(1, spans);
		List<VariantSpan> result = stream.getVariantSpans(new InvariantSpan(1, 4, documentHash));
		assertEquals(2, result.size());
		assertEquals(new VariantSpan(6, 1, documentHash), result.get(0));
		assertEquals(new VariantSpan(9, 3, documentHash), result.get(1));
	}

	@Test
	public void index() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		InvariantSpan span = stream.index(12);
		assertEquals(new InvariantSpan(300, 4, documentHash), span);
	}

	@Test
	public void index11() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		InvariantSpan span = stream.index(11);
		assertEquals(new InvariantSpan(250, 2, documentHash), span);
		span = stream.index(10);
		assertEquals(new InvariantSpan(250, 2, documentHash), span);

	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void indexNull() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		InvariantSpan span = stream.index(100);
		assertNull(span);
	}

	@Test
	public void moveSpan() throws Exception {
		// * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
		// Expected: 100, 300, 301, 302, 101, 102, 103, 104, 105, 200, 201, 202, 250,
		// 251, 303, 350, 360], 361....
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.move(2, new VariantSpan(12, 3));
		List<InvariantSpan> spans = stream.getStreamElements();
		assertEquals(new InvariantSpan(100, 1, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(300, 3, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(101, 5, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(3));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(4));
		assertEquals(new InvariantSpan(303, 1, documentHash), spans.get(5));
		assertEquals(new InvariantSpan(350, 1, documentHash), spans.get(6));
		assertEquals(new InvariantSpan(360, 6, documentHash), spans.get(7));
	}
	/*
	 * @Test public void overlay() throws Exception { OverlayElement os1 = new
	 * OverlayElement(1, homeDocument,
	 * Sets.newHashSet(PresenterOverlay.BOLD_OVERLAY)); OverlayElement os2 = new
	 * OverlayElement(1, homeDocument,
	 * Sets.newHashSet(PresenterOverlay.ITALIC_OVERLAY)); OverlayElement os3 = new
	 * OverlayElement(1, homeDocument,
	 * Sets.newHashSet(PresenterOverlay.STRIKE_THROUGH)); OverlayElement os4 = new
	 * OverlayElement(1, homeDocument, Sets.newHashSet(PresenterOverlay.UNDERLINE));
	 * 
	 * VariantStream<OverlayElement> stream = new RopeVariantStream<>(homeDocument);
	 * stream.put(1, os1); stream.put(2, os2); stream.put(3, os3); stream.put(4,
	 * os4);
	 * 
	 * stream.toggleOverlay(new VariantSpan(2, 2), PresenterOverlay.BOLD_OVERLAY);
	 * 
	 * List<OverlayElement> results = stream.getStreamElements();
	 * 
	 * OverlaySpan os1 = new OverlaySpan(1, 1, homeDocument); OverlaySpan os2 = new
	 * OverlaySpan(2, 1, homeDocument); OverlaySpan os3 = new OverlaySpan(3, 1,
	 * homeDocument); OverlaySpan os4 = new OverlaySpan(4, 1, homeDocument);
	 * 
	 * assertEquals(4, results.size()); assertTrue(results.contains(os1));
	 * assertTrue(results.contains(os2));
	 * 
	 * for(OverlaySpan span : results) { if(span.equals(os1)) { assertEquals(0,
	 * span.linkTypes.size()); } else if(span.equals(os2)) { assertEquals(1,
	 * span.linkTypes.size());
	 * assertTrue(span.hasLinkType(PresenterOverlay.BOLD_OVERLAY)); } else
	 * if(span.equals(os3)) { assertEquals(1, span.linkTypes.size());
	 * assertTrue(span.hasLinkType(PresenterOverlay.BOLD_OVERLAY)); } else
	 * if(span.equals(os4)) { assertEquals(0, span.linkTypes.size()); } } }
	 * 
	 * @Test public void overlayWithInsert() throws Exception { VariantStream stream
	 * = new RopeVariantStream(homeDocument); stream.put(new OverlaySpan(1, 1,
	 * homeDocument)); stream.put(new OverlaySpan(2, 1, homeDocument));
	 * stream.put(new OverlaySpan(3, 1, homeDocument)); stream.put(new
	 * OverlaySpan(4, 1, homeDocument));
	 * 
	 * stream.toggleOverlay(new VariantSpan(2, 2), PresenterOverlay.BOLD_OVERLAY);
	 * stream.put(new OverlaySpan(1, 2, homeDocument));
	 * 
	 * Set<OverlaySpan> results = stream.getOverlaySpans(); for(OverlaySpan span :
	 * results) { System.out.println(span); }
	 * 
	 * OverlaySpan os1 = new OverlaySpan(1, 1, homeDocument); OverlaySpan os2 = new
	 * OverlaySpan(2, 1, homeDocument); OverlaySpan os3 = new OverlaySpan(3, 1,
	 * homeDocument); OverlaySpan os4 = new OverlaySpan(4, 1, homeDocument);
	 * 
	 * assertEquals(4, results.size()); assertTrue(results.contains(os1));
	 * assertTrue(results.contains(os2));
	 * 
	 * for(OverlaySpan span : results) { if(span.equals(os1)) { assertEquals(0,
	 * span.linkTypes.size()); } else if(span.equals(os2)) { assertEquals(1,
	 * span.linkTypes.size());
	 * assertTrue(span.hasLinkType(PresenterOverlay.BOLD_OVERLAY)); } else
	 * if(span.equals(os3)) { assertEquals(1, span.linkTypes.size());
	 * assertTrue(span.hasLinkType(PresenterOverlay.BOLD_OVERLAY)); } else
	 * if(span.equals(os4)) { assertEquals(0, span.linkTypes.size()); } } }
	 */

	@Test
	public void put() throws Exception {
		// * [100, 6], [200,3], [250, 2], [300, 4], [350, 1], [360, 6]
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.put(12, new InvariantSpan(500, 34, documentHash));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(100, 6, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(500, 34, documentHash), spans.get(3));
		assertEquals(new InvariantSpan(300, 4, documentHash), spans.get(4));
		assertEquals(new InvariantSpan(350, 1, documentHash), spans.get(5));
		assertEquals(new InvariantSpan(360, 6, documentHash), spans.get(6));
	}

	@Test
	public void putSmallPartitions() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		stream.put(1, new InvariantSpan(1, 1, documentHash));
		stream.put(2, new InvariantSpan(2, 1, documentHash));
		stream.put(3, new InvariantSpan(3, 1, documentHash));
		stream.put(4, new InvariantSpan(4, 1, documentHash));
		stream.put(5, new InvariantSpan(5, 1, documentHash));
		stream.put(6, new InvariantSpan(6, 1, documentHash));
		stream.put(7, new InvariantSpan(7, 1, documentHash));
		stream.put(8, new InvariantSpan(8, 1, documentHash));

		List<InvariantSpan> spans = stream.getStreamElements(new VariantSpan(3, 4));

		assertEquals(new InvariantSpan(3, 1, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(4, 1, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(5, 1, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(6, 1, documentHash), spans.get(3));

		assertEquals(4, spans.size());

	}

	@Test
	public void putSmallPartitions2() throws Exception {
		RopeVariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		stream.put(1, new InvariantSpan(1, 1, documentHash));
		stream.put(2, new InvariantSpan(2, 1, documentHash));
		stream.put(3, new InvariantSpan(3, 1, documentHash));
		stream.put(4, new InvariantSpan(4, 1, documentHash));
		stream.put(5, new InvariantSpan(5, 1, documentHash));
		stream.put(6, new InvariantSpan(6, 1, documentHash));
		stream.put(7, new InvariantSpan(7, 1, documentHash));
		stream.put(8, new InvariantSpan(8, 1, documentHash));

		List<InvariantSpan> spans = stream.getStreamElements(new VariantSpan(4, 2));

		assertEquals(new InvariantSpan(4, 1, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(5, 1, documentHash), spans.get(1));

		assertEquals(2, spans.size());

	}

	@Test
	public void putSpans() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);
		List<InvariantSpan> spans = Arrays.asList(new InvariantSpan(7, 5, documentHash),
				new InvariantSpan(4, 3, documentHash), new InvariantSpan(1, 3, documentHash));

		stream.putElements(1, spans);
		List<InvariantSpan> results = stream.getStreamElements();

		assertEquals(results.get(0), spans.get(0));
		assertEquals(results.get(1), spans.get(1));
		assertEquals(results.get(2), spans.get(2));
	}

	@Test
	public void putSpansSequence() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);

		stream.put(1, new InvariantSpan(1, 1, documentHash));
		stream.put(2, new InvariantSpan(2, 1, documentHash));
		stream.put(3, new InvariantSpan(3, 1, documentHash));

		List<InvariantSpan> results = stream.getStreamElements();

		assertEquals(results.get(0), new InvariantSpan(1, 1, documentHash));
		assertEquals(results.get(1), new InvariantSpan(2, 1, documentHash));
		assertEquals(results.get(2), new InvariantSpan(3, 1, documentHash));
	}

	@Test
	public void putSpansSequence2() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash);

		stream.put(1, new InvariantSpan(1, 1, documentHash));
		stream.put(2, new InvariantSpan(2, 1, documentHash));
		stream.put(1, new InvariantSpan(3, 1, documentHash));

		stream.put(2, new InvariantSpan(4, 1, documentHash));

		List<InvariantSpan> results = stream.getStreamElements();

		assertEquals(results.get(0), new InvariantSpan(3, 1, documentHash));// 3
		assertEquals(results.get(1), new InvariantSpan(4, 1, documentHash));// 1
		assertEquals(results.get(2), new InvariantSpan(1, 1, documentHash));// 4
		assertEquals(results.get(3), new InvariantSpan(2, 1, documentHash));// 2

	}

	@Test
	public void putWithSplit() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.put(5, new InvariantSpan(500, 34, documentHash));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(100, 4, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(500, 34, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(104, 2, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(3));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(4));
		assertEquals(new InvariantSpan(300, 4, documentHash), spans.get(5));
		assertEquals(new InvariantSpan(350, 1, documentHash), spans.get(6));
		assertEquals(new InvariantSpan(360, 6, documentHash), spans.get(7));
	}

	@Test
	public void swap() throws Exception {
		VariantStream<InvariantSpan> stream = new RopeVariantStream<>(documentHash, getA());
		stream.swap(new VariantSpan(1, 3), new VariantSpan(12, 3));
		List<InvariantSpan> spans = stream.getStreamElements();

		assertEquals(new InvariantSpan(300, 3, documentHash), spans.get(0));
		assertEquals(new InvariantSpan(103, 3, documentHash), spans.get(1));
		assertEquals(new InvariantSpan(200, 3, documentHash), spans.get(2));
		assertEquals(new InvariantSpan(250, 2, documentHash), spans.get(3));
		assertEquals(new InvariantSpan(100, 3, documentHash), spans.get(4));
		assertEquals(new InvariantSpan(303, 1, documentHash), spans.get(5));
		assertEquals(new InvariantSpan(350, 1, documentHash), spans.get(6));
		assertEquals(new InvariantSpan(360, 6, documentHash), spans.get(7));

		assertEquals(8, spans.size());

		// [Span [start=300, width=3], Span [start=103, width=3],
		// Span [start=200, width=3], Span [start=250, width=2],
		// Span [start=303, width=1], Span [start=100, width=3],
		// Span [start=303, width=1], Span [start=350, width=1],
		// Span [start=360, width=6]]

	}

	@Test
	public void toggleOnOff() throws Exception {
		VariantStream<OverlayStream> stream = new RopeVariantStream<>(documentHash);
		stream.put(1, new OverlayStream(1));
		stream.put(2, new OverlayStream(1));
		stream.put(3, new OverlayStream(1));
		stream.put(4, new OverlayStream(1));

		stream.toggleOverlay(new VariantSpan(2, 2), PresenterOverlay.BOLD_OVERLAY);
		stream.toggleOverlay(new VariantSpan(2, 2), PresenterOverlay.BOLD_OVERLAY);

		List<OverlayStream> results = stream.getStreamElements();
		assertEquals(4, results.size());

		for (OverlayStream seg : results) {
			assertFalse(seg.hasLinks());
		}
	}

}
