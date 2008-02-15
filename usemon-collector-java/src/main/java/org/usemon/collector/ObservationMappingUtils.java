package org.usemon.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.HeapObservation;
import org.usemon.domain.InvocationObservation;
import org.usemon.domain.J2eeLocation;
import org.usemon.domain.MethodDetail;
import org.usemon.domain.MethodObservation;
import org.usemon.usageinfo.Info;
import org.usemon.usageinfo.MethodInfo;
import org.usemon.usageinfo.PrincipalUsage;
import org.usemon.usageinfo.ResourceInfo;
import org.usemon.usageinfo.Usage;

/**
 * @author t514257
 * 
 */
public class ObservationMappingUtils {
	private static final Logger log = LoggerFactory.getLogger(Collector.class);

	/**
	 * Extract a list of invocation objects from the data received from the agent.
	 * 
	 * @param usage -
	 *            Object containing invocation info
	 * @return List with Invocation objects
	 */
	@SuppressWarnings("unchecked")
	protected static List<InvocationObservation> buildInvocationsFromMethodUsage(Usage usage) {
		ArrayList<InvocationObservation> invocationList = new ArrayList<InvocationObservation>();
		InvocationObservation invocation = null;
		if (usage != null) {
			for (Info invokee : (Info[]) usage.getInvokees().values().toArray(new Info[usage.getInvokees().size()])) {
				invocation = new InvocationObservation();
				invocation.setSourceMethod(mapInformationToMethodDetail(invokee));
				invocation.setTargetMethod(mapInformationToMethodDetail(usage.getInfo()));
				invocation.setTimeStamp(usage.getExpirationTime());
				invocation.setInvocationCount(invokee.getCount());
				invocation.setObservationLocation(new J2eeLocation(usage.getLocation()));
				invocation.setPeriodLength(usage.getPeriodLengthInMillis());
				invocation.setObservationLocation(new J2eeLocation(usage.getLocation()));
				invocationList.add(invocation);
			}

		}
		return invocationList;
	}

	/**
	 * Extracts a list of methodObservations from the data received from the agent.
	 * 
	 * @param methodUsage  Object containing observation info
	 * @return List with MethodObservation objects.
	 */
	@SuppressWarnings("unchecked")
	protected static List<MethodObservation> buildMethodObservationFromMethodUsage(Usage usage) {

		ArrayList<MethodObservation> observationList = new ArrayList<MethodObservation>();
		MethodObservation methodObservation = null;

		/*
		 * map the method info for each principal create new instance of method observation, map values and add it to the list
		 */

		if (usage != null) {

			// Each usage object contains a map of principal usage objects, describing the method invocations per principal
			for (PrincipalUsage principal : (Collection<PrincipalUsage>)usage.getPrincipalUsage().values()) {
				methodObservation = new MethodObservation();
				methodObservation.setObservationLocation(new J2eeLocation(usage.getLocation()));

				methodObservation.setMethodDetail(mapInformationToMethodDetail(usage.getInfo()));
				// Sets the time stamp to the last execution time stamp 
				methodObservation.setTimeStamp(new Date(principal.getLastExecutionTime()));
				// Time span during which  the observation was made
				methodObservation.setPeriodLength(usage.getPeriodLengthInMillis());

				methodObservation.setPrincipal(principal.getPrincipal());
				// TODO: coordinate primitive types
				int avgRespTime = 0;
				if (principal.getExecutionCount() > 0) {
					avgRespTime = (int) (principal.getTotalExecutionTime() / principal.getExecutionCount());
				}
				methodObservation.setAvgResponseTime(avgRespTime);
				methodObservation.setMaxResponseTime((int) principal.getMaxExecutionTime());
				methodObservation.setInvocationCount(principal.getExecutionCount());
				methodObservation.setCheckedExceptions(principal.getCheckedCount());
				methodObservation.setUncheckedExceptions(principal.getUncheckedCount());
				methodObservation.setChannel("Unknown");

				observationList.add(methodObservation);
			}
		}

		return observationList;
	}

	/**
	 * Map method information from the transport domain the the usemon domain
	 * 
	 * @param info -
	 *            Transport domain object
	 * @return usemon MethodDetail domain object
	 */
	protected static MethodDetail mapInformationToMethodDetail(Info info) {
		if (info == null) {
			return null;
		}
		MethodDetail mappedDetail = null;
		if (info instanceof MethodInfo) {
			MethodInfo methodInformation = (MethodInfo) info;
			mappedDetail = new MethodDetail();
			mappedDetail.setClassName(methodInformation.getSimpleClassName());
			mappedDetail.setMethodName(methodInformation.getMethodName());
			mappedDetail.setPackageName(methodInformation.getPackageName());
			mappedDetail.setSignature(methodInformation.getSignature());
			mappedDetail.setInstanceId("unknown");

			// TODO: Map missing values
			// methodInformation.getReturnType()
		} else if (info instanceof ResourceInfo) {
			ResourceInfo resourceInfo = (ResourceInfo) info;
			mappedDetail = new MethodDetail();
			log.info("Received ResourceURI, map instanceId with value: " + resourceInfo.getResourceUri());
			mappedDetail.setInstanceId(resourceInfo.getResourceUri());
			
			// FIXME: PRJ: Kludge to split the className into package and simple name.
			// FIXME: PRJ: Should actually be done in the agent in the future, but this was the easiest way to fix it now.
			String packageName = null;
			String simpleClassName = null;
			String className = resourceInfo.getClassName();
			int i = className.lastIndexOf('.');
			if(i!=-1) {
				packageName = className.substring(0, i);
				simpleClassName = className.substring(i+1);
			} else {
				simpleClassName = className;
			}
			mappedDetail.setPackageName(packageName);
			mappedDetail.setClassName(simpleClassName);
		}

		return mappedDetail;

	}

	/**
	 * Map Heap usage values to Heap observation values
	 * 
	 * @param heapUsage
	 * @return
	 */
	public static HeapObservation buildHeapObservationFromUsage(Usage usage) {
		HeapObservation observation = null;
		if (usage != null) {
			observation = new HeapObservation();
			observation.setObservationLocation(new J2eeLocation(usage.getLocation()));
			observation.setTimeStamp(usage.getExpirationTime());
			observation.setTotal(usage.getTotalMemory());
			observation.setFree(usage.getFreeMemory());
			observation.setMaxMem(usage.getMaxMemory());

		}
		return observation;
	}

	/**
	 * Generate a MethodObservationObject based upon the JSON message
	 * 
	 * @param jsonMessage
	 *            JSON representation of a MethodObservation Object
	 * @return the generated object
	 */
	protected static MethodObservation buildMethodObservationFromJSONMessage(String jsonMessage) {
		JSONObject jsonMethodObservation = JSONObject.fromObject(jsonMessage);
		MethodObservation methodObservation = (MethodObservation) JSONObject.toBean(jsonMethodObservation, MethodObservation.class);
		return methodObservation;
	}

	/**
	 * Generate a InvocatinObservation object based upon the JSON message
	 * @param jsonMessage - JSON representation of a InvocationIbservation object
	 * @return the generated object
	 */
	protected static InvocationObservation buildInvocationObservationFromJSONMessage(String jsonMessage) {

		JSONObject jsonInvocationObservation = JSONObject.fromObject(jsonMessage);
		InvocationObservation invocation = (InvocationObservation) JSONObject.toBean(jsonInvocationObservation, InvocationObservation.class);
		return invocation;

	}
	/**
	 * Generate a HeapObservation object based upon the JSON message
	 * @param jsonMessage
	 * @return
	 */
	protected static HeapObservation buildHeapObservationFromJSONMessage(String jsonMessage) {
		JSONObject jsonHeapObservation = JSONObject.fromObject(jsonMessage);
		HeapObservation heap = (HeapObservation) JSONObject.toBean(jsonHeapObservation, HeapObservation.class);
		return heap;

	}
}
