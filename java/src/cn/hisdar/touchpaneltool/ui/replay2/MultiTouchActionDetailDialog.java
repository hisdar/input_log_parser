package cn.hisdar.touchpaneltool.ui.replay2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.setting.DrawSettingPanel;
import cn.hisdar.touchpaneltool.ui.control.ResolutionChangeListener;
import cn.hisdar.touchpaneltool.ui.control.ResolutionSetPanel;

public class MultiTouchActionDetailDialog extends JDialog implements ResolutionChangeListener {

	private JTabbedPane detailTabbedPane = null;
	
	private HImageLabel imageLabel = null;
	private JTextArea originalDataArea = null;
	private JTextArea eventDataArea = null;
	private Dimension currentResolution = null;
	
	private HConfig settingConfig = null;
	
	public MultiTouchActionDetailDialog() {
		setTitle("详情页");
		setModal(true);
		setSize(800, 400);
	
		currentResolution = new Dimension();
		settingConfig = HConfig.getInstance(DrawSettingPanel.DRAW_CONFIG_FILE_PATH);
		
		detailTabbedPane = new JTabbedPane();
		detailTabbedPane.addTab("预览", getImagePanel());
		detailTabbedPane.addTab("原始数据", getOriginalDataPanel());
		detailTabbedPane.addTab("事件数据", getEventDataPanel());
	
		setLayout(new BorderLayout());
		add(detailTabbedPane, BorderLayout.CENTER);
		ResolutionSetPanel.addResolutionChangeListener(this);
	}
	
	private JComponent getOriginalDataPanel() {
		originalDataArea = new JTextArea();
		JScrollPane originalDataPanel = new JScrollPane(originalDataArea);
		return originalDataPanel;
	}
	
	private JComponent getEventDataPanel() {
		eventDataArea = new JTextArea();
		JScrollPane eventDataPanel = new JScrollPane(eventDataArea);
		return eventDataPanel;
	}
	
	private JComponent getImagePanel() {
		JPanel imagePanel = new JPanel(new BorderLayout());
		imageLabel = new HImageLabel();
		imagePanel.add(imageLabel, BorderLayout.CENTER);
		return imagePanel;
	}
	
	public void setMultiTouchAction2Data(MultiTouchAction2 touchAction2) {
		originalDataArea.setText(touchAction2.getOriginalData().toString());
		ArrayList<MultiTouchPoint> touchPoints = touchAction2.getTouchPoints();
		imageLabel.setDrawImage(touchAction2.getPreviewImage((int)currentResolution.getWidth(), (int)currentResolution.getHeight(), getBaseConfig()));
		eventDataArea.setText("");
		for (int i = 0; i < touchPoints.size(); i++) {
			eventDataArea.append(touchPoints.get(i).toString() + "\n");
		}
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

	@Override
	public void resolutionChangeEvent(Dimension resolution) {
		currentResolution.setSize(resolution);
	}
}
