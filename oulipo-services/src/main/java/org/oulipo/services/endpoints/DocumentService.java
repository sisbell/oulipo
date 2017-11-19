/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
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
package org.oulipo.services.endpoints;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Map;

import org.oulipo.rdf.Thing;
import org.oulipo.rdf.model.Document;
import org.oulipo.rdf.model.Virtual;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.ThingRepository;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.UnauthorizedException;
import org.oulipo.services.OulipoRequest;
import org.oulipo.services.ResourceSessionManager;
import org.oulipo.services.responses.EndsetByType;
import org.oulipo.streams.IRI;
import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.RemoteFileManager;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.impl.DefaultOulipoMachine;

public final class DocumentService {

	private final RemoteFileManager remoteFileManager;

	private final ResourceSessionManager sessionManager;

	private final StreamLoader streamLoader;

	private final ThingRepository thingRepo;

	public DocumentService(ThingRepository thingRepo, ResourceSessionManager sessionManager, StreamLoader streamLoader,
			RemoteFileManager remoteFileManager) {
		this.thingRepo = thingRepo;
		this.sessionManager = sessionManager;
		this.streamLoader = streamLoader;
		this.remoteFileManager = remoteFileManager;
	}

	public Document getDocument(OulipoRequest oulipoRequest)
			throws ResourceNotFoundException, UnauthorizedException, AuthenticationException {
		return sessionManager.getDocumentForReadAccess(oulipoRequest);
	}

	public Collection<Thing> getDocuments(OulipoRequest oulipoRequest) throws NumberFormatException {
		Map<String, String> queryParams = oulipoRequest.queryParams();
		return thingRepo.getAllDocuments(queryParams);
	}

	public EndsetByType getEndsets(OulipoRequest oulipoRequest) throws Exception {
		sessionManager.getDocumentForReadAccess(oulipoRequest);

		String documentAddress = oulipoRequest.getDocumentHash();
		OulipoMachine om = DefaultOulipoMachine.createWritableMachine(streamLoader, remoteFileManager, documentAddress);

		Collection<Thing> ispans = thingRepo.findEndsetsOfDoc(new IRI(documentAddress));

		EndsetByType endset = new EndsetByType();
		for (Thing thing : ispans) {
		}		
		//TODO: implementation
		return endset;
	}

	/**
	 * Gets a list of text partitions and invariant addresses of that text for a
	 * document. This is used for transcluded content and for paid content.
	 * 
	 * @param oulipoRequest
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws UnauthorizedException
	 * @throws AuthenticationException
	 * @throws IOException
	 * @throws MalformedSpanException
	 */
	public Virtual getVirtual(OulipoRequest oulipoRequest) throws ResourceNotFoundException, UnauthorizedException,
			AuthenticationException, IOException, MalformedSpanException {

		Document document = sessionManager.getDocumentForReadAccess(oulipoRequest);
		IRI documentAddress = document.subject;
		OulipoMachine om = DefaultOulipoMachine.createWritableMachine(streamLoader, remoteFileManager,
				documentAddress.value);

		Virtual virtual = new Virtual();
		virtual.subject = documentAddress;
		virtual.content = om.getVirtualContent();
		// TODO: need ranges
		// TODO: check that content is either free or has been paid

		return virtual;
	}

	public void loadDocument(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException,
			ResourceNotFoundException, IOException, MalformedSpanException, SignatureException {
		String documentHash = oulipoRequest.getDocumentHash();
		OulipoMachine om = DefaultOulipoMachine.createWritableMachine(streamLoader, remoteFileManager, documentHash);
		om.loadDocument(documentHash);
	}

}
