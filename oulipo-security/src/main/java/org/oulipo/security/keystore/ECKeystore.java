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

import static com.google.common.io.BaseEncoding.base64Url;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.EncryptedData;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.Protos;
import org.spongycastle.crypto.params.KeyParameter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;

/**
 * Keystore for importing keys from JSON format and for exporting keys to JSON
 * format
 * 
 */
public final class ECKeystore {

	/**
	 * Exports the specified keys into JSON format
	 * 
	 * @param os
	 *            - stream to write JSON format to
	 * @param password
	 *            Scrypt password used to derive AES key
	 * @param keys
	 *            private keys to output
	 * @throws Exception
	 */
	public static void export(OutputStream os, String password, Collection<PrivateKey> keys) throws Exception {

		KeyCrypterScrypt crypterScrypt = new KeyCrypterScrypt(512);

		Protos.ScryptParameters params = crypterScrypt.getScryptParameters();
		ScryptHeader scryptHeader = new ScryptHeader.Builder().n(params.getN()).r(params.getR()).p(params.getP())
				.salt(params.getSalt().toByteArray()).build();

		KeyStore keyStore = new KeyStore(scryptHeader);
		KeyParameter aesKey = crypterScrypt.deriveKey(password);

		for (PrivateKey pk : keys) {
			EncryptedData data = crypterScrypt.encrypt(pk.key, aesKey);
			KeyEntry entry = new KeyEntry.Builder().alias(pk.nickname).kty("EC").crv("P-256").pk(data.encryptedBytes)
					.iv(data.initialisationVector).build();
			keyStore.addKey(entry);
		}

		new ObjectMapper().writeValue(os, keyStore);
		os.close();
	}

	/**
	 * Loads JSON keystore from the specified file and adds entries to the specified
	 * storage
	 * 
	 * @param file
	 *            JSON keystore
	 * @param password
	 *            Scrypt password used to derive AES key
	 * @param storage
	 *            output for keystore to write to
	 * @throws Exception
	 */
	public static void load(File file, String password, Storage storage) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		load(mapper.readValue(file, KeyStore.class), password, storage);
	}

	/**
	 * Loads JSON keystore from the specified input stream and adds entries to the
	 * specified storage
	 * 
	 * @param is
	 *            JSON keystore
	 * @param password
	 *            Scrypt password used to derive AES key
	 * @param storage
	 *            output for keystore to write to
	 * @throws Exception
	 */
	public static void load(InputStream is, String password, Storage storage) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		load(mapper.readValue(is, KeyStore.class), password, storage);
	}

	public static void load(KeyStore keystore, String password, Storage storage) throws Exception {
		Protos.ScryptParameters.Builder scryptParametersBuilder = Protos.ScryptParameters.newBuilder()
				.setSalt(ByteString.copyFrom(base64Url().decode(keystore.getScrypt().getSalt())))
				.setN(keystore.getScrypt().getN()).setR(keystore.getScrypt().getR()).setP(keystore.getScrypt().getP());

		KeyCrypterScrypt crypterScrypt = new KeyCrypterScrypt(scryptParametersBuilder.build());
		KeyParameter aesKey = crypterScrypt.deriveKey(password);

		for (KeyEntry entry : keystore.getKeys()) {
			byte[] key = base64Url().decode(entry.getPk());
			byte[] iv = base64Url().decode(entry.getIv());

			EncryptedData data = new EncryptedData(iv, key);
			ECKey ecKey = ECKey.fromPrivate(crypterScrypt.decrypt(data, aesKey));

			String address = ecKey.toAddress(MainNetParams.get()).toString();

			storage.add(entry.getAlias(), address, ecKey.getPrivKeyBytes(), ecKey.getPubKey());
		}
	}
	// ECKey.fromPrivate(kc.getPriv())
}
