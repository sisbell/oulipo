package org.oulipo.services.endpoints;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.oulipo.net.IRI;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
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

public class ElementsService {

	private final ResourceSessionManager sessionManager;

	private final ThingRepository thingRepo;

	public ElementsService(ThingRepository thingRepo, ResourceSessionManager sessionManager) {
		this.thingRepo = thingRepo;
		this.sessionManager = sessionManager;
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
	 * @throws MalformedTumblerException
	 */
	public Thing getElement(OulipoRequest oulipoRequest) throws MalformedTumblerException, ResourceNotFoundException,
			UnauthorizedException, AuthenticationException {
		sessionManager.getDocumentForReadAccess(oulipoRequest);

		TumblerAddress address = oulipoRequest.getElementAddress();

		if (address.isLinkElement()) {
			// TODO: all ispans in link need to be translated back to vspans
			return thingRepo.findInvariantLink(address, "Link not found");
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
	public Collection<Thing> getSystemLinks(OulipoRequest oulipoRequest) throws NumberFormatException, MalformedTumblerException {
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
	 */
	public Link createOrUpdateLink(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException, ResourceNotFoundException, MissingBodyException, IOException {
		
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
						// TODO: should save InvariantSpans
						InvariantSpan is = thingRepo.findInvariantSpan(ispan);
						if (is.removeToLink(currentLink.resourceId)) {
							thingRepo.update(is);
						}
					}
				}
			}

			// TODO: convert from link to invariant link
			thingRepo.update(link);// TODO: update InvariantLink

			// TODO: Convert VSpans to ISpans
			InvariantLink invariantLink = null;
			for (TumblerAddress vspan : link.fromVSpans) {
				List<IRI> ispan = lookup(vspan);
			}
			thingRepo.addInvariantSpans(invariantLink, invariantLink.fromInvariantSpans, SpanSource.FROM_LINK);
			thingRepo.addInvariantSpans(invariantLink, invariantLink.toInvariantSpans, SpanSource.TO_LINK);

			document.addLink((TumblerAddress) invariantLink.resourceId);
			document.removeDuplicateLinks();
			thingRepo.add(document);

			return link;

	}

	private static List<IRI> lookup(TumblerAddress vspan) {
		// TODO: ispans aren't tumblers (IRI?)
		// goto document of vspan. Document will have mapping of VSPan -> ISpan
		// pull out ISpan(s) and add to IRI list - (these do not have to be in RDF
		// store)
		// Repeat for the document of each VSPan
		return null;
	}
}
