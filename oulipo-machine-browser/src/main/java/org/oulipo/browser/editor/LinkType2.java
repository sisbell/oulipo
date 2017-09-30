package org.oulipo.browser.editor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.fxmisc.richtext.model.Codec;

import javafx.scene.paint.Color;

/**
 * Holds information about the style of a text fragment.
 */
public class LinkType2 {

	public static final Codec<LinkType2> CODEC = new Codec<LinkType2>() {

		private final Codec<Optional<Color>> OPT_COLOR_CODEC = Codec.optionalCodec(Codec.COLOR_CODEC);
		private final Codec<Optional<String>> OPT_STRING_CODEC = Codec.optionalCodec(Codec.STRING_CODEC);

		private Optional<Boolean> bold(byte bius) throws IOException {
			return decodeOptionalBoolean((bius >> 6) & 3);
		}

		@Override
		public LinkType2 decode(DataInputStream is) throws IOException {
			// TumblerAddress linkAddress = TumblerAddress.create(is.readUTF());
			byte bius = is.readByte();
			Optional<Integer> fontSize = decodeOptionalUint(is.readInt());
			Optional<String> fontFamily = OPT_STRING_CODEC.decode(is);
			Optional<Color> textColor = OPT_COLOR_CODEC.decode(is);
			Optional<Color> bgrColor = OPT_COLOR_CODEC.decode(is);
			return new LinkType2(bold(bius), italic(bius), underline(bius), strikethrough(bius), fontSize, fontFamily,
					textColor, bgrColor);
		}

		private Optional<Boolean> decodeOptionalBoolean(int i) throws IOException {
			switch (i) {
			case 0:
				return Optional.empty();
			case 2:
				return Optional.of(false);
			case 3:
				return Optional.of(true);
			}
			throw new MalformedInputException(0);
		}

		private Optional<Integer> decodeOptionalUint(int i) {
			return (i < 0) ? Optional.empty() : Optional.of(i);
		}

		@Override
		public void encode(DataOutputStream os, LinkType2 s) throws IOException {
			// os.writeUTF(s.spanAddress.toExternalForm());
			os.writeByte(encodeBoldItalicUnderlineStrikethrough(s));
			os.writeInt(encodeOptionalUint(s.fontSize));
			OPT_STRING_CODEC.encode(os, s.fontFamily);
			OPT_COLOR_CODEC.encode(os, s.textColor);
			OPT_COLOR_CODEC.encode(os, s.backgroundColor);
		}

		private int encodeBoldItalicUnderlineStrikethrough(LinkType2 s) {
			return encodeOptionalBoolean(s.bold) << 6 | encodeOptionalBoolean(s.italic) << 4
					| encodeOptionalBoolean(s.underline) << 2 | encodeOptionalBoolean(s.strikethrough);
		}

		private int encodeOptionalBoolean(Optional<Boolean> ob) {
			return ob.map(b -> 2 + (b ? 1 : 0)).orElse(0);
		}

		private int encodeOptionalUint(Optional<Integer> oi) {
			return oi.orElse(-1);
		}

		@Override
		public String getName() {
			return "text-style";
		}

		private Optional<Boolean> italic(byte bius) throws IOException {
			return decodeOptionalBoolean((bius >> 4) & 3);
		}

		private Optional<Boolean> strikethrough(byte bius) throws IOException {
			return decodeOptionalBoolean((bius >> 0) & 3);
		}

		private Optional<Boolean> underline(byte bius) throws IOException {
			return decodeOptionalBoolean((bius >> 2) & 3);
		}
	};

	public static final LinkType2 EMPTY = new LinkType2();

	public static LinkType2 backgroundColor(Color color) {
		return EMPTY.updateBackgroundColor(color);
	}

	public static LinkType2 bold(boolean bold) {
		return EMPTY.updateBold(bold);
	}

	static String cssColor(Color color) {
		int red = (int) (color.getRed() * 255);
		int green = (int) (color.getGreen() * 255);
		int blue = (int) (color.getBlue() * 255);
		return "rgb(" + red + ", " + green + ", " + blue + ")";
	}

	public static LinkType2 fontFamily(String family) {
		return EMPTY.updateFontFamily(family);
	}

	public static LinkType2 fontSize(int fontSize) {
		return EMPTY.updateFontSize(fontSize);
	}

	public static LinkType2 italic(boolean italic) {
		return EMPTY.updateItalic(italic);
	}

	public static LinkType2 strikethrough(boolean strikethrough) {
		return EMPTY.updateStrikethrough(strikethrough);
	}

	public static LinkType2 textColor(Color color) {
		return EMPTY.updateTextColor(color);
	}

	public static LinkType2 underline(boolean underline) {
		return EMPTY.updateUnderline(underline);
	}

	public final Optional<Color> backgroundColor;
	public final Optional<Boolean> bold;
	public final Optional<String> fontFamily;
	public final Optional<Integer> fontSize;
	public final Optional<Boolean> italic;
	public final Optional<Boolean> strikethrough;
	public final Optional<Color> textColor;
	public final Optional<Boolean> underline;

	public LinkType2() {
		this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.empty(), Optional.empty());
	}

	public LinkType2(Optional<Boolean> bold, Optional<Boolean> italic, Optional<Boolean> underline,
			Optional<Boolean> strikethrough, Optional<Integer> fontSize, Optional<String> fontFamily,
			Optional<Color> textColor, Optional<Color> backgroundColor) {
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
		this.strikethrough = strikethrough;
		this.fontSize = fontSize;
		this.fontFamily = fontFamily;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof LinkType2) {
			LinkType2 that = (LinkType2) other;
			return Objects.equals(this.bold, that.bold) && Objects.equals(this.italic, that.italic)
					&& Objects.equals(this.underline, that.underline)
					&& Objects.equals(this.strikethrough, that.strikethrough)
					&& Objects.equals(this.fontSize, that.fontSize) && Objects.equals(this.fontFamily, that.fontFamily)
					&& Objects.equals(this.textColor, that.textColor)
					&& Objects.equals(this.backgroundColor, that.backgroundColor);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(bold, italic, underline, strikethrough, fontSize, fontFamily, textColor, backgroundColor);
	}

	public String toCss() {
		StringBuilder sb = new StringBuilder();

		if (bold.isPresent()) {
			if (bold.get()) {
				sb.append("-fx-font-weight: bold;");
			} else {
				sb.append("-fx-font-weight: normal;");
			}
		}

		if (italic.isPresent()) {
			if (italic.get()) {
				sb.append("-fx-font-style: italic;");
			} else {
				sb.append("-fx-font-style: normal;");
			}
		}

		if (underline.isPresent()) {
			if (underline.get()) {
				sb.append("-fx-underline: true;");
			} else {
				sb.append("-fx-underline: false;");
			}
		}

		if (strikethrough.isPresent()) {
			if (strikethrough.get()) {
				sb.append("-fx-strikethrough: true;");
			} else {
				sb.append("-fx-strikethrough: false;");
			}
		}

		if (fontSize.isPresent()) {
			sb.append("-fx-font-size: " + fontSize.get() + "pt;");
		}

		if (fontFamily.isPresent()) {
			sb.append("-fx-font-family: " + fontFamily.get() + ";");
		}

		if (textColor.isPresent()) {
			Color color = textColor.get();
			sb.append("-fx-fill: " + cssColor(color) + ";");
		}

		if (backgroundColor.isPresent()) {
			Color color = backgroundColor.get();
			sb.append("-rtfx-background-color: " + cssColor(color) + ";");
		}
		// TODO: highlight based on address - each address has assigned style/color
		return sb.toString();
	}

	@Override
	public String toString() {
		List<String> styles = new ArrayList<>();

		bold.ifPresent(b -> styles.add(b.toString()));
		italic.ifPresent(i -> styles.add(i.toString()));
		underline.ifPresent(u -> styles.add(u.toString()));
		strikethrough.ifPresent(s -> styles.add(s.toString()));
		fontSize.ifPresent(s -> styles.add(s.toString()));
		fontFamily.ifPresent(f -> styles.add(f.toString()));
		textColor.ifPresent(c -> styles.add(c.toString()));
		backgroundColor.ifPresent(b -> styles.add(b.toString()));

		return String.join(",", styles);
	}

	public LinkType2 updateBackgroundColor(Color backgroundColor) {
		return new LinkType2(bold, italic, underline, strikethrough, fontSize, fontFamily, textColor,
				Optional.of(backgroundColor));
	}

	public LinkType2 updateBold(boolean bold) {
		return new LinkType2(Optional.of(bold), italic, underline, strikethrough, fontSize, fontFamily, textColor,
				backgroundColor);
	}

	public LinkType2 updateFontFamily(String fontFamily) {
		return new LinkType2(bold, italic, underline, strikethrough, fontSize, Optional.of(fontFamily), textColor,
				backgroundColor);
	}

	public LinkType2 updateFontSize(int fontSize) {
		return new LinkType2(bold, italic, underline, strikethrough, Optional.of(fontSize), fontFamily, textColor,
				backgroundColor);
	}

	public LinkType2 updateItalic(boolean italic) {
		return new LinkType2(bold, Optional.of(italic), underline, strikethrough, fontSize, fontFamily, textColor,
				backgroundColor);
	}

	public LinkType2 updateStrikethrough(boolean strikethrough) {
		return new LinkType2(bold, italic, underline, Optional.of(strikethrough), fontSize, fontFamily, textColor,
				backgroundColor);
	}

	public LinkType2 updateTextColor(Color textColor) {
		return new LinkType2(bold, italic, underline, strikethrough, fontSize, fontFamily, Optional.of(textColor),
				backgroundColor);
	}

	public LinkType2 updateUnderline(boolean underline) {
		return new LinkType2(bold, italic, Optional.of(underline), strikethrough, fontSize, fontFamily, textColor,
				backgroundColor);
	}

	public LinkType2 updateWith(LinkType2 mixin) {
		return new LinkType2(mixin.bold.isPresent() ? mixin.bold : bold,
				mixin.italic.isPresent() ? mixin.italic : italic,
				mixin.underline.isPresent() ? mixin.underline : underline,
				mixin.strikethrough.isPresent() ? mixin.strikethrough : strikethrough,
				mixin.fontSize.isPresent() ? mixin.fontSize : fontSize,
				mixin.fontFamily.isPresent() ? mixin.fontFamily : fontFamily,
				mixin.textColor.isPresent() ? mixin.textColor : textColor,
				mixin.backgroundColor.isPresent() ? mixin.backgroundColor : backgroundColor);
	}

}