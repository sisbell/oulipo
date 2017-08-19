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
package org.oulipo.resources.model;

import java.util.Collection;

import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.Schema;
import org.oulipo.resources.rdf.annotations.ObjectIRI;
import org.oulipo.resources.rdf.annotations.ObjectNonNegativeInteger;
import org.oulipo.resources.rdf.annotations.ObjectTumbler;
import org.oulipo.resources.rdf.annotations.Predicate;
import org.oulipo.resources.rdf.annotations.Subject;
import org.oulipo.resources.utils.Add;

import com.google.common.collect.ImmutableSet;

@Subject(value = Schema.LINK, key = "resourceId")
public class Link extends Thing {

	@Predicate("document")
	@ObjectTumbler
	public TumblerAddress document;// this tumbler does not include version, the
									// resourceId will include doc version

	@Predicate("fromSpan")
	@ObjectIRI
	public TumblerAddress fromSpan;

	@Predicate("fromVSpan")
	@ObjectIRI
	public TumblerAddress[] fromVSpans;
	
	@Predicate("linkType")
	@ObjectIRI
	public TumblerAddress[] linkTypes;

	@Predicate("sequence")
	@ObjectNonNegativeInteger
	public int sequence;

	@Predicate("toSpan")
	@ObjectIRI
	public TumblerAddress toSpan;

	@Predicate("toVSpan")
	@ObjectIRI
	public TumblerAddress[] toVSpans;
	

	public void addFromVSpan(Collection<TumblerAddress> vspan) {
		fromVSpans = Add.both(fromVSpans, vspan, TumblerAddress.class);
	}

	public void addFromVSpan(TumblerAddress vspan) {
		fromVSpans = Add.one(fromVSpans, vspan);
	}

	public void addFromVSpan(TumblerAddress[] vspan) {
		fromVSpans = Add.both(fromVSpans, vspan, TumblerAddress.class);
	}

	public void addLinkType(Collection<TumblerAddress> type) {
		linkTypes = Add.both(linkTypes, type, TumblerAddress.class);
	}
	
	public void addLinkType(TumblerAddress type) {
		linkTypes = Add.one(linkTypes, type);
	}
	
	public void addLinkType(TumblerAddress[] type) {
		linkTypes = Add.both(linkTypes, type, TumblerAddress.class);
	}
	
	public void addToVSpan(Collection<TumblerAddress> vspan) {
		toVSpans = Add.both(toVSpans, vspan, TumblerAddress.class);
	}
	
	public void addToVSpan(TumblerAddress vspan) {
		toVSpans = Add.one(toVSpans, vspan);
	}
	
	public void addToVSpan(TumblerAddress[] vspan) {
		toVSpans = Add.both(toVSpans, vspan, TumblerAddress.class);
	}
	

	public int elementType() {
		return ((TumblerAddress) resourceId).getElement().get(0);
	}

	public void removeDuplicateFromVSpans() {
		if(fromVSpans != null) {
			fromVSpans = ImmutableSet.copyOf(fromVSpans).toArray(new TumblerAddress[0]);
		}	
	}

	public void removeDuplicateLinkTypes() {
		if(linkTypes != null) {
			linkTypes = ImmutableSet.copyOf(linkTypes).toArray(new TumblerAddress[0]);
		}
	}

	public void removeDuplicates() {
		removeDuplicateLinkTypes();
		removeDuplicateFromVSpans();
		removeDuplicateToVSpans();
	}

	public void removeDuplicateToVSpans() {
		if(toVSpans != null) {
			toVSpans = ImmutableSet.copyOf(toVSpans).toArray(new TumblerAddress[0]);
		}		
	}
	
	public int sequence() {
		return ((TumblerAddress) resourceId).getElement().get(1);
	}

	public void validateLink() throws MalformedTumblerException {
		int elementType = elementType();
		if (elementType != 2) {
			throw new MalformedTumblerException(
					"Incorrect element type. Links must start with '2'. Requested link starts with "
							+ elementType);
		}
/*
		if (fromSpan != null && fromVSpans != null) {
			throw new InvalidJsonException(document,
					"Include Span OR VSpan(s) (not both) for the 'from' set : "
							+ elementType);
		}

		if (toSpan != null && toVSpans != null) {
			throw new InvalidJsonException(document,
					"Include Span OR VSpan(s) (not both) for the 'to' set : "
							+ elementType);
		}
		*/
	}
}
