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
package org.oulipo.machine.server.api;

import static spark.Spark.halt;

import java.util.Map;
import java.util.Optional;

import org.oulipo.machine.server.RdfMapper;
import org.oulipo.machine.server.RdfRepository;
import org.oulipo.machine.server.RequestMapper;
import org.oulipo.machine.server.XanSessionManager;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Node;
import org.oulipo.resources.responses.ErrorResponseDto;
import org.oulipo.security.auth.AddressValidator;
import org.oulipo.security.auth.AuthorizationException;
import org.oulipo.security.auth.XanAuthResponseCodes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import spark.Route;

public final class NodeApi {

	public static Route createNode(RdfRepository thingRepo,
			RequestMapper requestMapper, XanSessionManager sessionManager) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			sessionManager.authenticateSession(request);

			TumblerAddress nodeAddress = requestMapper.createNodeAddress(request);

			Node node = Strings.isNullOrEmpty(request.body()) ? new Node()
					: requestMapper.readNode(request,nodeAddress);
			node.publicKey = request.headers("x-oulipo-user");
			node.resourceId = nodeAddress;

			if(!AddressValidator.validateAddress(node.publicKey)) {
				ErrorResponseDto resp = new ErrorResponseDto(
						XanAuthResponseCodes.INCORRECT_PUBLIC_KEY,
						"Invalid public key: " + node.publicKey, nodeAddress);
				return halt(400, (new ObjectMapper()).writeValueAsString(resp));
			}

			// TODO: check if node already exists and match pk

			Optional<TumblerAddress> optParentNode = node.parentNode();

			if (optParentNode.isPresent()) {
				String publicKeyOfParentNode = thingRepo
						.getPublicKeyOfNode(optParentNode.get());
				if (publicKeyOfParentNode != null
						&& !node.publicKeyMatches(publicKeyOfParentNode)) {
					throw new AuthorizationException(
							XanAuthResponseCodes.INCORRECT_PUBLIC_KEY,
							"Incorrect public key for this node");
				}
			}

			thingRepo.update(node);
			return node;
		};
	}

	public static Route getSystemNodes(RdfRepository thingRepo) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return thingRepo.getAllNodes(request);

		};
	}

	public static Route getNode(RdfRepository thingRepo, RequestMapper requestMapper) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return thingRepo
					.findNode(requestMapper.createNodeAddress(request));
		};
	}

	public static Route getNodeUsers(RdfRepository thingRepo, RequestMapper requestMapper) {
		return (request, response) -> {
			response.header("content-type", "application/json");

			int network = Integer.parseInt(request.params(":networkId"));
			TumblerAddress nodeAddress = requestMapper.createNodeAddress(request);

			Map<String, String> queryParams = RdfMapper.getQueryMapFrom(request);
			queryParams.put("node", nodeAddress.toTumblerAuthority());
			return thingRepo.getAllUsers(network, queryParams);
		};
	}
}
