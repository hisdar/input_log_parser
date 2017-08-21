package cn.hisdar.touchpaneltool.setting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.radishlib.ui.LinerPanel;
import cn.hisdar.touchpaneltool.setting.common.ColorSetPanel;
import cn.hisdar.touchpaneltool.setting.common.PathSetPanel;

public class EnvironmentSettingPanel extends JPanel implements ControlActionListener, SettingPanelInterface {
	
	public final static Color DEFAULT_THEME_COLOR = new Color(0x293955);
	public final static Color DEFAULT_TITLE_PANEL_COLOR = new Color(0x44587c);
	public final static Color DEFAULT_MESSAGE_AND_CONTROL_PANEL_COLOR = new Color(0xbcc7d8);
	public final static String DEFAULT_WORKSPACE_PATH = "./";
	
	private final static String THEME_COLOR_DES = "说明：程序的背景、分割符的颜色";
	private final static String TITLE_VIEW_COLOR_DES = "说明：标题界面的背景颜色";
	private final static String MESSAGE_AND_CONTROL_VIEW_COLOR_DES = "说明：消息和控制界面的背景颜色";
	private static final String WORKSPACE_PATH_DES = "说明：工作空间是程序保存设置，存放缓冲文件和工程文件等信息的目录，尽量选择空间比较大的盘";
	
	public  static final String ENVIRONMENT_CONFIG_FILE = "./Config/environment_config.xml";
	
	public static final String WORKSPACE_PATH_CONFIG_NAME = "workspacePath";
	public static final String THEME_COLOR_CONFIG_NAME = "themeColor";
	public static final String TITLE_VIEW_COLOR_CONFIG_NAME = "titleViewColor";
	public static final String MESSAGE_AND_CONTROL_VIEW_COLOR_CONFIG_NAME = "messageAndControlViewColor";
	
	private static ArrayList<EnvironmentSettingChangeListener> settingChangeListeners = new ArrayList<EnvironmentSettingChangeListener>();
	private static HConfig environmentConfig = HConfig.getInstance(ENVIRONMENT_CONFIG_FILE, true);
	
	private ControlPanel controlPanel;
	
	private ColorSetPanel themeColorSetPanel;
	private ColorSetPanel controlViewColorSetPanel;
	private ColorSetPanel messageViewColorSetPanel;
	
	private PathSetPanel workspaceSetPanel;
	
	
	
	public EnvironmentSettingPanel() {
		
		setLayout(new BorderLayout());
		
		LinerPanel environmentPanelView = new LinerPanel();
		environmentPanelView.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		workspaceSetPanel = new PathSetPanel("工作路径：", DEFAULT_WORKSPACE_PATH, WORKSPACE_PATH_DES);
		environmentPanelView.addPanel(workspaceSetPanel);
		
		themeColorSetPanel = new ColorSetPanel("主题颜色：    ", DEFAULT_THEME_COLOR, THEME_COLOR_DES);
		controlViewColorSetPanel = new ColorSetPanel("标题界面颜色：", DEFAULT_TITLE_PANEL_COLOR, TITLE_VIEW_COLOR_DES);
		messageViewColorSetPanel = new ColorSetPanel("消息面板颜色：", DEFAULT_MESSAGE_AND_CONTROL_PANEL_COLOR, MESSAGE_AND_CONTROL_VIEW_COLOR_DES);
		
		environmentPanelView.addPanel(themeColorSetPanel);
		environmentPanelView.addPanel(controlViewColorSetPanel);
		environmentPanelView.addPanel(messageViewColorSetPanel);
		
		add(environmentPanelView, BorderLayout.CENTER);
		
		controlPanel = new ControlPanel();
		controlPanel.addControlActionListeners(this);
		
		JPanel controlPanelView = new JPanel(new BorderLayout());
		controlPanelView.add(controlPanel, BorderLayout.EAST);
		add(controlPanelView, BorderLayout.SOUTH);
		
		initConfig();
		if (!new File(ENVIRONMENT_CONFIG_FILE).exists()) {
			resetEvent();
		}
	}
	
	private void initConfig() {
		String workspace = null;
		if (environmentConfig != null) {
			workspace = environmentConfig.getConfigValue(WORKSPACE_PATH_CONFIG_NAME);
		}
		
		if (workspace == null) {
			workspace = DEFAULT_WORKSPACE_PATH;
		}
		
		workspaceSetPanel.setPath(workspace);
		
		if (environmentConfig != null) {
			themeColorSetPanel.setSelectedColor(
					new Color(environmentConfig.getConfigValue(THEME_COLOR_CONFIG_NAME, 
							DEFAULT_THEME_COLOR.getRGB())));
			controlViewColorSetPanel.setSelectedColor(
					new Color(environmentConfig.getConfigValue(TITLE_VIEW_COLOR_CONFIG_NAME, 
							DEFAULT_TITLE_PANEL_COLOR.getRGB())));
			messageViewColorSetPanel.setSelectedColor(
					new Color(environmentConfig.getConfigValue(MESSAGE_AND_CONTROL_VIEW_COLOR_CONFIG_NAME, 
							DEFAULT_MESSAGE_AND_CONTROL_PANEL_COLOR.getRGB())));
		} else {
			themeColorSetPanel.setSelectedColor(DEFAULT_THEME_COLOR);
			controlViewColorSetPanel.setSelectedColor(DEFAULT_TITLE_PANEL_COLOR);
			messageViewColorSetPanel.setSelectedColor(DEFAULT_MESSAGE_AND_CONTROL_PANEL_COLOR);
		}
	}
	
	private void saveConfig() {
		if (environmentConfig == null) {
			environmentConfig = HConfig.getInstance(ENVIRONMENT_CONFIG_FILE, true);
		}
		
		environmentConfig.setConfigItem(new ConfigItem(WORKSPACE_PATH_CONFIG_NAME, DEFAULT_WORKSPACE_PATH));
		environmentConfig.setConfigItem(
				new ConfigItem(THEME_COLOR_CONFIG_NAME, themeColorSetPanel.getSelectedColor().getRGB()));
		environmentConfig.setConfigItem(
				new ConfigItem(TITLE_VIEW_COLOR_CONFIG_NAME, controlViewColorSetPanel.getSelectedColor().getRGB()));
		environmentConfig.setConfigItem(
				new ConfigItem(MESSAGE_AND_CONTROL_VIEW_COLOR_CONFIG_NAME, messageViewColorSetPanel.getSelectedColor().getRGB()));
		environmentConfig.setConfigItem(
				new ConfigItem(WORKSPACE_PATH_CONFIG_NAME, workspaceSetPanel.getPath()));
	}
	
	public void notifyColorChangeEvent() {
		for (int i = 0; i < settingChangeListeners.size(); i++) {
			notifyColorChangeEvent(settingChangeListeners.get(i));
		}
	}
	
	private static void notifyColorChangeEvent(EnvironmentSettingChangeListener listener) {
		// 通知标题颜色的改变
		listener.colorChangeEvent(TITLE_VIEW_COLOR_CONFIG_NAME, 
				getConfigColor(TITLE_VIEW_COLOR_CONFIG_NAME, DEFAULT_TITLE_PANEL_COLOR));
		// 通知消息面板和控制面板的颜色改变
		listener.colorChangeEvent(MESSAGE_AND_CONTROL_VIEW_COLOR_CONFIG_NAME, 
				getConfigColor(MESSAGE_AND_CONTROL_VIEW_COLOR_CONFIG_NAME, DEFAULT_MESSAGE_AND_CONTROL_PANEL_COLOR));
		// 通知主题颜色的改变
		listener.colorChangeEvent(THEME_COLOR_CONFIG_NAME, 
				getConfigColor(THEME_COLOR_CONFIG_NAME, DEFAULT_THEME_COLOR));
	}
	
	public static void addEnvironmentSettingChangeListener(EnvironmentSettingChangeListener listener) {
		for (int i = 0; i < settingChangeListeners.size(); i++) {
			if (settingChangeListeners.get(i) == listener) {
				return;
			}
		}
		
		settingChangeListeners.add(listener);
		notifyColorChangeEvent(listener);
	}
	
	public static void removeEnvironmentSettingChangeListener(EnvironmentSettingChangeListener listener) {
		int listenerCount = settingChangeListeners.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			if (settingChangeListeners.get(i) == listener) {
				settingChangeListeners.remove(i);
			}
		}
	}
	
	private static Color getConfigColor(String configName, Color defaultColor) {
		if (environmentConfig != null) {
			return new Color(environmentConfig.getConfigValue(configName, defaultColor.getRGB()));
		} else {
			return null;
		}
	}

	@Override
	public void resetEvent() {
		themeColorSetPanel.setSelectedColor(DEFAULT_THEME_COLOR);
		controlViewColorSetPanel.setSelectedColor(DEFAULT_TITLE_PANEL_COLOR);
		messageViewColorSetPanel.setSelectedColor(DEFAULT_MESSAGE_AND_CONTROL_PANEL_COLOR);
		workspaceSetPanel.setPath(DEFAULT_WORKSPACE_PATH);
		saveConfig();
	}

	@Override
	public void submitEvent() {
		saveConfig();
		notifyColorChangeEvent();
	}
	
	@Override
	public void finishEvent() {
		saveConfig();
		notifyColorChangeEvent();
	}
	
	@Override
	public void addControlActionListener(ControlActionListener listener) {
		controlPanel.addControlActionListeners(listener);
	}

	@Override
	public void removeControlActionListener(ControlActionListener listener) {
		controlPanel.removeControlActionListeners(listener);
	}
}
