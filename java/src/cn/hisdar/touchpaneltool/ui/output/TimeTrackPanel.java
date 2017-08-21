package cn.hisdar.touchpaneltool.ui.output;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;

import cn.hisdar.MultiTouchEventParse.EventParser;
import cn.hisdar.MultiTouchEventParse.EventTime;
import cn.hisdar.MultiTouchEventParse.EventType;
import cn.hisdar.MultiTouchEventParse.KeyEvent.KeyEvent;
import cn.hisdar.MultiTouchEventParse.KeyEvent.PowerKeyEventListener;
import cn.hisdar.MultiTouchEventParse.KeyEvent.ResumeEventListener;
import cn.hisdar.MultiTouchEventParse.KeyEvent.SuspendEventListener;
import cn.hisdar.MultiTouchEventParse.KeyEvent.VolumeDownKeyEventListener;
import cn.hisdar.MultiTouchEventParse.KeyEvent.VolumeUpKeyEventListener;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchEventListener;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.lib.log.HLog;

public class TimeTrackPanel extends JPanel 
	implements MultiTouchEventListener, PowerKeyEventListener, 
				ResumeEventListener, SuspendEventListener, 
				VolumeDownKeyEventListener, VolumeUpKeyEventListener {

	public static final int DEFAULT_TIME_TRACK_ACTION_SIZE = 100;
	
	public static final Color DEFAULT_VOLUME_UP_COLOR = new Color(0xCD69C9);
	public static final Color DEFAULT_VOLUME_DOWM_COLOR = new Color(0x8B4789);
	public static final Color DEFAULT_MULTITOUCH_COLOR = new Color(0xFFE7BA);
	public static final Color DEFAULT_POWER_KEY_COLOR = new Color(0xFF0000);
	public static final Color DEFAULT_RESUME_COLOR = new Color(0x9400D3);
	public static final Color DEFAULT_SUSPEND_COLOR = new Color(0x1C1C1C);
	
	private TimeTrackAction multiTouchAction = null;
	private TimeTrackAction powerKeyAction = null;
	private TimeTrackAction resumeAction = null;
	private TimeTrackAction suspendAction = null;
	private TimeTrackAction volumeUpAction = null;
	private TimeTrackAction volumeDownAction = null;
	
	private Vector<TimeTrackAction> timeTrackActions = null;
	
	private EventParser eventParser = null;
	
	public TimeTrackPanel() {
		
		timeTrackActions = new Vector<TimeTrackAction>(DEFAULT_TIME_TRACK_ACTION_SIZE);
		eventParser = EventParser.getInstance();
		eventParser.addMultiTouchEventListener(this);
		eventParser.addPowerKeyEventListener(this);
		eventParser.addResumeEventListener(this);
		eventParser.addSuspendEventListener(this);
		eventParser.addVolumeDownEventListener(this);
		eventParser.addVolumeUpEventListener(this);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		BufferedImage timeTrackImage = TimeTrackImage.getTimeTrackImage(getWidth(), getHeight(), 0, timeTrackActions, 1000);
		if (timeTrackImage != null) {
			g.drawImage(timeTrackImage, 0, 0, null);
			HLog.il("[" + timeTrackImage.getWidth() + "," + timeTrackImage.getHeight() + "]");
		}
	}

	@Override
	public void MultiTouchDowmEvent(MultiTouchPoint point) {
		if (multiTouchAction == null) {
			EventTime eventTime = new EventTime(point.systemTime, point.bootUpTime);
			multiTouchAction = new TimeTrackAction();
			multiTouchAction.setStartTime(eventTime);
			multiTouchAction.setEventType(EventType.EVENT_MULTI_TOUCH);
			multiTouchAction.setEventValue(point + "");
			multiTouchAction.setEventColor(DEFAULT_MULTITOUCH_COLOR);
		}
	}

	@Override
	public void MultiTouchUpEvent(MultiTouchPoint point) {
		if (multiTouchAction != null) {
			EventTime eventTime = new EventTime(point.systemTime, point.bootUpTime);
			multiTouchAction.setEndTime(eventTime);
			if (timeTrackActions.size() >= DEFAULT_TIME_TRACK_ACTION_SIZE) {
				timeTrackActions.remove(0);
			}
			
			timeTrackActions.add(multiTouchAction);
			multiTouchAction = null;
			repaint();
		}
	}

	@Override
	public void MultiTouchActionEvent(MultiTouchPoint point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void MultiTouchUpEvent(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveSourceData(String sourceData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void volumeUpEvent(int eventValue, EventTime eventTime) {
		if (eventValue == KeyEvent.KEY_VOLUMEUP_UP) {
			if (volumeUpAction != null) {
				volumeUpAction.setEndTime(eventTime);
				if (timeTrackActions.size() >= DEFAULT_TIME_TRACK_ACTION_SIZE) {
					timeTrackActions.remove(0);
				}
				
				timeTrackActions.add(volumeUpAction);
				volumeUpAction = null;
				repaint();
			}
		} else if (eventValue == KeyEvent.KEY_VOLUMEUP_DOWN) {
			if (volumeUpAction == null) {
				volumeUpAction = new TimeTrackAction();
				volumeUpAction.setStartTime(eventTime);
				volumeUpAction.setEventType(EventType.KEVENT_VOLUMEUP_KEY);
				volumeUpAction.setEventValue(eventValue + "");
				volumeUpAction.setEventColor(DEFAULT_VOLUME_UP_COLOR);
			}
		}
	}

	@Override
	public void volumeDownKeyEvent(int eventValue, EventTime eventTime) {
		if (eventValue == KeyEvent.KEY_VOLUMEDOWN_UP) {
			if (volumeDownAction != null) {
				volumeDownAction.setEndTime(eventTime);
				if (timeTrackActions.size() >= DEFAULT_TIME_TRACK_ACTION_SIZE) {
					timeTrackActions.remove(0);
				}
				
				timeTrackActions.add(volumeDownAction);
				volumeDownAction = null;
				repaint();
			}
		} else if (eventValue == KeyEvent.KEY_VOLUMEDOWN_DOWN) {
			if (volumeDownAction == null) {
				volumeDownAction = new TimeTrackAction();
				volumeDownAction.setStartTime(eventTime);
				volumeDownAction.setEventType(EventType.EVEENT_VOLUMEDOWN_KEY);
				volumeDownAction.setEventValue(eventValue + "");
				volumeDownAction.setEventColor(DEFAULT_VOLUME_DOWM_COLOR);
			}
		}
	}

	@Override
	public void suspendEvent(EventTime eventTime) {
		suspendAction = new TimeTrackAction();
		suspendAction.setStartTime(eventTime);
		suspendAction.setEventType(EventType.EVENT_SUSPEND);
		suspendAction.setEventValue("");
		suspendAction.setEventColor(DEFAULT_SUSPEND_COLOR);
		
		suspendAction.setEndTime(eventTime);
		if (timeTrackActions.size() >= DEFAULT_TIME_TRACK_ACTION_SIZE) {
			timeTrackActions.remove(0);
		}
		
		timeTrackActions.add(suspendAction);
		repaint();
	}

	@Override
	public void resumeEvent(EventTime eventTime) {
		resumeAction = new TimeTrackAction();
		resumeAction.setStartTime(eventTime);
		resumeAction.setEventType(EventType.EVENT_RESUME);
		resumeAction.setEventValue("");
		resumeAction.setEventColor(DEFAULT_RESUME_COLOR);
		
		resumeAction.setEndTime(eventTime);
		if (timeTrackActions.size() >= DEFAULT_TIME_TRACK_ACTION_SIZE) {
			timeTrackActions.remove(0);
		}
		
		timeTrackActions.add(resumeAction);
		repaint();
	}

	@Override
	public void powerKeyEvent(int eventValue, EventTime eventTime) {
		if (eventValue == KeyEvent.KEY_POWER_DOWN) {
			if (powerKeyAction != null) {
				powerKeyAction.setEndTime(eventTime);
				if (timeTrackActions.size() >= DEFAULT_TIME_TRACK_ACTION_SIZE) {
					timeTrackActions.remove(0);
				}
				
				timeTrackActions.add(powerKeyAction);
				powerKeyAction = null;
				repaint();
			}
		} else if (eventValue == KeyEvent.KEY_POWER_UP) {
			if (powerKeyAction == null) {
				powerKeyAction = new TimeTrackAction();
				powerKeyAction.setStartTime(eventTime);
				powerKeyAction.setEventType(EventType.EVENT_POWER_KEY);
				powerKeyAction.setEventValue(eventValue + "");
				powerKeyAction.setEventColor(DEFAULT_POWER_KEY_COLOR);
			}
		}
	}
}
