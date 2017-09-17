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
package org.oulipo.browser.framework;

import java.util.Arrays;

public class HistoryTraverser {

	private String[] array = new String[100];

	private int index = -1;

	private int length;

	public void add(String address) {
		if (index == array.length) {
			resize(2 * array.length);
		}
		array[++index] = address;
		length = index + 1;
	}

	public boolean hasNext() {
		return index < length - 1;
	}

	public boolean hasPrevious() {
		return index > 0;
	}

	public String next() {
		if (hasNext()) {
			return array[++index];
		}
		return null;
	}

	public String previous() {
		if (hasPrevious()) {
			return array[--index];
		}
		return null;
	}

	private void resize(int max) {
		String[] temp = new String[max];
		for (int i = 0; i < length; i++) {
			temp[i] = array[i];
		}
		array = temp;
	}

	@Override
	public String toString() {
		return "HistoryTraverser [array=" + Arrays.toString(array) + ", index=" + index + ", length=" + length + "]";
	}

}
