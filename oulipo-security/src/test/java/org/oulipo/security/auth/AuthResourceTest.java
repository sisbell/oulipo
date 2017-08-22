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
