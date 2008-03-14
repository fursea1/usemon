package com.usemon.agent.instrumentation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.WeakHashMap;

import org.usemon.usageinfo.Info;
import org.usemon.utils.Log;

import com.usemon.agent.Config;
import com.usemon.agent.registry.Registry;
import com.usemon.agent.utils.JavassistUtils;
import com.usemon.agent.utils.ListUtils;
import com.usemon.lib.javassist.CannotCompileException;
import com.usemon.lib.javassist.ClassPool;
import com.usemon.lib.javassist.CtClass;
import com.usemon.lib.javassist.LoaderClassPath;
import com.usemon.lib.javassist.NotFoundException;
import com.usemon.lib.javassist.bytecode.ClassFile;
/**
 * Instruments classes at runtime. This class is instantiated by our custom class loader or the Java agent framework for JDK > 5.0
 * 
 * @author t535293
 *
 */
public class RootInstrumentor {
	private static boolean initialized = false;
	public static ClassPool classPool = new ClassPool(true);
	private static WeakHashMap classLoaders = new WeakHashMap();

	/**
	 * Entry point for instrumentation, invoked by the modified classloader for JVM <= 1.4x. For Java 1.5 it is invoked
	 * by the agent interface of the JVM.
	 * Thus this method is invoked for each class being loaded by the JVM. This does not imply that all classes are instrumented.
	 * 
	 * 
	 * @param loader the class loader attempting to load this class.
	 * @param className class name of class being loaded 
	 * @param protectionDomain security protection domain
	 * @param classfileBuffer
	 * @param offset
	 * @param length
	 * @return null if the class is not instrumented (modified)
	 */
	public static byte[] transform(ClassLoader loader, String className, ProtectionDomain protectionDomain, byte[] classfileBuffer, int offset, int length) {
		try {
			init();
			// Check to see if we already know this class loader or add it to the Javassist ClassPool search path if
			// it's new. This is important in J2EE- and Servlet containers that have class loader hierarchies
			if (!classLoaders.containsKey(loader)) {	// <<< TODO: this is a weak hash map, will this really work as intended?
				classPool.insertClassPath(new LoaderClassPath(loader));
				classLoaders.put(loader, null);
			}
			// Parses the byte array into a CtClass instance.
			CtClass ctc = loadClass(classfileBuffer, offset, length);
			// transforms the class if it is of any interest to us.
			byte[] bb = findTypeAndTransform(ctc);
			// removes the CtClass from the pool to release memory
			ctc.detach();
			return bb;
		} catch(Throwable t) {
			Log.warning("Exception during categorizeAndTransform of class "+className+". The class will be ignored.", t);
			return null;
		}
	}
	
	/** Used by the JDK  >= 5 */
	public static byte[] transform(ClassLoader loader, String className, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
		return transform(loader, className, protectionDomain, classfileBuffer, 0, classfileBuffer.length);
	}

	private static byte[] findTypeAndTransform(CtClass javaClass) throws CannotCompileException, IOException, NotFoundException {
		int componentType = findComponentType(javaClass);
		ClassFile cf = javaClass.getClassFile();
		if(componentType!=Info.COMPONENT_UNKNOWN && !cf.isInterface() && !cf.isAbstract()) {
			Log.info(javaClass.getName()+" is of type "+Info.TYPES[componentType]+" and will be instrumented");
			switch(componentType) {
			case Info.COMPONENT_SESSIONBEAN:
				return ComponentTransformer.transformSessionBean(javaClass);				
			case Info.COMPONENT_ENTITYBEAN:
				return ComponentTransformer.transformEntityBean(javaClass);				
			case Info.COMPONENT_MESSAGEDRIVENBEAN:
				return ComponentTransformer.transformMessageDrivenBean(javaClass);				
			case Info.COMPONENT_CUSTOM:
				return ComponentTransformer.transformCustom(javaClass);				
			case Info.COMPONENT_QUEUESENDER:
				return ComponentTransformer.transformQueueSender(javaClass);				
			case Info.COMPONENT_SERVLET:
				return ComponentTransformer.transformServlet(javaClass);				
			case Info.COMPONENT_SINGLETON:
				return ComponentTransformer.transformSingleton(javaClass);				
			case Info.COMPONENT_TOPICPUBLISHER:
				return ComponentTransformer.transformTopicPublisher(javaClass);				
			case Info.COMPONENT_SQLSTATEMENT:
				return ComponentTransformer.transformSQLStatement(javaClass);				
			case Info.COMPONENT_SQLCONNECTION:
				return ComponentTransformer.transformSQLConnection(javaClass);				
			default:
				return null;
			}
		}
		return null;
	}

	private static int findComponentType(CtClass javaClass) {
		if(!ListUtils.isBeginningInList(javaClass.getName(), Config.getIgnoredClasses())) {
			if(Config.isSessionBeanInScope() && JavassistUtils.implementsInterface(javaClass, "javax.ejb.SessionBean") && !isWebSphereProxy(javaClass)) {
				return Info.COMPONENT_SESSIONBEAN;
			} else if(Config.isEntityBeanInScope() && JavassistUtils.implementsInterface(javaClass, "javax.ejb.EntityBean") && !isWebSphereProxy(javaClass)) {
				return Info.COMPONENT_ENTITYBEAN;
			} else if(Config.isMessagedrivenBeanInScope() && JavassistUtils.implementsInterface(javaClass, "javax.ejb.MessageDrivenBean")) {
				return Info.COMPONENT_MESSAGEDRIVENBEAN;
			} else if(Config.isServletInScope() && JavassistUtils.extendsClass(javaClass, "javax.servlet.http.HttpServlet")) {
				return Info.COMPONENT_SERVLET;
			} else if(Config.isCustomInScope() && ListUtils.isBeginningInList(javaClass.getName(), Config.getCustomClasses())) {
				return Info.COMPONENT_CUSTOM;			
			} else if(Config.isSingletonInScope() && JavassistUtils.isSingleton(javaClass)) {
				return Info.COMPONENT_SINGLETON;
			} else if(Config.isMessageProducerInScope() && JavassistUtils.implementsInterface(javaClass, "javax.jms.QueueSender")) {
				return Info.COMPONENT_QUEUESENDER;
			} else if(Config.isMessageProducerInScope() && JavassistUtils.implementsInterface(javaClass, "javax.jms.TopicPublisher")) {
				return Info.COMPONENT_TOPICPUBLISHER;
			} else if(Config.isSQLStatementInScope() && JavassistUtils.implementsInterface(javaClass, "java.sql.Statement")) {
				return Info.COMPONENT_SQLSTATEMENT;
			}
		} else { // Ignore excluded classes for QueueSender, TopicPublisher and DataSource because these could be implemented by the container
			if(Config.isMessageProducerInScope() && JavassistUtils.implementsInterface(javaClass, "javax.jms.QueueSender")) {
				return Info.COMPONENT_QUEUESENDER;
			} else if(Config.isMessageProducerInScope() && JavassistUtils.implementsInterface(javaClass, "javax.jms.TopicPublisher")) {
				return Info.COMPONENT_TOPICPUBLISHER;
			} else if(Config.isSQLStatementInScope() && JavassistUtils.implementsInterface(javaClass, "java.sql.Statement")) {
				return Info.COMPONENT_SQLSTATEMENT;
			} else if(Config.isSQLStatementInScope() && JavassistUtils.implementsInterface(javaClass, "java.sql.Connection")) {
				return Info.COMPONENT_SQLCONNECTION;
			}
		}
		return Info.COMPONENT_UNKNOWN;
	}

	private static boolean isWebSphereProxy(CtClass javaClass) {
		// Ignore WebSphere proxy EJBs for local interfaces
		String name = javaClass.getSimpleName();
		if (name != null) {
			int i = name.indexOf('_');
			if (name.startsWith("EJS") && i != -1) {
				return true;
			}
		}
		return false;
	}

	private static CtClass loadClass(byte[] classfileBuffer, int offset, int length) throws IOException, RuntimeException {
		CtClass cc = classPool.makeClass(new ByteArrayInputStream(classfileBuffer, offset, length));
		if(cc.isFrozen()) {
			cc.defrost();
		}
/*		if(cc.getClassFile().getMajorVersion()<46) {
			cc.getClassFile().setMajorVersion(46);
			cc.getClassFile().setMinorVersion(0);
		}*/
		return cc;
	}

	private static void init() {
		if(!initialized) {
			initialized = true;
			Log.info("Usemon agent initialized");
		}
		// Bootstrap the publisher
		Registry.ensurePublisherAvailable();
	}
}
