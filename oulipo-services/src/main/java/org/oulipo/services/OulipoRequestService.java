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
package org.oulipo.services;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Collection;

import org.oulipo.rdf.Thing;
import org.oulipo.rdf.model.Document;
import org.oulipo.rdf.model.Virtual;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.ThingRepository;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.UnauthorizedException;
import org.oulipo.services.endpoints.DocumentService;
import org.oulipo.services.responses.EndsetByType;
import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.RemoteFileManager;
import org.oulipo.streams.StreamLoader;

/**
 * Main service for making Oulipo requests.
 */
public final class OulipoRequestService {

	private final DocumentService documentService;

	private final ThingRepository thingRepo;

	public OulipoRequestService(ThingRepository thingRepo, ResourceSessionManager sessionManager,
			StreamLoader streamLoader, RemoteFileManager remoteFileManager) {
		this.thingRepo = thingRepo;
		documentService = new DocumentService(thingRepo, sessionManager, streamLoader, remoteFileManager);
	}

	public Collection<Thing> getAllDocuments(OulipoRequest request) {
		return thingRepo.getAllThings("Document", request.queryParams());
	}

	public Collection<Thing> getAllInvariantLinks(OulipoRequest request) {
		return thingRepo.getAllThings("Link", // TODO: make sure name is Link (not Invariant Link)
				request.queryParams());
	}

	public Collection<Thing> getAllUsers(OulipoRequest request) {
		return thingRepo.getAllThings("Person", request.queryParams());
	}

	public Document getDocument(OulipoRequest oulipoRequest) throws
			ResourceNotFoundException, UnauthorizedException, AuthenticationException {
		return documentService.getDocument(oulipoRequest);
	}

	public Collection<Thing> getDocuments(OulipoRequest oulipoRequest) {
		return documentService.getDocuments(oulipoRequest);
	}

	public EndsetByType getEndsets(OulipoRequest oulipoRequest) throws Exception {
		return documentService.getEndsets(oulipoRequest);
	}

	public Virtual getVirtual(OulipoRequest oulipoRequest) throws ResourceNotFoundException, UnauthorizedException,
			AuthenticationException, IOException, MalformedSpanException {
		return documentService.getVirtual(oulipoRequest);
	}

	public String loadDocument(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException,
			ResourceNotFoundException, IOException, MalformedSpanException, SignatureException {
		documentService.loadDocument(oulipoRequest);
		return "{}";
	}

}
