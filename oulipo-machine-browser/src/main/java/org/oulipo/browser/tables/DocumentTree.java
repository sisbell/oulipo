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
package org.oulipo.browser.tables;

import org.oulipo.rdf.model.Document;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class DocumentTree extends RecursiveTreeObject<DocumentTree> {

	public final StringProperty address;

	public final StringProperty description;

	public final StringProperty title;

	public DocumentTree(Document document) {
		this.address = new SimpleStringProperty(document.subject.value);
		this.title = new SimpleStringProperty(document.title);
		this.description = new SimpleStringProperty(document.description);
		// TODO: document icon hash
	}
}
