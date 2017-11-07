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
package org.oulipo.services;

import java.io.File;
import java.io.IOException;

import org.oulipo.streams.RemoteFileManager;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;

public class IpfsFileManager implements RemoteFileManager {

	private IPFS ipfs;

	public IpfsFileManager() {
		ipfs = new IPFS(new MultiAddress("/ip4/127.0.0.1/tcp/5001"));
	}

	@Override
	public String add(File file) throws IOException {
		NamedStreamable f = new NamedStreamable.FileWrapper(file);
		MerkleNode addResult = ipfs.add(f);
		Multihash pointer = addResult.hash;
		return pointer.toBase58();
	}

	@Override
	public byte[] get(String hash) throws IOException {
		Multihash filePointer = Multihash.fromBase58(hash);
		return ipfs.cat(filePointer);
	}
}
