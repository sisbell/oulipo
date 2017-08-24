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
package org.oulipo.services;

import org.oulipo.net.TumblerAddress;
import org.oulipo.net.TumblerResourceException;

/**
 * Thrown when a request is missing the request body
 */
public class MissingBodyException extends TumblerResourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4879473897613471882L;

	public MissingBodyException(TumblerAddress tumblerAddress) {
		super(tumblerAddress);
	}

	public MissingBodyException(TumblerAddress tumblerAddress, String message) {
		super(tumblerAddress, message);
	}

	public MissingBodyException(TumblerAddress tumblerAddress, String message, Throwable cause) {
		super(tumblerAddress, message, cause);
	}

	public MissingBodyException(TumblerAddress tumblerAddress, Throwable cause) {
		super(tumblerAddress, cause);
	}

}
