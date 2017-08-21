package cn.hisdar.touchpaneltool.config;

public class MainFrameConfig {

	private static String CONFIG_FILE_NAME = "mainFrameConfigFile.ini";
	private static ConfigBase mainFrameConfigBase = null;
	
	private static final int DEFAULT_PROJECT_PANEL_WIDTH = 250;
	private static final int DEFAULT_CONTROL_PANEL_WIDTH = 350;
	private static final int DEFAULT_LOG_PANEL_HEIGHT = 300;
	private static final int DEFAULT_TOUCH_LOG_PANEL_WIDTH = 1000;
	private static final String DEFAULT_PROJECT_ADD_PATH = "./";
	
	private static final String PROJECT_PANEL_WIDTH_CONFIG_NAME = "projectPanelWidth";
	private static final String LOG_PANEL_HEIGHT_CONFIG_NAME = "logPanelHeight";
	private static final String CONTROL_PANEL_WIDTH_CONFIG_NAME = "controlPanelWidth";
	private static final String TOUCH_LOG_PANEL_WIDTH_CONFIG_NAME = "touchLogPanelWidth";
	private static final String PROJECT_ADD_PATH = "projectAddPath";
	
	private static int projectPanelWidth  = DEFAULT_PROJECT_PANEL_WIDTH;
	private static int logPanelHeight     = DEFAULT_LOG_PANEL_HEIGHT;
	private static int touchLogPanelWidth = DEFAULT_TOUCH_LOG_PANEL_WIDTH;
	private static int controlPanelWidth  = DEFAULT_CONTROL_PANEL_WIDTH;
	
	private static String projectAddPath = DEFAULT_PROJECT_ADD_PATH;
	
	
	
//	private int projectPanelWidth  = DEFAULT_PROJECT_PANEL_WIDTH;
//	private int logPanelHeight     = DEFAULT_LOG_PANEL_HEIGHT;
//	private int touchLogPanelWidth = DEFAULT_TOUCH_LOG_PANEL_WIDTH;
//	private int controlPanelWidth  = DEFAULT_CONTROL_PANEL_WIDTH;
	
	public MainFrameConfig() {
		if (mainFrameConfigBase == null) {
			mainFrameConfigBase = new ConfigBase(CONFIG_FILE_NAME);
			initConfig();
		}
	}
	
	private void initConfig() {
		
		// initialize projectPanelWidth
		projectPanelWidth = getConfigValue(PROJECT_PANEL_WIDTH_CONFIG_NAME, DEFAULT_PROJECT_PANEL_WIDTH);
		
		// Initialize logPanelHeight
		logPanelHeight = getConfigValue(LOG_PANEL_HEIGHT_CONFIG_NAME, DEFAULT_LOG_PANEL_HEIGHT);
		
		// Initialize controlPanelWidth
		controlPanelWidth = getConfigValue(CONTROL_PANEL_WIDTH_CONFIG_NAME, DEFAULT_CONTROL_PANEL_WIDTH);
		
		// Initialize touchLogPanelWidth
		touchLogPanelWidth = getConfigValue(TOUCH_LOG_PANEL_WIDTH_CONFIG_NAME, DEFAULT_TOUCH_LOG_PANEL_WIDTH);
		
		// Initialize projectAddPath
		projectAddPath = getConfigValue(PROJECT_ADD_PATH, DEFAULT_PROJECT_ADD_PATH);
	}
	
	private int getConfigValue(String configName, int defaultValue) {
		
		int configValue = defaultValue;
		
		String configValueString = mainFrameConfigBase.getConfigValue(configName);
		if (configValueString == null) {
			mainFrameConfigBase.setConfigValue(configName, defaultValue);
		} else {
			try {
				configValue = Integer.parseInt(configValueString);
			} catch (Exception e) {
				configValue = defaultValue;
			}
		}
		
		return configValue;
	}
	
	private String getConfigValue(String configName, String defaultValue) {
		String configValue = defaultValue;
		
		String configValueString = mainFrameConfigBase.getConfigValue(configName);
		if (configValueString == null) {
			mainFrameConfigBase.setConfigValue(configName, defaultValue);
		} else {
			configValue = configValueString;
		}
		
		return configValue;
	}

	public int getProjectPanelWidth() {
		return projectPanelWidth;
	}

	public void setProjectPanelWidth(int projectPanelWidth) {
		//System.out.println("Set projectPanelWidth = " + projectPanelWidth);
		this.projectPanelWidth = projectPanelWidth;
		mainFrameConfigBase.setConfigValue(PROJECT_PANEL_WIDTH_CONFIG_NAME, projectPanelWidth);
	}

	public int getLogPanelHeight() {
		return logPanelHeight;
	}

	public void setLogPanelHeight(int logPanelHeight) {
		this.logPanelHeight = logPanelHeight;
		mainFrameConfigBase.setConfigValue(LOG_PANEL_HEIGHT_CONFIG_NAME, logPanelHeight);
	}

	public int getControlPanelWidth() {
		return controlPanelWidth;
	}

	public void setControlPanelWidth(int controlPanelWidth) {
		this.controlPanelWidth = controlPanelWidth;
		mainFrameConfigBase.setConfigValue(CONTROL_PANEL_WIDTH_CONFIG_NAME, controlPanelWidth);
	}

	public int getTouchLogPanelWidth() {
		return touchLogPanelWidth;
	}

	public void setTouchLogPanelWidth(int touchLogPanelWidth) {
		this.touchLogPanelWidth = touchLogPanelWidth;
		mainFrameConfigBase.setConfigValue(TOUCH_LOG_PANEL_WIDTH_CONFIG_NAME, touchLogPanelWidth);
	}
	
	public String getProjectAddPath() {
		return projectAddPath;
	}
	
	public void setProjectAddPath(String path) {
		this.projectAddPath = path;
		mainFrameConfigBase.setConfigValue(PROJECT_ADD_PATH, path);
	}
}
