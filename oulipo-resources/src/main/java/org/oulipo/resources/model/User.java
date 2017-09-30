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

import java.util.Arrays;
import java.util.Collection;

import org.oulipo.net.IRI;
import org.oulipo.net.TumblerAddress;
import org.oulipo.rdf.annotations.ObjectIRI;
import org.oulipo.rdf.annotations.ObjectNonNegativeInteger;
import org.oulipo.rdf.annotations.ObjectString;
import org.oulipo.rdf.annotations.ObjectTumbler;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.Subject;
import org.oulipo.resources.utils.Add;

import com.google.common.base.Strings;

@Subject(value = Schema.USER, key = "resourceId")
public class User extends Thing {

	@Predicate("bitcoinPayinAddress")
	@ObjectString
	public String[] bitcoinPayinAddresses;// increase reserves in system (for payments)

	@Predicate("bitcoinPayoutAddress")
	@ObjectString
	public String bitcoinPayoutAddress;

	@Predicate("ownedDocument")
	@ObjectIRI
	public IRI[] documents;

	@Predicate("familyName")
	@ObjectString
	public String familyName;

	@Predicate("givenName")
	@ObjectString
	public String givenName;

	@Predicate("node")
	@ObjectTumbler
	public TumblerAddress node;

	@Predicate("publicKey")
	@ObjectString
	public String publicKey;

	@Predicate("rootId")
	@ObjectNonNegativeInteger
	public int rootId;

	/**
	 * Handle for user
	 */
	@Predicate("xandle")
	@ObjectString
	public String xandle;

	public void addBitcoinAddress(String address) {
		bitcoinPayinAddresses = Add.one(bitcoinPayinAddresses, address);
	}

	public void addBitcoinAddress(String[] address) {
		bitcoinPayinAddresses = Add.both(bitcoinPayinAddresses, address, String.class);
	}

	public void addBitcoinPayinAddress(Collection<String> address) {
		bitcoinPayinAddresses = Add.both(bitcoinPayinAddresses, address, String.class);
	}

	public void addDocument(Collection<IRI> document) {
		documents = Add.both(documents, document, IRI.class);
	}

	public void addDocument(IRI document) {
		documents = Add.one(documents, document);
	}

	public void addDocument(IRI[] document) {
		documents = Add.both(documents, document, IRI.class);
	}

	public boolean hasPublicKey() {
		return !Strings.isNullOrEmpty(publicKey);
	}

	public boolean hasXandle() {
		return !Strings.isNullOrEmpty(xandle);
	}

	public boolean publicKeyMatches(String publicKey) {
		if (Strings.isNullOrEmpty(publicKey)) {
			return false;
		}
		return publicKey.equals(this.publicKey);
	}

	@Override
	public String toString() {
		return "Person [documents=" + Arrays.toString(documents) + ", familyName=" + familyName + ", givenName="
				+ givenName + ", publicKey=" + publicKey + ", resourceId=" + resourceId + "]";
	}

}
