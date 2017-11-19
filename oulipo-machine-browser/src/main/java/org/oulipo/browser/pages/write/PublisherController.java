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
package org.oulipo.browser.pages.write;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyledText;
import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.editor.DocumentArea;
import org.oulipo.browser.editor.LinkType;
import org.oulipo.browser.editor.ParStyle;
import org.oulipo.browser.editor.remote.IpfsRemoteImage;
import org.oulipo.browser.editor.remote.RemoteImage;
import org.oulipo.browser.pages.BaseController;
import org.oulipo.resources.HyperOperation;
import org.oulipo.services.responses.Endset;
import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.RemoteFileManager;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.impl.RopeVariantStream;
import org.oulipo.streams.types.OverlayStream;
import org.reactfx.SuspendableNo;
import org.reactfx.util.Either;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public final class PublisherController extends BaseController {

	private DocumentArea area;

	private Map<String, Endset> endsets = new HashMap<>();

	private VirtualizedScrollPane<GenericStyledArea<ParStyle, Either<StyledText<LinkType>, RemoteImage<LinkType>>, LinkType>> renderPane;

	private final SuspendableNo updatingToolbar = new SuspendableNo();

	private VariantStream<OverlayStream> variantStream;

	private Button createMaterialButton(String resource, Runnable action, String toolTip) {
		Image image = new Image(getClass().getResourceAsStream("/images/ic_" + resource + "_black_24dp_1x.png"));
		JFXButton button = new JFXButton();
		button.setOnAction(evt -> {
			action.run();
			area.requestFocus();
		});
		if (!Strings.isNullOrEmpty(toolTip)) {
			button.setTooltip(new Tooltip(toolTip));
		}
		button.setPrefWidth(20);
		button.setPrefHeight(20);
		button.setGraphic(new ImageView(image));
		button.setButtonType(ButtonType.RAISED);

		return button;
	}

	private Node formatButtons() {
		HBox formatButtons = new HBox();
		formatButtons.setSpacing(3);
		formatButtons.setPadding(new Insets(0, 10, 10, 10));

		Button boldBtn = createMaterialButton("format_bold", area::toggleBold, null);
		Button italicBtn = createMaterialButton("format_italic", area::toggleItalic, null);
		Button strikeBtn = createMaterialButton("format_strikethrough", area::toggleStrikethrough, null);
		Button underlineBtn = createMaterialButton("format_underlined", area::toggleUnderline, null);
		formatButtons.getChildren().addAll(boldBtn, italicBtn, strikeBtn, underlineBtn);

		area.beingUpdatedProperty().addListener((o, old, beingUpdated) -> {
			if (!beingUpdated) {
				boolean bold, italic, underline, strike;

				IndexRange selection = area.getSelection();
				if (selection.getLength() != 0) {
					StyleSpans<LinkType> styles = area.getStyleSpans(selection);

					bold = styles.styleStream().anyMatch(s -> s.bold.orElse(false));
					italic = styles.styleStream().anyMatch(s -> s.italic.orElse(false));
					underline = styles.styleStream().anyMatch(s -> s.underline.orElse(false));
					strike = styles.styleStream().anyMatch(s -> s.strikethrough.orElse(false));

				} else {
					int p = area.getCurrentParagraph();
					int col = area.getCaretColumn();
					LinkType style = area.getStyleAtPosition(p, col);
					bold = style.bold.orElse(false);
					italic = style.italic.orElse(false);
					underline = style.underline.orElse(false);
					strike = style.strikethrough.orElse(false);
				}

				updatingToolbar.suspendWhile(() -> {
					if (bold) {
						if (!boldBtn.getStyleClass().contains("pressed")) {
							boldBtn.getStyleClass().add("pressed");
						}
					} else {
						boldBtn.getStyleClass().remove("pressed");
					}

					if (italic) {
						if (!italicBtn.getStyleClass().contains("pressed")) {
							italicBtn.getStyleClass().add("pressed");
						}
					} else {
						italicBtn.getStyleClass().remove("pressed");
					}

					if (underline) {
						if (!underlineBtn.getStyleClass().contains("pressed")) {
							underlineBtn.getStyleClass().add("pressed");
						}
					} else {
						underlineBtn.getStyleClass().remove("pressed");
					}

					if (strike) {
						if (!strikeBtn.getStyleClass().contains("pressed")) {
							strikeBtn.getStyleClass().add("pressed");
						}
					} else {
						strikeBtn.getStyleClass().remove("pressed");
					}

				});
			}
		});
		return formatButtons;

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	private Node insertButtons() {
		HBox insertButtons = new HBox();
		insertButtons.setSpacing(3);
		insertButtons.setPadding(new Insets(0, 10, 10, 10));

		Button commentBtn = createMaterialButton("insert_comment", null, "Comment");
		Button linkBtn = createMaterialButton("insert_link", null, "Jump Link");
		Button photoBtn = createMaterialButton("insert_photo", this::insertImage, "Insert Image");
		Button transBtn = createMaterialButton("filter_none", null, "Transclusion");

		insertButtons.getChildren().addAll(commentBtn, linkBtn, transBtn, photoBtn);
		return insertButtons;
	}

	/**
	 * Action listener which inserts a new image at the current caret position.
	 */
	private void insertImage() {
		printSpans();
		String initialDir = System.getProperty("user.dir");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Insert image");
		fileChooser.setInitialDirectory(new File(initialDir));

		File selectedFile = fileChooser.showOpenDialog(area.getScene().getWindow());
		if (selectedFile != null) {
			RemoteFileManager manager = ctx.getApplicationContext().getRemoteFileManager();
			try {
				String hash = manager.add(selectedFile);
				ReadOnlyStyledDocument<ParStyle, Either<StyledText<LinkType>, RemoteImage<LinkType>>, LinkType> ros = ReadOnlyStyledDocument
						.fromSegment(Either.right(new IpfsRemoteImage<>(hash, LinkType.EMPTY)), ParStyle.EMPTY,
								LinkType.EMPTY, area.getSegOps());
				area.replaceSelection(ros);
				area.setImage(hash);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Node optionButtons() {
		HBox opButtons = new HBox();
		opButtons.setSpacing(3);
		opButtons.setPadding(new Insets(0, 10, 10, 10));

		Button saveBtn = createMaterialButton("save", this::save, null);
		// Button openBtn = createMaterialButton("open_in_browser", this::open,
		// "Open Document");
		opButtons.getChildren().addAll(saveBtn);
		return opButtons;
	}

	public void printSpans() {
		StyleSpans<LinkType> spans = area.getStyleSpans(0, area.getLength());
		Iterator<StyleSpan<LinkType>> it = spans.iterator();
		while (it.hasNext()) {
			StyleSpan<LinkType> span = it.next();

			System.out.println("LEN: " + span.getLength() + ", " + span.getStyle().toCss());
		}
	}

	/**
	 * Save will just save list of edit operations. This will be used to reconstruct
	 * document in draft state.
	 * 
	 * Load will reconstruct.
	 * 
	 * when version it, then run it through generator
	 */
	private void save() {
		ArrayList<HyperOperation> hops = area.getOperations();
		ByteArrayOutputStream operations = new ByteArrayOutputStream();
		System.out.println("------------------------------");

		try {
			for (OverlayStream span : variantStream.getStreamElements()) {
				System.out.println(span);
			}
		} catch (MalformedSpanException e1) {
			e1.printStackTrace();
		}

		for (HyperOperation hop : hops) {
			System.out.println("SAVE:" + hop);
			// TODO: these will be replaced by either DocumentFile.Builder or an
			// OulipoMachone
			/*
			 * if (hop.getOperation().equals(OpCode.INSERT_TEXT)) { InsertTextOp op = new
			 * InsertTextOp(hop.getDocumentRegion().getStart() + 1, hop.getText()); try {
			 * operations.write(op.encode()); } catch (IOException e) {
			 * ctx.showMessage("Failed to write operation: " + e.getMessage());
			 * e.printStackTrace(); } } else if (hop.getOperation().equals(OpCode.DELETE)) {
			 * try { DeleteVariantOp op = new DeleteVariantOp(new VariantSpan((long)
			 * hop.getDocumentRegion().getStart() + 1, (long) hop.getText().length()));
			 * operations.write(op.encode()); } catch (IOException e) {
			 * ctx.showMessage("Failed to write operation: " + e.getMessage());
			 * e.printStackTrace(); } catch (MalformedSpanException e) {
			 * e.printStackTrace(); } }
			 */
		}
		hops.clear();

		String opMessage = BaseEncoding.base64Url().encode(operations.toByteArray());
		System.out.println("Data: " + opMessage);
		/*
		 * tumblerService.loadOperations(address.value, opMessage, new
		 * Callback<String>() {
		 * 
		 * @Override public void onFailure(Call<String> arg0, Throwable arg1) {
		 * arg1.printStackTrace();
		 * ctx.showMessage("Failed to sync document changes with Oulipo Server: " +
		 * arg1.getMessage()); }
		 * 
		 * @Override public void onResponse(Call<String> arg0, Response<String> arg1) {
		 * ctx.showMessage("Synched document changes with Oulipo Server"); } });
		 */

	}

	@Override
	public void show(AddressBarController controller) throws IOException {
		super.show(controller);

		// controller.getContext().getAccountManager().getActiveAccount().publicKey
		// controller.getContext().getApplicationContext().getStage(id)
		variantStream = new RopeVariantStream<OverlayStream>(address.value);
		this.area = DocumentArea.newInstance(address.value, ctx, variantStream);
		this.renderPane = new VirtualizedScrollPane<>(area);
		area.setMaxWidth(500);

		HBox editToolbar = new HBox(3.0);
		editToolbar.getChildren().addAll(optionButtons(), verticalSeparator(), formatButtons(), verticalSeparator(),
				insertButtons());
		editToolbar.setOnMouseEntered(e_ -> {
			editToolbar.setCursor(Cursor.HAND);
		});

		VBox vbox = new VBox();
		VBox.setVgrow(renderPane, Priority.ALWAYS);
		vbox.getChildren().addAll(editToolbar, new Separator(), renderPane);
		vbox.setMaxWidth(500);

		renderPane.prefHeightProperty().bind(vbox.heightProperty());
		/*
		 * tumblerService.getDocument(address.value, new retrofit2.Callback<Document>()
		 * {
		 * 
		 * @Override public void onFailure(Call<Document> arg0, Throwable arg1) {
		 * arg1.printStackTrace();
		 * ctx.showMessage("Problem getting document meta-data: " + arg1.getMessage());
		 * }
		 * 
		 * @Override public void onResponse(Call<Document> arg0, Response<Document>
		 * response) { Platform.runLater(() -> {
		 * 
		 * if (response.isSuccessful()) { final Document document = response.body();
		 * ctx.getTabManager().getSelectedTab().setTitle(document.title);
		 * addressBarController.addContent(vbox, document.title);
		 * 
		 * } else { addressBarController.addContent(vbox, "New Title"); }
		 * 
		 * // TODO: Does user own this document? HBox box = new HBox();
		 * box.getChildren().add(ButtonsCreator.writeDocument(addressBarController,
		 * address)); addressBarController.addRightAddressBar(null); });
		 * 
		 * } });
		 */

		// We could get this locally by pulling out chain of hashes
		// and building through OulipoMachine. Then call getVariants().
		// If user doesn't own document then can't chain edits.
		// TODO: GET VIRTUAL CONTEXT FROM DOCUMENT_FILE
	}

	private Separator verticalSeparator() {
		Separator separator = new Separator(Orientation.VERTICAL);
		return separator;
	}

}
