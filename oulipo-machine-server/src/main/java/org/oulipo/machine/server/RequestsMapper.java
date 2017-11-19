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

	private OulipoRequest createOulipoRequest(Request request) {
		Map<String, String> headers = new HashMap<>();
		for (String key : request.headers()) {
			String value = request.headers(key);
			headers.put(key, value);
		}
		// request.queryMap().toMap()
		return new OulipoRequest(sessionManager, headers, request.params(), request.bodyAsBytes());
	};

	public Route getDocument() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getDocument(createOulipoRequest(request));
		};
	};

	public Route getDocuments() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getDocuments(createOulipoRequest(request));
		};
	};

	public Route getEndsets() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getEndsets(createOulipoRequest(request));
		};
	};

	public Route getVirtual() {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return service.getVirtual(createOulipoRequest(request));
		};
	}

	public Route loadDocument() {
		return (request, response) -> {
			return service.loadDocument(createOulipoRequest(request));
		};
	}

}
