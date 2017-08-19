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
package org.oulipo.security.session;

import java.util.Random;

public final class CodeGenerator {

	public static final String alphabet = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private static final String alphabet2 = "abcdefghijklmnopqrstuvxyz0123456789";

	public static final int size = alphabet.length();

	public static String generateCode() {
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			sb.append(alphabet.charAt(r.nextInt(size)));
		}
		return sb.toString();
	}

	public static String generateCode(int s) {
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s; i++) {
			sb.append(alphabet.charAt(r.nextInt(size)));
		}
		return sb.toString();
	}

	public static String generateID() {
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 32; i++) {
			sb.append(alphabet2.charAt(r.nextInt(size)));
		}
		return sb.toString();
	}
}
