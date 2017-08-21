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
 * ���������ݱ�������
 * 1�����ݴ洢��ʽ
 * 		��һ�������Ϣ��Ϊһ�����ݿ����洢
 * 		��1�����ڴ滺���� -- �洢ָ���������¼�
 * 		��2�����ļ��洢    -- ���е��¼����������ļ�����ʽ�洢����
 * 		��3����ʹ�÷��� ���������һ����������������ʱ�䵹��
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
		
		// ������д���ļ�
		try {
			historyEventFileRandomAccess.write(dataToWrite.getBytes());
		} catch (IOException e) {
			HLog.el(e);
			return false;
		}
		
		// ������������һ��̧���¼�����ô����ǰ���ļ�λ�ü��뵽̧���¼��б���
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
