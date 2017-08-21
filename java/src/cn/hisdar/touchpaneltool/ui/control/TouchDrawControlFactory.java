package cn.hisdar.touchpaneltool.ui.control;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Vector;

import cn.hisdar.MultiTouchEventParse.EventDeviceMap;
import cn.hisdar.MultiTouchEventParse.EventParser;
import cn.hisdar.MultiTouchEventParse.EventTime;
import cn.hisdar.MultiTouchEventParse.EventType;
import cn.hisdar.MultiTouchEventParse.KeyEvent.ResumeEventListener;
import cn.hisdar.MultiTouchEventParse.KeyEvent.SuspendEventListener;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchEventListener;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.MultiTouchEventParse.stream.AbstractEventInputStream;
import cn.hisdar.MultiTouchEventParse.stream.BufferedEventInputStream;
import cn.hisdar.MultiTouchEventParse.stream.EventInputStream;
import cn.hisdar.MultiTouchEventParse.stream.RandomEventInputStream;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDevice;
import cn.hisdar.touchpaneltool.interfaces.TouchDrawControlInterface;
import cn.hisdar.touchpaneltool.interfaces.TouchShowControlInterface;
import cn.hisdar.touchpaneltool.setting.ParseSettingPanel;
import cn.hisdar.touchpaneltool.ui.output.TouchEventShowPanel;
import cn.hisdar.touchpaneltool.ui.show.TouchShowScreenInterface;

public class TouchDrawControlFactory 
	implements TouchDrawControlInterface, MultiTouchEventListener, ParseSpeedChangeListener,
				ResumeEventListener, SuspendEventListener {
	
	private static TouchShowControlInterface touchShowControlInterface = null;
	private TouchShowScreenInterface drawInterface = null;
	private Vector<TouchFinger> touchFingers = null;
	private EventParser eventParse = null;
	
	private TouchEventShowPanel touchEventShowPanel = null;
	private AbstractEventInputStream inputStream = null;
	private HConfig settingConfig = null;
	
	private boolean nextStepFlage = false;
	
	public TouchDrawControlFactory(TouchShowScreenInterface drawInterface) {
		this.settingConfig = HConfig.getInstance(ParseSettingPanel.PARSE_CONFIG_FILE_PATH);
		this.drawInterface = drawInterface;
		this.touchFingers = new Vector<TouchFinger>();
		this.eventParse = EventParser.getInstance();
		this.eventParse.addMultiTouchEventListener(this);
		
		touchEventShowPanel = TouchEventShowPanel.getInstance();
		ParseSpeedControlPanel.addParseSpeedChangeListener(this);
		eventParse.addResumeEventListener(this);
		eventParse.addSuspendEventListener(this);
	}
	
	@Override
	public void startFileInputDraw(String srcFile, EventDeviceMap[] eventDeviceMaps) {
		HLog.il("start file input draw\n");
		// ɾ���Ѿ����ڵ���ʷ��
		touchFingers.removeAllElements();
		
		// �����켣���
		inputStream = null;
		try {
			File dataFile = new File(srcFile);
			//inputStream = new BufferedEventInputStream(new FileInputStream(dataFile), true, dataFile.length());
			inputStream = new RandomEventInputStream(dataFile, true);
		} catch (FileNotFoundException e) {
			HLog.e("Fail to open: " + srcFile);
			HLog.el(e);
			return;
		}
		
		inputStream.setMaxWaitTime(settingConfig.getConfigValue(
				ParseSettingPanel.MAX_WAIT_TIME_CONFIG_NAME, ParseSettingPanel.DEFAULT_WAIT_TIME));
		
		eventParse.setEventDeviceMap(eventDeviceMaps);
		eventParse.setMultiTouchInputStream(inputStream);
	}
	
	@Override
	public boolean startPhoneInputDraw(AndroidDevice androidDevice) {

		if (androidDevice == null) {
			HLog.el("No android device found");
			return false;
		}
		
		HLog.il("start phone input draw\n");
		
		// ɾ���Ѿ����ڵ���ʷ��
		touchFingers.removeAllElements();
		
		// ����һ��Ҫ��������߳��������������ֽ��濨��������Ŀǰ��Ҳ��֪����Ϊʲô�����ڻ��ɺ�adb ִ����ɺ��inputStream �й�ϵ
		new Thread(new Runnable() {
			@Override
			public void run() {
				inputStream = new BufferedEventInputStream(androidDevice.getPhoneEventInputStream());
				inputStream.setMaxWaitTime(0);
				EventDeviceMap eventDeviceMap = new EventDeviceMap(androidDevice.getTouchInputEvent(), EventType.EVENT_MULTI_TOUCH);
				HLog.il("EventDeviceMap:" + eventDeviceMap);
				eventParse.addEventDeviceMap(eventDeviceMap);
				eventParse.setMultiTouchInputStream(inputStream);
			}
		}).start();
		
		return true;
	}
	
	@Override
	public void suspendDraw() {
		eventParse.suspend();
	}

	@Override
	public void resumeDraw() {
		// ����������ť�����µ�ʱ���˳�һ��һ��������ģʽ
		nextStepFlage = false;
		eventParse.resume();
	}

	@Override
	public void stopDraw() {
		eventParse.stopParse();
	}

	@Override
	public boolean isSuspend() {
		if (touchShowControlInterface != null) {
			return touchShowControlInterface.isSuspend();
		}
		
		return false;
	}

	@Override
	public boolean isRun() {
		if (touchShowControlInterface != null) {
			return touchShowControlInterface.isRun();
		}
		
		return false;
	}
	
	@Override
	public void setResolution(Dimension resolution) {
		drawInterface.setResolution(resolution.width, resolution.height);
	}

	@Override
	public void nextStep() {
		// ���ý���ģʽΪһ��һ������ģʽ�������ѽ����߳�
		nextStepFlage = true;
		eventParse.resume();
	}

	@Override
	public void MultiTouchActionEvent(MultiTouchPoint arg0) {
		// ��ϵͳ�е㱨������ʱ���ȼ��touchFingers���Ƿ�������㣬
		TouchFinger currentTouchFinger = null;
		for (int i = 0; i < touchFingers.size(); i++) {
			if (touchFingers.get(i).id == arg0.id) {
				//System.out.println("Found finger:" + touchFingers.get(i).id + ">>>>" + arg0.id);
				currentTouchFinger = touchFingers.get(i);
			}
		}
		
		// System.out.println("Handle point:" + arg0);
		
		// ���touchFingers��û������㣬˵��������������ĵ㣬��touchFingers�д���һ��touchFinger���洢�����
		if (currentTouchFinger == null) {
			
			//System.err.println("Add new point:" + arg0);
			
			currentTouchFinger = new TouchFinger();
			currentTouchFinger.id = arg0.id;
			// ����ǰ��ӵĵ���뵽��������ָ��ȥ
			currentTouchFinger.pointList.add(arg0);
			
			// ����������ָ���뵽touchFinger��ȥ
			touchFingers.add(currentTouchFinger);
		} else if (currentTouchFinger.touchState == TouchFinger.TOUCH_STATE_END) {
			// ���touchFingers��������㣬����������Ѿ�̧�����ˣ�˵�������µĴ����㣬��touchFingers�д����µ�touchFinger
			currentTouchFinger = new TouchFinger();
			currentTouchFinger.id = arg0.id;
			// ����ǰ��ӵĵ���뵽��������ָ��ȥ
			currentTouchFinger.pointList.add(arg0);
			
			// ����������ָ���뵽touchFinger��ȥ
			touchFingers.add(currentTouchFinger);
		} else if (currentTouchFinger.touchState == TouchFinger.TOUCH_STATE_TOUCH) {
			// ���touchFingers��������㣬�������û��̧���������������뵽touchFingers�������ָ��ȥ
			currentTouchFinger.pointList.add(arg0);
			//System.err.println("Up handle point:" + arg0);
		} else {
			System.err.println("Un handle point:" + arg0);
		}
		
		// ֪ͨ�������»���
		if (drawInterface != null) {
			drawInterface.repaintTouchLine(touchFingers);
		}
		//System.err.println("touch event:" + arg0);
	}

	@Override
	public void MultiTouchDowmEvent(MultiTouchPoint arg0) {
		//System.out.println("Point down:" + arg0);
		// ��ϵͳ�ϱ��˰����¼���ʱ��˵���㶼�Ѿ�̧�����ˣ����ʱ�������touchFingers�е����ݣ�Ȼ���ٴ���
		touchFingers.removeAllElements();
		MultiTouchActionEvent(arg0);
	}

	@Override
	public void MultiTouchUpEvent(MultiTouchPoint arg0) {
		// ���ϵͳ�ϱ���������ָ̧����¼��������е���ָ��״̬���ó�̧��״̬
		for (int i = 0; i < touchFingers.size(); i++) {
			// ϵͳ�ϱ����е�̧���¼���ʱ��������е�û��̧�����Ļ��������쳣״̬����һ��log
			if (touchFingers.get(i).touchState != TouchFinger.TOUCH_STATE_END) {
				System.err.println("Finger " + touchFingers.get(i).id + " have not up when every finger have up");
			}
			
			touchFingers.get(i).touchState = TouchFinger.TOUCH_STATE_END;
		}
		
		// ֪ͨ�������»���
		if (drawInterface != null) {
			drawInterface.repaintTouchLine(touchFingers);
		}
		
		// �������һ��һ��Ԥ����ģʽ����̧���¼��ϱ���ʱ����ͣ�������ȴ���һ��������
		if (nextStepFlage) {
			eventParse.suspend();
		}
	}

	@Override
	public void MultiTouchUpEvent(int arg0) {
		//System.out.println("Point " + arg0 + " is up");
		// ���ϵͳ�ϱ�ĳһ����̧�����ˣ���touchFingers�е�������״̬���ó�̧��״̬
		for (int i = 0; i < touchFingers.size(); i++) {
			if (touchFingers.get(i).id == arg0 && touchFingers.get(i).touchState == TouchFinger.TOUCH_STATE_TOUCH) {
				touchFingers.get(i).touchState = TouchFinger.TOUCH_STATE_END;
			}
		}
		
		// ֪ͨ�������»���
		if (drawInterface != null) {
			drawInterface.repaintTouchLine(touchFingers);
		}
	}

	@Override
	public void receiveSourceData(String sourceData) {
		touchEventShowPanel.outputLog(sourceData);
		//HLog.il(sourceData);
	}
	
	public void setSkipModel(boolean model) {
		if (inputStream == null) {
			return;
		}
		
		if (model) {
			inputStream.setMaxWaitTime(settingConfig.getConfigValue(
					ParseSettingPanel.MAX_WAIT_TIME_CONFIG_NAME, ParseSettingPanel.DEFAULT_WAIT_TIME));
		} else {
			inputStream.setMaxWaitTime(-1);
		}
	}

	@Override
	public void parseSpeedChangeEvent(double parseSpeed) {
		eventParse.setParseSpeed(parseSpeed);
	}

	@Override
	public void suspendEvent(EventTime eventTime) {
		drawInterface.setToSuspend();
	}

	@Override
	public void resumeEvent(EventTime eventTime) {
		drawInterface.setToResume();
	}
}
