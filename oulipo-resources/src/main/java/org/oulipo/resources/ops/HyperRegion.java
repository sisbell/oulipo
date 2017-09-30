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
package org.oulipo.resources.ops;

/**
 * Includes position within a containing rendered view
 */
public class HyperRegion {

	private int start;

	private final int width;

	/**
	 * Constructor specifying start position of a region within a rendered view and
	 * width of the region.
	 * 
	 * @param start
	 * @param width
	 */
	public HyperRegion(int start, int width) {
		this.start = start;
		this.width = width;
	}

	public final int end() {
		return start + width;
	}

	public final int getLength() {
		return width;
	}

	public final int getStart() {
		return start;
	}

	@Override
	public String toString() {
		return "HyperRegion [start=" + start + ", length=" + width + "]";
	}
}
