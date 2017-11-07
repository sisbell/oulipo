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
package org.oulipo.security.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.oulipo.security.session.SessionManager;

public class AuthResourceTest {

	@Test
	public void temporaryAuthToken() throws Exception {
		SessionManager mockManager = mock(SessionManager.class);
		when(mockManager.storeNewTempToken()).thenReturn("dummyToken");

		AuthResource resource = new AuthResource(mockManager, null, "localhost");
		TempTokenResponse response = resource.temporaryAuthToken("doc");
		assertNotNull(response.token);

		assertEquals("xanauth://localhost/auth?token=dummyToken&scope=doc", response.xanauth);

	}
}
