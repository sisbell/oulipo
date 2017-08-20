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

import org.junit.Test;
import org.oulipo.net.IRI;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Request;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class RequestMapperTest {

	@Test
	public void 	createNetworkAddress() throws Exception {
		Request mock = mock(Request.class);
		
		when(mock.params(":networkId")).thenReturn("1");

		RequestMapper mapper = new RequestMapper(new ObjectMapper());
		TumblerAddress tumbler = mapper.createNetworkAddress(mock);
		assertEquals("ted://1", tumbler.value);
	}
	
	@Test
	public void createNodeAddress() throws Exception {
		Request mock = mock(Request.class);
		
		when(mock.params(":networkId")).thenReturn("1");
		when(mock.params(":nodeId")).thenReturn("2");

		RequestMapper mapper = new RequestMapper(new ObjectMapper());
		TumblerAddress tumbler = mapper.createNodeAddress(mock);
		assertEquals("ted://1.2", tumbler.value);
	}
	
	@Test
	public void createUserAddress() throws Exception {
		Request mock = mock(Request.class);
		
		when(mock.params(":networkId")).thenReturn("1");
		when(mock.params(":nodeId")).thenReturn("2");
		when(mock.params(":userId")).thenReturn("3");

		RequestMapper mapper = new RequestMapper(new ObjectMapper());
		TumblerAddress tumbler = mapper.createUserAddress(mock);
		assertEquals("ted://1.2.0.3", tumbler.value);
	}
	
	@Test(expected = MalformedTumblerException.class)
	public void createBadDocumentAddress() throws Exception {
		Request mock = mock(Request.class);
		
		when(mock.params(":networkId")).thenReturn("1");
		when(mock.params(":nodeId")).thenReturn("2");
		when(mock.params(":userId")).thenReturn("3");
		when(mock.params(":docId")).thenReturn("2.1");

		RequestMapper mapper = new RequestMapper(new ObjectMapper());
		mapper.createDocumentAddress(mock);
	}

	@Test
	public void createDocumentAddress() throws Exception {
		Request mock = mock(Request.class);
		
		when(mock.params(":networkId")).thenReturn("1");
		when(mock.params(":nodeId")).thenReturn("2");
		when(mock.params(":userId")).thenReturn("3");
		when(mock.params(":docId")).thenReturn("2.1.1");

		RequestMapper mapper = new RequestMapper(new ObjectMapper());
		TumblerAddress tumbler = mapper.createDocumentAddress(mock);
		assertEquals("ted://1.2.0.3.0.2.1.1", tumbler.value);
	}
	
	@Test
	public void createElementAddress() throws Exception {
		Request mock = mock(Request.class);
		
		when(mock.params(":networkId")).thenReturn("1");
		when(mock.params(":nodeId")).thenReturn("2");
		when(mock.params(":userId")).thenReturn("3");
		when(mock.params(":docId")).thenReturn("2.1.1");
		when(mock.params(":elementId")).thenReturn("1.500");

		RequestMapper mapper = new RequestMapper(new ObjectMapper());
		TumblerAddress tumbler = mapper.createElementAddress(mock);
		assertEquals("ted://1.2.0.3.0.2.1.1.0.1.500", tumbler.value);
	}

	
	@Test
	public void readElement() throws Exception {
		Request mock = mock(Request.class);
		
		when(mock.params(":networkId")).thenReturn("1");
		when(mock.params(":nodeId")).thenReturn("2");
		when(mock.params(":userId")).thenReturn("3");
		when(mock.params(":docId")).thenReturn("2.1.1");
		when(mock.params(":elementId")).thenReturn("1.500");

		RequestMapper mapper = new RequestMapper(new ObjectMapper());
		IRI iri = mapper.readElement(mock);
		assertEquals("ted://1.2.0.3.0.2.1.1.0.1.500", iri.value);
	}



}
