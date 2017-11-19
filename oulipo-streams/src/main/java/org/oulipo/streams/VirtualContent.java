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

import org.oulipo.streams.types.Invariant;

public final class VirtualContent {

	/**
	 * Readable text that the invariant span is pointing to.
	 */
	public String content;

	/**
	 * Home document of the invariant spans and content
	 */
	public String documentHash;

	/**
	 * The invariant. This maps to the immutable text or media of the homeDocument.
	 */
	public Invariant invariant;

	/**
	 * Positional order of this content within the document
	 */
	public int order;

	@Override
	public String toString() {
		return "VirtualContent [content=" + content + ", documentHash=" + documentHash + ", invariant=" + invariant
				+ ", order=" + order + "]";
	}

}
