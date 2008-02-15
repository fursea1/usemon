package org.usemon.integrationtest;

import java.util.List;

import org.junit.Test;
import org.usemon.domain.DeputoService;
import org.usemon.domain.InvocationObservation;
import org.usemon.domain.MethodObservation;
import org.usemon.domain.UsemonServiceLocator;
import org.usemon.service.dao.TestDataGenerator;

public class TestdataGeneratorTest {
	
	DeputoService deputoService;

	@org.junit.Before
	public void oneTimeSetup() {
		deputoService = UsemonServiceLocator.getDeputoService();
	}	
	
	@Test
	public void testAddMethodObservationTestData() {
		List<MethodObservation> observations = TestDataGenerator.generateMethodObservationDataSet();
		deputoService.addMethodObservations(observations);
	}
	
	@Test
	public void testAddInvocationObservationTestData() {
		List<InvocationObservation> observations = TestDataGenerator.generateInvocationObservationDataSet();
		deputoService.addInvocationObservations(observations);
	}
	
}
