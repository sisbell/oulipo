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

import org.oulipo.browser.api.tabs.OulipoTab;

public class ViewerTab extends OulipoTab {

	private DocumentViewer documentViewer;

	public ViewerTab() {
		this.setText("New Tab");
		this.setOnSelectionChanged(e -> { 
			//TODO: USe loader?
			//this.setContent(new DocumentViewer(null));
		});
	}
	
	public ViewerTab(DocumentViewer documentViewer) {
		this.documentViewer = documentViewer;
		this.setText(documentViewer.getDocument().title);
	}
	
	public DocumentViewer getDocumentViewer() {
		return documentViewer;
	}
}