package org.oulipo.extensions.accounts;

import org.oulipo.browser.api.BaseExtension;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Extension;
import org.oulipo.browser.api.tabs.OulipoTab;
import org.oulipo.browser.framework.MenuContext.Type;

public class PeopleExtension extends BaseExtension implements Extension {

	@Override
	public void init(BrowserContext ctx) {
		addMenuItem(ctx, "Add Person", Type.PEOPLE, e -> {
			OulipoTab tab = new OulipoTab();
			tab.setText("Add Person");
			ctx.getTabManager().add(tab);
		});
	}

}
