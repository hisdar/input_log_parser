package cn.hisdar.touchpaneltool.ui.history;

import cn.hisdar.MultiTouchEventParse.EventTime;

public class HistoryEventFilePointer {

	private EventTime eventTime = null;
	private long filePoint = 0;
	
	public HistoryEventFilePointer() {
		super();
	}

	public HistoryEventFilePointer(String systemTime, double bootUpTime, long filePoint) {
		super();
		EventTime eventTime = new EventTime(systemTime, bootUpTime);
		this.eventTime = eventTime;
		this.filePoint = filePoint;
	}
	
	public HistoryEventFilePointer(EventTime eventTime, long filePoint) {
		super();
		this.eventTime = eventTime;
		this.filePoint = filePoint;
	}
	
	public EventTime getEventTime() {
		return eventTime;
	}
	
	public void setEventTime(EventTime eventTime) {
		this.eventTime = eventTime;
	}
	
	public long getFilePoint() {
		return filePoint;
	}
	
	public void setFilePoint(long filePoint) {
		this.filePoint = filePoint;
	}

	@Override
	public String toString() {
		return "HistoryEventFilePointer [eventTime=" + eventTime + ", filePoint=" + filePoint + "]";
	}
	
}
