package cn.hisdar.touchpaneltool.ui.output;

import java.awt.Color;

import cn.hisdar.MultiTouchEventParse.EventTime;
import cn.hisdar.MultiTouchEventParse.EventType;

public class TimeTrackAction {

	private EventTime startTime;
	private EventTime endTime;
	
	private EventType eventType;
	private String eventValue;
	
	private Color eventColor;
	
	public TimeTrackAction() {
		
	}

	public EventTime getStartTime() {
		return startTime;
	}

	public void setStartTime(EventTime startTime) {
		this.startTime = startTime;
	}

	public EventTime getEndTime() {
		return endTime;
	}

	public void setEndTime(EventTime endTime) {
		this.endTime = endTime;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getEventValue() {
		return eventValue;
	}

	public void setEventValue(String eventValue) {
		this.eventValue = eventValue;
	}

	public Color getEventColor() {
		return eventColor;
	}

	public void setEventColor(Color eventColor) {
		this.eventColor = eventColor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result
				+ ((eventColor == null) ? 0 : eventColor.hashCode());
		result = prime * result
				+ ((eventType == null) ? 0 : eventType.hashCode());
		result = prime * result
				+ ((eventValue == null) ? 0 : eventValue.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeTrackAction other = (TimeTrackAction) obj;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (eventColor == null) {
			if (other.eventColor != null)
				return false;
		} else if (!eventColor.equals(other.eventColor))
			return false;
		if (eventType != other.eventType)
			return false;
		if (eventValue == null) {
			if (other.eventValue != null)
				return false;
		} else if (!eventValue.equals(other.eventValue))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimeTrackAction [startTime=" + startTime + ", endTime="
				+ endTime + ", eventType=" + eventType + ", eventValue="
				+ eventValue + ", eventColor=" + eventColor + "]";
	}
}
