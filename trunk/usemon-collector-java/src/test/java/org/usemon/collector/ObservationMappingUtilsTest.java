package org.usemon.collector;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.InvocationObservation;
import org.usemon.domain.MethodDetail;
import org.usemon.domain.MethodObservation;
import org.usemon.service.dao.TestDataGenerator;
import org.usemon.usageinfo.MethodInfo;
import org.usemon.usageinfo.Usage;
import org.usemon.utils.TestTransportDataGenerator;

public class ObservationMappingUtilsTest {

	Usage usage = null;
	JSONObject json;
	String jsonUsageObjectString = null;
	private static final Logger log = LoggerFactory.getLogger(Collector.class);
	private static final int performanceTestAmount = 1000;
	
	@Before
	public void setUp() throws Exception {
		usage = TestTransportDataGenerator.generateUsage();
		json = JSONObject.fromObject( usage );
		jsonUsageObjectString = json.toString();
	}

	@Test
	public void testBuildInvocationsFromMethodUsage() {
		List<InvocationObservation> invocationList = ObservationMappingUtils
				.buildInvocationsFromMethodUsage(usage);
		assertTrue(usage.getPeriodLengthInMillis()> 0);
		assertNotNull(invocationList);
		assertTrue(invocationList.size() == 3);

	}

	@Test
	public void testBuildMethodObservationsFromMethodUsage() {
		List<MethodObservation> observations = ObservationMappingUtils
				.buildMethodObservationFromMethodUsage(usage);
		assertNotNull(observations);
		assertTrue(observations.size() == 2);
		
		MethodObservation observation = observations.get(0);
		assertTrue(observation.getMethodName().equals(((MethodInfo)usage.getInfo()).getMethodName()));
		assertNotNull(observation.getPrincipal());
	}

	@Test
	public void testMapTransportMethodToDomainMethod() {
		MethodDetail domainInfo;
		MethodInfo transportInformation;
		transportInformation = (MethodInfo)usage.getInfo();
		domainInfo = ObservationMappingUtils.mapInformationToMethodDetail(transportInformation);
		assertTrue(transportInformation.getMethodName().equals(domainInfo.getMethodName()));
	}
	
	@Test
	public void testSerializeDeSerializeUsage() {
		ByteArrayOutputStream s_out = new ByteArrayOutputStream (32 * 1024);
		s_out.reset ();
		try {
			Usage[] usageSourceArray = new Usage[performanceTestAmount];
			for (int i=0;i<performanceTestAmount;i++) {
				usageSourceArray[i] = TestTransportDataGenerator.generateUsage();
			}
			ObjectOutputStream oout = new ObjectOutputStream (s_out);
			Usage[] usageArray =  new Usage[performanceTestAmount];
			long start = System.currentTimeMillis();		
			for (int i=0;i<performanceTestAmount;i++) {
				oout.writeObject (usageSourceArray[i]);
//				oout.writeObject (usage);
				oout.flush();
				 ObjectInputStream in = new ObjectInputStream (new ByteArrayInputStream (s_out.toByteArray ()));
				 try {
					usageArray[i] = (Usage)in.readObject();
//					ObservationMappingUtils
//					.buildMethodObservationFromMethodUsage(usageArray[i]);
//					ObservationMappingUtils
//					.buildInvocationsFromMethodUsage(usageArray[i]);					
				} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			long totalTime = System.currentTimeMillis() - start;
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			log.debug("Total ms used: " + totalTime);
			log.debug("average ms per object:" + totalTime / performanceTestAmount);
			s_out.reset();
			oout = new ObjectOutputStream (s_out);
			oout.writeObject (TestTransportDataGenerator.generateUsage());
			 ObjectInputStream in = new ObjectInputStream (new ByteArrayInputStream (s_out.toByteArray ()));
			 try {
				in.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			log.debug("Size of serialized message: " + s_out.size());

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	@Test
	public void testBuildInvocationObservationFromJSONMessage() {
		InvocationObservation originalInvocation = TestDataGenerator.generateInvocationObservation();
		JSONObject jsonObject = JSONObject.fromBean(originalInvocation);
		log.debug(jsonObject.toString(2));;
		
		InvocationObservation generated = ObservationMappingUtils.buildInvocationObservationFromJSONMessage(jsonObject.toString());
		
		assertTrue(originalInvocation.getSourceMethod().getMethodName().equals(generated.getSourceMethod().getMethodName()));
	}
	

}
