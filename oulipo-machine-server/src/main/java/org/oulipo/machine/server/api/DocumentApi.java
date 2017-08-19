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
package org.oulipo.machine.server.api;

import static spark.Spark.halt;

import java.util.Map;
import java.util.Optional;

import org.oulipo.machine.server.RdfMapper;
import org.oulipo.machine.server.RdfRepository;
import org.oulipo.machine.server.RequestMapper;
import org.oulipo.machine.server.XanSessionManager;
import org.oulipo.net.IRI;
import org.oulipo.net.TumblerAddress;
import org.oulipo.net.TumblerField;
import org.oulipo.net.TumblerMatcher;
import org.oulipo.net.matchers.AddressTumblerMatcher;
import org.oulipo.net.matchers.RangeTumblerMatcher;
import org.oulipo.resources.ResourceErrorCodes;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.InvariantLink;
import org.oulipo.resources.model.Virtual;
import org.oulipo.resources.responses.ErrorResponseDto;
import org.oulipo.resources.responses.LinkAddresses;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.impl.StreamOulipoMachine;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Route;

public class DocumentApi {

	public static Route createDocument(RequestMapper requestMapper,
			RdfRepository thingRepo, XanSessionManager sessionManager) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			sessionManager.authenticateSession(request);
			sessionManager.authorizeResource(request);

			TumblerAddress documentAddress = requestMapper
					.createDocumentAddress(request);

			Document document = requestMapper.readDocument(request,
					documentAddress);
			TumblerField docField = documentAddress.getDocument();

			document.user = requestMapper.createUserAddress(request);
			document.majorVersion = docField.get(0);
			document.minorVersion = docField.get(1);
			document.revision = docField.get(2);
			// old document contains links

			document.removeDuplicates();
			thingRepo.update(document);
			return document;
		};
	}

	public static Route getDocumentLinks(RdfRepository thingRepo,
			XanSessionManager sessionManager, RequestMapper requestMapper) {
		return (request, response) -> {
			response.header("content-type", "application/json");

			int network = Integer.parseInt(request.params(":networkId"));
			TumblerAddress documentAddress = requestMapper
					.createDocumentAddress(request);
			Map<String, String> queryParams = RdfMapper
					.getQueryMapFrom(request);
			Document document = sessionManager
					.checkReadAccessOfDocument(request);

			//TODO: These are Variant Spans, convert to invariant for matching
			//This may result in more ISpans than VSpans
			String[] toParams = queryParams.get("to") != null ? queryParams
					.get("to").split("[,]") : null;
			String[] fromParams = queryParams.get("from") != null ? queryParams
					.get("from").split("[,]") : null;

			if (toParams != null || fromParams != null) {
				String[] typeParams = queryParams.get("type") != null ? queryParams
						.get("type").split("[,]") : null;

				RangeTumblerMatcher fromMatcher = RangeTumblerMatcher
						.createFromInvariantSpans(toParams);
				RangeTumblerMatcher toMatcher = RangeTumblerMatcher
						.createFromInvariantSpans(fromParams);
				AddressTumblerMatcher linkTypeMatcher = AddressTumblerMatcher
						.createLinkTypeMatcher(typeParams);

				TumblerAddress[] linkTumblers = document.links;
				LinkAddresses linkAddresses = new LinkAddresses();

				for (TumblerAddress linkTumbler : linkTumblers) {
					Optional<InvariantLink> optLink = thingRepo.findInvariantLink(linkTumbler);
					if (!optLink.isPresent()) {
						continue;
					}
					InvariantLink link = optLink.get();

					if (link.fromInvariantSpans != null
							&& !matchIt(link.fromInvariantSpans, fromMatcher)) {
						continue;
					}

					if (link.toInvariantSpans != null
							&& !matchIt(link.toInvariantSpans, toMatcher)) {
						continue;
					}

					if (link.linkTypes != null
							&& !matchIt(link.linkTypes, linkTypeMatcher)) {
						continue;
					}

					linkAddresses.links.add(link.resourceId);
				}

				return linkAddresses;
			}

			queryParams.put("document", documentAddress.toTumblerAuthority());
			//TODO: Convert Invariant Links to Links
			return thingRepo.getAllInvariantLinks(network, queryParams);
		};
	}

	public static boolean matchIt(TumblerAddress[] tumblers,
			TumblerMatcher matcher) {
		for (TumblerAddress tumbler : tumblers) {
			if (matcher.match(tumbler)) {
				return true;
			}
		}
		return false;
	}

	public static Route getSystemDocuments(RdfRepository thingRepo,
			XanSessionManager sessionManager) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return thingRepo.getAllDocuments(request);
		};
	}

	public static Route getDocument(XanSessionManager sessionManager) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return sessionManager.checkReadAccessOfDocument(request);
		};
	}

	public static Route newVersion(ObjectMapper mapper,
			RdfRepository thingRepo, XanSessionManager sessionManager, RequestMapper requestMapper) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			sessionManager.authenticateSession(request);
			sessionManager.authorizeResource(request);

			TumblerAddress documentAddress = requestMapper
					.createDocumentAddress(request);
			Document newDocument = thingRepo.findDocument(documentAddress)
					.newVersion();

			if (thingRepo.findDocumentOpt(
					(TumblerAddress) newDocument.resourceId).isPresent()) {
				ErrorResponseDto resp = new ErrorResponseDto(
						ResourceErrorCodes.DOCUMENT_ALREADY_EXISTS,
						"Document already exists: "
								+ newDocument.resourceId.value, documentAddress);
				return halt(400, mapper.writeValueAsString(resp));
			}

			thingRepo.add(newDocument);
			return newDocument;
		};
	}

	public static Route getVirtual(RdfRepository thingRepo,
			XanSessionManager sessionManager, StreamLoader streamLoader) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			Document document = sessionManager
					.checkReadAccessOfDocument(request);
			TumblerAddress documentAddress = (TumblerAddress) document.resourceId;
			OulipoMachine om = StreamOulipoMachine.create(streamLoader,
					documentAddress, true);

			Virtual virtual = new Virtual();
			virtual.resourceId = new IRI("ted://1.1.0.1.0.1.1.1.0.2.5");
			virtual.links = document.links;
			virtual.content =  om.getVirtualContent();

			return virtual;
		};
	}

}
