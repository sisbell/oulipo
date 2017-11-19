package org.oulipo.streams.overlays;

public final class PresenterStringOverlay extends PresenterOverlay {

	public final String value;

	public int valueIndex;

	public PresenterStringOverlay(int code, String value) {
		super(code);
		this.value = value;
	}

}
