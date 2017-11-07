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
package org.oulipo.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TumblerFieldTest {

	@Test(expected = MalformedTumblerException.class)
	public void appendNegative() throws Exception {
		TumblerField partition = TumblerField.createEmpty();
		partition.append(-1);
	}

	@Test
	public void appendTwo() throws MalformedTumblerException {
		TumblerField partition = TumblerField.createEmpty();
		partition.append(1).append(2);
		assertEquals("1.2", partition.asString());
	}

	@Test(expected = MalformedTumblerException.class)
	public void appendZero() throws Exception {
		TumblerField partition = TumblerField.createEmpty();
		partition.append(0);
	}

	@Test
	public void create() throws Exception {
		TumblerField partition = TumblerField.create("4.2.3");
		assertEquals(4, partition.get(0));
		assertEquals(2, partition.get(1));
		assertEquals(3, partition.get(2));
	}

	@Test(expected = MalformedTumblerException.class)
	public void createWithAlpha() throws Exception {
		TumblerField.create("4.2.a");
	}

	@Test(expected = MalformedTumblerException.class)
	public void createWithNegative() throws Exception {
		TumblerField.create("4.2.-10");
	}

	@Test(expected = MalformedTumblerException.class)
	public void createWithZero() throws Exception {
		TumblerField.create("4.2.0");
	}

	@Test(expected = MalformedTumblerException.class)
	public void emptyStringMalformed() throws Exception {
		TumblerField.createEmpty().asString();
	}

	@Test(expected = MalformedTumblerException.class)
	public void endWithEmptyDot() throws Exception {
		TumblerField.create("4.2.");
	}

	@Test
	public void isEquals() throws Exception {
		TumblerField f1 = TumblerField.create("1.1.1");
		TumblerField f2 = TumblerField.create("1.1.1");
		assertTrue(f1.equals(f2));
		assertTrue(f2.equals(f1));
	}

	@Test(expected = MalformedTumblerException.class)
	public void justDot() throws Exception {
		TumblerField.create(".");
	}

	@Test(expected = MalformedTumblerException.class)
	public void middleDots() throws Exception {
		TumblerField.create("4..2");
	}

	@Test(expected = MalformedTumblerException.class)
	public void multipleField() throws Exception {
		TumblerField.create("1.1.0.1.0.1.1.1");
	}

	@Test
	public void notEquals() throws Exception {
		TumblerField f1 = TumblerField.create("2.1.1");
		TumblerField f2 = TumblerField.create("1.1.1");
		assertFalse(f1.equals(f2));
		assertFalse(f2.equals(f1));
	}

	@Test
	public void notEqualsDifferentSize() throws Exception {
		TumblerField f1 = TumblerField.create("1.1.1");
		TumblerField f2 = TumblerField.create("1.1.1.2");
		assertFalse(f1.equals(f2));
		assertFalse(f2.equals(f1));
	}

	@Test
	public void notSystemField() throws Exception {
		TumblerField partition = TumblerField.create("1.2");
		assertFalse(partition.isSystemField());
	}

	@Test(expected = MalformedTumblerException.class)
	public void nullCreate() throws Exception {
		TumblerField.create(null);
	}

	@Test(expected = MalformedTumblerException.class)
	public void prependWithEmptyDot() throws Exception {
		TumblerField.create(".4.2");
	}

	@Test
	public void remove() throws Exception {
		TumblerField partition = TumblerField.create("1.2.3");
		partition.remove(1);
		assertTrue(TumblerField.create("1.3").equals(partition));
	}

	@Test(expected = MalformedTumblerException.class)
	public void setNegative() throws Exception {
		TumblerField partition = TumblerField.createEmpty().append(2);
		partition.set(0, -1);
	}

	@Test(expected = MalformedTumblerException.class)
	public void setZero() throws Exception {
		TumblerField partition = TumblerField.createEmpty().append(2);
		partition.set(0, 0);
	}

	@Test
	public void systemField() throws Exception {
		TumblerField partition = TumblerField.create("1.1");
		assertTrue(partition.isSystemField());
	}

}
