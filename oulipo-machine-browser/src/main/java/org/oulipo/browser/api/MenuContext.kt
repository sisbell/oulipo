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
package org.oulipo.browser.api

import javafx.scene.control.Menu
import javafx.scene.control.TabPane

/**
 * Allows access to system level org.oulipo.browser Menus.
 */
class MenuContext(
    val windowMenu: Menu, val peopleMenu: Menu, val managerMenu: Menu, val toolsMenu: Menu, val fileMenu: Menu,
    val bookmarkMenu: Menu, val historyMenu: Menu, val tabs: TabPane
) {

    enum class Type {
        BOOKMARK, FILE, HISTORY, MANAGER, PEOPLE, TOOLS, WINDOW
    }

    fun getMenu(type: Type): Menu? {
        if (Type.BOOKMARK == type) {
            return bookmarkMenu
        } else if (Type.FILE == type) {
            return fileMenu
        } else if (Type.HISTORY == type) {
            return historyMenu
        } else if (Type.MANAGER == type) {
            return managerMenu
        } else if (Type.TOOLS == type) {
            return toolsMenu
        } else if (Type.WINDOW == type) {
            return windowMenu
        }
        return null
    }
}
