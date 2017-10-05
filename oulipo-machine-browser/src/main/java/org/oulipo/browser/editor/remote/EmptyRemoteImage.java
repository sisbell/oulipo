package org.oulipo.browser.editor.remote;

import org.oulipo.client.services.RemoteFileManager;

import javafx.scene.Node;

public class EmptyRemoteImage<S> implements RemoteImage<S> {

	@Override
	public String getHash() {
		return "";
	}

	@Override
	public Node createNode(RemoteFileManager fileManager) {
		throw new AssertionError("Unreachable code");
	}

	@Override
	public S getStyle() {
		return null;
	}

	@Override
	public RemoteImage<S> setStyle(S style) {
		return null;
	}

}
