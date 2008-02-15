/*
 * Created on Mar 4, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.usemon.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author t535293
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Log {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private static PrintStream logStream;
	private static File logDir;
	
	private static File getLogDirectory() {
		if(logDir==null) {
			Properties p = new Properties();
			try {
				InputStream in = ClassLoader.getSystemResourceAsStream("/usemon-logging.properties");
				p.load(in);
				logDir = new File(p.getProperty("usemon.agent.log.directory"));
			} catch (Throwable e) {
				if(!(e instanceof NullPointerException)) {
					e.printStackTrace();
				}
				logDir = new File("logs");
				if(!logDir.exists()) { 
					logDir.mkdir();
				}
			}
		}
		return logDir;
	}
	
	private synchronized static PrintStream getLogStream() {
		if(logStream==null) {
			try {
				File f = new File((getLogDirectory().getAbsolutePath()+"/usemon.log"));
				if(f.exists()) {
					logStream = new PrintStream(new FileOutputStream(f, true));
				} else {
					logStream = new PrintStream(new FileOutputStream(f));					
				}
				System.err.println("Usemon log will be written to "+f.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
				logStream = System.out;
				System.err.println("Usemon log will be written to System.out");
			}
		}
		return logStream;
	}
	
    public synchronized static void info(String message) {
    	printTimestamp(getLogStream());
    	getLogStream().println(" INFO "+(message));
    }

	public synchronized static void warning(String message) {
    	printTimestamp(getLogStream());
    	getLogStream().println(" WARN "+(message));
    }

    public synchronized static void warning(Throwable e) {
    	printTimestamp(getLogStream());
    	getLogStream().println(" WARN "+e.getClass().getName()+": "+e.getMessage());
        e.printStackTrace(getLogStream());
    }

    public synchronized static void warning(String message, Throwable t) {
    	printTimestamp(getLogStream());
    	getLogStream().println(" WARN "+(message)+" ("+t.getClass().getName()+": "+t.getMessage()+")");
        t.printStackTrace(getLogStream());
    }
    
    public synchronized static void error(String message) {
    	printTimestamp(getLogStream());
        getLogStream().println("ERROR "+(message));
    }

    public synchronized static void debug(String message) {
    	printTimestamp(getLogStream());
    	getLogStream().println("DEBUG "+(message));
    }

	private static void printTimestamp(PrintStream out) {
		out.print(sdf.format(new Date()));
		out.print(" ");
		out.print(Thread.currentThread().getName());
	}
    
}