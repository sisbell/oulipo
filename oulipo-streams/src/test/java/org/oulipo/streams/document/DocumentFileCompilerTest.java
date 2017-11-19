package org.oulipo.streams.document;

import static org.junit.Assert.assertEquals;

import java.net.URLDecoder;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.junit.Test;

public class DocumentFileCompilerTest {

	@Test
	public void addEncryptedTextCompile() throws Exception {

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2056);
		KeyPair pair = keyGen.generateKeyPair();

		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.appendEncryptedText("Xanadu");
		builder.appendEncryptedText("Green");

		DocumentFile file = builder.build();
		String compiledDocument = DocumentFile.compiler().compileEncrypted(file, new ECKey(), pair.getPublic());

		DocumentFile result = DocumentFile.decompiler().decompileEncrypted("fakehash", compiledDocument.getBytes(),
				pair.getPrivate());
		assertEquals("XanaduGreen", result.getEncyptedInvariantStream());
	}

	@Test
	public void addTextCompile() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.appendText("Xanadu");
		builder.appendText("Green");

		DocumentFile file = builder.build();

		String compiledDocument = DocumentFile.compiler().compile(file, new ECKey());

		DocumentFile result = DocumentFile.decompiler().decompile("fakehash", compiledDocument.getBytes());
		assertEquals("XanaduGreen", result.getInvariantStream());
	}

	@Test
	public void compilePublicKeyHash() throws Exception {
		DocumentFile file = new DocumentFile.Builder("fakeHash").build();
		ECKey key = new ECKey();
		String result = DocumentFile.compiler().compile(file, key);
		String[] tokens = result.split("[.]");

		String publicKeyHash = key.toAddress(MainNetParams.get()).toString();
		assertEquals(publicKeyHash, URLDecoder.decode(tokens[1], "UTF-8"));
	}

	@Test
	public void hashBlockCompile() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.previousHashBlock("sadasdsad");

		DocumentFile file = builder.build();
		String compiledDocument = DocumentFile.compiler().compile(file, new ECKey());

		DocumentFile result = DocumentFile.decompiler().decompile("fakeHash", compiledDocument.getBytes());

		assertEquals("sadasdsad", result.getHashPreviousBlock());
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullCompileKey() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");

		DocumentFile file = builder.build();
		DocumentFile.compiler().compile(file, null);
	}

}
