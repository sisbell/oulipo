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
package org.oulipo.resources.utils;

import java.lang.reflect.Array;
import java.util.Collection;

import com.google.common.collect.ObjectArrays;

/**
 * Utility for adding arrays
 */
public class Add {

	@SuppressWarnings("unchecked")
	public static <T> T[] both(T[] array, Collection<T> object, Class<T> type) {

		if (array == null && object != null) {
			T[] o = (T[]) Array.newInstance(type, object.size());
			return object.toArray(o);
		} else if (array != null && object == null) {
			return array;
		} else if (array == null && object == null) {
			return null;
		}
		return ObjectArrays.concat(array, object.toArray(array),
				(Class<T>) object.getClass());
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] both(T[] array, T[] object, Class<T> type) {
		if (array == null && object != null) {
			return object;
		} else if (array != null && object == null) {
			return array;
		} else if (array == null && object == null) {
			return null;
		}
		
		T[] concat = ObjectArrays.concat(array, object, type);
		return concat;
	}

	public static <T> T[] one(T[] array, T object) {
		if (array == null && object != null) {			
			T[] o = (T[]) Array.newInstance(object.getClass(), 1);
			o[0] = object;
			return o;
		} else if(array!= null && object == null) {
			return array;
		}
		return ObjectArrays.concat(array, object);
	}
	
	public static <T> T[] toArray(Collection<T> object, Class<T> type) {
		if(object == null) {
			return null;
		}
		T[] o = (T[]) Array.newInstance(type, object.size());
		return object.toArray(o);
	}
}
