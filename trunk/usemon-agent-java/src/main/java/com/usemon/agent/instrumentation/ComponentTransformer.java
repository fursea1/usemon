package com.usemon.agent.instrumentation;

import java.io.IOException;

import org.usemon.usageinfo.Info;
import org.usemon.utils.Log;

import com.usemon.agent.Config;
import com.usemon.agent.Constants;
import com.usemon.agent.registry.Registry;
import com.usemon.agent.utils.ListUtils;
import com.usemon.lib.javassist.CannotCompileException;
import com.usemon.lib.javassist.CtClass;
import com.usemon.lib.javassist.CtField;
import com.usemon.lib.javassist.CtMethod;
import com.usemon.lib.javassist.Modifier;
import com.usemon.lib.javassist.NotFoundException;
import com.usemon.lib.javassist.bytecode.MethodInfo;

public class ComponentTransformer {

	/**
	 * javax.ejb.SessionBean
	 * 
	 * Strategy
	 * ¤ Locate the name of the field containing the javax.ejb.SessionContext
	 * ¤ Find all methods that is not private, not native and not abstract
	 * ¤¤ Skip all ignored methods
	 * ¤¤ Add push, invocation, exception handler and pop
	 */
	public static byte[] transformSessionBean(CtClass javaClass) throws NotFoundException, CannotCompileException, IOException {
		return transform(Info.COMPONENT_SESSIONBEAN, javaClass);
	}

	public static byte[] transformEntityBean(CtClass javaClass) throws NotFoundException, CannotCompileException, IOException {
		return transform(Info.COMPONENT_ENTITYBEAN, javaClass);
	}

	public static byte[] transformMessageDrivenBean(CtClass javaClass) throws NotFoundException, CannotCompileException, IOException {
		return transform(Info.COMPONENT_MESSAGEDRIVENBEAN, javaClass);
	}

	public static byte[] transformServlet(CtClass javaClass) throws NotFoundException, CannotCompileException, IOException {
		return transform(Info.COMPONENT_SERVLET, javaClass);
	}

	public static byte[] transformSingleton(CtClass javaClass) throws NotFoundException, CannotCompileException, IOException {
		return transform(Info.COMPONENT_SINGLETON, javaClass);
	}

	public static byte[] transformCustom(CtClass javaClass) throws NotFoundException, CannotCompileException, IOException {
		return transform(Info.COMPONENT_CUSTOM, javaClass);
	}
	
	/**
	 * javax.jms.QueueSender
	 * 
	 * Strategy
	 * ¤ Instruments all methods named "send"
	 * ¤ If the signature of the send method is "javax.jms.Queue, javax.jms.Message" or "javax.jms.Queue, javax.jms.Message, int"
	 *   it will do a simple push callback with the Queue.toString() as the name.
	 * ¤ If the signature of the send method is only "javax.jms.Message" or "javax.jms.Message, int" it will search for
	 *   a field in the class of the type javax.jms.Queue and if this is not null use it to do another toString() to
	 *   get the queue name. If this field is null it will name the queue "unknown".
	 * @throws CannotCompileException 
	 * @throws NotFoundException 
	 * @throws IOException 
	 * 
	 */
	public static byte[] transformQueueSender(CtClass javaClass) throws CannotCompileException, NotFoundException, IOException {
		// TODO Add statistics for message count and size
		CtMethod[] methods = javaClass.getMethods();
		for(int n=0;n<methods.length;n++) {
			CtMethod method = methods[n];
			MethodInfo methodInfo = method.getMethodInfo();
			// Skip all native and abstract methods
			if(methodInfo.isMethod() && !Modifier.isNative(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())) {
				if("send".equals(method.getName())) {
					String queueName = null;
					if("(Ljavax/jms/Queue;Ljavax/jms/Message;)V".equals(method.getSignature()) || "(Ljavax/jms/Queue;Ljavax/jms/Message;IIJ)V".equals(method.getSignature())) {
						method.addLocalVariable(Constants.FIELD_INVOKE_TIME, CtClass.longType );
						queueName = "$1.toString()";
					} else if("(Ljavax/jms/Message;)V".equals(method.getSignature()) || "(Ljavax/jms/Message;IIJ)V".equals(method.getSignature())) {
						method.addLocalVariable(Constants.FIELD_INVOKE_TIME, CtClass.longType );
						String queueFieldName = null;
						CtField[] fields = javaClass.getDeclaredFields();
						for(int t=0;t<fields.length;t++) {
							CtField field = fields[t];
							if ("javax.jms.Queue".equals(field.getType().getName())) {
								queueFieldName = field.getName();
								break;
							}
						}
						if(queueFieldName!=null) {
							queueName = queueFieldName+".toString()";
						} else {
							queueName = "\"queue://unknown.queue\"";
						}
					}

					if(queueName!=null) {
						StringBuffer before = new StringBuffer().append("{");
						StringBuffer after = new StringBuffer().append("{");
						before.append(Constants.CALLBACK_PUSH+"("+Info.COMPONENT_QUEUESENDER+", getClass().getName(), "+queueName+");");
						before.append(Constants.FIELD_INVOKE_TIME+"=java.lang.System.currentTimeMillis();");
						after.append(Constants.CALLBACK_INVOCATION);
						after.append("(");
						after.append(Info.COMPONENT_QUEUESENDER+", ");
						after.append("getClass().getName() ,");
						after.append(queueName+", ");
						after.append("(java.lang.System.currentTimeMillis()-"+Constants.FIELD_INVOKE_TIME+"), ");
						after.append("null, ");
						after.append("null);");
						after.append(Constants.CALLBACK_POP+"();");
						method.insertBefore(before.append("}").toString());
						method.insertAfter(after.append("}").toString());
					}
				}
			}
		}
		return javaClass.toBytecode();
	}
	
	public static byte[] transformTopicPublisher(CtClass javaClass) throws IOException, CannotCompileException, NotFoundException {
		CtMethod[] methods = javaClass.getMethods();
		for(int n=0;n<methods.length;n++) {
			CtMethod method = methods[n];
			MethodInfo methodInfo = method.getMethodInfo();
			// Skip all native and abstract methods
			if(methodInfo.isMethod() && !Modifier.isNative(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())) {
				String topicName = null;
				if("(Ljavax/jms/Topic;Ljavax/jms/Message;)V".equals(method.getSignature()) || "(Ljavax/jms/Topic;Ljavax/jms/Message;IIJ)V".equals(method.getSignature())) {
					method.addLocalVariable(Constants.FIELD_INVOKE_TIME, CtClass.longType );
					topicName = "$1.toString()";
				} else if("(Ljavax/jms/Topic;)V".equals(method.getSignature()) || "(Ljavax/jms/Topic;IIJ)V".equals(method.getSignature())) {
					method.addLocalVariable(Constants.FIELD_INVOKE_TIME, CtClass.longType );
					String topicFieldName = null;
					CtField[] fields = javaClass.getDeclaredFields();
					for(int t=0;t<fields.length;t++) {
						CtField field = fields[t];
						if ("javax.jms.Topic".equals(field.getType().getName())) {
							topicFieldName = field.getName();
							break;
						}
					}
					if(topicFieldName!=null) {
						topicName = topicFieldName+".toString()";
					} else {
						topicName = "\"topic://unknown.queue\"";
					}
				}
				
				if(topicName!=null) {
					StringBuffer before = new StringBuffer().append("{");
					StringBuffer after = new StringBuffer().append("{");
					before.append(Constants.CALLBACK_PUSH+"("+Info.COMPONENT_TOPICPUBLISHER+", getClass().getName(), "+topicName+");");
					before.append(Constants.FIELD_INVOKE_TIME+"=java.lang.System.currentTimeMillis();");
					after.append(Constants.CALLBACK_INVOCATION);
					after.append("(");
					after.append(Info.COMPONENT_TOPICPUBLISHER+", ");
					after.append("getClass().getName() ,");
					after.append(topicName+", ");
					after.append("(java.lang.System.currentTimeMillis()-"+Constants.FIELD_INVOKE_TIME+"), ");
					after.append("null, ");
					after.append("null);");
					after.append(Constants.CALLBACK_POP+"();");
					method.insertBefore(before.append("}").toString());
					method.insertAfter(after.append("}").toString());
				}
			}
		}
		return javaClass.toBytecode();
	}
	
	public static byte[] transformSQLConnection(CtClass javaClass) throws IOException, CannotCompileException, NotFoundException {
		String statementPush = Constants.CALLBACK_PUSH+"("+Info.COMPONENT_SQLSTATEMENT+", getClass().getName(), \"sql://\"+"+Constants.HELPER_SQLDEARG+"($1));";
		String pop = Constants.CALLBACK_POP+"();";
		CtMethod[] methods = javaClass.getMethods();
		for(int n=0;n<methods.length;n++) {
			CtMethod method = methods[n];
			MethodInfo methodInfo = method.getMethodInfo();
			if(methodInfo.isMethod() && !Modifier.isNative(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())) {
				StringBuffer before = new StringBuffer().append("{");
				StringBuffer after = new StringBuffer().append("{");
				
				if("prepareCall".equals(method.getName()) || "prepareStatement".equals(method.getName())) {
					CtClass[] paramTypes = method.getParameterTypes();
					if(paramTypes.length>0) {
//						System.out.println("== : "+paramTypes[0].getName());
						if("java.lang.String".equals(paramTypes[0].getName())) {
							method.addLocalVariable(Constants.FIELD_INVOKE_TIME, CtClass.longType );
							//before.append("java.lang.System.out.println(\"sql.prepare://\"+$1);");
							before.append(statementPush);
							before.append(Constants.FIELD_INVOKE_TIME+"=java.lang.System.currentTimeMillis();");
							after.append(Constants.CALLBACK_INVOCATION);
							after.append("(");
							after.append(Info.COMPONENT_SQLSTATEMENT+", ");
							after.append("getClass().getName() ,");
							after.append("\"sql.prepare://\"+"+Constants.HELPER_SQLDEARG+"($1), ");
							after.append("(java.lang.System.currentTimeMillis()-"+Constants.FIELD_INVOKE_TIME+"), ");
							after.append("null, ");
							after.append("null);");
							after.append(pop);
						}
					}
				}
				
				method.insertBefore(before.append("}").toString());
				method.insertAfter(after.append("}").toString());
			}
		}
		return javaClass.toBytecode();
	}

	public static byte[] transformSQLStatement(CtClass javaClass) throws CannotCompileException, IOException, NotFoundException {
//		String databasePush = Constants.CALLBACK_PUSH+"("+Info.COMPONENT_DATASOURCE+", getConnection().getClass().getName(), \"db://\"+getConnection().getURL());";		
		String statementPush = Constants.CALLBACK_PUSH+"("+Info.COMPONENT_SQLSTATEMENT+", getClass().getName(), \"sql://\"+"+Constants.HELPER_SQLDEARG+"($1));";
		String pop = Constants.CALLBACK_POP+"();";
		CtMethod[] methods = javaClass.getMethods();
		for(int n=0;n<methods.length;n++) {
			CtMethod method = methods[n];
			MethodInfo methodInfo = method.getMethodInfo();
			// Skip all native or abstract methods
//			System.out.println(javaClass.getName());
//			System.out.println(method.getName());
			if(methodInfo.isMethod() && !Modifier.isNative(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())) {
				StringBuffer before = new StringBuffer().append("{");
				StringBuffer after = new StringBuffer().append("{");

				if("execute".equals(method.getName()) || "executeQuery".equals(method.getName()) || "executeUpdate".equals(method.getName())) {
					CtClass[] paramTypes = method.getParameterTypes();
					if(paramTypes.length>0) {
//						System.out.println("-- : "+paramTypes[0].getName());
						if("java.lang.String".equals(paramTypes[0].getName())) {
							method.addLocalVariable(Constants.FIELD_INVOKE_TIME, CtClass.longType );
		//					before.append(databasePush).append(statementPush);
//							before.append("java.lang.System.out.println(\"sql://\"+$1);");
							before.append(statementPush);
							before.append(Constants.FIELD_INVOKE_TIME+"=java.lang.System.currentTimeMillis();");
							after.append(Constants.CALLBACK_INVOCATION);
							after.append("(");
							after.append(Info.COMPONENT_SQLSTATEMENT+", ");
							after.append("getClass().getName() ,");
							after.append("\"sql://\"+"+Constants.HELPER_SQLDEARG+"($1), ");
							after.append("(java.lang.System.currentTimeMillis()-"+Constants.FIELD_INVOKE_TIME+"), ");
		//					after.append("getConnection().getMetaData().getUserName(), ");
							after.append("null, ");
							after.append("null);");
		//					after.append(pop).append(pop);
							after.append(pop);
						}
					}
				} else if("addBatch".equals(method.getName())) {
					CtClass[] paramTypes = method.getParameterTypes();
					if(paramTypes.length>0) {
//						System.out.println("== : "+paramTypes[0].getName());
						if("java.lang.String".equals(paramTypes[0].getName())) {
							method.addLocalVariable(Constants.FIELD_INVOKE_TIME, CtClass.longType );
//							before.append("java.lang.System.out.println(\"sql.batch://\"+$1);");
							before.append(statementPush);
							before.append(Constants.FIELD_INVOKE_TIME+"=java.lang.System.currentTimeMillis();");
							after.append(Constants.CALLBACK_INVOCATION);
							after.append("(");
							after.append(Info.COMPONENT_SQLSTATEMENT+", ");
							after.append("getClass().getName() ,");
							after.append("\"sql.batch://\"+"+Constants.HELPER_SQLDEARG+"($1), ");
							after.append("(java.lang.System.currentTimeMillis()-"+Constants.FIELD_INVOKE_TIME+"), ");
							after.append("null, ");
							after.append("null);");
							after.append(pop);
						}
					}
				} else if("executeBatch".equals(method.getName())) {
					// TODO
				}
				
				try {
					method.insertBefore(before.append("}").toString());
					method.insertAfter(after.append("}").toString());
				} catch(CannotCompileException e) {
					System.err.println("BEFORE: "+before.toString());
					System.err.println("AFTER: "+after.toString());
					Log.warning(javaClass.getClassFile().getMajorVersion()+"."+javaClass.getClassFile().getMinorVersion());
					throw e;
				}
			}
		}
		return javaClass.toBytecode();
	}
	
	private static byte[] transform(int type, CtClass javaClass) throws NotFoundException, CannotCompileException, IOException {
		String sessCtxField = null;
		CtField[] fields = javaClass.getFields();
		for(int n=0;n<fields.length;n++) {
			CtField field = fields[n];
			String fieldTypeName = field.getType().getName();
			if ("javax.ejb.SessionContext".equals(fieldTypeName)) {
				sessCtxField = field.getName();
				break;
			} else if("javax.ejb.MessageDrivenContext".equals(fieldTypeName)) {
				sessCtxField = field.getName();
				break;
			} else if("javax.ejb.EntityContext".equals(fieldTypeName)) {
				sessCtxField = field.getName();
				break;
			}
		}

		CtMethod[] methods = javaClass.getDeclaredMethods();
		if(methods!=null) {
			for(int n=0;n<methods.length;n++) {
				CtMethod method = methods[n];
				MethodInfo methodInfo = method.getMethodInfo();
				// Skip all private, native or abstract methods
				if(methodInfo.isMethod() && !Modifier.isPrivate(method.getModifiers()) && !Modifier.isNative(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())) {
					// Skip all ignored method names
					if(!ListUtils.inList(method.getName(), Config.getIgnoredMethods())) {
						StringBuffer before = new StringBuffer().append("{");
						StringBuffer after = new StringBuffer().append("{");
						StringBuffer handler = new StringBuffer().append("{"); // Exception handler

						// Create a field for the invoke time and set it to currentTimeMillis
						method.addLocalVariable(Constants.FIELD_INVOKE_TIME, CtClass.longType );
						before.append(Constants.FIELD_INVOKE_TIME+"=java.lang.System.currentTimeMillis();");

						// Special case for Servlet do*** methods and MDBs onMessage method
						boolean extraPopNeeded = false;
						if(type==Info.COMPONENT_SERVLET) {
							if(method.getName().startsWith("do")) {
								before.append(Constants.CALLBACK_PUSH);
								before.append("(");
								before.append(Info.COMPONENT_SERVLET+", ");
								before.append(" $1.getClass().getName(), ");
								before.append("$1.getRequestURL()!=null?$1.getRequestURL().toString():\"http://unknown.url/\"");
								before.append(");");
								extraPopNeeded=true;
							}
						} else if(type==Info.COMPONENT_MESSAGEDRIVENBEAN) {
							if("onMessage".equals(method.getName())) {
								before.append(Constants.CALLBACK_PUSH);
								before.append("(");
								before.append(Info.COMPONENT_MESSAGEDRIVENBEAN+", ");
								before.append(" $1.getClass().getName(), ");
								before.append("$1.getJMSDestination()!=null?$1.getJMSDestination().toString():\"queue://unknown.queue\"");
								before.append(");");
								extraPopNeeded=true;
							}							
						}

						// Instrumentation common for most beans
						before.append(Constants.CALLBACK_PUSH);
						before.append("(");
						before.append(type+", ");
						before.append("\""+javaClass.getPackageName()+"\", ");
						before.append("\""+javaClass.getSimpleName()+"\", ");
						before.append("\""+method.getName()+"\", ");
						before.append("\""+method.getSignature()+"\"");
						before.append(");");

						after.append(Constants.CALLBACK_INVOCATION);
						after.append("(");
						after.append(type+", ");
						after.append("\""+javaClass.getPackageName()+"\", ");
						after.append("\""+javaClass.getSimpleName()+"\", ");
						after.append("\""+method.getName()+"\", ");
						after.append("\""+method.getSignature()+"\", ");
						after.append("\""+Modifier.toString(method.getModifiers())+"\", ");
						after.append("(java.lang.System.currentTimeMillis()-"+Constants.FIELD_INVOKE_TIME+"), ");
						if(sessCtxField!=null) {
							after.append(sessCtxField+"!=null?"+sessCtxField+".getCallerPrincipal().getName():null, ");
						} else {
							after.append("null, ");
						}
						after.append("null");
						after.append(");");
						after.append(Constants.CALLBACK_POP+"();");
						
						handler.append(Constants.CALLBACK_INVOCATION);
						handler.append("(");
						handler.append(type+", ");
						handler.append("\""+javaClass.getPackageName()+"\", ");
						handler.append("\""+javaClass.getSimpleName()+"\", ");
						handler.append("\""+method.getName()+"\", ");
						handler.append("\""+method.getSignature()+"\", ");
						handler.append("\""+Modifier.toString(method.getModifiers())+"\", ");
						handler.append("0L, ");
						// handler.append("(java.lang.System.currentTimeMillis()-"+Constants.FIELD_INVOKE_TIME+"), "); // FIELD_INVOKE_TIME is not visible in catch block :-/
						if(sessCtxField!=null) {
							handler.append(sessCtxField+"!=null?"+sessCtxField+".getCallerPrincipal().getName():null, ");
						} else {
							handler.append("null, ");
						}
						handler.append(Constants.FIELD_EXCEPTION);
						handler.append(");");
						handler.append(Constants.CALLBACK_POP+"();");
						handler.append("throw "+Constants.FIELD_EXCEPTION+";");

						if(extraPopNeeded) {
							after.append(Constants.CALLBACK_POP+"();");
						}

						try {
							method.insertBefore(before.append("}").toString());
							method.insertAfter(after.append("}").toString());
							method.addCatch(handler.append("}").toString(), RootInstrumentor.classPool.get("java.lang.Throwable"), Constants.FIELD_EXCEPTION);
							Registry.reportTransformation(type, javaClass.getName(), method.getName(), method.getSignature(), Modifier.toString(method.getModifiers()));
						} catch(ArrayIndexOutOfBoundsException e) {
							Log.warning("ArrayIndexOutOfBoundsException during inserting before and after code for "+javaClass.getName()+"."+method.getName()+". Can be ignored!", e);
							Log.warning(javaClass.getClassFile().getMajorVersion()+"."+javaClass.getClassFile().getMinorVersion());
						} catch(CannotCompileException e) {
							System.err.println("BEFORE: "+before.toString());
							System.err.println("AFTER: "+after.toString());
							Log.warning(javaClass.getClassFile().getMajorVersion()+"."+javaClass.getClassFile().getMinorVersion());
							throw e;
						}
					}
				}
			}
		}		
		return javaClass.toBytecode();
	}

}
