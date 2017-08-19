/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.  
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
package org.oulipo.machine.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.oulipo.machine.server.RdfMapper.TemplateBuilder;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.AbstractThingRepository;
import org.oulipo.resources.ThingMapper;
import org.oulipo.resources.model.Thing;
import org.oulipo.resources.rdf.Statement;
import org.oulipo.resources.responses.FusekiResponse;

import freemarker.template.TemplateException;
import spark.Request;

public class RdfRepository extends AbstractThingRepository {

	private RdfMapper rdfMapper;

	public RdfRepository(RdfMapper rdfMapper) {
		super(rdfMapper);
		this.rdfMapper = rdfMapper;
	}

	public Collection<Thing> findEndsetsOfDoc(TumblerAddress docId)
			throws Exception {
		TemplateBuilder builder = rdfMapper.template("endsets.sparql")
				.addParam("documentId", docId);
		return rdfMapper.getQueryEngine().things(builder.build());
	}

	public Collection<Thing> getAllDocuments(int network, Request request) {
		return getAllThings(network, "Document",
				RdfMapper.getQueryMapFrom(request));
	}

	public Collection<Thing> getAllDocuments(Request request) {
		return getAllDocuments(Integer.parseInt(request.params(":networkId")),
				request);
	}

	public Collection<Thing> getAllInvariantLinks(int network, Request request) {
		return getAllThings(network, "Link",//TODO: make sure name is Link (not Invariant Link)
				RdfMapper.getQueryMapFrom(request));
	}

	public Collection<Thing> getAllInvariantLinks(Request request) {
		return getAllInvariantLinks(Integer.parseInt(request.params(":networkId")),
				request);
	}
	
	public Collection<Thing> getAllNodes(int network, Request request) {
		return getAllThings(network, "Node",
				RdfMapper.getQueryMapFrom(request));
	}

	public Collection<Thing> getAllNodes(Request request) {
		return getAllNodes(Integer.parseInt(request.params(":networkId")),
				request);
	}

	public Collection<Thing> getAllThings(int network, String type,
			Map<String, String> queryParams) {
		return rdfMapper.getByTypeAndQueries(network, type, queryParams);
	}

	public Collection<Thing> getAllUsers(int network, Request request) {
		return getAllThings(network, "Person",
				RdfMapper.getQueryMapFrom(request));
	}

	public Collection<Thing> getAllUsers(Request request) {
		return getAllUsers(Integer.parseInt(request.params(":networkId")),
				request);
	}

	public Collection<Thing> getAllInvariantSpans(int network, Request request) {
		return getAllThings(network, "InvariantSpan",
				RdfMapper.getQueryMapFrom(request));
	}

	public Collection<Thing> getAllInvariantSpans(Request request) {
		return getAllInvariantSpans(Integer.parseInt(request.params(":networkId")),
				request);
	}
	/**
	 * Gets all field values for the specified fieldName of the specified
	 * resourceId
	 * 
	 * @param resourceId
	 *            resourceId of the entity or thing
	 * @param fieldName
	 *            field name to look for. This matches the RDF defined field
	 *            name
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public List<String> getFieldValuesOfResource(String resourceId,
			String fieldName) throws IOException, TemplateException {
		TemplateBuilder builder = rdfMapper
				.template("thingsByResourceIdAndFieldName.sparql")
				.addParam("resourceId", resourceId)
				.addParam("fieldName", fieldName);

		FusekiResponse fr = rdfMapper.getQueryEngine().fusekiResponse(
				builder.build());

		List<String> values = new ArrayList<>();
		for (Statement statement : fr.results.bindings) {
			values.add(statement.getObject().getValue());
		}
		return values;
	}

	public String getPublicKeyOfNode(String nodeId) throws Exception {
		List<String> pks = getFieldValuesOfResource(nodeId, "publicKey");
		if (pks == null || pks.isEmpty()) {
			return null;
		}
		return pks.get(0);
	}

	public String getPublicKeyOfNode(TumblerAddress nodeId) throws Exception {
		return getPublicKeyOfNode(nodeId.toTumblerAuthority());
	}

	public ThingMapper getThingMapper() {
		return rdfMapper;
	}

}
