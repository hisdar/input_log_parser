package cn.hisdar.touchpaneltool.ui.project2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import cn.hisdar.MultiTouchEventParse.EventDeviceMap;
import cn.hisdar.MultiTouchEventParse.EventType;
import cn.hisdar.input2.InputLogFormat;
import cn.hisdar.lib.adapter.FileAdapter;
import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.ui.HProgressDialog;
import cn.hisdar.touchpaneltool.ProgramConfiguration;
import cn.hisdar.touchpaneltool.common.GZip;
import cn.hisdar.touchpaneltool.ui.project2.CreateProjectDialog.CreateProjectAction;
import cn.hisdar.touchpaneltool.ui.project2.action.ProjectLoader;

public class Project {
	
	public static final String PROJECT_PROPERTY_XML_FILE_NAME = "project.xml";
	public static final String PROJECT_PROPERTY_FILE_NAME = "project.ipj";
	private static final String PROJECT_NAME_CONFIG_TITLE = "projectName";
	private static final String PROJECT_SRC_PATH_CONFIG_TITLE = "projectSrcPath";
	private static final String PROJECT_FILE_PATH_NAME = "input";
	private static final String DEVICE_EVENT_MAP_FILE_NAME = "device_event_map.xml";
	
	public static final String SUPPORT_LOG_TYPE_CONFIG_NAME = "logType";
	
	// �������֣�������ʾ�ڽ����ϵ�
	private String projectName;
	
	// �������ڵ�Ŀ¼
	private String projectPath;
	
	// ����Դ�ļ����ڵ�Ŀ¼
	private String projectSrcPath;
	
	private EventDeviceMap[] eventDeviceMaps;
	
	private static HProgressDialog createProjectProgressDialog = null;
	
	private Project() {
		eventDeviceMaps = null;
		createProjectProgressDialog = new HProgressDialog();
		createProjectProgressDialog.setLogo(new ImageIcon("./Image/VersionDialogLogo.png"));
	}
	
	/**
	 * @description if return value is null, it means user cancel project create
	 * @param projectBasePath
	 * @param isAddFile
	 * @return if user cancel project create, return null, else return this
	 */
	public static Project createProjectWithProgress(String projectBasePath,	boolean isAddFile) {
		
		 Project project = new Project();
		 return project.createProject(projectBasePath, isAddFile, true);
	}
	
	/**
	 * @description if return value is null, it means user cancel project create
	 * @param projectBasePath
	 * @param isAddFile
	 * @return if user cancel project create, return null, else return this
	 */
	public static Project createProject(String projectBasePath,	boolean isAddFile) {
		
		Project project = new Project();
		return project.createProject(projectBasePath, isAddFile, false);
	}
	
	/**
	 * @description if return value is null, it means user cancel project create
	 * @param projectBasePath
	 * @param isAddFile
	 * @param isShowProgress
	 * @return if user cancel project create, return null, else return this
	 */
	private Project createProject(String projectBasePath, boolean isAddFile, boolean isShowProgress) {
		
		HLog.dl("createProject: projectBasePath=" + projectBasePath);
		HLog.dl("createProject: isAddFile=" + isAddFile);
		HLog.dl("createProject: isShowProgress=" + isShowProgress);
		CreateProjectDialog createProjectDialog = new CreateProjectDialog();
		CreateProjectAction action = createProjectDialog.showProjectCreateDialog(isAddFile);
		if (action == CreateProjectAction.ACTION_OK) {
			String projectName = createProjectDialog.getProjectName();
			String projectSrcPath = createProjectDialog.getProjectSrcPath();
			HLog.il("createProject:projectName:" + projectName);
			HLog.il("createProject:projectSrcPath:" + projectSrcPath);
			
			this.projectName = projectName;
			this.projectSrcPath = projectSrcPath;
			
			if (isShowProgress) {
				CreateProjectRunnable createProjectRunnable = new CreateProjectRunnable(projectBasePath, projectName, projectSrcPath);
				
				Thread createProjectThread = new Thread(createProjectRunnable);
				createProjectThread.start();
				createProjectProgressDialog.setModal(true);
				createProjectProgressDialog.setVisible(true);
				return createProjectRunnable.getInputProject();
			} else {
				return createProject(projectBasePath, projectName, projectSrcPath);
			}
		} else {
			HLog.il("createProject:Cancle!!!");
			return null;
		}
	}
	
	// ����һ������
	private Project createProject(String projectBasePath, String projectName, String projectSrcPath) {
		HLog.dl("createProject: projectBasePath=" + projectBasePath);
		HLog.dl("createProject: projectName=" + projectName);
		HLog.dl("createProject: projectSrcPath=" + projectSrcPath);
		// 1�������̷���һ��Ŀ¼
		String projectPath = FileAdapter.pathCat(projectBasePath, createProjectPath(projectName));
		this.projectPath = projectPath;
		
		if (!FileAdapter.initFolder(projectPath)) {
			HLog.el("createProject: failed to create project path:" + projectPath);
			return null;
		}
		
		// 2������Դ�ļ�������
		if (!copyProjectLog(projectPath, projectSrcPath)) {
			HLog.el("createProject: copy src file to project folder fail:");
			HLog.el("createProject: projectPath=" + projectPath);
			HLog.el("createProject: projectSrcPath=" + projectSrcPath);
			cancleProjectCreate(projectPath);
			return null;
		}
		
		// 3�� ��ѹ�������ļ�
		if (!unCompressLogFiles(projectPath)) {
			HLog.el("createProject: un zip log files fail:" + projectPath);
			cancleProjectCreate(projectPath);
			return null;
		}
		
		// 4���������������ļ�
		if (!createProjectPropertyFile(projectPath, projectName, projectSrcPath)) {
			HLog.el("createProject: create project property file fail:" + projectPath);
			cancleProjectCreate(projectPath);
			return null;
		}
		
		// 5����ʽ��input��־
		InputLogFormat inputLogFormat = new InputLogFormat();
		inputLogFormat.formateInputLogWithProgress(getProjectFilePath());
		
		return this;
	}
	
	private boolean createProjectPropertyFile(String projectPath, String projectName, String projectSrcPath) {
		String projectPropertyFilePath = FileAdapter.pathCat(projectPath, PROJECT_PROPERTY_FILE_NAME);
		if (!FileAdapter.initFile(projectPropertyFilePath)) {
			HLog.el("createProjectPropertyFile: init project property file fail:" + projectPropertyFilePath);
			return false;
		}
		
		// �������ļ���������xml�ļ��Ա�����
		File projectPropertyFile = new File(projectPropertyFilePath);
		File projectPropertyXMLFile = new File(FileAdapter.pathCat(projectPropertyFile.getParent(), PROJECT_PROPERTY_XML_FILE_NAME));
		projectPropertyFile.renameTo(projectPropertyXMLFile);
		
		HConfig projectProperty = HConfig.getInstance(projectPropertyXMLFile.getPath(), true);

		projectProperty.setConfigItem(new ConfigItem(PROJECT_NAME_CONFIG_TITLE, projectName));
		projectProperty.setConfigItem(new ConfigItem(PROJECT_SRC_PATH_CONFIG_TITLE, projectSrcPath));
		
		// ��xml�ļ����������ƹ����ļ�
		if (!projectPropertyXMLFile.renameTo(projectPropertyFile)) {
			HLog.il("Project.createProjectPropertyFile(): fail to rename project xml file to project file");
			return false;
		}
		
		return true;
	}

	public static Project loadProject(String projectPath) {
		
		if (projectPath == null) {
			HLog.el("loadProject: project path illegal:" + projectPath);
			return null;
		}
		
		// 1������Ŀ¼�µĹ����ļ�
		String projectPropertyFilePath = FileAdapter.pathCat(projectPath, PROJECT_PROPERTY_FILE_NAME);
		File projectPropertyFile = new File(projectPropertyFilePath);
		if (!projectPropertyFile.exists() || !projectPropertyFile.isFile()) {
			HLog.el("loadProject: project property path illegal:" + projectPropertyFilePath);
			return null;
		}
		
		// 2���������ļ�ת����xml�ļ������н���
		String projectPropertyXMLFilePath = FileAdapter.pathCat(projectPath, PROJECT_PROPERTY_XML_FILE_NAME);
		File projectPropertyXMLFile = new File(projectPropertyXMLFilePath);
		projectPropertyFile.renameTo(projectPropertyXMLFile);
		
		HConfig projectPropertyConfig = HConfig.getInstance(projectPropertyXMLFilePath);
		if (projectPropertyConfig == null) {
			HLog.el("loadProject: no project property file found:" + projectPropertyXMLFilePath);
			return null;
		}
		
		ArrayList<ConfigItem> projectConfigItems = projectPropertyConfig.getConfigItemList();
		for (int i = 0; i < projectConfigItems.size(); i++) {
			HLog.il("Project.loadProject():" + projectConfigItems.get(i));
		}
		
		String projectName = projectPropertyConfig.getConfigValue(PROJECT_NAME_CONFIG_TITLE);
		if (projectName == null) {
			HLog.el("loadProject: project property file damaged:" + projectPath);
			return null;
		}
		
		String projectSrcPath = projectPropertyConfig.getConfigValue(PROJECT_SRC_PATH_CONFIG_TITLE);
		
		Project project = new Project();
		project.setProjectName(projectName);
		project.setProjectPath(projectPath);
		project.setProjectSrcPath(projectSrcPath);
		
		projectPropertyXMLFile.renameTo(projectPropertyFile);
		
		return project;
	}
	
	private String createProjectPath(String projectName) {
		 SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd_HH-mm-ss");
		 String projectPath = dateFormat.format(new Date());
		 projectPath = projectPath + "_" + projectName;
		 
		 return projectPath;
	}
	
	private boolean copyProjectLog(String projectPath, String projectSrcPath) {
		
		boolean retValue = false;
		
		String inputLogFileKeyWords = getInputLogFileKeyWords();
		if (inputLogFileKeyWords == null) {
			JOptionPane.showMessageDialog(null, "���ô���û���ҵ�֧�ֵ���־���ͣ�", "����", JOptionPane.ERROR_MESSAGE);
			HLog.el("Project.copyProjectLog(): no support log type found:");
			return false;
		}
		
		// 1��Ϊ֧�ֵ���־���ʹ����ļ���
		if (!FileAdapter.initFolder(getProjectFilePath())) {
			HLog.el("copyProjectLog: failed to create log path:" + getProjectFilePath());
			JOptionPane.showMessageDialog(null, "��������Ŀ¼ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// 2���ж�Ҫ������λ�����ļ�����Ŀ¼
		File srcFile = new File(projectSrcPath);
		if (srcFile.isFile()) {
			HLog.il("copyProjectLog: copy file to project");
			HLog.il("copyProjectLog: src file:" + projectSrcPath);
			HLog.il("copyProjectLog: project path:" + projectPath);
			retValue = copyProjectLogFile(inputLogFileKeyWords, projectPath, projectSrcPath);
			
			if (!retValue) {
				JOptionPane.showMessageDialog(null, "������־�ļ����̳��ִ���", "����", JOptionPane.ERROR_MESSAGE);
			}
			
			return retValue;
		}
		
		// 2������֧�ֵ���־���͵��ƶ����ļ���
		if (srcFile.isDirectory()) {
			HLog.il("copyProjectLog: copy file to project");
			HLog.il("copyProjectLog: src file:" + projectSrcPath);
			HLog.il("copyProjectLog: project path:" + projectPath);
			if (!copyProjectLogFolder(inputLogFileKeyWords, projectPath, projectSrcPath)) {
				HLog.el("copyProjectLog: copy src log folder fail:" + projectSrcPath);
				HLog.el("copyProjectLog: projectPath:" + projectPath);
				JOptionPane.showMessageDialog(null, "������־�ļ����̳��ִ���", "����", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		
		return true;
	}
	
	private boolean unCompressLogFiles(String projectPath) {
		
		createProjectProgressDialog.setProgressModel(true);
		createProjectProgressDialog.setMessage("���ڽ�ѹ���ļ������Ե�...");
		// 2����ȡÿһ��Ŀ¼�е�ѹ���ļ�������ѹ��
		while (true) {
			// ��⵱ǰĿ¼���Ƿ���ѹ���ļ��������ѹ���ļ����ͼ���ִ�У����û��ѹ���ļ���������
			ArrayList<File> fileList = FileAdapter.getFileList(projectPath);
			boolean havaUnCompressedLogFile = false;
			for (int j = 0; j < fileList.size(); j++) {
				if (fileList.get(j).getName().endsWith(".tar.gz")) {
					havaUnCompressedLogFile = true;
					break;
				}
			}
			
			if (!havaUnCompressedLogFile) {
				break;
			}
			
			// ��ѹ��ѹ���ļ�
			HLog.il("Unzip:" + projectPath);
			GZip.unTargzFolder(projectPath);
		}
		
		return true;
	}
	
	private boolean copyProjectLogFolder(String logFileKeyWords, String projectPath, String projectSrcPath) {
		createProjectProgressDialog.setProgress(0);
		createProjectProgressDialog.setMessage("�����ļ�������:");
		
		// 1����ȡԴ��־Ŀ¼�µ��ļ����б�
		ArrayList<File> srcFileList = FileAdapter.getFileList(projectSrcPath);
		if (srcFileList == null || srcFileList.size() <= 0) {
			HLog.el("copyProjectLogFolder: src path is not exist: " + projectSrcPath);
			JOptionPane.showMessageDialog(null, "ָ��Ŀ¼Ϊ�գ�" + projectSrcPath, "����", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// 2��ȥ����֧�ֵ��ļ�
		int fileCount = srcFileList.size();
		for (int i = fileCount - 1; i >= 0; i--) {
			if (srcFileList.get(i).getName().indexOf(logFileKeyWords) < 0) {
				srcFileList.remove(i);
			}
		}
		
		if (srcFileList.size() <= 0) {
			HLog.el("copyProjectLogFolder: no input log in src path: " + projectSrcPath);
			JOptionPane.showMessageDialog(null, "ָ��Ŀ¼��û���ҵ� input ��־�ļ���" + projectSrcPath, "����", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// ����֧�ֵ��ļ�������Ŀ¼
		String projectFilePath = getProjectFilePath();
		String srcFilePath = null;
		String filePathNoSrcFolder = null;
		String fileInProjectPath = null;
		for (int i = 0; i < srcFileList.size(); i++) {
			srcFilePath = srcFileList.get(i).getPath();
			filePathNoSrcFolder = srcFilePath.substring(projectSrcPath.length());
			fileInProjectPath = FileAdapter.pathCat(projectFilePath, filePathNoSrcFolder);
			
			createProjectProgressDialog.setMessage("�����ļ�:" + srcFilePath);
			int progress = (int)((i + 1) * 100.f / srcFileList.size());
			createProjectProgressDialog.setProgress(progress);
			
			if (!FileAdapter.copyFile(srcFilePath, fileInProjectPath)) {
				HLog.el("copyProjectLogFolder: copy file fail, from: " + srcFilePath);
				HLog.el("copyProjectLogFolder: copy file fail,   to: " + fileInProjectPath);
				JOptionPane.showMessageDialog(null, "�ļ�����ʧ�ܣ�" + srcFilePath, "����", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		
		createProjectProgressDialog.setProgress(100);
		return true;
	}
	
	private boolean copyProjectLogFile(String inputLogFileKeyWords, String projectPath, String projectSrcPath) {
		createProjectProgressDialog.setProgress(0);
		createProjectProgressDialog.setMessage("�����ļ�������:" + projectSrcPath);
		
		File srcFile = new File(projectSrcPath);
		if (!srcFile.isFile()) {
			JOptionPane.showMessageDialog(null, "ѡ��Ĳ����ļ�����ѡ���ļ���", "����", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// ������־�ļ�Ӧ�ÿ������ĸ�Ŀ¼
		String logFileFolderInProject = getProjectFilePath();
		
		boolean copyResult = true;
		copyResult = FileAdapter.copyFile(projectSrcPath, logFileFolderInProject);
		HLog.dl("copyProjectLogFile: copy result:" + copyResult);
		
		createProjectProgressDialog.setProgress(100);
		return copyResult;
	}
	
	public static String getInputLogFileKeyWords() {
		HConfig supportedLogTypeConfig = HConfig.getInstance(ProgramConfiguration.SUPPORTED_LOG_TYPE_CONFIG_FILE_PATH);
		if (supportedLogTypeConfig == null) {
			return null;
		}
		
		ArrayList<ConfigItem> configItems = supportedLogTypeConfig.getConfigItemList();
		if (configItems.size() > 1 || configItems.size() <= 0) {
			HLog.el("getSupportedLogType: supported log type config file error");
			return null;
		}
				
		return configItems.get(0).getValue();
	}
	
	private void cancleProjectCreate(String projectPath) {
		
		HLog.el("cancleProjectCreate: Create project error, clear");
		
		FileAdapter.deleteFolder(projectPath);
		
		this.projectName = null;
		this.projectSrcPath = null;
		this.projectPath = null;
	}
	
	private class CreateProjectRunnable implements Runnable {
		
		private Project inputProject = null;
		private String projectBasePath;
		private String projectName;
		private String projectSrcPath;
		
		public CreateProjectRunnable(String projectBasePath, String projectName, String projectSrcPath) {
			this.projectBasePath = projectBasePath;
			this.projectName = projectName;
			this.projectSrcPath = projectSrcPath;
		}
		
		public void run() {
			inputProject = createProject(projectBasePath, projectName, projectSrcPath);
			createProjectProgressDialog.setVisible(false);
		}
		
		public Project getInputProject() {
			return inputProject;
		}
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getProjectSrcPath() {
		return projectSrcPath;
	}

	public void setProjectSrcPath(String projectSrcPath) {
		this.projectSrcPath = projectSrcPath;
	}
	
	public String getProjectFilePathName() {
		return PROJECT_FILE_PATH_NAME;
	}
	
	public String getProjectFilePath() {
		return FileAdapter.pathCat(projectPath, PROJECT_FILE_PATH_NAME);
	}
	
	public String getEventDeviceMapFileName() {
		return DEVICE_EVENT_MAP_FILE_NAME;
	}
	
	public String getEventDeviceMapFilePath() {
		return FileAdapter.pathCat(projectPath, DEVICE_EVENT_MAP_FILE_NAME);
	}

	public String getMergedFilePath() {
		
		return FileAdapter.pathCat(projectPath, "./output/merged_log.ilg");
	}

	private EventDeviceMap[] loadEventDeviceMapMapFromConfigFile() {
		String eventDeviceMapConfigFile = getEventDeviceMapFilePath();
		HLog.dl("Project.loadEventDeviceMapMapFromConfigFile(): event device map configuration file: " + eventDeviceMapConfigFile);
		HConfig eventDeviceMapHConfig = HConfig.getInstance(eventDeviceMapConfigFile);
		if (eventDeviceMapHConfig == null) {
			return null;
		}
		
		ArrayList<ConfigItem> eventDeviceMapConfigs = eventDeviceMapHConfig.getConfigItemList();
		EventDeviceMap[] eventDeviceMaps = new EventDeviceMap[eventDeviceMapConfigs.size()];
		for (int i = 0; i < eventDeviceMapConfigs.size(); i++) {
			ConfigItem mapConfig = eventDeviceMapConfigs.get(i);
			EventType eventType = EventType.valueOf(mapConfig.getName());
			if (eventType == null) {
				HLog.el("getEventDeviceMap: undefined EventType:" + mapConfig.getName());
				continue;
			}
			
			eventDeviceMaps[i] = new EventDeviceMap(mapConfig.getValue(), eventType);
		}
		
		if (eventDeviceMaps.length <= 0) {
			return null;
		} else {
			return eventDeviceMaps;
		}
	}
	
	/**
	 * @description Create event and device maps. </br>
	 * The program will check is event device maps is exist, if exist, return immediately. </br>
	 * If not exist, the program will try to load event device maps from configuration file. </br>
	 * If configuration file is not exist, program will create event device maps and create configuration file. </br>
	 * @return 
	 * If success, return event device maps. </br> 
	 * If fail, return null. </br>
	 */
	public EventDeviceMap[] getEventDeviceMaps() {
		
		HLog.dl("Project.getEventDeviceMaps()");
		// 1. check if map is null
		if (eventDeviceMaps != null) {
			return eventDeviceMaps;
		}
		
		// 2. load map from config file
		eventDeviceMaps = loadEventDeviceMapMapFromConfigFile();
		if (eventDeviceMaps != null) {
			HLog.dl("Project.getEventDeviceMaps(): get event device maps from configuration file");
			return eventDeviceMaps;
		}
		
		// 3. create eventDeviceMaps
		HLog.dl("Project.getEventDeviceMaps(): create event device maps by project");
		ProjectLoader projectLoader = new ProjectLoader();
		eventDeviceMaps = projectLoader.getEventDeviceMapsWithProgress(this);
		
		HLog.dl("Project.getEventDeviceMaps(): eventDeviceMaps.length=" + eventDeviceMaps.length);
		for (int i = 0; i < eventDeviceMaps.length; i++) {
			HLog.dl("Project.getEventDeviceMaps(): eventDeviceMaps[" + i + "]=" + eventDeviceMaps[i].toString());
		}
		
		// 4. write event device map to configuration file
		HConfig eventDeviceMapConfig = HConfig.getInstance(getEventDeviceMapFilePath(), true);
		eventDeviceMapConfig.clear();
		for (int i = 0; i < eventDeviceMaps.length; i++) {
			ConfigItem configItem = new ConfigItem();
			configItem.setName(eventDeviceMaps[i].getEventType().toString());
			configItem.setValue(eventDeviceMaps[i].getDeviceName());
			eventDeviceMapConfig.addConfigItem(configItem);
		}
		
		return eventDeviceMaps;
	}
	
	//  for debug	
//	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		} catch (ClassNotFoundException | InstantiationException
//				| IllegalAccessException | UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}
//		
//		HLog.addHLogInterface(new HCmdLog());
//		
//		new InputProject().createProjectWithProgress("D:/project/", false);
//	}
}
