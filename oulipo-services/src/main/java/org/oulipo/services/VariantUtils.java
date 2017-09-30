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
package org.oulipo.services;

import java.util.ArrayList;
import java.util.List;

import org.oulipo.net.IRI;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.VariantSpan;

public final class VariantUtils {

	public static List<TumblerAddress> fromInvariantToVariant(IRI iri, OulipoMachine om)
			throws MalformedTumblerException, MalformedSpanException {
		return fromInvariantToVariant(TumblerAddress.create(iri.value), om);
	}

	public static List<TumblerAddress> fromInvariantToVariant(List<TumblerAddress> invariantSpans, OulipoMachine om)
			throws MalformedSpanException, MalformedTumblerException {
		List<TumblerAddress> variantSpans = new ArrayList<>();
		for (TumblerAddress ispanAddress : invariantSpans) {
			variantSpans.addAll(fromInvariantToVariant(ispanAddress, om));
		}
		return variantSpans;
	}

	public static List<TumblerAddress> fromInvariantToVariant(TumblerAddress ispanAddress, OulipoMachine om)
			throws MalformedTumblerException, MalformedSpanException {
		List<TumblerAddress> variantSpans = new ArrayList<>();
		org.oulipo.streams.InvariantSpan invariantSpan = new org.oulipo.streams.InvariantSpan(ispanAddress.spanStart(),
				ispanAddress.spanWidth());
		List<VariantSpan> vspans = om.getVariantSpans(invariantSpan);
		for (VariantSpan vspan : vspans) {
			TumblerAddress variantSpanAddress = TumblerAddress
					.create(vspan.homeDocument + ".0.1." + vspan.start + "~1." + vspan.width);
			variantSpans.add(variantSpanAddress);
		}
		return variantSpans;
	}

	public static TumblerAddress fromVariantToInvariant(IRI iri) throws MalformedTumblerException {
		return fromVariantToInvariant(TumblerAddress.create(iri.value));
	}

	public static List<TumblerAddress> fromVariantToInvariant(List<TumblerAddress> variantSpans, OulipoMachine om)
			throws MalformedSpanException, MalformedTumblerException {
		List<TumblerAddress> invariantSpans = new ArrayList<>();

		for (TumblerAddress vspan : variantSpans) {
			VariantSpan variantSpan = new VariantSpan(vspan.spanStart(), vspan.spanWidth());
			List<org.oulipo.streams.InvariantSpan> ispans = om.getInvariantSpans(variantSpan).getInvariantSpans();
			for (org.oulipo.streams.InvariantSpan ispan : ispans) {
				TumblerAddress invariantSpanAddress = TumblerAddress
						.create(ispan.homeDocument + ".0.3." + ispan.start + "~3." + ispan.width);
				invariantSpans.add(invariantSpanAddress);
			}
		}
		return invariantSpans;
	}

	public static List<TumblerAddress> fromVariantToInvariant(String[] variantSpans, OulipoMachine om)
			throws MalformedSpanException, MalformedTumblerException {
		List<TumblerAddress> tumblers = new ArrayList<>();
		for (String vs : variantSpans) {
			tumblers.add(TumblerAddress.create(vs));
		}
		return fromVariantToInvariant(tumblers, om);
	}

	public static TumblerAddress fromVariantToInvariant(TumblerAddress tumbler) throws MalformedTumblerException {
		return TumblerAddress
				.create(tumbler.documentVal() + ".0.3." + tumbler.spanStart() + "~3." + tumbler.spanWidth());
	}
}
