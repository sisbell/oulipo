package org.oulipo.streams.document;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Base64;

import org.oulipo.streams.Decompiler;
import org.oulipo.streams.opcodes.Op;
import org.oulipo.streams.overlays.Overlay;

import com.google.common.base.Charsets;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;

public final class DocumentFileDecompiler implements Decompiler<DocumentFile> {

	public static DocumentFileDecompiler createInstance() {
		return new DocumentFileDecompiler();
	}

	@Override
	public DocumentFile decompile(String hash, InputStream is) throws Exception {
		return decompileEncrypted(hash, ByteStreams.toByteArray(is), null);
	}

	@Override
	public DocumentFile decompileEncrypted(String docHash, byte[] input, Key privateKey) throws Exception {
		String message = new String(input, Charsets.UTF_8);
		String[] tokens = message.split("[.]");
		if (tokens.length != 3) {
			throw new IOException("Invalid message format");
		}

		String docFile64 = tokens[0];
		String publicKey = tokens[1];
		String sig64 = tokens[2];

		try {
			validate(publicKey, docFile64, sig64);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new SignatureException("Can't decode signature: " + sig64, e);
		}

		byte[] docFile = BaseEncoding.base64Url().decode(docFile64);
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(docFile));

		byte[] magicHeader = new byte[4];
		dis.read(magicHeader);
		if (!Arrays.equals(magicHeader, DocumentFile.MAGIC)) {
			throw new IOException("Not a DocumentFile");
		}
		short version = dis.readShort();
		if (version != 0) {
			throw new IOException("Unsupported version");
		}

		String pubKey = dis.readUTF();
		if (!pubKey.equals(publicKey)) {
			throw new SignatureException("Public Keys do not match: " + pubKey + "!=" + publicKey);
		}

		String genesisHash = dis.readUTF();
		String previousHash = dis.readUTF();
		int majorVersion = dis.readInt();
		int minorVersion = dis.readInt();
		long timestamp = dis.readLong();

		int stringPoolSize = dis.readInt();
		BiMap<Integer, String> stringPool = HashBiMap.create(stringPoolSize);
		for (int i = 0; i < stringPoolSize; i++) {
			stringPool.put(dis.readInt(), dis.readUTF());
		}

		int overlayPoolSize = dis.readInt();
		BiMap<Integer, Overlay> overlayPool = HashBiMap.create(overlayPoolSize);
		for (int i = 0; i < overlayPoolSize; i++) {
			overlayPool.put(dis.readInt(), null);// TODO: decode overlay - and fill in values from string pool
		}

		String invariantStream = dis.readUTF();
		// Need to know alg
		String encryptedInvariantStream = dis.readUTF();

		DocumentFile.Builder documentFileBuilder = new DocumentFile.Builder(docHash);
		documentFileBuilder.genesisHash(genesisHash);
		documentFileBuilder.previousHashBlock(previousHash);
		documentFileBuilder.version(majorVersion, minorVersion);
		documentFileBuilder.timestamp(timestamp);

		documentFileBuilder.stringPool = stringPool.inverse();
		documentFileBuilder.overlayPool = overlayPool.inverse();
		documentFileBuilder.appendText(invariantStream);

		if (privateKey != null) {
			documentFileBuilder
					.appendEncryptedText(decrypt(Base64.getDecoder().decode(encryptedInvariantStream), privateKey));
		}

		int opSpanSize = dis.readInt();
		for (int i = 0; i < opSpanSize; i++) {
			byte opType = dis.readByte();
			switch (opType) {
			case Op.COPY:
				documentFileBuilder.copyVariant(dis);
				break;
			case Op.DELETE:
				documentFileBuilder.deleteVariant(dis);
				break;
			case Op.MOVE:
				documentFileBuilder.moveVariant(dis);
				break;
			case Op.PUT_INVARIANT_SPAN:
				documentFileBuilder.putInvariantSpan(dis);
				break;
			case Op.PUT_OVERLAY_MEDIA:
				documentFileBuilder.putOverlayMediaOp(dis);
				break;
			case Op.PUT_INVARIANT_MEDIA:
				documentFileBuilder.putInvariantMediaOp(dis);
				break;
			case Op.APPLY_OVERLAY:
				documentFileBuilder.applyOverlay(dis);
				break;
			case Op.TOGGLE_OVERLAY:
				documentFileBuilder.toggleOverlay(dis);
				break;
			case Op.PUT_OVERLAY:
				documentFileBuilder.putOverlayOp(dis);
				break;
			case Op.SWAP:
				documentFileBuilder.swapVariant(dis);
				break;
			}
		}

		return documentFileBuilder.build();
	}

}
