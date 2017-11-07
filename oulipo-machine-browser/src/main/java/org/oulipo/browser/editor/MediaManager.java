package org.oulipo.browser.editor;

import java.io.File;
import java.io.IOException;

import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyledText;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.editor.remote.IpfsRemoteImage;
import org.oulipo.browser.editor.remote.RemoteImage;
import org.oulipo.streams.RemoteFileManager;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.types.Overlay;
import org.reactfx.util.Either;

import javafx.stage.FileChooser;

public final class MediaManager {

	private BrowserContext ctx;
	
	private DocumentArea document;
	
	private VariantStream<Overlay> variantStream;

	public MediaManager(DocumentArea document, VariantStream<Overlay> variantStream, BrowserContext ctx) {
		this.document = document;
		this.ctx = ctx;
		this.variantStream = variantStream;
	}
	
	public void insertDialog() {
		String initialDir = System.getProperty("user.dir");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Insert image");
		fileChooser.setInitialDirectory(new File(initialDir));

		File selectedFile = fileChooser.showOpenDialog(document.getScene().getWindow());
		if (selectedFile != null) {
			RemoteFileManager manager = ctx.getApplicationContext().getRemoteFileManager();
			try {
				String hash = manager.add(selectedFile);
				ReadOnlyStyledDocument<ParStyle, Either<StyledText<LinkType>, RemoteImage<LinkType>>, LinkType> ros = ReadOnlyStyledDocument
						.fromSegment(Either.right(new IpfsRemoteImage<>(hash, LinkType.EMPTY)), ParStyle.EMPTY,
								LinkType.EMPTY, document.getSegOps());
				document.replaceSelection(ros);
				document.setImage(hash);
				
				//TODO: insert overlay
				//variantStream
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
