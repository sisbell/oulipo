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
