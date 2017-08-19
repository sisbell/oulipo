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

import static com.google.common.io.BaseEncoding.base64Url;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.json.JSONException;
import org.json.JSONObject;

public final class JwtBuilder {

	public static String buildRequest(ECKey mKey, XanAuthUri xanAuthUri)
			throws UnsupportedEncodingException, NoSuchAlgorithmException,
			JSONException, InvalidKeyException {

		JSONObject claims = new JSONObject();
		long iat = (System.currentTimeMillis() / 1000L);
		String address = URLDecoder.decode(mKey.toAddress(MainNetParams.get())
				.toString(), "UTF-8");

		claims.put("iss", address);
		claims.put("iat", iat);
		claims.put("jti", xanAuthUri.getToken());
		claims.put("exp", iat + 180L);
		claims.put("aud", xanAuthUri.getRawUri());
		claims.put("scope", "login");

		return signAndBuildWebToken(claims, mKey);
	}

	private static String signAndBuildWebToken(JSONObject claims, ECKey mKey)
			throws InvalidKeyException, NoSuchAlgorithmException,
			JSONException, UnsupportedEncodingException {
		String message = buildHeaderAndClaims(claims);
		String signature64 = mKey.signMessage(message);
		return message + "." + signature64;
	}

	private static String buildHeaderAndClaims(JSONObject claims)
			throws InvalidKeyException, NoSuchAlgorithmException,
			JSONException, UnsupportedEncodingException {
		JSONObject header = new JSONObject();
		header.put("alg", "ES256");
		header.put("typ", "JWT");
		StringBuilder sb = new StringBuilder();
		return sb
				.append(base64Url().encode(header.toString().getBytes("UTF-8")))
				.append(".")
				.append(base64Url().encode(claims.toString().getBytes("UTF-8")))
				.toString();
	}
}
