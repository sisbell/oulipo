package org.oulipo.client.services;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class AuthServiceBuilder {

	private final String baseUrl;

	public AuthServiceBuilder(String baseUrl) {
		if (Strings.isNullOrEmpty(baseUrl)) {
			throw new IllegalArgumentException("baseURL must not be empty");
		}
		this.baseUrl = baseUrl;
	}

	/**
	 * Builds the Retrofit service
	 * 
	 * @return a service instance
	 */
	public AuthService build() {
		ObjectMapper mapper = new ObjectMapper();

		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(Level.BODY);

		OkHttpClient client = builder.connectTimeout(60, TimeUnit.SECONDS).retryOnConnectionFailure(true)
				.addInterceptor(logging).readTimeout(60, TimeUnit.SECONDS).build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
				.addConverterFactory(JacksonConverterFactory.create(mapper)).client(client).build();

		return retrofit.create(AuthService.class);
	}
}
