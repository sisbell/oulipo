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

import org.oulipo.rdf.model.Document;
import org.oulipo.rdf.model.Virtual;
import org.oulipo.services.responses.EndsetByType;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Provides services for accessing an Oulipo Server
 */
public interface DocuverseService {

	@GET("{hash}")
	Call<Document> getDocument(@Path("hash") String document);

	@GET("documents")
	Call<List<Document>> getDocuments(@QueryMap Map<String, String> options);

	@GET("{hash}/endsets")
	Call<EndsetByType> getEndsets(@Path("hash") String document);

	@GET("{hash}/virtual")
	Call<Virtual> getVirtual(@Path("hash") String document, @QueryMap Map<String, String> options);

	@POST("{hash}")
	Call<String> loadOperations(@Path("hash") String document);

}
