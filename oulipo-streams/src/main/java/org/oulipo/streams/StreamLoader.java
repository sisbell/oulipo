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

/**
 * Provides services for loading variant and invariant streams
 */
public interface StreamLoader {

	/**
	 * Flushes the cache
	 */
	void flushVariantCache();

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
	VariantStream openVariantStream(TumblerAddress homeDocument) throws IOException, MalformedSpanException;

	/**
	 * Writes an op code to an underlying stream. Op codes handle basic editing
	 * operations like inserting and deleting text. This will be called by an
	 * instance of an <code>OulipoMachine</code> prior to modifying the
	 * <code>VariantStream</code>
	 * 
	 * @see org.oulipo.machine.stream.opcodes.Op
	 * 
	 * @param op
	 *            the op code to write
	 * @return true if writing op code was successful
	 */
	boolean writeOp(byte[] op);

}
