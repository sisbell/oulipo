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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.bitcoinj.core.ECKey;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Strings;

public final class XanAuthUri {

	public static class Builder {

		private boolean isSecured;

		private ECKey key;

		private String rawUri;

		public XanAuthUri build() throws UnsupportedEncodingException {
			XanAuthUri xanAuth = new XanAuthUri();
			if (Strings.isNullOrEmpty(rawUri)) {
				throw new IllegalArgumentException("No URI specified");
			}

			if (key == null) {
				throw new IllegalArgumentException("No key specified");
			}

			xanAuth.uri = URI.create(rawUri);
			xanAuth.isSecured = isSecured;
			xanAuth.rawUri = rawUri;
			xanAuth.key = key;
			xanAuth.token = split(xanAuth.uri).get("token");
			return xanAuth;
		}

		public Builder isSecured(boolean isSecured) {
			this.isSecured = isSecured;
			return this;
		}

		public Builder key(ECKey key) {
			this.key = key;
			return this;
		}

		public Builder uri(String uri) {
			this.rawUri = uri;
			return this;
		}
	}

	public final class ResultCode {

		public static final int NO_CONNECTION = -1;

		public static final int OK = 0;

		public static final int UNKNOWN_ERROR = 199;

	}

	private static String asString(InputStream inputStream) throws IOException {
		try {
			return new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
		} finally {
			inputStream.close();
		}
	}

	private static Map<String, String> split(URI uri) throws UnsupportedEncodingException {
		Map<String, String> queryMap = new HashMap<>();
		String[] kvps = uri.getQuery().split("&");
		for (String kvp : kvps) {
			String[] tokens = kvp.split("[=]");
			queryMap.put(URLDecoder.decode(tokens[0], "UTF-8"), URLDecoder.decode(tokens[1], "UTF-8"));
		}
		return queryMap;
	}

	private HttpURLConnection connection;

	private boolean isSecured;

	private ECKey key;

	private String rawUri;

	private String token;

	private URI uri;

	private XanAuthUri() {
	}

	private String buildRequest()
			throws JSONException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
		return JwtBuilder.buildRequest(key, this);
	}

	public String getRawUri() {
		return rawUri;
	}

	public String getToken() {
		return token;
	}

	public SessionResponse makeRequest() {
		try {
			openConnection();
			writeRequest(buildRequest());
			return readResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new SessionResponse(ResultCode.UNKNOWN_ERROR, null);
	}

	private void openConnection() throws IOException, URISyntaxException {
		connection = (HttpURLConnection) toCallbackURI().toURL().openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);

		connection.connect();
	}

	private SessionResponse readResponse() throws IOException {
		int rc = connection.getResponseCode();
		if (rc == -1) {
			return new SessionResponse(ResultCode.NO_CONNECTION, null);
		}
		if (rc < 300 && rc >= 200) {
			JSONObject jo = new JSONObject(asString(connection.getInputStream()));
			SessionResponse response = new SessionResponse(ResultCode.OK, "OK");
			response.isAuthorized = true;
			response.publicKey = jo.getString("publicKey");
			response.masterToken = jo.getString("masterToken");	
			response.expiresIn = jo.getLong("expiresIn");
			return response;
		} else if (rc >= 400) {
			String message = asString(connection.getErrorStream());
			try {
				JSONObject jo = new JSONObject(message);
				return new SessionResponse(jo.getInt("code"), jo.getString("message"));
			} catch (JSONException e) {
				e.printStackTrace();
				System.out.println(message);
				return new SessionResponse(-1, e.getMessage());
			}
		} else {
			return new SessionResponse(ResultCode.UNKNOWN_ERROR, null);
		}
	}

	public URI toCallbackURI() throws URISyntaxException {
		return new URI(isSecured ? "https" : "http", null, uri.getHost(), uri.getPort(), uri.getPath(), null, null);
	}

	@Override
	public String toString() {
		String url = null;
		try {
			url = toCallbackURI().toString();
		} catch (URISyntaxException e) {

		}

		return "XanAuthUri{" + "mRawUri='" + rawUri + '\'' + ", callbackUri=" + url + '}';
	}

	private void writeRequest(String message) throws IOException {
		DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
		dos.write(message.getBytes());
		dos.close();
	}
}
