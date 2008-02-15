package org.usemon.jdk4installer;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;

import junit.framework.TestCase;

import com.usemon.agent.instrumentation.RootInstrumentor;
import com.usemon.installer.ClassLoaderInstrumenter;
import com.usemon.lib.javassist.CannotCompileException;
import com.usemon.lib.javassist.ClassPool;
import com.usemon.lib.javassist.CtClass;
import com.usemon.lib.javassist.CtMethod;
import com.usemon.lib.javassist.LoaderClassPath;
import com.usemon.lib.javassist.NotFoundException;

public class ClassLoaderInstrumenterTest extends TestCase {

	static final String TARGET_TEST_CLASSES = "target/test-classes";

	ClassPool classPool = null;
	protected void setUp() throws Exception {
		super.setUp();
		classPool = new ClassPool();
		classPool.appendClassPath(new LoaderClassPath(RootInstrumentor.class.getClassLoader()));
		assertTrue("This JUnit test requires a JDK 1.4",System.getProperty("java.version").startsWith("1.4"));
	}

	/** Attempts to instrument a JDK1.4 class loader */
	public void testInstrument() throws CannotCompileException, NotFoundException, IOException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		
		
		System.err.println(Object.class.getClassLoader());
		System.err.println(RootInstrumentor.class.getClassLoader());
		
		String classLoaderName = ClassLoader.class.getName();
		// Creates the compile time class instance for the class loader
		CtClass classLoaderCtClass = classPool.get(classLoaderName);
		
		assertNotNull(classLoaderCtClass);

		System.out.println("Starting out with: " + classLoaderCtClass.getName());
		assertTrue( classLoaderCtClass.getMethods().length > 10);
		
		// Modifies the copied class loader
		CtClass modifiedClassLoader = ClassLoaderInstrumenter.instrument(classLoaderCtClass);

		// Writes the class to disk
		writeModifiedClassLoaderToDisk(modifiedClassLoader);
		// Verfies that the .class file was actually written
		File f2 = new File(TARGET_TEST_CLASSES + "/" + classLoaderName.replace('.','/')+ ".class");
		
		assertTrue(f2.exists() && f2.isFile());
	}

	public void testInstrumentDummyClass() throws NotFoundException, CannotCompileException {
		CtClass ctClass = classPool.get(DummyClassLoader.class.getName());
		ClassLoaderInstrumenter.instrument(ctClass);
		CtMethod[] ctMethods = ctClass.getMethods();
		boolean newMethodAdded = false;
		for (int i=0; i < ctMethods.length; i++) {
			CtMethod ctMethod = ctMethods[i];
			
			if (ClassLoaderInstrumenter.composeNewMethodName().equals(ctMethod.getName()))
				newMethodAdded=true;
		}
		assertTrue("New method " + ClassLoaderInstrumenter.composeNewMethodName() + " was not added", newMethodAdded);
	}

	/** Verifies that an exception is thrown by {@link ClassLoaderInstrumenter#instrument(CtClass)}
	 * if the supplied class loader does not contain the method named <code>defineClass0</code>.
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public void testInstrumentInvalidClassLoader() throws NotFoundException, CannotCompileException {
		CtClass ctClass = classPool.get(InvalidClassLoader.class.getName());
		try {
			ClassLoaderInstrumenter.instrument(ctClass);
			fail("Exception not thrown when defineClass0 method not found");
		} catch (IllegalStateException e) {
			assertTrue(e.getMessage().indexOf("not found") >=0 );
		}
	}
	
	/**
	 * Verifies that an exception is thrown by {@link ClassLoaderInstrumenter#instrument(CtClass)} if the <code>defineClass0</code> of 
	 * the supplied class loader is not native
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public void testInstrumentClassLoaderWithNonNativeDefineClass0Method() throws NotFoundException, CannotCompileException {
		CtClass ctClass = classPool.get(InvalidClassLoader2.class.getName());
		try {
			ClassLoaderInstrumenter.instrument(ctClass);
			fail("Exception not thrown when defineClass0 method not found");
		} catch (IllegalStateException e) {
			assertTrue(e.getMessage().indexOf("native") >=0 );
		}
	}
	
	/**
	 * @param modifiedClassLoader
	 * @throws CannotCompileException
	 * @throws IOException
	 */
	private void writeModifiedClassLoaderToDisk(CtClass modifiedClassLoader) throws CannotCompileException, IOException {
		File f = new File(ClassLoaderInstrumenterTest.TARGET_TEST_CLASSES);
		assertTrue(f.exists() && f.isDirectory());
		
		modifiedClassLoader.writeFile(f.getPath());
	}

	public static class DummyClassLoader extends ClassLoader {
		public native void defineClass0(String name, byte[] b, int off, int len, ProtectionDomain domain);
	}

	public static class InvalidClassLoader  extends ClassLoader {
		
	}

	public static class InvalidClassLoader2  extends ClassLoader {
		// This method is not native, so instrumentation should fail
		public  void defineClass0(String name, byte[] b, int off, int len, ProtectionDomain domain) {
			
		}
		
	}

}
