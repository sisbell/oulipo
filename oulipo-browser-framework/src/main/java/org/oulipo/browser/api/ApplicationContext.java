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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.oulipo.browser.framework.ExtensionLoader;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import javafx.stage.Stage;

@Singleton
public class ApplicationContext {

	private LiveSpeechRecognizer recognizer;

	private Map<String, PageRouter> routers = new HashMap<>();

	private Map<String, Stage> stages = new HashMap<>();

	@Inject
	public ApplicationContext(Injector injector) {
		try {
			Set<ClassInfo> classInfos = ClassPath.from(ExtensionLoader.class.getClassLoader())
					.getTopLevelClassesRecursive("org.oulipo");
			for (ClassInfo info : classInfos) {
				for (Class<?> i : info.load().getInterfaces()) {
					if (i.getName().equals(PageRouter.class.getName())) {
						Class<?> clazz = info.load();
						for (Annotation annotation : clazz.getAnnotations()) {
							if (annotation instanceof Scheme) {
								PageRouter pageRouter = (PageRouter) injector.getInstance(clazz);
								routers.put(((Scheme) annotation).value(), pageRouter);
								break;
							}
						}
					}
				}
			}
			Configuration configuration = new Configuration();

			configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
			configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
			configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

			recognizer = new LiveSpeechRecognizer(configuration);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public LiveSpeechRecognizer getRecognizer() {
		return recognizer;
	}

	public PageRouter getRouter(String scheme) {
		return routers.get(scheme);
	}

	public Stage getStage(String id) {
		return stages.get(id);
	}

	public Map<String, Stage> getStages() {
		return stages;
	}
	
	public void putStage(String id, Stage stage) {
		stages.put(id, stage);
	}
}
