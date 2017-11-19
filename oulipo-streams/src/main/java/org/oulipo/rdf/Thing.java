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

import java.util.Date;

import org.oulipo.rdf.annotations.ObjectDate;
import org.oulipo.rdf.annotations.ObjectIRI;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.SchemaOulipo;
import org.oulipo.rdf.annotations.Subject;
import org.oulipo.rdf.model.Schema;
import org.oulipo.streams.IRI;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
@SchemaOulipo
@Subject(value = Schema.THING, key = "subject")
public class Thing {

	@Predicate("createdDate")
	@ObjectDate
	public Date createdDate;

	@Predicate("subject")
	@ObjectIRI
	public IRI subject;

	@Predicate("updatedDate")
	@ObjectDate
	public Date updatedDate;

	public Thing() {
	}

	public Thing(IRI subject) {
		this.subject = subject;
	}

	public Thing(String subject) {
		this.subject = new IRI(subject);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Thing other = (Thing) obj;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}
}
