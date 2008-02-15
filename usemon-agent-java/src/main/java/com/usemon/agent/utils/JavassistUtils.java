/*
 * Created on Mar 27, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.usemon.agent.utils;

import com.usemon.lib.javassist.CtClass;
import com.usemon.lib.javassist.CtConstructor;
import com.usemon.lib.javassist.CtMethod;
import com.usemon.lib.javassist.Modifier;
import com.usemon.lib.javassist.NotFoundException;

/**
 * @author t535293
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JavassistUtils {

    public static boolean implementsInterface(CtClass javaClass, String interfaceName) {
        try {
	        CtClass[] interfaces = javaClass.getInterfaces();
	        if(interfaces!=null) {
	            for(int n=0;n<interfaces.length;n++) {
	                CtClass iface = interfaces[n];
	                if(interfaceName.equals(iface.getName())) {
	                    return true;
	                }
	            }
	        }
        } catch(NotFoundException e) {
        }
        try {
            CtClass  superClass = javaClass.getSuperclass();
            if(superClass!=null) {
                return implementsInterface(superClass, interfaceName);
            }
        } catch (NotFoundException e1) {
        }
        return false;
    }
    
	public static boolean extendsClass(CtClass javaClass, String className) {
		try {
			CtClass superClass = javaClass.getSuperclass();
			if(superClass!=null) {
				if(className.equals(superClass.getName())) {
					return true;
				} else {
					return extendsClass(superClass, className);
				}
			}
		} catch (NotFoundException e) {
		}
		return false;
	}

    public static boolean isSingleton(CtClass javaClass) {
        CtConstructor[] constructors = javaClass.getDeclaredConstructors();
        boolean allConstructorsIsPrivate = true;
        for(int n=0;n<constructors.length;n++) {
            CtConstructor c = constructors[n];
            if(!Modifier.isPrivate(c.getModifiers())) {
                allConstructorsIsPrivate = false;
                break;
            }
        }
        if(allConstructorsIsPrivate) {
            CtMethod[] methods = javaClass.getDeclaredMethods();
            for(int n=0;n<methods.length;n++) {
                CtMethod m = methods[n];
                int modifiers = m.getModifiers();
                if(Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers)) {
                    try {
                        if(javaClass.getName().equals(m.getReturnType().getName())) {
                            return true;
                        }
                    } catch (NotFoundException e) {
                    }
                }
            }
        }
        return false;
    }

}