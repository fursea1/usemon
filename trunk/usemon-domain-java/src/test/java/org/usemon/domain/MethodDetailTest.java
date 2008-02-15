/**
 * Created 1. nov.. 2007 15.49.16 by Steinar Overbeck Cook
 */
package org.usemon.domain;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class MethodDetailTest {

	/**
	 * Test method for {@link org.usemon.domain.MethodDetail#MethodDetail(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testMethodDetailStringStringString() {
	}

	/**
	 * Test method for {@link org.usemon.domain.MethodDetail#MethodDetail(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testMethodDetailWithInstance() {
		new MethodDetail("p1","Class1","methodxc()","sql:selectt");
	}

}
