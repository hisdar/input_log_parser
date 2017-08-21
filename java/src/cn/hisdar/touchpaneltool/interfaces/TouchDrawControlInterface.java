package cn.hisdar.touchpaneltool.interfaces;

import java.awt.Dimension;
import java.io.InputStream;

import cn.hisdar.MultiTouchEventParse.EventDeviceMap;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDevice;

public interface TouchDrawControlInterface {
	public boolean startPhoneInputDraw(AndroidDevice androidDevice);
	public void startFileInputDraw(String fileDirectPath, EventDeviceMap[] eventDeviceMaps);
	public void suspendDraw();
	public void resumeDraw();
	public void stopDraw();
	public boolean isSuspend();
	public boolean isRun();
	public void setResolution(Dimension resolution);
	public void nextStep();
}
