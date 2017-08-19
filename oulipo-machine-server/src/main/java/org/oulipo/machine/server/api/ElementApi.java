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

import java.util.List;
import java.util.Optional;

import org.oulipo.machine.server.RdfRepository;
import org.oulipo.machine.server.RequestMapper;
import org.oulipo.machine.server.XanSessionManager;
import org.oulipo.net.IRI;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.SpanSource;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.InvariantLink;
import org.oulipo.resources.model.InvariantSpan;
import org.oulipo.resources.model.Link;

import spark.Route;

public class ElementApi {

	/**
	 * Returns either a link or a VSpan.
	 *
	 * @param mapper
	 * @param vSpanRepo
	 * @return
	 */
	public static Route getElement(RdfRepository thingRepo,
			RequestMapper requestMapper, XanSessionManager sessionManager) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			sessionManager.checkReadAccessOfDocument(request);

			TumblerAddress address = requestMapper
					.createElementAddress(request);
			if (address.isLinkElement()) {
				//TODO: all ispans in link need to be translated back to vspans
				return thingRepo.findInvariantLink(address, "Link not found");
			} else if (address.hasSpan()) {
				//TODO: convert ispan to vspan
				return thingRepo.findInvariantSpan(address);
			} else {
				return null;
			}
		};
	}

	/**
	 * Returns either a link or a VSpan.
	 *
	 * @param mapper
	 * @param vSpanRepo
	 * @return
	 */
	public static Route getSystemLinks(RdfRepository thingRepo) {
		return (request, response) -> {
			response.header("content-type", "application/json");

			return thingRepo.getAllInvariantLinks(request);

		};
	}

	/**
	 * Returns either a link or a VSpan.
	 *
	 * @param mapper
	 * @param vSpanRepo
	 * @return
	 */
	public static Route getSystemVSpans(RdfRepository thingRepo) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return thingRepo.getAllInvariantSpans(request);
		};
	}


	
	/**
	 * Creates a link and all VSpans within that link
	 *
	 * @param objectMapper
	 * @param thingMapper
	 * @param sessionManager
	 * @return
	 */
	public static Route createOrUpdateLink(RequestMapper requestMapper,
			RdfRepository thingRepo, XanSessionManager sessionManager) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			sessionManager.authenticateSession(request);
			sessionManager.authorizeResource(request);

			TumblerAddress documentAddress = requestMapper
					.createDocumentAddress(request);
			Document document = thingRepo.findDocument(documentAddress,
					"Please create document before any links");

			TumblerAddress linkAddress = requestMapper
					.createElementAddress(request);

			Link link = requestMapper.readLink(request, linkAddress);
			link.document = documentAddress;
			link.sequence = link.sequence();
			link.validateLink();
			link.removeDuplicates();

			Optional<InvariantLink> currentLinkOpt = thingRepo.findInvariantLinkOpt(linkAddress);
			if (currentLinkOpt.isPresent()) {
				InvariantLink currentLink = currentLinkOpt.get();
				if (currentLink.fromInvariantSpans != null) {
					for (TumblerAddress ispan : currentLink.fromInvariantSpans) {
						InvariantSpan is = thingRepo.findInvariantSpan(ispan);
						if (is.removeFromLink(currentLink.resourceId)) {
							thingRepo.update(is);
						}
					}
				}

				if (currentLink.toInvariantSpans != null) {
					for (TumblerAddress ispan : currentLink.toInvariantSpans) {
						//TODO: should save InvariantSpans
						InvariantSpan is = thingRepo.findInvariantSpan(ispan);
						if (is.removeToLink(currentLink.resourceId)) {
							thingRepo.update(is);
						}
					}
				}
			}

			//TODO: convert from link to invariant link
			thingRepo.update(link);//TODO: update InvariantLink

			//TODO: Convert VSpans to ISpans 
			InvariantLink invariantLink = null;
			for(TumblerAddress vspan : link.fromVSpans) {
				List<IRI> ispan = lookup(vspan);
			}
			thingRepo.addInvariantSpans(invariantLink, invariantLink.fromInvariantSpans, SpanSource.FROM_LINK);
			thingRepo.addInvariantSpans(invariantLink, invariantLink.toInvariantSpans, SpanSource.TO_LINK);

			document.addLink((TumblerAddress) invariantLink.resourceId);
			document.removeDuplicateLinks();
			thingRepo.add(document);

			return link;
		};
	}
	
	private static List<IRI> lookup(TumblerAddress vspan) {
		//TODO: ispans aren't tumblers (IRI?)
		//goto document of vspan. Document will have mapping of VSPan -> ISpan
		//pull out ISpan(s) and add to IRI list - (these do not have to be in RDF store)
		//Repeat for the document of each VSPan
		return null;
	}
}
