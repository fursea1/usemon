package com.usemon.installer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import com.usemon.lib.javassist.CannotCompileException;
import com.usemon.lib.javassist.NotFoundException;

public class InstallTest extends TestCase {

	public void testAtAll() {
		assertTrue(true);
	}
	
	// FIXME: Paul-Andre fix, yes?
	public void XtestInstrumentRuntimeJar() {
		Install i = new Install();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			i.instrumentRuntime(this.getClass().getResourceAsStream("/rt.zip"), baos);
			byte[] buf = baos.toByteArray();
			if(buf==null || buf.length==0) {
				fail("No output!");
			}
		} catch (CannotCompileException e) {
			e.printStackTrace();
			fail(e.getClass().getName()+": "+e.getMessage());
		} catch (NotFoundException e) {
			fail(e.getClass().getName()+": "+e.getMessage());
		} catch (IOException e) {
			fail(e.getClass().getName()+": "+e.getMessage());
		}
	}
	
}
