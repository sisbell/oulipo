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
 * Network with a value of 2 is a test network. It can be used to designate
 * local addresses in the docuverse or an internal private network. Network with
 * a value of 1 is the public, global network.
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

			TumblerAddress address = new TumblerAddress(scheme, network, node, user, document, element, width);
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
			} else if (elementType == ELEMENT_INVARIANT_BYTES) {
				if (element.size() != 2) {
					throw new MalformedTumblerException("Bytes element must have only start defined");
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

		@Override
		public String toString() {
			return "Builder [document=" + document + ", element=" + element + ", network=" + network + ", node=" + node
					+ ", path=" + path + ", queryParams=" + queryParams + ", scheme=" + scheme + ", user=" + user
					+ ", width=" + width + "]";
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
			try {
				return width(TumblerField.create(width));
			} catch (MalformedTumblerException e) {
				throw new MalformedTumblerException(e.getMessage() + " : " + toString());
			}
		}

		public Builder width(TumblerField width) {
			this.width = width;
			return this;
		}

	}

	public static final String A_SYSTEM_BASE = "1.1.0.1.0.1.1.1.0.";

	public static final TumblerAddress BOLD = TumblerAddress
			.createWithNoException(A_SYSTEM_BASE + TumblerField.BOLD.asStringNoException());

	private static final int ELEMENT_BYTES = 1;

	private static final int ELEMENT_INVARIANT_BYTES = 3;

	private static final int ELEMENT_LINK = 2;

	public static final TumblerAddress FONT_FAMILY_SERIF = TumblerAddress
			.createWithNoException(A_SYSTEM_BASE + TumblerField.FONT_FAMILY_SERIF.asStringNoException());

	public static final TumblerAddress FONT_SIZE_12 = TumblerAddress
			.createWithNoException(A_SYSTEM_BASE + TumblerField.FONT_SIZE_12.asStringNoException());

	public static final TumblerAddress FONT_SIZE_14 = TumblerAddress
			.createWithNoException(A_SYSTEM_BASE + TumblerField.FONT_SIZE_14.asStringNoException());

	public static final TumblerAddress FONT_SIZE_16 = TumblerAddress
			.createWithNoException(A_SYSTEM_BASE + TumblerField.FONT_SIZE_16.asStringNoException());

	public static final TumblerAddress ITALIC = TumblerAddress
			.createWithNoException(A_SYSTEM_BASE + TumblerField.ITALIC.asStringNoException());

	public static final TumblerAddress STRIKE_THROUGH = TumblerAddress
			.createWithNoException(A_SYSTEM_BASE + TumblerField.STRIKE_THROUGH.asStringNoException());

	public static final TumblerAddress UNDERLINE = TumblerAddress
			.createWithNoException(A_SYSTEM_BASE + TumblerField.UNDERLINE.asStringNoException());

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
				try {
					widthSpan = TumblerField.create(width[1]);
				} catch (MalformedTumblerException e) {
					throw new MalformedTumblerException(e.getMessage() + ":" + address);
				}
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

	public static TumblerAddress createBackgroundColor(int red, int green, int blue) {
		return TumblerAddress.createWithNoException("1.1.0.1.0.1.1.1.0.2.104." + red + "." + green + "." + blue);
	}

	public static TumblerField createMainNet() {
		try {
			return TumblerField.create("1");
		} catch (MalformedTumblerException e) {
		}
		return null;// never happen
	}

	public static TumblerAddress createTextColor(int red, int green, int blue) {
		return TumblerAddress.createWithNoException("1.1.0.1.0.1.1.1.0.2.103." + red + "." + green + "." + blue);
	}

	public static TumblerAddress createWithNoException(String address) {
		try {
			return create(address);
		} catch (MalformedTumblerException e) {
			return null;
		}
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
			TumblerField document, TumblerField element, TumblerField width) throws MalformedTumblerException {
		this.scheme = scheme;
		this.network = network;
		this.user = user;
		this.document = document;
		this.node = node;
		this.element = element;
		this.width = width;
		this.value = toExternalForm();
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
		if (queryParams == null) {
			queryParams = new HashMap<>();
		}
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

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public int spanStart() {
		return hasSpan() ? element.get(1) : -1;
	}

	public int spanWidth() {
		return hasSpan() ? width.get(1) : -1;
	}

	public String toExternalForm() throws MalformedTumblerException {
		String authority = toTumblerAuthority();
		if (hasSpan()) {
			authority += "~1." + spanWidth();
		}
		return !Strings.isNullOrEmpty(path) ? authority + path : authority;
	}

	public IRI toIRI() throws MalformedTumblerException {
		return new IRI(toTumblerAuthority());
	}

	@Override
	public String toString() {
		return "TumblerAddress [address = " + value  + ", document=" + document + ", element=" + element + ", network=" + network + ", node="
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

	public String toTumblerFields() throws MalformedTumblerException {
		String delim = ".0.";
		StringBuilder sb = new StringBuilder();
		sb.append(network.asString());
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
