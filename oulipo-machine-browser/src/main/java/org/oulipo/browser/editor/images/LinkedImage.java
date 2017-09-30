package org.oulipo.browser.editor.images;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fxmisc.richtext.model.Codec;

import javafx.scene.Node;

public interface LinkedImage<S> {
	static <S> Codec<LinkedImage<S>> codec(Codec<S> styleCodec) {
		return new Codec<LinkedImage<S>>() {

			@Override
			public RealLinkedImage<S> decode(DataInputStream is) throws IOException {
				String imagePath = Codec.STRING_CODEC.decode(is);
				imagePath = imagePath.replace("\\", "/");
				S style = styleCodec.decode(is);
				return new RealLinkedImage<>(imagePath, style);
			}

			@Override
			public void encode(DataOutputStream os, LinkedImage<S> i) throws IOException {
				if (i.getStyle() != null) {
					String externalPath = i.getImagePath().replace("\\", "/");
					Codec.STRING_CODEC.encode(os, externalPath);
					styleCodec.encode(os, i.getStyle());
				}
			}

			@Override
			public String getName() {
				return "LinkedImage<" + styleCodec.getName() + ">";
			}

		};
	}

	Node createNode();

	/**
	 * @return The path of the image to render.
	 */
	String getImagePath();

	S getStyle();

	LinkedImage<S> setStyle(S style);
}
