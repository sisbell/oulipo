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

import java.util.List;

import org.oulipo.machine.server.RequestMapper;
import org.oulipo.machine.server.XanSessionManager;
import org.oulipo.machine.server.exceptions.EditException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.impl.StreamOulipoMachine;

import spark.Route;

public class ContentApi {

	public static Route deleteContent(XanSessionManager sessionManager,
			StreamLoader streamLoader, RequestMapper requestMapper) {
		return (request, response) -> {
			sessionManager.authenticateSession(request);
			sessionManager.authorizeResource(request);

			TumblerAddress elementAddress = requestMapper
					.createElementAddress(request);
			OulipoMachine om = StreamOulipoMachine.create(streamLoader,
					elementAddress, true);

			if (elementAddress.hasSpan()) {
				om.delete(new VariantSpan(elementAddress));
				return "{}";// OK: Response
			} else {
				throw new EditException(elementAddress,
						"Attempting to delete content without specifying a span");

			}
		};
	};

	public static Route insertContent(XanSessionManager sessionManager,
			StreamLoader streamLoader, RequestMapper requestMapper) {
		return (request, response) -> {
			sessionManager.authenticateSession(request);
			sessionManager.authorizeResource(request);

			TumblerAddress elementAddress = requestMapper
					.createElementAddress(request);

			OulipoMachine om = StreamOulipoMachine.create(streamLoader,
					elementAddress, true);

			if (elementAddress.getElement().get(0) != 1) {
				throw new EditException(elementAddress,
						"Attempting to insert content outside of byte space");
			}
			//TODO:
		//	om..insert(elementAddress.getElement().get(1), request.body());
			return "{}";
		};
	};

	public static Route copyContent(RequestMapper requestMapper,
			XanSessionManager sessionManager, StreamLoader streamLoader) {
		return (request, response) -> {
			sessionManager.authenticateSession(request);
			sessionManager.authorizeResource(request);

			TumblerAddress elementAddress = requestMapper
					.createElementAddress(request);
			OulipoMachine om = StreamOulipoMachine.create(streamLoader,
					elementAddress.getDocumentAddress(), true);

			if (elementAddress.getElement().get(0) != 1) {
				throw new EditException(elementAddress,
						"Attempting to copy content outside of byte space");
			}

			List<VariantSpan> vspans = requestMapper.readVariantSpans(request,
					elementAddress);
			om.copy(elementAddress.getElement().get(1), vspans);

			return "{}";
		};
	};

	public static Route swapContent(XanSessionManager sessionManager,
			StreamLoader streamLoader, RequestMapper requestMapper) {
		return (request, response) -> {
			sessionManager.authenticateSession(request);
			sessionManager.authorizeResource(request);

			TumblerAddress documentAddress = requestMapper
					.createDocumentAddress(request);
			OulipoMachine om = StreamOulipoMachine.create(streamLoader,
					documentAddress.getDocumentAddress(), true);
			String spanField = request.params(":spans");
			String[] spans = spanField.split("[,]");
			String[] span1 = spans[0].split("~");
			String[] span2 = spans[1].split("~");
			om.swap(new VariantSpan(Integer.parseInt(span1[0]), Integer
					.parseInt(span1[1])),
					new VariantSpan(Integer.parseInt(span2[0]), Integer
							.parseInt(span2[1])));
			return "{}";
		};
	};
}
