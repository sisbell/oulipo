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
package org.oulipo.streams.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.OulipoMachine;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VirtualContent;
import org.oulipo.streams.types.InvariantSpan;
import org.oulipo.streams.types.Overlay;

public class DefaultOulipoMachineTest {

	private static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			for (String file : dir.list()) {
				deleteDir(new File(dir, file));
			}
		}
		dir.delete();
	}

	private DefaultStreamLoader streamLoader;

	private File testDir;

	@Test
	public void append() throws Exception {
		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1924.1.1");

		DefaultOulipoMachine som = DefaultOulipoMachine.createWritableMachine(streamLoader, new MockRemoteFileManager(),
				homeDocument);
		InvariantSpan span = som.append("Hello");
		assertEquals(span.getStart(), 1);
		assertEquals(span.getWidth(), 5);

		span = som.append("World");
		assertEquals(span.getStart(), 6);
		assertEquals(span.getWidth(), 5);
	}
	
	@After
	public void cleanup() {
		deleteDir(new File("target/test-streams"));
	}

	
	@Test
	public void deleteRange() throws Exception {
		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1924.1.1");

		DefaultOulipoMachine som = DefaultOulipoMachine.createWritableMachine(streamLoader, new MockRemoteFileManager(),
				homeDocument);
		som.insert(1, "My first document");
		som.putOverlay(1,  new Overlay(17));
		
		som.deleteVariant(new VariantSpan(4, 6));
				
		assertEquals("My document", getText(som));
	}
	
	@Test
	public void getText() throws Exception {
		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1925.1.1");

		DefaultOulipoMachine som = DefaultOulipoMachine.createWritableMachine(streamLoader, new MockRemoteFileManager(),
				homeDocument);
		som.append("Hello");
		som.append("World");
		String result = som.getText(new InvariantSpan(5, 5, homeDocument));
		assertEquals("oWorl", result);
	}

	private String getText(OulipoMachine om) throws IOException, MalformedSpanException {
		StringBuilder sb = new StringBuilder();
		List<VirtualContent> virtual = om.getVirtualContent();
		for(VirtualContent vc : virtual) {
			sb.append(vc.content);
		}
		return sb.toString();
	}

	@Test
	public void insertAndGetText() throws Exception {
		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1924.1.1");

		DefaultOulipoMachine som = DefaultOulipoMachine.createWritableMachine(streamLoader, new MockRemoteFileManager(),
				homeDocument);
		som.insert(1, "My first document");
		assertEquals("first", som.getText(new InvariantSpan(4, 5, homeDocument)));
	}

	@Test
	public void mixTest() throws Exception {
		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1924.3.1");

		DefaultOulipoMachine machine = DefaultOulipoMachine.createWritableMachine(streamLoader, new MockRemoteFileManager(),
				homeDocument);
		machine.insert(1, "Hello my name is Simon");
		assertEquals("Hello my name is Simon", getText(machine));

		machine.deleteVariant(new VariantSpan(18, 5));
		machine.insert(18, "Shane");		
		assertEquals("Hello my name is Shane", getText(machine));
		
		machine.insert(17, " not");		
		assertEquals("Hello my name is not Shane", getText(machine));
		
		machine.deleteVariant(new VariantSpan(22, 5));
		assertEquals("Hello my name is not ", getText(machine));

		machine.moveVariant(22, new VariantSpan(1, 6));
		assertEquals("my name is not Hello ", getText(machine));

	}
	
	@Test
	public void moveVariant() throws Exception {
		TumblerAddress homeDocument = TumblerAddress.create("1.999.0.56831.0.1924.3.1");

		DefaultOulipoMachine machine = DefaultOulipoMachine.createWritableMachine(streamLoader, new MockRemoteFileManager(),
				homeDocument);
		machine.insert(1, "My first document");
		assertEquals("My first document", getText(machine));

		machine.moveVariant(1, new VariantSpan(4, 6));
				
		assertEquals("first My document", getText(machine));
	}

	@Before
	public void setup() {
		String spec = "maximumSize=10000,expireAfterWrite=10m";
		testDir = new File("target/test-streams-" + System.currentTimeMillis());
		streamLoader = new DefaultStreamLoader(testDir, spec);
	}
}
