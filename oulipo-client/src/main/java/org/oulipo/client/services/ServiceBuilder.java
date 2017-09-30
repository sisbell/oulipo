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
package org.oulipo.client.services;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Builds retrofit service interfaces
 */
public final class ServiceBuilder {

	/**
	 * The base URL of this service
	 */
	private final String baseUrl;

	/**
	 * Public key of the user
	 */
	private String publicKey;

	/**
	 * Session token for the user
	 */
	private String sessionToken;

	/**
	 * Constructs a ServiceBuilder using the specified baseUrl
	 * 
	 * @param baseUrl
	 *            the base URL of any service
	 */
	public ServiceBuilder(String baseUrl) {
		if (Strings.isNullOrEmpty(baseUrl)) {
			throw new IllegalArgumentException("baseURL must not be empty");
		}
		this.baseUrl = baseUrl;
	}

	/**
	 * Sets the session token for the user. This will be added as a request header:
	 * x-oulipo-token
	 * 
	 * A session token grants access to the user's resource for the life of the
	 * token
	 * 
	 * @param sessionToken
	 *            the session token for the user
	 * @return this ServiceBuilder
	 */
	public ServiceBuilder sessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
		return this;
	}

	/**
	 * Sets the public key of the user.This will be added as a request header:
	 * x-oulipo-user
	 * 
	 * @param publicKey
	 *            the public key of the user
	 * @return this ServiceBuilder
	 */
	public ServiceBuilder publicKey(String publicKey) {
		this.publicKey = publicKey;
		return this;
	}

	/**
	 * Builds the Retrofit service
	 * 
	 * @param service
	 *            the service class
	 * @return a service instance
	 */
	public <T> T build(Class<T> service) {
		ObjectMapper mapper = new ObjectMapper();

		OkHttpClient.Builder builder = new OkHttpClient.Builder();

		if (!Strings.isNullOrEmpty(publicKey) && !Strings.isNullOrEmpty(sessionToken))
			builder.addInterceptor(chain -> {
				Request request = chain.request().newBuilder().addHeader("x-oulipo-token", sessionToken)
						.addHeader("x-oulipo-user", publicKey).build();
				return chain.proceed(request);
			});

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(Level.BODY);

		OkHttpClient client = builder.connectTimeout(60, TimeUnit.SECONDS).retryOnConnectionFailure(true)
				.addInterceptor(logging).readTimeout(60, TimeUnit.SECONDS).build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
				.addConverterFactory(ScalarsConverterFactory.create())
				.addConverterFactory(JacksonConverterFactory.create(mapper)).client(client).build();

		return retrofit.create(service);
	}
}
