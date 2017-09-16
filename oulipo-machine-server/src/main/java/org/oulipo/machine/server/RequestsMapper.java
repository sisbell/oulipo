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
package org.oulipo.machine.server;

import java.util.HashMap;
import java.util.Map;

import org.oulipo.resources.ThingRepository;
import org.oulipo.services.OulipoRequest;
import org.oulipo.services.OulipoRequestService;
import org.oulipo.services.ResourceSessionManager;

import spark.Request;
import spark.Route;

public class RequestsMapper {

	private final OulipoRequestService service;

	private final ResourceSessionManager sessionManager;

	private final ThingRepository thingRepo;

	public RequestsMapper(OulipoRequestService service, ResourceSessionManager sessionManager,
			ThingRepository thingRepo) {
		this.service = service;
		this.sessionManager = sessionManager;
		this.thingRepo = thingRepo;
	}

	public Route copyContent() {
		return (request, response) -> {
			return service.copyContent(createOulipoRequest(request));
		};
	}

	public Route createDocument() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.createDocument(createOulipoRequest(request));
		};
	}

	public Route createNode() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.createNode(createOulipoRequest(request));
		};
	}

	public Route createOrUpdateLink() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.createOrUpdateLink(createOulipoRequest(request));
		};
	};

	public Route createOrUpdateUser() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.createOrUpdateUser(createOulipoRequest(request));
		};
	};
	
	private OulipoRequest createOulipoRequest(Request request) {
		Map<String, String> headers = new HashMap<>();
		for(String key : request.headers()) {
			String value = request.headers(key);
			headers.put(key, value);
		}
		return new OulipoRequest(sessionManager, headers, request.params(), request.body());
	};


	public Route deleteContent() {
		return (request, response) -> {
			return service.deleteContent(createOulipoRequest(request));
		};
	};

	public Route getDocument() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getDocument(createOulipoRequest(request));
		};
	};

	public Route getDocumentLinks() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getDocumentLinks(createOulipoRequest(request));
		};
	}

	public Route getElement() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getElement(createOulipoRequest(request));
		};
	}

	public Route getEndsets() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getEndsets(createOulipoRequest(request));
		};
	}

	public Route getNetwork() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getNetwork(createOulipoRequest(request));
		};
	}

	public Route getNode() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getNode(createOulipoRequest(request));
		};
	}

	public Route getNodes() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			OulipoRequest oulipoRequest = createOulipoRequest(request);
			Map<String, String> queryParams = oulipoRequest.queryParams();
			return thingRepo.getAllNodes(oulipoRequest.getNetworkIdAsInt(), queryParams);
		};
	}

	public Route getNodeUsers() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getNodeUsers(createOulipoRequest(request));
		};
	}

	public Route getSystemDocuments() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getSystemDocuments(createOulipoRequest(request));
		};
	}
	
	public Route getSystemLinks() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getSystemLinks(createOulipoRequest(request));
		};
	}
	
	public Route getSystemNodes() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getSystemNodes(createOulipoRequest(request));
		};
	}
	
	public Route getSystemUser() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getSystemUsers(createOulipoRequest(request));
		};
	}
	
	public Route getSystemVSpans() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getSystemVSpans(createOulipoRequest(request));
		};
	}
	
	public Route getUser() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getUser(createOulipoRequest(request));
		};
	}
	
	public Route getUserDocuments() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getUserDocuments(createOulipoRequest(request));
		};
	}
	
	public Route getVirtual() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getVirtual(createOulipoRequest(request));
		};
	}
	
	public Route insertContent() {
		return (request, response) -> {
			return service.insertContent(createOulipoRequest(request));
		};
	}
	
	public Route newDocument() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.newDocument(createOulipoRequest(request));
		};
	}
	
	public Route newUser() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.newUser(createOulipoRequest(request));
		};
	}
	public Route newVersion() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.newVersion(createOulipoRequest(request));
		};
	}
	
	public Route swapContent() {
		return (request, response) -> {
			return service.swap(createOulipoRequest(request));
		};
	}
}
