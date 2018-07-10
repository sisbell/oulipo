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

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.File;
import java.io.IOException;

import org.oulipo.resources.DefaultThingRepository;
import org.oulipo.resources.ThingRepository;
import org.oulipo.resources.rdf.RdfDataMapper;
import org.oulipo.security.auth.AuthResource;
import org.oulipo.services.OulipoRequestService;
import org.oulipo.services.ResourceSessionManager;
import org.oulipo.storage.StorageService;
import org.oulipo.streams.RemoteFileManager;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.impl.DefaultStreamLoader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		// Dataset ds = TDBFactory.createDataset(databaseDir);
		// FusekiServer server = FusekiServer.create().add("/ds", ds,
		// true).setPort(3030).build();

		// FusekiLogging.setLogging();
		// server.start();

		// Templates
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
		cfg.setClassForTemplateLoading(RdfDataMapper.class, "/templates");
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
		StreamLoader streamLoader = new DefaultStreamLoader(new File("streams"), spec);

		AuthResource authResource = new AuthResource(sessionManager, objectMapper, host);
		RemoteFileManager remoteFileManager = null;//TODO: implement new org.oulipo.services.IpfsFileManager();

		OulipoRequestService serviceRequest = new OulipoRequestService(thingRepo, sessionManager, streamLoader,
				remoteFileManager);
		RequestsMapper req = new RequestsMapper(serviceRequest, sessionManager, thingRepo);

		get("/auth", JSON, AuthApi.temporyAuthToken(authResource), transformer);
		post("/auth", AuthApi.getSessionToken(authResource), transformer);

		get("/docuverse/documents", req.getDocuments(), transformer);

		get("/docuverse/:hash", req.getDocument(), transformer);
		get("/docuverse/:hash/virtual", req.getVirtual(), transformer);
		get("/docuverse/:hash/endsets", req.getEndsets(), transformer);
		post("/docuverse/:hash", req.loadDocument(), transformer);
	}

}
