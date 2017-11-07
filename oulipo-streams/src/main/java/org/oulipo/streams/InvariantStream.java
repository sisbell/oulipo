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

import org.oulipo.net.MalformedSpanException;
import org.oulipo.streams.types.InvariantSpan;

/**
 * An <code>InvariantStream</code> handles adding and getting text from an
 * immutable data source. Since the source is immutable there are no methods for
 * deleting or modifying existing text.
 * 
 * The invariant stream is sometimes referred to as an IStream.
 * 
 * @see <a href="http://xanadu.com/tech/">IStream reference</a>
 */
public interface InvariantStream {

	/**
	 * Appends text to the end of the IStream. 
	 * 
	 * @param text
	 *            the text to append. Text must not be null.
	 * @return the InvariantSpan that references the appended text
	 * @throws IOException
	 *             if there is an I/O problem in reading or writing to the stream
	 * @throws MalformedSpanException
	 *             if the invariant span is malformed
	 */
	InvariantSpan append(String text) throws IOException, MalformedSpanException;

	/**
	 * Gets the text bounded by the specified invariantSpan. The text is pulled from
	 * the IStream between [invariantSpan.start, invariantSpan.start +
	 * invariantSpan.width)
	 * 
	 * @param invariantSpan
	 *            text bounds within IStream
	 * @return the String bounded by the specified invariantSpan
	 * @throws IOException
	 */
	String getText(InvariantSpan invariantSpan) throws IOException;
}
