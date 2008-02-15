package com.usemon.agent.utils;

public class ListUtils {

    public static boolean inList(String str, String[] strList) {
        for(int n=0;n<strList.length;n++) {
            if(str.equals(strList[n])) {
                return true;
            }
        }
        return false;
    }
    
	public static boolean isBeginningInList(String str, String[] strList) {
		for (int n = 0; n < strList.length; n++) {
			if (str.startsWith(strList[n])) {
				return true;
			}
		}
		return false;
	}
	
}
