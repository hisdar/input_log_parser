package cn.hisdar.touchpaneltool.update;

import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;

public class Version {

	public static final int UPDATE_LEVEL_IMPORTANT = 0;
	public static final int UPDATE_LEVEL_REMINDER = 1;
	public static final int UPDATE_LEVEL_MESSAGE = 2;
	
	private static final String VERSION_CONFIG_NAME = "version";
	private static final String VERSION_DESCRIPTION_CONFIG_NAME = "versionDescription";
	private static final String UPDATE_DESCRIPTION_CONFIG_NAME = "updateDescription";
	private static final String UPDATE_LEVEL_CONFIG_NAME = "updateLevel";
	private static final String VERSION_PROOF_TEST_VALUE_NAME = "versionProofTestValue";
	
	
	public static final String DEFAULT_VERSION_INFORMATION = "Version Information not found";
	
	private String version;
	private String versionDescription;
	private String updateDescription;
	private String versionProofTestValue;
	private String copyRight;
	private String author;
	private String feedBack;
	private String homePage;
	
	private int updateLevel;
	
	private HConfig versionConfig;
	
	public Version() {
		version 				= null;
		versionDescription 		= null;
		updateDescription 		= null;
		updateLevel 			= 0;
		versionProofTestValue 	= null;
		copyRight 				= null;
		author 					= null;
		feedBack 				= null;
		homePage 				= null;
	}
	
	public Version(String versionFilePath) {
		parseVersionFile(versionFilePath);
	}

	public boolean parseVersionFile(String versionFilePath) {
		versionConfig = HConfig.getInstance(versionFilePath);
		if (versionConfig == null) {
			return false;
		}
		
		version 				= versionConfig.getConfigValue(VERSION_CONFIG_NAME, DEFAULT_VERSION_INFORMATION);
		versionDescription 		= versionConfig.getConfigValue(VERSION_DESCRIPTION_CONFIG_NAME, DEFAULT_VERSION_INFORMATION);
		updateDescription 		= versionConfig.getConfigValue(UPDATE_DESCRIPTION_CONFIG_NAME, DEFAULT_VERSION_INFORMATION);
		updateLevel 			= versionConfig.getConfigValue(UPDATE_LEVEL_CONFIG_NAME, 2);
		versionProofTestValue 	= versionConfig.getConfigValue(VERSION_PROOF_TEST_VALUE_NAME, "0");
		copyRight 				= versionConfig.getConfigValue("copyRight", "");
		author 					= versionConfig.getConfigValue("author", "");
		feedBack 				= versionConfig.getConfigValue("feedBack", "");
		homePage 				= versionConfig.getConfigValue("homePage", "");
		
		return true;
	}
	
	public String getVersion() {
		return version;
	}

	public String getVersionDescription() {
		return versionDescription;
	}

	public String getUpdateDescription() {
		return updateDescription;
	}

	public int getUpdateLevel() {
		return updateLevel;
	}
	
	public String getVersionProofTestValue() {
		return versionProofTestValue;
	}

	public String getCopyRight() {
		return copyRight;
	}

	public String getAuthor() {
		return author;
	}

	public String getFeedBack() {
		return feedBack;
	}

	public String getHomePage() {
		return homePage;
	}

	public int compareVersion(Version versionToCompare) {
		
		String[] currentVersions = this.version.replace('.', ':').split(":");
		String[] compareVersions = versionToCompare.getVersion().replace('.', ':').split(":");
		
		HLog.dl("Local version:" + this.version);
		HLog.dl("Verson on server:" + versionToCompare.getVersion());
		
		int[] currentVersionNumbers = new int[currentVersions.length];
		int[] compareVersionNumbers = new int[compareVersions.length];
		
		try {
			for (int i = 0; i < currentVersionNumbers.length; i++) {
				currentVersionNumbers[i] = Integer.parseInt(currentVersions[i]);
			}
			
			for (int i = 0; i < compareVersionNumbers.length; i++) {
				compareVersionNumbers[i] = Integer.parseInt(compareVersions[i]);
			}
		} catch (Exception e) {
			HLog.el(e);
			return 0;
		}
		
		int compareResult = 0;
		for (int i = 0; i < compareVersionNumbers.length && i < currentVersionNumbers.length; i++) {
			compareResult = currentVersionNumbers[i] - compareVersionNumbers[i];
			if (compareResult != 0) {
				return compareResult;
			}
		}
		
		if (currentVersionNumbers.length > compareVersionNumbers.length) {
			compareResult = currentVersionNumbers[compareVersionNumbers.length];
		} else if (currentVersionNumbers.length < compareVersionNumbers.length) {
			compareResult = -compareVersionNumbers[currentVersionNumbers.length];
		} else {
			compareResult = 0;
		}
		
		HLog.dl("compareResult=" + compareResult);
		return compareResult;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((copyRight == null) ? 0 : copyRight.hashCode());
		result = prime * result + ((feedBack == null) ? 0 : feedBack.hashCode());
		result = prime * result + ((homePage == null) ? 0 : homePage.hashCode());
		result = prime * result + ((updateDescription == null) ? 0 : updateDescription.hashCode());
		result = prime * result + updateLevel;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((versionConfig == null) ? 0 : versionConfig.hashCode());
		result = prime * result + ((versionDescription == null) ? 0 : versionDescription.hashCode());
		result = prime * result + ((versionProofTestValue == null) ? 0 : versionProofTestValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Version other = (Version) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (copyRight == null) {
			if (other.copyRight != null)
				return false;
		} else if (!copyRight.equals(other.copyRight))
			return false;
		if (feedBack == null) {
			if (other.feedBack != null)
				return false;
		} else if (!feedBack.equals(other.feedBack))
			return false;
		if (homePage == null) {
			if (other.homePage != null)
				return false;
		} else if (!homePage.equals(other.homePage))
			return false;
		if (updateDescription == null) {
			if (other.updateDescription != null)
				return false;
		} else if (!updateDescription.equals(other.updateDescription))
			return false;
		if (updateLevel != other.updateLevel)
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (versionConfig == null) {
			if (other.versionConfig != null)
				return false;
		} else if (!versionConfig.equals(other.versionConfig))
			return false;
		if (versionDescription == null) {
			if (other.versionDescription != null)
				return false;
		} else if (!versionDescription.equals(other.versionDescription))
			return false;
		if (versionProofTestValue == null) {
			if (other.versionProofTestValue != null)
				return false;
		} else if (!versionProofTestValue.equals(other.versionProofTestValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Version [version=" + version + ", versionDescription=" + versionDescription + ", updateDescription="
				+ updateDescription + ", versionProofTestValue=" + versionProofTestValue + ", copyRight=" + copyRight
				+ ", author=" + author + ", feedBack=" + feedBack + ", homePage=" + homePage + ", updateLevel="
				+ updateLevel + ", versionConfig=" + versionConfig + "]";
	}

}
