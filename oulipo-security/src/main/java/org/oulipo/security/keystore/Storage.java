/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.  
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.bitcoinj.core.ECKey;

/**
 * Service for adding keys to some underlying storage or datasource
 */
public interface Storage {

	void add(String alias, String address, byte[] privateKey, byte[] publicKey) throws IOException;

	void close() throws IOException;

	String[] getAliases() throws IOException;

	ECKey getECKey(String alias) throws UnsupportedEncodingException;
}
