package org.oulipo.browser.api.people;

import org.oulipo.storage.Id;

public class Account {

	public String bitcoinPayoutAddress;

	public String familyName;

	public String givenName;
	
	@Id
	public String publicKey;
	
	public String xandle;
	
	public String imageHash;
		
}
