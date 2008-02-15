/*
 * Created on Mar 8, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.usemon.installer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import com.usemon.lib.javassist.CannotCompileException;
import com.usemon.lib.javassist.ClassPool;
import com.usemon.lib.javassist.CtClass;
import com.usemon.lib.javassist.LoaderClassPath;
import com.usemon.lib.javassist.NotFoundException;

/**
 * @author t535293
 *
 * The installer pre-instruments the ClassLoader class from rt.jar (SUN JDK) or core.jar (IBM JDK) and creates a
 * bootstrap file that is to be included as part of the bootclasspath in the monitored JVM.
 * 
 */
public class Install {

	private HashMap instrumentedClasses;
    
    private static String[] classLoadersToInstrument = new String[] {
    	"java.lang.ClassLoader"
    };

    public static void main(String[] args) {
        System.out.println("UseMon Installer v1.1");
        System.out.println("All rights reserved, Copyright 2006, 2007");
        System.out.println("");
    	
    	String defaultRuntimeJar = null;
    	if(args.length==0) {
    		String javaHome = System.getProperty("java.home");
    		File rtFile = new File(javaHome+"/lib/rt.jar");
    		File coreFile = new File(javaHome+"/lib/core.jar");
    		if(rtFile.exists()) {
    			defaultRuntimeJar = rtFile.getAbsolutePath();
    		} else if(coreFile.exists()) {
    			defaultRuntimeJar = coreFile.getAbsolutePath();
    		}
    	}

    	String runtimeJar = null;
    	if(args.length>0) {
            runtimeJar = args[0];
            System.out.println("Instrumenting user provided runtime jar located at: "+runtimeJar);
    	} else {
    		runtimeJar = defaultRuntimeJar;
    		System.out.println("Instrumenting the runtime jar for current VM located at: "+runtimeJar);
    	}
        String bootstrapJar = "usemon-bootstrap.jar";
        if(args.length>1) {
            File f = new File(args[1]);
            if(f.isDirectory()) {
                bootstrapJar = f.getAbsolutePath()+"/usemon-bootstrap.jar";
            } else { 
                exit("Error: Second parameter (optional) must point to a directory where the boostrap file should be written.", 1);
            }
        }
        try {
            new Install().instrumentRuntime(runtimeJar, bootstrapJar);
            exit("Bootstrap jar successfully written to "+bootstrapJar+"\nPlease include this as the first element of the bootstrap classpath of your JVM.",0);
        } catch (CannotCompileException e) {
            exit("Error: "+e.getMessage(),1);
            e.printStackTrace();
        } catch (NotFoundException e) {
            exit("Error: "+e.getMessage(),1);
            e.printStackTrace();
        } catch (IOException e) {
            exit("Error: "+e.getMessage(),1);
            e.printStackTrace();
        }
    }
    
    private static void exit(String message, int status) {
        System.out.println("");
        System.out.println(message);
        System.exit(status);
    }

    public Install() {
        instrumentedClasses = new HashMap();
    }

    void instrumentRuntime(String inFilename, String outFilename) throws CannotCompileException, NotFoundException, IOException {
    	instrumentRuntime(new FileInputStream(inFilename), new FileOutputStream(outFilename));
    }

    void instrumentRuntime(InputStream in, OutputStream out) throws CannotCompileException, NotFoundException, IOException {
        JarEntry jarEntry = null;

        JarInputStream jis = new JarInputStream(in);
        while((jarEntry=jis.getNextJarEntry())!=null) {
            String name = jarEntry.getName();
            if(name!=null && name.endsWith(".class")) {
	            if(classNameInList(name, classLoadersToInstrument)) {
	            	// This is where the first step of the magic happens..
	            	// First we create a Javassist CtClass object from the current Java class
	                CtClass ctClass = loadClass(jis);
	                // Second we start instrumenting....
					instrumentedClasses.put(filenameToClass(name), ClassLoaderInstrumenter.instrument(ctClass));
	            }
            }
        }
        jis.close();
        
        JarOutputStream jos = new JarOutputStream(out);
        for(Iterator i=instrumentedClasses.keySet().iterator();i.hasNext();) {
            String className = (String) i.next();
            CtClass javaClass = (CtClass) instrumentedClasses.get(className);
            jarEntry = new JarEntry(classToFilename(className));
            jarEntry.setTime(System.currentTimeMillis());
            jos.putNextEntry(jarEntry);
            jos.write(javaClass.toBytecode());
        }
        
        jarEntry = new JarEntry("usemon-logging.properties");
        jarEntry.setTime(System.currentTimeMillis());
        jos.putNextEntry(jarEntry);
        Properties usemonLoggingProperties = new Properties();
        File logDir = new File("logs");
        logDir.mkdir();
        usemonLoggingProperties.setProperty("usemon.agent.log.directory", logDir.getAbsolutePath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        usemonLoggingProperties.store(baos, "Usemon Agent Logging Properties");
        jos.write(baos.toByteArray());
        jos.close();
    }

    boolean classNameInList(String classFilename, String[] classList) {
        for(int n=0;n<classList.length;n++) {
            if(classFilename.equals(classToFilename(classList[n]))) {
                return true;
            }
        }
        return false;
    }

    /** Provides a CtClass object based upon assumption that the supplied input stream
     * is currently positioned exactly on a Java class, typically loaded from a jar file.
     * @param is the input stream to load the Java class from
     * @return an instance of a Javassist CtClass object
     * 
     * @throws IOException
     */
    static final CtClass loadClass(InputStream is) throws IOException {
        // creates a new Java Assist class pool, appending the system 
    	// search path to the end of the search path
    	ClassPool cp = new ClassPool(true);
    	// modifies the class path
        cp.appendClassPath(new LoaderClassPath(Install.class.getClassLoader())); // Fix to include the junit classloader during unit tests.

        // creates a new CtClass from the supplied input stream
        return cp.makeClass(is);
    }
    
    static final String classToFilename(String className) {
        return className.replace('.','/')+".class";
    }

    static final String filenameToClass(String filename) {
        return (filename.substring(0, filename.lastIndexOf('.'))).replace('/','.');        
    }
}
