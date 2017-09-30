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
package org.oulipo.resources.model;

import java.util.ArrayList;
import java.util.List;

import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.rdf.annotations.ObjectNonNegativeInteger;
import org.oulipo.rdf.annotations.ObjectTumbler;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.Subject;

import com.google.common.collect.ImmutableSet;

@Subject(value = Schema.LINK, key = "resourceId")
public class Link extends Thing {

	@Predicate("document")
	@ObjectTumbler
	public TumblerAddress document;// this tumbler does not include version, the
									// resourceId will include doc version

	@Predicate("fromVSpan")
	@ObjectTumbler
	public List<TumblerAddress> fromVSpans = new ArrayList<>();

	@Predicate("linkType")
	@ObjectTumbler
	public List<TumblerAddress> linkTypes = new ArrayList<>();

	@Predicate("sequence")
	@ObjectNonNegativeInteger
	public int sequence;

	@Predicate("toVSpan")
	@ObjectTumbler
	public List<TumblerAddress> toVSpans = new ArrayList<>();

	public int elementType() {
		return ((TumblerAddress) resourceId).getElement().get(0);
	}

	public void removeDuplicateFromVSpans() {
		if (fromVSpans != null) {
			fromVSpans = ImmutableSet.copyOf(fromVSpans).asList();
		}
	}

	public void removeDuplicateLinkTypes() {
		if (linkTypes != null) {
			linkTypes = ImmutableSet.copyOf(linkTypes).asList();
		}
	}

	public void removeDuplicates() {
		removeDuplicateLinkTypes();
		removeDuplicateFromVSpans();
		removeDuplicateToVSpans();
	}

	public void removeDuplicateToVSpans() {
		if (toVSpans != null) {
			toVSpans = ImmutableSet.copyOf(toVSpans).asList();
		}
	}

	public int sequence() {
		return ((TumblerAddress) resourceId).getElement().get(1);
	}

	@Override
	public String toString() {
		return "Link [document=" + document + ", fromVSpans=" + fromVSpans + ", linkTypes=" + linkTypes + ", sequence="
				+ sequence + ", toVSpans=" + toVSpans + "]";
	}

	public void validateLink() throws MalformedTumblerException {
		int elementType = elementType();
		if (elementType != 2) {
			throw new MalformedTumblerException(
					"Incorrect element type. Links must start with '2'. Requested link starts with " + elementType);
		}
		/*
		 * if (fromSpan != null && fromVSpans != null) { throw new
		 * InvalidJsonException(document,
		 * "Include Span OR VSpan(s) (not both) for the 'from' set : " + elementType); }
		 * 
		 * if (toSpan != null && toVSpans != null) { throw new
		 * InvalidJsonException(document,
		 * "Include Span OR VSpan(s) (not both) for the 'to' set : " + elementType); }
		 */
	}
}
