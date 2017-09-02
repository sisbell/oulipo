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
package org.oulipo.client.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.regex.Pattern;

import org.oulipo.client.services.TumblerService.TumblerSuccess;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.Link;
import org.oulipo.resources.model.Node;
import org.oulipo.resources.model.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.common.base.Strings;

/**
 * Routes a tumbler address to the corresponding <code>TumblerService</code>
 * call.
 */
public final class TedRouter {

	/**
	 * Maps objects to and from JSON
	 */
	private ObjectMapper mapper = new ObjectMapper();

	private Pattern pathPattern = Pattern.compile("(?!.*\\/).*");

	private TumblerService service;

	/**
	 * Constructs a router with the specified <code>TumblerService</code>
	 * 
	 * @param service
	 *            the backing tumbler service
	 */
	public TedRouter(TumblerService service) {
		this.service = service;
	}
	
	public TedRouter(DocuverseService docuverseService) {
		this(new TumblerService(docuverseService));
	}

	public void deleteRequest(String address, TumblerSuccess callback) {

	}

	public void postRequest(String address, String body, TumblerSuccess callback) {

	}

	/**
	 * Makes GET request to the specified tumbler
	 * 
	 * @param tumbler
	 *            the tumbler to make the request
	 * @param callback
	 *            the async callback
	 * @throws IOException
	 *             if there is I/O exception in making the network request
	 * @throws URISyntaxException
	 */
	public void routeGetRequest(TumblerAddress tumbler, TumblerSuccess callback)
			throws IOException, MalformedSpanException, URISyntaxException {
		Map<String, String> queryParams = tumbler.getQueryParams();
		if (!Strings.isNullOrEmpty(tumbler.getPath())) {
			String path = tumbler.getPath();
			if (path.equals("/nodes")) {
				if (tumbler.isNetworkTumbler()) {
					service.getNodes(tumbler.toTumblerAuthority(), queryParams, callback);
				} else {
					throw new IOException("Malformed request: " + path);
				}
			} else if (path.equals("/users")) {
				if (tumbler.isNodeTumbler()) {
					service.getUsers(tumbler.toTumblerAuthority(), queryParams, callback);
				} else {
					throw new IOException("Malformed request: " + path);
				}
			} else if (path.equals("/documents")) {
				if (tumbler.isUserTumbler()) {
					service.getDocuments(tumbler.toTumblerAuthority(), queryParams, callback);
				} else {
					throw new IOException("Malformed request: " + path);
				}
			} else if (path.equals("/links")) {
				if (tumbler.isDocumentTumbler()) {
					service.getLinks(tumbler.toTumblerAuthority(), queryParams, callback);
				} else {
					throw new IOException("Malformed request: " + path);
				}
			} else if (path.equals("/virtual")) {
				if (tumbler.isDocumentTumbler()) {
					service.getVirtual(tumbler.toTumblerAuthority(), queryParams, callback);
				} else {
					throw new IOException("Malformed request: " + path);
				}
			} else if (path.equals("/endsets")) {
				if (tumbler.isDocumentTumbler()) {
					service.getEndsets(tumbler.toTumblerAuthority(), callback);
				} else {
					throw new IOException("Malformed request: " + path);
				}
			}

		} else {
			if (tumbler.isNetworkTumbler()) {
				service.getNetwork(tumbler.toTumblerAuthority(), callback);
			} else if (tumbler.isNodeTumbler()) {
				if (tumbler.isSystemAddress()) {
					service.getSystemNodes(queryParams, callback);
				} else {
					service.getNode(tumbler.toTumblerAuthority(), callback);
				}
			} else if (tumbler.isUserTumbler()) {
				if (tumbler.isSystemAddress()) {
					service.getSystemUsers(queryParams, callback);
				} else {
					service.getUser(tumbler.toTumblerAuthority(), callback);
				}
			} else if (tumbler.isDocumentTumbler()) {
				if (tumbler.isSystemAddress()) {
					service.getSystemDocuments(queryParams, callback);
				} else {
					service.getDocument(tumbler.toTumblerAuthority(), callback);
				}
			} else if (tumbler.isElementTumbler()) {
				if (tumbler.isSystemAddress()) {
					if (tumbler.hasWidth()) {
						if (!tumbler.isBytesElement()) {
							throw new MalformedSpanException("Tumbler span currently only supports byte ranges: "
									+ tumbler.toTumblerAuthority());
						}
						service.getSystemVSpans(queryParams, callback);
					} else {
						service.getSystemLinks(queryParams, callback);
					}
				} else {
					if (tumbler.hasSpan()) {
						if (!tumbler.isBytesElement()) {
							throw new MalformedSpanException("Tumbler span currently only supports byte ranges: "
									+ tumbler.toTumblerAuthority());
						}
						service.getVSpan(tumbler, callback);
					} else {
						service.getLink(tumbler, callback);
					}
				}
			}
		}

	}

	/**
	 * Makes PUT request to the specified tumbler
	 * 
	 * @param tumbler
	 *            the tumbler to make the request
	 * @param body
	 *            the message body to post to the tumbler resource
	 * @param callback
	 *            the async callback
	 * @throws IOException
	 *             if there is I/O exception in making the network request
	 */
	public void routePutRequest(TumblerAddress tumbler, String body, TumblerSuccess callback) throws IOException {
		try {
			if (tumbler.isNodeTumbler()) {
				Node node = mapper.readValue(body, Node.class);
				node.resourceId = tumbler;
				service.createOrUpdateNode(node, callback);
			} else if (tumbler.isUserTumbler()) {
				User user = mapper.readValue(body, User.class);
				user.resourceId = tumbler;
				service.createOrUpdateUser(user, callback);
			} else if (tumbler.isDocumentTumbler()) {
				Document document = mapper.readValue(body, Document.class);
				document.resourceId = tumbler;
				service.createOrUpdateDocument(document, callback);
			} else if (tumbler.isElementTumbler() && tumbler.isLinkElement()) {
				Link link = mapper.readValue(body, Link.class);
				link.resourceId = tumbler;
				service.createOrUpdateLink(link, callback);
			}
		} catch (UnrecognizedPropertyException e) {
			if (tumbler.isNodeTumbler()) {
				throw new IOException("Not valid node: " + e.getMessage());
			} else if (tumbler.isUserTumbler()) {
				throw new IOException("Not valid user: " + e.getMessage());
			} else if (tumbler.isDocumentTumbler()) {
				throw new IOException("Not valid document: " + e.getMessage());
			} else if (tumbler.isElementTumbler() && tumbler.isLinkElement()) {
				throw new IOException("Not valid link: " + e.getMessage());
			}
		}
	}
}
