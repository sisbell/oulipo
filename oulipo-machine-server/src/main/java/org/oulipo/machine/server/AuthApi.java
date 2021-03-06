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
package org.oulipo.machine.server;

import static spark.Spark.halt;

import java.io.InputStream;

import org.oulipo.security.auth.AuthResource;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import spark.Route;

/**
 * Provides services for authentication a user and for generating session tokens
 */
public final class AuthApi {

	public static Route getSessionToken(AuthResource resource) {
		return (request, response) -> {
			response.header("content-type", "application/json");

			try (InputStream is = request.raw().getInputStream()) {
				String message = new String(ByteStreams.toByteArray(is), Charsets.UTF_8);
				String[] tokens = message.split("[.]");
				if (tokens.length != 3) {
					halt(400);
				}

				String header64 = tokens[0];
				String claim64 = tokens[1];
				String sig64 = tokens[2];

				return resource.getSessionToken(header64, claim64, sig64);
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
	public static Route temporyAuthToken(AuthResource resource) {
		return (request, response) -> {
			response.header("content-type", "application/json");
			return resource.temporaryAuthToken(request.queryParams("scope"));
		};
	}
}
