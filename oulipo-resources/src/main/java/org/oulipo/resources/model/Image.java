package org.oulipo.resources.model;

import org.oulipo.net.TumblerAddress;
import org.oulipo.rdf.annotations.ObjectString;
import org.oulipo.rdf.annotations.ObjectTumbler;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.Subject;

@Subject(value = Schema.IMAGE, key = "resourceId")
public class Image extends Thing {

	@Predicate("document")
	@ObjectTumbler
	public TumblerAddress document;

	@Predicate("hash")
	@ObjectString
	public String hash;

}
