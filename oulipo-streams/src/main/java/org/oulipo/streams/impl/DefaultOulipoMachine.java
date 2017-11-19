/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
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
package org.oulipo.streams.impl;

import java.io.IOException;
import java.security.Key;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.oulipo.streams.InvariantStream;
import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.RemoteFileManager;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.document.DocumentFile;
import org.oulipo.streams.document.DocumentFile.Builder;
import org.oulipo.streams.opcodes.ApplyOverlayOp;
import org.oulipo.streams.opcodes.CopyVariantOp;
import org.oulipo.streams.opcodes.DeleteVariantOp;
import org.oulipo.streams.opcodes.MoveVariantOp;
import org.oulipo.streams.opcodes.Op;
import org.oulipo.streams.opcodes.PutInvariantMediaOp;
import org.oulipo.streams.opcodes.PutInvariantSpanOp;
import org.oulipo.streams.opcodes.PutOverlayMediaOp;
import org.oulipo.streams.opcodes.PutOverlayOp;
import org.oulipo.streams.opcodes.SwapVariantOp;
import org.oulipo.streams.opcodes.ToggleOverlayOp;
import org.oulipo.streams.overlays.Overlay;
import org.oulipo.streams.types.Invariant;
import org.oulipo.streams.types.InvariantMedia;
import org.oulipo.streams.types.InvariantSpan;
import org.oulipo.streams.types.OverlayMedia;
import org.oulipo.streams.types.OverlayStream;

import com.google.common.base.Strings;

/**
 * An OulipoMachine that is backed by a <code>StreamLoader</code>. The
 * StreamLoader can be remote or local.
 *
 */
public final class DefaultOulipoMachine implements OulipoMachine {

	private static void assertGreaterThanZero(long to) throws IllegalArgumentException {
		if (to < 1) {
			throw new IllegalArgumentException("to must be greater than 0");
		}
	}

	private static void assertSpanNotNull(Object span) {
		if (span == null) {
			throw new IllegalArgumentException("span is null");
		}
	}

	public static DefaultOulipoMachine createWritableMachine(StreamLoader loader, RemoteFileManager remoteFileManager,
			String documentHash) throws IOException, MalformedSpanException {
		return new DefaultOulipoMachine(loader, remoteFileManager, documentHash, null);
	}

	protected final Builder documentBuilder;

	private final String documentHash;

	private final InvariantStream iStream;

	private VariantStream<OverlayStream> oStream;

	private final RemoteFileManager remoteFileManager;

	protected final StreamLoader stream;

	private final VariantStream<Invariant> vStream;

	private boolean writeDocFile = false;

	public DefaultOulipoMachine(StreamLoader stream, RemoteFileManager remoteFileManager, String documentHash,
			Key privateKey) throws IOException, MalformedSpanException {
		if (stream == null) {
			throw new IllegalArgumentException("streamLoader is null");
		}

		if (remoteFileManager == null) {
			throw new IllegalArgumentException("remoteFileManager is null");
		}

		if (Strings.isNullOrEmpty(documentHash)) {
			throw new IllegalArgumentException("documentHash is null");
		}

		this.stream = stream;
		this.remoteFileManager = remoteFileManager;

		this.documentBuilder = new DocumentFile.Builder(documentHash);
		// TODO: stream needs encrypted stream
		this.iStream = stream.openInvariantStream(documentHash, privateKey);
		this.vStream = stream.openInvariantVariantStream(documentHash);
		this.oStream = stream.openOverlayVariantStream(documentHash);
		this.documentHash = documentHash;
	}

	@Override
	public InvariantSpan append(String text) throws IOException, MalformedSpanException {
		return iStream.append(text);// TODO: encrypted????
	}

	@Override
	public void applyOverlays(VariantSpan variantSpan, Set<Overlay> links) throws MalformedSpanException, IOException {// links
																														// are
																														// objects:
																														// string
																														// or
																														// IRI?,
																														// predeicate
																														// will
																														// be
																														// linkType
		assertSpanNotNull(variantSpan);

		oStream.applyOverlays(variantSpan, links);

		if (writeDocFile) {
			documentBuilder.applyOverlay(variantSpan, links);
		}
	}

	@Override
	public void copyVariant(long to, VariantSpan variantSpan) throws MalformedSpanException, IOException {
		assertGreaterThanZero(to);
		assertSpanNotNull(variantSpan);
		vStream.copy(to, variantSpan);
		oStream.copy(to, variantSpan);

		if (writeDocFile) {
			documentBuilder.copyVariant(to, variantSpan);
		}
	}

	@Override
	public void deleteVariant(VariantSpan variantSpan) throws MalformedSpanException, IOException {
		assertSpanNotNull(variantSpan);
		vStream.delete(variantSpan);
		oStream.delete(variantSpan);

		if (writeDocFile) {
			documentBuilder.deleteVariant(variantSpan);
		}
	}

	private void executeOps(DocumentFile document) throws MalformedSpanException, IOException {
		for (Op op : document.getOps()) {
			switch (op.getCode()) {
			case Op.COPY:
				CopyVariantOp cvo = (CopyVariantOp) op;
				copyVariant(cvo.to, cvo.variantSpan);
				break;
			case Op.DELETE:
				DeleteVariantOp dvo = (DeleteVariantOp) op;
				deleteVariant(dvo.variantSpan);
				break;
			case Op.MOVE:
				MoveVariantOp mvo = (MoveVariantOp) op;
				moveVariant(mvo.to, mvo.variantSpan);
				break;
			case Op.PUT_OVERLAY_MEDIA:
				PutOverlayMediaOp pmo = (PutOverlayMediaOp) op;
				putOverlay(pmo.to, new OverlayMedia(document.getString(pmo.hash), document.getOverlays(pmo.linkTypes)));
				break;
			case Op.PUT_INVARIANT_MEDIA:
				PutInvariantMediaOp pmso = (PutInvariantMediaOp) op;
				putInvariant(pmso.to, new InvariantMedia(document.getString(pmso.ripIndex)));
				break;
			case Op.PUT_OVERLAY:
				PutOverlayOp poo = (PutOverlayOp) op;
				putOverlay(poo.variantSpan.start,
						new OverlayStream(poo.variantSpan.width, document.getOverlays(poo.linkTypes)));
				break;
			case Op.PUT_INVARIANT_SPAN:
				PutInvariantSpanOp pso = (PutInvariantSpanOp) op;
				putInvariant(pso.to,
						new InvariantSpan(pso.invariantStart, pso.width, document.getString(pso.ripIndex)));
				break;
			case Op.SWAP:
				SwapVariantOp svo = (SwapVariantOp) op;
				swapVariants(svo.v1, svo.v2);
				break;
			case Op.APPLY_OVERLAY:
				ApplyOverlayOp aoo = (ApplyOverlayOp) op;
				applyOverlays(aoo.variantSpan, document.getOverlays(aoo.linkTypes));
				break;
			case Op.TOGGLE_OVERLAY:
				ToggleOverlayOp too = (ToggleOverlayOp) op;
				toggleOverlay(too.variantSpan, document.getOverlay(too.linkTypeIndex));
				break;
			}
		}
	}

	@Override
	public void flush() {
		stream.flushVariantCache();
	}

	@Override
	public String getDocumentHash() {
		return documentHash;
	}

	@Override
	public List<Invariant> getInvariants() throws MalformedSpanException {
		return vStream.getStreamElements();
	}

	@Override
	public List<Invariant> getInvariants(VariantSpan variantSpan) throws MalformedSpanException {
		assertSpanNotNull(variantSpan);
		return vStream.getStreamElements(variantSpan);
	}

	@Override
	public String getText(InvariantSpan invariantSpan) throws IOException {
		assertSpanNotNull(invariantSpan);
		return iStream.getText(invariantSpan);// TODO: encrypted??
	}

	@Override
	public List<VariantSpan> getVariantSpans(InvariantSpan invariantSpan) throws MalformedSpanException {
		assertSpanNotNull(invariantSpan);
		return vStream.getVariantSpans(invariantSpan);
	}

	@Override
	public Invariant index(long characterPosition) {
		assertGreaterThanZero(characterPosition);
		return vStream.index(characterPosition);
	}

	@Override
	public void insert(long to, String text) throws IOException, MalformedSpanException {
		assertGreaterThanZero(to);
		if (Strings.isNullOrEmpty(text)) {
			throw new IllegalArgumentException("Text can't be empty");
		}
		InvariantSpan ispan = iStream.append(text);// TODO: encrypted
		vStream.put(to, ispan);
		putOverlay(to, new OverlayStream(ispan.getWidth()));
	}

	@Override
	public void insertEncrypted(long to, String text) throws IOException, MalformedSpanException {
		assertGreaterThanZero(to);
		if (Strings.isNullOrEmpty(text)) {
			throw new IllegalArgumentException("Text can't be empty");
		}
		InvariantSpan ispan = iStream.append(text);// TODO: encrypted
		vStream.put(to, ispan);
		putOverlay(to, new OverlayStream(ispan.getWidth()));
	}

	@Override
	public void loadDocument(String hash) throws MalformedSpanException, IOException, SignatureException {
		if (Strings.isNullOrEmpty(hash)) {
			throw new IllegalArgumentException("hash is null");
		}
		List<DocumentFile> documents;
		try {
			documents = processFiles(hash);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		Collections.reverse(documents);
		for (DocumentFile document : documents) {
			executeOps(document);
		}
	}

	@Override
	public void moveVariant(long to, VariantSpan variantSpan) throws MalformedSpanException, IOException {
		assertGreaterThanZero(to);
		assertSpanNotNull(variantSpan);
		vStream.move(to, variantSpan);
		oStream.move(to, variantSpan);

		if (writeDocFile) {
			documentBuilder.moveVariant(to, variantSpan);
		}
	}

	private List<DocumentFile> processFiles(String hash)
			throws IOException, SignatureException, MalformedSpanException, Exception {
		List<DocumentFile> documents = new ArrayList<>();
		byte[] doc = remoteFileManager.get(hash);// TODO: check local cache
		DocumentFile file = DocumentFile.decompiler().decompile(hash, doc);
		if (!Strings.isNullOrEmpty(file.getHashPreviousBlock())) {
			documents.addAll(processFiles(file.getHashPreviousBlock()));
		}
		return documents;
	}

	@Override
	public void putInvariant(long to, Invariant invariant) throws MalformedSpanException, IOException {
		assertGreaterThanZero(to);
		assertSpanNotNull(invariant);

		if (invariant instanceof InvariantSpan) {
			InvariantSpan is = (InvariantSpan) invariant;
			insert(is.getStart(), "");// GET TEXT from text area
			if (writeDocFile) {
				documentBuilder.putInvariantSpan(to, is.getStart(), is.getWidth(), documentHash);
			}
		} else if (invariant instanceof InvariantMedia) {
			InvariantMedia im = (InvariantMedia) invariant;
			vStream.put(to, im);
			if (writeDocFile) {
				documentBuilder.putInvariantMediaOp(to, im.hash);
			}
		}
	}

	@Override
	public void putOverlay(long to, OverlayStream overlayStream) throws MalformedSpanException, IOException {
		assertGreaterThanZero(to);
		oStream.put(to, overlayStream);

		if (writeDocFile) {
			if (overlayStream instanceof OverlayStream) {
				documentBuilder.putOverlayOp(new VariantSpan(to, overlayStream.getWidth()), overlayStream.linkTypes);
			} else if (overlayStream instanceof OverlayMedia) {
				OverlayMedia om = (OverlayMedia) overlayStream;
				documentBuilder.putOverlayMediaOp(to, om.hash, om.linkTypes);
			}
		}
	}

	@Override
	public void swapVariants(VariantSpan v1, VariantSpan v2) throws MalformedSpanException, IOException {
		assertSpanNotNull(v1);
		assertSpanNotNull(v2);
		vStream.swap(v1, v2);
		oStream.swap(v1, v2);

		if (writeDocFile) {
			documentBuilder.swapVariant(v1, v2);
		}
	}

	@Override
	public void toggleOverlay(VariantSpan variantSpan, Overlay link) throws MalformedSpanException, IOException {
		assertSpanNotNull(variantSpan);
		oStream.toggleOverlay(variantSpan, link);

		if (writeDocFile) {
			documentBuilder.toggleOverlay(variantSpan, link);
		}
	}

}
