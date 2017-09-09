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
package org.oulipo.browser.framework;

import javafx.scene.control.Menu;
import javafx.scene.control.TabPane;

/**
 * Allows access to system level browser Menus.
 */
public final class MenuContext {

	public enum Type {
		BOOKMARK, FILE, HISTORY, MANAGER, PEOPLE, TOOLS, WINDOW
	}

	private final Menu bookmarkMenu;

	private final Menu fileMenu;

	private final Menu historyMenu;

	private final Menu managerMenu;

	private Menu peopleMenu;

	private final TabPane tabs;

	private final Menu toolsMenu;

	private Menu windowMenu;

	public MenuContext(Menu windowMenu, Menu peopleMenu, Menu managerMenu, Menu toolsMenu, Menu fileMenu,
			Menu bookmarkMenu, Menu historyMenu, TabPane tabs) {
		this.windowMenu = windowMenu;
		this.peopleMenu = peopleMenu;
		this.managerMenu = managerMenu;
		this.toolsMenu = toolsMenu;
		this.fileMenu = fileMenu;
		this.bookmarkMenu = bookmarkMenu;
		this.historyMenu = historyMenu;
		this.tabs = tabs;
	}

	public Menu getBookmarkMenu() {
		return bookmarkMenu;
	}

	public Menu getFileMenu() {
		return fileMenu;
	}

	public Menu getHistoryMenu() {
		return historyMenu;
	}

	public Menu getManagerMenu() {
		return managerMenu;
	}

	public Menu getMenu(Type type) {
		if (Type.BOOKMARK.equals(type)) {
			return bookmarkMenu;
		} else if (Type.FILE.equals(type)) {
			return fileMenu;
		} else if (Type.HISTORY.equals(type)) {
			return historyMenu;
		} else if (Type.MANAGER.equals(type)) {
			return managerMenu;
		} else if (Type.TOOLS.equals(type)) {
			return toolsMenu;
		} else if (Type.WINDOW.equals(type)) {
			return windowMenu;
		}
		return null;
	}

	public Menu getPeopleMenu() {
		return peopleMenu;
	}

	public TabPane getTabs() {
		return tabs;
	}

	public Menu getToolsMenu() {
		return toolsMenu;
	}

	public Menu getWindowMenu() {
		return windowMenu;
	}
}
