package org.usemon.live.data;

import java.util.Map;

public interface LiveListener {

	public void addInvocation(String source, String target, int invocationCount, Map sourceMetaData, Map targetMetaData);

}
