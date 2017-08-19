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
package org.oulipo.machine.server;


import org.oulipo.machine.server.exceptions.UnauthorizedException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.User;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.session.SessionManager;
import org.oulipo.storage.StorageService;

import spark.Request;

/**
 * Provides services for authentication resources
 */
public final class XanSessionManager extends SessionManager {

	private final RdfRepository thingRepo;
	
	private final RequestMapper requestMapper;

	public XanSessionManager(StorageService storage, RdfRepository thingRepo, RequestMapper requestMapper) {
		super(storage);
		this.thingRepo = thingRepo;
		this.requestMapper = requestMapper;
	}

	/**
	 * Authenticates user by checking if the user session (associated with
	 * x-oulipo-token) matches the publicKey in the HTTP header. This method
	 * does not check if the user is authorized for a particular resource.
	 * 
	 * Validates that the public key in the user session is the same as the
	 * specified public key (from HTTP request header).
	 * 
	 * @param request
	 * @throws AuthenticationException
	 */
	public void authenticateSession(Request request)
			throws AuthenticationException {
		String token = request.headers("x-oulipo-token");
		String publicKey = request.headers("x-oulipo-user");

		super.authenticateSession(token, publicKey);
	}

	/**
	 * Users publicKey matches what we have
	 * 
	 * @param resource
	 * @param request
	 * @throws UnauthorizedException
	 * @throws MalformedTumblerException
	 * @throws org.oulipo.resources.ResourceNotFoundException 
	 */
	public void authorizeResource(Request request)
			throws UnauthorizedException, MalformedTumblerException,
			ResourceNotFoundException {
		TumblerAddress userAddress = requestMapper.createUserAddress(request);
		User user = thingRepo.findUser(userAddress);
		if (!user.publicKeyMatches(request.headers("x-oulipo-user"))) {
			throw new UnauthorizedException(null,
					"User is not authorized to modify this resource");
		}
	}
	
	public Document checkReadAccessOfDocument(Request request)
			throws MalformedTumblerException, ResourceNotFoundException,
			AuthenticationException, UnauthorizedException {
		TumblerAddress documentAddress = requestMapper.createDocumentAddress(request);
		Document document = thingRepo.findDocument(documentAddress);
		if (document.isPublic != null && !document.isPublic) {
			authenticateSession(request);
			authorizeResource(request); 
		}

		return document;
	}

}
