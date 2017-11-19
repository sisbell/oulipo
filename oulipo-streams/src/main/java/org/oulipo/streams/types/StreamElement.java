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

public interface StreamElement {

	public static final int OVERLAY = 0x1;

	public static final int OVERLAY_MEDIA = 0x3;

	public static final int SPAN = 0x0;

	public static final int SPAN_MEDIA = 0x2;

	StreamElement copy() throws MalformedSpanException;

	long getWidth();

	void setWidth(long width);

	/**
	 * Partitions this StreamElement into a left and right half.
	 * 
	 * If the cutPoint is on the left boundary, then an IndexOutOfBoundsException
	 * will be thrown. If the cutPoint is on the right boundary, then it will be the
	 * only element in the right partition.
	 * 
	 * @param leftPartitionWidth
	 *            required width of left part of the partition
	 * @return
	 * @throws MalformedSpanException
	 */
	StreamElementPartition<? extends StreamElement> split(long leftPartitionWidth) throws MalformedSpanException;

}
