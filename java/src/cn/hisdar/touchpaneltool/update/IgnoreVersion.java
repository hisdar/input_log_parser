package cn.hisdar.touchpaneltool.update;

import java.util.ArrayList;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;

public class IgnoreVersion {
	
	private static final String IGNORE_VERSION_FILE = "./Config/ignore_version.xml";
	private ArrayList<String> ignoreVersionList;
	private String configFilePath;
	
	public IgnoreVersion() {
		configFilePath = null;
		ignoreVersionList = new ArrayList<String>();
	}
	
	public boolean parseIgnoreVersionConfigFile() {
		return parseIgnoreVersionConfigFile(IGNORE_VERSION_FILE);
	}
	
	public boolean parseIgnoreVersionConfigFile(String configFilePath) {
		this.configFilePath = configFilePath;
		HConfig ignoreVersionConfig = HConfig.getInstance(configFilePath);
		if (ignoreVersionConfig == null) {
			return false;
		}
		
		for (int j = 0; j < ignoreVersionConfig.getConfigItemList().size(); j++) {
			ignoreVersionList.add(ignoreVersionConfig.getConfigItemList().get(j).getValue());
		}
		
		return true;
	}
	
	public boolean isVersionIgnore(Version version) {
		for (int i = 0; i < ignoreVersionList.size(); i++) {
			if (ignoreVersionList.get(i).equals(version.getVersion())) {
				return true;
			}
		}
		
		return false;
	}

	public boolean addIgnoreVersion(String version) {
		HConfig ignoreVersionConfig = HConfig.getInstance(configFilePath);
		if (ignoreVersionConfig == null) {
			return false;
		}
		
		ignoreVersionList.add(version);
		ignoreVersionConfig.addConfigItem(new ConfigItem("ignoreVersion", version));
		return true;
	}
}
