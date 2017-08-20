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
import org.oulipo.machine.server.api.AuthApi;
import org.oulipo.machine.server.api.ContentApi;
import org.oulipo.machine.server.api.DocumentApi;
import org.oulipo.machine.server.api.ElementApi;
import org.oulipo.machine.server.api.EndsetsApi;
import org.oulipo.machine.server.api.NetworkApi;
import org.oulipo.machine.server.api.NodeApi;
import org.oulipo.machine.server.api.SparqlApi;
import org.oulipo.machine.server.api.UserApi;
import org.oulipo.machine.server.exceptions.EditException;
import org.oulipo.machine.server.exceptions.InvalidJsonException;
import org.oulipo.machine.server.exceptions.JsonMissingException;
import org.oulipo.machine.server.exceptions.UnauthorizedException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerResourceException;
import org.oulipo.resources.ResourceErrorCodes;
import org.oulipo.resources.ResourceNotFoundException;
import org.oulipo.resources.responses.ErrorResponseDto;
import org.oulipo.security.auth.AuthenticationException;
import org.oulipo.security.auth.AuthorizationException;
import org.oulipo.security.auth.XanAuthResponseCodes;
import org.oulipo.storage.StorageService;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.impl.DefaultStreamLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * Main app for running an OulipoServer
 */
public class App {

	/**
	 * Runs an OulipoServer
	 * 
	 * @param databaseDir the database directory for RDF datasets
	 * @throws IOException if there is an I/O Exception starting the server
	 */
	public static void run(String databaseDir) throws IOException {

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
		RdfMapper thingMapper = new RdfMapper(cfg);
		RequestMapper requestMapper = new RequestMapper(objectMapper);
		RdfRepository thingRepo = new RdfRepository(thingMapper);

		StorageService service = new StorageService("oulipo");
		XanSessionManager sessionManager = new XanSessionManager(service, thingRepo, requestMapper);
		JsonTransformer transformer = new JsonTransformer();

		String spec = "maximumSize=10000,expireAfterWrite=10m";
		StreamLoader streamLoader = new DefaultStreamLoader(new File("."), spec);

		post("/sparql", SparqlApi.query(objectMapper, thingMapper));

		get("/auth", "application/json", AuthApi.temporyAuthToken(sessionManager), transformer);
		post("/auth", AuthApi.getSessionToken(objectMapper, sessionManager), transformer);

		get("/docuverse/:networkId", "application/json", NetworkApi.getNetwork(requestMapper), transformer);

		get("/docuverse/:networkId/nodes", "application/json", NetworkApi.getNodes(thingRepo), transformer);

		get("/docuverse/:networkId/1", "application/json", NodeApi.getSystemNodes(thingRepo), transformer);
		get("/docuverse/:networkId/:nodeId", "application/json", NodeApi.getNode(thingRepo, requestMapper),
				transformer);
		get("/docuverse/:networkId/:nodeId/users", "application/json", NodeApi.getNodeUsers(thingRepo, requestMapper),
				transformer);
		put("/docuverse/:networkId/:nodeId", "application/json",
				NodeApi.createNode(thingRepo, requestMapper, sessionManager), transformer);

		get("/docuverse/:networkId/1/1", "application/json", UserApi.getSystemUser(thingRepo), transformer);
		get("/docuverse/:networkId/:nodeId/:userId", "application/json", UserApi.getUser(thingRepo, requestMapper),
				transformer);
		put("/docuverse/:networkId/:nodeId/:userId", "application/json",
				UserApi.createOrUpdateUser(objectMapper, requestMapper, thingRepo, sessionManager), transformer);
		get("/docuverse/:networkId/:nodeId/:userId/documents", "application/json",
				UserApi.getUserDocuments(thingRepo, requestMapper), transformer);

		get("/docuverse/:networkId/1/1/1.1.1", DocumentApi.getSystemDocuments(thingRepo, sessionManager), transformer);
		get("/docuverse/:networkId/:nodeId/:userId/:docId", DocumentApi.getDocument(sessionManager), transformer);
		get("/docuverse/:networkId/:nodeId/:userId/:docId/links",
				DocumentApi.getDocumentLinks(thingRepo, sessionManager, requestMapper), transformer);
		get("/docuverse/:networkId/:nodeId/:userId/:docId/virtual",
				DocumentApi.getVirtual(thingRepo, sessionManager, streamLoader), transformer);

		put("/docuverse/:networkId/:nodeId/:userId/:docId", "application/json",
				DocumentApi.createDocument(requestMapper, thingRepo, sessionManager), transformer);
		post("/docuverse/:networkId/:nodeId/:userId/:docId/newVersion",
				DocumentApi.newVersion(objectMapper, thingRepo, sessionManager, requestMapper), transformer);

		get("/docuverse/:networkId/:nodeId/:userId/:docId/endsets",
				EndsetsApi.getEndsets(thingRepo, sessionManager, requestMapper), transformer);

		get("/docuverse/:networkId/1/1/1.1.1/2.1", ElementApi.getSystemLinks(thingRepo), transformer);
		get("/docuverse/:networkId/1/1/1.1.1/1.1~1.1", ElementApi.getSystemVSpans(thingRepo), transformer);
		get("/docuverse/:networkId/:nodeId/:userId/:docId/:elementId",
				ElementApi.getElement(thingRepo, requestMapper, sessionManager), transformer);

		put("/docuverse/:networkId/:nodeId/:userId/:docId/:elementId", "application/json",
				ElementApi.createOrUpdateLink(requestMapper, thingRepo, sessionManager), transformer);

		post("/docuverse/:networkId/:nodeId/:userId/:docId/:elementId/insert", "application/json",
				ContentApi.insertContent(sessionManager, streamLoader, requestMapper), transformer);

		post("/docuverse/:networkId/:nodeId/:userId/:docId/:elementId/copy", "application/json",
				ContentApi.copyContent(requestMapper, sessionManager, streamLoader), transformer);

		post("/docuverse/:networkId/:nodeId/:userId/:docId/swap/:spans", "application/json",
				ContentApi.swapContent(sessionManager, streamLoader, requestMapper), transformer);

		delete("/docuverse/:networkId/:nodeId/:userId/:docId/:elementId",
				ContentApi.deleteContent(sessionManager, streamLoader, requestMapper), transformer);

		exception(MalformedTumblerException.class, (exception, request, response) -> {
			ErrorResponseDto resp = new ErrorResponseDto(ResourceErrorCodes.BAD_TUMBLER_ADDRESS, exception.getMessage(),
					null);
			try {
				response.status(400);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e) {
			}

		});

		exception(EditException.class, (exception, request, response) -> {
			TumblerResourceException e = (TumblerResourceException) exception;
			ErrorResponseDto resp = new ErrorResponseDto(ResourceErrorCodes.BAD_EDIT, exception.getMessage(),
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
			ErrorResponseDto resp = new ErrorResponseDto(ResourceErrorCodes.INVALID_JSON, exception.getMessage(), null);
			try {
				response.status(400);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e) {
			}

		});

		exception(UnauthorizedException.class, (exception, request, response) -> {
			TumblerResourceException e = (TumblerResourceException) exception;
			ErrorResponseDto resp = new ErrorResponseDto(XanAuthResponseCodes.NOT_AUTHORIZED, exception.getMessage(),
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
			ErrorResponseDto resp = new ErrorResponseDto(999, exception.getMessage(), null);
			try {
				response.status(401);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e1) {
			}

		});
		exception(AuthorizationException.class, (exception, request, response) -> {
			ErrorResponseDto resp = new ErrorResponseDto(XanAuthResponseCodes.NOT_AUTHORIZED, exception.getMessage(), null);
			try {
				response.status(401);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e1) {
			}

		});
		exception(ResourceNotFoundException.class, (exception, request, response) -> {
			ResourceNotFoundException e = (ResourceNotFoundException) exception;
			ErrorResponseDto resp = new ErrorResponseDto(e.getCode(), exception.getMessage(), e.getTumblerAddress());
			try {
				response.status(404);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e1) {
			}

		});

		exception(InvalidJsonException.class, (exception, request, response) -> {
			TumblerResourceException e = (TumblerResourceException) exception;
			ErrorResponseDto resp = new ErrorResponseDto(ResourceErrorCodes.INVALID_JSON, exception.getMessage(),
					e.getTumblerAddress());
			try {
				response.status(400);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e1) {
			}

		});

		exception(JsonMissingException.class, (exception, request, response) -> {
			TumblerResourceException e = (TumblerResourceException) exception;
			ErrorResponseDto resp = new ErrorResponseDto(ResourceErrorCodes.MISSING_JSON_BODY, exception.getMessage(),
					e.getTumblerAddress());
			try {
				response.status(400);
				response.type("application/json");
				response.body(objectMapper.writeValueAsString(resp));
			} catch (Exception e1) {
			}

		});
	}

	public static void main(String[] args) throws IOException {
		run("MyDatabases/Dataset14");
	}

}
