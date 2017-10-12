package org.oulipo.streams.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;

public class OverlayElement extends StreamElement {

	private Set<TumblerAddress> linkTypes;

	protected OverlayElement() { }
	
	public OverlayElement(long width, TumblerAddress homeDocument) throws MalformedSpanException {
		super(width, homeDocument);
		this.linkTypes = new HashSet<>();
	}

	public OverlayElement(long width, TumblerAddress homeDocument, Set<TumblerAddress> linkTypes)
			throws MalformedSpanException {
		super(width, homeDocument);
		this.linkTypes = linkTypes;
	}

	public OverlayElement(long width, TumblerAddress homeDocument, TumblerAddress... linkTypes)
			throws MalformedSpanException {
		super(width, homeDocument);
		this.linkTypes = new HashSet<TumblerAddress>(Arrays.asList(linkTypes));
	}

	public void addLinkType(TumblerAddress linkType) {
		linkTypes.add(linkType);
	}

	public void addLinkTypes(Collection<TumblerAddress> linkTypes) {
		this.linkTypes.addAll(linkTypes);
	}

	@Override
	public OverlayElement copy() throws MalformedSpanException {
		return new OverlayElement(width, homeDocument, new HashSet<>(linkTypes));
	}

	public boolean hasLinks() {
		return !linkTypes.isEmpty();
	}

	public boolean hasLinkType(TumblerAddress link) {
		return linkTypes.contains(link);
	}

	public int linkCount() {
		return linkTypes.size();
	}

	public void removeLinkType(TumblerAddress linkType) {
		linkTypes.remove(linkType);
	}

	@Override
	public StreamElementPartition<OverlayElement> split(long cutPoint) throws MalformedSpanException {
		long w = width - cutPoint;
		return new StreamElementPartition<OverlayElement>(new OverlayElement(cutPoint, homeDocument, new HashSet<>(linkTypes)),
				new OverlayElement(w, homeDocument, new HashSet<>(linkTypes)));
	}

	@Override
	public String toString() {
		return "OverlayElement [linkTypes=" + linkTypes + ", homeDocument=" + homeDocument + ", width=" + width + "]";
	}
}
