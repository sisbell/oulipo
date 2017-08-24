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
package org.oulipo.services.endpoints;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.ThingRepository;
import org.oulipo.resources.model.Node;
import org.oulipo.resources.model.Thing;
import org.oulipo.resources.model.User;
import org.oulipo.security.auth.AddressValidator;
import org.oulipo.security.auth.AuthResponseCodes;
import org.oulipo.security.auth.AuthorizationException;
import org.oulipo.services.OulipoRequest;

public class UserService {

	private final ThingRepository thingRepo;

	public UserService(ThingRepository thingRepo) {
		this.thingRepo = thingRepo;
	}

	/**
	 * Creates or updates a user for the specified node
	 * 
	 * @param account
	 *            the account to update or delete
	 * @param nodeAddress
	 *            the node of the user
	 * @param publicKey
	 *            the public key of the user
	 * @return
	 * @throws Exception
	 * @throws NumberFormatException
	 */

	public User createOrUpdateUser(OulipoRequest oulipoRequest) throws NumberFormatException, Exception {
		oulipoRequest.authenticate();

		Node node = thingRepo.findNode(oulipoRequest.getNodeAddress(),
				"Node for this user does not exist. Try creating node first. Node = " + oulipoRequest.getNodeId()
						+ ", User = " + oulipoRequest.getUserId());

		if (!node.allowUserToCreateAccount) {
			// throw new UnauthorizedException();
		}

		if (!node.publicKeyMatches(oulipoRequest.getPublicKey())) {
			throw new AuthorizationException(AuthResponseCodes.INCORRECT_PUBLIC_KEY,
					"Incorrect public key for creating new user");
		}

		User account = oulipoRequest.getUser();
		account.node = (TumblerAddress) node.resourceId;

		if (!account.hasPublicKey()) {
			throw new AuthorizationException(AuthResponseCodes.INCORRECT_PUBLIC_KEY, "Must add publicKey for user");
		}

		if (!AddressValidator.validateAddress(account.publicKey)) {
			throw new AuthorizationException(AuthResponseCodes.INCORRECT_PUBLIC_KEY,
					"Invalid public key: " + account.publicKey);
		}

		if (account.hasXandle()) {
			Optional<User> currentUserOfXandle = thingRepo
					.findUserByXandle(Integer.parseInt(oulipoRequest.getNetworkId()), account.xandle);
			if (currentUserOfXandle.isPresent()) {
				User current = currentUserOfXandle.get();
				if (!current.publicKeyMatches(account.publicKey) || !current.resourceId.equals(account.resourceId)) {
					throw new AuthorizationException(AuthResponseCodes.INCORRECT_PUBLIC_KEY,
							"Xandle in use : " + account.xandle + " @ " + current.resourceId.value);
				}
			}
		}

		thingRepo.update(account);

		return account;
	}

	public Collection<Thing> getSystemUsers(OulipoRequest oulipoRequest) {
		int network = Integer.parseInt(oulipoRequest.getNetworkId());
		return thingRepo.getAllUsers(network, oulipoRequest.queryParams());
	}

	public User getUser(OulipoRequest oulipoRequest) throws ResourceNotFoundException, MalformedTumblerException {
		return thingRepo.findUser(oulipoRequest.getUserAddress());
	}

	public Collection<Thing> getUserDocuments(OulipoRequest oulipoRequest) throws MalformedTumblerException {
		int network = Integer.parseInt(oulipoRequest.getNetworkId());
		TumblerAddress userAddress = oulipoRequest.getUserAddress();

		Map<String, String> queryParams = oulipoRequest.queryParams();
		queryParams.put("account", userAddress.toTumblerAuthority());
		return thingRepo.getAllDocuments(network, queryParams);

	}
}
