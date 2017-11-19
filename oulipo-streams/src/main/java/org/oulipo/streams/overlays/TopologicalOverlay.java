package org.oulipo.streams.overlays;

import java.io.IOException;

public class TopologicalOverlay implements Overlay {

	public final String toHash;

	public int toHashIndex;

	public final String type;

	public int typeIndex;

	public TopologicalOverlay(String toHash, String type) {
		this.toHash = toHash;
		this.type = type;
	}

	@Override
	public byte[] encode() throws IOException {
		return null;
	}
}
