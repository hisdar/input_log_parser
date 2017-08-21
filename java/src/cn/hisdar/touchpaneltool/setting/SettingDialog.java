package cn.hisdar.touchpaneltool.setting;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import cn.hisdar.radishlib.ui.UIAdapter;
import cn.hisdar.touchpaneltool.DataPool;

public class SettingDialog extends JDialog implements ControlActionListener {

	private static final String BASIC_SETTING_PANEL_TITLE = "解析设置";
	private static final String TRACK_SETTING_PANEL_TITLE = "轨迹设置";
	private static final String ANALYSIS_SETTING_PANEL_TITLE = "分析设置";
	private static final String ENVIRONMENT_SETTING_PANEL_TITLE = "环境设置";
	private static final String DEFAULT_SETTING_PANEL_TITLE = "设置";

	private static final int DEFAULT_SETTING_PANEL_WIDTH = 600;
	private static final int DEFAULT_SETTING_PANEL_HEIGHT = 425;
	
	private JTabbedPane settingTabbedPane = null;

	// 基本设置，包括最大间隔时间设置
	private EnvironmentSettingPanel environmentSettingPanel;
	private ParseSettingPanel basicSettingPanel;
	private DrawSettingPanel drawSettingPanel;
	private AnalysisSettingPanel analysisSettingPanel;
	
	public SettingDialog() {

		setModal(true);
		setTitle(DEFAULT_SETTING_PANEL_TITLE);
		setSize(DEFAULT_SETTING_PANEL_WIDTH, DEFAULT_SETTING_PANEL_HEIGHT);
		setLocation(UIAdapter.getCenterLocation(null, this));
		setIconImage(DataPool.programIcon);

		environmentSettingPanel = new EnvironmentSettingPanel();
		basicSettingPanel = new ParseSettingPanel();
		drawSettingPanel = new DrawSettingPanel();
		analysisSettingPanel = new AnalysisSettingPanel();
		
		settingTabbedPane = new JTabbedPane();
		settingTabbedPane.addTab(ENVIRONMENT_SETTING_PANEL_TITLE, environmentSettingPanel);
		settingTabbedPane.addTab(BASIC_SETTING_PANEL_TITLE, basicSettingPanel);
		settingTabbedPane.addTab(TRACK_SETTING_PANEL_TITLE, drawSettingPanel);
		settingTabbedPane.addTab(ANALYSIS_SETTING_PANEL_TITLE, analysisSettingPanel);

		environmentSettingPanel.addControlActionListener(this);
		basicSettingPanel.addControlActionListener(this);
		drawSettingPanel.addControlActionListener(this);
		analysisSettingPanel.addControlActionListener(this);
		
		setLayout(new BorderLayout());
		add(settingTabbedPane, BorderLayout.CENTER);
	}

	@Override
	public void resetEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void submitEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishEvent() {
		setVisible(false);
	}
}
