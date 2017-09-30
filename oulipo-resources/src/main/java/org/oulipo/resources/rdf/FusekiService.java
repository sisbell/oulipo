package org.oulipo.resources.rdf;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FusekiService {

	@FormUrlEncoded
	@POST("query")
	@Headers({ "Content-Type:application/x-www-form-urlencoded; charset=UTF-8",
			"Accept:application/sparql-results+json,*/*;q=0.9" })
	Call<FusekiResponse> query(@Field("query") String query);

	@FormUrlEncoded
	@POST("update")
	@Headers({ "Content-Type:application/x-www-form-urlencoded; charset=UTF-8",
			"Accept:application/sparql-results+json,*/*;q=0.9" })
	Call<FusekiResponse> update(@Field("update") String body);

}
