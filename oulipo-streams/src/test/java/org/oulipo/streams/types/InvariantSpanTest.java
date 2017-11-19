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
package org.oulipo.streams.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.StreamElementPartition;

public class InvariantSpanTest {

	public static final String documentHash = "fakeHash";

	@Test
	public void equality() throws Exception {
		InvariantSpan s1 = new InvariantSpan(10, 50, "ted://1");
		InvariantSpan s2 = new InvariantSpan(10, 50, "ted://1");
		assertEquals(s1, s2);
	}

	@Test(expected = MalformedSpanException.class)
	public void illegalStart() throws Exception {
		new InvariantSpan(0, 100, documentHash);
	}

	@Test
	public void inequality() throws Exception {
		InvariantSpan s1 = new InvariantSpan(10, 60, "ted://1");
		InvariantSpan s2 = new InvariantSpan(10, 50, "ted://1");
		assertFalse(s1.equals(s2));
	}

	@Test
	public void nullTumblerOk() throws Exception {
		new InvariantSpan(100, 10, "");
	}

	@Test
	public void split() throws Exception {
		InvariantSpan span = new InvariantSpan(1, 4, documentHash);
		StreamElementPartition<InvariantSpan> result = span.split(1);
		assertEquals(new InvariantSpan(1, 1, documentHash), result.getLeft());
		assertEquals(new InvariantSpan(2, 3, documentHash), result.getRight());
	}

	@Test
	public void splitEnd() throws Exception {
		InvariantSpan span = new InvariantSpan(1, 4, documentHash);
		StreamElementPartition<InvariantSpan> part = span.split(3);
		assertEquals(new InvariantSpan(1, 3, documentHash), part.getLeft());
		assertEquals(new InvariantSpan(4, 1, documentHash), part.getRight());
	}

	@Test
	public void splitOk() throws Exception {
		InvariantSpan span = new InvariantSpan(100, 10, documentHash);
		StreamElementPartition<InvariantSpan> partition = span.split(5);
		assertEquals(100L, partition.getLeft().getStart());
		assertEquals(5L, partition.getLeft().getWidth());
		assertEquals(105L, partition.getRight().getStart());
		assertEquals(5L, partition.getRight().getWidth());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void splitOverUpperBound() throws Exception {
		InvariantSpan span = new InvariantSpan(100, 10, documentHash);
		span.split(15);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void splitStart() throws Exception {
		InvariantSpan span = new InvariantSpan(1, 4, documentHash);
		span.split(0);
	}

	@Test(expected = MalformedSpanException.class)
	public void zeroWidth() throws Exception {
		new InvariantSpan(100, 0, documentHash);
	}

}
