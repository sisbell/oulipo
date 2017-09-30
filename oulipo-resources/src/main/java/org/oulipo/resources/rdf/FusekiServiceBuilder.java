package org.oulipo.resources.rdf;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FusekiServiceBuilder {

	private String baseUrl = "http://localhost:3030/ds/";

	public FusekiService build() {
		ObjectMapper mapper = new ObjectMapper();

		OkHttpClient.Builder builder = new OkHttpClient.Builder();

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(Level.BODY);

		OkHttpClient client = builder.connectTimeout(60, TimeUnit.SECONDS).retryOnConnectionFailure(true)
				.addInterceptor(logging).readTimeout(60, TimeUnit.SECONDS).build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
				.addConverterFactory(ScalarsConverterFactory.create())
				.addConverterFactory(JacksonConverterFactory.create(mapper)).client(client).build();

		return retrofit.create(FusekiService.class);
	}

}
