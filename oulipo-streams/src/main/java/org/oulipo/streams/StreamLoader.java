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
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.types.Invariant;
import org.oulipo.streams.types.Overlay;

/**
 * Provides services for loading variant and invariant streams
 */
public interface StreamLoader {

	/**
	 * Flushes the cache
	 */
	void flushVariantCache();

	/**
	 * Returns most recent hash for document
	 * 
	 * @return most recent hash
	 */
	String getHash();

	/**
	 * Opens the <code>InvariantStream<code> for the specified document. If the
	 * invariant stream does not exist, then one is created.
	 * 
	 * @param homeDocument
	 *            the document address of the invariant stream
	 * @return an InvariantStream
	 * @throws IOException
	 *             if there is I/O problem with the stream of if the specified
	 *             documentTumbler is not a document address
	 */
	InvariantStream openInvariantStream(TumblerAddress homeDocument) throws IOException;

	/**
	 * Opens the <code>VariantStream</code> for the specified document.
	 * 
	 * @param homeDocument
	 *            the document address of the variant stream
	 * @return a VariantStream
	 * @throws IOException
	 * @throws MalformedSpanException
	 */
	VariantStream<Invariant> openInvariantVariantStream(TumblerAddress homeDocument)
			throws IOException, MalformedSpanException;

	VariantStream<Overlay> openOverlayVariantStream(TumblerAddress homeDocument)
			throws IOException, MalformedSpanException;

	void setHash(String hash);

}
