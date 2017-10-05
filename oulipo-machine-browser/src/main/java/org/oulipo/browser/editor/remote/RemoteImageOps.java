package org.oulipo.browser.editor.remote;

import java.util.Optional;

import org.fxmisc.richtext.model.SegmentOps;

/**
 * Operations for remote image segments
 *
 * @param <S>
 */
public class RemoteImageOps<S> implements SegmentOps<RemoteImage<S>, S> {

	private final EmptyRemoteImage<S> emptySeg = new EmptyRemoteImage<>();

	@Override
	public char charAt(RemoteImage<S> seg, int index) {
		return seg == emptySeg ? '\0' : '\ufffc';
	}

	@Override
	public RemoteImage<S> createEmpty() {
		return emptySeg;
	}

	@Override
	public S getStyle(RemoteImage<S> seg) {
		return seg.getStyle();
	}

	@Override
	public String getText(RemoteImage<S> seg) {
		return seg == emptySeg ? "" : "\ufffc";
	}

	@Override
	public Optional<RemoteImage<S>> join(RemoteImage<S> currentSeg, RemoteImage<S> nextSeg) {
		return Optional.empty();
	}

	@Override
	public int length(RemoteImage<S> seg) {
		return seg == emptySeg ? 0 : 1;
	}

	@Override
	public RemoteImage<S> setStyle(RemoteImage<S> seg, S style) {
		return seg == emptySeg ? emptySeg : seg.setStyle(style);
	}

	@Override
	public RemoteImage<S> subSequence(RemoteImage<S> seg, int start) {
		if (start < 0) {
			throw new IllegalArgumentException("Start cannot be negative. Start = " + start);
		}
		return start == 0 ? seg : emptySeg;
	}

	@Override
	public RemoteImage<S> subSequence(RemoteImage<S> seg, int start, int end) {
		if (start < 0) {
			throw new IllegalArgumentException("Start cannot be negative. Start = " + start);
		}
		if (end > length(seg)) {
			throw new IllegalArgumentException("End cannot be greater than segment's length");
		}
		return start == 0 && end == 1 ? seg : emptySeg;
	
	}

}
