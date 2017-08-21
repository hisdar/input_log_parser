package cn.hisdar.touchpaneltool.ui.replay2;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.setting.DrawSettingPanel;


public class TouchActionContainer {
	private static final int DEFAULT_COLLECTER_SIZE = 40;
	
	private MultiTouchAction2[] touchActions = null;
	private int queueHead = 0;
	private int queueTail = 0;
	private int queueItemCount = 0;
	private ArrayList<Image> imagesList = null;
	
	private HConfig settingConfig = null;

	public TouchActionContainer() {
		settingConfig = HConfig.getInstance(DrawSettingPanel.DRAW_CONFIG_FILE_PATH);
		instance(DEFAULT_COLLECTER_SIZE);
	}
	
	public TouchActionContainer(int size) {
		instance(size);
	}
	
	private void instance(int size) {
		touchActions = new MultiTouchAction2[size];
		for (int i = 0; i < touchActions.length; i++) {
			touchActions[i] = new MultiTouchAction2();
		}
		
		imagesList = new ArrayList<Image>(DEFAULT_COLLECTER_SIZE);
	}
	
	public void queue(MultiTouchAction2 touchAction) {
		if (queueItemCount == touchActions.length) {
			queueHead = (queueHead + 1) % touchActions.length;
			touchActions[queueTail].setMultiTouchAction2(touchAction);
			queueTail = (queueTail + 1) % touchActions.length;
		} else {
			touchActions[queueTail].setMultiTouchAction2(touchAction);
			queueTail = (queueTail + 1) % touchActions.length;
			queueItemCount += 1;
		}
	}
	
	public MultiTouchAction2 getAt(int index) {
		if (index > queueItemCount) {
			return null;
		}
		
		int indexInQueue = (queueHead + index) % touchActions.length;
		return touchActions[indexInQueue];
	}
	
	public void setSize(int size) {
		
		if (size == touchActions.length) {
			return;
		}
		
		MultiTouchAction2[] oldActions = touchActions;
		touchActions = new MultiTouchAction2[size];
		
		// 长度在增加
		if (size > touchActions.length) {
			
			for (int i = 0; i < queueItemCount; i++) {
				int oldQueueIndex = (queueHead + i) % oldActions.length;
				touchActions[i] = oldActions[oldQueueIndex];
			}
			
			queueHead = 0;
			queueTail = queueItemCount;
			
			return;
		}
		
		// 长度变短了
		if (size < touchActions.length) {
			int copySize = touchActions.length > queueItemCount ? queueItemCount : touchActions.length;
			for (int i = 0; i < copySize; i++) {
				int oldQueueIndex = (queueHead + i) % oldActions.length;
				touchActions[i] = oldActions[oldQueueIndex];
			}
			
			queueHead = 0;
			queueTail = copySize % touchActions.length;
			queueItemCount = copySize;
			return;
		}
	}
	
	public int getQueueItemCount() {
		return queueItemCount;
	}
	
	public void removeAll() {
		
		queueHead = 0;
		queueTail = 0;
		queueItemCount = 0;
		
	}
	
	public ArrayList<MultiTouchAction2> getMultiTouchAction2s() {
		ArrayList<MultiTouchAction2> touchAction2s = new ArrayList<MultiTouchAction2>();
		for (int i = 0; i < queueItemCount; i++) {
			touchAction2s.add(getAt(i));
		}

		return touchAction2s;
	}

	public Image[] getTouchImages(int width, int height) {
			
		ImageConfig config = getPreviewConfig();
		
		int imageCount = 0;
		//System.out.println("imagesList.size():" + imagesList.size());
		for (int i = 0; i < getQueueItemCount(); i++) {
			MultiTouchAction2 touchAction = getAt(i);
			if (touchAction == null) {
				continue;
			}
			
			imageCount += 1;
			if (i >= imagesList.size()) {
				imagesList.add(touchAction.getPreviewImage(width, height, config));
			} else {
				imagesList.set(i, touchAction.getPreviewImage(width, height, config));
			}
		}
		
		//System.out.println("image count:" + imageCount);
		Image[] touchImages = new Image[imageCount];
		for (int i = 0; i < imageCount; i++) {
			touchImages[i] = imagesList.get(i);
		}
		
		return touchImages;
	}
	
	public ImageConfig getPreviewConfig() {
		ImageConfig config = new ImageConfig();
		config.trackLineColor = 
				new Color(settingConfig.getConfigValue(
						DrawSettingPanel.PREVIEW_TRACK_LINE_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_TRACK_LING_COLOR.getRGB()));
		config.backgroudColor = 
				new Color(settingConfig.getConfigValue(
						DrawSettingPanel.PREVIEW_IMAGE_BACKGROUD_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_IMAGE_BACKGROUD_COLOR.getRGB()));
		config.lastPointColor = 
				new Color(settingConfig.getConfigValue(
						DrawSettingPanel.PREVIEW_LAST_POINT_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_LAST_POINT_COLOR.getRGB()));
		config.touchPointColor = 
				new Color(settingConfig.getConfigValue(
						DrawSettingPanel.PREVIEW_TOUCH_POINT_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_TOUCH_POINT_COLOR.getRGB()));
		config.touchPointSize = settingConfig.getConfigValue(
						DrawSettingPanel.PREVIEW_TOUCH_POINT_SIZE_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_PREVIEW_TOUCH_POINT_SIZE);
		config.lastPointSize = settingConfig.getConfigValue(
				DrawSettingPanel.PREVIEW_LAST_POINT_SIZE_CONFIG_NAME, 
				DrawSettingPanel.DEFAULT_PREVIEW_LAST_POINT_SIZE);
		
		return config;
	}
	
	public ImageConfig getBaseConfig() {
		ImageConfig config = new ImageConfig();
		config.trackLineColor = 
				new Color(settingConfig.getConfigValue(
						DrawSettingPanel.BASE_TRACK_LINE_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_TRACK_LING_COLOR.getRGB()));
		config.backgroudColor = 
				new Color(settingConfig.getConfigValue(
						DrawSettingPanel.BASE_IMAGE_BACKGROUD_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_IMAGE_BACKGROUD_COLOR.getRGB()));
		config.lastPointColor = 
				new Color(settingConfig.getConfigValue(
						DrawSettingPanel.BASE_LAST_POINT_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_LAST_POINT_COLOR.getRGB()));
		config.touchPointColor = 
				new Color(settingConfig.getConfigValue(
						DrawSettingPanel.BASE_TOUCH_POINT_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_TOUCH_POINT_COLOR.getRGB()));
		config.touchPointSize = settingConfig.getConfigValue(
						DrawSettingPanel.BASE_TOUCH_POINT_SIZE_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_BASE_TOUCH_POINT_SIZE);
		config.lastPointSize = settingConfig.getConfigValue(
				DrawSettingPanel.BASE_LAST_POINT_SIZE_CONFIG_NAME, 
				DrawSettingPanel.DEFAULT_BASE_LAST_POINT_SIZE);
		
		HLog.il(config.toString());
		
		return config;
	}
}
