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

import static org.oulipo.services.VariantUtils.fromVariantToInvariant;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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
import org.oulipo.services.VariantUtils;
import org.oulipo.services.responses.LinkAddresses;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.impl.StreamOulipoMachine;

public class DocumentService {

	private static boolean matchIt(List<TumblerAddress> tumblers, TumblerMatcher matcher) {
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

	public Document getDocument(OulipoRequest oulipoRequest) throws MalformedTumblerException,
			ResourceNotFoundException, UnauthorizedException, AuthenticationException {
		return sessionManager.getDocumentForReadAccess(oulipoRequest);
	}

	/**
	 * Gets all link addresses to and from this document. If no to/from parameters
	 * are specified, this method returns all link addresses
	 * 
	 * @param oulipoRequest
	 * @return
	 * @throws AuthenticationException
	 * @throws UnauthorizedException
	 * @throws ResourceNotFoundException
	 * @throws MalformedSpanException
	 * @throws IOException
	 */
	public LinkAddresses getDocumentLinks(OulipoRequest oulipoRequest) throws ResourceNotFoundException,
			UnauthorizedException, AuthenticationException, IOException, MalformedSpanException {

		int network = Integer.parseInt(oulipoRequest.getNetworkId());
		TumblerAddress documentAddress = oulipoRequest.getDocumentAddress();
		Document document = sessionManager.getDocumentForReadAccess(oulipoRequest);

		OulipoMachine om = StreamOulipoMachine.create(streamLoader, oulipoRequest.getDocumentAddress(), false);

		Map<String, String> queryParams = oulipoRequest.queryParams();

		List<TumblerAddress> toParams = queryParams.get("to") != null
				? fromVariantToInvariant(queryParams.get("to").split("[,]"), om)
				: null;

		List<TumblerAddress> fromParams = queryParams.get("from") != null
				? VariantUtils.fromVariantToInvariant(queryParams.get("from").split("[,]"), om)
				: null;

		LinkAddresses linkAddresses = new LinkAddresses();

		if (toParams != null || fromParams != null) {
			String[] typeParams = queryParams.get("type") != null ? queryParams.get("type").split("[,]") : null;

			RangeTumblerMatcher fromMatcher = new RangeTumblerMatcher(new HashSet<>(toParams));
			RangeTumblerMatcher toMatcher = new RangeTumblerMatcher(new HashSet<>(fromParams));
			AddressTumblerMatcher linkTypeMatcher = AddressTumblerMatcher.createLinkTypeMatcher(typeParams);

			TumblerAddress[] linkTumblers = document.links;

			for (TumblerAddress linkTumbler : linkTumblers) {
				Optional<InvariantLink> optLink = thingRepo.findInvariantLink(linkTumbler);
				if (!optLink.isPresent()) {
					continue;
				}
				InvariantLink link = optLink.get();

				if (link.fromInvariantSpans != null && !matchIt(link.fromInvariantSpans, fromMatcher)) {
					continue;
				}

				if (link.toInvariantSpans != null && !matchIt(link.toInvariantSpans, toMatcher)) {
					continue;
				}

				if (link.linkTypes != null && !matchIt(link.linkTypes, linkTypeMatcher)) {
					continue;
				}
				linkAddresses.links.addAll(VariantUtils.fromInvariantToVariant(link.resourceId, om));
			}

			return linkAddresses;
		}

		queryParams.put("document", documentAddress.toTumblerAuthority());

		Collection<Thing> invariantLinks = thingRepo.getAllInvariantLinks(network, queryParams);
		for (Thing thing : invariantLinks) {
			InvariantLink ilink = (InvariantLink) thing;
			linkAddresses.links.addAll(VariantUtils.fromInvariantToVariant(ilink.resourceId, om));
		}
		return linkAddresses;
	}

	public Collection<Thing> getSystemDocuments(OulipoRequest oulipoRequest)
			throws NumberFormatException, MalformedTumblerException {
		int network = oulipoRequest.getNetworkIdAsInt();

		Map<String, String> queryParams = oulipoRequest.queryParams();
		return thingRepo.getAllDocuments(network, queryParams);
	}

	/**
	 * Gets a list of text partitions and invariant addresses of that text for a
	 * document
	 * 
	 * @param oulipoRequest
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws UnauthorizedException
	 * @throws AuthenticationException
	 * @throws IOException
	 * @throws MalformedSpanException
	 */
	public Virtual getVirtual(OulipoRequest oulipoRequest) throws ResourceNotFoundException, UnauthorizedException,
			AuthenticationException, IOException, MalformedSpanException {

		Document document = sessionManager.getDocumentForReadAccess(oulipoRequest);
		TumblerAddress documentAddress = (TumblerAddress) document.resourceId;
		OulipoMachine om = StreamOulipoMachine.create(streamLoader, documentAddress, true);

		Virtual virtual = new Virtual();
		virtual.resourceId = documentAddress;
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
