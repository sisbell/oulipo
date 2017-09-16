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
package org.oulipo.services;

import java.io.IOException;
import java.util.Collection;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.resources.ResourceFoundException;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.ThingRepository;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.Link;
import org.oulipo.resources.model.Node;
import org.oulipo.resources.model.Thing;
import org.oulipo.resources.model.User;
import org.oulipo.resources.model.Virtual;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.UnauthorizedException;
import org.oulipo.services.endpoints.ContentService;
import org.oulipo.services.endpoints.DocumentService;
import org.oulipo.services.endpoints.ElementsService;
import org.oulipo.services.endpoints.EndsetsService;
import org.oulipo.services.endpoints.NetworkService;
import org.oulipo.services.endpoints.NodeService;
import org.oulipo.services.endpoints.UserService;
import org.oulipo.services.responses.EndsetByType;
import org.oulipo.services.responses.Network;
import org.oulipo.streams.StreamLoader;

/**
 * Main service for making Oulipo requests.
 * 
 * @author sisbell
 *
 */
public class OulipoRequestService {

	private ContentService contentService;

	private DocumentService documentService;

	private ElementsService elementsService;

	private EndsetsService endsetsService;

	private NetworkService networkService;

	private NodeService nodeService;

	private final ThingRepository thingRepo;

	private UserService userService;

	public OulipoRequestService(ThingRepository thingRepo, ResourceSessionManager sessionManager,
			StreamLoader streamLoader) {
		this.thingRepo = thingRepo;
		userService = new UserService(thingRepo);
		networkService = new NetworkService();
		nodeService = new NodeService(thingRepo);
		contentService = new ContentService(sessionManager, streamLoader);
		documentService = new DocumentService(thingRepo, sessionManager, streamLoader);
		elementsService = new ElementsService(thingRepo, sessionManager);
		endsetsService = new EndsetsService(thingRepo, sessionManager);

	}

	public String copyContent(OulipoRequest oulipoRequest)
			throws AuthenticationException, UnauthorizedException, ResourceNotFoundException, IOException,
			MalformedSpanException, EditResourceException, MissingBodyException {
		return contentService.copyContent(oulipoRequest);
	}

	public Document createDocument(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException,
			ResourceNotFoundException, MissingBodyException, IOException {
		return documentService.createDocument(oulipoRequest);
	}

	public Node createNode(OulipoRequest oulipoRequest) throws Exception {
		return nodeService.createNode(oulipoRequest);
	}

	public Link createOrUpdateLink(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException,
			ResourceNotFoundException, MissingBodyException, IOException {
		return elementsService.createOrUpdateLink(oulipoRequest);
	}

	public User createOrUpdateUser(OulipoRequest oulipoRequest) throws Exception {
		return userService.createOrUpdateUser(oulipoRequest);
	}
	
	public String deleteContent(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException,
			ResourceNotFoundException, IOException, MalformedSpanException, EditResourceException {
		return contentService.deleteContent(oulipoRequest);
	}

	public Collection<Thing> getAllDocuments(int network, OulipoRequest request) {
		return thingRepo.getAllThings(network, "Document", request.queryParams());
	}

	public Collection<Thing> getAllDocuments(OulipoRequest request) throws MalformedTumblerException {
		return getAllDocuments(request.getNetworkIdAsInt(), request);
	}

	public Collection<Thing> getAllInvariantLinks(int network, OulipoRequest request) {
		return thingRepo.getAllThings(network, "Link", // TODO: make sure name is Link (not Invariant Link)
				request.queryParams());
	}

	public Collection<Thing> getAllInvariantLinks(OulipoRequest request) throws MalformedTumblerException {
		return getAllInvariantLinks(request.getNetworkIdAsInt(), request);
	}

	public Collection<Thing> getAllInvariantSpans(int network, OulipoRequest request) {
		return thingRepo.getAllThings(network, "InvariantSpan", request.queryParams());
	}

	public Collection<Thing> getAllInvariantSpans(OulipoRequest request) throws MalformedTumblerException {
		return getAllInvariantSpans(request.getNetworkIdAsInt(), request);
	}

	public Collection<Thing> getAllNodes(int network, OulipoRequest request) {
		return thingRepo.getAllThings(network, "Node", request.queryParams());
	}

	public Collection<Thing> getAllNodes(OulipoRequest request) throws MalformedTumblerException {
		return getAllNodes(request.getNetworkIdAsInt(), request);
	}

	public Collection<Thing> getAllUsers(int network, OulipoRequest request) {
		return thingRepo.getAllThings(network, "Person", request.queryParams());
	}

	public Collection<Thing> getAllUsers(OulipoRequest request) throws MalformedTumblerException {
		return getAllUsers(request.getNetworkIdAsInt(), request);
	}

	public Document getDocument(OulipoRequest oulipoRequest) throws MalformedTumblerException,
			ResourceNotFoundException, UnauthorizedException, AuthenticationException {
		return documentService.getDocument(oulipoRequest);
	}

	public Collection<Thing> getDocumentLinks(OulipoRequest oulipoRequest) {
		// TODO: fix
		return null;
		// return documentService.getDocumentLinks(oulipoRequest);
	}

	public Thing getElement(OulipoRequest oulipoRequest) throws MalformedTumblerException, ResourceNotFoundException,
			UnauthorizedException, AuthenticationException {
		return elementsService.getElement(oulipoRequest);
	}

	public EndsetByType getEndsets(OulipoRequest oulipoRequest) throws Exception {
		return endsetsService.getEndsets(oulipoRequest);
	}

	public Network getNetwork(OulipoRequest oulipoRequest) throws MalformedTumblerException, ResourceNotFoundException {
		return networkService.getNetwork(oulipoRequest);
	}

	public Node getNode(OulipoRequest oulipoRequest) throws ResourceNotFoundException, MalformedTumblerException {
		return nodeService.getNode(oulipoRequest);
	}

	public Collection<Thing> getNodeUsers(OulipoRequest oulipoRequest) throws MalformedTumblerException {
		return nodeService.getNodeUsers(oulipoRequest);
	}

	public Collection<Thing> getSystemDocuments(OulipoRequest oulipoRequest) throws MalformedTumblerException {
		return documentService.getSystemDocuments(oulipoRequest);
	}

	public Collection<Thing> getSystemLinks(OulipoRequest oulipoRequest) throws MalformedTumblerException {
		return elementsService.getSystemLinks(oulipoRequest);
	}

	public Collection<Thing> getSystemNodes(OulipoRequest oulipoRequest) throws MalformedTumblerException {
		return nodeService.getSystemNodes(oulipoRequest);
	}

	public Collection<Thing> getSystemUsers(OulipoRequest oulipoRequest) throws MalformedTumblerException {
		return userService.getSystemUsers(oulipoRequest);
	}

	public Collection<Thing> getSystemVSpans(OulipoRequest oulipoRequest) throws MalformedTumblerException {
		return elementsService.getSystemVSpans(oulipoRequest);
	}

	public User getUser(OulipoRequest oulipoRequest) throws ResourceNotFoundException, MalformedTumblerException {
		return userService.getUser(oulipoRequest);
	}

	public Collection<Thing> getUserDocuments(OulipoRequest oulipoRequest) throws MalformedTumblerException {
		return userService.getUserDocuments(oulipoRequest);
	}

	public Virtual getVirtual(OulipoRequest oulipoRequest) throws ResourceNotFoundException, UnauthorizedException,
			AuthenticationException, IOException, MalformedSpanException {
		return documentService.getVirtual(oulipoRequest);
	}

	public String insertContent(OulipoRequest oulipoRequest) throws AuthenticationException, UnauthorizedException,
			ResourceNotFoundException, IOException, MalformedSpanException, EditResourceException {
		return contentService.insertContent(oulipoRequest);
	}

	public Document newDocument(OulipoRequest oulipoRequest) throws MalformedTumblerException,
			ResourceNotFoundException, AuthenticationException, UnauthorizedException, ResourceFoundException {
		return documentService.newVersion(oulipoRequest);
	}

	public User newUser(OulipoRequest oulipoRequest) throws Exception {
		return userService.newUser(oulipoRequest);
	}

	public Document newVersion(OulipoRequest oulipoRequest) throws MalformedTumblerException, ResourceNotFoundException,
			AuthenticationException, UnauthorizedException, ResourceFoundException {
		return documentService.newVersion(oulipoRequest);
	}

	public String swap(OulipoRequest oulipoRequest)
			throws AuthenticationException, UnauthorizedException, ResourceNotFoundException, IOException,
			MalformedSpanException, EditResourceException, MissingBodyException {
		return contentService.swap(oulipoRequest);
	}

}
