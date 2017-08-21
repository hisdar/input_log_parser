package cn.hisdar.touchpaneltool.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.FileAdapter;

public class ConfigBase {

	private static final String DEFAULT_FILE_PANEL_CONFIG_PATH = "./Config/";
	
	private String configFileName = null;
	private Vector<ConfigItem> configItems = null;
	private boolean isInitialized = false;
	
	public ConfigBase(String configFileName) {
		this.configFileName = configFileName;
		
		configItems = new Vector<ConfigItem>();
	}
	
	public String getConfigValue(String configName) {
		if (!isInitialized) {
			configItems.removeAllElements();
			loadConfigFile();
			isInitialized = true;
		}
		
		// search the value of config name
		for (int i = 0; i < configItems.size(); i++) {
			if (configItems.get(i).getName().equals(configName)) {
				return configItems.get(i).getValue();
			}
		}
		
		return null;
	}
	
	public void setConfigValue(String configName, int configValue) {
		setConfigValue(configName, configValue + "");
	}
	
	public void setConfigValue(String configName, String configValue) {
		if (!isInitialized) {
			configItems.removeAllElements();
			loadConfigFile();
			isInitialized = true;
		}
		
		// search the value of config name
		for (int i = 0; i < configItems.size(); i++) {
			if (configItems.get(i).getName().equals(configName)) {
				configItems.get(i).setValue(configValue);
				saveConfigFile();
				//System.out.printf("set %s to %s\n", configItems.get(i).getConfigName(), configValue);
				return ;
			}
		}
		
		configItems.add(new ConfigItem(configName, configValue));
		saveConfigFile();
	}
	
	public boolean saveConfigFile() {
		
		if (configFileName == null) {
			System.err.println("Config file name not set");
			return false;
		}
		
		String configFilePath = DEFAULT_FILE_PANEL_CONFIG_PATH + configFileName;
		boolean bRetVal = FileAdapter.initFile(configFilePath);
		if (!bRetVal) {
			System.err.println("Fail to create :" + configFilePath);
			return false;
		}
		
		BufferedWriter writer = FileAdapter.getBufferedWriter(configFilePath);
		if (writer == null) {
			System.err.println("Fail to open :" + configFilePath);
			return false;
		}
		
		for (int i = 0; i < configItems.size(); i++) {
			try {
				writer.write(configItems.get(i).toString() + "\n");
			} catch (IOException e) {
				System.err.printf("Fail to write %s to %s\n", configItems.get(i).toString(), configFilePath);
				HLog.el(e);
				return false;
			}
		}
		
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println("Fail to flush :" + configFilePath);
			HLog.el(e);
			return false;
		}
		
		return true;
	}
	
	public boolean loadConfigFile() {
		if (configFileName == null) {
			System.err.println("Config file name not set");
			return false;
		}
		
		String configFilePath = DEFAULT_FILE_PANEL_CONFIG_PATH + configFileName;
		
		BufferedReader reader = FileAdapter.getBufferedReader(configFilePath);
		if (reader == null) {
			System.err.println("Fail to open :" + configFilePath);
			return false;
		}
		
		String lineString = null;
		try {
			lineString = reader.readLine();
		} catch (IOException e) {
			System.err.println("Fail to read :" + configFilePath);
			HLog.el(e);
			return false;
		}
		
		while (lineString != null) {
			
			ConfigItem configItem = ConfigItem.parseConfigItem(lineString);
			if (configItem != null) {
				configItems.add(configItem);
			}
			
			try {
				lineString = reader.readLine();
			} catch (IOException e) {
				HLog.el("Fail to read :" + configFilePath);
				HLog.el(e);
				return false;
			}
		}
		
		return true;
	}
}
