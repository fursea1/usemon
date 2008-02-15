/*
 * Created on Mar 8, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.usemon.installer;

import com.usemon.agent.instrumentation.RootInstrumentor;
import com.usemon.lib.javassist.CannotCompileException;
import com.usemon.lib.javassist.ClassMap;
import com.usemon.lib.javassist.CtClass;
import com.usemon.lib.javassist.CtMethod;
import com.usemon.lib.javassist.CtNewMethod;
import com.usemon.lib.javassist.Modifier;
import com.usemon.lib.javassist.expr.ExprEditor;
import com.usemon.lib.javassist.expr.MethodCall;

/**
 * @author t535293
 * 
 */
public class ClassLoaderInstrumenter {

	/** Name of method which triggers our modification of the supplied class */
	protected static final String DEFINE_CLASS0 = "defineClass0";

	/**
	 * Instruments a Java {@link ClassLoader} by adding a new method to it named <code>usemon_instrument_defineClass0</code>.
	 * 
	 * <code>defineClass0</code> is a native method, i.e. programmed in C (most likely) and supplied together with your JRE or
	 * JDK. It transforms a byte array into a java {@link Class}
	 * 
	 * @param javaClass
	 *            the class loader to modify
	 * @return compile time class representing the byte code of the modified class loader.
	 * @throws CannotCompileException
	 * 
	 * @see {@link RootInstrumentor#transform(ClassLoader, String, java.security.ProtectionDomain, byte[], int, int)}
	 */
	public static CtClass instrument(CtClass javaClass) throws CannotCompileException {

		addNewDefineClass0Method(javaClass);
		replaceInvocationsOfDefineClass0(javaClass);
		return javaClass;
	}

	/**
	 * Adds another method to the class if it contains a native method named 'defineClass0'
	 * 
	 * @param javaClass
	 * @param methods
	 * @throws CannotCompileException
	 * @return <code>true</code> if the native method <code>defineClass0</code> was found and a new method with the name
	 *         composed from invocation of {@link #composeNewMethodName()} was added to the class, <code>false</code> otherwise.
	 */
	private static void addNewDefineClass0Method(CtClass javaClass) throws CannotCompileException {
		CtMethod[] methods = javaClass.getDeclaredMethods();

		boolean methodFound = false;

		// Iterates over all the methods in the class, which is a java class
		// loader
		for (int n = 0; n < methods.length; n++) {
			CtMethod method = methods[n];

			if (composeNewMethodName().equals(method.getName()))
				throw new IllegalArgumentException("Class loader already instrumented: " + javaClass.getName());
			// once we find the defineClass0() method
			if (DEFINE_CLASS0.equals(method.getName())) {
				methodFound = true;
				// we verify that the method is in fact native
				if (Modifier.isNative(method.getModifiers())) {
					System.out.println("Adding new method " + Constants.instrumentCallback + "() to " + javaClass.getName());
					// creates a copy of the 'defineClass0' declaration, i.e.
					// this is a native method so there is no implementation available to us
					// The copy is given a name which we have prepended with a
					// constant to shove it into our own name space
					// When you create a copy of something, you must give it a name :-)
					CtMethod newDefineClass0Method = CtNewMethod.copy(method, composeNewMethodName(), javaClass, new ClassMap());

					// removes the native flag, as we will actually supply the
					// java byte code implementation for it.
					newDefineClass0Method.setModifiers(newDefineClass0Method.getModifiers() ^ Modifier.NATIVE);
					StringBuffer code = new StringBuffer();
					code.append("{");
					// $0 is first argument, $1 is second etc.
					// When executed, this code will invoke our
					// com.usemon.agent.instrumentation.RootInstrumentor.transform() method
					code.append("  byte[] newClass = " + Constants.instrumentCallback + "($0, $1, $5, $2, $3, $4);");

					// if the returned byte array is not null, the class was instrumented
					code.append("  if(newClass!=null) {");
					// replaces the original byte array with our modified byte array
					code.append("    $2 = newClass;");
					code.append("    $3 = 0;"); // start of byte array
					// the length of our modified byte array
					code.append("    $4 = newClass.length;");
					code.append("  }");
					// invokes the original native code which we act as a proxy for.
					code.append("  return defineClass0($$);");
					code.append("}");
					// In our copy of the defineClass0 method, the body is empty
					// since the method was native,
					// Fills the empty body with our code
					newDefineClass0Method.setBody(code.toString());
					newDefineClass0Method.insertBefore(""); // This works, remove at your own risk :-)
					javaClass.addMethod(newDefineClass0Method); // adds our new method into the class
					break;
				} else
					throw new IllegalStateException(method.getName() + " is not native");
			}
		}
		if (!methodFound)
			throw new IllegalStateException("Method " + DEFINE_CLASS0 + " not found in " + javaClass.getName());
	}

	/**
	 * Composes the new name of the new method which the {@link #instrument(CtClass)} method will insert.
	 * 
	 * @return name of the new method
	 */
	public static String composeNewMethodName() {
		return Constants.instrumentMethodPrefix + DEFINE_CLASS0;
	}

	/**
	 * Adds code which will intercept expressions which invoke defineClass0
	 * 
	 * @param methods
	 *            the methods to modify
	 * @throws CannotCompileException
	 */
	private static void replaceInvocationsOfDefineClass0(CtClass javaClass) throws CannotCompileException {
		CtMethod[] methods = javaClass.getDeclaredMethods();

		for (int n = 0; n < methods.length; n++) {
			CtMethod method = methods[n];
			int modifiers = method.getModifiers();
			
			
			if (!method.getName().startsWith(Constants.instrumentMethodPrefix)) {
				// if method is not (native or abstract), i.e. the method has an
				// implementation (a body)
				if (!(Modifier.isNative(modifiers) || Modifier.isAbstract(modifiers))) {
					method.instrument(new ExprEditor() {
						// intercepts only the expressions which are method calls
						public void edit(MethodCall mc) throws CannotCompileException {
							// we only take an interest in the invocations of
							// defineClass0() method
							if (DEFINE_CLASS0.equals(mc.getMethodName())) {
								// Replaces the native defineClass0 with our proxy
								// method
								mc.replace("$_ = " + Constants.instrumentMethodPrefix + "defineClass0($$);");
							}
						}
					});
				}
			}
		}
	}

}