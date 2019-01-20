/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */
package org.oulipo.browser.api.tabs

import java.io.IOException

/**
 * Service for adding and inserting OulipoTabs into the browser's toolbar.
 */
interface TabManager {

    /**
     * Gets the currently selected tab
     *
     * @return the currently selected tab
     */
    val selectedTab: OulipoTab

    /**
     * Adds the specified tab to the right of all other tabs in the toolbar
     *
     * @param tab
     * the tab to add
     */
    fun add(tab: OulipoTab)

    @Throws(IOException::class)
    fun addTabWithAddressBar(address: String, title: String): OulipoTab

    fun backward(tab: OulipoTab)

    fun forward(tab: OulipoTab)

    /**
     * Inserts the specified tab at the specified position (from left to right) in
     * the toolbar bar
     *
     * @param position
     * the position to insert
     * @param tab
     * the tab to insert
     */
    fun insert(position: Int, tab: OulipoTab)

    fun remove(tab: OulipoTab)

    fun removeSelectedTab()

    /**
     * Selects the specified tab and makes it active for the user
     */
    fun selectTab(tab: OulipoTab)

    @Throws(IOException::class)
    fun showInTab(tab: OulipoTab, address: String, title: String)

    /**
     * Returns the number of tabs in the toolbar
     *
     * @return the number of tabs in the toolbar
     */
    fun size(): Int

}
