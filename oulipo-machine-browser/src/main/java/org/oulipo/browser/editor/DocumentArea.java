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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyledText;
import org.fxmisc.richtext.model.TextOps;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.editor.images.LinkedImage;
import org.oulipo.browser.editor.images.LinkedImageOps;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.ops.HyperOperation;
import org.oulipo.resources.ops.HyperOperation.OpCode;
import org.oulipo.resources.ops.HyperRegion;
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
		extends GenericStyledArea<ParStyle, Either<StyledText<LinkType2>, LinkedImage<LinkType2>>, LinkType2> {

	/**
	 * Manages deletion of text
	 */
	public static class Deleter {

		/**
		 * Creates a Delete HyperOperation
		 * 
		 * @param tumblerAddress
		 * @param change
		 * @return
		 */
		public HyperOperation delete(TumblerAddress tumblerAddress, PlainTextChange change) {
			HyperRegion region = new HyperRegion(change.getPosition(), change.getRemoved().length());
			HyperOperation op = new HyperOperation(tumblerAddress, region, OpCode.DELETE, change.getRemoved());
			return op;
		}
	}

	/**
	 * Manages insertion of text. Maintains a buffer of text and when the
	 */
	public static class Inserter {

		/**
		 * Buffer for text to insert
		 */
		private StringBuilder insertionText = new StringBuilder();

		private int previousPosition = 0;

		public Optional<HyperOperation> createOperation(TumblerAddress tumblerAddress, int insertionEnd) {
			if (hasText()) {
				System.out.println("insertionEnd: " + insertionEnd);
				HyperOperation op = new HyperOperation(tumblerAddress,
						new HyperRegion(insertionEnd - insertionText.length(), insertionText.length()), OpCode.INSERT,
						insertionText.toString());
				return Optional.of(op);
			}
			return Optional.empty();
		}

		public void flush() {
			insertionText = new StringBuilder();
		}

		/**
		 * If current position is different than previous position, then the cursor has
		 * moved
		 * 
		 * @param currentPosition
		 *            the current position of cursor
		 * @return true is cursor has moved
		 */
		protected boolean hasMoved(int currentPosition) {
			return currentPosition - previousPosition != 0;
		}

		/**
		 * The buffer contains text
		 * 
		 * @return true if buffer contains text
		 */
		protected boolean hasText() {
			return insertionText.length() > 0;
		}

		/**
		 * Appends current text into buffer and resets positions.
		 * 
		 * If the caret position has moved, this method creates and returns an INSERT
		 * HyperOperation. Otherwise it returns an empty operation.
		 * 
		 * @param tumblerAddress
		 * @param change
		 * @return
		 */
		public Optional<HyperOperation> insert(TumblerAddress tumblerAddress, PlainTextChange change) {
			if (hasMoved(change.getPosition()) && hasText()) {
				System.out.println(change.getInsertionEnd() + ":" + change.getInserted().length());
				Optional<HyperOperation> ohop = createOperation(tumblerAddress, change.getInsertionEnd());
				reset(change);
				// System.out.println(opString());
				return ohop;
			}
			previousPosition = change.getInsertionEnd();
			insertionText.append(change.getInserted());
			return Optional.empty();
		}

		/**
		 * Resets positions and inserts current text into buffer
		 * 
		 * @param change
		 *            the most recent text change
		 */
		public void reset(PlainTextChange change) {
			previousPosition = change.getInsertionEnd();
			// insertionStart = previousPosition;

			insertionText = new StringBuilder();
			insertionText.append(change.getInserted());
		}
	}

	private static Node createNode(TextOps<StyledText<LinkType2>, LinkType2> styledTextOps,
			Either<StyledText<LinkType2>, LinkedImage<LinkType2>> seg,
			BiConsumer<? super TextExt, LinkType2> applyStyle) {
		if (seg.isLeft()) {
			return StyledTextArea.createStyledTextNode(seg.getLeft(), styledTextOps, applyStyle);
		} else {
			return seg.getRight().createNode();
		}
	}

	/**
	 * Creates new instance. The specified tumbler address is the local address of
	 * the current XanaDoc being worked on.
	 * 
	 * @param homeDocument
	 * @return
	 * @throws IllegalTumblerException
	 */
	public static DocumentArea newInstance(TumblerAddress tumblerAddress, BrowserContext ctx) {
		TextOps<StyledText<LinkType2>, LinkType2> styledTextOps = StyledText.textOps();
		LinkedImageOps<LinkType2> linkedImageOps = new LinkedImageOps<>();

		return new DocumentArea(tumblerAddress, ctx, styledTextOps._or(linkedImageOps),
				seg -> createNode(styledTextOps, seg, (text, style) -> text.setStyle(style.toCss())));
	}

	private final BrowserContext ctx;

	private final Deleter deleter = new Deleter();

	private int deletionPosition = 0;

	private StringBuilder deletionText = new StringBuilder();

	/**
	 * Tumbler address of this Document
	 */
	private final TumblerAddress homeDocument;

	private final Inserter inserter = new Inserter();

	private final ArrayList<HyperOperation> operations = new ArrayList<>();

	private boolean writeOps;

	private DocumentArea(TumblerAddress homeDocument, BrowserContext ctx,
			TextOps<Either<StyledText<LinkType2>, LinkedImage<LinkType2>>, LinkType2> segmentOps,
			Function<Either<StyledText<LinkType2>, LinkedImage<LinkType2>>, Node> nodeFactory) {
		super(ParStyle.EMPTY, (paragraph, style) -> paragraph.setStyle(style.toCss()),
				LinkType2.EMPTY.updateFontSize(12).updateFontFamily("Serif").updateTextColor(Color.BLACK), segmentOps,
				nodeFactory);
		this.ctx = ctx;
		this.homeDocument = homeDocument;
		homeDocument.setScheme("ted");

		setShowCaret(CaretVisibility.ON);

		setWrapText(true);
		setStyleCodecs(ParStyle.CODEC,
				Codec.eitherCodec(StyledText.codec(LinkType2.CODEC), LinkedImage.codec(LinkType2.CODEC)));
		requestFocus();

		EventStream<PlainTextChange> textChanges = plainTextChanges();
		textChanges.subscribe(change -> {
			try {
				if (!Strings.isNullOrEmpty(change.getInserted())) {
					notifyTextInsertionChange(change);
				} else {// deleted
					notifyTextInsertionChange(change);
					deleteText(change);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ctx.showMessage("Text Change Exception: " + e.getMessage());
			}
		});
	}

	/**
	 * Applies the specified linkType style to the specified span of text
	 * 
	 * @param span
	 * @param linkType
	 */
	public void applyStyle(TumblerAddress span, TumblerAddress linkType) {
		StyleSpans<LinkType2> styles = getStyleSpans(span.spanStart() - 1, span.spanStart() - 1 + span.spanWidth());
		LinkType2 mixin = LinkType2.bold(true);// TODO -need to know style type
		StyleSpans<LinkType2> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
		setStyleSpans(span.spanStart() - 1, newStyles);
	}

	private void delete2(PlainTextChange change) {
		if ((change.getPosition() - deletionPosition) != 0 && deletionText.length() > 0) {
			String text = deletionText.reverse().toString();
			HyperOperation op = new HyperOperation(homeDocument, new HyperRegion(change.getPosition(), text.length()),
					OpCode.DELETE, text);
			operations.add(op);
			deletionText = new StringBuilder();

			System.out.println(op);
		}
		deletionPosition = change.getPosition();
		deletionText.append(change.getRemoved());
	}

	private void deleteText(PlainTextChange change) {
		operations.add(deleter.delete(homeDocument, change));
	}

	/*
	 * public Optional<HyperOperation> flush(TumblerAddress tumblerAddress) {
	 * Optional<HyperOperation> ohop = inserter.createOperation(tumblerAddress);
	 * inserter.flush(); return ohop; }
	 */

	public void flush() {
		inserter.flush();
	}

	public ArrayList<HyperOperation> getOperations() {
		return operations;
	}

	/**
	 * Inserts the specified text at the specified position
	 */
	@Override
	public void insertText(int position, String text) {
		if (writeOps) {
			HyperRegion region = new HyperRegion(position, text.length());
			operations.add(new HyperOperation(homeDocument, region, OpCode.INSERT, text));
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
			Optional<HyperOperation> ho = inserter.insert(homeDocument, change);
			if (ho.isPresent()) {
				operations.add(ho.get());
			}
		}
	}

	public String opString() {
		StringBuilder sb = new StringBuilder();
		for (HyperOperation op : operations) {
			if (OpCode.INSERT.equals(op.getOperation())) {
				if (op.getDocumentRegion().getStart() > sb.length()) {
					sb.append(op.getText());
				} else {
					sb.insert(op.getDocumentRegion().getStart(), op.getText());
				}
			} else if (OpCode.DELETE.equals(op.getOperation())) {
				try {
					sb.delete(op.getDocumentRegion().getStart(), op.getDocumentRegion().end());
				} catch (Exception e) {
					System.out.println("Delete out of bounds: Start = " + op.getDocumentRegion().getStart() + ", End = "
							+ op.getDocumentRegion().end() + ", len = " + sb.length());
				}
			}
		}
		return sb.toString();
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
				operations.add(new HyperOperation(homeDocument, selectedRegion(), OpCode.PASTE, text));
				paste();
			}
		}
	}

	public void printSpans() {
		StyleSpans<LinkType2> spans = getStyleSpans(0, 50);
		Iterator<StyleSpan<LinkType2>> it = spans.iterator();
		while (it.hasNext()) {
			StyleSpan<LinkType2> span = it.next();
			System.out.println("LEN: " + span.getLength() + ", " + span.getStyle().toCss());
			// span.getStyle().
			// Rewrite style to give start, and width, otherwise iterate and add position
			// these will return tumbler type and variant spans - translate into endset for
			// home doc
		}
	}

	private HyperRegion selectedRegion() {
		return new HyperRegion(getSelection().getStart(), getLength());
	}

	public void toggleBold() {
		updateStyleInSelection(
				spans -> LinkType2.bold(!spans.styleStream().allMatch(style -> style.bold.orElse(false))));
	}

	public void toggleItalic() {
		updateStyleInSelection(
				spans -> LinkType2.italic(!spans.styleStream().allMatch(style -> style.italic.orElse(false))));
	}

	public void toggleStrikethrough() {
		updateStyleInSelection(spans -> LinkType2
				.strikethrough(!spans.styleStream().allMatch(style -> style.strikethrough.orElse(false))));
	}

	public void toggleUnderline() {
		updateStyleInSelection(
				spans -> LinkType2.underline(!spans.styleStream().allMatch(style -> style.underline.orElse(false))));
	}

	private void updateStyleInSelection(Function<StyleSpans<LinkType2>, LinkType2> mixinGetter) {
		IndexRange selection = getSelection();
		if (selection.getLength() != 0) {
			StyleSpans<LinkType2> styles = getStyleSpans(selection);
			LinkType2 mixin = mixinGetter.apply(styles);
			// TODO: Transclusions with different home address
			// TumblerAddress span =
			// TumblerAddress.create(this.homeDocument.toExternalForm() + ".0.1."
			// + (selection.getStart() + 1) + "~1." + selection.getLength());
			StyleSpans<LinkType2> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
			setStyleSpans(selection.getStart(), newStyles);
		}
	}

	public void writeOpsOff() {
		writeOps = false;
	}

	/*
	 * public void appendText(String text, boolean writeOpCodes) { if (writeOpCodes)
	 * { this.insertText(getLength(), text); } else { super.insertText(getLength(),
	 * text); } }
	 */

	public void writeOpsOn() {
		writeOps = true;
	}
}
