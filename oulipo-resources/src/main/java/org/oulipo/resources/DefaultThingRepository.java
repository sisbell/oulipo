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
package org.oulipo.resources;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.oulipo.net.IRI;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.InvariantLink;
import org.oulipo.resources.model.InvariantSpan;
import org.oulipo.resources.model.Node;
import org.oulipo.resources.model.Thing;
import org.oulipo.resources.model.User;

import com.github.andrewoma.dexx.collection.Maps;

public class DefaultThingRepository implements ThingRepository {

	// TODO: This assumes that InvariantSpan uses tumbler
	protected static InvariantSpan createInvariantSpan(IRI iri, SpanSource spanSource, InvariantLink link)
			throws MalformedTumblerException {

		String range = iri.value.substring(iri.value.lastIndexOf(".0.") + 3);// TODO:
																				// catch
																				// this
																				// exception
		String[] linkTokens = range.split("[~]");

		String[] startPoints = linkTokens[0].split("[.]");
		String[] endPoints = linkTokens[1].split("[.]");

		InvariantSpan ispan = new InvariantSpan();
		ispan.start = Integer.parseInt(startPoints[1]);
		ispan.width = Integer.parseInt(endPoints[1]);
		ispan.resourceId = iri;
		if (link != null) {
			if (SpanSource.FROM_LINK.equals(spanSource)) {
				ispan.addFromLink((TumblerAddress) link.resourceId);
			} else if (SpanSource.TO_LINK.equals(spanSource)) {
				ispan.addToLink((TumblerAddress) link.resourceId);
			}
		}

		ispan.document = TumblerAddress.create(iri.value.split("~")[0]).getDocumentAddress();
		return ispan;
	}

	private final DataMapper dataMapper;

	/**
	 * Constructs a <code>ThingRepository</code> with the specified
	 * <code>DataMapper</code>
	 * 
	 * @param dataMapper
	 */
	public DefaultThingRepository(DataMapper dataMapper) {
		if (dataMapper == null) {
			throw new IllegalArgumentException("dataMapper must not be null");
		}
		this.dataMapper = dataMapper;
	}

	@Override
	public void add(Thing... thing) {
		dataMapper.add(thing);
	}

	@Override
	public void addInvariantSpans(InvariantLink link, List<TumblerAddress> ispans, SpanSource source)
			throws MalformedTumblerException {
		if (ispans == null) {
			return;
		}

		for (IRI iri : ispans) {
			dataMapper.add(createInvariantSpan(iri, source, link));
		}
	}

	@Override
	public Document findDocument(TumblerAddress address) throws ResourceNotFoundException {
		return findDocument(address, "Document not found");
	}

	@Override
	public Document findDocument(TumblerAddress address, String message) throws ResourceNotFoundException {
		Document thing = (Document) dataMapper.get(address);
		if (thing == null) {
			throw new ResourceNotFoundException(address, ResourceErrorCodes.DOCUMENT_NOT_FOUND, message);
		}
		return thing;
	}

	@Override
	public Optional<Document> findDocumentOpt(TumblerAddress address) {
		Document document = (Document) dataMapper.get(address);
		return document == null ? Optional.empty() : Optional.of(document);
	}

	@Override
	public Collection<Thing> findEndsetsOfDoc(TumblerAddress docAddress) throws Exception {
		return dataMapper.findEndsetsOfDoc(docAddress);
	}

	@Override
	public Optional<InvariantLink> findInvariantLink(TumblerAddress tumbler) {
		InvariantLink thing = (InvariantLink) dataMapper.get(tumbler);
		return thing != null ? Optional.of(thing) : Optional.empty();
	}

	@Override
	public InvariantLink findInvariantLink(TumblerAddress address, String message) throws ResourceNotFoundException {
		InvariantLink thing = (InvariantLink) dataMapper.get(address);
		if (thing == null) {
			throw new ResourceNotFoundException(address, ResourceErrorCodes.LINK_NOT_FOUND, message);

		}
		return thing;
	}

	@Override
	public Optional<InvariantLink> findInvariantLinkOpt(TumblerAddress address) throws ResourceNotFoundException {
		return findInvariantLink(address);
	}

	@Override
	public InvariantSpan findInvariantSpan(TumblerAddress tumbler) throws ResourceNotFoundException {
		InvariantSpan thing = (InvariantSpan) dataMapper.get(tumbler);
		if (thing == null) {
			throw new ResourceNotFoundException(null, 300, "Not found:" + tumbler.value);
		}
		return thing;
	}

	@Override
	public Node findNode(TumblerAddress address) throws ResourceNotFoundException {
		return findNode(address, "Node does not exist");
	}

	@Override
	public Node findNode(TumblerAddress address, String message) throws ResourceNotFoundException {
		Node thing = (Node) dataMapper.get(address);
		if (thing == null) {
			throw new ResourceNotFoundException(address, ResourceErrorCodes.NODE_NOT_FOUND, message);
		}
		return thing;
	}

	@Override
	public User findUser(TumblerAddress address) throws ResourceNotFoundException {
		User thing = (User) dataMapper.get(address);
		if (thing == null) {
			throw new ResourceNotFoundException(address, ResourceErrorCodes.USER_NOT_FOUND, "User not found");
		}
		return thing;
	}

	@Override
	public User findUser(TumblerAddress address, String message) throws ResourceNotFoundException {
		User thing = (User) dataMapper.get(address);
		if (thing == null) {
			throw new ResourceNotFoundException(address, ResourceErrorCodes.USER_NOT_FOUND, message);
		}
		return thing;
	}

	@Override
	public Optional<User> findUserByXandle(int network, String xandle) throws Exception {
		Collection<Thing> things = getAllUsers(network, Maps.of("xandle", xandle).asMap());
		if (things == null || things.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of((User) things.iterator().next());
	}

	@Override
	public Collection<Thing> getAllDocuments(int network, Map<String, String> queryParams) {
		return getAllThings(network, "Document", queryParams);
	}

	@Override
	public Collection<Thing> getAllInvariantLinks(int network, Map<String, String> queryParams) {
		return getAllThings(network, "Link", queryParams);
	}

	@Override
	public Collection<Thing> getAllNodes(int network, Map<String, String> params) {
		return getAllThings(network, "Node", params);
	}

	@Override
	public Collection<Thing> getAllThings(int network, String type, Map<String, String> queryParams) {
		return dataMapper.getAllThings(network, type, queryParams);
	}

	@Override
	public Collection<Thing> getAllUsers(int network, Map<String, String> queryParams) {
		return getAllThings(network, "User", queryParams);
	}

	@Override
	public String getPublicKeyOfNode(TumblerAddress node) throws Exception {
		return dataMapper.getPublicKeyOfNode(node);
	}

	@Override
	public void removeInvariantSpansOfLink(InvariantLink link) {

	}

	@Override
	public void update(Thing thing) {
		dataMapper.update(thing);
	}

}
