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
package org.oulipo.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
 
import com.fasterxml.jackson.databind.ObjectMapper;

public class TumblerAddressTest {

	@Test
	public void createSimpleNetworkOk() throws Exception {
		TumblerAddress ta = TumblerAddress.create("ted://1");
		assertEquals(1, ta.getNetwork().get(0));
		assertEquals("ted", ta.getScheme());
	}

	@Test
	public void createNodeOk() throws Exception {
		TumblerAddress ta = TumblerAddress.create("ted://1.5.1");
		assertEquals(5, ta.getNode().get(0));
		assertEquals(1, ta.getNode().get(1));
	}

	@Test
	public void createUserOk() throws Exception {
		TumblerAddress ta = TumblerAddress.create("ted://1.5.1.0.8.4");
		assertEquals(8, ta.getUser().get(0));
		assertEquals(4, ta.getUser().get(1));
	}

	@Test
	public void createDocumentOk() throws Exception {
		TumblerAddress ta = TumblerAddress
				.create("ted://1.5.1.0.8.4.0.16.4");
		assertEquals(16, ta.getDocument().get(0));
		assertEquals(4, ta.getDocument().get(1));
	}

	@Test
	public void elementsEqual() throws Exception {
		TumblerAddress ta = TumblerAddress
				.create("ted://1.5.1.0.8.4.0.16.4.0.2.43.62");
		TumblerAddress ta2 = TumblerAddress
				.create("ted://1.5.1.0.8.4.0.16.4.0.2.43.62");
		assertTrue(ta.equals(ta2));
	}

	@Test
	public void elementsNotEqual() throws Exception {
		TumblerAddress ta = TumblerAddress
				.create("ted://1.5.1.0.8.4.0.16.4.0.2.43.62");
		TumblerAddress ta2 = TumblerAddress
				.create("ted://1.5.1.0.8.4.0.16.4.0.2.43.63");
		assertFalse(ta.equals(ta2));
	}

	@Test
	public void createBytesOk() throws Exception {
		TumblerAddress ta = TumblerAddress
				.create("ted://1.5.1.0.8.4.0.16.4.0.2.43.62");
		assertEquals(2, ta.getElement().get(0));
		assertEquals(43, ta.getElement().get(1));
		assertEquals(62, ta.getElement().get(2));
	}

	@Test
	public void deepCopyOk() throws Exception {
		TumblerAddress ta = TumblerAddress
				.create("ted://1.5.1.0.8.4.0.16.4.0.2.43.62");
		TumblerAddress ta2 = new TumblerAddress.Builder(ta).build();
		assertTrue(ta.equals(ta2));
	}

	@Test
	public void globalMainNet() throws Exception {
		assertEquals("1", TumblerAddress.createMainNet().asString());
	}

	@Test(expected = MalformedTumblerException.class)
	public void nullSchemeThrowsException() throws Exception {
		new TumblerAddress.Builder(null, "1.1");
	}

	@Test(expected = MalformedTumblerException.class)
	public void emptyNetworkThrowsException() throws Exception {
		new TumblerAddress.Builder("ted", "");
	}

	@Test
	public void buildSimpleTumbler() throws Exception {
		new TumblerAddress.Builder("ted", "1.1").build();
		new TumblerAddress.Builder().build();
	}

	@Test(expected = MalformedTumblerException.class)
	public void addDocumentWithoutUser() throws Exception {
		new TumblerAddress.Builder("ted", "1.1").document("1").build();
	}

	@Test
	public void elementJumpLinkOk() throws Exception {
		new TumblerAddress.Builder().node("1").user("1").document("100")
				.element("2.1.2").build();
	}

	@Test
	public void elementOverlayLinkOk() throws Exception {
		new TumblerAddress.Builder().node("1").user("1").document("100")
				.element("2.2.2").build();
	}

	@Test(expected = MalformedTumblerException.class)
	public void elementLinkNoSubtype() throws Exception {
		new TumblerAddress.Builder().node("1").user("1").document("100")
				.element("1").build();
	}

	@Test
	public void elementBytesOk() throws Exception {
		new TumblerAddress.Builder().node("1").user("1").document("100")
				.element("1.1").build();
	}


	@Test(expected = MalformedTumblerException.class)
	public void elementBytesNegative() throws Exception {
		new TumblerAddress.Builder().node("1").user("1").document("100")
				.element("1.-1").build();
	}


	@Test(expected = MalformedTumblerException.class)
	public void elementBytesToManyElements() throws Exception {
		new TumblerAddress.Builder().node("1").user("1").document("100")
				.element("1.1.1").build();
	}

	@Test(expected = MalformedTumblerException.class)
	public void elementZero() throws Exception {
		new TumblerAddress.Builder().node("1").user("1").document("100")
				.element("0.1").build();
	}

	@Test(expected = MalformedTumblerException.class)
	public void elementOutOfBounds() throws Exception {
		new TumblerAddress.Builder().node("1").user("1").document("100")
				.element("5.1").build();
	}

	@Test
	public void serialize() throws Exception {
		TumblerAddress ta = TumblerAddress
				.create("ted://1.5.1.0.8.4.0.16.4.0.2.43.62");
		String result = new ObjectMapper().writeValueAsString(ta);
		assertEquals("\"ted://1.5.1.0.8.4.0.16.4.0.2.43.62\"",
				result);
	}

	@Test
	public void deserialize() throws Exception {
		TumblerAddress ta = TumblerAddress
				.create("ted://1.5.1.0.8.4.0.16.4.0.2.43.62");
		TumblerAddress result = new ObjectMapper().readValue(
				"\"ted://1.5.1.0.8.4.0.16.4.0.2.43.62\"",
				TumblerAddress.class);
		assertEquals(ta, result);
	}
}
