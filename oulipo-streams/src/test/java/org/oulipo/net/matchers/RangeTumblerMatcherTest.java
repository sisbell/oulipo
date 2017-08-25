package org.oulipo.net.matchers;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.oulipo.net.TumblerAddress;

import com.google.common.collect.Sets;

public class RangeTumblerMatcherTest {

	@Test
	public void overlap() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.250~1.500"));
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertTrue(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500")));
	}
	
	@Test
	public void emptyAlwaysTrue() throws Exception {
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(new HashSet<>());
		assertTrue(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500")));
	}

	@Test
	public void nullAlwaysTrue() throws Exception {
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(null);
		assertTrue(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500")));
	}

	@Test
	public void noSpanFalse() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.250"));
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertFalse(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.250")));
	}

	
	@Test
	public void exactOverlap() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.250~1.500"));
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertTrue(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.250~1.500")));
	}
	
	@Test
	public void overlapEdge() throws Exception {
		TumblerAddress t1 = TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500");
		TumblerAddress t2 = TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.500~1.500");
		
		Set<TumblerAddress> addresses = Sets.newHashSet(t2);
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertTrue(matcher.match(t1));

		addresses = Sets.newHashSet(t1);
		matcher = new RangeTumblerMatcher(addresses);
		assertTrue(matcher.match(t2));	
	}
	
	@Test
	public void overlapEdge2() throws Exception {
		TumblerAddress t1 = TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.1000");
		TumblerAddress t2 = TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.500~1.500");
		
		Set<TumblerAddress> addresses = Sets.newHashSet(t2);
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertTrue(matcher.match(t1));

		addresses = Sets.newHashSet(t1);
		matcher = new RangeTumblerMatcher(addresses);
		assertTrue(matcher.match(t2));	
	}


	
	@Test
	public void disjoint() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.501~1.1500"));
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertFalse(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500")));
	}

}
