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

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;

/**
 * This represents one section of a tumbler. Can contain no zero elements
 */
public final class TumblerField {

	public static TumblerField create(String field, int size) throws MalformedTumblerException {
		List<Integer> items = createList(field);
		if (items.size() != size) {
			throw new MalformedTumblerException(
					"Field does not match specified size: is = " + items.size() + ", expected = " + size);
		}
		return new TumblerField(items);
	}

	public boolean isSystemField() {
		return !numbers.isEmpty() && (numbers.stream().allMatch(x -> x == 1));
	}

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

	public static TumblerField create(String field) throws MalformedTumblerException {
		return new TumblerField(createList(field));
	}

	public static TumblerField createEmpty() {
		return new TumblerField();
	}

	private final LinkedList<Integer> numbers;

	private TumblerField() {
		this.numbers = new LinkedList<>();
	}

	public LinkedList<Integer> getNumbers() {
		return numbers;
	}

	private TumblerField(List<Integer> numbers) throws MalformedTumblerException {
		if (numbers.isEmpty()) {
			throw new MalformedTumblerException("Field is empty");
		}
		if (numbers.stream().anyMatch(x -> x <= 0)) {
			throw new MalformedTumblerException("Illegal value(s): " + numbers);
		}
		this.numbers = new LinkedList<>(numbers);// TODO: umodifiable
	}

	public TumblerField append(int fieldValue) throws MalformedTumblerException {
		if (fieldValue <= 0) {
			throw new MalformedTumblerException("Illegal value: " + fieldValue);
		}
		numbers.add(fieldValue);
		return this;
	}

	public int get(int index) {
		return numbers.get(index);
	}

	public TumblerField remove(int index) {
		numbers.remove(index);
		return this;
	}

	public int size() {
		return numbers.size();
	}

	public TumblerField set(int index, int fieldValue) throws MalformedTumblerException {
		if (fieldValue <= 0) {
			throw new MalformedTumblerException("Illegal value: " + fieldValue);
		}
		numbers.set(index, fieldValue);
		return this;
	}

	public byte[] getBytes() throws MalformedTumblerException {
		return asString().getBytes();
	}

	public String asString() throws MalformedTumblerException {
		if (numbers.isEmpty()) {
			throw new MalformedTumblerException("Tumbler is empty");
		}
		return numbers.stream().map(i -> i.toString()).collect(Collectors.joining("."));
	}

	@Override
	public String toString() {
		return "TumblerField [numbers=" + numbers + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numbers == null) ? 0 : numbers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TumblerField other = (TumblerField) obj;
		if (numbers == null) {
			if (other.numbers != null)
				return false;
		} else {
			try {
				return asString().equals(other.asString());
			} catch (MalformedTumblerException e) {

			}
			return false;
		}
		return true;
	}

}
