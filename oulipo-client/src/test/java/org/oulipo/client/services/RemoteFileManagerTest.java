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
package org.oulipo.client.services;

import java.io.File;

import org.junit.Test;
import org.oulipo.services.IpfsFileManager;
import org.oulipo.streams.RemoteFileManager;

public class RemoteFileManagerTest {

	//@Test
	public void add() throws Exception {
		RemoteFileManager m = new IpfsFileManager();
		String hash = m.add(new File("Sample.txt"));
		System.out.println(hash);

		byte[] content = m.get(hash);
		System.out.println(new String(content));
	}
}
