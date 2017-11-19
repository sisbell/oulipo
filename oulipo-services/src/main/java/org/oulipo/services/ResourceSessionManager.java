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

import org.oulipo.rdf.model.Document;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.ThingRepository;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.UnauthorizedException;
import org.oulipo.security.session.SessionManager;
import org.oulipo.storage.StorageService;
import org.oulipo.streams.IRI;

public class ResourceSessionManager extends SessionManager {

	private final ThingRepository thingRepo;

	public ResourceSessionManager(StorageService storage, ThingRepository thingRepo) {
		super(storage);
		this.thingRepo = thingRepo;
	}

	/**
	 * Authenticates user by checking if the user session (associated with
	 * x-oulipo-token) matches the publicKey in the HTTP header. This method does
	 * not check if the user is authorized for a particular resource.
	 * 
	 * Validates that the public key in the user session is the same as the
	 * specified public key (from HTTP request header).
	 * 
	 * @param request
	 * @throws AuthenticationException
	 */
	public void authenticateSession(OulipoRequest oulipoRequest) throws AuthenticationException {
		super.authenticateSession(oulipoRequest.getToken(), oulipoRequest.getPublicKey());
	}

	public Document getDocumentForReadAccess(OulipoRequest oulipoRequest) throws 
			ResourceNotFoundException, AuthenticationException, UnauthorizedException {
		String documentHash = oulipoRequest.getDocumentHash();
		return thingRepo.findDocument(new IRI(documentHash));
	}

}
