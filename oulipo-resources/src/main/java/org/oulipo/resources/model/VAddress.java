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
package org.oulipo.resources.model;

import org.oulipo.net.CompactTumblerAddress;
import org.oulipo.net.MalformedTumblerException;

/**
 * These tumblers are relative to a given document, so they only have one or two
 * digits. The first digit determines the space (1=text, 2=link). The second
 * digit determines the location within that space.
 * 
 * As an example, if the VAddress is 1.230, it denotes the byte at position 230
 * within the content region.
 */
public final class VAddress extends CompactTumblerAddress {

	/**
	 * Number of preceding zeros
	 */
	private final int exponent;

	private final int sequence;

	/**
	 * Tumbler type (1=text, 2=link)
	 */
	private final int type;

	/**
	 * Constructor. Used for types that do not require a sequence
	 * 
	 * @param exponent
	 *            number of preceding zeros
	 * @param type
	 *            tumbler type
	 * @throws MalformedTumblerException 
	 */
	public VAddress(int exponent, int type) throws MalformedTumblerException {
		this(exponent, type, -1);
	}

	/**
	 * Constructor
	 * 
	 * @param exponent
	 *            number of preceding zeros
	 * @param type
	 *            tumbler type (1=text, 2=link)
	 * @param sequence
	 *            location
	 * @throws MalformedTumblerException
	 */
	public VAddress(int exponent, int type, int sequence)
			throws MalformedTumblerException {
		this.exponent = exponent;
		this.type = type;
		this.sequence = sequence;
		
		append(exponent).append(type);
		if(sequence != -1) {
			append(sequence);
		}	
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VAddress other = (VAddress) obj;
		if (exponent != other.exponent)
			return false;
		if (sequence != other.sequence)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public int getExponent() {
		return exponent;
	}

	public int getSequence() {
		return sequence;
	}

	public int getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + exponent;
		result = prime * result + sequence;
		result = prime * result + type;
		return result;
	}

	@Override
	public String toString() {
		return "VAddress [exponent=" + exponent + ", sequence=" + sequence
				+ ", type=" + type + "]";
	}
}
