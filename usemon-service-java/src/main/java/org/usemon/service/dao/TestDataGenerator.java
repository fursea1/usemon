/**
 * 
 */
package org.usemon.service.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.usemon.domain.HeapObservation;
import org.usemon.domain.InvocationObservation;
import org.usemon.domain.MethodDetail;
import org.usemon.domain.MethodObservation;
import org.usemon.domain.Observation;
import org.usemon.domain.J2eeLocation;

/**
 * Generates test data instances suitable for testing etc.
 * <p>You might be tempted to move this class into the <code>src/main/test</code> hierarchy, but don't do it.
 * It needs to be packaged into the generated .jar files.
 * 
 * @author t547116
 * 
 */
public class TestDataGenerator {

	private static final int MILLIS_PER_SECOND = 1000;

	public static MethodObservation generateMethodObservation() {
		return generateMethodObservation(new Date());
	}
	/** Generates a sample method measurement object */
	public static MethodObservation generateMethodObservation(Date dt) {
		MethodObservation methodObservation = new MethodObservation(
				generateSamplePackageName(), generateSampleClassName(), generateSampleMethodSignature());
		methodObservation.setObservationLocation(generateObservationLocation());
		methodObservation.setInvocationCount((int) Math.round((Math.random() * 10)* Math.random()*100));
		methodObservation.setAvgResponseTime((int) (Math.random() * 100.0f));
		methodObservation.setCheckedExceptions((int) (Math.random() * 100.0f));
		methodObservation.setUncheckedExceptions((int) (Math.random() * 100.0f));
		methodObservation.setMaxResponseTime((int) (Math.random() * 200.0f));
		methodObservation.setTimeStamp(dt);
		methodObservation.setPeriodLength(60L* MILLIS_PER_SECOND); // one minute
		return methodObservation;
	}

	/** Generates a sample heap measurement object */
	public static HeapObservation generateHeapObservation() {
		HeapObservation heapObservation = new HeapObservation();
		heapObservation.setObservationLocation(generateObservationLocation());
		heapObservation.setFree((long)(100000L * Math.random()) );
		heapObservation.setMaxMem((long)(100000L * Math.random()) );
		heapObservation.setPeriodLength((long)Math.random() * MILLIS_PER_SECOND * 60); // one minute 
		heapObservation.setTimeStamp(new Date());
		heapObservation.setTotal((long)(Math.random() * 100000L));
		return heapObservation;
	}

	public static InvocationObservation generateInvocationObservation() {
			return generateInvocationObservation(new Date());
	}
	/** Generates a sample InvocationObservation object */
	public static InvocationObservation generateInvocationObservation(Date dt) {
		InvocationObservation invocationObservation = new InvocationObservation();
		invocationObservation.setSourceMethod(new MethodDetail("p1","Class1","methodxc()","sql: select bla bla"));
		invocationObservation.setTimeStamp(dt);
		invocationObservation.setPeriodLength((long)(Math.random() * MILLIS_PER_SECOND * 60));
		invocationObservation.setObservationLocation(generateObservationLocation());
		invocationObservation.setInvocationCount((int)(Math.random()*10));
		
		// Source
		MethodDetail sourceMethod = new MethodDetail(generateSamplePackageName(), generateSampleClassName(), generateSampleMethodSignature());
		sourceMethod.setInstanceId(generateSampleInstanceId());
		invocationObservation.setSourceMethod(sourceMethod);
		
		// Target
		invocationObservation.setTargetMethod(new MethodDetail(generateSamplePackageName(), generateSampleClassName(), generateSampleMethodSignature()));
		return invocationObservation;
	}
	
	public static List<MethodObservation> generateMethodObservationDataSet() {
		List<MethodObservation> observations = new ArrayList<MethodObservation>();
		Calendar  cal = Calendar.getInstance();
		int instancePerMinute = (int)(Math.random()*300);
		if (instancePerMinute==0)instancePerMinute=1;
		for (int minutes = 0;minutes< 30;minutes++) {
			cal.set(Calendar.MINUTE, minutes);
			for (int i=0;i<instancePerMinute;i++) {
				observations.add(generateMethodObservation(cal.getTime()));
			}
		}
		return observations;
	}
	
	public static List<InvocationObservation> generateInvocationObservationDataSet() {

		List<InvocationObservation> observations = new ArrayList<InvocationObservation>();
		Calendar  cal = Calendar.getInstance();
		int instancePerMinute = (int)(Math.random()*300);
		if (instancePerMinute==0)instancePerMinute=1;
		for (int minutes = 0;minutes< 30;minutes++) {
			cal.set(Calendar.MINUTE, minutes);
			for (int i=0;i<instancePerMinute;i++) {
				observations.add(generateInvocationObservation(cal.getTime()));
			}
		}
		return observations;
	}
	
	public static String generateSampleInstanceId() {
		return "Select averything from table number " + intBetween(1, 20);
	}
	
	/**
	 * @return
	 */
	public static String generateSampleMethodSignature() {
		return "testMethod" + intBetween(1,10) + "()";
	}

	/**
	 * @return
	 */
	public static String generateSampleClassName() {
		return "SampleClass" + intBetween(1, 20);
	}

	/**
	 * @return
	 */
	public static String generateSamplePackageName() {
		return "org.usemon.test" + intBetween(1,20);
	}

	/**
	 * @return
	 */
	public static J2eeLocation generateObservationLocation() {
		return new J2eeLocation("L" + intBetween(1, 256));
		
	}
	
	public static String generateJSONString(Observation observation) {
		JSONObject generatedObject = JSONObject.fromObject( observation );
		return generatedObject.toString(); 
		
	}
	
	public static int intBetween(int low, int high){
		int i = 0;
		int n = 0;
		while ( i < low || i > high) {
			double d =  (Math.random()*1000.0);
			i = (int) Math.round(d);
			if (n > 256)
				throw new IllegalStateException("Max number of randomizer attempts exceeded");
		}
		return i;
	}

}
