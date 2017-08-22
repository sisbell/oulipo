package org.oulipo.security.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.oulipo.security.session.CodeGenerator;
import org.oulipo.security.session.SessionManager;
import org.oulipo.security.session.UserSession;
import org.oulipo.storage.StorageException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;

/**
 * Provides services for authentication a user and for generating session tokens
 */
public final class AuthResource {

	/**
	 * Host address (including port)
	 */
	private final String host;

	/**
	 * JSON mapper to/from objects
	 */
	private final ObjectMapper mapper;

	/**
	 * Manages user session information
	 */
	private final SessionManager sessionManager;

	/**
	 * Constructs an <code>AuthResource</code> for the specified host
	 * 
	 * @param sessionManager
	 *            the session manager for user information
	 * @param mapper
	 *            the JSON mapper
	 * @param host
	 *            the host address (including port)
	 */
	public AuthResource(SessionManager sessionManager, ObjectMapper mapper, String host) {
		if (Strings.isNullOrEmpty(host)) {
			throw new IllegalArgumentException("Host must be defined");
		}
		if (sessionManager == null) {
			throw new IllegalArgumentException("Must provide a session manager");
		}
		this.host = host;
		this.sessionManager = sessionManager;
		this.mapper = mapper == null ? new ObjectMapper() : mapper;
	}

	/**
	 * Gets a user session token
	 * 
	 * @param header64
	 * @param claim64
	 * @param sig64
	 * @return
	 * @throws StorageException
	 * @throws AuthenticationException
	 * @throws IOException
	 * @throws AuthorizationException
	 */
	public SessionResponse getSessionToken(String header64, String claim64, String sig64) throws StorageException,
			AuthenticationException, IOException, AuthorizationException {

		JwtClaim jwtClaim = mapper.readValue(BaseEncoding.base64Url().decode(claim64), JwtClaim.class);

		String publicKey = jwtClaim.iss;

		TempToken tempToken = sessionManager.findTempToken(jwtClaim.jti);
		if (tempToken.isUsed) {// TODO: or expired
			throw new AuthenticationException(AuthResponseCodes.INVALID_TOKEN, "Temp token or jti has been used");
		}

		tempToken.isUsed = true;
		sessionManager.put(tempToken);

		SessionResponse sessionResponse = JwtValidator.verifyMessage(publicKey, header64 + "." + claim64, sig64);
		if (!sessionResponse.isAuthorized) {
			throw new AuthorizationException(AuthResponseCodes.INVALID_TOKEN, "Invalid token");
		}

		String masterToken = CodeGenerator.generateCode(32);
		UserSession userSessionMaster = new UserSession(publicKey, masterToken, jwtClaim.scope, "oulipo.master");
		sessionManager.put(userSessionMaster);
		sessionResponse.masterToken = masterToken;

		return sessionResponse;
	}

	/**
	 * Gets a temporary authentication token that can be used to obtain a user
	 * session token
	 * 
	 * @param scope
	 *            the resource scope that the user is requesting
	 * @return a XanAuthTokenDto that contains the authentication URL and temporary
	 *         token
	 * @throws StorageException
	 * @throws UnsupportedEncodingException
	 */
	public TempTokenResponse temporaryAuthToken(String scope) throws StorageException, UnsupportedEncodingException {
		if (Strings.isNullOrEmpty(scope)) {
			scope = "all";
		}

		String tempToken = sessionManager.storeNewTempToken();
		String xanauthURL = "xanauth://" + host + "/auth?token=" + tempToken + "&scope="
				+ URLEncoder.encode(scope, "UTF-8");

		TempTokenResponse tempTokenResponse = new TempTokenResponse();
		tempTokenResponse.xanauth = xanauthURL;
		tempTokenResponse.token = tempToken;

		return tempTokenResponse;
	}
}
