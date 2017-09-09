package org.oulipo.client.services;

import org.oulipo.security.auth.TempTokenResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AuthService {

	@GET("/auth")
	Call<TempTokenResponse> getTempToken();

}
