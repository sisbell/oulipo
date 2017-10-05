package org.oulipo.browser.editor.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fxmisc.richtext.model.Codec;
import org.oulipo.client.services.RemoteFileManager;

import javafx.scene.Node;

public interface RemoteImage<S> {

	static <S> Codec<RemoteImage<S>> codec(Codec<S> styleCodec) {
		return new Codec<RemoteImage<S>>() {

			@Override
			public RemoteImage<S> decode(DataInputStream is) throws IOException {
				String hash = Codec.STRING_CODEC.decode(is);
				S style = styleCodec.decode(is);
				return new IpfsRemoteImage<>(hash, style);
			}

			@Override
			public void encode(DataOutputStream os, RemoteImage<S> i) throws IOException {
				if (i.getStyle() != null) {
					String hash = i.getHash();
					Codec.STRING_CODEC.encode(os, hash);
					styleCodec.encode(os, i.getStyle());
				}
			}

			@Override
			public String getName() {
				return "RemoteImage<" + styleCodec.getName() + ">";
			}

		};
	}

	/**
	 * Gets node view for image
	 * 
	 * @return node view for image
	 */
	Node createNode(RemoteFileManager fileManager);
	
	/**
	 * Gets unique hash for image
	 * 
	 * @return unique has for image
	 */
	String getHash();
	
	S getStyle();

	RemoteImage<S> setStyle(S style);

	
}
