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

import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.Extension;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import java.io.IOException;
import java.util.Set;

/**
 * Loads all extensions into a <code>BrowserContext</code>
 */
public class ExtensionLoader {

	public static void loadExtensions(BrowserContext ctx)
			throws IOException, InstantiationException, IllegalAccessException {
		Set<ClassInfo> classInfos = ClassPath.from(ExtensionLoader.class.getClassLoader())
				.getTopLevelClassesRecursive("org.oulipo.extensions");
		for (ClassInfo info : classInfos) {
			for (Class<?> i : info.load().getInterfaces()) {
				if (i.getName().equals(Extension.class.getName())) {
					Extension extension = (Extension) info.load().newInstance();
					extension.init(ctx);
				}
			}
		}
	}
}
