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
package org.oulipo.net.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.oulipo.net.TumblerAddress;

import com.google.common.collect.Sets;

public class RangeTumblerMatcherTest {

	@Test
	public void disjoint() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.501~1.1500"));
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertFalse(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500")));
	}

	@Test
	public void emptyAlwaysTrue() throws Exception {
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(new HashSet<>());
		assertTrue(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500")));
	}

	@Test
	public void exactOverlap() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.250~1.500"));
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertTrue(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.250~1.500")));
	}

	@Test
	public void noSpanFalse() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.250"));
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertFalse(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.250")));
	}

	@Test
	public void nullAlwaysTrue() throws Exception {
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(null);
		assertTrue(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500")));
	}

	@Test
	public void overlap() throws Exception {
		Set<TumblerAddress> addresses = Sets.newHashSet(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.250~1.500"));
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertTrue(matcher.match(TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500")));
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
		TumblerAddress t1 = TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.1~1.500");
		TumblerAddress t2 = TumblerAddress.create("1.1.0.2.0.2.2.4.0.1.500~1.500");

		Set<TumblerAddress> addresses = Sets.newHashSet(t2);
		RangeTumblerMatcher matcher = new RangeTumblerMatcher(addresses);
		assertTrue(matcher.match(t1));

		addresses = Sets.newHashSet(t1);
		matcher = new RangeTumblerMatcher(addresses);
		assertTrue(matcher.match(t2));
	}

}
