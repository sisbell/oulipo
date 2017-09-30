package org.oulipo.browser.editor.images;

import javafx.scene.Node;

public class EmptyLinkedImage<S> implements LinkedImage<S> {

	@Override
	public Node createNode() {
		throw new AssertionError("Unreachable code");
	}

	@Override
	public String getImagePath() {
		return "";
	}

	@Override
	public S getStyle() {
		return null;
	}

	@Override
	public LinkedImage<S> setStyle(S style) {
		return this;
	}
}
