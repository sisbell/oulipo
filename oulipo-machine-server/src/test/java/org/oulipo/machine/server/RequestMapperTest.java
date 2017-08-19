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
import org.oulipo.net.TumblerAddress;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Request;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class RequestMapperTest {

	@Test
	public void a() throws Exception {
		Request mock = mock(Request.class);
		
		when(mock.params(":networkId")).thenReturn("1");
		when(mock.params(":nodeId")).thenReturn("2");

		RequestMapper mapper = new RequestMapper(new ObjectMapper());
		TumblerAddress tumbler = mapper.createNodeAddress(mock);
		System.out.println(tumbler.value);
	}
}
