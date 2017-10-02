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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyledText;
import org.oulipo.browser.api.AddressBarController;
import org.oulipo.browser.editor.DocumentArea;
import org.oulipo.browser.editor.LinkFactory;
import org.oulipo.browser.editor.LinkType2;
import org.oulipo.browser.editor.ParStyle;
import org.oulipo.browser.editor.images.LinkedImage;
import org.oulipo.browser.editor.images.RealLinkedImage;
import org.oulipo.browser.pages.BaseController;
import org.oulipo.browser.tables.ButtonsCreator;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.Link;
import org.oulipo.resources.model.Virtual;
import org.oulipo.resources.ops.HyperOperation;
import org.oulipo.resources.ops.HyperOperation.OpCode;
import org.oulipo.services.responses.Endset;
import org.oulipo.services.responses.EndsetByType;
import org.oulipo.streams.VirtualContent;
import org.oulipo.streams.opcodes.InsertTextOp;
import org.reactfx.SuspendableNo;
import org.reactfx.util.Either;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;

import javafx.application.Platform;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class PublisherController extends BaseController {

	private DocumentArea area;

	private Map<String, Endset> endsets = new HashMap<>();

	private VirtualizedScrollPane<GenericStyledArea<ParStyle, Either<StyledText<LinkType2>, LinkedImage<LinkType2>>, LinkType2>> renderPane;

	private final SuspendableNo updatingToolbar = new SuspendableNo();

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
					StyleSpans<LinkType2> styles = area.getStyleSpans(selection);

					bold = styles.styleStream().anyMatch(s -> s.bold.orElse(false));
					italic = styles.styleStream().anyMatch(s -> s.italic.orElse(false));
					underline = styles.styleStream().anyMatch(s -> s.underline.orElse(false));
					strike = styles.styleStream().anyMatch(s -> s.strikethrough.orElse(false));

				} else {
					int p = area.getCurrentParagraph();
					int col = area.getCaretColumn();
					LinkType2 style = area.getStyleAtPosition(p, col);
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

	private void getEndsets() throws MalformedTumblerException, IOException {
		tumblerService.getEndsets(address.toExternalForm(), new Callback<EndsetByType>() {

			@Override
			public void onFailure(Call<EndsetByType> arg0, Throwable arg1) {
				arg1.printStackTrace();
				ctx.showMessage("Failed to retreive endsets: " + arg1.getMessage());
			}

			@Override
			public void onResponse(Call<EndsetByType> arg0, Response<EndsetByType> arg1) {
				EndsetByType endset = arg1.body();
				endsets = endset.endsets;

				Platform.runLater(() -> {
					for (Entry<String, Endset> e : endsets.entrySet()) {
						HashSet<TumblerAddress> spans = e.getValue().fromVSpans;
						for (TumblerAddress span : spans) {
							area.applyStyle(span, TumblerAddress.createWithNoException(e.getKey()));
						}
					}
				});
				System.out.println("ENDSETS: " + endsets);
			}
		});
	}

	private List<String> getLinkAddressesForType(TumblerAddress linkType) {
		List<String> linkAddresses = new ArrayList<>();
		for (Map.Entry<String, Endset> entry : endsets.entrySet()) {
			Endset endset = entry.getValue();
			for (TumblerAddress type : endset.types) {
				if (type.equals(linkType)) {
					linkAddresses.add(entry.getKey());
					break;
				}
			}
		}
		return linkAddresses;
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
			String imagePath = selectedFile.getAbsolutePath();
			imagePath = imagePath.replace('\\', '/');
			ReadOnlyStyledDocument<ParStyle, Either<StyledText<LinkType2>, LinkedImage<LinkType2>>, LinkType2> ros = ReadOnlyStyledDocument
					.fromSegment(Either.right(new RealLinkedImage<>(imagePath, LinkType2.EMPTY)), ParStyle.EMPTY,
							LinkType2.EMPTY, area.getSegOps());
			area.replaceSelection(ros);
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
		StyleSpans<LinkType2> spans = area.getStyleSpans(0, 50);
		Iterator<StyleSpan<LinkType2>> it = spans.iterator();
		while (it.hasNext()) {
			StyleSpan<LinkType2> span = it.next();

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
		area.flush();

		for (HyperOperation hop : hops) {
			System.out.println("SAVE:" + hop);
			if (hop.getOperation().equals(OpCode.INSERT)) {
				InsertTextOp op = new InsertTextOp(hop.getDocumentRegion().getStart() + 1, hop.getText());
				try {
					operations.write(op.toBytes());
				} catch (IOException e) {
					ctx.showMessage("Failed to write operation: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		hops.clear();

		try {
			String opMessage = BaseEncoding.base64Url().encode(operations.toByteArray());
			System.out.println("Data: " + opMessage);
			tumblerService.loadOperations(address.value, opMessage, new Callback<String>() {

				@Override
				public void onFailure(Call<String> arg0, Throwable arg1) {
					arg1.printStackTrace();
					ctx.showMessage("Failed to sync document changes with Oulipo Server: " + arg1.getMessage());
				}

				@Override
				public void onResponse(Call<String> arg0, Response<String> arg1) {
					System.out.println("FINISH:" + arg1.body());
					ctx.showMessage("Synched document changes with Oulipo Server");
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			ctx.showMessage("Failed to sync document changes with Oulipo Server: " + e.getMessage());
		}

		// TODO: bulk upload
		StyleSpans<LinkType2> spans = area.getStyleSpans(0, area.getLength());
		Iterator<StyleSpan<LinkType2>> it = spans.iterator();

		Link boldLink = LinkFactory.bold(address);
		Link underlineLink = LinkFactory.underline(address);
		Link strikeThroughLink = LinkFactory.strikeThrough(address);
		Link italicLink = LinkFactory.italic(address);

		int position = 0;
		while (it.hasNext()) {
			StyleSpan<LinkType2> span = it.next();
			LinkType2 linkType = span.getStyle();
			System.out.println(span);

			if (linkType.bold.isPresent() && linkType.bold.get()) {
				try {
					TumblerAddress s = TumblerAddress
							.create(address.toExternalForm() + ".0.1." + (position + 1) + "~1." + span.getLength());
					boldLink.fromVSpans.add(s);
				} catch (MalformedTumblerException e) {
					e.printStackTrace();
				}
			}

			if (linkType.italic.isPresent() && linkType.italic.get()) {
				// italicLink.fromVSpans.add(linkType.getSpanAddress());
			}

			if (linkType.strikethrough.isPresent() && linkType.strikethrough.get()) {
				// strikeThroughLink.fromVSpans.add(linkType.getSpanAddress());
			}

			if (linkType.underline.isPresent() && linkType.underline.get()) {
				// underlineLink.fromVSpans.add(linkType.getSpanAddress());
			}

			position += span.getLength();
			System.out.println("LEN: " + span.getLength() + ", " + span.getStyle().toCss());
		}

		setLink(boldLink);
		setLink(italicLink);
		setLink(strikeThroughLink);
		setLink(underlineLink);
	}

	private void setLink(Link link) {
		if (!link.fromVSpans.isEmpty()) {
			try {
				tumblerService.createOrUpdateLink(link, new Callback<Link>() {

					@Override
					public void onFailure(Call<Link> arg0, Throwable arg1) {
						arg1.printStackTrace();
						ctx.showMessage("Failed to sync link changes with Oulipo Server: " + arg1.getMessage());
					}

					@Override
					public void onResponse(Call<Link> arg0, Response<Link> arg1) {
						Link link = arg1.body();
						ctx.showMessage("Synced link changes with Oulipo Server: " + link.resourceId);
						System.out.println(link);
					}

				});
			} catch (IOException e) {
				ctx.showMessage("Failed to sync link changes with Oulipo Server: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void show(AddressBarController controller) throws MalformedTumblerException, IOException {
		super.show(controller);

		// controller.getContext().getAccountManager().getActiveAccount().publicKey
		// controller.getContext().getApplicationContext().getStage(id)
		this.area = DocumentArea.newInstance(address, ctx);
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

		tumblerService.getDocument(address.toTumblerAuthority(), new retrofit2.Callback<Document>() {

			@Override
			public void onFailure(Call<Document> arg0, Throwable arg1) {
				arg1.printStackTrace();
				ctx.showMessage("Problem getting document meta-data: " + arg1.getMessage());
			}

			@Override
			public void onResponse(Call<Document> arg0, Response<Document> response) {
				Platform.runLater(() -> {

					if (response.isSuccessful()) {
						final Document document = response.body();
						ctx.getTabManager().getSelectedTab().setTitle(document.title);
						addressBarController.addContent(vbox, document.title);

					} else {
						addressBarController.addContent(vbox, "New Title");
					}
					
					// TODO: Does user own this document?
					HBox box = new HBox();
					box.getChildren().add(ButtonsCreator.writeDocument(addressBarController, address));
					addressBarController.addRightAddressBar(null);

				});

			
			}
		});

		tumblerService.getVirtual(address.toTumblerAuthority(), null, new retrofit2.Callback<Virtual>() {

			@Override
			public void onFailure(Call<Virtual> arg0, Throwable arg1) {
				arg1.printStackTrace();
				ctx.showMessage("Unable to fetch text from server: " + arg1.getMessage());
			}

			@Override
			public void onResponse(Call<Virtual> arg0, Response<Virtual> response) {
				if (response.isSuccessful()) {
					final Virtual virtual = response.body();

					Platform.runLater(() -> {
						// TODO: apply links
						area.writeOpsOff();
						for (VirtualContent vc : virtual.content) {
							// TODO: if transclusion, apply style
							area.appendText(vc.content);
						}
						area.writeOpsOn();
						try {
							getEndsets();
						} catch (IOException e) {
							e.printStackTrace();
							ctx.showMessage("Problem retrieving document endsets: " + e.getMessage());
						}
					});
				}
			}
		});
	}

	private Separator verticalSeparator() {
		Separator separator = new Separator(Orientation.VERTICAL);
		return separator;
	}

}
