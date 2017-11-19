package org.oulipo.streams;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;

import org.bitcoinj.core.ECKey;

public interface Compiler<T> {

	default String compile(T file, ECKey ecKey) throws Exception {
		if (file == null) {
			throw new IllegalArgumentException("Input file is null");
		}

		if (ecKey == null) {
			throw new IllegalArgumentException("ECKey is null");
		}
		return compileEncrypted(file, ecKey, null);
	}

	/**
	 * Compile <code>DocumentFile</code> to binary and generated a signature using
	 * the specified key.
	 * 
	 * @param ecKey
	 *            the key to use to generate signature
	 * @return a string in the format [documentFile + "." + publicKeyHash + "." +
	 *         signature64];
	 * @throws Exception
	 */
	String compileEncrypted(T file, ECKey ecKey, Key key) throws Exception;

	default String encrypt(String message, Key publicKey) throws Exception {
		Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
		c.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherTextArray = c.doFinal(message.getBytes());
		return Base64.getEncoder().encodeToString(cipherTextArray);
	}

}
