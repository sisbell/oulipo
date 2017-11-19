package org.oulipo.rdf.model;

import org.oulipo.rdf.Thing;
import org.oulipo.rdf.annotations.ObjectString;
import org.oulipo.rdf.annotations.Predicate;
import org.oulipo.rdf.annotations.Subject;

@Subject(value = Schema.IMAGE, key = "resourceId")
public class Image extends Thing {

	@Predicate("hash")
	@ObjectString
	public String hash;

}
