package org.oulipo.browser.api.tabs;

/**
 * Called each time a tab's content is refreshed. Refresh means that the most
 * recent content is pulled from the server or URL location.
 */
public interface PageRefreshListener {

	/**
	 * Called when contents of the specified tab are refreshed
	 * 
	 * @param tab
	 */
	void refresh(OulipoTab tab);
}
