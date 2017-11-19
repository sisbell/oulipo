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
package org.oulipo.rdf;

import static org.oulipo.rdf.DataType.XSD_ANYURI;
import static org.oulipo.rdf.DataType.XSD_BOOLEAN;
import static org.oulipo.rdf.DataType.XSD_BYTE;
import static org.oulipo.rdf.DataType.XSD_DOUBLE;
import static org.oulipo.rdf.DataType.XSD_FLOAT;
import static org.oulipo.rdf.DataType.XSD_INTEGER;
import static org.oulipo.rdf.DataType.XSD_LONG;
import static org.oulipo.rdf.DataType.XSD_NONNEGATIVEINTEGER;
import static org.oulipo.rdf.DataType.XSD_SHORT;
import static org.oulipo.rdf.DataType.XSD_STRING;
import static org.oulipo.rdf.NodeType.IRI;
import static org.oulipo.rdf.NodeType.literal;

import java.net.URI;
import java.net.URL;

import org.oulipo.rdf.annotations.ObjectNonNegativeInteger;
import org.oulipo.rdf.annotations.ObjectXSD;
import org.oulipo.streams.IRI;

public final class RdfFactory {

	public final static String BASE_URI = "schema2://oulipo/";

	public final static String SCHEMA_ORG = "schema://oulipo/";

	protected static RdfObject createRdfObject(boolean value) {
		return new RdfObject(String.valueOf(value), literal, XSD_BOOLEAN);
	}

	public static RdfObject createRdfObject(IRI iri, boolean expand) {
		return new RdfObject(expand ? expandIri(iri, expand).value : iri.value, IRI, null);
	}

	protected static RdfObject createRdfObject(Number value) {
		return new RdfObject(value.toString(), literal, getNumberType(value));
	}

	protected static RdfObject createRdfObject(ObjectNonNegativeInteger nonNegInt, Integer value) {
		return new RdfObject(String.valueOf(value), literal, XSD_NONNEGATIVEINTEGER);
	}

	protected static RdfObject createRdfObject(ObjectXSD xsd, Object value) {
		return new RdfObject(String.valueOf(value), literal, xsd.value());
	}

	protected static RdfObject createRdfObject(String value) {
		return new RdfObject(value, literal, XSD_STRING);
	}

	protected static RdfObject createRdfObject(URI value) {
		return new RdfObject(value.toString(), literal, XSD_ANYURI);
	}

	protected static RdfObject createRdfObject(URL value) {
		return new RdfObject(value.toString(), literal, XSD_ANYURI);
	}

	public static RdfPredicate createRdfPredicate(IRI iri) {
		return new RdfPredicate(expandIri(iri, true));
	}

	public static RdfSubject createRdfSubject(IRI iri) {
		return new RdfSubject(expandIri(iri, false));
	}

	private static IRI expandIri(IRI iri, boolean expand) {
		if (!expand || isAbsolute(iri.value)) {
			return iri;
		}
		if (iri.value.startsWith("@")) {
			iri.value = iri.value.replaceFirst("^@", BASE_URI);
			return iri;
		}

		iri.value = BASE_URI + iri.value;
		return iri;
	}

	private static String getNumberType(Number value) {
		if (value instanceof Integer) {
			return XSD_INTEGER;
		} else if (value instanceof Long) {
			return XSD_LONG;
		} else if (value instanceof Float) {
			return XSD_FLOAT;
		} else if (value instanceof Double) {
			return XSD_DOUBLE;
		} else if (value instanceof Short) {
			return XSD_SHORT;
		} else if (value instanceof Byte) {
			return XSD_BYTE;
		}
		throw new IllegalArgumentException("Unrecognized number type: " + value);
	}

	private static boolean isAbsolute(String iri) {
		return iri.startsWith("schema") || iri.startsWith("http");
	}

}
