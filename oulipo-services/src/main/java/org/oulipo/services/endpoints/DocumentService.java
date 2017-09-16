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
package org.oulipo.services.endpoints;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.oulipo.net.IRI;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.net.TumblerField;
import org.oulipo.net.TumblerMatcher;
import org.oulipo.net.matchers.AddressTumblerMatcher;
import org.oulipo.net.matchers.RangeTumblerMatcher;
import org.oulipo.resources.ResourceFoundException;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.ThingRepository;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.InvariantLink;
import org.oulipo.resources.model.Thing;
import org.oulipo.resources.model.Virtual;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.UnauthorizedException;
import org.oulipo.services.MissingBodyException;
import org.oulipo.services.OulipoRequest;
import org.oulipo.services.ResourceSessionManager;
import org.oulipo.services.responses.LinkAddresses;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.impl.StreamOulipoMachine;

public class DocumentService {

	private static boolean matchIt(TumblerAddress[] tumblers, TumblerMatcher matcher) {
		for (TumblerAddress tumbler : tumblers) {
			if (matcher.match(tumbler)) {
				return true;
			}
		}
		return false;
	}

	private final ResourceSessionManager sessionManager;

	private final StreamLoader streamLoader;

	private final ThingRepository thingRepo;

	public DocumentService(ThingRepository thingRepo, ResourceSessionManager sessionManager,
			StreamLoader streamLoader) {
		this.thingRepo = thingRepo;
		this.sessionManager = sessionManager;
		this.streamLoader = streamLoader;
	}

	public Document createDocument(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException,
			ResourceNotFoundException, MissingBodyException, IOException {

		oulipoRequest.authenticate();
		oulipoRequest.authorize();

		TumblerAddress documentAddress = oulipoRequest.getDocumentAddress();

		Document document = oulipoRequest.getDocument();
		TumblerField docField = documentAddress.getDocument();

		document.user = oulipoRequest.getUserAddress();
		document.majorVersion = docField.get(0);
		document.minorVersion = docField.get(1);
		document.revision = docField.get(2);
		// old document contains links

		document.removeDuplicates();
		thingRepo.update(document);
		return document;
	}
	// TODO: Fix
	/*
	 * public Collection<Thing> getDocumentLinks(OulipoRequest oulipoRequest) {
	 * 
	 * int network = Integer.parseInt(oulipoRequest.getNetworkId()); TumblerAddress
	 * documentAddress = oulipoRequest.getDocumentAddress(); Document document =
	 * sessionManager.getDocumentForReadAccess(oulipoRequest);
	 * 
	 * Map<String, String> queryParams = oulipoRequest.queryParams(); // TODO: These
	 * are Variant Spans, convert to invariant for matching // This may result in
	 * more ISpans than VSpans String[] toParams = queryParams.get("to") != null ?
	 * queryParams.get("to").split("[,]") : null; String[] fromParams =
	 * queryParams.get("from") != null ? queryParams.get("from").split("[,]") :
	 * null;
	 * 
	 * if (toParams != null || fromParams != null) { String[] typeParams =
	 * queryParams.get("type") != null ? queryParams.get("type").split("[,]") :
	 * null;
	 * 
	 * RangeTumblerMatcher fromMatcher =
	 * RangeTumblerMatcher.createFromInvariantSpans(toParams); RangeTumblerMatcher
	 * toMatcher = RangeTumblerMatcher.createFromInvariantSpans(fromParams);
	 * AddressTumblerMatcher linkTypeMatcher =
	 * AddressTumblerMatcher.createLinkTypeMatcher(typeParams);
	 * 
	 * TumblerAddress[] linkTumblers = document.links; LinkAddresses linkAddresses =
	 * new LinkAddresses();
	 * 
	 * for (TumblerAddress linkTumbler : linkTumblers) { Optional<InvariantLink>
	 * optLink = thingRepo.findInvariantLink(linkTumbler); if (!optLink.isPresent())
	 * { continue; } InvariantLink link = optLink.get();
	 * 
	 * if (link.fromInvariantSpans != null && !matchIt(link.fromInvariantSpans,
	 * fromMatcher)) { continue; }
	 * 
	 * if (link.toInvariantSpans != null && !matchIt(link.toInvariantSpans,
	 * toMatcher)) { continue; }
	 * 
	 * if (link.linkTypes != null && !matchIt(link.linkTypes, linkTypeMatcher)) {
	 * continue; }
	 * 
	 * linkAddresses.links.add(link.resourceId); }
	 * 
	 * return linkAddresses; }
	 * 
	 * queryParams.put("document", documentAddress.toTumblerAuthority()); // TODO:
	 * Convert Invariant Links to Links return
	 * thingRepo.getAllInvariantLinks(network, queryParams); }
	 */

	public Document getDocument(OulipoRequest oulipoRequest) throws MalformedTumblerException,
			ResourceNotFoundException, UnauthorizedException, AuthenticationException {
		return sessionManager.getDocumentForReadAccess(oulipoRequest);
	}

	public Collection<Thing> getSystemDocuments(OulipoRequest oulipoRequest)
			throws NumberFormatException, MalformedTumblerException {
		int network = oulipoRequest.getNetworkIdAsInt();

		Map<String, String> queryParams = oulipoRequest.queryParams();
		return thingRepo.getAllDocuments(network, queryParams);
	}

	public Virtual getVirtual(OulipoRequest oulipoRequest) throws ResourceNotFoundException, UnauthorizedException,
			AuthenticationException, IOException, MalformedSpanException {

		Document document = sessionManager.getDocumentForReadAccess(oulipoRequest);
		TumblerAddress documentAddress = (TumblerAddress) document.resourceId;
		OulipoMachine om = StreamOulipoMachine.create(streamLoader, documentAddress, true);

		Virtual virtual = new Virtual();
		virtual.resourceId = new IRI("ted://1.1.0.1.0.1.1.1.0.2.5");
		virtual.links = document.links;
		virtual.content = om.getVirtualContent();

		return virtual;

	}

	public Document newDocument(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException,
			ResourceNotFoundException, MissingBodyException, IOException {

		oulipoRequest.authenticate();
		oulipoRequest.authorize();

		// Generate Random number and then verify not exists - TODO: replace with
		// sequence
		Random random = new Random();
		TumblerField docField = TumblerField.create(random.nextInt(10000) + 1 + ".1.1");

		Document document = new Document();
		document.isPublic = true;// TODO: user configurable
		document.resourceId = TumblerAddress
				.create(oulipoRequest.getUserAddress().toExternalForm() + ".0." + docField.asString());

		document.user = oulipoRequest.getUserAddress();
		document.majorVersion = docField.get(0);
		document.minorVersion = docField.get(1);
		document.revision = docField.get(2);

		thingRepo.update(document);
		return document;
	}

	public Document newVersion(OulipoRequest oulipoRequest) throws MalformedTumblerException, ResourceNotFoundException,
			AuthenticationException, UnauthorizedException, ResourceFoundException {

		oulipoRequest.authenticate();
		oulipoRequest.authorize();

		TumblerAddress documentAddress = oulipoRequest.getDocumentAddress();
		Document newDocument = thingRepo.findDocument(documentAddress).newVersion();

		if (thingRepo.findDocumentOpt((TumblerAddress) newDocument.resourceId).isPresent()) {
			throw new ResourceFoundException(documentAddress,
					"Document already exists: " + newDocument.resourceId.value);
		}

		thingRepo.add(newDocument);
		return newDocument;

	}

}
