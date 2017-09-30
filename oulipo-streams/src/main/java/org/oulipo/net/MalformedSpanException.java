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
package org.oulipo.net;

import org.oulipo.streams.Span;

/**
 * Thrown to indicate that a malformed span has occurred. This can occur if the
 * width or start position of a span is zero.
 */
public class MalformedSpanException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1820892557728771392L;

	private Span span;

	public MalformedSpanException(Span span) {
		super();
		this.span = span;
	}

	public MalformedSpanException(Span span, String message) {
		super(message);
		this.span = span;
	}

	public MalformedSpanException(Span span, String message, Throwable cause) {
		super(message, cause);
		this.span = span;
	}

	public MalformedSpanException(Span span, String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.span = span;
	}

	public MalformedSpanException(Span span, Throwable cause) {
		super(cause);
		this.span = span;
	}

	/**
	 * Construct a <code>MalformedSpanException</code> with the specified message
	 * 
	 * @param message
	 *            the detail message
	 */
	public MalformedSpanException(String message) {
		super(message);
	}

	public Span getSpan() {
		return span;
	}

}
