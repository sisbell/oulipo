package org.oulipo.browser.api.history;

import java.util.HashSet;
import java.util.Set;

import org.oulipo.browser.api.tabs.OulipoTab;
import org.oulipo.browser.api.tabs.PageRefreshListener;

public class HistoryManager {

	private Set<PageRefreshListener> listeners = new HashSet<>();
	
	public void registerListener(PageRefreshListener listener) {
		listeners.add(listener);
	}
	
	public void visitPage(OulipoTab tab) {
		for(PageRefreshListener vpl : listeners) {
			vpl.refresh(tab);
		}
	}
}
