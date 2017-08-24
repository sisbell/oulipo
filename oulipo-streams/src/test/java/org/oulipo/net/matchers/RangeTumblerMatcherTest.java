package org.oulipo.net.matchers;

import static org.junit.Assert.*;

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
	public void overlapEdge() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.500~1.500"));
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertTrue(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500")));
	}

	
	@Test
	public void nooverlap() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.501~1.1500"));
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertFalse(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500")));
	}

}
