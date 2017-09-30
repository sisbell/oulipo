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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;

public class CompactTumblerAddress {

	private static List<Integer> createList(String field) throws MalformedTumblerException {

		if (Strings.isNullOrEmpty(field)) {
			throw new MalformedTumblerException("Field is empty");
		}

		if (field.endsWith(".")) {
			throw new MalformedTumblerException("Field cannot end with . ");
		}

		try {
			return Stream.of(field.split("[.]")).map(Integer::parseInt).collect(Collectors.toList());
		} catch (NumberFormatException e) {
			throw new MalformedTumblerException("Field contains non-numeric input: " + field);
		}
	}

	private final LinkedList<Integer> numbers;

	public CompactTumblerAddress() {
		this.numbers = new LinkedList<>();
	}

	public CompactTumblerAddress(List<Integer> numbers) throws MalformedTumblerException {
		if (numbers.isEmpty()) {
			throw new MalformedTumblerException("Field is empty");
		}
		if (numbers.stream().anyMatch(x -> x <= 0)) {
			throw new MalformedTumblerException("Illegal value(s): " + numbers);
		}
		this.numbers = new LinkedList<>(numbers);// TODO: umodifiable
	}

	public CompactTumblerAddress(String field) throws MalformedTumblerException {
		numbers = new LinkedList<>(createList(field));
	}

	public CompactTumblerAddress append(int digit) throws MalformedTumblerException {
		if (digit <= 0) {
			throw new MalformedTumblerException("Illegal value: " + digit);
		}
		numbers.add(digit);
		return this;
	}

	public String asString() throws MalformedTumblerException {
		if (numbers.isEmpty()) {
			throw new MalformedTumblerException("Tumbler is empty");
		}
		return numbers.stream().map(i -> i.toString()).collect(Collectors.joining("."));
	}

	public void compactExponent() {
		if (numbers.size() <= 1) {
			return;
		}
		int expCount = -1;

		Iterator<Integer> it = numbers.listIterator();
		while (it.hasNext()) {
			Integer i = it.next();
			if (i == 0) {
				numbers.remove();
				expCount++;
			} else {
				break;
			}
		}

		numbers.push(expCount);

	}

	public int get(int index) {
		return numbers.get(index);
	}

	public byte[] getBytes() throws MalformedTumblerException {
		return asString().getBytes();
	}

	public LinkedList<Integer> getTumblerDigits() {
		return numbers;
	}

	public CompactTumblerAddress remove(int index) {
		numbers.remove(index);
		return this;
	}

	public CompactTumblerAddress set(int index, int fieldValue) throws MalformedTumblerException {
		if (fieldValue <= 0) {
			throw new MalformedTumblerException("Illegal value: " + fieldValue);
		}
		numbers.set(index, fieldValue);
		return this;
	}

	public int size() {
		return numbers.size();
	}

}
