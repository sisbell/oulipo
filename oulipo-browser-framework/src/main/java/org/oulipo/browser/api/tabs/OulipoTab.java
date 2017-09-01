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
package org.oulipo.browser.api.tabs;

import com.google.common.base.Strings;

import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;

/**
 * A tab that includes tumbler and site specific information useful for
 * displaying to the user.
 * 
 * This class should be extended by extension providers to provide additional
 * information needed for display,
 *
 */
public class OulipoTab extends Tab {

	/**
	 * Image displayed in the tab
	 */
	private ImageView image;

	/**
	 * The title of the tab
	 */
	private String title;

	private String tumblerAddress;

	private String description;
	
	public boolean hasAddress() {
		return !Strings.isNullOrEmpty(tumblerAddress);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return "My Description";
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTumblerAddress(String tumblerAddress) {
		this.tumblerAddress = tumblerAddress;
	}

	public String getTitle() {
		return "Title";
	}

	public String getTumblerAddress() {
		return "ted://1.3.0.1.0.1.1.3";
	}

	public void setImage(String url) {
		this.image = new ImageView(url);
		setGraphic(image);
	}
}
