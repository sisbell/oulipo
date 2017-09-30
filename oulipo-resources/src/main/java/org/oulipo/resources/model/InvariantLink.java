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

import org.oulipo.net.TumblerAddress;
import org.oulipo.rdf.annotations.ObjectNonNegativeInteger;
import org.oulipo.rdf.annotations.ObjectTumbler;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.Subject;

@Subject(value = Schema.INVARIANT_LINK, key = "resourceId")
public class InvariantLink extends Thing {

	@Predicate("document")
	@ObjectTumbler
	public TumblerAddress document;

	@Predicate("fromInvariantSpan")
	@ObjectTumbler
	public List<TumblerAddress> fromInvariantSpans = new ArrayList<>();

	@Predicate("linkType")
	@ObjectTumbler
	public List<TumblerAddress> linkTypes = new ArrayList<>();

	@Predicate("sequence")
	@ObjectNonNegativeInteger
	public int sequence;

	@Predicate("toInvariantSpan")
	@ObjectTumbler
	public List<TumblerAddress> toInvariantSpans = new ArrayList<>();

	public int sequence() {
		return ((TumblerAddress) resourceId).getElement().get(1);
	}
}
