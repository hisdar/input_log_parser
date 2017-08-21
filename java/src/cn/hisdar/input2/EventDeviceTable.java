package cn.hisdar.input2;

import java.util.HashMap;

import cn.hisdar.MultiTouchEventParse.KeyEvent.KeyEvent;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPointA;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPointB;

public class EventDeviceTable {
	
	public HashMap<String, String> eventDeviceTable;
	
	// À´×Ôcn.hisdar.EventParse.multiTouchEvent.MultiTouchPoint
	public EventDeviceTable() {
		eventDeviceTable = new HashMap<String, String>();
		
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_TOUCH_MAJOR, "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_TOUCH_MINOR, "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_WIDTH_MAJOR, "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_WIDTH_MINOR, "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_ORIENTATION, "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_POSITION_X , "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_POSITION_Y , "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_TOOL_TYPE  , "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_BLOB_ID    , "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_TRACKING_ID, "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_PRESSURE   , "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.ABS_MT_DISTANCE   , "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPoint.BTN_TOUCH   , "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPointA.SYN_MT_REPORT    , "/dev/input/event0");
		eventDeviceTable.put(MultiTouchPointB.ABS_MT_SLOT      , "/dev/input/event0");
		eventDeviceTable.put(KeyEvent.KEY_POWER                , "/dev/input/event1");
		eventDeviceTable.put(KeyEvent.ABS_DISTANCE             , "/dev/input/event2");
	}
	
	public String getDeviceName(String eventName) {
		return eventDeviceTable.get(eventName);
	}
}
