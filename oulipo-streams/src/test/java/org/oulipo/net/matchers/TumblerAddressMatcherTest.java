package org.oulipo.net.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.oulipo.net.TumblerAddress;

import com.google.common.collect.Sets;

public class TumblerAddressMatcherTest {

	@Test
	public void contains() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1"), TumblerAddress.create("1.2"));
		AddressTumblerMatcher matcher = new AddressTumblerMatcher(addresses);
		assertTrue(matcher.match(TumblerAddress.create("1.1")));
	}

	@Test
	public void notContains() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1"), TumblerAddress.create("1.2"));
		AddressTumblerMatcher matcher = new AddressTumblerMatcher(addresses);
		assertFalse(matcher.match(TumblerAddress.create("1.4")));
	}

	@Test
	public void nullConstructor() throws Exception {
		AddressTumblerMatcher matcher = new AddressTumblerMatcher(null);
		assertTrue(matcher.match(TumblerAddress.create("1.1")));
	}

}
