package com.usemon.agent.jdk5;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import com.usemon.agent.instrumentation.RootInstrumentor;

/**
 * @author t535293
 * 
 * Class conforming to the JDK5 agent interface. This works as a proxy for the JDK1.4 implementation
 * of the 
 *
 */
public class JDK5Agent implements ClassFileTransformer {

	public static void premain(String agentArgs, Instrumentation inst) {
		inst.addTransformer(new JDK5Agent());
	}

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		return RootInstrumentor.transform(loader, className, protectionDomain, classfileBuffer);
	}

}
