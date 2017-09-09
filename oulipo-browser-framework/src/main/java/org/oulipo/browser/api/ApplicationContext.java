package org.oulipo.browser.api;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;

import javafx.stage.Stage;

@Singleton
public class ApplicationContext {

	private Map<String, Stage> stages = new HashMap<>();

	public void putStage(String id, Stage stage) {
		stages.put(id, stage);
	}

	public Stage getStage(String id) {
		return stages.get(id);
	}
	
	public Map<String, Stage> getStages() {
		return stages;
	}
}
