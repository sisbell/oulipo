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
package org.oulipo.browser.framework.impl;

import java.io.File;
import java.io.IOException;

import org.oulipo.browser.api.storage.RemoteStorage;
import org.oulipo.client.services.IpfsFileManager;

public final class IpfsRemoteStorage implements RemoteStorage {

	private final IpfsFileManager fileManager;

	public IpfsRemoteStorage() {
		this.fileManager = null;// new IpfsFileManager();
	}

	@Override
	public String add(File file) throws IOException {
		return "";
		// return fileManager.add(file);
	}

	@Override
	public byte[] get(String hash) throws IOException {
		return new byte[0];
		// return fileManager.get(hash);
	}

}
