package cn.hisdar.touchpaneltool.update;

import java.util.ArrayList;

import cn.hisdar.lib.adapter.FileAdapter;
import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

public class VersionServer {

	
	
	private String versionServerPath;
	
	public VersionServer(String versionServerPath) {
		this.versionServerPath = versionServerPath;
	}

	public String getVersionServerVersionFilePath() {
		if (versionServerPath == null) {
			return null;
		} else {
			return FileAdapter.pathCat(versionServerPath, "version.xml");
		}
	}

	public String getInstallPackagePath() {
		if (versionServerPath == null) {
			return null;
		} else {
			return FileAdapter.pathCat(versionServerPath, "Multi-Touch_Input_Parser.exe");
		}
	}

	public String getVersionServerPath() {
		return versionServerPath;
	}

	public void setVersionServerPath(String versionServerPath) {
		this.versionServerPath = versionServerPath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((versionServerPath == null) ? 0 : versionServerPath.hashCode());
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
		VersionServer other = (VersionServer) obj;
		if (versionServerPath == null) {
			if (other.versionServerPath != null)
				return false;
		} else if (!versionServerPath.equals(other.versionServerPath))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VersionServer [versionServerPath=" + versionServerPath + "]";
	}
}
