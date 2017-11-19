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
package org.oulipo.streams.document;

import static org.junit.Assert.assertEquals;

import java.security.SignatureException;

import org.bitcoinj.core.ECKey;
import org.junit.Test;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.opcodes.CopyVariantOp;
import org.oulipo.streams.opcodes.DeleteVariantOp;
import org.oulipo.streams.opcodes.Op;
import org.oulipo.streams.overlays.PresenterOverlay;

import com.google.common.collect.Sets;

public class DocumentFileTest {

	@Test
	public void addText() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.appendText("Xanadu");
		builder.appendText("Green");

		DocumentFile file = builder.build();
		assertEquals("XanaduGreen", file.getInvariantStream());
	}

	@Test(expected = SignatureException.class)
	public void badSig() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");

		DocumentFile file = builder.build();
		String compiledDocument = DocumentFile.compiler().compile(file, new ECKey());

		DocumentFile.decompiler().decompile("fakeHash",
				compiledDocument.subSequence(0, compiledDocument.length() - 1).toString().getBytes());
	}

	@Test
	public void copyVariant() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.copyVariant(100, new VariantSpan(200, 10));

		DocumentFile file = builder.build();
		assertEquals(new CopyVariantOp(100, new VariantSpan(200, 10)), file.getOps().get(0));
	}

	@Test
	public void copyVariantCompile() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.copyVariant(100, new VariantSpan(200, 10));

		DocumentFile file = builder.build();
		String compiledDocument = DocumentFile.compiler().compile(file, new ECKey());

		DocumentFile result = DocumentFile.decompiler().decompile("fakeHash", compiledDocument.getBytes());
		assertEquals(new CopyVariantOp(100, new VariantSpan(200, 10)), result.getOps().get(0));
	}

	@Test
	public void deleteVariant() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.deleteVariant(new VariantSpan(200, 10));

		DocumentFile file = builder.build();
		assertEquals(new DeleteVariantOp(new VariantSpan(200, 10)), file.getOps().get(0));
	}

	@Test
	public void deleteVariantCompile() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.deleteVariant(new VariantSpan(200, 10));

		DocumentFile file = builder.build();
		String compiledDocument = DocumentFile.compiler().compile(file, new ECKey());

		DocumentFile result = DocumentFile.decompiler().decompile("fakeHash", compiledDocument.getBytes());
		assertEquals(new DeleteVariantOp(new VariantSpan(200, 10)), result.getOps().get(0));
	}

	@Test
	public void hashBlock() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.previousHashBlock("sadasdsad");

		DocumentFile file = builder.build();
		assertEquals("sadasdsad", file.getHashPreviousBlock());
	}

	@Test
	public void putInvariantMedia() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.putInvariantMediaOp(50, "fakeHash");

		DocumentFile file = builder.build();
		assertEquals(Op.PUT_INVARIANT_MEDIA, file.getOps().get(0).getCode());

		assertEquals("fakeHash", file.getString(0));
	}

	@Test
	public void putInvariantMediaOp() throws Exception {

		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.putInvariantMediaOp(1, "fakeHash");
		DocumentFile file = builder.build();

		String compiledDocument = DocumentFile.compiler().compile(file, new ECKey());

		DocumentFile result = DocumentFile.decompiler().decompile("fakeHash", compiledDocument.getBytes());
		assertEquals("fakeHash", result.getString(0));
	}

	@Test
	public void putOverlay() throws Exception {
		DocumentFile.Builder builder = new DocumentFile.Builder("fakeHash");
		builder.putOverlayOp(new VariantSpan(1, 10), Sets.newHashSet(PresenterOverlay.BOLD_OVERLAY));

		DocumentFile file = builder.build();
		assertEquals(Op.PUT_OVERLAY, file.getOps().get(0).getCode());

		assertEquals(PresenterOverlay.BOLD_OVERLAY, file.getOverlay(0));
	}

}
