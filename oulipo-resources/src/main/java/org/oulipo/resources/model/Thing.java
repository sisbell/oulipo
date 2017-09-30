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
package org.oulipo.resources.model;

import java.util.Date;

import org.oulipo.net.IRI;
import org.oulipo.net.MalformedTumblerException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.rdf.annotations.ObjectDate;
import org.oulipo.rdf.annotations.ObjectIRI;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.SchemaOulipo;
import org.oulipo.rdf.annotations.Subject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
@SchemaOulipo
@Subject(value = Schema.THING, key = "name")
public class Thing {

	@Predicate("createdDate")
	@ObjectDate
	public Date createdDate;

	@Predicate("resourceId")
	@ObjectIRI
	public IRI resourceId;

	@Predicate("updatedDate")
	@ObjectDate
	public Date updatedDate;

	public Thing() {
	}

	public Thing(IRI resourceId) {
		this.resourceId = resourceId;
	}

	public Thing(String resourceId) {
		this.resourceId = new IRI(resourceId);
	}

	public String documentId() throws MalformedTumblerException {
		if (resourceId instanceof TumblerAddress) {
			return ((TumblerAddress) resourceId).documentVal();
		}
		return TumblerAddress.create(resourceId.value).documentVal();
	}

	public String elementId() throws MalformedTumblerException {
		if (resourceId instanceof TumblerAddress) {
			return ((TumblerAddress) resourceId).getElement().asString();
		}
		return TumblerAddress.create(resourceId.value).getElement().asString();
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
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
		return result;
	}

	public String networkId() throws MalformedTumblerException {
		if (resourceId instanceof TumblerAddress) {
			return ((TumblerAddress) resourceId).networkVal();
		}
		return TumblerAddress.create(resourceId.value).networkVal();
	}

	public String nodeId() throws MalformedTumblerException {
		if (resourceId instanceof TumblerAddress) {
			return ((TumblerAddress) resourceId).nodeVal();
		}
		return TumblerAddress.create(resourceId.value).nodeVal();

	}

	public String userId() throws MalformedTumblerException {
		if (resourceId instanceof TumblerAddress) {
			return ((TumblerAddress) resourceId).userVal();
		}
		return TumblerAddress.create(resourceId.value).userVal();
	}

}
