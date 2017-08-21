package cn.hisdar.touchpaneltool.ui.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.FileAdapter;
import cn.hisdar.touchpaneltool.common.GZip;
import cn.hisdar.touchpaneltool.setting.AnalysisSettingPanel;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingPanel;
import cn.hisdar.touchpaneltool.ui.WaitWork;
import cn.hisdar.touchpaneltool.ui.WaitWorkTaskInterface;

public class ProjectLogFileInit implements WaitWorkTaskInterface {
	public static final String PROJECT_FOLDER_NAME = "project";
	public static final String INPUT_LOG_FOLDER_NAME = "input";
	public static final String KERNEL_LOG_FOLDER_NAME = "kmsgcat";
	public static final String RESUME_SUSPEND_FILE_NAME = "resumeAndSuspendInfo.log";
	
	private static final String PROJECT_FOLDER = "project_folder_";
	private String currentProjectPath;
	
	private HConfig environmentConfig;
	private HConfig analysisConfig;
	
	private String projectName;
	private String srcLogPath;
	
	private WaitWork addProjectWaitWork;
	
	public ProjectLogFileInit() {
		
		environmentConfig = HConfig.getInstance(EnvironmentSettingPanel.ENVIRONMENT_CONFIG_FILE);
		analysisConfig = HConfig.getInstance(AnalysisSettingPanel.ANALYSIS_SETTING_CONFIG_FILE);
		
		addProjectWaitWork = new WaitWork(false);
	}
	
	public void initLogFiles(String projectName, String logPath) {
		
		this.projectName = projectName;
		this.srcLogPath = logPath;
		
		addProjectWaitWork.setWorkTask(this);
		addProjectWaitWork.startWaitWork();
	}
	
	private boolean addProject(String projectName, String srcPath) {
		
		// Step 1: create project folder
		// get workspace path
		addProjectWaitWork.setRange(0, 6);
		addProjectWaitWork.setProgress(0);
		addProjectWaitWork.setMessage("初始化工程");
		String workspacePath =  environmentConfig.getConfigValue(EnvironmentSettingPanel.WORKSPACE_PATH_CONFIG_NAME);
		if (null == workspacePath) {
			workspacePath = EnvironmentSettingPanel.DEFAULT_WORKSPACE_PATH;
		}
		
		// get project folder path
		String projectPath = FileAdapter.pathCat(workspacePath, PROJECT_FOLDER_NAME);
		
		// get current project path
		String projectNameHead = getProjectFolderHead();
		String currentProjectPath = FileAdapter.pathCat(projectPath, projectNameHead + projectName);
		this.currentProjectPath = currentProjectPath;
		
		// get input log path in current project 
		String inputLogFolder = FileAdapter.pathCat(currentProjectPath, INPUT_LOG_FOLDER_NAME);
		
		// get kernel log path in current project
		String kernelLogFolder = FileAdapter.pathCat(currentProjectPath, KERNEL_LOG_FOLDER_NAME);
		
		// create folders
		if (!FileAdapter.initFolder(inputLogFolder)) {
			HLog.el("Fail to create folder:" + inputLogFolder);
			return false;
		}
		
		if (!FileAdapter.initFolder(kernelLogFolder)) {
			HLog.el("Fail to create folder:" + kernelLogFolder);
			return false;
		}
		
		// get input log file name keyword
		String inputFileNameKeyword = analysisConfig.getConfigValue(AnalysisSettingPanel.INPUT_LOG_KEY_WORD_CONFIG_NAME);
		if (inputFileNameKeyword == null) {
			inputFileNameKeyword = AnalysisSettingPanel.INPUT_LOG_KEY_WORD;
		}
		
		// get kernel log file name keyword
		String kernelLogFileKeyword = analysisConfig.getConfigValue(AnalysisSettingPanel.KERNEL_LOG_KEY_WORD_CONFIG_NAME);
		if (kernelLogFileKeyword == null) {
			kernelLogFileKeyword = AnalysisSettingPanel.KERNEL_LOG_KEY_WORD;
		}
		
		addProjectWaitWork.setProgress(1);
		addProjectWaitWork.setMessage("复制Input日志文件");
		// Step 2: copy log file to current project folder
		File srcFile = new File(srcPath);
		if (srcFile.isDirectory()) {
			if (!copyLogFiles(srcPath, inputLogFolder, inputFileNameKeyword)) {
				return false;
			}
		} else {
			if (!FileAdapter.copyFile(srcPath, inputLogFolder, true)) {
				return false;
			}
		}
		
		addProjectWaitWork.setProgress(2);
		addProjectWaitWork.setMessage("复制Kernel日志文件");
		if (srcFile.isDirectory()) {
			if (!copyLogFiles(srcPath, kernelLogFolder, kernelLogFileKeyword)) {
				return false;
			}
		} else {
			if (!copyLogFiles(srcFile.getParent(), kernelLogFolder, kernelLogFileKeyword)) {
				return false;
			}
		}
		
		// Step 3: unzip log files
		addProjectWaitWork.setProgress(3);
		addProjectWaitWork.setMessage("解压缩Input日志文件");
		GZip.unTargzFolder(inputLogFolder);
		
		addProjectWaitWork.setProgress(4);
		addProjectWaitWork.setMessage("解压缩Kernel日志文件");
		GZip.unTargzFolder(kernelLogFolder);

		// Step 4: analysis resume and suspend information
		// get resume and suspend keyword
		addProjectWaitWork.setProgress(5);
		addProjectWaitWork.setMessage("分析休眠唤醒信息");
		String resumeKeyword = analysisConfig.getConfigValue(AnalysisSettingPanel.RESUME_KEY_WORD_CONFIG_NAME);
		String suspendKeyword = analysisConfig.getConfigValue(AnalysisSettingPanel.SUSPEND_KEY_WORD_CONFIG_NAME);
		if (resumeKeyword == null || suspendKeyword == null) {
			return true;
		}
		
		// get resume and suspend information file path
		String resumeAndSuspendFilePath = FileAdapter.pathCat(kernelLogFolder, RESUME_SUSPEND_FILE_NAME);
		analysisKernelLogFile(kernelLogFolder, resumeAndSuspendFilePath, resumeKeyword, suspendKeyword);
		
		addProjectWaitWork.setProgress(6);
		addProjectWaitWork.setMessage("日志文件添加完成");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		
		return true;
	}
	
	private boolean analysisKernelLogFile(String srcPath, String targetPath, String resumeKeyword, String suspendKeyword) {
		
		File kernelLogFolder = new File(srcPath);
		if (!kernelLogFolder.isDirectory()) {
			HLog.el("Not a folder:" + srcPath);
			return false;
		}
		
		File[] kernelLogFiles = kernelLogFolder.listFiles();
		if (kernelLogFiles == null || kernelLogFiles.length <= 0) {
			HLog.el("No kernel log file found at:" + srcPath);
			return false;
		}
		
		// sort kernel log files and get resume and suspend information
		BufferedWriter writer = null;
		BufferedReader reader = null;
		try {
			writer = new BufferedWriter(new FileWriter(targetPath));
		
			kernelLogFiles = sortLogFiles(kernelLogFiles);
			for (int i = 0; i < kernelLogFiles.length; i++) {
				if (kernelLogFiles[i].isDirectory()) {
					File[] childKernelLogFiles = kernelLogFiles[i].listFiles();
					if (childKernelLogFiles == null || childKernelLogFiles.length <= 0) {
						continue;
					}
					
					childKernelLogFiles = sortLogFiles(childKernelLogFiles);
					for (int j = 0; j < childKernelLogFiles.length; j++) {
						reader = new BufferedReader(new FileReader(childKernelLogFiles[j]));
						if (!parseResumeSuspendEvent(reader, writer, resumeKeyword, suspendKeyword)) {
							HLog.el("parse resume and suspend event fail");
							return false;
						}
						
						reader.close();
						FileAdapter.deleteFile(childKernelLogFiles[j].getPath());
					}
					
					FileAdapter.deleteFolder(kernelLogFiles[i].getPath());
				} else if (kernelLogFiles[i].isFile()) {
					reader = new BufferedReader(new FileReader(kernelLogFiles[i]));
					if (!parseResumeSuspendEvent(reader, writer, resumeKeyword, suspendKeyword)) {
						HLog.el("parse resume and suspend event fail");
						return false;
					}
					
					reader.close();
					FileAdapter.deleteFile(kernelLogFiles[i].getPath());
				} 
			}
		
			writer.flush();
			writer.close();
		} catch (IOException e) {
			HLog.el(e);
			HLog.el(e.getLocalizedMessage());
		}
		
		return true;
	}
	
	private String getProjectFolderHead() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String folderHead = PROJECT_FOLDER + dateFormat.format(new Date()) + "_";
		
		return folderHead;
	}
	
	/**
	 * @description serche resume and suspend information in reader, add exchange to event and write to writer
	 * @param reader
	 * @param writer
	 * @param resumeKeyword
	 * @param suspendKeyword
	 * @return 
	 */
	private boolean parseResumeSuspendEvent(BufferedReader reader, BufferedWriter writer, 
			String resumeKeyword, String suspendKeyword) {

		try {
			String lineString = reader.readLine();
			while (lineString != null) {
				
				String systemTime = getKernelSystemTime(lineString);
				String bootUpTime = getKernelBootUpTime(lineString);
				if (systemTime != null && bootUpTime != null) {
					bootUpTime = "               " + bootUpTime;
					bootUpTime = bootUpTime.substring(bootUpTime.length() - 15);
					
					String resumeSuspendEvent = "[" + systemTime + " ][" + bootUpTime + "] /dev/input/eventX: EV_ABS       ";
					if (lineString.indexOf(resumeKeyword) >= 0) {
						// change log to event information
						resumeSuspendEvent += "RESUME_EVENT" ;
						writer.write(resumeSuspendEvent + "\n"); 
					} else if (lineString.indexOf(suspendKeyword) >= 0) {
						resumeSuspendEvent += "SUSPEND_EVENT" ;
						writer.write(resumeSuspendEvent + "\n");
					} else {
						
					}
				} else {
					HLog.dl("system time or boot up time is null");
				}
				
				lineString = reader.readLine();
			}
		} catch (IOException e) {
			HLog.el(e);
			return false;
		}
		
		return true;
	}
	
	private String getKernelBootUpTime(String lineString) {
		int startIndex = lineString.indexOf("][") + 2;
		if (startIndex < 0) {
			return null;
		}
		
		int endIndex = lineString.indexOf(']', startIndex);
		if (endIndex < 0) {
			return null;
		}
		
		return lineString.substring(startIndex, endIndex).trim();
	}
	
	private String getKernelSystemTime(String lineString) {
		int endIndex = lineString.indexOf('<');
		if (endIndex > 0) {
			return lineString.substring(0, endIndex).trim();
		}
		
		return null;
	}
	
	/**
	 * @description sort log files by log file create time
	 * @param files files to sort
	 * @return the sorted file list
	 */
	public static File[] sortLogFiles(File[] files) {
		
		int exchangeIndex = 0;
		for (int i = 0; i < files.length; i++) {
			exchangeIndex = i;
			for (int j = i + 1; j < files.length; j++) {
				if (files[j].isDirectory() && files[exchangeIndex].isFile()){
					exchangeIndex = j;
				} else if (files[j].isDirectory() && files[exchangeIndex].isDirectory()) {
					if (files[j].getPath().length() > files[exchangeIndex].getPath().length()) {
						exchangeIndex = j;
					} else if (files[j].getPath().length() == files[exchangeIndex].getPath().length()) {
						if (files[j].getPath().compareTo(files[exchangeIndex].getPath()) > 0) {
							exchangeIndex = j;
						}
					}
				} else if (files[j].isFile() && files[exchangeIndex].isFile()) {
					if (files[j].getPath().length() > files[exchangeIndex].getPath().length()) {
						exchangeIndex = j;
					} else if (files[j].getPath().length() == files[exchangeIndex].getPath().length()) {
						if (files[j].getPath().compareTo(files[exchangeIndex].getPath()) > 0) {
							exchangeIndex = j;
						}
					}
				}
			}
			
			if (exchangeIndex != i) {
				File exchangeFile = files[i];
				files[i] = files[exchangeIndex];
				files[exchangeIndex] = exchangeFile;
			}
		}
		
		return files;
	}
	
	/**
	 * @description copy log file form srcFolder to targetFolder, the file name must include filter
	 * @param srcFolder
	 * @param targetFolder
	 * @param filter
	 * @return success return true, fail return false
	 */
	private boolean copyLogFiles(String srcFolder, String targetFolder, String filter) {
		File srcFile = new File(srcFolder);
		File[] srcLogFileList = srcFile.listFiles();
		if (srcLogFileList == null || srcLogFileList.length <= 0) {
			HLog.el("No log file found !");
			return false;
		}
		
		for (int i = 0; i < srcLogFileList.length; i++) {
			if (srcLogFileList[i].getName().indexOf(filter) < 0) {
				// this file is not a target log file
				continue;
			}
			
			if (srcLogFileList[i].isFile()) {
				if (!FileAdapter.copyFile(srcLogFileList[i].getPath(), targetFolder, true)) {
					HLog.el("Fail to copy file :" + srcLogFileList[i].getPath() + " to " + targetFolder);
					return false;
				}
			} else if (srcLogFileList[i].isDirectory()) {
				if (!FileAdapter.copyFolder(srcLogFileList[i].getPath(), targetFolder, true)) {
					HLog.el("Fail to copy folder :" + srcLogFileList[i].getPath() + " to " + targetFolder);
					return false;
				}
			} else {
				HLog.el("File not be copied :" + srcLogFileList[i].getPath());
			}
		}
		
		return true;
	}
	
	public String getProjectPath() {
		return currentProjectPath;
	}
	
	@Override
	public void waitWorkTask() {
		if (!addProject(projectName, srcLogPath)) {
			JOptionPane.showMessageDialog(null, 
					"工程添加失败！\n"
					+ "可能的原因是：\n"
					+ "1、日志目录不存在；\n"
					+ "2、所选择的目录中没有日志文件；\n"
					+ "3、磁盘空间不足；\n"
					+ "4、文件读写失败；", 
					"错误", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void taskFinishCallBack() {
		
	}
}
