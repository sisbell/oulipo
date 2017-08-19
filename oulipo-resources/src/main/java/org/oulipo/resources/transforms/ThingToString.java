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
package org.oulipo.resources.transforms;

import static org.oulipo.resources.rdf.DataType.XSD_ANYURI;
import static org.oulipo.resources.rdf.DataType.XSD_BOOLEAN;
import static org.oulipo.resources.rdf.DataType.XSD_BYTE;
import static org.oulipo.resources.rdf.DataType.XSD_DOUBLE;
import static org.oulipo.resources.rdf.DataType.XSD_FLOAT;
import static org.oulipo.resources.rdf.DataType.XSD_GDAY;
import static org.oulipo.resources.rdf.DataType.XSD_GMONTH;
import static org.oulipo.resources.rdf.DataType.XSD_GYEAR;
import static org.oulipo.resources.rdf.DataType.XSD_INTEGER;
import static org.oulipo.resources.rdf.DataType.XSD_LONG;
import static org.oulipo.resources.rdf.DataType.XSD_NONNEGATIVEINTEGER;
import static org.oulipo.resources.rdf.DataType.XSD_SHORT;
import static org.oulipo.resources.rdf.DataType.XSD_STRING;
import static org.oulipo.resources.utils.EscapeUtils.escapeString;

import java.util.Collection;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.oulipo.resources.model.Thing;
import org.oulipo.resources.rdf.NodeType;
import org.oulipo.resources.rdf.RdfObject;
import org.oulipo.resources.rdf.RdfPredicate;
import org.oulipo.resources.rdf.RdfSubject;
import org.oulipo.resources.rdf.Statement;

import com.google.common.base.Strings;

public class ThingToString {

	private static final Logger LOG = Logger.getLogger("RdfRepository");

	public static String asString(Thing thing) throws Exception {
		Collection<Statement> statements = ThingToStatements
				.transform(thing);
		StringBuilder statementBuilder = new StringBuilder();
		for (Statement statement : statements) {
			if (isCompleteStatement(statement, thing)) {
				writeStatement(statement, statementBuilder);
			}
		}
		return statementBuilder.toString();
	}

	public static String asStrings(Collection<Thing> things) throws Exception {
		StringBuilder statementBuilder = new StringBuilder();
		for (Thing t : things) {
			statementBuilder.append(asString(t));
		}
		return statementBuilder.toString();
	}
	
	private static boolean isCompleteStatement(Statement statement, Thing t) {
		if (statement.getSubject() == null) {
			LOG.info("Subject is null: " + t.toString() + ", "
					+ t.getClass().getName());
			return false;
		}
		if (statement.getPredicate() == null) {
			LOG.info("Predicate is null: " + statement.getSubject().getValue());
			return false;
		}
		if (statement.getObject() == null
				|| Strings.isNullOrEmpty(statement.getObject().getValue())) {
			LOG.info("Object is null: " + statement.getSubject().getValue()
					+ ", " + statement.getPredicate().getValue() + ", "
					+ t.toString());
			return false;
		}
		return true;
	}
	
	private static void writeStatement(Statement statement,
			StringBuilder statementBuilder) {
		addSubject(statement.getSubject(), statementBuilder);

		statementBuilder.append(" ");
		addPredicate(statement.getPredicate(), statementBuilder);
		statementBuilder.append(" ");

		if (NodeType.IRI.equals(statement.getObject().getType())) {
			addObjectResource(statement.getObject(), statementBuilder);
		} else {
			addObjectLiteral(statement.getObject(), statementBuilder);
		}
		statementBuilder.append(" .\r\n");
	}

	private static void addObjectLiteral(RdfObject object, StringBuilder sb) {
		Literal objectLiteral = ResourceFactory.createPlainLiteral(object
				.getValue());

		String type = object.getDatatype();
		if (!Strings.isNullOrEmpty(type)) {
			if (XSD_ANYURI.equals(type)) {
				addObjectLiteralString(object, sb);
				sb.append("^^<" + XSD_ANYURI + ">");
			} else if (XSD_BOOLEAN.equals(type)) {
				sb.append(objectLiteral.getBoolean());
			} else if (XSD_BYTE.equals(type)) {
				sb.append(objectLiteral.getByte());
			} else if (XSD_DOUBLE.equals(type)) {
				sb.append(objectLiteral.getDouble());
			} else if (XSD_FLOAT.equals(type)) {
				sb.append(objectLiteral.getFloat());
			} else if (XSD_STRING.equals(type)) {
				addObjectLiteralString(object, sb);
			} else if (XSD_SHORT.equals(type)) {
				sb.append(objectLiteral.getShort());
			} else if (XSD_NONNEGATIVEINTEGER.equals(type)) {
				addObjectLiteralString(object, sb);
				sb.append("^^<" + XSD_NONNEGATIVEINTEGER + ">");
			} else if (XSD_LONG.equals(type)) {
				sb.append(objectLiteral.getLong());
			} else if (XSD_INTEGER.equals(type) || XSD_GDAY.equals(type)
					|| XSD_GMONTH.equals(type) || XSD_GYEAR.equals(type)) {
				sb.append(objectLiteral.getInt());
			}
		}
	}

	private static void addObjectLiteralString(RdfObject object,
			StringBuilder sb) {
		Literal objectLiteral = ResourceFactory.createPlainLiteral(object
				.getValue());
		sb.append("\"").append(escapeString(objectLiteral.toString()))
				.append("\"");
	}

	private static void addObjectResource(RdfObject object, StringBuilder sb) {
		Resource obj = ResourceFactory.createResource(object.getValue());
		sb.append("<").append(obj.toString()).append(">");
	}

	private static void addPredicate(RdfPredicate predicate, StringBuilder sb) {
		Property predicateProperty = ResourceFactory.createProperty(predicate
				.getValue());
		sb.append("<").append(predicateProperty.toString()).append(">");
	}

	public static void addSubject(RdfSubject subject, StringBuilder sb) {
		Resource resource = ResourceFactory.createResource(subject.getValue());
		sb.append("<").append(resource.toString()).append(">");
	}
}
