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

/**
 * Service for finding and updating Things.
 */
public interface ThingRepository {

	public enum SpanSource {
		DOCUMENT, FROM_LINK, TO_LINK
	}

	void add(Thing... thing);

	Document findDocument(IRI address) throws ResourceNotFoundException;

	Document findDocument(IRI address, String message) throws ResourceNotFoundException;

	Optional<Document> findDocumentOpt(IRI address);

	Collection<Thing> findEndsetsOfDoc(IRI docId) throws Exception;

	User findUser(IRI address) throws ResourceNotFoundException;

	User findUser(IRI address, String message) throws ResourceNotFoundException;

	Collection<Thing> getAllDocuments(Map<String, String> queryParams);

	Collection<Thing> getAllThings(String type, Map<String, String> queryParams);

	Collection<Thing> getAllUsers(Map<String, String> queryParams);

	void update(Thing thing);

}