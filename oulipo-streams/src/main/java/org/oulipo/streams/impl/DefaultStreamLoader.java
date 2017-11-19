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
package org.oulipo.streams.impl;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.List;

import org.oulipo.streams.InvariantStream;
import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.types.Invariant;
import org.oulipo.streams.types.OverlayStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * An implementation of StreamLoader that is backed by the file system for the
 * variant and invariant streams.
 */
public final class DefaultStreamLoader implements StreamLoader {

	/**
	 * Base directory where the streams are stored
	 */
	private File baseDir;

	/**
	 * Maps invariant spans to/from JSON format
	 */
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Variant stream in-memory cache (key = documentHash)
	 */
	private final LoadingCache<String, VariantStream<OverlayStream>> overlayCache;

	/**
	 * Variant stream in-memory cache (key = documentHash)
	 */
	private final LoadingCache<String, VariantStream<Invariant>> spanCache;

	/**
	 * Constructs a <code>StreamLoader</code> instance backed by the file system and
	 * in-memory cache.
	 * 
	 * @param baseDir
	 *            the base directory where the streams are stored
	 * @param spec
	 *            the spec for the cache. Contains parameters like the expiry time
	 *            and max size of the cache.
	 */
	public DefaultStreamLoader(File baseDir, String spec) {
		this.baseDir = baseDir;
		baseDir.mkdirs();
		spanCache = CacheBuilder.from(spec).removalListener(new RemovalListener<String, VariantStream<Invariant>>() {

			@Override
			public void onRemoval(RemovalNotification<String, VariantStream<Invariant>> notification) {
				try {
					persistInvariantElements(notification.getKey(), notification.getValue());
				} catch (IOException | MalformedSpanException e) {
					e.printStackTrace();
				}
			}
		}).build(new CacheLoader<String, VariantStream<Invariant>>() {
			@Override
			public VariantStream<Invariant> load(String key) throws IOException, MalformedSpanException {
				return openInvariantVariantStream(key);
			}
		});

		overlayCache = CacheBuilder.from(spec)
				.removalListener(new RemovalListener<String, VariantStream<OverlayStream>>() {

					@Override
					public void onRemoval(RemovalNotification<String, VariantStream<OverlayStream>> notification) {
						try {
							persistOverlayElements(notification.getKey(), notification.getValue());
						} catch (IOException | MalformedSpanException e) {
							e.printStackTrace();
						}
					}
				}).build(new CacheLoader<String, VariantStream<OverlayStream>>() {
					@Override
					public VariantStream<OverlayStream> load(String key) throws IOException, MalformedSpanException {
						return openOverlayVariantStream(key);
					}
				});

	}

	@Override
	public void flushVariantCache() {
		spanCache.invalidateAll();
	}

	@Override
	public String getHash() {
		return null;
	}

	@Override
	public InvariantStream openInvariantStream(String documentHash, Key key) throws IOException {
		return new FileInvariantStream(new File(baseDir, documentHash + "-invariant.txt"),
				new File(baseDir, documentHash + "-invariant-encrypted.txt"), documentHash, key);
	}

	@Override
	public VariantStream<Invariant> openInvariantVariantStream(String documentHash)
			throws IOException, MalformedSpanException {
		VariantStream<Invariant> stream = spanCache.getIfPresent(documentHash);
		if (stream != null) {
			return stream;
		}

		File file = new File(baseDir, documentHash + "-invariants.json");
		stream = new RopeVariantStream<Invariant>(documentHash);
		if (file.exists()) {
			List<Invariant> elements = mapper.readValue(file, new TypeReference<List<Invariant>>() {
			});
			stream.load(elements);
		}
		spanCache.put(documentHash, stream);

		return stream;
	}

	@Override
	public VariantStream<OverlayStream> openOverlayVariantStream(String documentHash)
			throws IOException, MalformedSpanException {
		VariantStream<OverlayStream> stream = overlayCache.getIfPresent(documentHash);
		if (stream != null) {
			return stream;
		}

		File file = new File(baseDir, documentHash + "-overlays.json");
		stream = new RopeVariantStream<OverlayStream>(documentHash);
		if (file.exists()) {
			List<OverlayStream> elements = mapper.readValue(file, new TypeReference<List<OverlayStream>>() {
			});
			stream.load(elements);
		}
		overlayCache.put(documentHash, stream);

		return stream;
	}

	private void persistInvariantElements(String documentHash, VariantStream<Invariant> stream)
			throws IOException, MalformedSpanException {
		File file = new File(baseDir, documentHash + "-invariants.json");
		mapper.writeValue(file, stream.getStreamElements());
	}

	private void persistOverlayElements(String documentHash, VariantStream<OverlayStream> stream)
			throws IOException, MalformedSpanException {
		File file = new File(baseDir, documentHash + "-overlays.json");
		mapper.writeValue(file, stream.getStreamElements());
	}

	@Override
	public void setHash(String hash) {

	}
}
