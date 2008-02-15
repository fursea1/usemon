package org.usemon.jdk4installer;

import junit.framework.TestCase;

public class RunWithModifiedClassLoaderTest extends TestCase {

	public void testLoadClass() {
		Singleton singleton = Singleton.getInstance();
		assertEquals("Hello world",singleton.helloWorld());
	}
	public static class Singleton {
		private static Singleton singleton = new Singleton();
		
		private Singleton(){
		};
		
		public static Singleton getInstance() {
			return singleton;
		}
		
		public String helloWorld() {
			return "Hello world";
		}
		
	}
}
