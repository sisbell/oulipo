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

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.UnauthorizedException;
import org.oulipo.services.OulipoRequest;
import org.oulipo.services.ResourceSessionManager;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.RemoteFileManager;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.impl.DefaultOulipoMachine;

public class ContentService {

	private final RemoteFileManager remoteFileManager;

	private final ResourceSessionManager sessionManager;

	private final StreamLoader streamLoader;

	public ContentService(ResourceSessionManager sessionManager, StreamLoader streamLoader,
			RemoteFileManager remoteFileManager) {
		this.sessionManager = sessionManager;
		this.streamLoader = streamLoader;
		this.remoteFileManager = remoteFileManager;
	}

	public void loadOperations(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException,
			ResourceNotFoundException, IOException, MalformedSpanException, SignatureException {
		oulipoRequest.authenticate();
		oulipoRequest.authorize();

		TumblerAddress documentAddress = oulipoRequest.getDocumentAddress();
		OulipoMachine om = DefaultOulipoMachine.createWritableMachine(streamLoader, remoteFileManager, documentAddress);

		String hash = oulipoRequest.getBody();
		System.out.println("Load Data: " + hash);
		om.loadDocument(hash);
	}
/**
 * 		byte[] bodyBytes = BaseEncoding.base64Url().decode(base64Body);	
		OpCodeReader reader = new OpCodeReader(new DataInputStream(new ByteArrayInputStream(bodyBytes)));
 */
}
