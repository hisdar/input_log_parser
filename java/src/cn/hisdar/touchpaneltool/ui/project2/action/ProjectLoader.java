package cn.hisdar.touchpaneltool.ui.project2.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import cn.hisdar.MultiTouchEventParse.Device;
import cn.hisdar.MultiTouchEventParse.Event;
import cn.hisdar.MultiTouchEventParse.EventDeviceMap;
import cn.hisdar.MultiTouchEventParse.EventType;
import cn.hisdar.lib.adapter.FileAdapter;
import cn.hisdar.lib.adapter.StringAdapter;
import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.work.HTask;
import cn.hisdar.lib.work.HTaskFinishInterface;
import cn.hisdar.lib.work.HWork;
import cn.hisdar.lib.work.TaskResult;
import cn.hisdar.touchpaneltool.ProgramConfiguration;
import cn.hisdar.touchpaneltool.ui.project2.Project;

// 1、按照文件生成的事件数据来合成日志文件
public class ProjectLoader implements HTaskFinishInterface {

	private MergeFilesWork mergeFilesWork;
	private int mergeFileReturnValue = 0;
	
	public ProjectLoader() {

	}
	
	public EventDeviceMap[] getEventDeviceMaps(Project project) {
		EventDeviceMapCreater eventDeviceMapCreater = new EventDeviceMapCreater(project);
		return eventDeviceMapCreater.getEventDeviceMaps();
	}
	
	public EventDeviceMap[] getEventDeviceMapsWithProgress(Project project) {
		
		EventDeviceMapCreater eventDeviceMapCreater = new EventDeviceMapCreater(project);
		return eventDeviceMapCreater.getEventDeviceMapsWithProgress();
	}
	
	public boolean loadProjectFile(Project project, String[] selectedFilePaths) {
		ArrayList<File> fileList = FileAdapter.getFileList(project.getProjectFilePath());
		if (fileList == null || fileList.size() <= 0) {
			return false;
		}
		
		File[] logFiles = new File[fileList.size()];
		for (int i = 0; i < logFiles.length; i++) {
			logFiles[i] = fileList.get(i);
		}
		 
		// 1、对文件进行排序
		logFiles = sortLogFiles(logFiles);
		
		// 2、生成event device 对照表
//		if (!createEventDeviceMap(project)) {
//			return false;
//		}
		
		// 3、对选中的文件进行排序
		File[] selectedFiles = sortLogFiles(selectedFilePaths);
		if (selectedFilePaths == null) {
			return false;
		}
		
		// 4、合并选中的文件
		String outputPath = project.getMergedFilePath();
		if (!mergeSelectedFiles(project, selectedFiles, outputPath)) {
			return false;
		}
		
		return true;
	}
	
	private boolean mergeSelectedFiles(Project project, File[] selectedFiles, String outputPath) {
		mergeFilesWork = new MergeFilesWork(project, selectedFiles, outputPath);
		HWork work = new HWork(mergeFilesWork);
		work.setTaskFinishInterface(this);
		work.setLogo(ProgramConfiguration.getDefaultDialogLogo());
		TaskResult taskResult = work.startWork();
		work.setTaskFinishInterface(null);
		if (taskResult == TaskResult.TASK_FAIL || mergeFileReturnValue != selectedFiles.length) {
			HLog.el("mergeSelectedFiles: merge log file fail");
			JOptionPane.showMessageDialog(null, "合并日志文件失败!", "错误", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}

//	public boolean createEventDeviceMap(Project project) {
//		
//		// 1、判断对照关系文件是否存在
//		String deviceEventMapPath = project.getEventDeviceMapFilePath();
//		File deviceEventMapFile = new File(deviceEventMapPath);
//		if (deviceEventMapFile.exists() && !deviceEventMapFile.isFile()) {
//			HLog.el("createDeviceEventMap: create device event map fail, path = " + deviceEventMapPath);
//			JOptionPane.showMessageDialog(null, "无法创建设备和事件的对应表，请不要随意修改工程目录", "错误", JOptionPane.ERROR_MESSAGE);
//			return false;
//		}
//		
//		if (!deviceEventMapFile.exists()) {
//			
//			CreateEventDeviceMapWork createEventDeviceMapWork = new CreateEventDeviceMapWork(project);
//			HWork createWork = new HWork(createEventDeviceMapWork);
//			createWork.setLogo(ProgramConfiguration.getDefaultDialogLogo());
//			TaskResult taskResult = createWork.startWork();
//			if (taskResult == TaskResult.TASK_FAIL) {
//				HLog.il("createEventDeviceMap: create event device map fail");
//				JOptionPane.showMessageDialog(null, "分析设备和事件的对应关系失败！", "错误", JOptionPane.ERROR_MESSAGE);
//				return false;
//			}
//			
//			ArrayList<EventDeviceMap> eventDeviceMap = createEventDeviceMapWork.getEventDeviceMap();
//			HConfig deviceEventMapConfig = HConfig.getInstance(project.getEventDeviceMapFilePath(), true);
//			if (deviceEventMapConfig == null) {
//				HLog.el("createDeviceEventMap: load device event map config file fail = " + project.getEventDeviceMapFilePath());
//				JOptionPane.showMessageDialog(null, "加载设备和事件的对应表文件失败", "错误", JOptionPane.ERROR_MESSAGE);
//				return false;
//			}
//			
//			for (int i = 0; i < eventDeviceMap.size(); i++) {
//				ConfigItem configItem = new ConfigItem();
//				configItem.setName(eventDeviceMap.get(i).getEventType().toString());
//				configItem.setValue(eventDeviceMap.get(i).getDeviceName());
//				deviceEventMapConfig.addConfigItem(configItem);
//			}
//		}
//		
//		return true;
//	}
	
	public static File[] sortLogFiles(String[] filePaths) {
		if (filePaths == null || filePaths.length == 0) {
			return null;
		}
		
		File[] files = new File[filePaths.length];
		for (int i = 0; i < filePaths.length; i++) {
			files[i] = new File(filePaths[i]);
		}
		
		return sortLogFiles(files);
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
	
	private class MergeFilesWork implements HTask {

		private File[] files;
		private String outputPath;
		private Project project;
		
		public MergeFilesWork(Project project, File[] files, String outputPath) {
			this.project = project;
			this.files = files;
			this.outputPath = outputPath;
		}
		
		@Override
		public int task(HWork work) {
			if (files == null || files.length == 0 || project == null) {
				return 0;
			}
			
			work.setMessage("日志文件合并");
			work.setProgressIndeterminate(true);
			
			// 计算日志文件的大小
			long logFileSize = 0;
			for (int i = 0; i < files.length; i++) {
				logFileSize += files[i].length();
			}
			
			// 创建输出文件
			if (!FileAdapter.initFile(outputPath)) {
				HLog.el("MergeFilesWork.task(): fail to create file:" + outputPath);
				return -2;
			}
			
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(outputPath);
			} catch (FileNotFoundException e) {
				HLog.el("MergeFilesWork.task(): fail to create output stream:" + outputPath);
				HLog.el("MergeFilesWork.task():" + e.getMessage());
				HLog.el("MergeFilesWork.task():" + StringAdapter.toString(e.getStackTrace()));
				return -1;
			}
			
			work.setProgressIndeterminate(false);
			long readCount = 0;
			int retValue = files.length;
			for (int i = 0; i < files.length; i++) {
				
				work.setMessage("合并文件：" + files[i].getPath().substring(project.getProjectFilePath().length()));
				
				try {
					HLog.il("MergeFilesWork.MergeFilesWork():" + files[i].getAbsolutePath());
					FileInputStream fileInputStream = new FileInputStream(files[i]);
					InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					
					String lineString = bufferedReader.readLine();
					while (lineString != null) {
						lineString += "\n";
						
						readCount += lineString.length();
						float progress = 1.0f * readCount / logFileSize;
						work.setProgressValue(progress > 1 ? 1 : progress);
						
						fileOutputStream.write(lineString.getBytes());
						lineString = bufferedReader.readLine();
					}
					
					bufferedReader.close();
					inputStreamReader.close();
					fileInputStream.close();
				} catch (Exception e) {
					HLog.el(e);
					retValue = -2;
					break;
				}
			}
			
			try {
				fileOutputStream.flush();
				fileOutputStream.close();
			} catch (IOException e) {
				retValue = -3;
				HLog.el(e);
			}
			
			return retValue;
		}
	}

	@Override
	public void taskFinishEvent(HTask task, int functionResult) {
		if (task == mergeFilesWork) {
			mergeFileReturnValue = functionResult;
		}
	}
}
