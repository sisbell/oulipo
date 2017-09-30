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
package org.oulipo.net.matchers;

import java.util.HashSet;
import java.util.Set;

import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.net.TumblerMatcher;

/**
 * This matcher is used for home set restrictions on queries
 *
 * Example use:
 *
 * TumblerAddressMatcher homeDocMatcher = new
 * TumblerAddressMatcher(Sets.newHashSet(documentAddress));
 */
public final class AddressTumblerMatcher implements TumblerMatcher {

	public static AddressTumblerMatcher createLinkTypeMatcher(String[] linkTypes) throws MalformedTumblerException {
		Set<TumblerAddress> linkTypeAddresses = new HashSet<>();
		if (linkTypes != null) {
			for (String linkType : linkTypes) {
				TumblerAddress linkAddress = TumblerAddress.create(linkType);
				if (!linkAddress.isLinkElement()) {
					throw new MalformedTumblerException("Must be a valid link address");
				}
				linkTypeAddresses.add(linkAddress);
			}
		}
		return new AddressTumblerMatcher(linkTypeAddresses);
	}

	private final Set<TumblerAddress> tumblers;

	public AddressTumblerMatcher(Set<TumblerAddress> tumblers) {
		this.tumblers = tumblers;
	}

	@Override
	/**
	 * Returns true if the specified tumbler matches any of the tumblerAdd
	 */
	public boolean match(TumblerAddress tumbler) {
		return tumblers == null || tumblers.isEmpty() || tumblers.contains(tumbler);

	}

}
