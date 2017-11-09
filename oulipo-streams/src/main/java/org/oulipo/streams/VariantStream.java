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
import java.util.Set;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.types.InvariantSpan;
import org.oulipo.streams.types.Overlay;
import org.oulipo.streams.types.StreamElement;

/**
 * A stream of elements that are linearly spanning a document.
 *
 * @param <T>
 *            the stream element type contained in this stream
 */
public interface VariantStream<T extends StreamElement> {

	default void applyOverlays(VariantSpan variantSpan, Set<TumblerAddress> links)
			throws MalformedSpanException, IOException {
		List<T> elements = getStreamElements(variantSpan);

		if (!elements.isEmpty() && !(elements.get(0) instanceof Overlay)) {
			throw new UnsupportedOperationException("Can only apply overlays to OverlayElements");
		}

		for (T element : elements) {
			Overlay overlay = (Overlay) element;
			overlay.addLinkTypes(links);
		}
		delete(variantSpan);
		putElements(variantSpan.start, elements);
	}

	default void copy(long characterPosition, List<VariantSpan> vspans) throws MalformedSpanException, IOException {
		long start = characterPosition;
		for (VariantSpan vspan : vspans) {
			copy(start, vspan);
			start += vspan.width;
		}
	}

	void copy(long characterPosition, VariantSpan variantSpan) throws MalformedSpanException, IOException;

	/**
	 * Deletes stream elements within the specified variant span
	 * 
	 * @param variantSpan
	 * @throws MalformedSpanException
	 * @throws IOException
	 */
	void delete(VariantSpan variantSpan) throws MalformedSpanException, IOException;

	/**
	 * Gets the home document that contains the variant spans. The contained
	 * invariant span values may have different homeDocuments (transclusion)
	 * 
	 * @return home document
	 */
	TumblerAddress getHomeDocument();

	List<T> getStreamElements() throws MalformedSpanException;

	List<T> getStreamElements(VariantSpan variantSpan) throws MalformedSpanException;

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
	 * Returns the <code>Span<code> at the specified character position, or null if
	 * none exists at that position
	 * 
	 * @param characterPosition
	 * @return
	 */
	T index(long characterPosition);

	default void load(List<T> elements) throws MalformedSpanException, IOException {
		putElements(1, elements);
	}

	/**
	 * Moves all stream elements in the bounds of the specified VariantStream to the
	 * specified position
	 * 
	 * @param to
	 *            position to move stream elements to
	 * @param variantSpan
	 * @throws MalformedSpanException
	 * @throws IOException
	 */
	void move(long to, VariantSpan variantSpan) throws MalformedSpanException, IOException;

	/**
	 * Puts the stream value at the specified character position
	 * 
	 * @param characterPosition
	 *            the position to insert the stream element
	 * @param streamElement
	 *            the element value to insert
	 * @throws MalformedSpanException
	 *             if the stream element has a width less than 1
	 * @throws IndexOutOfBounds
	 *             if characterPosition < 1 or greater (by 2) than the total number
	 *             of characters in stream
	 * @throws IllegalArgumentException
	 *             if the stream element is null OR if the stream is empty and the
	 *             characterPosition is greater than 1
	 * @throws IOException
	 */
	void put(long characterPosition, T streamElement) throws MalformedSpanException, IOException;

	/**
	 * Puts list of stream elements in order, starting at the specified
	 * characterPosition
	 * 
	 * @param characterPosition
	 * @param streamElements
	 * @throws MalformedSpanException
	 * @throws IOException
	 */
	default void putElements(long characterPosition, List<T> streamElements)
			throws MalformedSpanException, IOException {
		long start = characterPosition;
		for (int i = 0; i < streamElements.size(); i++) {
			T element = streamElements.get(i);
			put(start, element);
			start += element.getWidth();
		}
	}

	void rebalance() throws MalformedSpanException;

	void swap(VariantSpan v1, VariantSpan v2) throws MalformedSpanException, IOException;

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
	void toggleOverlay(VariantSpan variantSpan, TumblerAddress link) throws MalformedSpanException, IOException;

}