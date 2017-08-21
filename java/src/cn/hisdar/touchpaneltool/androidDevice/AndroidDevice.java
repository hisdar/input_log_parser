package cn.hisdar.touchpaneltool.androidDevice;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import cn.hisdar.lib.log.HLog;

public class AndroidDevice {
	private static final String TOUCH_PANEL_FLAG = "INPUT_PROP_DIRECT";
	
	private String deviceName = null;
	private String deviceID   = null;
	private Process touchEventProcess = null;
	
	public AndroidDevice() {
		
	}

	public AndroidDevice(String deviceName, String deviceID) {
		this.deviceID = deviceID;
		this.deviceName = deviceName;
	}
	
	public String getDeviceName() {
		return deviceName;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public BufferedImage getScreenShot() {
		String localScreenShotPath = "./buffer/screenShot.png";
		String screenShotCmd = "adb -s " + deviceID + " shell \"/system/bin/screencap -p /sdcard/screenshot.png\"";
		String pullScreenShotCmd = "adb -s " + deviceID + " pull /sdcard/screenshot.png " + localScreenShotPath;
		String deleteScreenShotCmd = "adb -s " + deviceID + " shell \"rm -rf /sdcard/screenshot.png\"";
		
		if (!CommandLine.execCommand(screenShotCmd)) {
			return null;
		}
		
		if (!CommandLine.execCommand(pullScreenShotCmd)) {
			return null;
		}
		
		CommandLine.execCommand(deleteScreenShotCmd);
		
		BufferedImage screenShotImage = null;
		try {
			screenShotImage = ImageIO.read(new File(localScreenShotPath));
		} catch (IOException e) {
			HLog.el(e);
			return null;
		}
		
		return screenShotImage;
	}
	
	public Dimension getScreenResolution() {
		BufferedImage screenShot = getScreenShot();
		if (screenShot == null) {
			return null;
		}
		
		return new Dimension(screenShot.getWidth(), screenShot.getHeight());
	}
	
	public InputStream getPhoneEventInputStream() {
		
		String cmdString = "adb -s " + deviceID + " shell \"getevent -lrt\"";
		HLog.il("AndroidDevice.getPhoneEventInputStream(): cmd is:" + cmdString);
		if (touchEventProcess != null && touchEventProcess.isAlive()) {
			try {
				touchEventProcess.getInputStream().close();
			} catch (IOException e) {
				HLog.el(e);
			}
		}
		
		try {
			touchEventProcess = Runtime.getRuntime().exec(cmdString);
		} catch (IOException e) {
			HLog.el(e);
			return null;
		}
		
		return touchEventProcess.getInputStream();
	}
	
	public String getTouchInputEvent() {
		int inputDeviceCount = getPhoneInputDeviceCount();
		String eventName = "event";
		for (int i = 0; i < inputDeviceCount; i++) {
			if (checkTouchPanelDevice(eventName + i)) {
				return "/dev/input/" + eventName + i;
			}
		}
		
		return null;
	}
	
	/*************************************************************
	 * 获取注册到手机中的输入设备的数量
	 * @return 
	 * ***********************************************************/
	private int getPhoneInputDeviceCount() {
		int inputDeviceCount = 0;
		
		String cmdString = "adb -s " + deviceID + " shell \"ls /dev/input/\"";
		StringBuffer inputDeviceInfo = CommandLine.execCommandAndGetResult(cmdString);
		String[] inputDeviceList = inputDeviceInfo.toString().split("\n");
		if (inputDeviceList == null) {
			return 0;
		}
		
		for (int i = 0; i < inputDeviceList.length; i++) {
			if (inputDeviceList[i].trim().length() > 0) {
				inputDeviceCount += 1;
			}
		}
		
		return inputDeviceCount;
	}
	
	/************************************************************
	 * 输入/dev/input/eventX, 检测/dev/input/eventX是不是触摸屏
	 * @param eventX
	 * @return
	 ************************************************************/
	private boolean checkTouchPanelDevice(String eventX) {
		String cmdString = "adb -s " + deviceID + " shell getevent -i /dev/input/" + eventX;
		
		HLog.il("AndroidDevice.checkTouchPanelDevice(): Check touch event, cmd:" + cmdString);
		StringBuffer deviceInfo = CommandLine.execCommandAndGetResult(cmdString);
		if (deviceInfo.indexOf(TOUCH_PANEL_FLAG) >= 0) {
			return true;
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deviceID == null) ? 0 : deviceID.hashCode());
		result = prime * result
				+ ((deviceName == null) ? 0 : deviceName.hashCode());
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
		AndroidDevice other = (AndroidDevice) obj;
		if (deviceID == null) {
			if (other.deviceID != null)
				return false;
		} else if (!deviceID.equals(other.deviceID))
			return false;
		if (deviceName == null) {
			if (other.deviceName != null)
				return false;
		} else if (!deviceName.equals(other.deviceName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AndroidDevice [deviceName=" + deviceName + ", deviceID="
				+ deviceID + "]";
	}
	
	public void sendEvent(String[] commands) {
		String[] adbCommands = new String[commands.length];
		for (int i = 0; i < adbCommands.length; i++) {
			adbCommands[i] = "adb -s " + deviceID + " shell \"" + commands[i] + "\"";
		}
		
		for (int i = 0; i < adbCommands.length; i++) {
			HLog.il("AndroidDevice.sendEvent(): " + adbCommands[i]);
		}
		
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec(adbCommands);
		} catch (IOException e) {
			HLog.el(e);
		}
	}
}
