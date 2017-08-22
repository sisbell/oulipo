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

import java.security.SignatureException;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public final class SessionResponse {

	/**
	 * The public key (or userId) to which the masterToken is assigned
	 */
	public String publicKey;

	/**
	 * Session token
	 */
	public String masterToken;

	/**
	 * The time in milliseconds that the masterToken expires
	 */
	public long expiresIn;

	/**
	 * Response message
	 */
	public String message;

	/**
	 * Authentication response code
	 * 
	 * @see com.oulipo.security.auth.AuthResponseCode
	 */
	public int code;

	@JsonIgnore
	public boolean isAuthorized;

	public SessionResponse() {
	}

	/**
	 * Constructs a <code>SessionResponse</code> with the specified code and message
	 * 
	 * @param code
	 *            the authentication response code
	 * @param message
	 *            the response message
	 */
	public SessionResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public String toString() {
		return "XanAuthResponseDto [publicKey=" + publicKey + ", masterToken=" + masterToken + ", expiresIn="
				+ expiresIn + ", message=" + message + ", code=" + code + "]";
	}

	/**
	 * Creates a used SessionToken with response code INVALID_TOKEN.
	 * 
	 * @return
	 */
	static SessionResponse createUsedSessionResponse() {
		return new SessionResponse(AuthResponseCodes.INVALID_TOKEN, "JIT has been used");
	}

	/**
	 * Creates a session response. This method will verify the message signature and
	 * in case of failure will return a session with response code INVALID_ADDRESS
	 * or INVALID_SIGNATURE. Otherwise it will return a valid SessionResponse with
	 * response code OK.
	 * 
	 * This SessionResponse will still need the masterToken set (if it is valid)
	 * before returning to the user
	 * 
	 * @param publicKey
	 * @param message
	 * @param signature
	 * @return
	 */
	public static SessionResponse createVerifiedSessionResponse(String publicKey, String message, String signature) {
		try {
			if (!publicKey
					.equals(ECKey.signedMessageToKey(message, signature).toAddress(MainNetParams.get()).toString())) {
				return new SessionResponse(AuthResponseCodes.INVALID_ADDRESS, "Signature is incorrect");
			}
		} catch (SignatureException e) {
			return new SessionResponse(AuthResponseCodes.INVALID_SIGNATURE, e.getMessage());
		}
		SessionResponse response = new SessionResponse();
		response.expiresIn = -1;
		response.isAuthorized = true;
		response.publicKey = publicKey;
		return response;
	}

}
