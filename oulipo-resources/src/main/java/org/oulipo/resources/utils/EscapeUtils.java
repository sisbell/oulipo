/*  Sesame - Storage and Querying architecture for RDF and RDF Schema
 *  Copyright (C) 2001-2006 Aduna
 *
 *  Contact: 
 *  	Aduna
 *  	Prinses Julianaplein 14 b
 *  	3817 CS Amersfoort
 *  	The Netherlands
 *  	tel. +33 (0)33 465 99 87
 *  	fax. +33 (0)33 465 99 87
 *
 *  	http://aduna-software.com/
 *  	http://www.openrdf.org/
 *  
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.oulipo.resources.utils;

public class EscapeUtils {
	/**
	 * Escapes a Unicode string to an all-ASCII character sequence. Any special
	 * characters are escaped using backslashes (<tt>"</tt> becomes <tt>\"</tt>,
	 * etc.), and non-ascii/non-printable characters are escaped using Unicode
	 * escapes (<tt>&#x5C;uxxxx</tt> and <tt>&#x5C;Uxxxxxxxx</tt>).
	 **/
	//http://grepcode.com/file_/repo1.maven.org/maven2/org.openrdf/rio/1.0.9/org/openrdf/rio/ntriples/NTriplesUtil.java/?v=source
	public static String escapeString(String label) {
		int labelLength = label.length();
		StringBuffer result = new StringBuffer(2 * labelLength);

		for (int i = 0; i < labelLength; i++) {
			char c = label.charAt(i);
			int cInt = (int)c;

			if (c == '\\') {
				result.append("\\\\");
			}
			else if (c == '"') {
				result.append("\\\"");
			}
			else if (c == '\n') {
				result.append("\\n");
			}
			else if (c == '\r') {
				result.append("\\r");
			}
			else if (c == '\t') {
				result.append("\\t");
			}
			else if (
				cInt >= 0x0 && cInt <= 0x8 ||
				cInt == 0xB || cInt == 0xC ||
				cInt >= 0xE && cInt <= 0x1F ||
				cInt >= 0x7F && cInt <= 0xFFFF)
			{
				result.append("\\u");
				result.append(toHexString(cInt, 4));
			}
			else if (cInt >= 0x10000 && cInt <= 0x10FFFF) {
				result.append("\\U");
				result.append(toHexString(cInt, 8));
			}
			else {
				result.append(c);
			}
		}

		return result.toString();
	}
	
	/**
	 * Converts a decimal value to a hexadecimal string represention
	 * of the specified length.
	 *
	 * @param decimal A decimal value.
	 * @param stringLength The length of the resulting string.
	 **/
	//http://grepcode.com/file_/repo1.maven.org/maven2/org.openrdf/rio/1.0.9/org/openrdf/rio/ntriples/NTriplesUtil.java/?v=source
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
