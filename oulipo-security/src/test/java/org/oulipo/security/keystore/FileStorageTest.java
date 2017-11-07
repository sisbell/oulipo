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
package org.oulipo.security.keystore;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.junit.Test;
import org.oulipo.storage.StorageService;

public class FileStorageTest {

	@Test
	public void a() throws Exception {
		FileStorage storage = new FileStorage(new StorageService("keys"));
		ECKey key = new ECKey();
		storage.add("Mine", key.toAddress(MainNetParams.get()).toString(), key.getPrivKeyBytes(), key.getPubKey());
		System.out.println(storage.getAliases()[0]);
		System.out.println(storage.getECKey("Mine").toAddress(MainNetParams.get()).toString());
	}
}
