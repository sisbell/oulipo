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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.types.SpanElement;
import org.oulipo.streams.types.StreamElementPartition;

public class SpanElementTest {

	public static final TumblerAddress homeDocument = TumblerAddress.createWithNoException("ted://1.2.0.2.0.23.1.1");

	@Test
	public void equality() throws Exception {
		SpanElement s1 = new SpanElement(10, 50, "ted://1");
		SpanElement s2 = new SpanElement(10, 50, "ted://1");
		assertEquals(s1, s2);
	}

	@Test(expected = MalformedSpanException.class)
	public void illegalStart() throws Exception {
		new SpanElement(0, 100, homeDocument);
	}

	@Test
	public void inequality() throws Exception {
		SpanElement s1 = new SpanElement(10, 60, "ted://1");
		SpanElement s2 = new SpanElement(10, 50, "ted://1");
		assertFalse(s1.equals(s2));
	}

	@Test
	public void nullTumblerOk() throws Exception {
		new SpanElement(100, 10, "");
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void splitLowerBound() throws Exception {
		SpanElement span = new SpanElement(100, 10, homeDocument);
		StreamElementPartition<SpanElement> partition = span.split(100);
		System.out.println(partition);
	}

	@Test
	public void splitOk() throws Exception {
		SpanElement span = new SpanElement(100, 10, homeDocument);
		StreamElementPartition<SpanElement> partition = span.split(105);
		assertEquals(100L, partition.getLeft().getStart());
		assertEquals(5L, partition.getLeft().getWidth());
		assertEquals(105L, partition.getRight().getStart());
		assertEquals(5L, partition.getRight().getWidth());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void splitOverUpperBound() throws Exception {
		SpanElement span = new SpanElement(100, 10, homeDocument);
		span.split(120);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void splitUnderLowerBound() throws Exception {
		SpanElement span = new SpanElement(100, 10, homeDocument);
		span.split(90);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void splitUpperBound() throws Exception {
		SpanElement span = new SpanElement(100, 10, homeDocument);
		span.split(110);
	}

	@Test(expected = MalformedSpanException.class)
	public void zeroWidth() throws Exception {
		new SpanElement(100, 0, homeDocument);
	}

}
