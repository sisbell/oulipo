package org.oulipo.client.services;

import java.io.File;
import java.io.IOException;

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
