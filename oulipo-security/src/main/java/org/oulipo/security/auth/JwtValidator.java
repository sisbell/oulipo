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

/**
 * Validates a JSON Web Token
 */
public final class JwtValidator {

	/**
	 * Verify message was signed with a private key that matches publicKey
	 * 
	 * The signature is the signed message, where the message includes the publicKey
	 * 
	 * @param publicKey
	 *            the publicKey of the public/private key pair used for signing
	 * @param message
	 *            the payload
	 * @param signature
	 *            the signature portion of a JWT
	 * @return
	 * @throws AuthenticationException
	 *             if publicKey doesn't match signed message
	 */
	public static SessionResponse verifyMessage(String publicKey, String message, String signature)
			throws AuthenticationException {
		try {
			if (!publicKey
					.equals(ECKey.signedMessageToKey(message, signature).toAddress(MainNetParams.get()).toString())) {
				throw new AuthenticationException(AuthResponseCodes.INVALID_ADDRESS, "Signature is incorrect");
			}
		} catch (SignatureException e) {
			throw new AuthenticationException(AuthResponseCodes.INVALID_SIGNATURE, e.getMessage());
		}
		SessionResponse response = new SessionResponse();
		response.expiresIn = -1;
		response.publicKey = publicKey;
		response.isAuthorized = true;
		return response;
	}
}
