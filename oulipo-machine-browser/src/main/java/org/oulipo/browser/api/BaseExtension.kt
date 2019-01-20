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

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import org.oulipo.browser.api.MenuContext.Type

abstract class BaseExtension : Extension {

    fun addMenu(ctx: BrowserContext, text: String, type: Type): Menu {
        val menu = Menu()
        menu.text = text
        if (Type.BOOKMARK == type) {
            ctx.menuContext.bookmarkMenu.items.add(menu)
        } else if (Type.FILE == type) {
            ctx.menuContext.fileMenu.items.add(menu)
        } else if (Type.HISTORY == type) {
            ctx.menuContext.historyMenu.items.add(menu)
        } else if (Type.MANAGER == type) {
            ctx.menuContext.managerMenu.items.add(menu)
        } else if (Type.PEOPLE == type) {
            ctx.menuContext.peopleMenu.items.add(menu)
        } else if (Type.TOOLS == type) {
            ctx.menuContext.toolsMenu.items.add(menu)
        }
        return menu
    }

    fun addMenuItem(ctx: BrowserContext, text: String, type: Type, e: EventHandler<ActionEvent>): MenuItem {
        val item = MenuItem()
        item.text = text
        item.onAction = e
        if (Type.BOOKMARK == type) {
            ctx.menuContext.bookmarkMenu.items.add(item)
        } else if (Type.FILE == type) {
            ctx.menuContext.fileMenu.items.add(item)
        } else if (Type.HISTORY == type) {
            ctx.menuContext.historyMenu.items.add(item)
        } else if (Type.MANAGER == type) {
            ctx.menuContext.managerMenu.items.add(item)
        } else if (Type.PEOPLE == type) {
            ctx.menuContext.peopleMenu.items.add(item)
        } else if (Type.TOOLS == type) {
            ctx.menuContext.toolsMenu.items.add(item)
        }
        return item
    }

    fun addSeparator(ctx: BrowserContext, type: Type) {
        val item = SeparatorMenuItem()
        if (Type.BOOKMARK == type) {
            ctx.menuContext.bookmarkMenu.items.add(item)
        } else if (Type.FILE == type) {
            ctx.menuContext.fileMenu.items.add(item)
        } else if (Type.HISTORY == type) {
            ctx.menuContext.historyMenu.items.add(item)
        } else if (Type.MANAGER == type) {
            ctx.menuContext.managerMenu.items.add(item)
        } else if (Type.PEOPLE == type) {
            ctx.menuContext.peopleMenu.items.add(item)
        } else if (Type.TOOLS == type) {
            ctx.menuContext.toolsMenu.items.add(item)
        }
    }
}
