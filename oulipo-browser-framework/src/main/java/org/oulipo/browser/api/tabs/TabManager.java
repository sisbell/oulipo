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

import java.io.IOException;

/**
 * Service for adding and inserting OulipoTabs into the brower's toolbar.
 */
public interface TabManager {

	/**
	 * Adds the specified tab to the right of all other tabs in the toolbar
	 *
	 * @param tab
	 *            the tab to add
	 */
	void add(OulipoTab tab);

	OulipoTab addTabWithAddressBar(String address, String title) throws IOException;

	OulipoTab addTabWithAddressBar(String address, String title, String body) throws IOException;

	void backward(OulipoTab tab);

	void forward(OulipoTab tab);

	/**
	 * Gets the currently selected tab
	 * 
	 * @return the currently selected tab
	 */
	OulipoTab getSelectedTab();

	/**
	 * Inserts the specified tab at the specified position (from left to right) in
	 * the toolbar bar
	 * 
	 * @param position
	 *            the position to insert
	 * @param tab
	 *            the tab to insert
	 */
	void insert(int position, OulipoTab tab);

	void remove(OulipoTab tab);

	void removeSelectedTab();

	/**
	 * Selects the specified tab and makes it active for the user
	 * 
	 * @param tab
	 *            the tab to select
	 */
	void selectTab(OulipoTab tab);

	void showInTab(OulipoTab tab, String address, String title) throws IOException;

	/**
	 * Returns the number of tabs in the toolbar
	 * 
	 * @return the number of tabs in the toolbar
	 */
	int size();

}
