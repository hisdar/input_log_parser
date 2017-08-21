	package cn.hisdar.touchpaneltool.ui.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cn.hisdar.MultiTouchEventParse.Event;
import cn.hisdar.MultiTouchEventParse.EventDeviceMap;
import cn.hisdar.MultiTouchEventParse.EventParser;
import cn.hisdar.MultiTouchEventParse.EventType;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.FileAdapter;
import cn.hisdar.touchpaneltool.common.LogFileFactory;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingPanel;
import cn.hisdar.touchpaneltool.ui.WaitWork;
import cn.hisdar.touchpaneltool.ui.WaitWorkTaskInterface;

public class ProjectLoad implements WaitWorkTaskInterface {

	public static final String TOUCH_EVENT_LOG_FILE    = "buffer/touchEventFile";
	public static final String EVENT_LOG_FILE          = "buffer/eventFile";
	public static final String KEY_EVENT_BUFFER_FILE   = "buffer/keyEventFile";
	public static final String DEVICE_NAME_START       = "/dev/input/event";
	
	private HConfig environmentConfig;
	private WaitWork loadProjectWaitWork;
	private String projectPath;
	private String loadFilePath;
	private String eventLogFilePath;
	
	private String multiTouchDeviceName = null;
	private String powerKeyDeviceName   = null;
	private String voiceUpDeviceName    = null;
	private String voiceDownDeviceName  = null;
	private String resumeDeviceName  = null;
	private String suspendDeviceName  = null;
	
	public ProjectLoad() {
		environmentConfig = HConfig.getInstance(EnvironmentSettingPanel.ENVIRONMENT_CONFIG_FILE);
		loadProjectWaitWork = new WaitWork(false);
		loadProjectWaitWork.setWorkTask(this);
	}
	
	public void loadProject(String projectPath) {
		this.projectPath = projectPath;
		this.loadFilePath = FileAdapter.pathCat(projectPath, ProjectLogFileInit.INPUT_LOG_FOLDER_NAME);
		loadProjectWaitWork.startWaitWork();
	}
	
	public void loadProjectChildNode(String projectPath, String loadFilePath) {
		this.projectPath = projectPath;
		this.loadFilePath = loadFilePath;
		loadProjectWaitWork.startWaitWork();
	}
	
	private void loadProject_(String projectPath) {
		HLog.il("loadProject_: Loading project:" + projectPath);
		
		loadProjectWaitWork.setRange(0, 4);
		loadProjectWaitWork.setMessage("初始化工作目录[1/4]");
		loadProjectWaitWork.setProgress(0);
		
		// reset devices names
		multiTouchDeviceName = null;
		powerKeyDeviceName   = null;
		voiceUpDeviceName    = null;
		voiceDownDeviceName  = null;
		resumeDeviceName = null;
		suspendDeviceName = null;
		
		// get output file path
		String workspace =  environmentConfig.getConfigValue(EnvironmentSettingPanel.WORKSPACE_PATH_CONFIG_NAME);
		if (workspace == null) {
			workspace = EnvironmentSettingPanel.DEFAULT_WORKSPACE_PATH;
		}
		
		// input log file merged path
		String touchEventLogPath = FileAdapter.pathCat(workspace, TOUCH_EVENT_LOG_FILE);
		
		// resume and suspend event file path
		String resumeSuspendEventPath = FileAdapter.pathCat(projectPath, ProjectLogFileInit.KERNEL_LOG_FOLDER_NAME);
		resumeSuspendEventPath = FileAdapter.pathCat(resumeSuspendEventPath, ProjectLogFileInit.RESUME_SUSPEND_FILE_NAME);
		
		// all the event log and suspend/resume log will be merged to mainEventLogPath
		String mainEventLogPath = FileAdapter.pathCat(workspace, EVENT_LOG_FILE);
		eventLogFilePath = mainEventLogPath;
		
		loadProjectWaitWork.setMessage("合并Input日志文件[2/4]");
		loadProjectWaitWork.setProgress(1);
		// Step 1: merge input log file
		HLog.il("loadProject_: merge input log file");
		boolean retValue = mergeInputLogFile(loadFilePath, touchEventLogPath);
		if (!retValue) {
			HLog.el("loadProject_: Fail to mergeInputLogFile");
			return;
		}
		
		HLog.il("loadProject_: merge input log file success");
		
		loadProjectWaitWork.setMessage("合并Input日志文件和休眠唤醒信息[3/4]");
		loadProjectWaitWork.setProgress(2);
		// step 2: merge input log and resume/suspend event log
		// if resume/suspend event log file is not exist, do not do this step
		if (!new File(resumeSuspendEventPath).exists()) {
			// create a empty one for mergeInputFileAndResumeSuspendFile
			FileAdapter.initFile(resumeSuspendEventPath);
		} 
		
		HLog.il("loadProject_:merge resume and suspend log file");
		retValue = mergeInputFileAndResumeSuspendFile(touchEventLogPath, resumeSuspendEventPath, mainEventLogPath);
		if (!retValue) {
			HLog.il("Fail to mergeInputFileAndResumeSuspendFile");
			return;
		}
		
		HLog.il("loadProject_: delete buffer files");
		
		// step 3: delete buffer file
		loadProjectWaitWork.setMessage("删除缓存文件[3/4]");
		loadProjectWaitWork.setProgress(3);
		FileAdapter.deleteFile(touchEventLogPath);
		
		// step 4: set device and event maps
		setEventDeviceMaps();
		
		loadProjectWaitWork.setMessage("加载完成");
		loadProjectWaitWork.setProgress(4);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
	}
	
	private void setEventDeviceMap(String deviceName, EventType eventType) {
		EventDeviceMap touchEventDeviceMap = new EventDeviceMap(deviceName, eventType);
		EventParser.getInstance().addEventDeviceMap(touchEventDeviceMap);
		HLog.il("ProjectLoad.setEventDeviceMap():" + touchEventDeviceMap);
	}
	
	private void setEventDeviceMaps() {
		if (multiTouchDeviceName != null) {
			setEventDeviceMap(multiTouchDeviceName, EventType.EVENT_MULTI_TOUCH);
		}
		
		if (powerKeyDeviceName != null) {
			setEventDeviceMap(powerKeyDeviceName, EventType.EVENT_POWER_KEY);
		}
		
		if (voiceUpDeviceName != null) {
			setEventDeviceMap(voiceUpDeviceName, EventType.KEVENT_VOLUMEUP_KEY);
		}
		
		if (voiceDownDeviceName != null) {
			setEventDeviceMap(voiceDownDeviceName, EventType.EVEENT_VOLUMEDOWN_KEY);
			
		}
		
		if (resumeDeviceName != null) {
			setEventDeviceMap(resumeDeviceName, EventType.EVENT_RESUME);
		}
		
		if (suspendDeviceName != null) {
			setEventDeviceMap(suspendDeviceName, EventType.EVENT_SUSPEND);
		}
	}
	
	private void initInputDeviceName(String lineString) {
		
		if (multiTouchDeviceName == null) {
			multiTouchDeviceName = getInputDeviceName(lineString, MultiTouchPoint.ABS_MT_POSITION_X);
		}
		
		if (powerKeyDeviceName == null) {
			powerKeyDeviceName = getInputDeviceName(lineString, Event.KEY_POWER);
		}
		
		if (voiceDownDeviceName == null) {
			voiceDownDeviceName = getInputDeviceName(lineString, Event.KEY_VOLUMEDOWN);
		} 
		
		if (voiceUpDeviceName == null) {
			voiceUpDeviceName = getInputDeviceName(lineString, Event.KEY_VOLUMEUP);
		}
		
		if (resumeDeviceName == null) {
			resumeDeviceName = getInputDeviceName(lineString, Event.RESUME_EVENT);
		}
		
		if (suspendDeviceName == null) {
			suspendDeviceName = getInputDeviceName(lineString, Event.SUSPEND_EVENT);
		}
	}
	
	private boolean mergeInputFileAndResumeSuspendFile(String inputFile, String resumeSuspendFile, String mainEventLogPath) {
	
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(mainEventLogPath));
			BufferedReader inputLogReader = new BufferedReader(new FileReader(inputFile));
			BufferedReader resumeSuspendReader = new BufferedReader(new FileReader(resumeSuspendFile));
		
			// step 1 : every reader get a new line
			String inputLogLine = inputLogReader.readLine();
			String resumeSuspendLine = resumeSuspendReader.readLine();
		
			// 先定位input日志的当前时间
			String inputLogTime = null;
			while (inputLogLine != null) {
				inputLogTime = LogFileFactory.getEventLogSystemTime(inputLogLine);
				if (inputLogTime != null) {
					break;
				}
				
				inputLogLine = inputLogReader.readLine();
			}
			
			if (inputLogTime == null) {
				writer.close();
				inputLogReader.close();
				resumeSuspendReader.close();
				return false;
			}
			
			// 日志分析是以input为主的，首先要找到休眠和唤醒文件开始的位置，即休眠唤醒事件的开始位置不能比input事件的时间早
			String resumeSuspendTime = null;
			while (resumeSuspendLine != null) {
				// 将休眠唤醒事件的时间确定到input事件的时间之后
				resumeSuspendTime = LogFileFactory.getEventLogSystemTime(resumeSuspendLine);
				if (resumeSuspendTime != null) {
					if (inputLogTime.compareTo(resumeSuspendTime) <= 0) {
						break;
					}
				}
				
				resumeSuspendLine = resumeSuspendReader.readLine();
			}
			
			// 现在开始合并文件
			while (inputLogLine != null) {
				if (compareTime(inputLogLine, resumeSuspendLine) > 0) {
					initInputDeviceName(resumeSuspendLine);
					
					writer.write(resumeSuspendLine + "\n");
					resumeSuspendLine = resumeSuspendReader.readLine();
				} else {
					initInputDeviceName(inputLogLine);
					
					writer.write(inputLogLine + "\n");
					inputLogLine = inputLogReader.readLine();
				}
			}

			resumeSuspendReader.close();
			inputLogReader.close();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			HLog.el(e);
			return false;
		}
		
		return true;
	}
	
	private int compareTime(String eventLine1, String eventLine2) {
		String systemTime1 = LogFileFactory.getEventLogSystemTime(eventLine1);
		String systemTime2 = LogFileFactory.getEventLogSystemTime(eventLine2);
		
		if (systemTime1 == null && systemTime2 != null) {
			return -1;
		} else if (systemTime1 != null && systemTime2 == null) {
			return -1;
		} else if (systemTime1 == null && systemTime2 == null) {
			return 0;
		}
		
		int compareResult = systemTime1.compareTo(systemTime2);
		if (!systemTime1.equals(systemTime2)) {
			return compareResult;
		}
		
		double bootupTime1 = LogFileFactory.getEventLogBootupTime(eventLine1);
		double bootupTime2 = LogFileFactory.getEventLogBootupTime(eventLine2);
		if (bootupTime1 == -1 && bootupTime2 == -1) {
			return 0;
		} else if (bootupTime1 == -1 && bootupTime2 != -1) {
			return -1;
		} else if (bootupTime1 != -1 && bootupTime2 == -1) {
			return 1;
		}
		
		if (bootupTime1 > bootupTime2) {
			return 1;
		} else if (bootupTime1 < bootupTime2) {
			return -1;
		} else {
			return 0;
		}
	}
	
	private boolean mergeInputLogFile(String inputLogFilePath, String touchEventLogPath) {
		
		//check project folder 
		File[] inputLogFiles = null;
		HLog.il("ProjectLoad.mergeInputLogFile():" + inputLogFilePath);
		File inputLogFolder = new File(inputLogFilePath);
		HLog.il("ProjectLoad.mergeInputLogFile():" + inputLogFolder.isDirectory());
		if (inputLogFolder.isDirectory()) {
			inputLogFiles = inputLogFolder.listFiles();
		} else {
			inputLogFiles = new File[1];
			inputLogFiles[0] = inputLogFolder;
		}
		
		if (inputLogFiles == null || inputLogFiles.length <= 0) {
			HLog.el("No input file found at:" + inputLogFilePath);
			return false;
		}
		
		// clear buffer folder
		FileAdapter.deleteFolder(touchEventLogPath);
		FileAdapter.initFile(touchEventLogPath);
		
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(touchEventLogPath);
			HLog.il("write to:" + touchEventLogPath);
			FileInputStream  fileInputStream;
			
			// sort and merge files
			inputLogFiles = ProjectLogFileInit.sortLogFiles(inputLogFiles);
			for (int i = 0; i < inputLogFiles.length; i++) {
				HLog.il(inputLogFiles[i].getPath());
			}
			
			HLog.il("Sort input files");
			for (int i = 0; i < inputLogFiles.length; i++) {
				if (inputLogFiles[i].isDirectory()) {
					HLog.il("Merge:" + inputLogFiles[i].getPath());
					File[] childFiles = inputLogFiles[i].listFiles();
					childFiles = ProjectLogFileInit.sortLogFiles(childFiles);
					for (int j = 0; (childFiles != null) && (j < childFiles.length); j++) {
						HLog.il("Merge:" + childFiles[j].getPath());
						fileInputStream = new FileInputStream(childFiles[j]);
						if (!fileCopy(fileInputStream, fileOutputStream)) {
							HLog.el("Fail to merge file:" + childFiles[i].getPath());
						}
							
						fileInputStream.close();
					}
				} else {
					HLog.il("Merge:" + inputLogFiles[i].getPath());
					fileInputStream = new FileInputStream(inputLogFiles[i]);
					if (!fileCopy(fileInputStream, fileOutputStream)) {
						HLog.el("Fail to merge file:" + inputLogFiles[i].getPath());
					}
					
					fileInputStream.close();
				}
			}
			
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			HLog.el(e);
			return false;
		} catch (IOException e) {
			HLog.el(e);
			return false;
		}
		
		return true;
	}
	
	private boolean fileCopy(FileInputStream inputStream, FileOutputStream outputStream) {
		
		byte[] readBuffer = new byte[1024 * 4];
		
		try {
			int readCount = inputStream.read(readBuffer);
			while (readCount > 0) {
				outputStream.write(readBuffer, 0, readCount);
				readCount = inputStream.read(readBuffer);
			}
			
			outputStream.flush();
		} catch (IOException e) {
			HLog.el(e);
			return false;
		}
		
		return true;
	}
	
	private String getInputDeviceName(String lineString, String flag) {
		String deviceName = null;
		
		if (lineString.indexOf(flag) >= 0) {
			
			int deviceNameStartIndex = lineString.indexOf(DEVICE_NAME_START);
			int deviceNameEndIndex = 0;
			
			if (deviceNameStartIndex >= 0) {
				deviceNameEndIndex = lineString.indexOf(':', deviceNameStartIndex);
				if (deviceNameEndIndex >= 0) {
					deviceName = lineString.substring(deviceNameStartIndex, deviceNameEndIndex).trim();
				}
			}
		}
		
		return deviceName;
	}
	
	@Override
	public void waitWorkTask() {
		loadProject_(projectPath);
	}

	@Override
	public void taskFinishCallBack() {
		
	}
	
	public String getEventLogPath() {
		return eventLogFilePath;
	}
	
}
