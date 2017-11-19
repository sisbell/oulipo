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
import org.oulipo.rdf.annotations.ObjectString;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.Subject;

import com.google.common.base.Strings;

@Subject(value = Schema.USER, key = "resourceId")
public class User extends Thing {

	@Predicate("familyName")
	@ObjectString
	public String familyName;

	@Predicate("givenName")
	@ObjectString
	public String givenName;

	@Predicate("publicKey")
	@ObjectString
	public String publicKey;

	/**
	 * Handle for user
	 */
	@Predicate("xandle")
	@ObjectString
	public String xandle;

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
		return "User [familyName=" + familyName + ", givenName=" + givenName + ", publicKey=" + publicKey + ", xandle="
				+ xandle + "]";
	}
}
