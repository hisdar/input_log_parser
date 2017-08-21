package cn.hisdar.touchpaneltool.common;

import cn.hisdar.lib.log.HLog;


public class LogFileFactory {

	public static String getEventLogSystemTime(String eventLine) {
		if (eventLine == null) {
			return null;
		}
		
		int startIndex = eventLine.indexOf('[');
		int endIndex = eventLine.indexOf("]");
		
		if (startIndex < 0 || endIndex <= 0 || endIndex <= startIndex) {
			return null;
		}
		
		return eventLine.substring(startIndex + 1, endIndex).trim();
	}
	
	public static double getEventLogBootupTime(String eventLine) {
		if (eventLine == null) {
			return -1;
		}
		
		int startIndex = eventLine.indexOf("][");
		if (startIndex < 0) {
			return -1;
		}
		
		int endIndex = eventLine.indexOf("]", startIndex + 2);
		if (endIndex <= 0 || endIndex <= startIndex) {
			return -1;
		}
		
		try {
			return Double.parseDouble(eventLine.substring(startIndex + 2, endIndex).trim());			
		} catch (Exception e) {
			HLog.el("Fail to parse time:" + eventLine.substring(startIndex + 2, endIndex).trim());
			HLog.el(e);
			return -1;
		}
	}
}
