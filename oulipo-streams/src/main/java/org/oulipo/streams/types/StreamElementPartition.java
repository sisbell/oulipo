package org.oulipo.streams.types;

/**
 * A two-part partition of a stream element. It has left and right elements
 * along a cut point.
 */
public final class StreamElementPartition<T extends StreamElement> {
	
	/**
	 * Left half of partition
	 */
	private final T left;

	/**
	 * Right half of partition
	 */
	private final T right;

	/**
	 * Constructs a partition containing a left and right stream elements.
	 * 
	 * @param left
	 *            left half of partition. Must not be null
	 * @param right
	 *            right half of partition. Must not be null.
	 */
	public StreamElementPartition(T left, T right) {
		if (left == null) {
			throw new IllegalArgumentException("left span is null");
		}

		if (right == null) {
			throw new IllegalArgumentException("right span is null");
		}

		this.left = left;
		this.right = right;

	}

	/**
	 * Gets left half of partition
	 * 
	 * @return
	 */
	public T getLeft() {
		return left;
	}

	/**
	 * Gets right half of partition
	 * 
	 * @return
	 */
	public T getRight() {
		return right;
	}

}
