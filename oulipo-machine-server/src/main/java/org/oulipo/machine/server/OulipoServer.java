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
package org.oulipo.machine.server;

import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.io.File;
import java.io.IOException;

import org.apache.jena.fuseki.FusekiLogging;
import org.apache.jena.fuseki.embedded.FusekiEmbeddedServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerResourceException;
import org.oulipo.resources.DefaultThingRepository;
import org.oulipo.resources.ResourceErrorCodes;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.ThingRepository;
import org.oulipo.resources.rdf.RdfDataMapper;
import org.oulipo.security.auth.AuthResource;
import org.oulipo.security.auth.AuthResponseCodes;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.AuthorizationException;
import org.oulipo.security.auth.UnauthorizedException;
import org.oulipo.services.EditResourceException;
import org.oulipo.services.MissingBodyException;
import org.oulipo.services.OulipoRequestService;
import org.oulipo.services.ResourceSessionManager;
import org.oulipo.services.responses.ErrorResponse;
import org.oulipo.storage.StorageService;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.impl.DefaultStreamLoader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * Main app for running an OulipoServer
 */
public class OulipoServer {

	private static final String JSON = "application/json";

	public static void main(String[] args) throws IOException {
		run("MyDatabases/Dataset14", "localhost:4567");
	}

	/**
	 * Runs an OulipoServer
	 * 
	 * @param databaseDir
	 *            the database directory for RDF datasets
	 * @param host
	 *            the domain name of this server
	 * @throws IOException
	 *             if there is an I/O Exception starting the server
	 */
	public static void run(String databaseDir, String host) throws IOException {

		(new File(databaseDir)).mkdirs();
		Dataset ds = TDBFactory.createDataset(databaseDir);
		FusekiEmbeddedServer server = FusekiEmbeddedServer.create().add("/ds", ds, true).setPort(3030).build();

		FusekiLogging.setLogging();
		server.start();

		// Templates
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
		cfg.setDirectoryForTemplateLoading(new File("./src/main/resources/templates"));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

		RdfDataMapper thingMapper = new RdfDataMapper(cfg);
		ThingRepository thingRepo = new DefaultThingRepository(thingMapper);

		StorageService service = new StorageService("oulipo");
		ResourceSessionManager sessionManager = new ResourceSessionManager(service, thingRepo);
		JsonTransformer transformer = new JsonTransformer();

		String spec = "maximumSize=10000,expireAfterWrite=10m";
		StreamLoader streamLoader = new DefaultStreamLoader(new File("."), spec);

		AuthResource authResource = new AuthResource(sessionManager, objectMapper, host);
		OulipoRequestService serviceRequest = new OulipoRequestService(thingRepo, sessionManager, streamLoader);
		RequestsMapper req = new RequestsMapper(serviceRequest, sessionManager, thingRepo);

		post("/sparql", SparqlApi.query(objectMapper, thingMapper));

		get("/auth", JSON, AuthApi.temporyAuthToken(authResource), transformer);
		post("/auth", AuthApi.getSessionToken(authResource), transformer);

		get("/docuverse/:networkId", JSON, req.getNetwork(), transformer);

		get("/docuverse/:networkId/nodes", JSON, req.getNodes(), transformer);

		get("/docuverse/:networkId/1", JSON, req.getSystemNodes(), transformer);
		get("/docuverse/:networkId/:nodeId", JSON, req.getNode(), transformer);
		get("/docuverse/:networkId/:nodeId/users", JSON, req.getNodeUsers(), transformer);
		put("/docuverse/:networkId/:nodeId", JSON, req.createNode(), transformer);

		get("/docuverse/:networkId/1/1", JSON, req.getSystemUser(), transformer);
		get("/docuverse/:networkId/:nodeId/:userId", JSON, req.getUser(), transformer);
		put("/docuverse/:networkId/:nodeId/:userId", JSON, req.createOrUpdateUser(), transformer);
		get("/docuverse/:networkId/:nodeId/:userId/documents", JSON, req.getUserDocuments(), transformer);

		get("/docuverse/:networkId/1/1/1.1.1", req.getSystemDocuments(), transformer);
		get("/docuverse/:networkId/:nodeId/:userId/:docId", req.getDocument(), transformer);
		get("/docuverse/:networkId/:nodeId/:userId/:docId/links", req.getDocumentLinks(), transformer);
		get("/docuverse/:networkId/:nodeId/:userId/:docId/virtual", req.getVirtual(), transformer);

		put("/docuverse/:networkId/:nodeId/:userId/:docId", JSON, req.createDocument(), transformer);
		post("/docuverse/:networkId/:nodeId/:userId/:docId/newVersion", req.newVersion(), transformer);

		get("/docuverse/:networkId/:nodeId/:userId/:docId/endsets", req.getEndsets(), transformer);

		get("/docuverse/:networkId/1/1/1.1.1/2.1", req.getSystemLinks(), transformer);
		get("/docuverse/:networkId/1/1/1.1.1/1.1~1.1", req.getSystemVSpans(), transformer);
		get("/docuverse/:networkId/:nodeId/:userId/:docId/:elementId", req.getElement(), transformer);

		put("/docuverse/:networkId/:nodeId/:userId/:docId/:elementId", JSON, req.createOrUpdateLink(), transformer);

		post("/docuverse/:networkId/:nodeId/:userId/:docId/:elementId/insert", JSON, req.insertContent(), transformer);

		post("/docuverse/:networkId/:nodeId/:userId/:docId/:elementId/copy", JSON, req.copyContent(), transformer);

		post("/docuverse/:networkId/:nodeId/:userId/:docId/swap/:spans", JSON, req.swapContent(), transformer);

		delete("/docuverse/:networkId/:nodeId/:userId/:docId/:elementId", req.deleteContent(), transformer);

		exception(MalformedTumblerException.class, (exception, request, response) -> {
			ErrorResponse resp = new ErrorResponse(ResourceErrorCodes.BAD_TUMBLER_ADDRESS, exception.getMessage(),
					null);
			try {
				response.status(400);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e) {
			}

		});

		exception(EditResourceException.class, (exception, request, response) -> {
			TumblerResourceException e = (TumblerResourceException) exception;
			ErrorResponse resp = new ErrorResponse(ResourceErrorCodes.BAD_EDIT, exception.getMessage(),
					e.getTumblerAddress());
			try {
				response.status(400);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e1) {
			}

		});

		exception(UnrecognizedPropertyException.class, (exception, request, response) -> {
			exception.printStackTrace();
			ErrorResponse resp = new ErrorResponse(ResourceErrorCodes.INVALID_JSON, exception.getMessage(), null);
			try {
				response.status(400);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e) {
			}

		});

		exception(UnauthorizedException.class, (exception, request, response) -> {
			TumblerResourceException e = (TumblerResourceException) exception;
			ErrorResponse resp = new ErrorResponse(AuthResponseCodes.NOT_AUTHORIZED, exception.getMessage(),
					e.getTumblerAddress());
			try {
				response.status(401);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e1) {
			}

		});

		exception(AuthenticationException.class, (exception, request, response) -> {
			AuthenticationException e = (AuthenticationException) exception;
			ErrorResponse resp = new ErrorResponse(999, exception.getMessage(), null);
			try {
				response.status(401);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e1) {
			}

		});
		exception(AuthorizationException.class, (exception, request, response) -> {
			ErrorResponse resp = new ErrorResponse(AuthResponseCodes.NOT_AUTHORIZED, exception.getMessage(), null);
			try {
				response.status(401);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e1) {
			}

		});
		exception(ResourceNotFoundException.class, (exception, request, response) -> {
			ResourceNotFoundException e = (ResourceNotFoundException) exception;
			ErrorResponse resp = new ErrorResponse(e.getCode(), exception.getMessage(), e.getTumblerAddress());
			try {
				response.status(404);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e1) {
			}

		});

		exception(MissingBodyException.class, (exception, request, response) -> {
			TumblerResourceException e = (TumblerResourceException) exception;
			ErrorResponse resp = new ErrorResponse(ResourceErrorCodes.MISSING_JSON_BODY, exception.getMessage(),
					e.getTumblerAddress());
			try {
				response.status(400);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e1) {
			}

		});
	}

}
