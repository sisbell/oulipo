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
package org.oulipo.browser.api;

import org.oulipo.browser.api.MenuContext.Type;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public abstract class BaseExtension implements Extension {

	public Menu addMenu(BrowserContext ctx, String text, MenuContext.Type type) {
		Menu menu = new Menu();
		menu.setText(text);
		if (Type.BOOKMARK.equals(type)) {
			ctx.getMenuContext().getBookmarkMenu().getItems().add(menu);
		} else if (Type.FILE.equals(type)) {
			ctx.getMenuContext().getFileMenu().getItems().add(menu);
		} else if (Type.HISTORY.equals(type)) {
			ctx.getMenuContext().getHistoryMenu().getItems().add(menu);
		} else if (Type.MANAGER.equals(type)) {
			ctx.getMenuContext().getManagerMenu().getItems().add(menu);
		} else if (Type.PEOPLE.equals(type)) {
			ctx.getMenuContext().getPeopleMenu().getItems().add(menu);
		} else if (Type.TOOLS.equals(type)) {
			ctx.getMenuContext().getToolsMenu().getItems().add(menu);
		}
		return menu;
	}

	public MenuItem addMenuItem(BrowserContext ctx, String text, MenuContext.Type type, EventHandler<ActionEvent> e) {
		MenuItem item = new MenuItem();
		item.setText(text);
		item.setOnAction(e);
		if (Type.BOOKMARK.equals(type)) {
			ctx.getMenuContext().getBookmarkMenu().getItems().add(item);
		} else if (Type.FILE.equals(type)) {
			ctx.getMenuContext().getFileMenu().getItems().add(item);
		} else if (Type.HISTORY.equals(type)) {
			ctx.getMenuContext().getHistoryMenu().getItems().add(item);
		} else if (Type.MANAGER.equals(type)) {
			ctx.getMenuContext().getManagerMenu().getItems().add(item);
		} else if (Type.PEOPLE.equals(type)) {
			ctx.getMenuContext().getPeopleMenu().getItems().add(item);
		} else if (Type.TOOLS.equals(type)) {
			ctx.getMenuContext().getToolsMenu().getItems().add(item);
		}
		return item;
	}

	public void addSeparator(BrowserContext ctx, MenuContext.Type type) {
		SeparatorMenuItem item = new SeparatorMenuItem();
		if (Type.BOOKMARK.equals(type)) {
			ctx.getMenuContext().getBookmarkMenu().getItems().add(item);
		} else if (Type.FILE.equals(type)) {
			ctx.getMenuContext().getFileMenu().getItems().add(item);
		} else if (Type.HISTORY.equals(type)) {
			ctx.getMenuContext().getHistoryMenu().getItems().add(item);
		} else if (Type.MANAGER.equals(type)) {
			ctx.getMenuContext().getManagerMenu().getItems().add(item);
		} else if (Type.PEOPLE.equals(type)) {
			ctx.getMenuContext().getPeopleMenu().getItems().add(item);
		} else if (Type.TOOLS.equals(type)) {
			ctx.getMenuContext().getToolsMenu().getItems().add(item);
		}

	}
}
