package com.usemon.olap;

public enum Period {
	
	MINUTE(1000*60),
	HOUR(1000*60*60),
	DAY(1000*60*60*24),
	WEEK(1000*60*60*24*7),
	MONTH(1000*60*60*24*30);
	
	private long length;

	private Period(long length) {
		this.length = length;
	}
	
	/**
	 * @return the length of the Period in milliseconds
	 */
	public long getLength() {
		return length;
	}

}
