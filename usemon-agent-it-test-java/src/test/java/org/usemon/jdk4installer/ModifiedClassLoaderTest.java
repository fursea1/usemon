package org.usemon.jdk4installer;


import junit.framework.TestCase;

import com.usemon.lib.javassist.ClassPool;
import com.usemon.lib.javassist.CtClass;
import com.usemon.lib.javassist.CtMethod;
import com.usemon.lib.javassist.NotFoundException;

public class ModifiedClassLoaderTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testInspect() throws NotFoundException {
		ClassPool classPool = new ClassPool(true);
		CtClass ctClassLoader = classPool.get("java.lang.ClassLoader");
	}
}
