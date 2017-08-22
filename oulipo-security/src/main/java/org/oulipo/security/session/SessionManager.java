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
package org.oulipo.security.session;

import java.util.Date;

import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.TempToken;
import org.oulipo.security.auth.AuthResponseCodes;
import org.oulipo.storage.StorageException;
import org.oulipo.storage.StorageService;

import com.google.common.base.Strings;

public class SessionManager {

	protected StorageService storage;

	public SessionManager(StorageService storage) {
		this.storage = storage;
	}
	
	/**
	 * Authenticates user by checking if the user session (associated with
	 * x-oulipo-token) matches the publicKey in the HTTP header. This method
	 * does not check if the user is authorized for a particular resource.
	 * 
	 * Validates that the public key in the user session is the same as the
	 * specified public key (from HTTP request header).
	 * 
	 * @throws AuthenticationException
	 */
	public void authenticateSession(String token, String publicKey)
			throws AuthenticationException {
		if (Strings.isNullOrEmpty(publicKey) || Strings.isNullOrEmpty(token)) {
			throw new AuthenticationException(
					AuthResponseCodes.INVALID_TOKEN,
					"Please add authentication headers in request");
		}

		try {
			UserSession session = findSession(token);
			if (!session.userId.equals(publicKey)) {
				throw new AuthenticationException(
						AuthResponseCodes.INVALID_TOKEN, "Invalid token");
			}
		} catch (StorageException e) {
			e.printStackTrace();
			throw new AuthenticationException(
					AuthResponseCodes.INVALID_ADDRESS,
					"Session token not found: " + e.getMessage());
		}
	}

	
	public UserSession findSession(String id) throws StorageException {
		if (Strings.isNullOrEmpty(id)) {
			throw new IllegalArgumentException("id is null");
		}
		return storage.load(id, UserSession.class);
	}

	public TempToken findTempToken(String jti) throws StorageException {
		if (Strings.isNullOrEmpty(jti)) {
			throw new IllegalArgumentException("jti is nnull");
		}
		return storage.load(jti, TempToken.class);
	}

	public void put(TempToken token) throws StorageException {
		storage.save(token);
	}

	public void put(UserSession session) throws StorageException {
		storage.save(session);
	}

	public String storeNewTempToken() throws StorageException {
		String token = CodeGenerator.generateCode(16);
		storage.save(new TempToken(token, new Date(), false));
		return token;
	}

}
