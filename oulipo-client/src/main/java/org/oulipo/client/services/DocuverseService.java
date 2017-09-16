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
package org.oulipo.client.services;

import java.util.List;
import java.util.Map;

import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.Link;
import org.oulipo.resources.model.Node;
import org.oulipo.resources.model.User;
import org.oulipo.resources.model.VSpan;
import org.oulipo.resources.model.Virtual;
import org.oulipo.services.responses.EndsetByType;
import org.oulipo.services.responses.Network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Provides services for accessing an Oulipo Server
 */
public interface DocuverseService {

	/**
	 * Creates or updates document meta-data at the constructed tumbler address. The
	 * document contains information like doc version, title, description. It does
	 * not contain the text of the document.
	 * 
	 * @param network
	 *            the network field
	 * @param node
	 *            the node field
	 * @param user
	 *            the user field
	 * @param document
	 *            the document field
	 * @param body
	 *            the document
	 * @return async retrofit call containing the complete <code>Document</code>
	 */
	@PUT("{network}/{node}/{user}/{document}")
	Call<Document> createOrUpdateDocument(@Path("network") String network, @Path("node") String node,
			@Path("user") String user, @Path("document") String document, @Body Document body);

	@PUT("{network}/{node}/{user}/{document}/{link}")
	Call<Link> createOrUpdateLink(@Path("network") String network, @Path("node") String node, @Path("user") String user,
			@Path("document") String document, @Path("link") String link, @Body Link body);

	@PUT("{network}/{node}")
	Call<Node> createOrUpdateNode(@Path("network") String network, @Path("node") String node, @Body Node body);

	/**
	 * Creates or updates a user's meta-data.
	 * 
	 * @param network
	 *            the network field
	 * @param node
	 *            the node field
	 * @param user
	 *            the user field
	 * @param body
	 *            the user info
	 * @return async retrofit call containing the complete <code>User</code> info
	 */
	@PUT("{network}/{node}/{user}")
	Call<User> createOrUpdateUser(@Path("network") String network, @Path("node") String node, @Path("user") String user,
			@Body User body);

	/**
	 * Gets the document meta-data at the constructed tumbler address
	 * 
	 * @param network
	 *            the network field
	 * @param node
	 *            the node field
	 * @param user
	 *            the user field
	 * @param document
	 *            the document field
	 * @param body
	 *            the document
	 * @return async retrofit call containing a <code>Document</code>
	 */
	@GET("{network}/{node}/{user}/{document}")
	Call<Document> getDocument(@Path("network") String network, @Path("node") String node, @Path("user") String user,
			@Path("document") String document);

	@GET("{network}/{node}/{user}/documents")
	Call<List<Document>> getDocuments(@Path("network") String network, @Path("node") String node,
			@Path("user") String user, @QueryMap Map<String, String> options);

	@GET("{network}/{node}/{user}/{document}/endsets")
	Call<EndsetByType> getEndsets(@Path("network") String network, @Path("node") String node, @Path("user") String user,
			@Path("document") String document);

	@GET("{network}/{node}/{user}/{document}/{link}")
	Call<Link> getLink(@Path("network") String network, @Path("node") String node, @Path("user") String user,
			@Path("document") String document, @Path("link") String link);

	@GET("{network}/{node}/{user}/{document}/links")
	Call<List<Link>> getLinks(@Path("network") String network, @Path("node") String node, @Path("user") String user,
			@Path("document") String document, @QueryMap Map<String, String> options);

	/**
	 * Gets list of available network
	 * 
	 * @param network
	 * @return
	 */
	@GET("{network}")
	Call<Network> getNetwork(@Path("network") String network);

	@GET("{network}/{node}")
	Call<Node> getNode(@Path("network") String network, @Path("node") String node);

	@GET("{network}/nodes")
	Call<List<Node>> getNodes(@Path("network") String network, @QueryMap Map<String, String> options);

	/**
	 * Gets a list of all documents in the system, without filter for network, node
	 * or user.
	 * 
	 * @param options
	 *            additional filters
	 * @return
	 */
	@GET("1/1/1/1.1.1")
	Call<List<Document>> getSystemDocuments(@QueryMap Map<String, String> options);

	@GET("1/1/1/1.1.1/2.1")
	Call<List<Link>> getSystemLinks(@QueryMap Map<String, String> options);

	@GET("1/1")
	Call<List<Node>> getSystemNodes(@QueryMap Map<String, String> options);

	@GET("1/1/1")
	Call<List<User>> getSystemUsers(@QueryMap Map<String, String> options);

	@GET("1/1/1/1.1.1/1.1~1.1")
	Call<List<VSpan>> getSystemVSpans(@QueryMap Map<String, String> options);

	@GET("{network}/{node}/{user}")
	Call<User> getUser(@Path("network") String network, @Path("node") String node, @Path("user") String user);

	@GET("{network}/{node}/users")
	Call<List<User>> getUsers(@Path("network") String network, @Path("node") String node,
			@QueryMap Map<String, String> options);

	// TODO: needs server implementation
	@GET("{network}/{node}/{user}/{document}/virtual")
	Call<Virtual> getVirtual(@Path("network") String network, @Path("node") String node, @Path("user") String user,
			@Path("document") String document, @QueryMap Map<String, String> options);

	@GET("{network}/{node}/{user}/{document}/{vspan}")
	Call<VSpan> getVSpan(@Path("network") String network, @Path("node") String node, @Path("user") String user,
			@Path("document") String document, @Path("vspan") String vspan);

	@GET("{network}/{node}/{user}/newDocument")
	Call<Document> newDocument(@Path("network") String network, @Path("node") String node, @Path("user") String user);

	@GET("{network}/{node}/newUser")
	Call<User> newUser(@Path("network") String network, @Path("node") String node);

}
