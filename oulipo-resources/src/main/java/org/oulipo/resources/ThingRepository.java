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

import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.InvariantLink;
import org.oulipo.resources.model.InvariantSpan;
import org.oulipo.resources.model.Node;
import org.oulipo.resources.model.Thing;
import org.oulipo.resources.model.User;

/**
 * Service for finding and updating Things.
 */
public interface ThingRepository {

	public enum SpanSource {
		DOCUMENT, FROM_LINK, TO_LINK
	}

	void add(Thing... thing);

	void addInvariantSpans(InvariantLink link, List<TumblerAddress> ispans, SpanSource source)
			throws MalformedTumblerException;

	Document findDocument(TumblerAddress address) throws ResourceNotFoundException;

	Document findDocument(TumblerAddress address, String message) throws ResourceNotFoundException;

	Optional<Document> findDocumentOpt(TumblerAddress address);

	Collection<Thing> findEndsetsOfDoc(TumblerAddress docId) throws Exception;

	Optional<InvariantLink> findInvariantLink(TumblerAddress tumbler);

	InvariantLink findInvariantLink(TumblerAddress address, String message) throws ResourceNotFoundException;

	Optional<InvariantLink> findInvariantLinkOpt(TumblerAddress address) throws ResourceNotFoundException;

	InvariantSpan findInvariantSpan(TumblerAddress tumbler) throws ResourceNotFoundException;

	Node findNode(TumblerAddress address) throws ResourceNotFoundException;

	Node findNode(TumblerAddress address, String message) throws ResourceNotFoundException;

	User findUser(TumblerAddress address) throws ResourceNotFoundException;

	User findUser(TumblerAddress address, String message) throws ResourceNotFoundException;

	Optional<User> findUserByXandle(int network, String xandle) throws Exception;

	Collection<Thing> getAllDocuments(int network, Map<String, String> queryParams);

	Collection<Thing> getAllInvariantLinks(int network, Map<String, String> queryParams);

	Collection<Thing> getAllNodes(int network, Map<String, String> params);

	Collection<Thing> getAllThings(int network, String type, Map<String, String> queryParams);

	Collection<Thing> getAllUsers(int network, Map<String, String> queryParams);

	String getPublicKeyOfNode(TumblerAddress node) throws Exception;

	void removeInvariantSpansOfLink(InvariantLink link);

	void update(Thing thing);

}