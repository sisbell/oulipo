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
package org.oulipo.streams.types;

import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.StreamElementPartition;

import com.google.common.base.Strings;

public final class InvariantSpan implements Invariant {

	/**
	 * Home document of this element. The home document may be for spans
	 * (characters) or for a media type (image/video)
	 */
	protected String documentHash;

	protected boolean isEncrypted;

	/**
	 * Start byte position of an invariant stream
	 */
	protected long start;

	/**
	 * Number of characters in the element
	 */
	protected long width;

	public InvariantSpan() {
	}

	public InvariantSpan(long start, long width, String documentHash) throws MalformedSpanException {
		if (width < 1) {
			throw new MalformedSpanException("Width must be greater than 0");
		}

		if (start < 1) {
			throw new MalformedSpanException("Start must be greater than 0");
		}

		this.width = width;
		this.documentHash = documentHash;
		this.start = start;
	}

	@Override
	public InvariantSpan copy() throws MalformedSpanException {
		return new InvariantSpan(start, width, documentHash);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InvariantSpan other = (InvariantSpan) obj;
		if (documentHash == null) {
			if (other.documentHash != null)
				return false;
		} else if (!documentHash.equals(other.documentHash))
			return false;
		if (start != other.start)
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	public String getDocumentHash() {
		return documentHash;
	}

	public long getStart() {
		return start;
	}

	@Override
	public long getWidth() {
		return width;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentHash == null) ? 0 : documentHash.hashCode());
		result = prime * result + (int) (start ^ (start >>> 32));
		result = prime * result + (int) (width ^ (width >>> 32));
		return result;
	}

	public boolean hashDocumentHash() {
		return !Strings.isNullOrEmpty(documentHash);
	}

	public void setStart(long start) {
		this.start = start;
	}

	@Override
	public void setWidth(long width) {
		this.width = width;
	}

	/**
	 * Splits this invariant span into two parts. The left part will have a width if
	 * the specified leftPartitionWidth, while the right partition width will be
	 * span.width - leftPartitionWidth.
	 * 
	 * @param leftPartitionWidth
	 *            required width of left part of the partition
	 * @return an invariant span partition
	 * @throws MalformedSpanException
	 *             if the left or right partition of this span has a start < 1 ||
	 *             width < 1
	 * @throws IndexOutOfBoundsException
	 *             if the position is out of range (position >= start + width ||
	 *             position <= start)
	 */
	@Override
	public StreamElementPartition<InvariantSpan> split(long leftPartitionWidth) throws MalformedSpanException {
		if (leftPartitionWidth < 1) {
			throw new IndexOutOfBoundsException(
					"Partition width must be greater than 0, partitionWidth = " + leftPartitionWidth);
		}

		if (width <= leftPartitionWidth) {
			throw new IndexOutOfBoundsException("Width of left partition is greater than or equal to span: span width ="
					+ width + ", partitionWidth = " + leftPartitionWidth);
		}
		return new StreamElementPartition<InvariantSpan>(new InvariantSpan(start, leftPartitionWidth, documentHash),
				new InvariantSpan(start + leftPartitionWidth, width - leftPartitionWidth, documentHash));
	}

	@Override
	public String toString() {
		return "InvariantSpan [documentHash=" + documentHash + ", start=" + start + ", width=" + width + "]";
	}

}
