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
import java.util.Set;

import org.oulipo.net.IRI;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.rdf.annotations.ObjectNonNegativeInteger;
import org.oulipo.rdf.annotations.ObjectTumbler;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.Subject;
import org.oulipo.resources.utils.Add;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Invariant Span
 */
@Subject(value = Schema.INVARIANT_SPAN, key = "resourceId")
public class InvariantSpan extends Thing {

	/**
	 * Home document of this invariant span
	 */
	@Predicate("document")
	@ObjectTumbler
	public TumblerAddress document;

	@Predicate("fromLink")
	@ObjectTumbler
	public TumblerAddress[] fromLinks;

	@Predicate("start")
	@ObjectNonNegativeInteger
	public int start;

	@Predicate("toLink")
	@ObjectTumbler
	public TumblerAddress[] toLinks;

	@Predicate("width")
	@ObjectNonNegativeInteger
	public int width;

	public void addFromLink(Collection<TumblerAddress> link) {
		fromLinks = Add.both(fromLinks, link, TumblerAddress.class);
	}

	public void addFromLink(TumblerAddress link) {
		fromLinks = Add.one(fromLinks, link);
	}

	public void addFromLink(TumblerAddress[] link) {
		fromLinks = Add.both(fromLinks, link, TumblerAddress.class);
	}

	public void addToLink(Collection<TumblerAddress> link) {
		toLinks = Add.both(toLinks, link, TumblerAddress.class);
	}

	public void addToLink(TumblerAddress link) {
		toLinks = Add.one(toLinks, link);
	}

	public void addToLink(TumblerAddress[] link) {
		toLinks = Add.both(toLinks, link, TumblerAddress.class);
	}

	public void removeDuplicateLinks() {
		if (fromLinks != null) {
			fromLinks = ImmutableSet.copyOf(fromLinks).toArray(new TumblerAddress[0]);
		}

		if (toLinks != null) {
			toLinks = ImmutableSet.copyOf(toLinks).toArray(new TumblerAddress[0]);
		}
	}

	/**
	 * Removes the specified link from the fromLinks array
	 * 
	 * @param link
	 * @return
	 * @throws MalformedTumblerException
	 */
	public boolean removeFromLink(IRI link) throws MalformedTumblerException {
		if (fromLinks != null) {
			TumblerAddress lta = TumblerAddress.create(link.value);
			Set<TumblerAddress> links = Sets.newHashSet(fromLinks);
			if (links.remove(lta)) {
				fromLinks = links.toArray(new TumblerAddress[links.size()]);
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the specified link from the toLinks array
	 * 
	 * @param link
	 * @return
	 * @throws MalformedTumblerException
	 */
	public boolean removeToLink(IRI link) throws MalformedTumblerException {
		if (toLinks != null) {
			TumblerAddress lta = TumblerAddress.create(link.value);
			Set<TumblerAddress> links = Sets.newHashSet(toLinks);
			if (links.remove(lta)) {
				toLinks = links.toArray(new TumblerAddress[links.size()]);
				return true;
			}
		}
		return false;
	}
}
