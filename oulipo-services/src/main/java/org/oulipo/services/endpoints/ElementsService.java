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

import static org.oulipo.services.VariantUtils.fromInvariantToVariant;
import static org.oulipo.services.VariantUtils.fromVariantToInvariant;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.net.TumblerField;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.ThingRepository;
import org.oulipo.resources.ThingRepository.SpanSource;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.InvariantLink;
import org.oulipo.resources.model.InvariantSpan;
import org.oulipo.resources.model.Link;
import org.oulipo.resources.model.Thing;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.UnauthorizedException;
import org.oulipo.services.MissingBodyException;
import org.oulipo.services.OulipoRequest;
import org.oulipo.services.ResourceSessionManager;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.impl.StreamOulipoMachine;
import org.oulipo.streams.types.SpanElement;

public class ElementsService {

	private final ResourceSessionManager sessionManager;

	private final StreamLoader<SpanElement> streamLoader;

	private final ThingRepository thingRepo;

	public ElementsService(ThingRepository thingRepo, ResourceSessionManager sessionManager,
			StreamLoader<SpanElement> streamLoader) {
		this.thingRepo = thingRepo;
		this.sessionManager = sessionManager;
		this.streamLoader = streamLoader;
	}

	/**
	 * Creates a link and all VSpans within that link
	 *
	 * @param objectMapper
	 * @param thingMapper
	 * @param sessionManager
	 * @return
	 * @return
	 * @throws AuthenticationException
	 * @throws ResourceNotFoundException
	 * @throws UnauthorizedException
	 * @throws IOException
	 * @throws MissingBodyException
	 * @throws MalformedSpanException
	 */
	public Link createOrUpdateLink(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException,
			ResourceNotFoundException, MissingBodyException, IOException, MalformedSpanException {

		oulipoRequest.authenticate();
		oulipoRequest.authorize();

		TumblerAddress documentAddress = oulipoRequest.getDocumentAddress();
		Document document = thingRepo.findDocument(documentAddress, "Please create document before any links");

		TumblerAddress linkAddress = oulipoRequest.getElementAddress();

		Link link = oulipoRequest.getLink();
		link.document = documentAddress;

		link.sequence = link.sequence();
		link.validateLink();
		link.removeDuplicates();

		InvariantLink invariantLink = removeLinkFromInvariantSpans(thingRepo.findInvariantLinkOpt(linkAddress));
		invariantLink.resourceId = oulipoRequest.getElementAddress();
		invariantLink.document = documentAddress;
		invariantLink.sequence = link.sequence;
		invariantLink.updatedDate = new Date();
		invariantLink.linkTypes = link.linkTypes;

		OulipoMachine<SpanElement> om = StreamOulipoMachine.create(streamLoader, documentAddress, true);

		invariantLink.fromInvariantSpans.clear();
		invariantLink.toInvariantSpans.clear();
		invariantLink.fromInvariantSpans.addAll(fromVariantToInvariant(link.fromVSpans, om));
		invariantLink.toInvariantSpans.addAll(fromVariantToInvariant(link.toVSpans, om));

		thingRepo.addInvariantSpans(invariantLink, invariantLink.fromInvariantSpans, SpanSource.FROM_LINK);
		thingRepo.addInvariantSpans(invariantLink, invariantLink.toInvariantSpans, SpanSource.TO_LINK);

		thingRepo.update(invariantLink);

		document.addLink((TumblerAddress) invariantLink.resourceId);
		document.removeDuplicateLinks();
		thingRepo.add(document);

		return link;

	}

	/**
	 * Returns either a link or a VSpan.
	 *
	 * @param mapper
	 * @param vSpanRepo
	 * @return
	 * @throws AuthenticationException
	 * @throws UnauthorizedException
	 * @throws ResourceNotFoundException
	 * @throws MalformedSpanException
	 * @throws IOException
	 */
	public Thing getElement(OulipoRequest oulipoRequest) throws ResourceNotFoundException, UnauthorizedException,
			AuthenticationException, IOException, MalformedSpanException {
		sessionManager.getDocumentForReadAccess(oulipoRequest);

		TumblerAddress address = oulipoRequest.getElementAddress();

		if (address.isLinkElement()) {
			Link link = new Link();
			link.resourceId = oulipoRequest.getElementAddress();
			link.document = oulipoRequest.getDocumentAddress();

			InvariantLink invariantLink = thingRepo.findInvariantLink(address, "Link not found");
			link.sequence = invariantLink.sequence;
			link.updatedDate = invariantLink.updatedDate;
			link.linkTypes = invariantLink.linkTypes;

			OulipoMachine<SpanElement> om = StreamOulipoMachine.create(streamLoader, oulipoRequest.getDocumentAddress(), false);

			link.fromVSpans.addAll(fromInvariantToVariant(invariantLink.fromInvariantSpans, om));
			link.toVSpans.addAll(fromInvariantToVariant(invariantLink.toInvariantSpans, om));

			return link;
		} else if (address.hasSpan()) {
			// TODO: convert ispan to vspan
			return thingRepo.findInvariantSpan(address);
		} else {
			return null;
		}
	}

	/**
	 * Returns either a link or a VSpan.
	 *
	 * @param mapper
	 * @param vSpanRepo
	 * @return
	 * @return
	 * @throws MalformedTumblerException
	 * @throws NumberFormatException
	 */
	public Collection<Thing> getSystemLinks(OulipoRequest oulipoRequest)
			throws NumberFormatException, MalformedTumblerException {
		Map<String, String> queryParams = oulipoRequest.queryParams();
		return thingRepo.getAllInvariantLinks(oulipoRequest.getNetworkIdAsInt(), queryParams);
	}

	/**
	 * Returns either a link or a VSpan.
	 *
	 * @param mapper
	 * @param vSpanRepo
	 * @return
	 * @return
	 * @throws MalformedTumblerException
	 * @throws NumberFormatException
	 */
	public Collection<Thing> getSystemVSpans(OulipoRequest oulipoRequest)
			throws NumberFormatException, MalformedTumblerException {
		Map<String, String> queryParams = oulipoRequest.queryParams();
		return thingRepo.getAllThings(oulipoRequest.getNetworkIdAsInt(), "InvariantSpan", queryParams);
	}

	public Link newLink(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException,
			MalformedTumblerException, ResourceNotFoundException {
		oulipoRequest.authenticate();
		oulipoRequest.authorize();

		// TODO: replace with sequence
		Random random = new Random();
		int sequence = random.nextInt(10000) + 1;
		TumblerField linkField = TumblerField.create("2." + sequence);

		Link link = new Link();
		link.resourceId = TumblerAddress
				.create(oulipoRequest.getDocumentAddress().toExternalForm() + ".0." + linkField.asString());
		link.createdDate = new Date();
		link.updatedDate = link.createdDate;
		link.document = oulipoRequest.getDocumentAddress();
		link.sequence = sequence;

		thingRepo.update(link);

		return link;

	}

	/**
	 * Detach this link from its current to/from invariant spans.
	 * 
	 * @param currentLinkOpt
	 * @return
	 * @throws MalformedTumblerException
	 * @throws ResourceNotFoundException
	 */
	private InvariantLink removeLinkFromInvariantSpans(Optional<InvariantLink> currentLinkOpt)
			throws MalformedTumblerException, ResourceNotFoundException {
		if (currentLinkOpt.isPresent()) {
			InvariantLink currentLink = currentLinkOpt.get();
			if (currentLink.fromInvariantSpans != null) {
				for (TumblerAddress ispanAddress : currentLink.fromInvariantSpans) {
					InvariantSpan ispan = thingRepo.findInvariantSpan(ispanAddress);
					if (ispan.removeFromLink(currentLink.resourceId)) {
						thingRepo.update(ispan);
					}
				}
			}

			if (currentLink.toInvariantSpans != null) {
				for (TumblerAddress ispanAddress : currentLink.toInvariantSpans) {
					InvariantSpan ispan = thingRepo.findInvariantSpan(ispanAddress);
					if (ispan.removeToLink(currentLink.resourceId)) {
						thingRepo.update(ispan);
					}
				}
			}
			return currentLink;
		} else {
			return new InvariantLink();
		}

	}
}
