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

import java.io.ByteArrayOutputStream;
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

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.sparql.ARQException;
import org.apache.jena.sparql.resultset.ResultsFormat;
import org.apache.jena.system.Txn;
import org.oulipo.net.IRI;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.rdf.RdfFactory;
import org.oulipo.rdf.RdfSubject;
import org.oulipo.rdf.Statement;
import org.oulipo.resources.DataMapper;
import org.oulipo.resources.model.Thing;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Maps between Things and RDF representations
 */
public final class RdfDataMapper2 implements DataMapper {

	public class QueryEngine {

		public void delete(String query) throws ARQException {
			conn.delete(query);
		}

		public FusekiResponse fusekiResponse(String query)
				throws JsonParseException, JsonMappingException, IOException {
			return mapper.readValue(json(query), FusekiResponse.class);
		}

		public String json(String query) {
			return raw(query, ResultsFormat.FMT_RS_JSON);
		}

		public String raw(String query, ResultsFormat format) throws ARQException {
			QueryExecution qExec = conn.query(query);
			ResultSet rs = qExec.execSelect();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ResultSetFormatter.output(baos, rs, format);
			return baos.toString();
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

	private final RDFConnection conn;

	private QueryEngine engine = new QueryEngine();

	private ObjectMapper mapper = new ObjectMapper();

	public RdfDataMapper2(Configuration cfg) {
		conn = RDFConnectionFactory.connect("http://localhost:3030/ds");
		this.cfg = cfg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.oulipo.machine.server.IThingMapper#add(java.util.Collection)
	 */
	@Override
	public void add(Collection<Thing> things) {
		Txn.executeWrite(conn, () -> {
			try {
				conn.update("PREFIX : <http://oulipo.org/>INSERT DATA {\r\n" + ThingToString.asStrings(things) + "}");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 *
	 * The language in which to write the model is specified by the lang argument.
	 * Predefined values are "N-TRIPLE", "TURTLE", (and "TTL") and "N3". The default
	 * value, represented by null, is TTL".
	 *
	 * @param query
	 * @param lang
	 * @return
	 */
	public String construct(String query, String lang) {
		Model model = conn.queryConstruct(query);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		model.write(baos, lang);
		return baos.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.oulipo.machine.server.IThingMapper#delete(java.util.Collection)
	 */
	@Override
	public void delete(Collection<Thing> things) {
		Txn.executeWrite(conn, () -> {
			try {
				conn.update("PREFIX : <http://oulipo.org/>DELETE DATA {\r\n" + ThingToString.asStrings(things) + "}");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public boolean exists(IRI address) {
		RdfSubject subject = RdfFactory.createRdfSubject(address);
		StringBuilder sb = new StringBuilder();
		ThingToString.addSubject(subject, sb);
		QueryExecution qExec = conn.query("SELECT * { " + sb.toString() + " ?predicate ?object }");

		ResultSet rs = qExec.execSelect();
		return rs.hasNext();
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
		Txn.executeWrite(conn, () -> {
			try {
				String de = "PREFIX : <http://oulipo.org/>DELETE DATA {\r\n" + ThingToString.asString(thing) + "}";
				;
				System.out.println(de);
				conn.update("PREFIX : <http://oulipo.org/>DELETE DATA {\r\n" + ThingToString.asString(thing) + "}");
				// conn.update("DELETE WHERE {\r\n<" + thing.resourceId.value + "> ?p ?o }");
				conn.update("PREFIX : <http://oulipo.org/>INSERT DATA {\r\n" + ThingToString.asString(thing) + "}");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
