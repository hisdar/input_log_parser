package cn.hisdar.touchpaneltool.androidDevice;

import java.io.IOException;

import cn.hisdar.lib.log.HLog;

public class PhoneEvent {

	public static void reportKeyCode(int keyCode) {
		String cmdString = "adb shell input keyevent " + keyCode;
		
		try {
			Runtime.getRuntime().exec(cmdString);
		} catch (IOException e) {
			HLog.el(e);
		}
	}
	
	public static void reportTapEvent(int x, int y) {
		String commandString = "adb shell input tap " + 
				x + " " + y;
		try {
			Runtime.getRuntime().exec( commandString );
		} catch (IOException e) {
			HLog.el(e);
		}
	}
	
	public static void reportSwipe(int x1, int y1, int x2, int y2) {
		String commandString = "adb shell input swipe " + 
				x1 + " " + y1 + " " +
				x2 + " " + y2;
		try {
			Runtime.getRuntime().exec( commandString );
		} catch (IOException e) {
			HLog.el(e);
		}
	}
}
