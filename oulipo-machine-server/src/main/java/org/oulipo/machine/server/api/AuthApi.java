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
package org.oulipo.machine.server.api;

import static spark.Spark.halt;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.oulipo.machine.server.XanSessionManager;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.JwtValidator;
import org.oulipo.security.auth.TempToken;
import org.oulipo.security.auth.XanAuthAddressDto;
import org.oulipo.security.auth.XanAuthRequestDto;
import org.oulipo.security.auth.XanAuthResponseCodes;
import org.oulipo.security.auth.XanAuthResponseDto;
import org.oulipo.security.session.CodeGenerator;
import org.oulipo.security.session.UserSession;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;

import spark.Route;

/**
 * Provides services for authentication a user and for generating session tokens
 */
public final class AuthApi {

	public static Route getSessionToken(ObjectMapper mapper, XanSessionManager sessionManager) {
		return (request, response) -> {
			response.header("content-type", "application/json");

			try (InputStream is = request.raw().getInputStream()) {
				String message = new String(ByteStreams.toByteArray(is),
						Charsets.UTF_8);
				String[] tokens = message.split("[.]");
				if (tokens.length != 3) {
					halt(400);
				}

				String header64 = tokens[0];
				String claim64 = tokens[1];
				String sig64 = tokens[2];

				mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

				XanAuthRequestDto authDto = mapper.readValue(BaseEncoding
						.base64Url().decode(claim64), XanAuthRequestDto.class);

				String publicKey = authDto.iss;

				TempToken tempToken = sessionManager.findTempToken(authDto.jti);
				if (tempToken.isUsed) {// TODO: or expired
					throw new AuthenticationException(
							XanAuthResponseCodes.INVALID_TOKEN,
							"Temp token or jti has been used");
				}

				tempToken.isUsed = true;
				sessionManager.put(tempToken);

				XanAuthResponseDto authResponseDto = JwtValidator
						.verifyMessage(publicKey, header64 + "." + claim64,
								sig64);
				if (!authResponseDto.isAuthorized) {//TODO: Authorization Exception
					throw new AuthenticationException(
							XanAuthResponseCodes.INVALID_TOKEN, "Invalid token");
				}

				String masterToken = CodeGenerator.generateCode(32);
				UserSession userSessionMaster = new UserSession(publicKey,
						masterToken, authDto.scope, "oulipo.master");
				sessionManager.put(userSessionMaster);
				authResponseDto.masterToken = masterToken;

				return authResponseDto;
			}

		};
	}

	/**
	 * Generate a temporary token and builds authentication URL
	 * 
	 * @param mapper
	 * @param tokenDao
	 * @return
	 */
	public static Route temporyAuthToken(XanSessionManager sessionManager) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			String scope = request.queryParams("scope");
			if (Strings.isNullOrEmpty(scope)) {
				scope = "all";
			}

			String token = sessionManager.storeNewTempToken();

			String host = "localhost:4567";

			String xanauthURL = null;
			try {
				xanauthURL = "xanauth://" + host + "/auth?token=" + token
						+ "&scope=" + URLEncoder.encode(scope, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				halt(400);
			}

			XanAuthAddressDto xanauthDto = new XanAuthAddressDto();
			xanauthDto.xanauth = xanauthURL;
			xanauthDto.token = token;

			return xanauthDto;
		};
	}
}
