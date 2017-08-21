package cn.hisdar.touchpaneltool.update;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import cn.hisdar.lib.adapter.FileAdapter;
import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.Main;
import cn.hisdar.touchpaneltool.update.VersionDowndDialog.VersionDownLoad;
import cn.hisdar.touchpaneltool.update.VersionUpdateMessageDialog.ActionType;
import sun.reflect.generics.tree.Tree;

public class UpdateServer2 {
	
	private static final String LOCAL_VERSION_FILE = "./Config/version.xml";
	private static final String SERVER_VERSION_FILE = "./update/version.xml";
	private static final String AVAILABLE_VERSION_SERVER_FILE = "./update/available_version_server.xml";
	public static final String VERSION_SERVER_CONFIG_FILE = "./Config/version_server.xml";
	
	private boolean isCancleAutoUpdate = false;
	private Thread autoUpdateThread = null;
	private Thread manualUpdateThread = null;
	
	public UpdateServer2() {
		
	}
	
	// 1、自动检测更新
	public void autoUpdate() {
		if (autoUpdateThread == null || !autoUpdateThread.isAlive()) {
			autoUpdateThread = new Thread(new AutoUpdateRunnable());
			autoUpdateThread.start();
		}
	}
	
	public void manualUpdate() {
		if (manualUpdateThread == null || !manualUpdateThread.isAlive()) {
			manualUpdateThread = new Thread(new ManualUpdateRunnable());
			manualUpdateThread.start();
		}
	}
	
	public void autoUpdate_() {
		// 1、解析版本的Version文件， 如果本地Version找不到，那么停止检测
		Version localVersion = new Version();
		if (!localVersion.parseVersionFile(LOCAL_VERSION_FILE)) {
			return;
		}
		
		// 2、加载从服务器上下载的Version文件
		Version serverVersion = new Version();
		VersionServer availableVersionServer = null;
		if (!serverVersion.parseVersionFile(SERVER_VERSION_FILE)) {
			// 本地文件加载失败，从服务器上下载
			HLog.il("autoUpdate:download server verion file");
			availableVersionServer = getAvailableServer();
			if (availableVersionServer == null) {
				HLog.il("autoUpdate:no available version server found");
				return;
			}
			
			// 下载服务器上的版本信息文件
			if (!FileAdapter.copyFile(availableVersionServer.getVersionServerVersionFilePath(), SERVER_VERSION_FILE)) {
				HLog.il("autoUpdate:get version file on server fail");
				return;
			}
			
			// 保存可用的服务器信息
			HConfig availableServerConfig = HConfig.getInstance(AVAILABLE_VERSION_SERVER_FILE, true);
			availableServerConfig.setConfigItem(new ConfigItem("serverPath", availableVersionServer.getVersionServerPath()));
			
			if (!serverVersion.parseVersionFile(SERVER_VERSION_FILE)) {
				HLog.il("autoUpdate:parse server verion file fail");
				new File(SERVER_VERSION_FILE).delete();
				return;
			}
		} else {
			HConfig availableServerConfig = HConfig.getInstance(AVAILABLE_VERSION_SERVER_FILE);
			if (availableServerConfig == null) {
				HLog.il("autoUpdate:version server not available");
				new File(SERVER_VERSION_FILE).delete();
				new File(AVAILABLE_VERSION_SERVER_FILE).delete();
				return;
			}
			
			String serverPath = availableServerConfig.getConfigValue("serverPath");
			if (serverPath == null) {
				HLog.il("autoUpdate:version server not available2");
				new File(SERVER_VERSION_FILE).delete();
				new File(AVAILABLE_VERSION_SERVER_FILE).delete();
				return;
			}
			
			availableVersionServer = new VersionServer(serverPath);
			new File(SERVER_VERSION_FILE).delete();
		}
		
		// 3、版本号对比，看是否需要升级
		if (localVersion.compareVersion(serverVersion) >= 0) {
			HLog.il("autoUpdate:No need to update version");
			return;
		}
		
		HLog.il("autoUpdate:version number check pass");
		// 4、查看用户是否过滤，如果过滤，那么不升级
		IgnoreVersion ignoreVersion = new IgnoreVersion();
		if (!ignoreVersion.parseIgnoreVersionConfigFile()) {
			HLog.il("autoUpdate:parse ignore version config fail");
			return;
		}
		
		if (ignoreVersion.isVersionIgnore(serverVersion)) {
			HLog.il("autoUpdate:version ignored:" + serverVersion.getVersion());
			return;
		}
		
		HLog.il("autoUpdate:ignore list check pass");
		if (isCancleAutoUpdate) {
			HLog.il("autoUpdate:system cancled version auto update");
			return;
		}
		
		// 5、显示版本更新提示
		VersionUpdateMessageDialog versionUpdateMessageDialog = new VersionUpdateMessageDialog(serverVersion);
		versionUpdateMessageDialog.setVisible(true);
		ActionType actionType = versionUpdateMessageDialog.getActionType();
		
		handleVersionUpdateFeedBack(serverVersion, availableVersionServer,  actionType, false);
	}
	
	private void handleVersionUpdateFeedBack(
			Version versionOnServer, 
			VersionServer versionServer, 
			ActionType actionType,
			boolean showMessage) {

		switch (actionType) {
		case UPDATE:
			HLog.il("handleVersionUpdateFeedBack:switch to update");
			VersionDowndDialog versionDowndDialog = new VersionDowndDialog();
			
			File versionFile = new File(versionServer.getInstallPackagePath());
			String versionFileName = versionFile.getName();
			String localVersionFilePath = FileAdapter.pathCat("./update/", versionFileName);
			HLog.il("handleVersionUpdateFeedBack:local version file:" + localVersionFilePath);
			VersionDownLoad versionDownLoadResult = versionDowndDialog.startDowndVersion(
							versionServer.getInstallPackagePath(), 
							localVersionFilePath, 
							versionOnServer.getVersionProofTestValue());
			
			if (versionDownLoadResult == VersionDownLoad.VERSION_DAMAGE
					|| versionDownLoadResult == VersionDownLoad.DOWNLOAD_FAIL) {
				if (showMessage) {
					JOptionPane.showMessageDialog(null, "更新包下载失败，请手动下载更新包！", "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			checkVersionDownloadResultAndAction(versionDownLoadResult, localVersionFilePath, versionOnServer);
			break;
		case IGNORE_CURRENT_UPDATE:
			HLog.il("handleVersionUpdateFeedBack:user ignore version update");
			addVersionToIgnoreList(versionOnServer);
			
			break;
		case CANCLE:
			HLog.il("handleVersionUpdateFeedBack:user cancle version update");
		default:
			break;
		}
	}
	
	private boolean addVersionToIgnoreList(Version version) {
		IgnoreVersion ignoreVersion = new IgnoreVersion();
		if (!ignoreVersion.parseIgnoreVersionConfigFile()) {
			HLog.il("autoUpdate:parse ignore version config fail");
			return false;
		}
		
		return ignoreVersion.addIgnoreVersion(version.getVersion());
	}

	public void checkVersionDownloadResultAndAction(
			VersionDownLoad versionDownLoadResult, 
			String localVersionFilePath,
			Version versionOnServer) {
		
		switch (versionDownLoadResult) {
		case DOWNLOAD_SUCCESS:
			// 启动版本升级程序
			try {
				Runtime.getRuntime().exec(localVersionFilePath);
			} catch (IOException e) {
				HLog.el(e);
				break;
			}
			
			// 退出程序
			Main.exit();
			break;
		case VERSION_DAMAGE:
			// 下载的安装包校验不过的话，就不再下载该版本，等待下次版本更新再下载
			addVersionToIgnoreList(versionOnServer);
			break;
		case DOWNLOAD_FAIL:
			HLog.il("Version download fail");
			break;
		case DOWNLOAD_CANCLE:
			HLog.il("Version download cancle");
			break;
		case DOWNLOADING:
			HLog.il("Version is downloading");
			break;
		default:
			break;
		}
	}
	
	private VersionServer getAvailableServer() {
		
		// 1、获取服务器信息
		String[] versionServerPaths = getVersionServerPaths(VERSION_SERVER_CONFIG_FILE);
		if (versionServerPaths == null || versionServerPaths.length <= 0) {
			HLog.el("downLoadVersionFile:parse version server file fail");
			return null;
		}
		
		// 2、从服务器上下载Version文件
		VersionServer availableVersionServer = null;
		for (int i = 0; i < versionServerPaths.length; i++) {
			VersionServer versionServer = new VersionServer(versionServerPaths[i]);
			HLog.il("UpdateServer2.getAvailableServer(): try to downd:" + versionServer.getVersionServerVersionFilePath());
			HLog.il(SERVER_VERSION_FILE);
			
			if (!FileAdapter.copyFile(versionServer.getVersionServerVersionFilePath(), SERVER_VERSION_FILE)) {
				HLog.el("UpdateServer2.getAvailableServer version server file fail");
				continue;
			} else {
				HLog.il("UpdateServer2.getAvailableServer(): version server file downdload success: " + versionServerPaths[i]);
				availableVersionServer = versionServer;
				break;
			}
		}
		
		return availableVersionServer;
	}

	// 2、停止自动检测
	public void stopAutoUpdate() {
		isCancleAutoUpdate = true;
	}
	
	// 3、手动检测更新
	public void manualUpdate_() {
		// 1、解析版本的Version文件， 如果本地Version找不到，那么停止检测
		Version localVersion = new Version();
		if (!localVersion.parseVersionFile(LOCAL_VERSION_FILE)) {
			JOptionPane.showMessageDialog(null, "本地版本文件损坏，无法检测更新，请手动下载更新包！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// 2、加载从服务器上下载的Version文件
		Version serverVersion = new Version();
		// 从服务器上下载Version文件
		
		VersionServer availableVersionServer = getAvailableServer();
		if (availableVersionServer == null) {
			HLog.el("manualUpdate:no avaiable version server found");
			return ;
		}
		
		HLog.il("manualUpdate:download server verion file");
		if (!FileAdapter.copyFile(availableVersionServer.getVersionServerVersionFilePath(), LOCAL_VERSION_FILE)) {
			HLog.el("manualUpdate:download server verion file fail");
			JOptionPane.showMessageDialog(null, "服务器连接失败，无法下载更新包，请手动下载更新包！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (!serverVersion.parseVersionFile(SERVER_VERSION_FILE)) {
			HLog.el("manualUpdate:parse server verion file fail");
			JOptionPane.showMessageDialog(null, "服务器连接失败，无法下载更新包，请手动下载更新包！", "错误", JOptionPane.ERROR_MESSAGE);
			new File(SERVER_VERSION_FILE).delete();
			return;
		}
		
		new File(SERVER_VERSION_FILE).delete();
		
		// 3、版本号对比，看是否需要升级
		if (localVersion.compareVersion(serverVersion) >= 0) {
			HLog.il("manualUpdate:No need to update version");
			JOptionPane.showMessageDialog(null, "当前版本已是最新版本，无需升级！", "提示", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		HLog.il("autoUpdate:version number check pass");
	
		// 4、下载版本，进行升级
		handleVersionUpdateFeedBack(serverVersion, availableVersionServer,  ActionType.UPDATE, true);
	}
	
	private class AutoUpdateRunnable implements Runnable {

		@Override
		public void run() {
			autoUpdate_();
		}
	}
	
	private class ManualUpdateRunnable implements Runnable {

		@Override
		public void run() {
			manualUpdate_();
		}
	}
	
	public String[] getVersionServerPaths(String configFile) {
		HConfig versionServerConfig = HConfig.getInstance(configFile);
		if (versionServerConfig == null) {
			return null;
		}
		
		String[] versionServerPaths = null;
		ArrayList<ConfigItem> versionServerList = versionServerConfig.getConfigItemList();
		if (versionServerList == null || versionServerList.size() <= 0) {
			versionServerPaths = null;
		} else {
			versionServerPaths = new String[versionServerList.size()];
			for (int i = 0; i < versionServerList.size(); i++) {
				versionServerPaths[i] = versionServerList.get(i).value;
			}
		}
		
		return versionServerPaths;
	}
}
