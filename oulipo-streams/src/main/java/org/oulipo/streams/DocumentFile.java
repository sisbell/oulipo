/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
 *
 * You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for 
 * additional information regarding copyright ownership. 
 *******************************************************************************/
package org.oulipo.streams;

import static com.google.common.io.BaseEncoding.base64Url;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.opcodes.ApplyOverlayOp;
import org.oulipo.streams.opcodes.CopyVariantOp;
import org.oulipo.streams.opcodes.DeleteVariantOp;
import org.oulipo.streams.opcodes.MoveVariantOp;
import org.oulipo.streams.opcodes.Op;
import org.oulipo.streams.opcodes.PutInvariantMediaOp;
import org.oulipo.streams.opcodes.PutInvariantSpanOp;
import org.oulipo.streams.opcodes.PutOverlayMediaOp;
import org.oulipo.streams.opcodes.PutOverlayOp;
import org.oulipo.streams.opcodes.SwapVariantOp;
import org.oulipo.streams.opcodes.ToggleOverlayOp;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;

/**
 * Hold the parts of a document, including edit operations, overlays and added
 * text.
 * 
 * DocumentFiles are chained through the hash field.
 * 
 */
public final class DocumentFile {

	/**
	 * Builder for a DocumentFile. The build methods can pass in a
	 * <code>DataInputStream</code> or can pass in non-stream parameter values. Do
	 * not mix these method types when building a Document.
	 */
	public static class Builder {

		/**
		 * Contains text to be appended to the encrypted invariant stream.
		 */
		private StringBuilder encryptedInvariantStream = new StringBuilder();

		/**
		 * The hash of the previous block (or documentFile)
		 */
		private String hashPreviousBlock;

		private final TumblerAddress homeDocument;

		/**
		 * Contains text to be appended to the invariant stream.
		 */
		private StringBuilder invariantStream = new StringBuilder();

		/**
		 * Current number of media items in media pool
		 */
		private int mediaCount;

		/**
		 * Contains media hashes. Key = media hash, Value = index in media pool
		 */
		private BiMap<String, Integer> mediaPool = HashBiMap.create();

		/**
		 * List of operations to perform on document
		 */
		private List<Op> ops = new ArrayList<>();

		/**
		 * Current number of tumbler addresses in tumbler pool
		 */
		private int tumblerCount = 1;

		/**
		 * Pool of tumbler addresses. Key = tumbler address, Value = index in tumbler
		 * pool
		 */
		private BiMap<String, Integer> tumblerPool = HashBiMap.create();

		/**
		 * Constructs a <code>DocumentFile.Builder</code> with the specified home
		 * document.
		 * 
		 * @param homeDocument
		 */
		public Builder(TumblerAddress homeDocument) {
			if (homeDocument == null) {
				throw new IllegalArgumentException("homeDocument is null");
			}
			if (!homeDocument.hasDocument()) {
				throw new IllegalArgumentException("Tumbler does not contain a home document");
			}
			if (homeDocument.hasElement()) {
				throw new IllegalArgumentException("Tumbler contains an element field");
			}
			this.homeDocument = homeDocument;
			tumblerPool.put(homeDocument.value, 0);
		}

		/**
		 * Adds a media hash to the media pool.
		 * 
		 * @param hash
		 *            the media hash
		 * @return the index of the specified hash
		 */
		private Integer addMediaHash(String hash) {
			if (Strings.isNullOrEmpty(hash)) {
				throw new IllegalArgumentException("Media hash is null");
			}
			Integer index = mediaPool.get(hash);
			if (index == null) {
				index = mediaCount;
				mediaPool.put(hash, index);
				mediaCount++;
			}
			return index;
		}

		/**
		 * Adds a tumbler address to the tumbler pool
		 * 
		 * @param tumbler
		 *            the tumbler to add
		 * @return the index of the tumbler address
		 */
		private Integer addTumbler(TumblerAddress tumbler) {
			if (tumbler == null) {
				throw new IllegalArgumentException("tumbler is null");
			}
			Integer index = tumblerPool.get(tumbler.value);
			if (index == null) {
				index = tumblerCount;
				tumblerPool.put(tumbler.value, index);
				tumblerCount++;
			}
			return index;
		}

		/**
		 * Adds set of tumbler addresses to tumbler pool. Returns set of tumbler indices
		 * in pool.
		 * 
		 * @param linkTypes
		 * @return
		 */
		private Set<Integer> addTumblersToPool(Set<TumblerAddress> linkTypes) {
			if (linkTypes == null) {
				throw new IllegalArgumentException("linkTypes are null");
			}
			Set<Integer> links = new HashSet<>();
			for (TumblerAddress tumbler : linkTypes) {
				Integer index = tumblerPool.get(tumbler.value);
				if (index == null) {
					index = tumblerCount;
					tumblerPool.put(tumbler.value, index);
					tumblerCount++;
				}
				links.add(index);
			}
			return links;
		}

		/**
		 * Appends text to the encrypted invariant stream. The specified text is
		 * unencrypted and will encrypted later.
		 * 
		 * @param text
		 *            the text to append
		 * @return
		 */
		public Builder appendEncryptedText(String text) {
			if (!Strings.isNullOrEmpty(text)) {
				encryptedInvariantStream.append(text);
			}
			return this;
		}

		/**
		 * Appends text to the invariant string area
		 * 
		 * @param text
		 *            the text to append
		 * @return
		 */
		public Builder appendText(String text) {
			if (!Strings.isNullOrEmpty(text)) {
				invariantStream.append(text);
			}
			return this;
		}

		public Builder applyOverlay(DataInputStream dis) throws MalformedSpanException, IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			ops.add(new ApplyOverlayOp(dis));
			return this;
		}

		public Builder applyOverlay(VariantSpan variantSpan, Set<TumblerAddress> links) {
			ops.add(new ApplyOverlayOp(variantSpan, addTumblersToPool(links)));
			return this;
		}

		/**
		 * Builds the document file
		 * 
		 * @return the document file
		 */
		public DocumentFile build() {
			DocumentFile file = new DocumentFile();
			file.hashPreviousBlock = hashPreviousBlock;
			file.tumblerPool = tumblerPool.inverse();
			file.ops = ops;
			file.invariantStream = invariantStream.toString();
			file.encryptedInvariantStream = null;
			file.mediaPool = mediaPool.inverse();
			file.homeDocument = homeDocument;
			return file;
		}

		public Builder copyVariant(DataInputStream dis) throws IOException, MalformedSpanException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}

			ops.add(new CopyVariantOp(dis));
			return this;
		}

		public Builder copyVariant(long to, VariantSpan variantSpan) {
			ops.add(new CopyVariantOp(to, variantSpan));
			return this;
		}

		public Builder deleteVariant(DataInputStream dis) throws MalformedSpanException, IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}

			ops.add(new DeleteVariantOp(dis));
			return this;
		}

		public Builder deleteVariant(VariantSpan variantSpan) {
			ops.add(new DeleteVariantOp(variantSpan));
			return this;
		}

		public Builder moveVariant(DataInputStream dis) throws IOException, MalformedSpanException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}

			ops.add(new MoveVariantOp(dis));
			return this;
		}

		public Builder moveVariant(long to, VariantSpan variantSpan) {
			ops.add(new MoveVariantOp(to, variantSpan));
			return this;
		}

		public Builder previousHashBlock(String hash) {
			this.hashPreviousBlock = hash;
			return this;
		}

		public Builder putInvariantMediaOp(DataInputStream dis) throws IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			ops.add(new PutInvariantMediaOp(dis));
			return this;
		}

		public Builder putInvariantMediaOp(long to, String hash, TumblerAddress mediaAddress)
				throws MalformedSpanException {
			if (!mediaAddress.isElementTumbler()) {
				throw new MalformedSpanException("Expecting a mediaAddress: " + mediaAddress.value);
			}
			ops.add(new PutInvariantMediaOp(to, addMediaHash(hash), addTumbler(mediaAddress)));
			return this;
		}

		public Builder putInvariantSpan(DataInputStream dis) throws IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			ops.add(new PutInvariantSpanOp(dis));
			return this;
		}

		public Builder putInvariantSpan(long to, long start, long width, TumblerAddress homeDocument) {
			ops.add(new PutInvariantSpanOp(to, start, width, addTumbler(homeDocument)));
			return this;
		}

		public Builder putOverlayMediaOp(DataInputStream dis) throws MalformedSpanException, IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			ops.add(new PutOverlayMediaOp(dis));
			return this;
		}

		public Builder putOverlayMediaOp(long to, String hash, TumblerAddress mediaAddress,
				Set<TumblerAddress> linkTypes) {
			ops.add(new PutOverlayMediaOp(to, addMediaHash(hash), addTumbler(mediaAddress),
					addTumblersToPool(linkTypes)));
			return this;
		}

		public Builder putOverlayOp(DataInputStream dis) throws MalformedSpanException, IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			ops.add(new PutOverlayOp(dis));
			return this;
		}

		public Builder putOverlayOp(VariantSpan variantSpan, Set<TumblerAddress> linkTypes) {
			ops.add(new PutOverlayOp(variantSpan, addTumblersToPool(linkTypes)));
			return this;
		}

		public Builder swapVariant(DataInputStream dis) throws MalformedSpanException, IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			ops.add(new SwapVariantOp(dis));
			return this;
		}

		public Builder swapVariant(VariantSpan v1, VariantSpan v2) {
			ops.add(new SwapVariantOp(v1, v2));
			return this;
		}

		public Builder toggleOverlay(DataInputStream dis) throws MalformedSpanException, IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			ops.add(new ToggleOverlayOp(dis));
			return this;
		}

		public Builder toggleOverlay(VariantSpan variantSpan, TumblerAddress link) {
			ops.add(new ToggleOverlayOp(variantSpan, addTumbler(link)));
			return this;
		}
	}

	/**
	 * Magic bytes to detect a documentFile
	 */
	private static final byte[] magic = DatatypeConverter.parseHexBinary("daceface");

	public static void loadSpanOperations(OulipoMachine om, String base64Body)
			throws MalformedSpanException, IOException {
		byte[] bodyBytes = BaseEncoding.base64Url().decode(base64Body);
		// new DataInputStream(new ByteArrayInputStream(bodyBytes)));
	}

	// TODO: key for decrypt

	/**
	 * Reads compiled bytes and returns a <code>DocumentFile</code> instance.
	 * 
	 * @param input
	 *            the bytes of a compiled document file
	 * @return
	 * @throws IOException
	 * @throws SignatureException
	 * @throws MalformedSpanException
	 */
	public static DocumentFile read(byte[] input) throws IOException, SignatureException, MalformedSpanException {
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
		if (!Arrays.equals(magicHeader, magic)) {
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

		String previousHash = dis.readUTF();

		int tumblerPoolSize = dis.readInt();
		BiMap<Integer, String> tumblerPool = HashBiMap.create(tumblerPoolSize);
		for (int i = 0; i < tumblerPoolSize; i++) {
			tumblerPool.put(dis.readInt(), dis.readUTF());
		}

		int mediaPoolSize = dis.readInt();
		BiMap<Integer, String> mediaPool = HashBiMap.create(mediaPoolSize);
		for (int i = 0; i < mediaPoolSize; i++) {
			mediaPool.put(dis.readInt(), dis.readUTF());
		}

		String invariantStream = dis.readUTF();
		// Need to know alg
		String encryptedInvariantStream = dis.readUTF();

		DocumentFile.Builder documentFileBuilder = new DocumentFile.Builder(TumblerAddress.create(tumblerPool.get(0)));
		documentFileBuilder.previousHashBlock(previousHash);
		documentFileBuilder.mediaPool = mediaPool.inverse();
		documentFileBuilder.tumblerPool = tumblerPool.inverse();
		documentFileBuilder.appendText(invariantStream);

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

	/**
	 * Reads <code>DocumentFile<code> for the specified input stream.
	 * 
	 * @param is
	 * @return documentFile
	 * @throws IOException
	 * @throws SignatureException
	 * @throws MalformedSpanException
	 */
	public static DocumentFile read(InputStream is) throws IOException, SignatureException, MalformedSpanException {
		return read(ByteStreams.toByteArray(is));
	}

	private static void validate(String publicKey, String message, String signature) throws SignatureException {
		if (!publicKey.equals(ECKey.signedMessageToKey(message, signature).toAddress(MainNetParams.get()).toString())) {
			throw new SignatureException("Signature is incorrect");
		}
	}

	/**
	 * Contains encrypted text to be appended to the encrypted invariant stream
	 */
	private String encryptedInvariantStream;

	/**
	 * Hash of the previous DocumentFile
	 */
	private String hashPreviousBlock;

	private TumblerAddress homeDocument;

	/**
	 * Contains text to be appended to the invariant stream.
	 */
	private String invariantStream;

	/**
	 * Contains media hashes. Value = media hash, Key = index in media pool
	 */
	private Map<Integer, String> mediaPool;

	/**
	 * List of operations to perform on document
	 */
	private List<Op> ops;

	/**
	 * Pool of tumbler addresses. Value = tumbler address, Index = index in tumbler
	 * pool
	 */
	private Map<Integer, String> tumblerPool;

	private DocumentFile() {
	}

	/**
	 * Compile <code>DocumentFile</code> to binary and generated a signature using
	 * the specified key.
	 * 
	 * @param ecKey
	 *            the key to use to generate signature
	 * @return a string in the format [documentFile + "." + publicKeyHash + "." +
	 *         signature64];
	 * 
	 * @throws IOException
	 */
	public String compile(ECKey ecKey) throws IOException {
		if (ecKey == null) {
			throw new IllegalArgumentException("ECKey is null");
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(baos);
		String publicKeyHash = URLDecoder.decode(ecKey.toAddress(MainNetParams.get()).toString(), "UTF-8");

		os.write(magic);
		os.writeShort(0);
		os.writeUTF(publicKeyHash);
		os.writeUTF(Strings.isNullOrEmpty(hashPreviousBlock) ? "" : hashPreviousBlock);

		os.writeInt(tumblerPool.size());
		for (Map.Entry<Integer, String> entry : tumblerPool.entrySet()) {
			os.writeInt(entry.getKey());
			os.writeUTF(entry.getValue());
		}
		os.writeInt(mediaPool.size());
		for (Map.Entry<Integer, String> entry : mediaPool.entrySet()) {
			os.writeInt(entry.getKey());
			os.writeUTF(entry.getValue());
		}
		os.writeUTF(Strings.isNullOrEmpty(invariantStream) ? "" : invariantStream);
		os.writeUTF(Strings.isNullOrEmpty(encryptedInvariantStream) ? "" : encryptedInvariantStream);

		os.writeInt(ops.size());
		for (Op op : ops) {
			os.write(op.encode());
		}

		String message = base64Url().encode(baos.toByteArray());
		String signature64 = ecKey.signMessage(message);
		return message + "." + publicKeyHash + "." + signature64;
	}

	public String getEncyptedInvariantStream() {
		return encryptedInvariantStream;
	}

	public String getHashPreviousBlock() {
		return hashPreviousBlock;
	}

	public TumblerAddress getHomeDocument() {
		return homeDocument;
	}

	public String getInvariantStream() {
		return invariantStream;
	}

	public String getMediaHash(int mediaPoolIndex) {
		if (mediaPoolIndex < 0) {
			throw new IllegalArgumentException("mediaPoolIndex must be non-negative: " + mediaPoolIndex);
		}
		return mediaPool.get(mediaPoolIndex);
	}

	public Map<Integer, String> getMediaPool() {
		return Collections.unmodifiableMap(mediaPool);
	}

	public List<Op> getOps() {
		return Collections.unmodifiableList(ops);
	}

	/**
	 * Gets the tumbler address at the specified index in the tumblerPool
	 * 
	 * @param tumblerPoolIndex
	 * @return tumbler address at the specified index in the tumblerPool
	 * @throws MalformedTumblerException
	 *             if tumbler address in pool is malformed
	 * @IllegalArgumentException if tumblerPool index is negative
	 */
	public TumblerAddress getTumblerAddress(int tumblerPoolIndex) throws MalformedTumblerException {
		if (tumblerPoolIndex < 0) {
			throw new IllegalArgumentException("tumblerPoolIndex must be non-negative: " + tumblerPoolIndex);
		}
		return TumblerAddress.create(tumblerPool.get(tumblerPoolIndex));
	}

	public Set<TumblerAddress> getTumblerAddresses(Set<Integer> indicies) throws MalformedTumblerException {
		if (indicies == null) {
			throw new IllegalArgumentException("indicies is null");
		}
		Set<TumblerAddress> tumblers = new HashSet<>(indicies.size());
		for (int index : indicies) {
			tumblers.add(getTumblerAddress(index));
		}
		return tumblers;
	}

	public Map<Integer, String> getTumblerPool() {
		return Collections.unmodifiableMap(tumblerPool);
	}
}
