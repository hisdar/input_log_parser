package cn.hisdar.touchpaneltool.ui.replay2;

import java.awt.Color;

import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.setting.DrawSettingPanel;

public class ImageConfigAdapter {
	
	private static HConfig drawConfig = HConfig.getInstance(DrawSettingPanel.DRAW_CONFIG_FILE_PATH);;
	
	public static ImageConfig getPreviewConfig() {
		ImageConfig config = new ImageConfig();
		config.trackLineColor = 
				new Color(drawConfig.getConfigValue(
						DrawSettingPanel.PREVIEW_TRACK_LINE_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_TRACK_LING_COLOR.getRGB()));
		config.backgroudColor = 
				new Color(drawConfig.getConfigValue(
						DrawSettingPanel.PREVIEW_IMAGE_BACKGROUD_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_IMAGE_BACKGROUD_COLOR.getRGB()));
		config.lastPointColor = 
				new Color(drawConfig.getConfigValue(
						DrawSettingPanel.PREVIEW_LAST_POINT_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_LAST_POINT_COLOR.getRGB()));
		config.touchPointColor = 
				new Color(drawConfig.getConfigValue(
						DrawSettingPanel.PREVIEW_TOUCH_POINT_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_TOUCH_POINT_COLOR.getRGB()));
		config.touchPointSize = drawConfig.getConfigValue(
						DrawSettingPanel.PREVIEW_TOUCH_POINT_SIZE_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_PREVIEW_TOUCH_POINT_SIZE);
		config.lastPointSize = drawConfig.getConfigValue(
				DrawSettingPanel.PREVIEW_LAST_POINT_SIZE_CONFIG_NAME, 
				DrawSettingPanel.DEFAULT_PREVIEW_LAST_POINT_SIZE);
		
		return config;
	}
	
	public static ImageConfig getBaseConfig() {
		ImageConfig config = new ImageConfig();
		config.trackLineColor = 
				new Color(drawConfig.getConfigValue(
						DrawSettingPanel.BASE_TRACK_LINE_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_TRACK_LING_COLOR.getRGB()));
		config.backgroudColor = 
				new Color(drawConfig.getConfigValue(
						DrawSettingPanel.BASE_IMAGE_BACKGROUD_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_IMAGE_BACKGROUD_COLOR.getRGB()));
		config.lastPointColor = 
				new Color(drawConfig.getConfigValue(
						DrawSettingPanel.BASE_LAST_POINT_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_LAST_POINT_COLOR.getRGB()));
		config.touchPointColor = 
				new Color(drawConfig.getConfigValue(
						DrawSettingPanel.BASE_TOUCH_POINT_COLOR_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_TOUCH_POINT_COLOR.getRGB()));
		config.touchPointSize = drawConfig.getConfigValue(
						DrawSettingPanel.BASE_TOUCH_POINT_SIZE_CONFIG_NAME, 
						DrawSettingPanel.DEFAULT_BASE_TOUCH_POINT_SIZE);
		config.lastPointSize = drawConfig.getConfigValue(
				DrawSettingPanel.BASE_LAST_POINT_SIZE_CONFIG_NAME, 
				DrawSettingPanel.DEFAULT_BASE_LAST_POINT_SIZE);
		
		HLog.il(config);
		
		return config;
	}
}
