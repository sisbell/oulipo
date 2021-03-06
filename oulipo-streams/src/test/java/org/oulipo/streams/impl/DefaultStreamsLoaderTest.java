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

import java.io.File;

import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.types.Invariant;
import org.oulipo.streams.types.InvariantSpan;

public class DefaultStreamsLoaderTest {
	public static final String documentHash = "fakeHash";

	// @Test
	public void a() throws Exception {
		String spec = "maximumSize=10000,expireAfterWrite=10m";
		File testDir = new File("test-streams");
		StreamLoader streamLoader = new DefaultStreamLoader(testDir, spec);
		VariantStream<Invariant> vs = streamLoader.openInvariantVariantStream(documentHash);
		vs.put(1, new InvariantSpan(1, 10, documentHash));
		streamLoader.flushVariantCache();
	}
}
