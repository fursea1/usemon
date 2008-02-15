package org.usemon.multicast;

import org.usemon.usageinfo.Usage;

/**
 * Processes multicasted messages in various formats.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface MulticastProcessor {

	public void addUsageMessage(Usage usage);
	public void addJsonMessage(String message);
}
