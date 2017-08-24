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
package org.oulipo.resources;

import org.oulipo.net.TumblerAddress;
import org.oulipo.net.TumblerResourceException;

public class ResourceNotFoundException extends TumblerResourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 440364700238709821L;
	
	private int code;

	public ResourceNotFoundException(TumblerAddress tumblerAddress, int code) {
		super(tumblerAddress);
		this.code = code;
	}

	public ResourceNotFoundException(TumblerAddress tumblerAddress, int code, 
			String message) {
		super(tumblerAddress, message);
		this.code = code;
	}

	public ResourceNotFoundException(TumblerAddress tumblerAddress, int code, 
			String message, Throwable cause) {
		super(tumblerAddress, message, cause);
		this.code = code;
	}

	public ResourceNotFoundException(TumblerAddress tumblerAddress, int code, 
			Throwable cause) {
		super(tumblerAddress, cause);
		this.code = code;
	}

	public int getCode() {
		return code;
	}	
}
