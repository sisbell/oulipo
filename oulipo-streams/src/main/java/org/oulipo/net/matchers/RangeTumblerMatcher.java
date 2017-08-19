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

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.net.TumblerMatcher;

/**
 * Matches a tumbler if the ranges overlap. If there is no span range in the tumbler address,
 * no match will be made.
 */
public final class RangeTumblerMatcher implements TumblerMatcher {

	private Set<TumblerAddress> ispansToMatch;

	public static RangeTumblerMatcher createFromInvariantSpans(
			String[] ispans) throws MalformedTumblerException, MalformedSpanException {

		Set<TumblerAddress> elementDataItems = new HashSet<>();
		if (ispans != null) {
			for (String ispan : ispans) {
				TumblerAddress ispanTumbler = TumblerAddress.create(ispan);

				if (!ispanTumbler.hasSpan()) {
					throw new MalformedSpanException(
							"Invalid Invariant Span: " + ispan);
				}
				elementDataItems.add(ispanTumbler);
			}
		}
		return new RangeTumblerMatcher(elementDataItems);
	}

	private RangeTumblerMatcher(Set<TumblerAddress> ispansToMatch) {
		this.ispansToMatch = ispansToMatch;
	}

	public boolean match(TumblerAddress address) {
		if (ispansToMatch == null || ispansToMatch.isEmpty()) {
			return true;
		}

		if (!address.hasSpan()) {
			return false;
		}
		for (TumblerAddress t : ispansToMatch) {
			// match overlap/range - any match return true
			int beginPos = address.spanStart();
			int endPos = beginPos + address.spanWidth();
			int startV = t.spanStart();
			int widthV = t.spanWidth();

			if (inRange(beginPos, startV, startV + widthV)
					|| inRange(endPos, startV, startV + widthV)
					|| inRange(startV, beginPos, endPos)
					|| inRange(startV + widthV, beginPos, endPos)) {
				return true;
			}

		}
		return false;

	}

	private static boolean inRange(int position, int start, int end) {
		return position >= start && position <= end;
	}

}
