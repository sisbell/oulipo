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

import java.util.Map;

import org.oulipo.machine.server.RdfMapper;
import org.oulipo.machine.server.RdfRepository;
import org.oulipo.machine.server.RequestMapper;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.ResourceErrorCodes;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.responses.Network;

import spark.Route;

public final class NetworkApi {

	public static Route getNetwork(RequestMapper requestMapper) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			TumblerAddress networkAddress = requestMapper
					.createNetworkAddress(request);
			if (networkAddress.isMainNet()) {
				return new Network("mainnet");
			} else if (networkAddress.isTestNet()) {
				return new Network("testnet");
			}
			throw new ResourceNotFoundException(networkAddress,
					ResourceErrorCodes.NETWORK_NOT_FOUND, "Network does not exist");
		};
	}

	public static Route getNodes(RdfRepository thingRepo) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			Map<String, String> queryParams = RdfMapper.getQueryMapFrom(request);
			return thingRepo.getAllNodes(Integer.parseInt(request.params(":networkId")), queryParams);
		};
	}
}
