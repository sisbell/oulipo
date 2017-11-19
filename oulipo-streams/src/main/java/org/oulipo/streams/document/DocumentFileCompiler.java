package org.oulipo.streams.document;

import static com.google.common.io.BaseEncoding.base64Url;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.URLDecoder;
import java.security.Key;
import java.util.Map;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.oulipo.streams.Compiler;
import org.oulipo.streams.opcodes.Op;
import org.oulipo.streams.overlays.Overlay;

public final class DocumentFileCompiler implements Compiler<DocumentFile> {

	public static DocumentFileCompiler createInstance() {
		return new DocumentFileCompiler();
	}

	@Override
	public String compileEncrypted(DocumentFile doc, ECKey ecKey, Key key) throws Exception {
		if (ecKey == null) {
			throw new IllegalArgumentException("ECKey is null");
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(baos);
		String publicKeyHash = URLDecoder.decode(ecKey.toAddress(MainNetParams.get()).toString(), "UTF-8");

		os.write(DocumentFile.MAGIC);
		os.writeShort(0);
		os.writeUTF(publicKeyHash);
		os.writeUTF(doc.hasGenesisHash() ? doc.getGenesisHash() : "");
		os.writeUTF(doc.hasHashPreviousBlock() ? doc.getHashPreviousBlock() : "");
		os.writeInt(doc.getMajorVersion());
		os.writeInt(doc.getMinorVersion());
		os.writeLong(System.currentTimeMillis());

		os.writeInt(doc.stringPoolSize());
		for (Map.Entry<Integer, String> entry : doc.getStringPool().entrySet()) {
			os.writeInt(entry.getKey());
			os.writeUTF(entry.getValue());
		}
		os.writeInt(doc.overlayPoolSize());
		for (Map.Entry<Integer, Overlay> entry : doc.getOverlayPool().entrySet()) {
			os.writeInt(entry.getKey());
			os.write(entry.getValue().encode());
		}

		os.writeUTF(doc.hasInvariantStream() ? doc.getInvariantStream() : "");
		// TODO: if enc stream need key
		os.writeUTF(doc.hasEncryptedInvariantStream() ? encrypt(doc.getEncyptedInvariantStream(), key) : "");

		os.writeInt(doc.operationCount());
		for (Op op : doc.getOps()) {
			os.write(op.encode());
		}

		String message = base64Url().encode(baos.toByteArray());
		String signature64 = ecKey.signMessage(message);
		return message + "." + publicKeyHash + "." + signature64;
	}
}
