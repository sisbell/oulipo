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

import java.util.Collection;

import org.oulipo.net.IRI;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.net.TumblerField;
import org.oulipo.rdf.annotations.ObjectBoolean;
import org.oulipo.rdf.annotations.ObjectIRI;
import org.oulipo.rdf.annotations.ObjectNonNegativeInteger;
import org.oulipo.rdf.annotations.ObjectString;
import org.oulipo.rdf.annotations.ObjectTumbler;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.Subject;
import org.oulipo.resources.utils.Add;

import com.google.common.collect.ImmutableSet;

/**
 *
 */
@Subject(value = Schema.DOCUMENT, key = "resourceId")
public class Document extends Thing {

	@Predicate("description")
	@ObjectString
	public String description;

	@Predicate("isPublic")
	@ObjectBoolean
	public Boolean isPublic;

	@Predicate("documentLink")
	@ObjectTumbler
	public TumblerAddress[] links;// links can be from previous versions (allows
									// reuse)

	/**
	 * This will be set by the system. Do not use in request
	 */
	@Predicate("majorVersion")
	@ObjectNonNegativeInteger
	public int majorVersion;

	/**
	 * This will be set by the system. Do not use in request
	 */
	@Predicate("minorVersion")
	@ObjectNonNegativeInteger
	public int minorVersion;

	/**
	 * This will be set by the system. Do not use in request
	 */
	@Predicate("revision")
	@ObjectNonNegativeInteger
	public int revision;

	@Predicate("title")
	@ObjectString
	public String title;

	/**
	 * This will be set by the system. Do not use in request
	 */
	@Predicate("account")
	@ObjectIRI
	public IRI user;

	@Predicate("containsVSpan")
	@ObjectTumbler
	public TumblerAddress[] vspans;

	public void addLink(Collection<TumblerAddress> link) {
		links = Add.both(links, link, TumblerAddress.class);
	}

	public void addLink(TumblerAddress link) {
		links = Add.one(links, link);
	}

	public void addLink(TumblerAddress[] link) {
		links = Add.both(links, link, TumblerAddress.class);
	}

	public void addVSpan(Collection<TumblerAddress> vspan) {
		vspans = Add.both(vspans, vspan, TumblerAddress.class);
	}

	public void addVSpan(TumblerAddress vspan) {
		vspans = Add.one(vspans, vspan);
	}

	public void addVSpan(TumblerAddress[] vspan) {
		vspans = Add.both(vspans, vspan, TumblerAddress.class);
	}

	public Document newVersion() throws MalformedTumblerException {
		TumblerAddress address = TumblerAddress.create(resourceId.value);
		TumblerField docField = address.getDocument();
		int v = docField.get(2) + 1;

		TumblerField newDocField = TumblerField.create(docField.get(0) + "." + docField.get(1) + "." + v);

		address = new TumblerAddress.Builder(address.getScheme(), address.getNetwork(), address.getNode(),
				address.getUser(), newDocField).build();
		Document document = new Document();
		document.resourceId = address;
		document.isPublic = isPublic;
		document.title = title;
		document.user = user;
		document.revision = v;
		document.links = links;
		document.vspans = vspans;
		document.majorVersion = majorVersion;
		document.minorVersion = minorVersion;
		// document.payableBitcoinAddresses = payableBitcoinAddresses;
		return document;
	}

	public void removeDuplicateLinks() {
		if (links != null) {
			links = ImmutableSet.copyOf(links).toArray(new TumblerAddress[0]);
		}
	}

	public void removeDuplicates() {
		removeDuplicateLinks();
		removeDuplicateVSpans();
	}

	public void removeDuplicateVSpans() {
		if (vspans != null) {
			vspans = ImmutableSet.copyOf(vspans).toArray(new TumblerAddress[0]);
		}
	}

}
