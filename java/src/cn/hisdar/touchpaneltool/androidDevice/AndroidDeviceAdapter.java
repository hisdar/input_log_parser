package cn.hisdar.touchpaneltool.androidDevice;

import java.util.ArrayList;

import cn.hisdar.lib.log.HLog;

public class AndroidDeviceAdapter {
	private static final String GET_DEVICE_COMMAND = "adb devices -l";
	private static final String GET_DEVICE_COMMAND_NO_L = "adb devices";
	
	private static AndroidDeviceAdapter androidDeviceAdapter = null;
	private static SelectAndroidDeviceDialig selectAndroidDeviceDialig = null;
	
	private ArrayList<AndroidDevice> androidDevices = null;
	private ArrayList<AndroidDeviceChangeListener> androidDeviceChangeListeners = null;
	
	private AndroidDevice selectedAndroidDevice = null;
	
	private AndroidDeviceAdapter() {
		androidDevices = new ArrayList<AndroidDevice>();
		androidDeviceChangeListeners = new ArrayList<AndroidDeviceChangeListener>();
		selectAndroidDeviceDialig = new SelectAndroidDeviceDialig();
	}
	
	public static AndroidDeviceAdapter getInstance() {
		if (androidDeviceAdapter == null) {
			synchronized (AndroidDeviceAdapter.class) {
				if (androidDeviceAdapter == null) {
					androidDeviceAdapter = new AndroidDeviceAdapter();
				}
			}
		}
		
		return androidDeviceAdapter;
	}
	
	public void selectAndroidDevice() {
		int retValue = selectAndroidDeviceDialig.showSelectAndroidDeviceDialog();
		if (retValue != SelectAndroidDeviceDialig.APPROVE_OPTION) {
			return;
		}
		
		AndroidDevice newSelectedAndroidDevice = selectAndroidDeviceDialig.getSelectedAndroidDevice();
		if (selectedAndroidDevice == null || !newSelectedAndroidDevice.equals(selectedAndroidDevice)) {
			HLog.il("notify android change");
			selectedAndroidDevice = newSelectedAndroidDevice;
			notifyAndroidDeviceChangeEvent(selectedAndroidDevice);
		}
	}
	
	public AndroidDevice getAndroidDevice(int index) {
		AndroidDevice[] androidDevices = getAndroidDevices();
		if (androidDevices == null) {
			return null;
		}
		
		if (index >= androidDevices.length) {
			return null;
		}
		
		return androidDevices[index];
	}
	
	public int getAndroidDeviceCount() {
		AndroidDevice[] androidDevices = getAndroidDevices();
		if (androidDevices != null) {
			return androidDevices.length;
		} else {
			return 0;
		}
	}
	
	public AndroidDevice[] getAndroidDevices() {
		AndroidDevice[] androidDevices = getAndroidDevices_();
		
		if (androidDevices == null || androidDevices.length <= 0) {
			androidDevices = getAndroidDevicesNotUseL();
		}
		
		if (androidDevices == null || androidDevices.length <= 0) {
			CommandLine.execCommand("adb kill-server");
			androidDevices = getAndroidDevices_();
		}
		
		if (androidDevices == null || androidDevices.length <= 0) {
			androidDevices = getAndroidDevicesNotUseL();
		}
		
		return androidDevices;
	}
	
	public AndroidDevice[] getAndroidDevicesNotUseL() {
		StringBuffer devicesData = CommandLine.execCommandAndGetResult(GET_DEVICE_COMMAND);
		String[] deviceStringList = devicesData.toString().split("\n");
		if (deviceStringList == null) {
			return null;
		}
		
		// 清除之前的设备
		int deviceCount = androidDevices.size();
		for (int i = 0; i < deviceCount; i++) {
			androidDevices.remove(0);
		}
		
		for (int i = 0; i < deviceStringList.length; i++) {
			AndroidDevice androidDevice = getAndroidDeviceFromDeviceDataNoL(deviceStringList[i]);
			if (androidDevice != null) {
				androidDevices.add(androidDevice);
			}
		}
		
		if (androidDevices.size() == 0) {
			return null;
		}
		
		AndroidDevice[] androidDeviceArray = new AndroidDevice[androidDevices.size()];
		for (int i = 0; i < androidDeviceArray.length; i++) {
			androidDeviceArray[i] = androidDevices.get(i);
		}
		
		return androidDeviceArray;
	}
	
	public AndroidDevice[] getAndroidDevices_() {
		
		StringBuffer devicesData = CommandLine.execCommandAndGetResult(GET_DEVICE_COMMAND);
		String[] deviceStringList = devicesData.toString().split("\n");
		if (deviceStringList == null) {
			return null;
		}
		
		// 清除之前的设备
		int deviceCount = androidDevices.size();
		for (int i = 0; i < deviceCount; i++) {
			androidDevices.remove(0);
		}
		
		for (int i = 0; i < deviceStringList.length; i++) {
			AndroidDevice androidDevice = getAndroidDeviceFromDeviceData(deviceStringList[i]);
			if (androidDevice != null) {
				androidDevices.add(androidDevice);
			}
		}
		
		if (androidDevices.size() == 0) {
			return null;
		}
		
		AndroidDevice[] androidDeviceArray = new AndroidDevice[androidDevices.size()];
		for (int i = 0; i < androidDeviceArray.length; i++) {
			androidDeviceArray[i] = androidDevices.get(i);
		}
		
		return androidDeviceArray;
	}
	
	private AndroidDevice getAndroidDeviceFromDeviceData(String deviceData) {
		if (deviceData == null) {
			return null;
		}
		
		String formatedDeviceData = deviceData.trim();
		if (formatedDeviceData == null || formatedDeviceData.length() <= 0) {
			return null;
		}
		
		int startIndex = 0;
		int endIndex = formatedDeviceData.indexOf("device");
		if (endIndex < 0) {
			return null;
		}
		
		String deviceId = formatedDeviceData.substring(startIndex, endIndex).trim();
		
		startIndex = formatedDeviceData.indexOf(":", endIndex) + 1;
		if (startIndex < 0) {
			return null;
		}
		
		endIndex = formatedDeviceData.indexOf("model");
		if (endIndex < 0) {
			return null;
		}
		
		String deviceName = formatedDeviceData.substring(startIndex, endIndex).trim();
		
		AndroidDevice androidDevice = new AndroidDevice(deviceName, deviceId);

		return androidDevice;
	}
	
	private AndroidDevice getAndroidDeviceFromDeviceDataNoL(String deviceData) {
		if (deviceData == null) {
			return null;
		}
		
		String formatedDeviceData = deviceData.trim();
		if (formatedDeviceData == null || formatedDeviceData.length() <= 0) {
			return null;
		}
		
		int startIndex = 0;
		int endIndex = formatedDeviceData.indexOf("device");
		if (endIndex < 0) {
			return null;
		}
		
		String deviceId = formatedDeviceData.substring(startIndex, endIndex).trim();
		String deviceName = formatedDeviceData.substring(endIndex).trim();
		
		AndroidDevice androidDevice = new AndroidDevice(deviceName, deviceId);

		return androidDevice;
	}
	
	private void notifyAndroidDeviceChangeEvent(AndroidDevice androidDevice) {
		if (androidDevice == null) {
			HLog.el("notifyAndroidDeviceChangeEvent: android deivce is null");
			return;
		}
		
		for (int i = 0; i < androidDeviceChangeListeners.size(); i++) {
			HLog.il("notify android change,id=" + i);
			androidDeviceChangeListeners.get(i).androudDeviceChangeEvent(androidDevice);
		}
	}
	
	public void addAndroidDeviceChangeListener(AndroidDeviceChangeListener listener) {
		for (int i = 0; i < androidDeviceChangeListeners.size(); i++) {
			if (androidDeviceChangeListeners.get(i) == listener) {
				return;
			}
		}
		
		androidDeviceChangeListeners.add(listener);
		if (selectedAndroidDevice != null) {
			listener.androudDeviceChangeEvent(selectedAndroidDevice);
		}
	}
	
	public void removeAndroidDeviceChangeListener(AndroidDeviceChangeListener listener) {
		int listenerCount = androidDeviceChangeListeners.size();
		for (int i = listenerCount; i >= 0; i--) {
			if (androidDeviceChangeListeners.get(i) == listener) {
				androidDeviceChangeListeners.remove(i);
			}
		}
	}
}
