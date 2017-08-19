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
import java.util.Optional;

import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.Schema;
import org.oulipo.resources.rdf.annotations.ObjectBoolean;
import org.oulipo.resources.rdf.annotations.ObjectString;
import org.oulipo.resources.rdf.annotations.ObjectTumbler;
import org.oulipo.resources.rdf.annotations.Predicate;
import org.oulipo.resources.rdf.annotations.Subject;
import org.oulipo.resources.utils.Add;

import com.google.common.base.Strings;

@Subject(value = Schema.NODE, key = "resourceId")
public final class Node extends Thing {

	@Predicate("publicKey")
	@ObjectString
	public String publicKey;

	@Predicate("allowUserToCreateAccount")
	@ObjectBoolean
	public boolean allowUserToCreateAccount;

	@Predicate("account")
	@ObjectTumbler
	public TumblerAddress[] accounts;

	@Predicate("nodeName")
	@ObjectString
	public String nodeName;

	@Predicate("organizationName")
	@ObjectString
	public String organizationName;

	@Predicate("addressCountry")
	@ObjectString
	public String addressCountry;

	@Predicate("addressLocality")
	@ObjectString
	public String addressLocality;

	@Predicate("addressRegion")
	@ObjectString
	public String addressRegion;

	@Predicate("postOfficeBoxNumber")
	@ObjectString
	public String postOfficeBoxNumber;

	@Predicate("postalCode")
	@ObjectString
	public String postalCode;

	@Predicate("streetAddress")
	@ObjectString
	public String streetAddress;

	@Predicate("telephone")
	@ObjectString
	public String telephone;

	@Predicate("email")
	@ObjectString
	public String email;
	
	public Node() {
	}
	

	public Node(String tumbler) throws MalformedTumblerException {
		super(TumblerAddress.create("ted://" + tumbler));
	}

	public Node(TumblerAddress resourceId) {
		super(resourceId);
	}

	public void addAccount(Collection<TumblerAddress> user) {
		accounts = Add.both(accounts, user, TumblerAddress.class);
	}

	public void addAccount(TumblerAddress user) {
		accounts = Add.one(accounts, user);
	}

	public void addAccount(TumblerAddress[] user) {
		accounts = Add.both(accounts, user, TumblerAddress.class);
	}

	public Optional<TumblerAddress> parentNode()
			throws MalformedTumblerException {
		if (resourceId == null) {
			throw new IllegalStateException("ResourceId has not been set");
		}
		TumblerAddress address = parentNodeOf((TumblerAddress) resourceId);
		return address == null ? Optional.empty() : Optional.of(address);
	}

	public boolean publicKeyMatches(String publicKey) {
		if (Strings.isNullOrEmpty(publicKey)) {
			return false;
		}
		return publicKey.equals(this.publicKey);
	}
	
	public static TumblerAddress parentNodeOf(TumblerAddress address)
			throws MalformedTumblerException {
		if (address == null) {
			throw new MalformedTumblerException("address is null");
		}
		return new TumblerAddress.Builder("ted", address.getNetwork()
				.asString()).node(String.valueOf(address.getNode().get(0)))
				.build();
	}

}
