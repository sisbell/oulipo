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

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class KeyStore {

    private ScryptHeader scrypt;

    private ArrayList<KeyEntry> keys = new ArrayList<>();

    private KeyStore() { }
    
    public KeyStore(ScryptHeader scrypt) {
    	this.scrypt = scrypt;
    }
    
    public ScryptHeader getScrypt() {
        return scrypt;
    }

    public void addKey(KeyEntry key) {
        keys.add(key);
    }

    public ArrayList<KeyEntry> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<KeyEntry> keys) {
        this.keys = keys;
    }
}
