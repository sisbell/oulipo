package org.oulipo.browser.api.people;

import org.oulipo.storage.Id;

public class UserSession {

	@Id
	public String publicKey;
	
	public String host;
	
	public String sessionToken;
}
