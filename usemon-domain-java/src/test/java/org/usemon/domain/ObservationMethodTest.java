/**
 * 
 */
package org.usemon.domain;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class ObservationMethodTest {

	public static final String METHOD_SIGNATURE = "(a,b,c)";

	/**
	 * Test method for {@link org.usemon.domain.MethodDetail#ObservationMethod(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testMethodDetailStringStringString() {
		String methodName = "testMethod";
		MethodDetail methodDetail = new MethodDetail("P1","C1",methodName +METHOD_SIGNATURE);
		assertEquals(methodName, methodDetail.getMethodName());
		assertEquals(METHOD_SIGNATURE, methodDetail.getSignature());
	}

}
