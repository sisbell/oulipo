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
package org.oulipo.streams.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.InvariantStream;
import org.oulipo.streams.types.InvariantSpan;

import com.google.common.base.Strings;

/**
 * An <code>InvariantStream</code> that uses the file system for accessing text.
 */
public class FileInvariantStream implements InvariantStream {

	private static ByteBuffer buffer = ByteBuffer.allocate(1024);

	private FileChannel channel;

	private final TumblerAddress homeDocument;

	/**
	 * Constructs an InvariantStream backed by the specified file. Creates a new
	 * file if it does not exist
	 * 
	 * @param file
	 *            backing file
	 * @throws IOException
	 *             if there is an I/O exception with the specified file
	 */
	public FileInvariantStream(File file, TumblerAddress homeDocument) throws IOException {
		file.getParentFile().mkdirs();
		file.createNewFile();
		RandomAccessFile f = new RandomAccessFile(file, "rw");

		channel = f.getChannel();
		channel.position(channel.size());
		this.homeDocument = homeDocument;
	}

	@Override
	public InvariantSpan append(String text) throws IOException, MalformedSpanException {
		if (Strings.isNullOrEmpty(text)) {
			throw new MalformedSpanException("No text - span length is 0");
		}

		FileLock lock = channel.lock();
		try {
			InvariantSpan span = new InvariantSpan(channel.position() + 1, text.length(),
					homeDocument);

			buffer.clear();
			buffer.put(text.getBytes());
			buffer.flip();

			while (buffer.hasRemaining()) {
				channel.write(buffer);
			}

			channel.force(true);
			return span;
		} finally {
			lock.release();
		}
	}

	@Override
	public String getText(InvariantSpan ispan) throws IOException {
		return getText(ispan.getStart(), ispan.getWidth());
	}

	private String getText(long position, long width) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate((int) width);
		channel.read(buf, position - 1);
		buf.rewind();
		return Charset.forName("UTF-8").decode(buf).toString();
	}
}
