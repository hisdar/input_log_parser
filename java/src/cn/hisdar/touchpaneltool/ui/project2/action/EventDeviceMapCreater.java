package cn.hisdar.touchpaneltool.ui.project2.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import cn.hisdar.MultiTouchEventParse.Device;
import cn.hisdar.MultiTouchEventParse.Event;
import cn.hisdar.MultiTouchEventParse.EventDeviceMap;
import cn.hisdar.MultiTouchEventParse.EventType;
import cn.hisdar.lib.adapter.FileAdapter;
import cn.hisdar.lib.adapter.StringAdapter;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.work.HTask;
import cn.hisdar.lib.work.HWork;
import cn.hisdar.lib.work.HWorkActionListener;
import cn.hisdar.lib.work.TaskResult;
import cn.hisdar.touchpaneltool.ui.project2.Project;

/**
 * @description 
 * This class provide two ways to create EventDeviceMap: <br>
 * EventDeviceMapCreater(Project project): the constructor, use this class, you mast introduction Project </br>
 * getEventDeviceMaps(): create EventDeviceMap with no progress dialog. </br>
 * getEventDeviceMapsWithProgress(): create EventDeviceMap with progress dialog. </br>
 * @author Hisdar
 */
public class EventDeviceMapCreater implements HTask, HWorkActionListener {
	
	private Project project;
	private EventDeviceMap[] eventDeviceMaps;
	private boolean isCancelCreateWork = false;
	
	/**
	 * @description constructor
	 * @param project use to create EventDeviceMap array
	 */
	public EventDeviceMapCreater(Project project) {
		this.project = project;
	}
	
	/**
	 * @description create EventDeviceMap with no progress dialog. </br>
	 * @return EventDeviceMap array
	 */
	public EventDeviceMap[] getEventDeviceMaps() {
		return createEventDeviceMaps(null);
	}

	/**
	 * @description create EventDeviceMap with progress dialog. </br>
	 * @return EventDeviceMap array
	 */
	public EventDeviceMap[] getEventDeviceMapsWithProgress() {
		HWork eventDeviceMapCreateWork = new HWork(this);
		eventDeviceMapCreateWork.setHWorkActionListener(this);
		TaskResult taskResult = eventDeviceMapCreateWork.startWork();
		if (taskResult == TaskResult.TASK_FAIL) {
			HLog.dl("EventDeviceMapCreater.getEventDeviceMapsWithProgress(): create device event map fail");
			JOptionPane.showMessageDialog(null, "创建设备和事件对应关系变失败！", "错误", JOptionPane.ERROR_MESSAGE);
			return null;
		} else if (taskResult == TaskResult.TASK_CANCLE) {
			HLog.dl("EventDeviceMapCreater.getEventDeviceMapsWithProgress(): user canceled device event map create");
			return null;
		} else {
			HLog.dl("EventDeviceMapCreater.getEventDeviceMapsWithProgress(): create device event map success");
			return eventDeviceMaps;
		}
	}
	
	private EventDeviceMap[] createEventDeviceMaps(HWork work) {
		
		ArrayList<EventDeviceMap> eventDeviceList = new ArrayList<>();
		
		if (work != null) {
			work.setProgressIndeterminate(true);
			work.setTitle("分析设备和事件的对应关系");
			work.setMessage("计算工程文件的大小");
		}
		
		// 1、计算工程文件的大小
		long projectFileSize = 0;
		ArrayList<File> projectFiles = FileAdapter.getFileList(project.getProjectFilePath());
		for (int i = 0; i < projectFiles.size(); i++) {
			projectFileSize += projectFiles.get(i).length();
		}
		
		if (work != null) {
			work.setProgressIndeterminate(false);
		}
		
		// 2、依次读取每个工程文件，并创EventDeviceMap
		long readCount = 0;
		for (int i = 0; i < projectFiles.size() && !isCancelCreateWork; i++) {
			
			if (work != null) {
				work.setMessage("分析文件：" + projectFiles.get(i).getPath().substring(project.getProjectFilePath().length()));
			}
			
			FileReader projectFileReader = null;
			try {
				projectFileReader = new FileReader(projectFiles.get(i));
				BufferedReader projectFileBufferedReader = new BufferedReader(projectFileReader);
				
				String lineString = projectFileBufferedReader.readLine() + 1;
				while (lineString != null) {
					addEventDeviceMap(eventDeviceList, lineString);
					readCount = readCount + lineString.length();
					
					float progress = 1.0f * readCount / projectFileSize;
					
					if (work != null) {	
						work.setProgressValue(progress > 1 ? 1 : progress);
					}
					
					lineString = projectFileBufferedReader.readLine();
				}
				
				projectFileBufferedReader.close();
				projectFileReader.close();
			} catch (IOException e) {
				HLog.el(e.getMessage());
				HLog.el(StringAdapter.toString(e.getStackTrace()));
			}
			
			if (work != null) {
				work.setTitle("分析设备和事件的对应关系 - " + (100 * readCount / projectFileSize) + "%");
			}
		}
		
		isCancelCreateWork = false;
		
		HLog.il("CreateEventDeviceMapWork.task():task finished");
		EventDeviceMap[] eventDeviceMaps = new EventDeviceMap[eventDeviceList.size()];
		for (int i = 0; i < eventDeviceMaps.length; i++) {
			eventDeviceMaps[i] = eventDeviceList.get(i);
		}
		
		return eventDeviceMaps;
	}

	@Override
	public int task(HWork work) {
		if (project == null) {
			HLog.el("EventDeviceMapCreater.task(): project can not be null");
			JOptionPane.showMessageDialog(null, "工程不能为NULL！", "错误", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
		
		eventDeviceMaps = createEventDeviceMaps(work);
		
		return 0;
	}
	
	private void addEventDeviceMap (ArrayList<EventDeviceMap> eventDeviceMaps, String lineString) {
		EventType eventType = Event.getEventType(lineString);
		String deviceName = Device.getDeviceName(lineString);
		
		if (eventType == EventType.EVENT_NONE || deviceName == null) {
			return;
		}
		
		// 检查当前的EventDeviceMap 是否已经在列表中
		boolean isInMap = false;
		for (int j = 0; j < eventDeviceMaps.size(); j++) {
			EventDeviceMap eventDeviceMap = eventDeviceMaps.get(j);
			if (eventDeviceMap.getDeviceName().equals(deviceName) && eventDeviceMap.getEventType() == eventType) {
				isInMap = true;
			}
		}
		
		if (!isInMap) {
			EventDeviceMap eventDeviceMap = new EventDeviceMap();
			eventDeviceMap.setDeviceName(deviceName);
			eventDeviceMap.setEventType(eventType);
			eventDeviceMaps.add(eventDeviceMap);
		}
	}

	@Override
	public boolean cancelTaskEvent() {
		HLog.dl("EventDeviceMapCreater.cancelTaskEvent(): cancel button event");
		isCancelCreateWork = true;
		return true;
	}
}
