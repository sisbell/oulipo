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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.oulipo.rdf.RdfFactory.createRdfObject;
import static org.oulipo.rdf.RdfFactory.createRdfPredicate;
import static org.oulipo.rdf.RdfFactory.createRdfSubject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.oulipo.rdf.annotations.ObjectBoolean;
import org.oulipo.rdf.annotations.ObjectDate;
import org.oulipo.rdf.annotations.ObjectEnum;
import org.oulipo.rdf.annotations.ObjectIRI;
import org.oulipo.rdf.annotations.ObjectNonNegativeInteger;
import org.oulipo.rdf.annotations.ObjectNumber;
import org.oulipo.rdf.annotations.ObjectString;
import org.oulipo.rdf.annotations.ObjectURL;
import org.oulipo.rdf.annotations.ObjectXSD;
import org.oulipo.rdf.annotations.ParentClass;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.SchemaOulipo;
import org.oulipo.rdf.annotations.Subject;
import org.oulipo.streams.IRI;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Transforms a Thing into a collection of Statements.
 */
public final class ThingToStatements {

	protected static Collection<Statement> createStatements(RdfSubject rdfSubject, FieldWrapper field, Thing thing)
			throws Exception {
		Collection<Statement> statements = Lists.newArrayList();

		Annotation predicateAnnotation = field.getAnnotation(Predicate.class);
		if (predicateAnnotation == null) {
			return statements;
		}
		Object fieldInstance = field.get(thing);
		if (fieldInstance == null) {
			return statements;
		}

		String predicateIRI = ((Predicate) predicateAnnotation).value();

		RdfPredicate rdfPredicate = createRdfPredicate(new IRI(getIRI(field, predicateIRI)));

		for (Annotation annotation : field.getAnnotations()) {
			if (annotation instanceof ObjectIRI) {
				if (fieldInstance instanceof IRI[]) {
					IRI[] c = (IRI[]) fieldInstance;
					for (IRI i : c) {
						RdfObject rdfObject = createRdfObject(i, false);
						statements.add(new Statement(rdfSubject, rdfPredicate, rdfObject));
					}
				} else if (fieldInstance instanceof IRI) {
					IRI value = (IRI) fieldInstance;
					RdfObject rdfObject = createRdfObject(value, false);
					statements.add(new Statement(rdfSubject, rdfPredicate, rdfObject));
				} else {
					throwMismatchException(field, thing.getClass());
				}
			} else if (annotation instanceof ObjectString) {
				if (fieldInstance instanceof String[]) {
					String[] c = (String[]) fieldInstance;
					for (String value : c) {
						if (!Strings.isNullOrEmpty(value)) {
							statements.add(new Statement(rdfSubject, rdfPredicate, value));
						}
					}
				} else if (fieldInstance instanceof String) {
					String value = (String) fieldInstance;
					if (!Strings.isNullOrEmpty(value)) {
						statements.add(new Statement(rdfSubject, rdfPredicate, value));
					}
				} else {
					throwMismatchException(field, thing.getClass());
				}
			} else if (annotation instanceof ObjectEnum) {
				if (fieldInstance instanceof Enum[]) {
					Enum[] c = (Enum[]) fieldInstance;
					for (Enum value : c) {
						statements.add(new Statement(rdfSubject, rdfPredicate, value.name()));
					}
				} else if (fieldInstance instanceof Enum) {
					String value = ((Enum<?>) fieldInstance).name();
					statements.add(new Statement(rdfSubject, rdfPredicate, value));
				} else {
					throwMismatchException(field, thing.getClass());
				}
			} else if (annotation instanceof ObjectBoolean) {
				if (!(fieldInstance instanceof Boolean)) {
					throwMismatchException(field, thing.getClass());
				}
				Boolean value = (Boolean) fieldInstance;
				statements.add(new Statement(rdfSubject, rdfPredicate, value));

			} else if (annotation instanceof ObjectDate) {
				if (!(fieldInstance instanceof Date)) {
					throwMismatchException(field, thing.getClass());
				}
				// TODO: implement date (formatter)
			} else if (annotation instanceof ObjectNumber) {
				if (fieldInstance instanceof Number[]) {
					Number[] c = (Number[]) fieldInstance;
					for (Number value : c) {
						statements.add(new Statement(rdfSubject, rdfPredicate, value));
					}
				} else if (!(fieldInstance instanceof Number)) {
					throwMismatchException(field, thing.getClass());
				} else {
					Number value = (Number) fieldInstance;
					statements.add(new Statement(rdfSubject, rdfPredicate, value));
				}
			} else if (annotation instanceof ObjectURL) {
				if (fieldInstance instanceof URL[]) {
					URL[] c = (URL[]) fieldInstance;
					for (URL value : c) {
						statements.add(new Statement(rdfSubject, rdfPredicate, value));
					}
				} else if (!(fieldInstance instanceof URL)) {
					throwMismatchException(field, thing.getClass());
				} else {
					URL value = (URL) fieldInstance;
					statements.add(new Statement(rdfSubject, rdfPredicate, value));
				}
			} else if (annotation instanceof ObjectXSD) {
				statements.add(new Statement(rdfSubject, rdfPredicate, (ObjectXSD) annotation, fieldInstance));
			} else if (annotation instanceof ObjectNonNegativeInteger) {
				if (fieldInstance instanceof Integer[]) {
					Integer[] c = (Integer[]) fieldInstance;
					for (Number value : c) {
						statements.add(new Statement(rdfSubject, rdfPredicate, value));
					}
				} else if (!(fieldInstance instanceof Integer)) {
					throwMismatchException(field, thing.getClass());
				} else {
					statements.add(new Statement(rdfSubject, rdfPredicate, (ObjectNonNegativeInteger) annotation,
							(Integer) fieldInstance));
				}
			}
		}
		return statements;
	}

	private static String getIRI(FieldWrapper field, String iri) {
		if (iri.startsWith("http")) {
			return iri;
		}
		return isOulipoUrl(field) ? RdfFactory.BASE_URI + iri : RdfFactory.SCHEMA_ORG + iri;
	}

	private static boolean isOulipoUrl(FieldWrapper field) {
		return field.getAnnotation(SchemaOulipo.class) != null;
	}

	private static Statement subject(Subject subject, IRI resourceId) {
		RdfSubject rdfSubject = createRdfSubject(resourceId);
		RdfPredicate rdfPredicate = createRdfPredicate(new IRI(DataType.RDF_TYPE));
		RdfObject rdfObject = createRdfObject(new IRI(subject.value()), false);

		return new Statement(rdfSubject, rdfPredicate, rdfObject);
	}

	private static void throwMismatchException(FieldWrapper field, Class<? extends Thing> clazz) {
		throw new IllegalArgumentException(
				"Annotated type does not match Java field type: " + field.getName() + ", " + clazz.getName());
	}

	/**
	 * Transforms thing into a collection of statements using the specified
	 * rdfSubject as the subject in each returned statement
	 * 
	 * @param rdfSubject
	 *            the subject to use for each statement
	 * @param thing
	 * @return
	 * @throws Exception
	 */
	private static Collection<Statement> transform(RdfSubject rdfSubject, Thing thing) throws Exception {
		Collection<Statement> statements = new ArrayList<>();
		for (Field field : thing.getClass().getFields()) {
			field.setAccessible(true);
			statements.addAll(createStatements(rdfSubject, new FieldWrapper(field), thing));
		}
		return statements;
	}

	/**
	 * Transforms a <code>Thing</code> into a collection of RDF statements. This
	 * flattens an object into a set of tuples.
	 * 
	 * @param thing
	 *            the thing to transform
	 * @return collection of statements or tuples
	 * @throws IllegalArgumentException
	 *             if thing is null or if thing does not have a subject defined
	 * @throws Exception
	 */
	public static Collection<Statement> transform(Thing thing) throws Exception {
		checkNotNull(thing, "thing");
		Collection<Statement> statements = new ArrayList<>();
		Class<?> clazz = thing.getClass();
		Subject subject = clazz.getAnnotation(Subject.class);

		if (subject == null) {
			throw new IllegalArgumentException("No subject annotation on Thing");
		}

		IRI id = thing.subject;
		if (id == null) {
			throw new IllegalArgumentException("thing.subject is empty");
		}

		statements.add(subject(subject, id));

		RdfSubject rdfSubject = createRdfSubject(id);
		for (Field field : thing.getClass().getFields()) {
			field.setAccessible(true);
			if (field.getAnnotation(ParentClass.class) == null) {
				statements.addAll(createStatements(rdfSubject, new FieldWrapper(field), thing));
			} else {
				Object fieldInstance = field.get(thing);
				if (fieldInstance != null) {
					statements.addAll(transform(rdfSubject, (Thing) fieldInstance));
				}
			}
		}
		return statements;

	}

}
