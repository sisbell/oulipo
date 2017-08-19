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
package org.oulipo.streams.serializers;

import java.io.IOException;

import org.oulipo.net.IRI;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class IRISerializer extends StdSerializer<IRI> {

	protected IRISerializer() {
		this(null);
	}

	protected IRISerializer(Class<?> t) {
		super(t, true);
	}

	private static final long serialVersionUID = 3336682630396444045L;

	@Override
	public void serialize(IRI iri, JsonGenerator jgen, SerializerProvider sp) throws IOException {
		jgen.writeString(iri.value);
	}

}
