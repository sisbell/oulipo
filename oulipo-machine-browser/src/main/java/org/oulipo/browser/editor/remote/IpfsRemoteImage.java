package org.oulipo.browser.editor.remote;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.oulipo.streams.RemoteFileManager;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IpfsRemoteImage<S> implements RemoteImage<S> {

	private final String hash;
	
	private S style;

	public IpfsRemoteImage(String hash, S style) {
		this.hash = hash;
		this.style = style;
	}

	@Override
	public Node createNode(RemoteFileManager fileManager) {
		byte[] image;
		try {
			image = fileManager.get(getHash());
		} catch (IOException e) {
			e.printStackTrace();
			return new Label("Image not found");
		}
		return new ImageView(new Image(new ByteArrayInputStream(image), 64, 64, false, false));
	}

	@Override
	public String getHash() {
		return hash;
	}

	@Override
	public S getStyle() {
		return style;
	}

	@Override
	public RemoteImage<S> setStyle(S style) {
		return new IpfsRemoteImage<>(hash, style);
	}

}
