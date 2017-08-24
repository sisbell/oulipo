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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.ThingRepository;
import org.oulipo.resources.model.InvariantLink;
import org.oulipo.resources.model.InvariantSpan;
import org.oulipo.resources.model.Thing;
import org.oulipo.services.OulipoRequest;
import org.oulipo.services.ResourceSessionManager;
import org.oulipo.services.responses.EndsetByType;

/**
 * Groups from and to VSpans by link type
 */
public class EndsetsService {

	private static final TumblerAddress BLANK_LINK_TYPE =null;// TumblerAddress.create(
	//	"ted://1.0.1.0.1.0.1.0.2.628");

	private static TumblerAddress[] filter(TumblerAddress[] ispans, TumblerAddress address) {
		Set<TumblerAddress> set = new HashSet<>();
		for (TumblerAddress ispan : ispans) {
			if (ispan.value.startsWith(address.value)) {
				/*
				VariantStream vs = null;
				for(org.oulipo.machine.server.editor.VariantSpan vspan : 
					vs.getVariantSpansOf(new org.oulipo.machine.server.editor.InvariantSpan(ispan.spanStart(), ispan.spanWidth())) {
					set.add(vspan.toTumbler(address));
				}
				*/
			}
		}
		return set.toArray(new TumblerAddress[set.size()]);
	}

	private final ResourceSessionManager sessionManager;

	private final ThingRepository thingRepo;

	public EndsetsService(ThingRepository thingRepo, ResourceSessionManager sessionManager) {
		this.thingRepo = thingRepo;
		this.sessionManager = sessionManager;
	}
	
	public EndsetByType getEndsets(OulipoRequest oulipoRequest) throws Exception {
		sessionManager.getDocumentForReadAccess(oulipoRequest);

		TumblerAddress documentAddress = oulipoRequest.getDocumentAddress();

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
					if (link.fromInvariantSpans != null) {
						if (link.linkTypes != null) {
							for (TumblerAddress linkType : link.linkTypes) {
								endset.addFrom(linkType, filter(link.fromInvariantSpans, documentAddress));
							}
						} else {
							endset.addFrom(BLANK_LINK_TYPE, filter(link.fromInvariantSpans, documentAddress));
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
					if (link.toInvariantSpans != null) {
						if (link.linkTypes != null) {
							for (TumblerAddress linkType : link.linkTypes) {
								endset.addTo(linkType, filter(link.toInvariantSpans, documentAddress));
							}
						} else {
							endset.addTo(BLANK_LINK_TYPE, filter(link.toInvariantSpans, documentAddress));
						}
					}
				}
			}
		}
		// TODO: replace ispans with vspans
		return endset;

	}

}
