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
package org.oulipo.security.auth;

/**
 * Provide an address and temporary token for authentication. This will be
 * returned by the server to the requesting client as the first step in an
 * authentication flow.
 * 
 */
public final class TempTokenResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7329489471066021397L;

	/**
	 * Temporary authentication token
	 */
	public String token;

	/**
	 * Authentication URL
	 */
	public String xanauth;

	public TempTokenResponse() {
	}

	public TempTokenResponse(String xanauth, String token) {
		this.xanauth = xanauth;
		this.token = token;
	}

}
