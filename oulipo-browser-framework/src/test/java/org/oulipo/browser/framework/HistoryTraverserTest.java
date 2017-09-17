package org.oulipo.browser.framework;

import static org.junit.Assert.*;

import org.junit.Test;

public class HistoryTraverserTest {

	@Test
	public void singleNoIteration() {
		HistoryTraverser ht = new HistoryTraverser();
		ht.add("a");
		
		assertFalse(ht.hasNext());
		assertFalse(ht.hasPrevious());
	}
	
	@Test
	public void twoHasBack() {
		HistoryTraverser ht = new HistoryTraverser();
		ht.add("a");
		ht.add("b");

		assertFalse(ht.hasNext());
		assertTrue(ht.hasPrevious());

	}
	
	@Test
	public void goBack() {
		HistoryTraverser ht = new HistoryTraverser();
		ht.add("a");
		ht.add("b");
		assertEquals("a", ht.previous());
	}
	
	@Test
	public void goBackForward() {
		HistoryTraverser ht = new HistoryTraverser();
		ht.add("a");
		ht.add("b");
		ht.add("c");
		ht.add("d");

		assertEquals("c", ht.previous());
		assertEquals("b", ht.previous());
		assertEquals("a", ht.previous());
		
		assertEquals("b", ht.next());
		assertEquals("c", ht.next());
		assertEquals("d", ht.next());
		assertEquals(null, ht.next());
		assertEquals("c", ht.previous());
	}
	
	@Test
	public void goBackAndAdd() {
		HistoryTraverser ht = new HistoryTraverser();
		ht.add("a");
		ht.add("b");
		ht.add("c");
		ht.add("d");

		assertEquals("c", ht.previous());
		assertEquals("b", ht.previous());
		ht.add("e");
		assertEquals("b", ht.previous());
		assertEquals("e", ht.next());
		assertEquals(null, ht.next());
	}
	
	@Test
	public void goBackAndAdd2() {
		HistoryTraverser ht = new HistoryTraverser();
		ht.add("a");
		ht.add("b");
		ht.add("c");
		ht.add("d");

		assertEquals("c", ht.previous());
		assertEquals("b", ht.previous());
		ht.add("e");
		assertEquals(null, ht.next());
	}


}
