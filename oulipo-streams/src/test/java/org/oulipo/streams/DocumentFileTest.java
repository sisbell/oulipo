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
package org.oulipo.streams;

import static org.junit.Assert.assertEquals;

import java.net.URLDecoder;
import java.security.SignatureException;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.junit.Test;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.opcodes.CopyVariantOp;
import org.oulipo.streams.opcodes.DeleteVariantOp;
import org.oulipo.streams.opcodes.Op;

import com.google.common.collect.Sets;

public class DocumentFileTest {

	@Test
	public void addText() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));
		builder.appendText("Xanadu");
		builder.appendText("Green");

		DocumentFile file = builder.build();
		assertEquals("XanaduGreen", file.getInvariantStringArea());
	}

	@Test
	public void addTextCompile() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));
		builder.appendText("Xanadu");
		builder.appendText("Green");

		DocumentFile file = builder.build();
		String compiledDocument = file.compile(new ECKey());

		DocumentFile result = DocumentFile.read(compiledDocument.getBytes());
		assertEquals("XanaduGreen", result.getInvariantStringArea());
	}

	@Test(expected = SignatureException.class)
	public void badSig() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));

		DocumentFile file = builder.build();
		String compiledDocument = file.compile(new ECKey());

		DocumentFile.read(compiledDocument.subSequence(0, compiledDocument.length() - 1).toString().getBytes());
	}
	/*
	@Test(expected = IOException.class)
	public void badMagic() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));

		DocumentFile file = builder.build();
		byte[] compiledDocument = file.compile(new ECKey()).getBytes();
		compiledDocument[0] = 100;
		DocumentFile.read(compiledDocument);
	}
*/

	@Test(expected = IllegalArgumentException.class)
	public void buildNoDocument() throws Exception {
		new DocumentFile.Builder(TumblerAddress.create("1.2.0.12")).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildWithElementField() throws Exception {
		new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1.0.1.1")).build();
	}

	@Test
	public void compilePublicKeyHash() throws Exception {
		DocumentFile file = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1")).build();
		ECKey key = new ECKey();
		String result = file.compile(key);
		String[] tokens = result.split("[.]");

		String publicKeyHash = key.toAddress(MainNetParams.get()).toString();
		assertEquals(publicKeyHash, URLDecoder.decode(tokens[1], "UTF-8"));
	}
	
	@Test
	public void copyVariant() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));
		builder.copyVariant(100, new VariantSpan(200, 10));

		DocumentFile file = builder.build();
		assertEquals(new CopyVariantOp(100, new VariantSpan(200, 10)), file.getOps().get(0));
	}
	
	@Test
	public void copyVariantCompile() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));
		builder.copyVariant(100, new VariantSpan(200, 10));

		DocumentFile file = builder.build();
		String compiledDocument = file.compile(new ECKey());

		DocumentFile result = DocumentFile.read(compiledDocument.getBytes());
		assertEquals(new CopyVariantOp(100, new VariantSpan(200, 10)), result.getOps().get(0));
	}

	@Test
	public void deleteVariant() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));
		builder.deleteVariant(new VariantSpan(200, 10));

		DocumentFile file = builder.build();
		assertEquals(new DeleteVariantOp(new VariantSpan(200, 10)), file.getOps().get(0));
	}

	@Test
	public void deleteVariantCompile() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));
		builder.deleteVariant(new VariantSpan(200, 10));

		DocumentFile file = builder.build();
		String compiledDocument = file.compile(new ECKey());

		DocumentFile result = DocumentFile.read(compiledDocument.getBytes());
		assertEquals(new DeleteVariantOp(new VariantSpan(200, 10)), result.getOps().get(0));
	}

	@Test
	public void hashBlock() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));
		builder.previousHashBlock("sadasdsad");

		DocumentFile file = builder.build();
		assertEquals("sadasdsad", file.getHashPreviousBlock());
	}

	@Test
	public void hashBlockCompile() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));
		builder.previousHashBlock("sadasdsad");

		DocumentFile file = builder.build();
		String compiledDocument = file.compile(new ECKey());

		DocumentFile result = DocumentFile.read(compiledDocument.getBytes());

		assertEquals("sadasdsad", result.getHashPreviousBlock());
	}
	
	@Test
	public void homeDocumentInTumblerPool() throws Exception {
		TumblerAddress homeDocument = TumblerAddress.create("1.2.0.12.0.3.2.1");
		DocumentFile.Builder builder = new DocumentFile.Builder(homeDocument);

		DocumentFile file = builder.build();
		assertEquals(homeDocument, file.getHomeDocument());

		assertEquals(1, file.getTumblerPool().size());
		assertEquals("ted://1.2.0.12.0.3.2.1", file.getTumblerPool().get(0));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void nullCompileKey() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));

		DocumentFile file = builder.build();
		file.compile(null);
	}

	@Test
	public void putInvariantMedia() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));
		builder.putInvariantMediaOp(50, "fakehash", TumblerAddress.create("1.2.0.12.0.3.2.1.0.2.1"));

		DocumentFile file = builder.build();
		assertEquals(Op.PUT_INVARIANT_MEDIA, file.getOps().get(0).getCode());
		
		assertEquals("fakehash", file.getMediaHash(0));
	}

	@Test
	public void putOverlay() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder(TumblerAddress.create("1.2.0.12.0.3.2.1"));
		builder.putOverlayOp(new VariantSpan(1, 10), Sets.newHashSet(TumblerAddress.BOLD));

		DocumentFile file = builder.build();
		assertEquals(Op.PUT_OVERLAY, file.getOps().get(0).getCode());
		
		assertEquals(TumblerAddress.BOLD, file.getTumblerAddress(1));
	}
}
