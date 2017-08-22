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
package org.oulipo.machine.server.api;

import static spark.Spark.halt;

import java.io.InputStream;

import org.apache.jena.sparql.resultset.ResultsFormat;
import org.oulipo.machine.server.RdfMapper;
import org.oulipo.resources.responses.ErrorResponseDto;
import org.oulipo.security.auth.AuthResponseCodes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

import spark.Route;

public class SparqlApi {

	public static Route query(ObjectMapper mapper, RdfMapper repo) {
		return (request, response) -> {
			String fmt = request.queryParams("fmt");
			String verb = request.queryParams("verb");

			if (Strings.isNullOrEmpty(fmt)) {
				ErrorResponseDto resp = new ErrorResponseDto(
						AuthResponseCodes.UNKNOWN_ERROR,
						"Missing fmt param: ", null);
				return halt(400, mapper.writeValueAsString(resp));
			}

			if (Strings.isNullOrEmpty(verb)) {
				ErrorResponseDto resp = new ErrorResponseDto(
						AuthResponseCodes.UNKNOWN_ERROR,
						"Missing verb param: ", null);
				return halt(400, mapper.writeValueAsString(resp));
			}

			if (verb.toLowerCase().equals("select")) {
				ResultsFormat format = ResultsFormat.lookup(fmt);
				try (InputStream is = request.raw().getInputStream()) {
					String query = new String(ByteStreams.toByteArray(is),
							Charsets.UTF_8);
					if (ResultsFormat.FMT_RS_JSON.equals(format)) {
						response.header("content-type", "application/json");
						try {
							return repo.getQueryEngine().json(query);
						} catch (Exception e) {
							ErrorResponseDto resp = new ErrorResponseDto(
									AuthResponseCodes.UNKNOWN_ERROR,
									"Query Failed: " + e.getMessage(), null);
							e.printStackTrace();
							return halt(400, mapper.writeValueAsString(resp));
						}
					} else {
						try {
							return repo.getQueryEngine().raw(query, format);
						} catch (Exception e) {
							e.printStackTrace();
							ErrorResponseDto resp = new ErrorResponseDto(
									AuthResponseCodes.UNKNOWN_ERROR,
									"Unsupported format: " + fmt, null);
							return halt(400, mapper.writeValueAsString(resp));
						}
					}
				}
			} else if (verb.toLowerCase().equals("construct")) {
				try (InputStream is = request.raw().getInputStream()) {
					String query = new String(ByteStreams.toByteArray(is),
							Charsets.UTF_8);
					if (!"n-triple".equalsIgnoreCase(fmt)
							&& !"turtle".equalsIgnoreCase(fmt)
							&& !"ttl".equalsIgnoreCase(fmt)
							&& !"n3".equalsIgnoreCase(fmt)) {
						throw new Exception();
					}
					return repo.construct(query, fmt);
				} catch (Exception e) {
					e.printStackTrace();
					ErrorResponseDto resp = new ErrorResponseDto(
							AuthResponseCodes.UNKNOWN_ERROR, "Error: "
									+ e.getMessage(), null);
					return halt(400, mapper.writeValueAsString(resp));
				}
			} else if (verb.toLowerCase().equals("delete")) {
				try (InputStream is = request.raw().getInputStream()) {
					String query = new String(ByteStreams.toByteArray(is),
							Charsets.UTF_8);
					try {
						repo.getQueryEngine().delete(query);
					} catch (Exception e) {
						e.printStackTrace();
						ErrorResponseDto resp = new ErrorResponseDto(
								AuthResponseCodes.UNKNOWN_ERROR,
								"Delete failed", null);
						return halt(400, mapper.writeValueAsString(resp));
					}
				}
			
				return "{}";
			} else {
				ErrorResponseDto resp = new ErrorResponseDto(
						AuthResponseCodes.UNKNOWN_ERROR, "Unknown verb: "
								+ verb, null);
				return halt(400, mapper.writeValueAsString(resp));
			}

		};
	}
}
