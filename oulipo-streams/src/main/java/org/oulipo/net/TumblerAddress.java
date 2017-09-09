/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.  
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
package org.oulipo.net;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.oulipo.streams.serializers.TumblerAddressDeserializer;
import org.oulipo.streams.serializers.TumblerAddressSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

/**
 * Address for documents in the docuverse. More precisely it is a linear
 * ordering of a tree that maps to specific documents and elements within the
 * universe of all documents
 *
 * The field ordering is
 * {scheme}://{network}.{node}.0.{user}.0.{document}.0.{element}
 *
 * Network with a value of 1 is a private network. It can be used to designate
 * local addresses in the docuverse or an internal private network. Network with
 * a value of 2 is the public, global network.
 *
 * Having a private/public network field, means we don't have to have reserved
 * address spaces for local and private networks.
 *
 * Node is equivalent to a server address
 */
@JsonSerialize(using = TumblerAddressSerializer.class)
@JsonDeserialize(using = TumblerAddressDeserializer.class)
public final class TumblerAddress extends IRI {

	public static class Builder {

		private TumblerField document;

		private TumblerField element;

		/**
		 * If global is 1, then it is the public address space, otherwise it is private
		 */
		private TumblerField network;

		/**
		 * Server address
		 */
		private TumblerField node;

		private String path;

		private Map<String, String> queryParams;

		/**
		 * Protocol used. There are initially only two protocols: 1) ted for document
		 * delivery and 2) xanauth for authentication
		 */
		private String scheme;

		/**
		 * User id. It may be broken down into additional elements to handle
		 * organizations, sub-organizations, groups, all the way down to individual
		 * people
		 */
		private TumblerField user;

		private TumblerField width;

		/**
		 * Default builder instance. It automatically sets the tumbler address to ted
		 * scheme on private network
		 */
		public Builder() {
			this.scheme = "ted";
			this.network = createMainNet();
		}

		public Builder(String scheme, String network) throws MalformedTumblerException {
			this(scheme, TumblerField.create(network));
		}

		public Builder(String scheme, TumblerField network) throws MalformedTumblerException {
			if (Strings.isNullOrEmpty(scheme)) {
				throw new MalformedTumblerException("Scheme must be specified");
			}

			if (network == null) {
				throw new MalformedTumblerException("Network must be specified");
			}

			this.scheme = scheme;
			this.network = network;
		}

		public Builder(String scheme, TumblerField network, TumblerField node) throws MalformedTumblerException {
			this(scheme, network);
			node(node);
		}

		public Builder(String scheme, TumblerField network, TumblerField node, TumblerField user)
				throws MalformedTumblerException {
			this(scheme, network, node);
			user(user);
		}

		public Builder(String scheme, TumblerField network, TumblerField node, TumblerField user, TumblerField document)
				throws MalformedTumblerException {
			this(scheme, network, node, user);
			document(document);
		}

		public Builder(String scheme, TumblerField network, TumblerField node, TumblerField user, TumblerField document,
				TumblerField element) throws MalformedTumblerException {
			this(scheme, network, node, user, document);
			element(element);
		}

		/**
		 * Creates copy of tumbler for this builder
		 *
		 * @param tumblerAddress
		 * @throws MalformedTumblerException
		 */
		public Builder(TumblerAddress tumblerAddress) throws MalformedTumblerException {
			if (tumblerAddress == null) {
				throw new IllegalArgumentException("tumblerAddress is null");
			}
			this.scheme = tumblerAddress.scheme;
			this.network = tumblerAddress.network;
			node(tumblerAddress.node);
			user(tumblerAddress.user);
			document(tumblerAddress.document);
			element(tumblerAddress.element);
		}

		public TumblerAddress build() throws MalformedTumblerException {
			if (user != null && node == null) {
				throw new MalformedTumblerException("Missing tumbler segment: node");
			} else if (document != null && user == null) {
				throw new MalformedTumblerException("Missing tumbler segment: user");
			} else if (element != null && document == null) {
				throw new MalformedTumblerException("Missing tumbler segment: document");
			}

			TumblerAddress address = new TumblerAddress(scheme, network, node, user, document, element);
			address.queryParams = queryParams;
			address.path = path;
			address.width = width;
			return address;
		}

		public Builder document(String document) throws MalformedTumblerException {
			return document(TumblerField.create(document));
		}

		public Builder document(TumblerField document) {
			if (document == null) {
				return this;
			}
			this.document = document;
			return this;
		}

		public Builder element(String element) throws MalformedTumblerException {
			return element(TumblerField.create(element));
		}

		/**
		 * Element meta can't have any additional elements. This is always just a single
		 * value of 1
		 *
		 * Overlays only have an additional sequence defined. So 3.1.2, would be an
		 * overlay at position 2.
		 *
		 * Element bytes has three components. 2.1.50 would be element starting at
		 * position 1, with a length of 50 bytes
		 *
		 * A link starts with the number 3. 3.2.3 is overlay. 3.1.2 is jump link
		 * 3.3.4.1.100 is transclusion from 1 to 100.
		 *
		 * This method will throw a MalformedTumblerException if the element is an
		 * unrecognized type
		 *
		 * @param element
		 * @return
		 * @throws MalformedTumblerException
		 */
		public Builder element(TumblerField element) throws MalformedTumblerException {
			if (element == null || element.size() == 0) {
				return this;
			}

			int elementType = element.get(0);
			if (elementType == ELEMENT_BYTES) {
				if (element.size() != 2) {
					throw new MalformedTumblerException("Bytes element must have only start defined");
				}
			} else if (elementType == ELEMENT_LINK) {
				if (element.size() < 2) {
					throw new MalformedTumblerException("Links requires sequence index");
				}
			} else {
				throw new MalformedTumblerException("Unrecognized element type: " + elementType);
			}

			this.element = element;
			return this;
		}

		public Builder node(String node) throws MalformedTumblerException {
			return node(TumblerField.create(node));
		}

		public Builder node(TumblerField node) {
			if (node == null) {
				return this;
			}
			this.node = node;
			return this;
		}

		public Builder path(String path) {
			this.path = path;
			return this;
		}

		public Builder queryParams(Map<String, String> queryParams) {
			this.queryParams = queryParams;
			return this;
		}

		public Builder user(String user) throws MalformedTumblerException {
			return user(TumblerField.create(user));
		}

		public Builder user(TumblerField user) {
			if (user == null) {
				return this;
			}
			this.user = user;
			return this;
		}

		public Builder width(long width) throws MalformedTumblerException {
			return width(String.valueOf(width));
		}

		public Builder width(String width) throws MalformedTumblerException {
			return width(TumblerField.create(width));
		}

		public Builder width(TumblerField width) {
			this.width = width;
			return this;
		}
	}

	public static final int ELEMENT_BYTES = 1;

	public static final int ELEMENT_LINK = 2;

	private static String addressAuthority(String address) {
		String[] query = address.split("[?]");
		if (query.length > 1) {
			return query[0];
		}
		return address;

	}

	/**
	 * Parses the specified string address and returns a TumblerAddress
	 *
	 * @param addressAuthority
	 * @return
	 * @throws MalformedTumblerException
	 */
	/**
	 * @param address
	 * @return
	 * @throws MalformedTumblerException
	 */
	public static TumblerAddress create(String address) throws MalformedTumblerException {
		if (Strings.isNullOrEmpty(address)) {
			throw new MalformedTumblerException("Tumbler is null");
		}

		URI tumblerURI;
		TumblerField widthSpan = null;
		try {
			String addressAuthority = addressAuthority(address);
			String[] width = addressAuthority.split("[~]");
			if (width.length > 1) {
				addressAuthority = width[0];
				widthSpan = TumblerField.create(width[1]);
			}
			String[] tokens = addressAuthority.split("://");
			tumblerURI = tokens.length == 1 ? new URI("ted://" + addressAuthority) : new URI(addressAuthority);
		} catch (URISyntaxException e) {
			throw new MalformedTumblerException(e.getMessage());
		}

		String[] fields = tumblerURI.getAuthority().split("\\.0\\.");

		int firstDot = fields[0].indexOf(".");

		Builder builder = new Builder(tumblerURI.getScheme(),
				firstDot == -1 ? fields[0] : fields[0].substring(0, firstDot));
		builder.path(tumblerURI.getPath());
		builder.queryParams(queryMap(address));
		builder.width(widthSpan);

		int len = fields.length;
		if (firstDot != -1) {
			builder.node(fields[0].substring(firstDot + 1));
			if (len > 1) {
				builder.user(fields[1]);
				if (len > 2) {
					builder.document(fields[2]);
					if (len > 3) {
						builder.element(fields[3]);
					}
				}
			}
		}
		return builder.build();
	}

	public static TumblerField createMainNet() {
		try {
			return TumblerField.create("1");
		} catch (MalformedTumblerException e) {
		}
		return null;// never happen
	}

	private static Map<String, String> queryMap(String address) {
		String[] query = address.split("[?]");
		if (query.length > 1) {
			return Strings.isNullOrEmpty(query[1]) ? new HashMap<>()
					: new HashMap<>(Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query[1]));
		}
		return new HashMap<>();

	}

	public static void validate(String address) throws MalformedTumblerException {
		create(address);
	}

	private TumblerField document;

	private TumblerField element;

	private TumblerField network;

	private TumblerField node;

	private String path;

	private Map<String, String> queryParams;

	private Path resourcePath;

	private String scheme;

	private TumblerField user;

	private TumblerField width;

	private TumblerAddress() {
	}

	private TumblerAddress(String scheme, TumblerField network, TumblerField node, TumblerField user,
			TumblerField document, TumblerField element) throws MalformedTumblerException {
		this.scheme = scheme;
		this.network = network;
		this.user = user;
		this.document = document;
		this.node = node;
		this.element = element;
		this.value = toTumblerAuthority();
	}

	public String documentVal() throws MalformedTumblerException {
		return document.asString();
	}

	public TumblerField getDocument() {
		return document;
	}

	/**
	 * Returns a copy of this address that only includes up to document field
	 *
	 * @return
	 * @throws MalformedTumblerException
	 */
	public TumblerAddress getDocumentAddress() throws MalformedTumblerException {
		return new TumblerAddress.Builder(scheme, network, node, user, document).build();
	}

	/**
	 * Returns element of a document. Will be null if this tumbler address not
	 * reference an element
	 *
	 * @return
	 */
	public TumblerField getElement() {
		return element;
	}

	public TumblerField getNetwork() {
		return network;
	}

	public TumblerField getNode() {
		return node;
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	public Path getResourcePath() {
		return resourcePath;
	}

	public String getScheme() {
		return scheme;
	}

	public TumblerField getUser() {
		return user;
	}

	public boolean hasDocument() {
		return document != null;
	}

	public boolean hasElement() {
		return element != null;
	}

	public boolean hasNetwork() {
		return network != null;
	}

	public boolean hasNode() {
		return node != null;
	}

	public boolean hasSpan() {
		return width != null;
	}

	public boolean hasUser() {
		return user != null;
	}

	public boolean hasWidth() {
		return width != null;
	}

	public boolean isBytesElement() {
		return hasElement() && element.get(0) == ELEMENT_BYTES;
	}

	public boolean isDocumentTumbler() {
		return hasDocument() && !hasElement();
	}

	public boolean isElementTumbler() {
		return hasElement();
	}

	public boolean isLinkElement() {
		return hasElement() && element.get(0) == ELEMENT_LINK;
	}

	public boolean isMainNet() {
		return network.get(0) == 1;
	}

	public boolean isNetworkTumbler() {
		return hasNetwork() && !hasNode();
	}

	public boolean isNodeTumbler() {
		return hasNode() && !hasUser();
	}

	public boolean isSystemAddress() {
		if (hasNetwork()) {
			if (!network.isSystemField()) {
				return false;
			}
		}
		if (hasNode()) {
			if (!node.isSystemField()) {
				return false;
			}
		}

		if (hasUser()) {
			if (!user.isSystemField()) {
				return false;
			}
		}

		if (hasDocument()) {
			if (!document.isSystemField()) {
				return false;
			}
		}

		return true;
	}

	public boolean isTestNet() {
		return network.get(0) == 2;
	}

	public boolean isUserTumbler() {
		return hasUser() && !hasDocument();
	}

	public String networkVal() throws MalformedTumblerException {
		return network.asString();
	}

	public String nodeVal() throws MalformedTumblerException {
		return node.asString();
	}

	public void setResourcePath(Path resourcePath) {
		this.resourcePath = resourcePath;
	}

	public int spanStart() {
		return hasSpan() ? element.get(1) : -1;
	}

	public int spanWidth() {
		return hasSpan() ? width.get(1) : -1;
	}

	public String toExternalForm() throws MalformedTumblerException {
		String authority = toTumblerAuthority();
		return !Strings.isNullOrEmpty(path) ? authority + path : authority;
	}

	public IRI toIRI() throws MalformedTumblerException {
		return new IRI(toTumblerAuthority());
	}

	@Override
	public String toString() {
		return "TumblerAddress [document=" + document + ", element=" + element + ", network=" + network + ", node="
				+ node + ", resourcePath=" + resourcePath + ", scheme=" + scheme + ", user=" + user + "]";
	}

	public String toTumblerAuthority() throws MalformedTumblerException {
		String delim = ".0.";
		StringBuilder sb = new StringBuilder();
		sb.append(scheme).append("://").append(network.asString());
		if (hasNode()) {
			sb.append(".").append(node.asString());
			if (hasUser()) {
				sb.append(delim).append(user.asString());

				if (hasDocument()) {
					sb.append(delim).append(document.asString());
					if (hasElement()) {
						sb.append(delim).append(element.asString());
					}
				}
			}
		}
		return sb.toString();
	}

	public String userVal() throws MalformedTumblerException {
		return user.asString();
	}

}
