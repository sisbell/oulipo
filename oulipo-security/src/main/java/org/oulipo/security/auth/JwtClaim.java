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
 * JWT Claim. JSON Web Token (JWT) is a compact, URL-safe means of representing
 * claims to be transferred between two parties. The claims in a JWT are encoded
 * as a JSON object that is used as the payload of a JSON Web Signature (JWS)
 * structure or as the plaintext of a JSON Web Encryption (JWE) structure,
 * enabling the claims to be digitally signed or integrity protected with a
 * Message Authentication Code (MAC) and/or encrypted.
 * 
 * @see https://jwt.io/introduction/
 */
public final class JwtClaim {

	/**
	 * Issuer. Identifies the principal that issued the JWT
	 */
	public String iss;

	/**
	 * Issued At. Identifies the time at which the JWT was issued
	 */
	public long iat;

	/**
	 * JWT ID. Provides a unique identifier for the JWT
	 */
	public String jti;

	/**
	 * Expiration Time. Identifies the expiration time on or after which the JWT
	 * MUST NOT be accepted for processing
	 */
	public long exp;

	/**
	 * Audience. Identifies the recipients that the JWT is intended for
	 */
	public String aud;

	/**
	 * Scope
	 */
	public String scope;

	/**
	 * Application of claim
	 */
	public String app;
}
