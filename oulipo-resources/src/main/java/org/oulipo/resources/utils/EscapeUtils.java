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

public class EscapeUtils {
	/**
	 * Escapes a Unicode string to an all-ASCII character sequence. Any special
	 * characters are escaped using backslashes (<tt>"</tt> becomes <tt>\"</tt>,
	 * etc.), and non-ascii/non-printable characters are escaped using Unicode
	 * escapes (<tt>&#x5C;uxxxx</tt> and <tt>&#x5C;Uxxxxxxxx</tt>).
	 **/
	// http://grepcode.com/file_/repo1.maven.org/maven2/org.openrdf/rio/1.0.9/org/openrdf/rio/ntriples/NTriplesUtil.java/?v=source
	public static String escapeString(String label) {
		int labelLength = label.length();
		StringBuffer result = new StringBuffer(2 * labelLength);

		for (int i = 0; i < labelLength; i++) {
			char c = label.charAt(i);
			int cInt = c;

			if (c == '\\') {
				result.append("\\\\");
			} else if (c == '"') {
				result.append("\\\"");
			} else if (c == '\n') {
				result.append("\\n");
			} else if (c == '\r') {
				result.append("\\r");
			} else if (c == '\t') {
				result.append("\\t");
			} else if (cInt >= 0x0 && cInt <= 0x8 || cInt == 0xB || cInt == 0xC || cInt >= 0xE && cInt <= 0x1F
					|| cInt >= 0x7F && cInt <= 0xFFFF) {
				result.append("\\u");
				result.append(toHexString(cInt, 4));
			} else if (cInt >= 0x10000 && cInt <= 0x10FFFF) {
				result.append("\\U");
				result.append(toHexString(cInt, 8));
			} else {
				result.append(c);
			}
		}

		return result.toString();
	}

	/**
	 * Converts a decimal value to a hexadecimal string represention of the
	 * specified length.
	 *
	 * @param decimal
	 *            A decimal value.
	 * @param stringLength
	 *            The length of the resulting string.
	 **/
	// http://grepcode.com/file_/repo1.maven.org/maven2/org.openrdf/rio/1.0.9/org/openrdf/rio/ntriples/NTriplesUtil.java/?v=source
	public static String toHexString(int decimal, int stringLength) {
		StringBuffer result = new StringBuffer(stringLength);

		String hexVal = Integer.toHexString(decimal).toUpperCase();

		// insert zeros if hexVal has less than stringLength characters:
		int nofZeros = stringLength - hexVal.length();
		for (int i = 0; i < nofZeros; i++) {
			result.append('0');
		}

		result.append(hexVal);

		return result.toString();
	}
}
