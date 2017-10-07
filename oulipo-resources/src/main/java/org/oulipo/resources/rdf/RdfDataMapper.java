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
package org.oulipo.resources.rdf;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.oulipo.net.IRI;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.rdf.Statement;
import org.oulipo.resources.DataMapper;
import org.oulipo.resources.model.Thing;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import retrofit2.Response;

/**
 * Maps between Things and RDF representations
 */
public final class RdfDataMapper implements DataMapper {

	public class QueryEngine {

		public FusekiResponse fusekiResponse(String query)
				throws JsonParseException, JsonMappingException, IOException {
			return mapper.readValue(rawQuery(query), FusekiResponse.class);
		}

		public String rawQuery(String query) throws IOException {
			FusekiResponse response = fusekiService.query(query).execute().body();
			return new ObjectMapper().writeValueAsString(response);
		}

		public boolean rawUpdate(String update) {
			try {
				Response<FusekiResponse> response = fusekiService.update(update).execute();
				if(response.isSuccessful()) {
					return true;
				}
			} catch (Exception e) {

			}
			return false;
		}

		public Collection<Thing> things(String query) throws Exception {
			return StatementsToThing.transformThings(fusekiResponse(query).results.bindings);
		}
	}

	public class TemplateBuilder {

		private final Map<String, Object> params = new HashMap<>();

		private final String template;

		private TemplateBuilder(String template) {
			this.template = template;
		}

		public TemplateBuilder addParam(String name, Map<String, String> value) {
			params.put(name, value);
			return this;
		}

		public TemplateBuilder addParam(String name, String value) {
			params.put(name, value);
			return this;
		}

		public TemplateBuilder addParam(String name, TumblerAddress value) throws MalformedTumblerException {
			params.put(name, value.toTumblerAuthority());
			return this;
		}

		public String build() throws IOException, TemplateException {
			Template temp = cfg.getTemplate(template);
			StringWriter query = new StringWriter();
			temp.process(params, query);
			return query.toString();
		}
	}

	private static final Logger LOG = Logger.getLogger("ThingMapper");

	protected static Pattern standardEntities = Pattern.compile("&|<|>|\t|\n|\r|\'|\"");

	private final Configuration cfg;

	private QueryEngine engine = new QueryEngine();

	private FusekiService fusekiService;

	private ObjectMapper mapper = new ObjectMapper();

	public RdfDataMapper(Configuration cfg) {
		this.cfg = cfg;
		this.fusekiService = new FusekiServiceBuilder().build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.oulipo.machine.server.IThingMapper#add(java.util.Collection)
	 */
	@Override
	public void add(Collection<Thing> things) {
		try {
			getQueryEngine()
					.rawUpdate("PREFIX : <http://oulipo.org/>INSERT DATA {" + ThingToString.asStrings(things) + "}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.oulipo.machine.server.IThingMapper#delete(java.util.Collection)
	 */
	@Override
	public void delete(Collection<Thing> things) {
		try {
			this.getQueryEngine()
					.rawUpdate("PREFIX : <http://oulipo.org/>DELETE DATA {" + ThingToString.asStrings(things) + "}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Collection<Thing> findEndsetsOfDoc(TumblerAddress docAddress) throws Exception {
		TemplateBuilder builder = template("endsets.sparql").addParam("documentId", docAddress);
		return getQueryEngine().things(builder.build());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.oulipo.machine.server.IThingMapper#get(org.oulipo.net.IRI)
	 */
	@Override
	public Thing get(IRI address) {
		TemplateBuilder builder = template("thingByResourceId.sparql").addParam("resourceId", address.value);
		Collection<Thing> things;
		try {
			things = getQueryEngine().things(builder.build());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if (things.isEmpty()) {
			return null;
		}
		return things.iterator().next();
	}

	@Override
	public Collection<Thing> getAllThings(int network, String type, Map<String, String> queryParams) {
		return getByTypeAndQueries(network, type, queryParams);
	}

	public Collection<Thing> getByTypeAndQueries(int network, String type, Map<String, String> queryParams) {
		TemplateBuilder builder = template("thingsByTypeAndQueryParams.sparql").addParam("type", type)
				.addParam("networkId", String.valueOf(network)).addParam("queryParams", queryParams);
		try {
			return getQueryEngine().things(builder.build());
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**
	 * Gets all field values for the specified fieldName of the specified resourceId
	 * 
	 * @param resourceId
	 *            resourceId of the entity or thing
	 * @param fieldName
	 *            field name to look for. This matches the RDF defined field name
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	private List<String> getFieldValuesOfResource(String resourceId, String fieldName)
			throws IOException, TemplateException {
		TemplateBuilder builder = template("thingsByResourceIdAndFieldName.sparql").addParam("resourceId", resourceId)
				.addParam("fieldName", fieldName);

		FusekiResponse fr = getQueryEngine().fusekiResponse(builder.build());

		List<String> values = new ArrayList<>();
		for (Statement statement : fr.results.bindings) {
			values.add(statement.getObject().getValue());
		}
		return values;
	}

	@Override
	public String getPublicKeyOfNode(TumblerAddress node) throws Exception {
		List<String> pks = getFieldValuesOfResource(node.toTumblerAuthority(), "publicKey");
		if (pks == null || pks.isEmpty()) {
			return null;
		}
		return pks.get(0);
	}

	public QueryEngine getQueryEngine() {
		return engine;
	}

	public TemplateBuilder template(String template) {
		return new TemplateBuilder(template);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.oulipo.machine.server.IThingMapper#update(org.oulipo.resources.model.
	 * Thing)
	 */
	@Override
	public void update(Thing thing) {
		thing.updatedDate = new Date();
		getQueryEngine().rawUpdate("DELETE WHERE {<" + thing.resourceId.value + "> ?p ?o }");
		try {
			getQueryEngine()
					.rawUpdate("PREFIX : <http://oulipo.org/>INSERT DATA {" + ThingToString.asString(thing) + "}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
