package org.oulipo.streams.overlays;

import java.io.IOException;

public interface Overlay {

	public static final byte ONTOLOGICAL = 0x1;

	public static final byte PRESENTER = 0x0;

	public static final byte TOPOLOGICAL = 0x2;

	byte[] encode() throws IOException;

}
