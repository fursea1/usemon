/**
 * 
 */
package org.usemon.domain;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author t547116
 *
 */
public class ObservationTypeTest {

	@Test
	public void testFromStringToObservationType() {
		ObservationType observationType = ObservationType.valueOf(ObservationType.METHOD_OBSERVATION.toString());
	}
	
	@Test
	public void testInvalidType() {
		try {
			ObservationType.valueOf("unknown");
			fail("'unknown' is not a known observation type");
		} catch (Exception e) {
			// As expected
		}
		
	}
}
