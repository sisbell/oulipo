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
import java.util.Map;
import java.util.Optional;

import org.oulipo.rdf.Thing;
import org.oulipo.rdf.model.Document;
import org.oulipo.rdf.model.User;
import org.oulipo.streams.IRI;

public class DefaultThingRepository implements ThingRepository {

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
	public Document findDocument(IRI documentHash) throws ResourceNotFoundException {
		return findDocument(documentHash, "Document not found");
	}

	@Override
	public Document findDocument(IRI documentHash, String message) throws ResourceNotFoundException {
		Document thing = (Document) dataMapper.get(documentHash);
		if (thing == null) {
			throw new ResourceNotFoundException(null, ResourceErrorCodes.DOCUMENT_NOT_FOUND, message);
		}
		return thing;
	}

	@Override
	public Optional<Document> findDocumentOpt(IRI documentHash) {
		Document document = (Document) dataMapper.get(documentHash);
		return document == null ? Optional.empty() : Optional.of(document);
	}

	@Override
	public Collection<Thing> findEndsetsOfDoc(IRI docAddress) throws Exception {
		return dataMapper.findEndsetsOfDoc(docAddress);
	}

	@Override
	public User findUser(IRI address) throws ResourceNotFoundException {
		User thing = (User) dataMapper.get(address);
		if (thing == null) {
			throw new ResourceNotFoundException(address, ResourceErrorCodes.USER_NOT_FOUND, "User not found");
		}
		return thing;
	}

	@Override
	public User findUser(IRI address, String message) throws ResourceNotFoundException {
		User thing = (User) dataMapper.get(address);
		if (thing == null) {
			throw new ResourceNotFoundException(address, ResourceErrorCodes.USER_NOT_FOUND, message);
		}
		return thing;
	}

	@Override
	public Collection<Thing> getAllDocuments(Map<String, String> queryParams) {
		return getAllThings("Document", queryParams);
	}

	@Override
	public Collection<Thing> getAllThings(String type, Map<String, String> queryParams) {
		return dataMapper.getAllThings(type, queryParams);
	}

	@Override
	public Collection<Thing> getAllUsers(Map<String, String> queryParams) {
		return getAllThings("User", queryParams);
	}

	@Override
	public void update(Thing thing) {
		dataMapper.update(thing);
	}

}
