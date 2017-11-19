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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.oulipo.security.auth.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class OulipoRequest {

	private final byte[] body;

	private final String hash;

	private final Map<String, String> headers;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private Map<String, String> params;

	private String publicKey;

	private final ResourceSessionManager sessionManager;
	
	private String token;

	public OulipoRequest(ResourceSessionManager sessionManager, Map<String, String> headers, Map<String, String> params,
			byte[] body) {
		this.sessionManager = sessionManager;
		this.body = body;
		this.headers = headers;
		this.hash = params.get(":hash");
		if (headers != null) {
			this.publicKey = headers.get("x-oulipo-user");
			this.token = headers.get("x-oulipo-token");
		}
	}

	public void authenticate() throws AuthenticationException {
		sessionManager.authenticateSession(this);
	}

	public String getBody() {
		return new String(body, StandardCharsets.UTF_8);
	}

	public byte[] getBodyAsBytes() {
		return body;
	}

	public String getDocumentHash()  {
		return hash;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public String getToken() {
		return token;
	}

	public boolean hasBody() {
		return body != null && body.length > 0;
	}

	public Map<String, String> queryParams() {
		//TODO: parse
		/*
		try {
			return getNodeAddress().getQueryParams();
		} catch (MalformedTumblerException e) {
			e.printStackTrace();
		}
		*/
		return new HashMap<>();
	}

}
