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

import static org.oulipo.rdf.RdfFactory.createRdfObject;

import java.net.URL;

import org.oulipo.rdf.annotations.ObjectNonNegativeInteger;
import org.oulipo.rdf.annotations.ObjectXSD;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Statement {

	private RdfObject object;

	private RdfPredicate predicate;

	private RdfSubject subject;

	public Statement() {
	}

	public Statement(RdfSubject subject, RdfPredicate predicate, Boolean value) {
		this(subject, predicate, createRdfObject(value));
	}

	public Statement(RdfSubject subject, RdfPredicate predicate, Number value) {
		this(subject, predicate, createRdfObject(value));
	}

	public Statement(RdfSubject subject, RdfPredicate predicate,
			ObjectNonNegativeInteger nonNegInt, Integer value) {
		this(subject, predicate, createRdfObject(nonNegInt, value));
	}

	public Statement(RdfSubject subject, RdfPredicate predicate, ObjectXSD xsd,
			Object value) {
		this(subject, predicate, createRdfObject(xsd, value));
	}

	public Statement(RdfSubject subject, RdfPredicate predicate,
			RdfObject rdfObject) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = rdfObject;
	}

	public Statement(RdfSubject subject, RdfPredicate predicate, String value) {
		this(subject, predicate, createRdfObject(value));
	}

	public Statement(RdfSubject subject, RdfPredicate predicate, URL value) {
		this(subject, predicate, createRdfObject(value));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Statement other = (Statement) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	public RdfObject getObject() {
		return object;
	}

	public RdfPredicate getPredicate() {
		return predicate;
	}

	public RdfSubject getSubject() {
		return subject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result
				+ ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Statement [subject=" + subject + ", predicate=" + predicate
				+ ", object=" + object + "]";
	}

}
