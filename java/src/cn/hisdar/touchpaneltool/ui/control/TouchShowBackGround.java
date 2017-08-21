package cn.hisdar.touchpaneltool.ui.control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDevice;

public class TouchShowBackGround {

	public enum TouchShowBackgroundType {
		BACKGROUND_COLOR,
		BACKGROUND_IMAGE,
		BACKGROUND_FROM_PHONE,
	}

	private static final String TOUCH_SHOW_BACKGROUND_CONFIG_FILE = "./Config/touchShowBackgroundConfig.xml";

	private static final String BACKGROUND_IMAGE_PATH_CONFIG_NAME = "backGroundImagePathFolder";
	
	private static TouchShowBackGround backGround = null;
	
	private ArrayList<TouchShowBackgroundChangeListener> backgroundChangeListeners = null;
	private BufferedImage backgroundImage = null;
	private ScreenShotThread screenShotThread = null;
	private HConfig touchShowBackgroundConfig = null;
	private TouchShowBackgroundType currentBackgroundType = TouchShowBackgroundType.BACKGROUND_COLOR;
	
	private TouchShowBackGround() {
		touchShowBackgroundConfig = HConfig.getInstance(TOUCH_SHOW_BACKGROUND_CONFIG_FILE);
		backgroundChangeListeners = new ArrayList<TouchShowBackgroundChangeListener>();
	}
	
	public static TouchShowBackGround getInstance() {
		if (backGround == null) {
			synchronized (TouchShowBackGround.class) {
				if (backGround == null) {
					backGround = new TouchShowBackGround();
				}
			}
		}
		
		return backGround;
	}
	
	private void notifyBackgroundImageChangeEvent() {
		if (backgroundImage != null) {
			for (int i = 0; i < backgroundChangeListeners.size(); i++) {
				backgroundChangeListeners.get(i).touchShowBackgroundChangeEvent(backgroundImage);
			}
		}
	}
	
	private void notifyBackgroundImageTypeChangeEvent() {
		
		for (int i = 0; i < backgroundChangeListeners.size(); i++) {
			backgroundChangeListeners.get(i).touchShowBackgroundTypeChangeEvent(currentBackgroundType);
		}
	}
	
	public void addTouchShowBackgroundChangeListener(TouchShowBackgroundChangeListener listener) {
		
		for (int j = 0; j < backgroundChangeListeners.size(); j++) {
			if (backgroundChangeListeners.get(j) == listener) {
				return;
			}
		}
		
		backgroundChangeListeners.add(listener);
		listener.touchShowBackgroundChangeEvent(backgroundImage);
		listener.touchShowBackgroundTypeChangeEvent(currentBackgroundType);
	}
	
	public void removeTouchShowBackgroundChangeListener(TouchShowBackgroundChangeListener listener) {
		int listenerCount = backgroundChangeListeners.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			if (backgroundChangeListeners.get(i) == listener) {
				backgroundChangeListeners.remove(i);
			}
		}
	}
	
	public void setColorBackgroundImage(int width, int height, Color backColor) {
		disablePhoneScreenShow();
		
		currentBackgroundType = TouchShowBackgroundType.BACKGROUND_COLOR;
		backgroundImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = backgroundImage.getGraphics();
		graphics.setColor(backColor);
		graphics.fillRect(0, 0, width, height);
		notifyBackgroundImageChangeEvent();
		notifyBackgroundImageTypeChangeEvent();
	}
	
	public void setImageBackgroud() {
		// ѡ�񱳾�ͼƬ
		String selectePath = touchShowBackgroundConfig.getConfigValue(BACKGROUND_IMAGE_PATH_CONFIG_NAME);
		JFileChooser imageFileChooser = new JFileChooser(selectePath);
		imageFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int retValue = imageFileChooser.showOpenDialog(null);
		if (retValue != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		File selectedFile = imageFileChooser.getSelectedFile();
		touchShowBackgroundConfig.setConfigItem(new ConfigItem(BACKGROUND_IMAGE_PATH_CONFIG_NAME, selectedFile.getParent()));
		
		try {
			backgroundImage = ImageIO.read(selectedFile);
		} catch (IOException e) {
			HLog.el(e);
			return;
		}
		
		currentBackgroundType = TouchShowBackgroundType.BACKGROUND_IMAGE;
		disablePhoneScreenShow();
		notifyBackgroundImageChangeEvent();
		notifyBackgroundImageTypeChangeEvent();
	}
	
	public void setBackgroundImageFromPhone(AndroidDevice androidDevice) {
		disablePhoneScreenShow();
		
		currentBackgroundType = TouchShowBackgroundType.BACKGROUND_FROM_PHONE;
		notifyBackgroundImageTypeChangeEvent();
		enablePhoneScreenShow(androidDevice);
	}
	
	private class ScreenShotThread extends Thread {
		boolean runFlag = true;
		
		private AndroidDevice androidDevice = null;
		
		public ScreenShotThread(AndroidDevice androidDevice) {
			this.androidDevice = androidDevice; 
		}
		
		public void run () {
			while (runFlag) {
				
				if (androidDevice == null) {
					HLog.el("Device is null");
					return;
				}
				
		        BufferedImage phoneImage = androidDevice.getScreenShot();//screenShot.getScreenShot();
		        if (phoneImage != null) {
		        	backgroundImage = phoneImage;
		        	synchronized (ScreenShotThread.class) {
		        		if (runFlag) {
			        		notifyBackgroundImageChangeEvent();
						}
					}
				}
			}
		}
		
		public void stopScreenShot() {
			synchronized (ScreenShotThread.class) {
				runFlag = false;
			}
		}
	}
	
	private void enablePhoneScreenShow(AndroidDevice androidDevice) {
		disablePhoneScreenShow();
		screenShotThread = new ScreenShotThread(androidDevice);
		screenShotThread.start();
	}
	
	private void disablePhoneScreenShow() {
		if (screenShotThread != null) {
			screenShotThread.stopScreenShot();
			
			screenShotThread = null;
		}
	}
}
