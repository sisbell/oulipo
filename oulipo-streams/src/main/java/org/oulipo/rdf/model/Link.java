package org.oulipo.rdf.model;

import org.oulipo.rdf.Thing;
import org.oulipo.rdf.annotations.Subject;

@Subject(value = Schema.LINK, key = "resourceId")
public class Link extends Thing {

	public String fromDocumentHash;

	public long fromInvariantStart;

	public long fromInvariantWidth;

	public String linkType;

	public String toDocumentHash;
}
