package org.oulipo.browser.api;

import org.oulipo.browser.framework.MenuContext;
import org.oulipo.browser.framework.MenuContext.Type;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public abstract class BaseExtension implements Extension {

	public void addMenuItem(BrowserContext ctx, String text, MenuContext.Type type, EventHandler<ActionEvent> e) {
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
	}
}
