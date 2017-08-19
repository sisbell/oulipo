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
package org.oulipo.resources.responses;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.oulipo.net.TumblerAddress;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EndsetByType {

	public Map<String, Endset> endsets = new HashMap<>();
	
	public void addFrom(TumblerAddress linkType, TumblerAddress[] from) {
		Endset es = getEndsetByType(linkType);
		if(from != null) {
			if(es.fromVSpans == null) {
				es.fromVSpans = new HashSet<>();
			}
			for(TumblerAddress i : from) {
				es.fromVSpans.add(i);
			}
		} 
	}
	
	public void addTo(TumblerAddress linkType, TumblerAddress[] to) {
		Endset es = getEndsetByType(linkType);
		if(to != null) {
			if(es.toVSpans == null) {
				es.toVSpans = new HashSet<>();
			}
			for(TumblerAddress i : to) {
				es.toVSpans.add(i);
			}
		}	
	}
	
	private Endset getEndsetByType(TumblerAddress linkType) {
		Endset es = endsets.get(linkType.value);
		if(es == null) {
			es = new Endset();	
			endsets.put(linkType.value, es);
		} 
		return es;
	}
}
