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
package org.oulipo.browser.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyledText;
import org.fxmisc.richtext.model.TextChange.ChangeType;
import org.fxmisc.richtext.model.TextOps;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.editor.remote.RemoteImage;
import org.oulipo.browser.editor.remote.RemoteImageOps;
import org.oulipo.resources.HyperOperation;
import org.oulipo.resources.HyperOperation.OpCode;
import org.oulipo.resources.HyperRegion;
import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.RemoteFileManager;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.overlays.PresenterOverlay;
import org.oulipo.streams.types.OverlayStream;
import org.reactfx.EventStream;
import org.reactfx.util.Either;

import com.google.common.base.Strings;

import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.input.Clipboard;
import javafx.scene.paint.Color;

/**
 *
 */
public class DocumentArea
		extends GenericStyledArea<ParStyle, Either<StyledText<LinkType>, RemoteImage<LinkType>>, LinkType> {

	/**
	 * Manages deletion of text
	 */
	public static class Deleter {

		/**
		 * Creates a Delete HyperOperation
		 *
		 * @param documentHash
		 * @param change
		 * @return
		 */
		public HyperOperation delete(String documentHash, PlainTextChange change) {
			int width = change.getRemoved().length();
			HyperRegion region = new HyperRegion(change.getRemovalEnd() - width, width);
			HyperOperation op = new HyperOperation(documentHash, region, OpCode.DELETE, change.getRemoved());
			return op;
		}
	}

	/**
	 * Manages insertion of text. Maintains a buffer of text and when the
	 */
	public static class Inserter {

		/**
		 * Appends current text into buffer and resets positions.
		 *
		 * If the caret position has moved, this method creates and returns an INSERT
		 * HyperOperation. Otherwise it returns an empty operation.
		 *
		 * @param documentHash
		 * @param change
		 * @return
		 */
		public Optional<HyperOperation> insert(String documentHash, PlainTextChange change) {
			int width = change.getInserted().length();
			if (width > 0) {
				HyperRegion region = new HyperRegion(change.getInsertionEnd() - width, width);
				HyperOperation op = new HyperOperation(documentHash, region, OpCode.INSERT_TEXT, change.getInserted());
				return Optional.of(op);
			} else {
				return Optional.empty();
			}
		}
	}

	/**
	 * Manages overlaying of text and media
	 */
	public static class Overlayer {

		public HyperOperation on(String tumblerAddress, IndexRange range) {
			// int width = change.getRemoved().length();
			// HyperRegion region = new HyperRegion(change.getRemovalEnd() - width, width);
			// HyperOperation op = new HyperOperation(tumblerAddress, region, OpCode.DELETE,
			// change.getRemoved());
			// return op;
			return null;
		}

		public HyperOperation toggle(String tumblerAddress, IndexRange range) {
			return null;
		}

	}

	private static Node createNode(TextOps<StyledText<LinkType>, LinkType> styledTextOps,
			Either<StyledText<LinkType>, RemoteImage<LinkType>> seg, BiConsumer<? super TextExt, LinkType> applyStyle,
			RemoteFileManager fileManager) {
		if (seg.isLeft()) {
			return StyledTextArea.createStyledTextNode(seg.getLeft(), styledTextOps, applyStyle);
		} else {
			return seg.getRight().createNode(fileManager);
		}
	}

	/**
	 * Creates new instance. The specified tumbler address is the local address of
	 * the current XanaDoc being worked on.
	 *
	 * @param documentHash
	 * @return
	 * @throws IllegalTumblerException
	 */
	public static DocumentArea newInstance(String documentHash, BrowserContext ctx,
			VariantStream<OverlayStream> variantStream) {
		TextOps<StyledText<LinkType>, LinkType> styledTextOps = StyledText.textOps();
		RemoteImageOps<LinkType> linkedImageOps = new RemoteImageOps<>();
		RemoteFileManager fileManager = ctx.getApplicationContext().getRemoteFileManager();

		return new DocumentArea(documentHash, ctx, styledTextOps._or(linkedImageOps),
				seg -> createNode(styledTextOps, seg, (text, style) -> text.setStyle(style.toCss()), fileManager),
				variantStream);
	}

	private final BrowserContext ctx;

	private final Deleter deleter = new Deleter();

	/**
	 * Tumbler address of this Document
	 */
	private final String documentHash;

	private final Inserter inserter = new Inserter();

	private final ArrayList<HyperOperation> operations = new ArrayList<>();

	private VariantStream<OverlayStream> variantStream;

	private boolean writeOps;

	private DocumentArea(String documentHash, BrowserContext ctx,
			TextOps<Either<StyledText<LinkType>, RemoteImage<LinkType>>, LinkType> segmentOps,
			Function<Either<StyledText<LinkType>, RemoteImage<LinkType>>, Node> nodeFactory,
			VariantStream<OverlayStream> variantStream) {
		super(ParStyle.EMPTY, (paragraph, style) -> paragraph.setStyle(style.toCss()),
				LinkType.EMPTY.updateFontSize(12).updateFontFamily("Serif").updateTextColor(Color.BLACK), segmentOps,
				nodeFactory);
		this.variantStream = variantStream;
		this.ctx = ctx;
		this.documentHash = documentHash;
		// homeDocument.setScheme("ted");

		setShowCaret(CaretVisibility.ON);

		setWrapText(true);
		setStyleCodecs(ParStyle.CODEC,
				Codec.eitherCodec(StyledText.codec(LinkType.CODEC), RemoteImage.codec(LinkType.CODEC)));
		requestFocus();
		richChanges().subscribe(change -> {
			System.out.println("Rich text change");
		});

		EventStream<PlainTextChange> textChanges = plainTextChanges();
		textChanges.subscribe(change -> {
			ChangeType type = change.getType();
			if (ChangeType.INSERTION.equals(type)) {
				if (!Strings.isNullOrEmpty(change.getInserted())) {
					notifyTextInsertionChange(change);
				}
			} else if (ChangeType.DELETION.equals(type)) {
				notifyTextInsertionChange(change);
				deleteText(change);
			} else if (ChangeType.REPLACEMENT.equals(type)) {
				System.out.println("Replacement");
				notifyTextInsertionChange(change);
				deleteText(change);
			}
		});
	}

	/**
	 * Applies the specified linkType style to the specified span of text
	 *
	 * @param span
	 * @param linkType
	 */
	/*
	 * public void applyStyle(TumblerField span, int linkType) { int styleStart =
	 * span.spanStart() - 1; StyleSpans<LinkType> styles = getStyleSpans(styleStart,
	 * styleStart + span.spanWidth()); if (PresenterOverlay.BOLD == linkType) {
	 * LinkType mixin = LinkType.bold(true); StyleSpans<LinkType> newStyles =
	 * styles.mapStyles(style -> style.updateWith(mixin)); setStyleSpans(styleStart,
	 * newStyles); } else if (PresenterOverlay.ITALIC == linkType) { LinkType mixin
	 * = LinkType.italic(true); StyleSpans<LinkType> newStyles =
	 * styles.mapStyles(style -> style.updateWith(mixin)); setStyleSpans(styleStart,
	 * newStyles); } else if (PresenterOverlay.UNDERLINE == linkType) { LinkType
	 * mixin = LinkType.underline(true); StyleSpans<LinkType> newStyles =
	 * styles.mapStyles(style -> style.updateWith(mixin)); setStyleSpans(styleStart,
	 * newStyles); } else if (PresenterOverlay.STRIKE_THROUGH == linkType) {
	 * LinkType mixin = LinkType.strikethrough(true); StyleSpans<LinkType> newStyles
	 * = styles.mapStyles(style -> style.updateWith(mixin));
	 * setStyleSpans(styleStart, newStyles); }
	 *
	 * // HyperRegion region = new HyperRegion(change.getRemovalEnd() - width,
	 * width);
	 *
	 * // HyperOperation op = new HyperOperation(tumblerAddress, region, //
	 * OpCode.OVERLAY_ON, change.getRemoved());
	 *
	 * // operations.add(e) try { variantStream.applyOverlays(new
	 * VariantSpan(span.spanStart(), span.spanWidth()), Sets.newHashSet(linkType));
	 * } catch (MalformedSpanException | IOException e) { e.printStackTrace(); } }
	 */

	private void deleteText(PlainTextChange change) {
		operations.add(deleter.delete(documentHash, change));

		long width = change.getRemoved().length();
		long start = change.getRemovalEnd() - width + 1;
		try {
			variantStream.delete(new VariantSpan(start, width));
		} catch (MalformedSpanException | IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<HyperOperation> getOperations() {
		return operations;
	}

	/**
	 * Inserts the specified text at the specified position
	 */
	@Override
	public void insertText(int position, String text) {
		if (Strings.isNullOrEmpty(text)) {
			return;
		}

		if (writeOps) {
			HyperRegion region = new HyperRegion(position, text.length());
			operations.add(new HyperOperation(documentHash, region, OpCode.INSERT_TEXT, text));
		}
		try {
			long start = position + 1;
			variantStream.put(start, new OverlayStream(text.length(), documentHash));
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.insertText(position, text);
	}

	/**
	 * Inserts text and adds insert operation
	 *
	 * @param change
	 */
	private void notifyTextInsertionChange(PlainTextChange change) {
		if (writeOps) {
			Optional<HyperOperation> ho = inserter.insert(documentHash, change);
			if (ho.isPresent()) {
				HyperOperation hop = ho.get();
				operations.add(hop);
			}
		}

		try {
			long width = change.getInserted().length();
			if (width > 0) {
				long start = change.getInsertionEnd() - width + 1;
				variantStream.put(start, new OverlayStream(width, documentHash));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MalformedSpanException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Pastes any text from the clipboard to the document at the current caret
	 * position
	 */
	public void pasteText() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		if (clipboard.hasString()) {
			String text = clipboard.getString();
			if (text != null) {
				operations.add(new HyperOperation(documentHash, selectedRegion(), OpCode.PASTE, text));
				paste();
			}
		}
	}

	private HyperRegion selectedRegion() {
		return new HyperRegion(getSelection().getStart(), getLength());
	}

	public void setImage(String hash) {
		updateStyleInSelection(LinkType.image(hash));
	}

	public void toggleBold() {
		updateStyleInSelection(PresenterOverlay.BOLD_OVERLAY,
				spans -> LinkType.bold(!spans.styleStream().allMatch(style -> style.bold.orElse(false))));
	}

	public void toggleItalic() {
		updateStyleInSelection(PresenterOverlay.ITALIC_OVERLAY,
				spans -> LinkType.italic(!spans.styleStream().allMatch(style -> style.italic.orElse(false))));
	}

	public void toggleStrikethrough() {
		updateStyleInSelection(PresenterOverlay.STRIKE_THROUGH_OVERLAY, spans -> LinkType
				.strikethrough(!spans.styleStream().allMatch(style -> style.strikethrough.orElse(false))));
	}

	public void toggleUnderline() {
		updateStyleInSelection(PresenterOverlay.UNDERLINE_OVERLAY,
				spans -> LinkType.underline(!spans.styleStream().allMatch(style -> style.underline.orElse(false))));
	}

	private void updateStyleInSelection(LinkType mixin) {
		IndexRange selection = getSelection();
		StyleSpans<LinkType> styles = getStyleSpans(selection.getStart(), selection.getStart() + 1);
		StyleSpans<LinkType> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
		setStyleSpans(selection.getStart(), newStyles);
	}

	private void updateStyleInSelection(PresenterOverlay linkType,
			Function<StyleSpans<LinkType>, LinkType> mixinGetter) {
		IndexRange selection = getSelection();
		if (selection.getLength() != 0) {

			try {
				variantStream.toggleOverlay(new VariantSpan(selection.getStart() + 1, selection.getLength()), linkType);
			} catch (MalformedSpanException | IOException e) {
				e.printStackTrace();
			}

			StyleSpans<LinkType> styles = getStyleSpans(selection);
			LinkType mixin = mixinGetter.apply(styles);
			StyleSpans<LinkType> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
			setStyleSpans(selection.getStart(), newStyles);
		}
	}

	public void writeOpsOff() {
		writeOps = false;
	}

	public void writeOpsOn() {
		writeOps = true;
	}
}
