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
import org.oulipo.security.auth.AddressValidator;
import org.oulipo.security.auth.AuthResponseCodes;
import org.oulipo.security.auth.AuthorizationException;
import org.oulipo.services.OulipoRequest;

public class NodeService {

	private final ThingRepository thingRepo;

	public NodeService(ThingRepository thingRepo) {
		this.thingRepo = thingRepo;
	}

	public Node createNode(OulipoRequest oulipoRequest) throws Exception {
		oulipoRequest.authenticate();
		
		Node node = oulipoRequest.hasBody() ? oulipoRequest.getNode() : new Node();
		node.publicKey = oulipoRequest.getPublicKey();

		if (!AddressValidator.validateAddress(node.publicKey)) {
			throw new AuthorizationException(AuthResponseCodes.INCORRECT_PUBLIC_KEY,
					"Invalid public key: " + node.publicKey);
		}

		// TODO: check if node already exists and match pk

		Optional<TumblerAddress> optParentNode = node.parentNode();

		if (optParentNode.isPresent()) {
			String publicKeyOfParentNode = thingRepo.getPublicKeyOfNode(optParentNode.get());
			if (publicKeyOfParentNode != null && !node.publicKeyMatches(publicKeyOfParentNode)) {
				throw new AuthorizationException(AuthResponseCodes.INCORRECT_PUBLIC_KEY,
						"Incorrect public key for this node");
			}
		}

		thingRepo.update(node);
		return node;

	}
	
	public Node getNode(OulipoRequest oulipoRequest) throws ResourceNotFoundException, MalformedTumblerException {
		return thingRepo.findNode(oulipoRequest.getNodeAddress());
	}
	
	public Collection<Thing> getNodeUsers(OulipoRequest oulipoRequest) throws MalformedTumblerException {
		Map<String, String> queryParams = oulipoRequest.queryParams();
		queryParams.put("node", oulipoRequest.getNodeAddress().toTumblerAuthority());
		return thingRepo.getAllUsers(oulipoRequest.getNetworkIdAsInt(), queryParams);
	}
	
	public Collection<Thing> getSystemNodes(OulipoRequest oulipoRequest) throws MalformedTumblerException {

		Map<String, String> queryParams = oulipoRequest.queryParams();
		return thingRepo.getAllNodes(oulipoRequest.getNetworkIdAsInt(), queryParams);
	}
	


}
