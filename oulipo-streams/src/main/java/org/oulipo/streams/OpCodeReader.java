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

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.streams.opcodes.InsertTextOp;
import org.oulipo.streams.opcodes.Op;
import org.oulipo.streams.opcodes.SwapOp;
import org.oulipo.streams.opcodes.SwapOp.Data;

public final class OpCodeReader implements Iterable<Op<?>>, Closeable {

	private class OpIterator implements Iterator<Op<?>> {

		private Op<?> nextOp;

		@Override
		public boolean hasNext() {
			boolean hasNext = false;
			try {
				hasNext = nextOp != null || nextOp() != null;
			} catch (IOException e) {
				throw new IllegalStateException("Exception reading input", e);
			} catch (MalformedSpanException e) {
				throw new IllegalStateException("Malformed op code", e);
			}
			System.out.println("hasNext: " + hasNext);
			if (!hasNext) {
				try {
					System.out.println("Closing");
					input.close();
				} catch (IOException e) {
					throw new IllegalStateException("Exception closing stream", e);
				}
			}
			return hasNext;
		}

		@Override
		public Op<?> next() {
			if (!hasNext()) {
				System.out.println("NoSuchElement");
				throw new NoSuchElementException();
			}
			Op<?> result = nextOp;
			nextOp = null;
			System.out.println("Next: " + result);
			return result;
		}

		private Op<?> nextOp() throws IOException, MalformedSpanException {
			byte opCode;
			try {
				opCode = input.readByte();
			} catch (EOFException e) {
				System.out.println("EOF");
				return null;
			}
			System.out.println("OP CODE: " + opCode);
			switch (opCode) {
			case Op.COPY:

				break;
			case Op.DELETE:

				break;
			case Op.INSERT_TEXT:
				System.out.println("NextOP: insertText");

				return nextOp = InsertTextOp.read(input);
			case Op.MOVE:

				break;
			case Op.PUT:

				break;
			case Op.SWAP:
				VariantSpan v1 = new VariantSpan(input.readLong(), input.readLong());
				VariantSpan v2 = new VariantSpan(input.readLong(), input.readLong());
				return nextOp = new SwapOp(new Data(v1, v2));
			}
			System.out.println("Return null opCode");
			;
			return null;
		}

	}

	private final DataInputStream input;

	public OpCodeReader(DataInputStream input) {
		this.input = input;
	}

	public OpCodeReader(File file) throws FileNotFoundException {
		this(new DataInputStream(new FileInputStream(file)));
	}

	@Override
	public void close() throws IOException {
		input.close();
	}

	@Override
	public Iterator<Op<?>> iterator() {
		return new OpIterator();
	}
}
