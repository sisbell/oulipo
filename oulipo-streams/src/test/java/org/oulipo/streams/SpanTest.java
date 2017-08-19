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
package org.oulipo.streams;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.Span;

@RunWith(JUnitPlatform.class)
public class SpanTest {
	
	@Test
	public void equality() throws Exception {
		Span s1 = new Span(10, 50, "ted://1");
		Span s2 = new Span(10, 50, "ted://1");
		assertEquals(s1, s2);
	}
	

	@Test
	public void inequality() throws Exception {
		Span s1 = new Span(10, 60, "ted://1");
		Span s2 = new Span(10, 50, "ted://1");
		assertFalse(s1.equals(s2));
	}
	
	//TODO: should authority include width?
	public void addTumbler() throws Exception {	
		String input = "ted://1.1.0.2.0.1.2.3";
		Span s1 = new Span(10, 60, "ted://1");
		TumblerAddress tumbler = s1.addToTumbler(TumblerAddress.create(input));
		assertEquals(input + ".0.1.10~1.60", tumbler.toTumblerAuthority());
	}



}
