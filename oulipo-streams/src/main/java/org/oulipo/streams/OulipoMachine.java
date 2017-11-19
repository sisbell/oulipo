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
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.oulipo.streams.overlays.Overlay;
import org.oulipo.streams.types.Invariant;
import org.oulipo.streams.types.InvariantMedia;
import org.oulipo.streams.types.InvariantSpan;
import org.oulipo.streams.types.OverlayStream;

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
public interface OulipoMachine extends InvariantStream {

	void applyOverlays(VariantSpan variantSpan, Set<Overlay> links) throws MalformedSpanException, IOException;

	void copyVariant(long to, VariantSpan variantSpan) throws MalformedSpanException, IOException;

	void deleteVariant(VariantSpan variantSpan) throws MalformedSpanException, IOException;

	void flush();

	/**
	 * Returns the home address of invariant spans within the invariant stream. The
	 * invariant stream may have a home document different from the one specified
	 * here. In this case, this document is pointing to another document.
	 * 
	 * @return
	 */
	String getDocumentHash();

	List<Invariant> getInvariants() throws MalformedSpanException;

	List<Invariant> getInvariants(VariantSpan variantSpan) throws MalformedSpanException;

	/**
	 * Gets all variant spans that intersect the specified span element. If the
	 * VariantStream instance contains no span elements, returns an empty list.
	 * 
	 * @param spanElement
	 * @return
	 * @throws MalformedSpanException
	 */
	List<VariantSpan> getVariantSpans(InvariantSpan spanElement) throws MalformedSpanException;

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
		List<Invariant> invariants = getInvariants();
		int order = 0;
		for (Invariant invariant : invariants) {
			VirtualContent vc = new VirtualContent();
			vc.invariant = invariant;

			vc.order = order++;
			vc.documentHash = getDocumentHash();
			if (invariant instanceof InvariantSpan) {
				// TODO: check is encrypted/paid
				vc.content = getText((InvariantSpan) invariant);
			} else if (invariant instanceof InvariantMedia) {

			}

			virtuals.add(vc);
		}

		return virtuals;
	}

	/**
	 * Returns the <code>Span<code> at the specified character position, or null if
	 * none exists at that position
	 * 
	 * @param characterPosition
	 * @return
	 */
	Invariant index(long characterPosition);

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

	void insertEncrypted(long characterPosition, String text) throws IOException, MalformedSpanException;

	void loadDocument(String hash) throws MalformedSpanException, IOException, SignatureException;

	void moveVariant(long to, VariantSpan variantSpan) throws MalformedSpanException, IOException;

	void putInvariant(long to, Invariant invariant) throws MalformedSpanException, IOException;

	void putOverlay(long to, OverlayStream overlayStream) throws MalformedSpanException, IOException;

	void swapVariants(VariantSpan v1, VariantSpan v2) throws MalformedSpanException, IOException;

	/**
	 * Toggle the overlay. If any of the overlays in the specified variantSpan do
	 * not have the link type, then add the link type to every overlay, otherwise
	 * remove it from every overlay.
	 * 
	 * @param variantSpan
	 * @param link
	 *            the link type of the overlay
	 * @throws MalformedSpanException
	 * @throws IOException
	 */
	void toggleOverlay(VariantSpan variantSpan, Overlay link) throws MalformedSpanException, IOException;

}