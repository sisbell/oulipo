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
import java.util.List;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.InvariantStream;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.types.Invariant;
import org.oulipo.streams.types.Overlay;

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
	 * Variant stream in-memory cache
	 */
	private final LoadingCache<TumblerAddress, VariantStream<Overlay>> overlayCache;

	/**
	 * Variant stream in-memory cache
	 */
	private final LoadingCache<TumblerAddress, VariantStream<Invariant>> spanCache;

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
		spanCache = CacheBuilder.from(spec)
				.removalListener(new RemovalListener<TumblerAddress, VariantStream<Invariant>>() {

					@Override
					public void onRemoval(RemovalNotification<TumblerAddress, VariantStream<Invariant>> notification) {
						try {
							persistInvariantElements(notification.getKey(), notification.getValue());
						} catch (IOException | MalformedSpanException e) {
							e.printStackTrace();
						}
					}
				}).build(new CacheLoader<TumblerAddress, VariantStream<Invariant>>() {
					@Override
					public VariantStream<Invariant> load(TumblerAddress key)
							throws IOException, MalformedSpanException {
						return openInvariantVariantStream(key);
					}
				});

		overlayCache = CacheBuilder.from(spec)
				.removalListener(new RemovalListener<TumblerAddress, VariantStream<Overlay>>() {

					@Override
					public void onRemoval(RemovalNotification<TumblerAddress, VariantStream<Overlay>> notification) {
						try {
							persistOverlayElements(notification.getKey(), notification.getValue());
						} catch (IOException | MalformedSpanException e) {
							e.printStackTrace();
						}
					}
				}).build(new CacheLoader<TumblerAddress, VariantStream<Overlay>>() {
					@Override
					public VariantStream<Overlay> load(TumblerAddress key) throws IOException, MalformedSpanException {
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
	public InvariantStream openInvariantStream(TumblerAddress tumbler) throws IOException {
		return new FileInvariantStream(
				new File(baseDir, tumbler.userVal() + ".0." + tumbler.documentVal() + "-invariant.txt"), tumbler);
	}

	@Override
	public VariantStream<Invariant> openInvariantVariantStream(TumblerAddress tumbler)
			throws IOException, MalformedSpanException {
		tumbler = tumbler.getDocumentAddress();
		VariantStream<Invariant> stream = spanCache.getIfPresent(tumbler);
		if (stream != null) {
			return stream;
		}

		File file = new File(baseDir, tumbler.userVal() + ".0." + tumbler.documentVal() + "-invariants.json");
		stream = new RopeVariantStream<Invariant>(tumbler);
		if (file.exists()) {
			List<Invariant> elements = mapper.readValue(file, new TypeReference<List<Invariant>>() {
			});
			stream.load(elements);
		}
		spanCache.put(tumbler, stream);

		return stream;
	}

	@Override
	public VariantStream<Overlay> openOverlayVariantStream(TumblerAddress tumbler)
			throws IOException, MalformedSpanException {
		tumbler = tumbler.getDocumentAddress();
		VariantStream<Overlay> stream = overlayCache.getIfPresent(tumbler);
		if (stream != null) {
			return stream;
		}

		File file = new File(baseDir, tumbler.userVal() + ".0." + tumbler.documentVal() + "-overlays.json");
		stream = new RopeVariantStream<Overlay>(tumbler);
		if (file.exists()) {
			List<Overlay> elements = mapper.readValue(file, new TypeReference<List<Overlay>>() {
			});
			stream.load(elements);
		}
		overlayCache.put(tumbler, stream);

		return stream;
	}

	private void persistInvariantElements(TumblerAddress tumbler, VariantStream<Invariant> stream)
			throws IOException, MalformedSpanException {
		File file = new File(baseDir, tumbler.userVal() + ".0." + tumbler.documentVal() + "-invariants.json");
		mapper.writeValue(file, stream.getStreamElements());
	}

	private void persistOverlayElements(TumblerAddress tumbler, VariantStream<Overlay> stream)
			throws IOException, MalformedSpanException {
		File file = new File(baseDir, tumbler.userVal() + ".0." + tumbler.documentVal() + "-overlays.json");
		mapper.writeValue(file, stream.getStreamElements());
	}

	@Override
	public void setHash(String hash) {

	}

}
