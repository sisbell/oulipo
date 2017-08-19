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
import org.oulipo.resources.model.User;
import org.oulipo.resources.responses.ErrorResponseDto;
import org.oulipo.security.auth.AddressValidator;
import org.oulipo.security.auth.AuthorizationException;
import org.oulipo.security.auth.XanAuthResponseCodes;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Route;

public class UserApi {

	public static Route createUser(ObjectMapper objectMapper,
			RequestMapper requestMapper, RdfRepository thingRepo,
			XanSessionManager sessionManager) {
		return null;
	}

	public static Route createOrUpdateUser(ObjectMapper objectMapper,
			RequestMapper requestMapper, RdfRepository thingRepo,
			XanSessionManager sessionManager) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			sessionManager.authenticateSession(request);

			Node node = thingRepo.findNode(
					requestMapper.createNodeAddress(request),
					"Node for this user does not exist. Try creating node first. Node = "
							+ request.params(":nodeId") + ", User = "
							+ request.params(":userId"));


			if (!node.allowUserToCreateAccount) {
				// throw new UnauthorizedException();
			}

			if (!node.publicKeyMatches(request.headers("x-oulipo-user"))) {
				throw new AuthorizationException(
						XanAuthResponseCodes.INCORRECT_PUBLIC_KEY,
						"Incorrect public key for creating new user");
			}

			TumblerAddress userAddress = requestMapper
					.createUserAddress(request);

			User account = requestMapper.readUser(request, userAddress);
			account.node = node.resourceId;

			if (!account.hasPublicKey()) {
				ErrorResponseDto resp = new ErrorResponseDto(
						XanAuthResponseCodes.INCORRECT_PUBLIC_KEY,
						"Must add publicKey for user", userAddress);
				return halt(400, objectMapper.writeValueAsString(resp));
			}

			if(!AddressValidator.validateAddress(account.publicKey)) {
				ErrorResponseDto resp = new ErrorResponseDto(
						XanAuthResponseCodes.INCORRECT_PUBLIC_KEY,
						"Invalid public key: " + account.publicKey, userAddress);
				return halt(400, objectMapper.writeValueAsString(resp));
			}

			if (account.hasXandle()) {
				Optional<User> currentUserOfXandle = thingRepo
						.findUserByXandle(
								Integer.parseInt(request.params(":networkId")),
								account.xandle);
				if (currentUserOfXandle.isPresent()) {
					User current = (User) currentUserOfXandle.get();
					if (!current.publicKeyMatches(account.publicKey)
							|| !current.resourceId.equals(account.resourceId)) {
						ErrorResponseDto resp = new ErrorResponseDto(
								XanAuthResponseCodes.INCORRECT_PUBLIC_KEY,
								"Xandle in use : " + account.xandle + " @ "
										+ current.resourceId.value, userAddress);
						return halt(400, objectMapper.writeValueAsString(resp));
					}
				}
			}

			thingRepo.update(account);

			return account;
		};
	}

	public static Route getSystemUser(RdfRepository thingRepo) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return thingRepo.getAllUsers(request);
		};
	}

	public static Route getUser(RdfRepository thingRepo, RequestMapper requestMapper) {
		return (request, response) -> {
			response.header("content-type", "application/json");

			TumblerAddress userAddress = requestMapper
					.createUserAddress(request);
			return thingRepo.findUser(userAddress);
		};
	}

	public static Route getUserDocuments(RdfRepository thingRepo, RequestMapper requestMapper) {
		return (request, response) -> {
			response.header("content-type", "application/json");

			int network = Integer.parseInt(request.params(":networkId"));
			TumblerAddress userAddress = requestMapper
					.createUserAddress(request);
			Map<String, String> queryParams = RdfMapper.getQueryMapFrom(request);
			queryParams.put("account", userAddress.toTumblerAuthority());
			return thingRepo.getAllDocuments(network, queryParams);
		};
	}
}
