package org.oulipo.streams.overlays;

import java.io.IOException;

public class PresenterOverlay implements Overlay {

	public static final int BOLD = 0;

	public static final PresenterOverlay BOLD_OVERLAY = new PresenterOverlay(BOLD);

	public static final int FONT_FAMILY = 1;

	public static final int FONT_SIZE = 2;

	public static final int ITALIC = 3;

	public static final PresenterOverlay ITALIC_OVERLAY = new PresenterOverlay(ITALIC);

	public static final int STRIKE_THROUGH = 4;

	public static final PresenterOverlay STRIKE_THROUGH_OVERLAY = new PresenterOverlay(STRIKE_THROUGH);

	public static final int UNDERLINE = 5;

	public static final PresenterOverlay UNDERLINE_OVERLAY = new PresenterOverlay(UNDERLINE);

	public final int code;

	public PresenterOverlay(int code) {
		this.code = code;
	}

	@Override
	public byte[] encode() throws IOException {
		return null;
	}

}
