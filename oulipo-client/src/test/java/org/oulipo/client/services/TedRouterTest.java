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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.oulipo.client.services.TumblerService.TumblerSuccess;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.Link;
import org.oulipo.resources.model.Node;
import org.oulipo.resources.model.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Response;

/**
 * These tests verify that the router is making the correct
 * <code>TumblerService</code> call.
 */
public class TedRouterTest {

	private TumblerService tumblerServiceMock = mock(TumblerService.class);

	private TedRouter tedRouter;
	
	private ObjectMapper mapper = new ObjectMapper();

	private TumblerSuccess dummyCallback = new TumblerSuccess() {

		@Override
		public void onSuccess(Response thing) {

		}

	};

	@Before
	public void setUp() {
		tedRouter = new TedRouter(tumblerServiceMock);
	}

	@Test
	public void getSystemNodes() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.1");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getSystemNodes(tumbler.getQueryParams(), dummyCallback);
	}

	@Test
	public void getSystemUsers() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.1.0.1");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getSystemUsers(tumbler.getQueryParams(), dummyCallback);
	}

	@Test
	public void getVSpans() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2.0.5.0.2.2.3.0.1.50~100");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getVSpan(tumbler, dummyCallback);
	}

	@Test
	public void getSystemVSpans() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.1.0.1.0.1.1.1.0.1.1~1.45");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getSystemVSpans(tumbler.getQueryParams(), dummyCallback);
	}
	
	@Test
	public void getSystemLinks() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.1.0.1.0.1.1.1.0.2.1");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getSystemLinks(tumbler.getQueryParams(), dummyCallback);
	}
	
	@Test(expected = MalformedSpanException.class)
	public void getSystemLinksWithSpan() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.1.0.1.0.1.1.1.0.2.1~2.4");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getSystemLinks(tumbler.getQueryParams(), dummyCallback);
	}
	
	@Test
	public void getLink() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2.0.5.0.2.2.3.0.2.50");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getLink(tumbler, dummyCallback);
	}

	@Test(expected = MalformedTumblerException.class)
	public void getUnknownElement() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2.0.5.0.2.2.3.0.3.50");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
	}

	@Test(expected = MalformedSpanException.class)
	public void spanOnLinksNotSupported() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2.0.5.0.2.2.3.0.2.1~100");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
	}

	@Test
	public void getUser() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2.0.5");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getUser(tumbler.toTumblerAuthority(), dummyCallback);
	}

	@Test
	public void getSystemDocuments() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.1.0.1.0.1.1.1");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getSystemDocuments(tumbler.getQueryParams(), dummyCallback);
	}

	@Test
	public void getDocument() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2.0.5.0.1.1.1");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getDocument(tumbler.toTumblerAuthority(), dummyCallback);
	}

	@Test
	public void getNode() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getNode(tumbler.toTumblerAuthority(), dummyCallback);
	}

	@Test
	public void getNetwork() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getNetwork(tumbler.toTumblerAuthority(), dummyCallback);
	}

	@Test
	public void getNodesWithPath() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1/nodes");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getNodes(tumbler.toTumblerAuthority(), tumbler.getQueryParams(), dummyCallback);
	}

	@Test
	public void getUsersWithPath() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.1/users");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
		verify(tumblerServiceMock).getUsers(tumbler.toTumblerAuthority(), tumbler.getQueryParams(), dummyCallback);
	}

	@Test(expected = IOException.class)
	public void getUsersWithPathAndUserAddress() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.1.0.2/users");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
	}

	@Test(expected = IOException.class)
	public void getNodesWithPathAndDocumentAddress() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2.0.4.0.1.1.1/nodes");
		tedRouter.routeGetRequest(tumbler, dummyCallback);
	}
	
	@Test
	public void createNode() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2");
		Node node = new Node();
		node.resourceId = tumbler;
		
		tedRouter.routePutRequest(tumbler, mapper.writeValueAsString(node), dummyCallback);
		verify(tumblerServiceMock).createOrUpdateNode(node, dummyCallback);
	}
	
	@Test
	public void createUser() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2.0.1");
		User user = new User();
		user.resourceId = tumbler;
		
		tedRouter.routePutRequest(tumbler, mapper.writeValueAsString(user), dummyCallback);
		verify(tumblerServiceMock).createOrUpdateUser(user, dummyCallback);
	}
	
	@Test
	public void createDocument() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2.0.1.0.2.1.1");
		Document document = new Document();
		document.resourceId = tumbler;
		
		tedRouter.routePutRequest(tumbler, mapper.writeValueAsString(document), dummyCallback);
		verify(tumblerServiceMock).createOrUpdateDocument(document, dummyCallback);
	}
	
	@Test
	public void createLink() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2.0.1.0.2.1.1.0.2.1");
		Link link = new Link();
		link.resourceId = tumbler;
		
		tedRouter.routePutRequest(tumbler, mapper.writeValueAsString(link), dummyCallback);
		verify(tumblerServiceMock).createOrUpdateLink(link, dummyCallback);
	}
	
	@Test(expected = IOException.class)
	public void failCreateUserIfNode() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2");
		User user = new User();
		user.familyName = "K";
		user.resourceId = tumbler;
		
		tedRouter.routePutRequest(tumbler, mapper.writeValueAsString(user), dummyCallback);
	}
	
	@Test(expected = IOException.class)
	public void failCreateUserIfDocument() throws Exception {
		TumblerAddress tumbler = TumblerAddress.create("1.2.0.2.0.2.1.1");
		User user = new User();
		user.resourceId = tumbler;
		user.familyName = "K";
		tedRouter.routePutRequest(tumbler, mapper.writeValueAsString(user), dummyCallback);
	}



}
