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
package org.oulipo.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.Link;
import org.oulipo.resources.model.Node;
import org.oulipo.resources.model.User;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.UnauthorizedException;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantSpans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

public class OulipoRequest {

	private final String body;

	private TumblerAddress documentAddress;

	private final String documentId;

	private TumblerAddress elementAddress;

	private final String elementId;

	private Map<String, String> headers;

	private TumblerAddress networkAddress;

	private final String networkId;

	private TumblerAddress nodeAddress;

	private final String nodeId;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private String publicKey;

	private ResourceSessionManager sessionManager;

	private final String spans;
	
	private String token;

	private TumblerAddress userAddress;

	private final String userId;

	public OulipoRequest(ResourceSessionManager sessionManager, Map<String, String> headers, Map<String, String> params, String body) {
		this.sessionManager = sessionManager;
		this.body = body;
		this.headers = headers;
		this.networkId = params.get(":networkId");
		this.nodeId = params.get(":nodeId");
		this.userId = params.get(":userId");
		this.documentId = params.get(":docId");
		this.elementId = params.get(":elementId");
		this.spans = params.get(":spans");
		if(headers != null) {
			this.publicKey = headers.get("x-oulipo-user");
			this.token = headers.get("x-oulipo-token");	
		}
	}
	
	public void authenticate() throws AuthenticationException {
		sessionManager.authenticateSession(this);
	}
	public void authorize() throws UnauthorizedException, MalformedTumblerException, ResourceNotFoundException {
		sessionManager.authorizeResource(this);
	}
	
	public Document getDocument() throws IOException, MissingBodyException {
		if (!hasBody()) {
			throw new MissingBodyException(getDocumentAddress(),
					"Unable to create document. Please add JSON body to request");
		}

		Document document = objectMapper.readValue(body, Document.class);
		document.resourceId = getDocumentAddress();
		return document;
	}

	public TumblerAddress getDocumentAddress() throws MalformedTumblerException {
		if (documentAddress == null) {
			documentAddress = new TumblerAddress.Builder("ted", networkId).node(nodeId).user(userId)
					.document(documentId).build();

			if (documentAddress.getDocument().size() != 3) {
				throw new MalformedTumblerException(
						"Document tumbler field only supports 3 elements x.x.x . Try recreating document");
			}
		}
		return documentAddress;
	}

	public String getDocumentId() {
		return documentId;
	}

	public TumblerAddress getElementAddress() throws MalformedTumblerException {
		if (elementAddress == null) {
			elementAddress = new TumblerAddress.Builder("ted", networkId).node(nodeId).user(userId).document(documentId)
					.element(elementId).build();
		}
		return elementAddress;
	}

	public String getElementId() {
		return elementId;
	}

	public Link getLink() throws IOException, MissingBodyException {
		if (!hasBody()) {
			throw new MissingBodyException(getElementAddress(),
					"Unable to create link. Please add JSON body to request");
		}

		Link link = objectMapper.readValue(body, Link.class);
		link.resourceId = getElementAddress();
		return link;
	}

	public TumblerAddress getNetworkAddress() throws MalformedTumblerException {
		if (networkAddress == null) {
			elementAddress = new TumblerAddress.Builder("ted", networkId).build();
		}
		return elementAddress;
	}

	public String getNetworkId() {
		return networkId;
	}

	public Node getNode() throws IOException, MissingBodyException {
		if (!hasBody()) {
			throw new MissingBodyException(getNodeAddress(), "Unable to create node. Please add JSON body to request");
		}

		Node node = objectMapper.readValue(body, Node.class);
		node.resourceId = getNodeAddress();
		return node;
	}

	public TumblerAddress getNodeAddress() throws MalformedTumblerException {
		if (nodeAddress == null) {
			nodeAddress = new TumblerAddress.Builder("ted", networkId).node(nodeId).build();
		}
		return nodeAddress;
	}

	public String getNodeId() {
		return nodeId;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public String getSpans() {
		return spans;
	}

	public String getToken() {
		return token;
	}

	public User getUser() throws IOException, MissingBodyException {
		if (!hasBody()) {
			throw new MissingBodyException(getUserAddress(), "Unable to create user. Please add JSON body to request");
		}

		User user = objectMapper.readValue(body, User.class);
		user.resourceId = getUserAddress();
		user.rootId = getUserAddress().getUser().get(0);
		return user;
	}

	public TumblerAddress getUserAddress() throws MalformedTumblerException {
		if (userAddress == null) {
			userAddress = new TumblerAddress.Builder("ted", networkId).node(nodeId).user(userId).build();
		}
		return userAddress;
	}

	public String getUserId() {
		return userId;
	}

	public List<VariantSpan> getVariantSpans() throws IOException, MissingBodyException {
		if (!hasBody()) {
			throw new MissingBodyException(getElementAddress(),
					"Unable to read variant spans. Please add JSON body to request");
		}

		VariantSpans spans = objectMapper.readValue(body, VariantSpans.class);
		return spans.spans;
	}

	public boolean hasBody() {
		return !Strings.isNullOrEmpty(body);
	}

	public Map<String, String> queryParams() {
		return null;
	}

}
