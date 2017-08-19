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

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.oulipo.machine.server.RdfRepository;
import org.oulipo.machine.server.RequestMapper;
import org.oulipo.machine.server.XanSessionManager;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.InvariantLink;
import org.oulipo.resources.model.InvariantSpan;
import org.oulipo.resources.model.Thing;
import org.oulipo.resources.responses.EndsetByType;

import spark.Route;

/**
 * Groups from and to VSpans by link type
 */
public final class EndsetsApi {

	private static final TumblerAddress BLANK_LINK_TYPE =null;// TumblerAddress.create(
		//	"ted://1.0.1.0.1.0.1.0.2.628");

	public static Route getEndsets(RdfRepository thingRepo,
			XanSessionManager sessionManager, RequestMapper requestMapper) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			sessionManager.checkReadAccessOfDocument(request);

		//	VariantStream vStream = null;
			TumblerAddress documentAddress = requestMapper
					.createDocumentAddress(request);

			Collection<Thing> ispans = thingRepo
					.findEndsetsOfDoc(documentAddress);

			EndsetByType endset = new EndsetByType();
			for (Thing thing : ispans) {
				InvariantSpan ispan = (InvariantSpan) thing;
				if (ispan.fromLinks != null) {
					for (TumblerAddress fromAddress : ispan.fromLinks) {
						Optional<InvariantLink> optLink = thingRepo
								.findInvariantLink(fromAddress);
						if (!optLink.isPresent()) {
							continue;
						}
						InvariantLink link = optLink.get();
						if (link.fromInvariantSpans != null) {
							if (link.linkTypes != null) {
								for (TumblerAddress linkType : link.linkTypes) {
									endset.addFrom(
											linkType,
											filter(link.fromInvariantSpans,
													documentAddress));
								}
							} else {
								endset.addFrom(
										BLANK_LINK_TYPE,
										filter(link.fromInvariantSpans, documentAddress));
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
									endset.addTo(
											linkType,
											filter(link.toInvariantSpans,
													documentAddress));
								}
							} else {
								endset.addTo(BLANK_LINK_TYPE,
										filter(link.toInvariantSpans, documentAddress));
							}
						}
					}
				}
			}
			//TODO: replace ispans with vspans
			return endset;
		};
	}

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
}
