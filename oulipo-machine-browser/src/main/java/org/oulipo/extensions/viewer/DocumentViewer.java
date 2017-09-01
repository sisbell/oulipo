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
package org.oulipo.extensions.viewer;

import java.util.ArrayList;

import org.oulipo.resources.model.Document;

import javafx.scene.layout.TilePane;

public class DocumentViewer extends TilePane {

	private Document document;

	private ArrayList<Document> documents = new ArrayList<>();

	public DocumentViewer(Document document) {
		this.document = document;
		this.setHeight(400);
		this.setWidth(800);
	}
/*
	public ViewerTab updateTab(ViewerTab tab) {
		if (Strings.isNullOrEmpty(document.title)) {
			try {
				tab.setText(document.documentId());
			} catch (MalformedTumblerException e) {

			}
		} else {
			tab.setText(document.title);
		}
		if (!Strings.isNullOrEmpty(document.description)) {
			tab.setTooltip(new Tooltip(document.description));
		}
		tab.setContent(this);
		
		JFXTextField tedAddress = new JFXTextField();
		tedAddress.setPromptText("TED Address");
		tedAddress.setText(document.resourceId.value);
		
		this.getChildren().add(tedAddress);
		
		tedAddress.prefWidthProperty().bind(widthProperty());

		
		return tab;
	}
	*/

	/**
	 * Adds document to this viewer
	 * 
	 * @param document
	 *            the document to add
	 */
	public void addDocument(Document document) {
		documents.add(document);
	}

	public void addDocument(int index, Document document) {
		documents.add(index, document);
	}

	public Document getDocument() {
		return document;
	}

	/**
	 * Removes document from this viewer
	 * 
	 * @param document
	 *            the document to remove
	 */
	public void removeDocument(Document document) {
		documents.remove(document);
	}

	public void removeDocument(int index) {
		documents.remove(index);
	}

}
