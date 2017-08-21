package cn.hisdar.input2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.lib.adapter.FileAdapter;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.ui.HProgressDialog;

public class InputLogFormat {

	private static final String LOG_FILE_FORMAT_TEMP_FILE = "./buffer/logFileFormat.tmp";
	
	public enum LogType {
		LOG_TYPE_STANDARD,
		LOG_TYPE_SIMPLIFICATION,
	}
	
	private LogType logType = LogType.LOG_TYPE_SIMPLIFICATION;
	private String historyDeviceName = null;
	private HProgressDialog progressDialog = null;

	// for debug
//	public static void main(String[] args) {
//		HLog.addHLogInterface(new HCmdLog());
//		new InputLogFormat().formateInputLogWithProgress("E:\\Workspace\\EclipseWorkspace\\Classic"
//				+ "\\gitServer\\TouchPanelTool\\Project\\project_folder_2015_08_28_21_37_37_MultiTouchEvent-B-FULL\\input");
//		new InputLogFormat().formateInputLogWithProgress("D:\\Abdroid_Debug\\new_input_log\\");
//		System.exit(0);
//	}
	
	public InputLogFormat() {
		progressDialog = new HProgressDialog();
		progressDialog.setLogo(new ImageIcon("./Image/VersionDialogLogo.png"));
	}
	
	public void formateInputLogWithProgress(String srcPath) {
		
		FormatInputLogThread formatInputLogThread = new FormatInputLogThread(srcPath);
		formatInputLogThread.start();
		progressDialog.setModal(true);
		progressDialog.setVisible(true);
	}
	
	public void formateInputLogUseThread(String srcPath) {
		FormatInputLogThread formatInputLogThread = new FormatInputLogThread(srcPath);
		formatInputLogThread.start();
	}
	
	public boolean formateInputLog(String srcPath) {
		
		HLog.il("formateToStandardInputLog: srcPath=" + srcPath);
		
		// 1����ȡinput�ļ��б�
		ArrayList<File> inputFiles = FileAdapter.getFileList(srcPath);
		inputFiles = sortLogFiles(inputFiles);
		
		// 2����Դ�ļ��ŵ�һ���ļ��У��������ļ���Ϣ�Ķ��ձ�
		if (!FileAdapter.initFile(LOG_FILE_FORMAT_TEMP_FILE)) {
			HLog.el("formateToStandardInputLog: init temp file faile:" + LOG_FILE_FORMAT_TEMP_FILE);
			clearWorkSpace();
			return false;
		}
		
		// 3�����log�Ƿ��Ǳ�׼��log������ǣ�ֱ�ӷ���
		ArrayList<SourceFileInformation> inputLogFileInformations = createFormatTempFile(inputFiles, LOG_FILE_FORMAT_TEMP_FILE);
		if (logType == LogType.LOG_TYPE_STANDARD) {
			HLog.il("formateToStandardInputLog: log is standard log, no need to format");
			clearWorkSpace();
			return true;
		}
		
		// 4����ʽ���Ǳ�׼log����д�ص�logԴ�ļ�
		if (formatInputLogFiles(inputFiles, inputLogFileInformations)) {
			clearWorkSpace();
			return false;
		}
		
		clearWorkSpace();
		return true;
	}
	
	private void clearWorkSpace() {
		new File(LOG_FILE_FORMAT_TEMP_FILE).delete();
		progressDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		progressDialog.setVisible(false);
	}
	
	private boolean formatInputLogFiles(ArrayList<File> inputFiles, ArrayList<SourceFileInformation> inputLogFileInformations) {
		// ��ʼ���Ի��򲿷�
		progressDialog.setProgress(0);
		progressDialog.setMessage("��ʽ��Input Log");
		
		try {
			BufferedReader formatTempFileReader = new BufferedReader(new FileReader(new File(LOG_FILE_FORMAT_TEMP_FILE)));
			String lineString = null;
			String last1LineString = null;
			lineString = formatTempFileReader.readLine();
			for (int i = 0; i < inputFiles.size(); i++) {
				long logFileLineCount = getFileLineCount(inputLogFileInformations, inputFiles.get(i).getPath());
				if (logFileLineCount < 0) {
					HLog.el("formateToStandardInputLog: format log file fail:" + inputFiles.get(i).getPath());
					break;
				}
				
				HLog.il("formateToStandardInputLog:format file:" + inputFiles.get(i).getPath());
				FileOutputStream fileOutputStream = new FileOutputStream(inputFiles.get(i));
				String formatedLineString = null;
				while (lineString != null && logFileLineCount > 0) {
					formatedLineString = formatLineString(lineString, last1LineString);
					// for debug
					//formatedLineString = lineString + "\n";
					if (formatedLineString != null) {
						formatedLineString += "\n";
						fileOutputStream.write(formatedLineString.getBytes());
					} else {
						lineString += "\n";
						fileOutputStream.write(lineString.getBytes());
					}
					
					logFileLineCount -= 1;
					
					// �洢��һ������
					last1LineString = lineString;
					lineString = formatTempFileReader.readLine();
				}
				
				fileOutputStream.flush();
				fileOutputStream.close();
				
				// ���ò�������
				float progress = i * 100.0f / inputFiles.size();
				progressDialog.setProgress((int)progress);
				progressDialog.setMessage("��ʽ��Input Log - " + progress + "%");
			}
			
			formatTempFileReader.close();
			
			progressDialog.setProgress(100);
			progressDialog.setMessage("��ʽ��Input Log - 100%");
		} catch (IOException e) {
			HLog.el(e);
			return false;
		}
		
		return true;
	}
	
	private String formatLineString(String currentLineString, String last1LineString) {
		
		// 1���滻����д������
		String formatedLineString = replaceAcronymedEvent(currentLineString);
		String formatedLast1Line = replaceAcronymedEvent(last1LineString);
		
		// 2������input�豸��
		formatedLineString = insertInputDeviceName(formatedLineString, formatedLast1Line);
		//HLog.il(formatedLineString);
		return formatedLineString;
	}

	private String replaceAcronymedEvent(String currentLineString) {
		String acronymedEventName =  getAcronymedEventName(currentLineString);
		if (acronymedEventName == null) {
			HLog.il("replaceAcronymedEvent: incomplete log line:" + currentLineString);
			return null;
		}
		
		String fullEventName = new AcronymTable().getFullName(acronymedEventName);
		if (fullEventName == null) {
			HLog.il("replaceAcronymedEvent: incomplete log line:" + currentLineString);
			HLog.il("replaceAcronymedEvent: acronym event name not in map:" + acronymedEventName);
			return null;
		}
		
		// ��ʽ��һ��fullEventName�ĳ���(������21)
		fullEventName += "                     ";
		fullEventName = fullEventName.substring(0, 21);
		
		return currentLineString.replaceAll(acronymedEventName, fullEventName);
	}
	
	private class FormatInputLogThread extends Thread {

		private String srcPath;
		
		public FormatInputLogThread(String srcPath) {
			this.srcPath = srcPath;
		}
		
		@Override
		public void run() {
			formateInputLog(srcPath);
		}
	}

	private String insertInputDeviceName(String currentLineString, String last1LineString) {
		
		String eventName = getAcronymedEventName(currentLineString);
		if (eventName == null) {
			HLog.il("insertInputDeviceName: get event name fail:" + currentLineString);
			return null;
		}
		
		String deviceName = null;
		if (eventName.equals(MultiTouchPoint.SYN_REPORT)) {
			// ����һ�л�ȡdevice name
			eventName = getAcronymedEventName(last1LineString);
			if (eventName == null) {
				HLog.il("insertInputDeviceName: get event name fail:" + last1LineString);
				return null;
			}
			
			// ����һ���¼���Ѱ��
			deviceName = new EventDeviceTable().getDeviceName(eventName);
			// �����һ���¼���Ҳ�Ҳ�������ô����ʷ���豸��
			if (deviceName == null) {
				deviceName = historyDeviceName;
			}
		} else {
			deviceName = new EventDeviceTable().getDeviceName(eventName);
		}
		
		// �Ҳ����Ļ��������ϴε�
		if (deviceName == null) {
			HLog.il("insertInputDeviceName: get device name fail:" + currentLineString);
			HLog.il("insertInputDeviceName: get device name fail:" + last1LineString);
			HLog.il("insertInputDeviceName: get device name fail:" + eventName);
		} else {
			historyDeviceName = deviceName;
		}
		
		// 1���ҵ����һ��] �� ] �ĺ�������豸��
		int insertIndex = currentLineString.lastIndexOf(']');
		if (insertIndex < 0) {
			insertIndex = currentLineString.length();
		} else {
			insertIndex += 1;
		}
		
		String formatedLineString = currentLineString.substring(0, insertIndex) + " ";
		formatedLineString = formatedLineString + deviceName + ":";
		formatedLineString += currentLineString.substring(insertIndex);
		return formatedLineString;
	}

	// ��ȡ��дlog�е�event
	private String getAcronymedEventName(String lineString) {
		if (lineString == null) {
			return null;
		}
		
		int startIndex = lineString.lastIndexOf(']');
		if (startIndex < 0) {
			return null;
		}
		
		String acronymedEventName = lineString.substring(startIndex + 1).trim();
		if (acronymedEventName.length() == 0) {
			return null;
		}
		
		int endIndex = acronymedEventName.indexOf(' ');
		if (endIndex < 0) {
			endIndex = acronymedEventName.length();
		}
		
		return acronymedEventName.substring(0, endIndex).trim();
	}
	
	private ArrayList<SourceFileInformation> createFormatTempFile(ArrayList<File> inputFiles, String tempFilePath) {
		
		// ��ʼ���Ի��򲿷�
		progressDialog.setProgress(0);
		progressDialog.setMessage("������ʽ�������ļ�");
		
		ArrayList<SourceFileInformation> sourceFileInformations = new ArrayList<SourceFileInformation>();

		long fileLineCount = 0;
		for (int i = 0; i < inputFiles.size(); i++) {
			fileLineCount = fileAppend(new File(tempFilePath), inputFiles.get(i));
			SourceFileInformation sourceFileInformation = new SourceFileInformation();
			sourceFileInformation.srcFileLineCount = fileLineCount;
			sourceFileInformation.srcFilePath = inputFiles.get(i).getPath();
			
			sourceFileInformations.add(sourceFileInformation);
			
			// ���ò�������
			float progress = i * 100.0f / inputFiles.size();
			progressDialog.setProgress((int)progress);
			progressDialog.setMessage("������ʽ�������ļ� - " + progress + "%");
		}
		
		progressDialog.setProgress(100);
		progressDialog.setMessage("������ʽ�������ļ� - 100%");
		
		return sourceFileInformations;
	}
	
	private long getFileLineCount(ArrayList<SourceFileInformation> sourceFileInformations, String filePath) {
		for (int i = 0; i < sourceFileInformations.size(); i++) {
			if (sourceFileInformations.get(i).srcFilePath.equals(filePath)) {
				return sourceFileInformations.get(i).srcFileLineCount;
			}
		}
		
		return -1;
	}
	
	private long fileAppend(File targetFile, File srcFile) {
		long writeCount = 0;
		try {
			FileOutputStream targetFileOutputStream = new FileOutputStream(targetFile, true);
			BufferedReader srcReader = new BufferedReader(new FileReader(srcFile));
			String lineString = srcReader.readLine();
			while (lineString != null) {
				lineString += "\n";
				targetFileOutputStream.write(lineString.getBytes());
				writeCount += 1;
				
				lineString = srcReader.readLine();
				
				if (logType == LogType.LOG_TYPE_SIMPLIFICATION) {
					if (lineString != null && isStandardInputLog(lineString)) {
						logType = LogType.LOG_TYPE_STANDARD;
					}
				}
			}

			srcReader.close();
			
			targetFileOutputStream.flush();
			targetFileOutputStream.close();
		} catch (Exception e) {
			HLog.el("fileAppend: file append fail:" + srcFile.getPath());
			HLog.el(e);
		}
		
		return writeCount;
	}
	
	public static ArrayList<File> sortLogFiles(ArrayList<File> logFiles) {
		
		int miniIndex = 0;
		for (int i = 0; i < logFiles.size(); i++) {
			miniIndex = i;
			for (int j = i + 1; j < logFiles.size(); j++) {
				if (compareFilePath(logFiles.get(miniIndex), logFiles.get(j)) < 0) {
					miniIndex = j;
				}
			}
			
			if (miniIndex != i) {
				File exchangeFile = logFiles.get(i);
				logFiles.set(i, logFiles.get(miniIndex));
				logFiles.set(miniIndex, exchangeFile);
			}
		}
		
		return logFiles;
		
	}
	
	private static int compareFilePath(File file1, File file2) {
		// 1����ȡ�ļ���Ŀ¼�б�
		String[] file1Parens = getFileParentList(file1.getPath());
		String[] file2Parens = getFileParentList(file2.getPath());
		
		// 2��Ѱ��ͬ��Ŀ¼
		int sameParentIndex = -1;
		for (int i = 0; i < file2Parens.length && i < file1Parens.length; i++) {
			if (!file2Parens[i].equals(file1Parens[i])) {
				break;
			} else {
				sameParentIndex = i;
			}
		}
		
		// �ҵ���file1�Ľ�β, ˵��file2��Ŀ¼����, file2�Ĵ���ʱ�����
		if (sameParentIndex == file1Parens.length - 1 && sameParentIndex < file2Parens.length - 1) {
			return -1;
		}
		
		// �ҵ���file2�Ľ�β, ˵��file1��Ŀ¼����, file1�Ĵ���ʱ�����
		if (sameParentIndex < file1Parens.length - 1 && sameParentIndex == file2Parens.length - 1) {
			return 1;
		}
		
		// �����ļ����ҵ��˽�β����ôֱ�ӱȽ������ļ�
		if (sameParentIndex == file1Parens.length - 1 && sameParentIndex == file2Parens.length - 1) {
			// Ŀ¼�Ĵ���ʱ�������ļ�
			if (file1.isDirectory() && file2.isFile()) {
				return 1;
			}
			
			if (file2.isDirectory() && file1.isFile()) {
				return -1;
			}
			
			// �ļ��������ļ�������ʱ�����
			if (file1.getName().length() > file2.getName().length()) {
				return 1;
			} else if (file1.getName().length() < file2.getName().length()) {
				return -1;
			}
			
			// ֱ�ӱȽ��ļ����ַ���
			return file1.getName().compareTo(file2.getName());
		}
		
		// �����ļ���·����û���ҵ���β
		if (sameParentIndex < file1Parens.length - 1 && sameParentIndex < file2Parens.length - 1) {
			// �ļ��������ļ�������ʱ�����
			if (file1Parens[sameParentIndex + 1].length() > file2Parens[sameParentIndex + 1].length()) {
				return 1;
			} else if (file1Parens[sameParentIndex + 1].length() < file2Parens[sameParentIndex + 1].length()) {
				return -1;
			}
			
			// ֱ�ӱȽ��ļ����ַ���
			return file1Parens[sameParentIndex + 1].compareTo(file2Parens[sameParentIndex + 1]);
		}
		
		return 0;
	}
	
	private static String[] getFileParentList(String filePath) {
		
		int fileDeep = getLogFileDeep(filePath);
		String[] fileParents = new String[fileDeep];
		
		String fileParent = new File(filePath).getParent();
		for (int i = 0; i < fileDeep; i++) {
			fileParents[fileDeep - i - 1] = fileParent;
			fileParent = new File(fileParent).getParent();
		}
		
		return fileParents;
	}

	public static int getLogFileDeep(String logFilePath) {
		int logFileDeep = 0;
		
		File currentFile = new File(logFilePath);
		while (currentFile != null && currentFile.getParent() != null) {
			logFileDeep += 1;
			currentFile = new File(currentFile.getParent());
		}
		
		return logFileDeep;
	}
	
	private boolean isStandardInputLog(String line) {
		if (line.indexOf("ABS_MT_POSITION") >= 0 && line.indexOf("/dev/input/event") >= 0) {
			return true;
		}
		
		return false;
	}
	
	private class SourceFileInformation {
		public String srcFilePath;
		public long srcFileLineCount;
		
		public SourceFileInformation() {
			
		}
	}
}
