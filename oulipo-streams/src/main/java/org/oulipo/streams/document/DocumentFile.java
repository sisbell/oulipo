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
package org.oulipo.streams.document;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.oulipo.rdf.NodeType;
import org.oulipo.rdf.Statement;
import org.oulipo.rdf.Thing;
import org.oulipo.rdf.ThingToStatements;
import org.oulipo.rdf.ThingToString;
import org.oulipo.streams.Decompiler;
import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.opcodes.ApplyOverlayOp;
import org.oulipo.streams.opcodes.CopyVariantOp;
import org.oulipo.streams.opcodes.DeleteVariantOp;
import org.oulipo.streams.opcodes.MoveVariantOp;
import org.oulipo.streams.opcodes.Op;
import org.oulipo.streams.opcodes.PutInvariantMediaOp;
import org.oulipo.streams.opcodes.PutInvariantSpanOp;
import org.oulipo.streams.opcodes.PutMetaDataOp;
import org.oulipo.streams.opcodes.PutOverlayMediaOp;
import org.oulipo.streams.opcodes.PutOverlayOp;
import org.oulipo.streams.opcodes.SwapVariantOp;
import org.oulipo.streams.opcodes.ToggleOverlayOp;
import org.oulipo.streams.overlays.Overlay;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

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

		private final String documentHash;

		/**
		 * Contains text to be appended to the encrypted invariant stream.
		 */
		private StringBuilder encryptedInvariantStream = new StringBuilder();

		/**
		 * First hash of the <code>DocumentFile</code> chain.
		 */
		private String genesisHash;

		/**
		 * The hash of the previous block (or documentFile)
		 */
		private String hashPreviousBlock;

		/**
		 * Contains text to be appended to the invariant stream.
		 */
		private StringBuilder invariantStream = new StringBuilder();

		/**
		 * Major version of document
		 */
		private int majorVersion;

		/**
		 * Minor version of document
		 */
		private int minorVersion;

		/**
		 * List of operations to perform on document
		 */
		private List<Op> ops = new ArrayList<>();

		/**
		 * Current number of overlays in overlay pool
		 */
		private int overlayCount;

		/**
		 * Pool of overlays. Key = overlay, Value = index in overlay pool
		 */
		BiMap<Overlay, Integer> overlayPool = HashBiMap.create();

		/**
		 * Current number of strings in string pool
		 */
		private int stringCount;

		/**
		 * Pool of string. Key = string, Value = index in string pool
		 */
		BiMap<String, Integer> stringPool = HashBiMap.create();

		/**
		 * Timestamp document
		 */
		private long timestamp;

		/**
		 * Constructs a <code>DocumentFile.Builder</code> with the specified home
		 * document.
		 * 
		 * @param homeDocument
		 */
		public Builder(String documentHash) {
			if (Strings.isNullOrEmpty(documentHash)) {
				throw new IllegalArgumentException("documentHash is null");
			}
			this.documentHash = documentHash;
		}

		/**
		 * Adds an overlay to the overlay pool
		 * 
		 * @param overlay
		 *            the overlay to add
		 * @return the index of the overlay
		 */
		private Integer addOverlay(Overlay overlay) {
			if (overlay == null) {
				throw new IllegalArgumentException("overlay param is null");
			}

			Integer index = overlayPool.get(overlay);
			if (index == null) {
				index = overlayCount;
				overlayPool.put(overlay, index);
				overlayCount++;
			}
			return index;
		}

		/**
		 * Adds set of overlays to overlay pool. Returns set of overlay indices in pool.
		 * 
		 * @param linkTypes
		 * @return
		 */
		private Set<Integer> addOverlaysToPool(Set<Overlay> linkTypes) {
			if (linkTypes == null) {
				throw new IllegalArgumentException("linkTypes are null");
			}
			Set<Integer> indexes = new HashSet<>();
			for (Overlay overlay : linkTypes) {
				indexes.add(addOverlay(overlay));
			}
			return indexes;
		}

		/**
		 * Adds an overlay to the overlay pool
		 * 
		 * @param overlay
		 *            the overlay to add
		 * @return the index of the overlay
		 */
		private Integer addString(String string) {
			if (Strings.isNullOrEmpty(string)) {
				throw new IllegalArgumentException("string param is empty");
			}

			Integer index = stringPool.get(string);
			if (index == null) {
				index = stringCount;
				stringPool.put(string, index);
				stringCount++;
			}
			return index;
		}

		/**
		 * Adds set of strings to string pool. Returns set of string indices in pool.
		 * 
		 * @param strings
		 * @return
		 */
		private Set<Integer> addStringsToPool(Set<String> strings) {
			if (strings == null) {
				throw new IllegalArgumentException("linkTypes are null");
			}
			Set<Integer> indexes = new HashSet<>();
			for (String string : strings) {
				indexes.add(addString(string));
			}
			return indexes;
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

		public Builder applyOverlay(VariantSpan variantSpan, Set<Overlay> links) {
			ops.add(new ApplyOverlayOp(variantSpan, addOverlaysToPool(links)));
			return this;
		}

		/**
		 * Builds the document file
		 * 
		 * @return the document file
		 */
		public DocumentFile build() {
			DocumentFile file = new DocumentFile();
			file.documentHash = documentHash;
			file.hashPreviousBlock = hashPreviousBlock;
			file.stringPool = stringPool.inverse();
			file.ops = ops;
			file.invariantStream = invariantStream.toString();
			file.encryptedInvariantStream = encryptedInvariantStream.toString();
			file.overlayPool = overlayPool.inverse();
			file.documentHash = documentHash;
			file.genesisHash = genesisHash;
			file.timestamp = timestamp;
			file.majorVersion = majorVersion;
			file.minorVersion = minorVersion;
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

		public Builder genesisHash(String hash) {
			this.genesisHash = hash;
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

		public Builder putInvariantMediaOp(long to, String hash) throws MalformedSpanException {
			ops.add(new PutInvariantMediaOp(to, addString(hash)));
			return this;
		}

		public Builder putInvariantSpan(DataInputStream dis) throws IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			ops.add(new PutInvariantSpanOp(dis));
			return this;
		}

		public Builder putInvariantSpan(long to, long start, long width, String documentHash) {
			ops.add(new PutInvariantSpanOp(to, start, width, addString(documentHash)));
			return this;
		}

		/**
		 * Puts collection of meta-data statements into <code>DocumentFile</code>. Each
		 * statement will be added with its own <code>PutMetaDataOp</code>.
		 * 
		 * @param statements
		 *            the RDF statements to add
		 * @return this builder
		 */
		public Builder putMetaData(Collection<Statement> statements) {
			for (Statement statement : statements) {
				putMetaDataOp(statement);
			}
			return this;
		}

		public Builder putMetaData(Thing thing) throws Exception {
			putMetaData(ThingToStatements.transform(thing));
			return this;
		}

		public Builder putMetaDataOp(DataInputStream dis) throws IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			// ops.add(new PutOverlayOp(dis));
			return this;
		}

		public Builder putMetaDataOp(Statement statement) {
			StringBuilder sb = new StringBuilder();
			ThingToString.addSubject(statement.getSubject(), sb);
			int subjectIndex = addString(sb.toString());

			sb = new StringBuilder();
			ThingToString.addPredicate(statement.getPredicate(), sb);
			int predicateIndex = addString(sb.toString());

			byte objectType;
			sb = new StringBuilder();
			if (NodeType.IRI.equals(statement.getObject().getType())) {
				ThingToString.addObjectResource(statement.getObject(), sb);
				objectType = 0;
			} else {
				ThingToString.addObjectLiteral(statement.getObject(), sb);
				objectType = 1;
			}
			int objectIndex = addString(sb.toString());

			ops.add(new PutMetaDataOp(subjectIndex, predicateIndex, objectIndex, objectType));
			return this;
		}

		public Builder putOverlayMediaOp(DataInputStream dis) throws MalformedSpanException, IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			ops.add(new PutOverlayMediaOp(dis));
			return this;
		}

		public Builder putOverlayMediaOp(long to, String hash, Set<Overlay> linkTypes) {
			ops.add(new PutOverlayMediaOp(to, addString(hash), addOverlaysToPool(linkTypes)));
			return this;
		}

		public Builder putOverlayOp(DataInputStream dis) throws MalformedSpanException, IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			ops.add(new PutOverlayOp(dis));
			return this;
		}

		public Builder putOverlayOp(VariantSpan variantSpan, Set<Overlay> linkTypes) {
			ops.add(new PutOverlayOp(variantSpan, addOverlaysToPool(linkTypes)));
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

		public Builder timestamp(long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder toggleOverlay(DataInputStream dis) throws MalformedSpanException, IOException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}
			ops.add(new ToggleOverlayOp(dis));
			return this;
		}

		public Builder toggleOverlay(VariantSpan variantSpan, Overlay link) {
			ops.add(new ToggleOverlayOp(variantSpan, addOverlay(link)));
			return this;
		}

		public Builder version(int majorVersion, int minorVersion) {
			this.majorVersion = majorVersion;
			this.minorVersion = minorVersion;
			return this;
		}
	}

	public static class StreamBuilder {

		private Builder builder;

		public StreamBuilder(String documentHash) {
			builder = new Builder(documentHash);
		}

		public DocumentFile build() {
			return builder.build();
		}

		public StreamBuilder copyVariant(DataInputStream dis) throws IOException, MalformedSpanException {
			if (dis == null) {
				throw new IllegalArgumentException("input stream is null");
			}

			// ops.add(new CopyVariantOp(dis));
			return this;
		}

	}

	/**
	 * Magic bytes to detect a documentFile
	 */
	public static final byte[] MAGIC = DatatypeConverter.parseHexBinary("daceface");

	public static org.oulipo.streams.Compiler<DocumentFile> compiler() {
		return new DocumentFileCompiler();
	}

	public static Decompiler<DocumentFile> decompiler() {
		return new DocumentFileDecompiler();
	}

	private String documentHash;

	/**
	 * Contains encrypted text to be appended to the encrypted invariant stream
	 */
	private String encryptedInvariantStream;

	/**
	 * First hash of the <code>DocumentFile</code> chain.
	 */
	private String genesisHash;

	/**
	 * Hash of the previous DocumentFile
	 */
	private String hashPreviousBlock;

	/**
	 * Contains text to be appended to the invariant stream.
	 */
	private String invariantStream;

	private int majorVersion;

	private int minorVersion;

	/**
	 * List of operations to perform on document
	 */
	private List<Op> ops;

	/**
	 * Pool of overlays. Key = overlay, Value = index in overlay pool
	 */
	private BiMap<Integer, Overlay> overlayPool = HashBiMap.create();

	/**
	 * Pool of string. Key = string, Value = index in string pool
	 */
	private BiMap<Integer, String> stringPool = HashBiMap.create();

	/**
	 * Timestamp document
	 */
	private long timestamp;

	private DocumentFile() {
	}

	public Map<Integer, String> get() {
		return Collections.unmodifiableMap(stringPool);
	}

	public String getDocumentHash() {
		return documentHash;
	}

	public String getEncyptedInvariantStream() {
		return encryptedInvariantStream;
	}

	/**
	 * Gets first hash of the <code>DocumentFile</code> chain. This is used as a
	 * unique identifier for the document.
	 * 
	 * @return
	 */
	public String getGenesisHash() {
		return genesisHash;
	}

	public String getHashPreviousBlock() {
		return hashPreviousBlock;
	}

	public String getInvariantStream() {
		return invariantStream;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public List<Op> getOps() {
		return Collections.unmodifiableList(ops);
	}

	/**
	 * Gets an overlay at the specified index in the overlayPool
	 * 
	 * @param overlayIndex
	 * @return overlay at the specified index in the overlayPool
	 * @IllegalArgumentException if overlayIndex is negative
	 */

	public Overlay getOverlay(int overlayIndex) {
		return overlayPool.get(overlayIndex);
	}

	public Map<Integer, Overlay> getOverlayPool() {
		return Collections.unmodifiableMap(overlayPool);
	}

	public Set<Overlay> getOverlays(Set<Integer> indicies) {
		if (indicies == null) {
			throw new IllegalArgumentException("indicies is null");
		}
		Set<Overlay> overlays = new HashSet<>(indicies.size());
		for (int index : indicies) {
			overlays.add(getOverlay(index));
		}
		return overlays;
	}

	public String getString(int stringPoolIndex) {
		if (stringPoolIndex < 0) {
			throw new IllegalArgumentException("stringPoolIndex must be non-negative: " + stringPoolIndex);
		}
		return stringPool.get(stringPoolIndex);
	}

	protected BiMap<Integer, String> getStringPool() {
		return stringPool;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public boolean hasEncryptedInvariantStream() {
		return !Strings.isNullOrEmpty(encryptedInvariantStream);
	}

	public boolean hasGenesisHash() {
		return !Strings.isNullOrEmpty(genesisHash);
	}

	public boolean hasHashPreviousBlock() {
		return !Strings.isNullOrEmpty(hashPreviousBlock);
	}

	public boolean hasInvariantStream() {
		return !Strings.isNullOrEmpty(invariantStream);
	}

	public int operationCount() {
		return ops.size();
	}

	public int overlayPoolSize() {
		return overlayPool.size();
	}

	public int stringPoolSize() {
		return stringPool.size();
	}
}
