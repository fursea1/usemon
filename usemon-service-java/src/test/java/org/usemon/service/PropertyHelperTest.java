/**
 * 
 */
package org.usemon.service;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author t547116
 *
 */
public class PropertyHelperTest {

	/**
	 * Test method for {@link org.usemon.service.PropertyHelper#getJdbcDriverClassName()}.
	 */
	@Test
	public void testGetJdbcDriverClassName() {
		assertNotNull(PropertyHelper.getJdbcDriverClassName());
	}

	/**
	 * Test method for {@link org.usemon.service.PropertyHelper#getJdbcUrl()}.
	 */
	@Test
	public void testGetJdbcUrl() {
		assertNotNull(PropertyHelper.getJdbcUrl());
	}

	/**
	 * Test method for {@link org.usemon.service.PropertyHelper#getJdbcUserName()}.
	 */
	@Test
	public void testGetJdbcUserName() {
		assertNotNull(PropertyHelper.getJdbcUserName());
	}

	/**
	 * Test method for {@link org.usemon.service.PropertyHelper#getJdbcPassword()}.
	 */
	@Test
	public void testGetJdbcPassword() {
		assertNotNull(PropertyHelper.getJdbcPassword());
	}

}
