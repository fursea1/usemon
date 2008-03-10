package org.usemon.live.data;

import java.util.Map;

public interface InvocationListener {

	public void addInvocation(String source, String target, int invocationCount, Map sourceMetaData, Map targetMetaData);

}
