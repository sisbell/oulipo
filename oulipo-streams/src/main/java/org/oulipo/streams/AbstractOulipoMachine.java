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

import java.io.IOException;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.streams.opcodes.CopyOp;
import org.oulipo.streams.opcodes.DeleteOp;
import org.oulipo.streams.opcodes.InsertTextOp;
import org.oulipo.streams.opcodes.MoveOp;
import org.oulipo.streams.opcodes.PutOp;
import org.oulipo.streams.opcodes.SwapOp;

import com.google.common.base.Strings;

/**
 * Implements the <code>VariantStream</code> methods as wrappers for the push
 * and write operations of the <code>OulipoMachine</code>
 */
public abstract class AbstractOulipoMachine implements OulipoMachine {

	private static void assertGreaterThanZero(long to) throws IllegalArgumentException {
		if (to < 1) {
			throw new IllegalArgumentException("to must be greater than 0");
		}
	}

	private static void assertSpanNotNull(Span span) {
		if (span == null) {
			throw new IllegalArgumentException("span is null");
		}
	}

	/**
	 * Constructs an OulipoMachine
	 */
	protected AbstractOulipoMachine() {
	}

	@Override
	public void copy(long to, VariantSpan variantSpan) throws MalformedSpanException, IOException {
		assertGreaterThanZero(to);
		assertSpanNotNull(variantSpan);
		push(writeOp(new CopyOp(to, variantSpan)));
	}

	@Override
	public void delete(VariantSpan variantSpan) throws MalformedSpanException, IOException {
		assertSpanNotNull(variantSpan);
		push(writeOp(new DeleteOp(variantSpan)));
	}

	@Override
	public void insert(long to, String text) throws IOException, MalformedSpanException {
		assertGreaterThanZero(to);
		if (Strings.isNullOrEmpty(text)) {
			throw new IllegalArgumentException("Text can't be empty");
		}
		push(writeOp(new InsertTextOp(to, text)));
	}

	@Override
	public void move(long to, VariantSpan variantSpan) throws MalformedSpanException, IOException {
		assertGreaterThanZero(to);
		assertSpanNotNull(variantSpan);
		push(writeOp(new MoveOp(variantSpan, to)));
	}

	@Override
	public void put(long to, Span invariantSpan) throws MalformedSpanException, IOException {
		assertGreaterThanZero(to);
		assertSpanNotNull(invariantSpan);
		push(writeOp(new PutOp(to, invariantSpan)));
	}

	@Override
	public void swap(VariantSpan v1, VariantSpan v2) throws MalformedSpanException, IOException {
		assertSpanNotNull(v1);
		assertSpanNotNull(v2);

		push(writeOp(new SwapOp(v1, v2)));
	}

}
