package cn.hisdar.touchpaneltool.ui.history;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import cn.hisdar.MultiTouchEventParse.EventParser;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchEventListener;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.lib.log.HLog;

/***
 * 将解析数据保存起来
 * 1、数据存储方式
 * 		将一个点的信息作为一个数据块来存储
 * 		（1）、内存缓冲区 -- 存储指定数量的事件
 * 		（2）、文件存储    -- 所有的事件都将被以文件的形式存储起来
 * 		（3）、使用方法 ：点击倒退一个操作，长按按照时间倒退
 * */
public class EventHistoryManager implements MultiTouchEventListener  {

	private static EventHistoryManager eventHistoryManager = null;
	private RandomAccessFile historyEventFileRandomAccess = null;
	
	private ArrayList<HistoryEventFilePointer> filePointers = null;
	private EventParser eventParser = null;
	
	private EventHistoryManager() {
		filePointers = new ArrayList<HistoryEventFilePointer>();
		eventParser = EventParser.getInstance();
		
		try {
			historyEventFileRandomAccess = new RandomAccessFile("./buffer/historyEvent", "rw");
		} catch (FileNotFoundException e) {
			HLog.el(e);
		}
		
		eventParser.addMultiTouchEventListener(this);
		HLog.il("EventHistoryManager init");
	}
	
	static {
		EventParser.getInstance();
	}
	
	public static EventHistoryManager getInstance() {
		if (eventHistoryManager == null) {
			synchronized (EventHistoryManager.class) {
				if (eventHistoryManager == null) {
					eventHistoryManager = new EventHistoryManager();
				}
			}
		}
		
		HLog.il("EventHistoryManager init");
		return eventHistoryManager;
	}
	
	@Override
	public void MultiTouchDowmEvent(MultiTouchPoint point) {
		saveEvent(point, false);
	}

	@Override
	public void MultiTouchUpEvent(MultiTouchPoint point) {
		saveEvent(point, true);
	}

	@Override
	public void MultiTouchActionEvent(MultiTouchPoint point) {
		HLog.il("EventHistoryManager init");
		saveEvent(point, false);
	}

	@Override
	public void MultiTouchUpEvent(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveSourceData(String sourceData) {
		// TODO Auto-generated method stub
		
	}
	
	private boolean saveEvent(MultiTouchPoint point, boolean isAllFingerUp) {
		String dataToWrite = point.toString() + "\n";
		
		// 将数据写入文件
		try {
			historyEventFileRandomAccess.write(dataToWrite.getBytes());
		} catch (IOException e) {
			HLog.el(e);
			return false;
		}
		
		// 如果这个数据是一个抬起事件，那么将当前的文件位置加入到抬起事件列表中
		if (isAllFingerUp) {
			long filePointer;
			try {
				filePointer = historyEventFileRandomAccess.getFilePointer();
			} catch (IOException e) {
				HLog.el(e);
				return false;
			}
			
			HistoryEventFilePointer eventFilePointer = new HistoryEventFilePointer(point.systemTime, point.bootUpTime, filePointer);
			filePointers.add(eventFilePointer);
		}
		
		return true;
	}

}
