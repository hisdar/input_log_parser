package cn.hisdar.touchpaneltool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import cn.hisdar.lib.adapter.FileAdapter;
import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.ui.project2.Project;

/**
 * @description 用来初始化和设置程序的配置信息
 * @author Hisdar
 *
 */

public class ProgramConfiguration {

	public final static String PROGRAM_CONFIG_FILE_PATH = "./config/program_config.xml";
	public final static String SUPPORTED_LOG_TYPE_CONFIG_FILE_PATH = "./config/supported_log_type.xml";
	public final static String PROJECT_PATH_CONFIG_NAME = "projectPath";
	
	public final static String PROGRAM_WORK_SPACE_CONFIG_NAME = "workSpace";
	
	private static Icon defaultDialogLogo = null;
	private HashMap<String, String> defaultConfigurationMap;
	
	public ProgramConfiguration() {
		defaultConfigurationMap = new HashMap<>();
		initDefaultConfigurations();
	}
	
	private void initDefaultConfigurations() {
		defaultConfigurationMap.put(PROGRAM_WORK_SPACE_CONFIG_NAME, "./");
	}

	public boolean initProgramConfiguration() {
		// 检查应用程序的配置信息
		HLog.il("initProgramConfiguration: init program configurations");
		HConfig programConfig = HConfig.getInstance(PROGRAM_CONFIG_FILE_PATH);
		if (programConfig == null) {
			HLog.il("initProgramConfiguration: program config file is not exist, create it");
			programConfig = HConfig.getInstance(PROGRAM_CONFIG_FILE_PATH, true);
		}
		
		// 检测每一个基本配置项是否存在
		Iterator<Entry<String, String>> iter = defaultConfigurationMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = (Entry<String, String>) iter.next();
			String configName = entry.getKey();
			String defaultConfigValue = entry.getValue();
			
			String configValue = programConfig.getConfigValue(configName);
			if (configValue == null) {
				HLog.il("initProgramConfiguration: is not exist, set default value");
				HLog.il("initProgramConfiguration: configName=" + configName);
				HLog.il("initProgramConfiguration: defaultConfigValue=" + defaultConfigValue);
				programConfig.setConfigItem(new ConfigItem(configName, defaultConfigValue));
			}
		}
		
		// 檢查每一個特殊配置項
		// 1、檢查工程目錄
		String projectPath = programConfig.getConfigValue(PROJECT_PATH_CONFIG_NAME);
		if (projectPath == null) {
			String workSpace = programConfig.getConfigValue(PROGRAM_WORK_SPACE_CONFIG_NAME);
			projectPath = FileAdapter.pathCat(workSpace, "/project/");
			HLog.il("initProgramConfiguration: is not exist, set default value");
			HLog.il("initProgramConfiguration: configName=" + PROJECT_PATH_CONFIG_NAME);
			HLog.il("initProgramConfiguration: defaultConfigValue=" + projectPath);
			programConfig.setConfigItem(new ConfigItem(PROJECT_PATH_CONFIG_NAME, projectPath));
		}
		
		// 2、初始化程序支持的日志类型
		HConfig supportedLogTypeConfig = HConfig.getInstance(SUPPORTED_LOG_TYPE_CONFIG_FILE_PATH);
		if (supportedLogTypeConfig == null) {
			supportedLogTypeConfig = HConfig.getInstance(SUPPORTED_LOG_TYPE_CONFIG_FILE_PATH, true);
			supportedLogTypeConfig.addConfigItem(new ConfigItem(Project.SUPPORT_LOG_TYPE_CONFIG_NAME, "input"));
		} else if (supportedLogTypeConfig.getConfigItemList().size() <= 0) {
			supportedLogTypeConfig.addConfigItem(new ConfigItem(Project.SUPPORT_LOG_TYPE_CONFIG_NAME, "input"));
		}
		
		return true;
	}
	
	public static Icon getDefaultDialogLogo() {
		if (defaultDialogLogo == null) {
			defaultDialogLogo = new ImageIcon("./Image/defaultDialogLogo.png");
		}
		
		return defaultDialogLogo;
	}
}
