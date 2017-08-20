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
package org.oulipo.machine.server;

import java.io.IOException;
import java.util.List;

import org.oulipo.machine.server.exceptions.JsonMissingException;
import org.oulipo.net.IRI;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.Link;
import org.oulipo.resources.model.Node;
import org.oulipo.resources.model.User;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantSpans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import spark.Request;

/**
 * Maps Spark requests to things
 */
public final class RequestMapper {

	private ObjectMapper objectMapper;

	public RequestMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public TumblerAddress createDocumentAddress(Request request)
			throws MalformedTumblerException {
		String networkId = request.params(":networkId");
		String nodeId = request.params(":nodeId");
		String userId = request.params(":userId");
		String docId = request.params(":docId");

		TumblerAddress documentAddress = new TumblerAddress.Builder("ted",
				networkId).node(nodeId).user(userId).document(docId).build();

		if (documentAddress.getDocument().size() != 3) {
			throw new MalformedTumblerException(
					"Document tumbler field only supports 3 elements x.x.x . Try recreating document");
		}
		return documentAddress;
	}

	public TumblerAddress createUserAddress(Request request)
			throws MalformedTumblerException {
		String networkId = request.params(":networkId");
		String nodeId = request.params(":nodeId");
		String userId = request.params(":userId");

		return new TumblerAddress.Builder("ted", networkId).node(nodeId)
				.user(userId).build();
	}

	public TumblerAddress createNodeAddress(Request request)
			throws MalformedTumblerException {
		String networkId = request.params(":networkId");
		String nodeId = request.params(":nodeId");

		return new TumblerAddress.Builder("ted", networkId).node(nodeId)
				.build();
	}

	public TumblerAddress createNetworkAddress(Request request)
			throws MalformedTumblerException {
		String networkId = request.params(":networkId");

		return new TumblerAddress.Builder("ted", networkId).build();
	}

	public TumblerAddress createElementAddress(Request request)
			throws MalformedTumblerException {
		String networkId = request.params(":networkId");
		String nodeId = request.params(":nodeId");
		String userId = request.params(":userId");
		String docId = request.params(":docId");
		String elementId = request.params(":elementId");

		return new TumblerAddress.Builder("ted", networkId).node(nodeId).user(userId).document(docId).element(elementId)
				.build();
	}

	public IRI readElement(Request request) {
		String networkId = request.params(":networkId");
		String nodeId = request.params(":nodeId");
		String userId = request.params(":userId");
		String docId = request.params(":docId");
		String elementId = request.params(":elementId");
		return new IRI("ted://" + networkId + "." +  nodeId + ".0." + userId
				+ ".0." + docId + ".0." + elementId);
	}

	public Document readDocument(Request request, TumblerAddress documentAddress)
			throws IOException, JsonMissingException {
		if (Strings.isNullOrEmpty(request.body())) {
			throw new JsonMissingException(documentAddress,
					"Unable to create document. Please add JSON body to request");
		}

		Document document = objectMapper.readValue(request.body(),
				Document.class);
		document.resourceId = documentAddress;
		return document;
	}

	public List<VariantSpan> readVariantSpans(Request request, TumblerAddress elementAddress)
			throws IOException, JsonMissingException {
		if (Strings.isNullOrEmpty(request.body())) {
			throw new JsonMissingException(elementAddress,
					"Unable to read variant spans. Please add JSON body to request");
		}

		VariantSpans spans = objectMapper.readValue(request.body(), VariantSpans.class);
		return spans.spans;
	}
	
	public Link readLink(Request request, TumblerAddress linkAddress)
			throws IOException, JsonMissingException {
		if (Strings.isNullOrEmpty(request.body())) {
			throw new JsonMissingException(linkAddress,
					"Unable to create link. Please add JSON body to request");
		}

		Link link = objectMapper.readValue(request.body(), Link.class);
		link.resourceId = linkAddress;
		return link;
	}

	public Node readNode(Request request, TumblerAddress nodeAddress)
			throws IOException, JsonMissingException {
		if (Strings.isNullOrEmpty(request.body())) {
			throw new JsonMissingException(nodeAddress,
					"Unable to create node. Please add JSON body to request");
		}

		Node node = objectMapper.readValue(request.body(), Node.class);
		node.resourceId = nodeAddress;
		return node;
	}

	public User readUser(Request request, TumblerAddress userAddress)
			throws IOException, JsonMissingException {
		if (Strings.isNullOrEmpty(request.body())) {
			throw new JsonMissingException(userAddress,
					"Unable to create user. Please add JSON body to request");
		}

		User user = objectMapper.readValue(request.body(), User.class);
		user.resourceId = userAddress;
		user.rootId = userAddress.getUser().get(0);
		// TODO: validate pk?? request.pk = user.pk
		return user;
	}
}
