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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.ThingRepository;
import org.oulipo.resources.model.InvariantLink;
import org.oulipo.resources.model.InvariantSpan;
import org.oulipo.resources.model.Thing;
import org.oulipo.services.OulipoRequest;
import org.oulipo.services.ResourceSessionManager;
import org.oulipo.services.responses.Endset;
import org.oulipo.services.responses.EndsetByType;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.impl.StreamOulipoMachine;
import org.oulipo.streams.types.SpanElement;

/**
 * Groups from and to VSpans by link type
 */
public class EndsetsService {

	private static final TumblerAddress BLANK_LINK_TYPE = TumblerAddress
			.createWithNoException("ted://1.1.0.1.0.1.1.1.0.2.628");

	private final ResourceSessionManager sessionManager;

	private StreamLoader<SpanElement> streamLoader;

	private final ThingRepository thingRepo;

	public EndsetsService(ThingRepository thingRepo, ResourceSessionManager sessionManager, StreamLoader<SpanElement> streamLoader) {
		this.thingRepo = thingRepo;
		this.sessionManager = sessionManager;
		this.streamLoader = streamLoader;
	}

	private TumblerAddress[] filter(List<TumblerAddress> ispans, TumblerAddress address, OulipoMachine<SpanElement> om)
			throws MalformedSpanException, MalformedTumblerException {
		Set<TumblerAddress> set = new HashSet<>();
		for (TumblerAddress ispanAddress : ispans) {
			if (ispanAddress.value.startsWith(address.value)) {
				SpanElement invariantSpan = new SpanElement(
						ispanAddress.spanStart(), ispanAddress.spanWidth(), ispanAddress);
				List<VariantSpan> vspans = om.getVariantSpans(invariantSpan);
				for (VariantSpan vspan : vspans) {
					TumblerAddress variantSpanAddress = TumblerAddress
							.create(vspan.homeDocument + ".0.1." + vspan.start + "~1." + vspan.width);
					set.add(variantSpanAddress);
				}
			}
		}
		return set.toArray(new TumblerAddress[set.size()]);
	}

	public EndsetByType getEndsets(OulipoRequest oulipoRequest) throws Exception {
		sessionManager.getDocumentForReadAccess(oulipoRequest);

		TumblerAddress documentAddress = oulipoRequest.getDocumentAddress();
		OulipoMachine<SpanElement> om = StreamOulipoMachine.create(streamLoader, documentAddress, true);

		Collection<Thing> ispans = thingRepo.findEndsetsOfDoc(documentAddress);

		EndsetByType endset = new EndsetByType();
		for (Thing thing : ispans) {
			InvariantSpan ispan = (InvariantSpan) thing;
			if (ispan.fromLinks != null) {
				for (TumblerAddress fromAddress : ispan.fromLinks) {
					Optional<InvariantLink> optLink = thingRepo.findInvariantLink(fromAddress);
					if (!optLink.isPresent()) {
						continue;
					}
					InvariantLink link = optLink.get();
					if (!link.fromInvariantSpans.isEmpty()) {
						if (!link.linkTypes.isEmpty()) {
							for (TumblerAddress linkType : link.linkTypes) {
								endset.addFrom(linkType, filter(link.fromInvariantSpans, documentAddress, om));
							}
						} else {
							endset.addFrom(BLANK_LINK_TYPE, filter(link.fromInvariantSpans, documentAddress, om));
						}
					}
				}
			}
			if (ispan.toLinks != null) {
				for (TumblerAddress toAddress : ispan.toLinks) {
					Optional<InvariantLink> optLink = thingRepo.findInvariantLink(toAddress);
					if (!optLink.isPresent()) {
						continue;
					}
					InvariantLink link = optLink.get();
					if (!link.toInvariantSpans.isEmpty()) {
						if (!link.linkTypes.isEmpty()) {
							for (TumblerAddress linkType : link.linkTypes) {
								endset.addTo(linkType, filter(link.toInvariantSpans, documentAddress, om));
							}
						} else {
							endset.addTo(BLANK_LINK_TYPE, filter(link.toInvariantSpans, documentAddress, om));
						}
					}
				}
			}
		}
		return endset;
	}

	public EndsetByType putEndsets(OulipoRequest oulipoRequest) throws Exception {
		// EndsetByType endset = null;
		HashMap<String, Endset> endsets = null;

		// List<String> linkAddresses = new ArrayList<>();
		for (Map.Entry<String, Endset> entry : endsets.entrySet()) {
			Endset endset = entry.getValue();
			for (TumblerAddress type : endset.types) {

			}
		}
		return null;
	}
}
