package org.oulipo.services.endpoints;

import org.oulipo.resources.ThingRepository;
import org.oulipo.services.ResourceSessionManager;
import org.oulipo.streams.RemoteFileManager;

public final class PaymentService {

	private final RemoteFileManager remoteFileManager;

	private final ResourceSessionManager sessionManager;

	private final ThingRepository thingRepo;

	public PaymentService(ThingRepository thingRepo, ResourceSessionManager sessionManager,
			RemoteFileManager remoteFileManager) {
		this.thingRepo = thingRepo;
		this.sessionManager = sessionManager;
		this.remoteFileManager = remoteFileManager;
	}
	
	//service: tip
	//service: get all purchase orders by key of publisher document key
	//service: setup payment channel in
	//service: setup payment channel out
	
	//service: accept PO from client: this should be unenypted blob. 
	//check payment channel to see if it has funds
	//check if we have key for content requested
	//charge user from payment channel using sig
	//submit encrypted PO (with publisher docKey) to IPFS
	//send OK to client, with hash of PO. they can now request VirtualStream from document service

}
