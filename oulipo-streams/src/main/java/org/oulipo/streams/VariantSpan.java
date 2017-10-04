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

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;

/**
 * This span is the public interface that clients see. As an example, if the
 * variant span is 1.10~1.50, then this represents the content in the document
 * between bytes 10 and 60. The content in this range is variant (or mutable)
 * and can be different after user edits.
 *
 */
public final class VariantSpan extends Span {

	private VariantSpan() {
	}

	/**
	 * Constructs a <code>VariantSpan</code> starting at the specified start
	 * position with the specified width
	 * 
	 * @param start
	 *            the character position this span starts at. Must be greater than
	 *            0.
	 * @param width
	 *            the number of characters in this span. Must be greater than 0
	 * 
	 * @throws MalformedSpanException
	 *             if the start or width is an illegal value
	 */
	public VariantSpan(long start, long width) throws MalformedSpanException {
		super(start, width);
	}
	
	public VariantSpan(long start, long width, String homeDocument) throws MalformedSpanException {
		super(start, width, homeDocument);
	}

	public VariantSpan(TumblerAddress tumbler) throws MalformedSpanException {
		super(tumbler.spanStart(), tumbler.spanWidth());
	}

}
