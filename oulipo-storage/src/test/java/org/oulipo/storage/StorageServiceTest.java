package org.oulipo.storage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class StorageServiceTest {

	private StorageService service;
	
	@Before
	public void setup() {
		try {
			service = new StorageService("./target/StorageServiceTest");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void cleanup() {
		try {
			Files.walk(Paths.get("./target/StorageServiceTest"))
			.map(Path::toFile)
			.sorted((o1, o2) -> -o1.compareTo(o2))
			.forEach(File::delete);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getAll() throws Exception {
		TestObject to1 = new TestObject("1", "456");
		TestObject to2 = new TestObject("2", "abc");
		TestObject to3 = new TestObject("3", "xyz");
		
		service.save(to1);
		service.save(to2);
		service.save(to3);

		Collection<TestObject> results = service.getAll(TestObject.class);
		assertEquals(3, results.size());
		assertTrue(results.contains(to1));
		assertTrue(results.contains(to2));
		assertTrue(results.contains(to3));

	}
	
	@Test
	public void delete() throws Exception {
		TestObject to1 = new TestObject("1", "456");
		TestObject to2 = new TestObject("2", "abc");
		TestObject to3 = new TestObject("3", "xyz");
		
		service.save(to1);
		service.save(to2);
		service.save(to3);

		service.delete("1", TestObject.class);
		service.delete("3", TestObject.class);
		
		Collection<TestObject> results = service.getAll(TestObject.class);
		assertEquals(1, results.size());
		assertFalse(results.contains(to1));
		assertTrue(results.contains(to2));
		assertFalse(results.contains(to3));

	}

}
