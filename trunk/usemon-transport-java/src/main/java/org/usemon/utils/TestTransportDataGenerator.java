/**
 * 
 */
package org.usemon.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.usemon.usageinfo.MethodInfo;
import org.usemon.usageinfo.PrincipalUsage;
import org.usemon.usageinfo.ResourceInfo;
import org.usemon.usageinfo.Usage;

/**
 * @author t547116
 * 
 */
public class TestTransportDataGenerator {

	private static final int COMPONENT_TYPE = 1;

	private static String packageName = "testforusage";
	private static String className = "toBeUsedClass";
	private static String methodName = "iAmUsed";
	private static String methodSignature = "String";
	
	private static String invokeeName = "uAreInvoked";
	private static String invokeeName2 = "uAreInvokedAgain";

	
	public static byte[] generateSerializedUsage() {
		ByteArrayOutputStream s_out = new ByteArrayOutputStream (32 * 1024);
		s_out.reset ();
		ObjectOutputStream oout;
		try {
			oout = new ObjectOutputStream (s_out);
			oout.writeObject (generateUsage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return s_out.toByteArray();
	}
	public static Usage generateUsage() {

		MethodInfo methodInformation = new MethodInfo(COMPONENT_TYPE,
				packageName, className, methodName, methodSignature);

		Usage usage = new Usage(methodInformation);
		
		HashMap principalMap = new HashMap();
		PrincipalUsage principalUsage1 = new PrincipalUsage("aatst1");
		principalUsage1.addSample(100, new IOException());
		principalUsage1.addSample(400, new NumberFormatException());
		principalUsage1.addSample(400, null);
		
		PrincipalUsage principalUsage2 = new PrincipalUsage("aatst2");
		principalUsage2.addSample(999, new IOException());
		principalUsage2.addSample(999, new NumberFormatException());
		principalUsage2.addSample(999, null);
		
		principalMap.put(principalUsage1.getPrincipal(), principalUsage1);
		principalMap.put(principalUsage2.getPrincipal(), principalUsage2);
		usage.setPrincipalUsage(principalMap);
		
		
		MethodInfo invokeeInfo = new MethodInfo(COMPONENT_TYPE,
				packageName, className, invokeeName, methodSignature);
		usage.addInvokee(invokeeInfo);

		MethodInfo invokeeInfo2 = new MethodInfo(COMPONENT_TYPE,
				packageName, className, invokeeName2, methodSignature);
		usage.addInvokee(invokeeInfo2);
		
		ResourceInfo invokeeInfo3 = new ResourceInfo(COMPONENT_TYPE, TestTransportDataGenerator.class.getName(), "Select all from everything" + (int)(Math.random()*10));
		usage.addInvokee(invokeeInfo3);
		return usage;
		
	}

	public static Usage generateResourceURIUsage() {


		ResourceInfo resourceInfo = new ResourceInfo(COMPONENT_TYPE, TestTransportDataGenerator.class.getName(), "Select I am the Usage ResourceURI" + (int)(Math.random()*10));
		Usage usage = new Usage(resourceInfo);
		
		
		HashMap principalMap = new HashMap();
		PrincipalUsage principalUsage1 = new PrincipalUsage("aatst1");
		principalUsage1.addSample(100, new IOException());
		principalUsage1.addSample(400, new NumberFormatException());
		principalUsage1.addSample(400, null);
		
		PrincipalUsage principalUsage2 = new PrincipalUsage("aatst2");
		principalUsage2.addSample(999, new IOException());
		principalUsage2.addSample(999, new NumberFormatException());
		principalUsage2.addSample(999, null);
		
		principalMap.put(principalUsage1.getPrincipal(), principalUsage1);
		principalMap.put(principalUsage2.getPrincipal(), principalUsage2);
		usage.setPrincipalUsage(principalMap);
		
		
		MethodInfo invokeeInfo = new MethodInfo(COMPONENT_TYPE,
				packageName, className, invokeeName, methodSignature);
		usage.addInvokee(invokeeInfo);

		MethodInfo invokeeInfo2 = new MethodInfo(COMPONENT_TYPE,
				packageName, className, invokeeName2, methodSignature);
		usage.addInvokee(invokeeInfo2);
		
		ResourceInfo invokeeInfo3 = new ResourceInfo(COMPONENT_TYPE, TestTransportDataGenerator.class.getName(), "Select all from everything" + (int)(Math.random()*10));
		usage.addInvokee(invokeeInfo3);
		return usage;
		
	}

}
