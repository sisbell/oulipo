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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.opcodes.Op;

import com.google.common.base.Strings;

/**
 * Provides services for loading and pushing Oulipo OP codes.
 * 
 * OP codes designate document editing operations like put and deleting text.
 * 
 * OP codes are meant to be stored in a stream like a file or to be accessible
 * from a remote endpoint. An implementation of an <code>OulipoMachine</code>
 * will read these OP codes and build the document, storing the relevant bits
 * within the underlying variant and invariant streams.
 */
public interface OulipoMachine extends VariantStream, InvariantStream {

	void flush();

	/**
	 * Returns the home address of invariant spans within the invariant stream. The
	 * invariant stream may have a home document different from the one specified
	 * here. In this case, this document is pointing to another document.
	 * 
	 * @return
	 */
	TumblerAddress getHomeDocument();

	/**
	 * Gets the virtual content collection of the homeDocument. The virtual content
	 * is in order of how it should be displayed to the user.
	 * 
	 * @return ordered collection of VirtualContent
	 * @throws IOException
	 * @throws MalformedSpanException
	 *             if any of the spans is malformed or out of index range
	 */
	default List<VirtualContent> getVirtualContent() throws IOException, MalformedSpanException {
		List<VirtualContent> virtuals = new ArrayList<>();
		List<InvariantSpan> it = getInvariantSpans().getInvariantSpans();
		int order = 0;
		for (InvariantSpan span : it) {
			VirtualContent vc = new VirtualContent();
			vc.invariantSpan = span;

			vc.order = order++;
			if (Strings.isNullOrEmpty(span.homeDocument)) {
				vc.content = getText(span);
				vc.homeDocument = getHomeDocument();
			} else {
				vc.homeDocument = TumblerAddress.create(span.homeDocument);
			}

			virtuals.add(vc);
		}

		return virtuals;
	}

	/**
	 * Inserts the specified text at the specified (variant) character position. The
	 * text will be appended to the end of the invariant stream but its variant
	 * position and width will be stored to the variant stream.
	 * 
	 * @param characterPosition
	 *            the character position within the variant stream
	 * @param text
	 *            the text to insert
	 * @throws IOException
	 * @throws MalformedSpanException
	 */
	void insert(long characterPosition, String text) throws IOException, MalformedSpanException;

	/**
	 * Loads op codes from the specified input stream and pushes them into the
	 * <code>OulipoMachine</code> for processing
	 * 
	 * @param input
	 *            the input stream for stored OP codes
	 * 
	 * @throws IOException
	 * @throws MalformedSpanException
	 */
	default void loadOpCodes(DataInputStream input) throws IOException, MalformedSpanException {
		OpCodeReader reader = new OpCodeReader(input);
		Iterator<Op<?>> opCodes = reader.iterator();
		while (opCodes.hasNext()) {
			push(opCodes.next());
		}
		reader.close();
	}

	/**
	 * Loads op codes from the specified file and pushes them into the
	 * <code>OulipoMachine</code> for processing
	 * 
	 * @param file
	 *            the file for stored OP codes
	 * @throws IOException
	 * @throws MalformedSpanException
	 */
	default void loadOpCodes(File file) throws IOException, MalformedSpanException {
		loadOpCodes(new DataInputStream(new FileInputStream(file)));
	}

	/**
	 * Pushes op codes into the machine. This method is responsible for modifying
	 * the IStream and VStreams based on the op code.
	 * 
	 * @param op
	 *            the operation code to push
	 * @throws MalformedSpanException
	 * @throws IOException
	 */
	void push(Op<?> op) throws MalformedSpanException, IOException;

	/**
	 * Writes OP code to an underlying datasource
	 * 
	 * @param op
	 *            the op code to write
	 * @return the same op code specified in the params
	 */
	Op<?> writeOp(Op<?> op);
}