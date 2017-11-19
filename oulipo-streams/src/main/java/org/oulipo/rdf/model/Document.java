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
package org.oulipo.rdf.model;

import org.oulipo.rdf.Thing;
import org.oulipo.rdf.annotations.ObjectIRI;
import org.oulipo.rdf.annotations.ObjectNonNegativeInteger;
import org.oulipo.rdf.annotations.ObjectString;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.Subject;
import org.oulipo.streams.IRI;

/**
 *
 */
@Subject(value = Schema.DOCUMENT, key = "resourceId")
public class Document extends Thing {

	@Predicate("description")
	@ObjectString
	public String description;

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

}
