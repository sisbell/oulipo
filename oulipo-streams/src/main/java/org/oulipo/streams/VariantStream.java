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
package org.oulipo.streams;

import java.io.IOException;
import java.util.List;

import org.oulipo.net.MalformedSpanException;

public interface VariantStream {

	default void copy(long characterPosition, List<VariantSpan> vspans) throws MalformedSpanException, IOException {
		long start = characterPosition;
		for (VariantSpan vspan : vspans) {
			copy(start, vspan);
			start += vspan.width;
		}
	}

	void copy(long characterPosition, VariantSpan variantSpan) throws MalformedSpanException, IOException;

	void delete(VariantSpan variantSpan) throws MalformedSpanException, IOException;

	InvariantSpans getInvariantSpans() throws MalformedSpanException;

	InvariantSpans getInvariantSpans(VariantSpan variantSpan) throws MalformedSpanException;

	List<VariantSpan> getVariantSpans(InvariantSpan invariantSpan) throws MalformedSpanException;

	/**
	 * Returns the <code>InvariantSpan<code> at the specified character position, or
	 * null if none exists at that position
	 * 
	 * @param characterPosition
	 * @return
	 */
	InvariantSpan index(long characterPosition);

	default void load(InvariantSpans spans) throws MalformedSpanException, IOException {
		put(1, spans.getInvariantSpans());
	}

	void move(long to, VariantSpan variantSpan) throws MalformedSpanException, IOException;

	void put(long characterPosition, InvariantSpan invariantSpan) throws MalformedSpanException, IOException;

	default void put(long characterPosition, List<InvariantSpan> ispans) throws MalformedSpanException, IOException {
		long start = characterPosition;
		for (int i = 0; i < ispans.size(); i++) {
			InvariantSpan ispan = ispans.get(i);
			put(start, ispan);
			start += (i == 0) ? ispan.width - 1 : ispan.width;
		}
	}

	void swap(VariantSpan v1, VariantSpan v2) throws MalformedSpanException, IOException;

}