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
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.DocumentFile;
import org.oulipo.streams.DocumentFile.Builder;
import org.oulipo.streams.InvariantStream;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.RemoteFileManager;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;
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
import org.oulipo.streams.types.Invariant;
import org.oulipo.streams.types.InvariantMedia;
import org.oulipo.streams.types.InvariantSpan;
import org.oulipo.streams.types.Overlay;
import org.oulipo.streams.types.OverlayMedia;

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
			TumblerAddress tumbler) throws IOException, MalformedSpanException {
		return new DefaultOulipoMachine(loader, remoteFileManager, tumbler);
	}

	protected final Builder documentBuilder;

	private final TumblerAddress homeDocument;

	private final InvariantStream iStream;

	private VariantStream<Overlay> oStream;

	private final RemoteFileManager remoteFileManager;

	protected final StreamLoader stream;

	private final VariantStream<Invariant> vStream;

	private boolean writeDocFile = false;

	public DefaultOulipoMachine(StreamLoader stream, RemoteFileManager remoteFileManager, TumblerAddress homeDocument)
			throws IOException, MalformedSpanException {
		if (stream == null) {
			throw new IllegalArgumentException("streamLoader is null");
		}
		
		if(remoteFileManager == null) {
			throw new IllegalArgumentException("remoteFileManager is null");
		}
		
		if(homeDocument == null) {
			throw new IllegalArgumentException("homeDocument is null");
		}
	
		
		this.stream = stream;
		this.remoteFileManager = remoteFileManager;

		this.documentBuilder = new DocumentFile.Builder(homeDocument);

		this.iStream = stream.openInvariantStream(homeDocument);
		this.vStream = stream.openInvariantVariantStream(homeDocument);
		this.oStream = stream.openOverlayVariantStream(homeDocument);
		this.homeDocument = homeDocument;
	}

	@Override
	public InvariantSpan append(String text) throws IOException, MalformedSpanException {
		return iStream.append(text);
	}

	@Override
	public void applyOverlays(VariantSpan variantSpan, Set<TumblerAddress> links)
			throws MalformedSpanException, IOException {
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

	private void executeOps(DocumentFile document)
			throws MalformedTumblerException, MalformedSpanException, IOException {
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
				putOverlay(pmo.to, new OverlayMedia(document.getTumblerAddress(pmo.mediaAddress),
						document.getMediaHash(pmo.hash), document.getTumblerAddresses(pmo.linkTypes)));
				break;
			case Op.PUT_INVARIANT_MEDIA:
				PutInvariantMediaOp pmso = (PutInvariantMediaOp) op;
				putInvariant(pmso.to, new InvariantMedia(document.getMediaHash(pmso.mediaPoolIndex),
						document.getTumblerAddress(pmso.mediaTumblerIndex)));
				break;
			case Op.PUT_OVERLAY:
				PutOverlayOp poo = (PutOverlayOp) op;
				putOverlay(poo.variantSpan.start,
						new Overlay(poo.variantSpan.width, document.getTumblerAddresses(poo.linkTypes)));
				break;
			case Op.PUT_INVARIANT_SPAN:
				PutInvariantSpanOp pso = (PutInvariantSpanOp) op;
				putInvariant(pso.to,
						new InvariantSpan(pso.invariantStart, pso.width, document.getTumblerAddress(pso.homeDocumentIndex)));
				break;
			case Op.SWAP:
				SwapVariantOp svo = (SwapVariantOp) op;
				swapVariants(svo.v1, svo.v2);
				break;
			case Op.APPLY_OVERLAY:
				ApplyOverlayOp aoo = (ApplyOverlayOp) op;
				applyOverlays(aoo.variantSpan, document.getTumblerAddresses(aoo.linkTypeIndicies));
				break;
			case Op.TOGGLE_OVERLAY:
				ToggleOverlayOp too = (ToggleOverlayOp) op;
				toggleOverlay(too.variantSpan, document.getTumblerAddress(too.linkTypeIndex));
				break;
			}
		}
	}

	@Override
	public void flush() {
		stream.flushVariantCache();
	}

	@Override
	public TumblerAddress getHomeDocument() {
		return homeDocument;
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
		return iStream.getText(invariantSpan);
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
		InvariantSpan ispan = iStream.append(text);
		vStream.put(to, ispan);
		putOverlay(to, new Overlay(ispan.getWidth()));
	}

	@Override
	public void loadDocument(String hash)
			throws MalformedTumblerException, MalformedSpanException, IOException, SignatureException {
		if(Strings.isNullOrEmpty(hash)) {
			throw new IllegalArgumentException("hash is null");
		}		
		List<DocumentFile> documents = processFiles(hash);
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
			throws IOException, SignatureException, MalformedSpanException {
		List<DocumentFile> documents = new ArrayList<>();
		byte[] doc = remoteFileManager.get(hash);// TODO: check local cache
		DocumentFile file = DocumentFile.read(doc);
		if (!Strings.isNullOrEmpty(file.getHashPreviousBlock())) {
			documents.addAll(processFiles(file.getHashPreviousBlock()));
		}
		return documents;
	}

	@Override
	public void putInvariant(long to, Invariant invariant) throws MalformedSpanException, IOException {
		assertGreaterThanZero(to);
		assertSpanNotNull(invariant);
		
		if(invariant instanceof InvariantSpan) {
			InvariantSpan is = (InvariantSpan) invariant;
			insert(is.getStart(), "");//GET TEXT from text area
			if (writeDocFile) {
				documentBuilder.putInvariantSpan(to, is.getStart() , is.getWidth(), homeDocument);
			}
		} else if(invariant instanceof InvariantMedia) {
			InvariantMedia im = (InvariantMedia) invariant;
			vStream.put(to, im);
			if (writeDocFile) {
				documentBuilder.putInvariantMediaOp(to, im.hash, im.mediaAddress);
			}
		}
	}

	@Override
	public void putOverlay(long to, Overlay overlay) throws MalformedSpanException, IOException {
		assertGreaterThanZero(to);
		oStream.put(to, overlay);

		if (writeDocFile) {
			if(overlay instanceof Overlay) {
				documentBuilder.putOverlayOp(new VariantSpan(to, overlay.getWidth()), overlay.linkTypes);
			} else if(overlay instanceof OverlayMedia) {
				OverlayMedia om = (OverlayMedia) overlay;
				documentBuilder.putOverlayMediaOp(to, om.hash, om.mediaAddress, om.linkTypes);
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
	public void toggleOverlay(VariantSpan variantSpan, TumblerAddress link) throws MalformedSpanException, IOException {
		assertSpanNotNull(variantSpan);
		oStream.toggleOverlay(variantSpan, link);

		if (writeDocFile) {
			documentBuilder.toggleOverlay(variantSpan, link);
		}
	}

}
