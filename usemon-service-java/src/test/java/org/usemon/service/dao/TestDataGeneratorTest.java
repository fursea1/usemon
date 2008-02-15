/**
 * 
 */
package org.usemon.service.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.usemon.domain.HeapObservation;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class TestDataGeneratorTest {

	/**
	 * Test method for {@link org.usemon.service.dao.TestDataGenerator#generateHeapObservation()}.
	 */
	@Test
	public void testGenerateHeapMeasurement() {
		HeapObservation heapObservation = TestDataGenerator.generateHeapObservation();
		assertTrue(heapObservation.getFree() > 0);
		assertTrue(heapObservation.getTotal() > 0);
		
	}

}
