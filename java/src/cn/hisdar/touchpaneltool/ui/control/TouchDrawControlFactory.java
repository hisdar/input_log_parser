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
		// 删除已经存在的历史点
		touchFingers.removeAllElements();
		
		// 触摸轨迹输出
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
		
		// 删除已经存在的历史点
		touchFingers.removeAllElements();
		
		// 这里一定要重新起个线程来做，否则会出现界面卡死的现象，目前我也不知道是为什么，现在怀疑和adb 执行完成后的inputStream 有关系
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
		// 继续解析按钮被按下的时候，退出一步一步解析的模式
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
		// 设置解析模式为一步一步解析模式，并唤醒解析线程
		nextStepFlage = true;
		eventParse.resume();
	}

	@Override
	public void MultiTouchActionEvent(MultiTouchPoint arg0) {
		// 当系统有点报上来的时候，先检查touchFingers中是否有这个点，
		TouchFinger currentTouchFinger = null;
		for (int i = 0; i < touchFingers.size(); i++) {
			if (touchFingers.get(i).id == arg0.id) {
				//System.out.println("Found finger:" + touchFingers.get(i).id + ">>>>" + arg0.id);
				currentTouchFinger = touchFingers.get(i);
			}
		}
		
		// System.out.println("Handle point:" + arg0);
		
		// 如果touchFingers中没有这个点，说明这个点是新增的点，在touchFingers中创建一个touchFinger来存储这个点
		if (currentTouchFinger == null) {
			
			//System.err.println("Add new point:" + arg0);
			
			currentTouchFinger = new TouchFinger();
			currentTouchFinger.id = arg0.id;
			// 将当前添加的点加入到创建的手指上去
			currentTouchFinger.pointList.add(arg0);
			
			// 将创建的手指加入到touchFinger中去
			touchFingers.add(currentTouchFinger);
		} else if (currentTouchFinger.touchState == TouchFinger.TOUCH_STATE_END) {
			// 如果touchFingers中有这个点，但是这个点已经抬起来了，说明这是新的触摸点，在touchFingers中创建新的touchFinger
			currentTouchFinger = new TouchFinger();
			currentTouchFinger.id = arg0.id;
			// 将当前添加的点加入到创建的手指上去
			currentTouchFinger.pointList.add(arg0);
			
			// 将创建的手指加入到touchFinger中去
			touchFingers.add(currentTouchFinger);
		} else if (currentTouchFinger.touchState == TouchFinger.TOUCH_STATE_TOUCH) {
			// 如果touchFingers中有这个点，且这个点没有抬起来，将这个点加入到touchFingers的这个手指中去
			currentTouchFinger.pointList.add(arg0);
			//System.err.println("Up handle point:" + arg0);
		} else {
			System.err.println("Un handle point:" + arg0);
		}
		
		// 通知界面重新绘制
		if (drawInterface != null) {
			drawInterface.repaintTouchLine(touchFingers);
		}
		//System.err.println("touch event:" + arg0);
	}

	@Override
	public void MultiTouchDowmEvent(MultiTouchPoint arg0) {
		//System.out.println("Point down:" + arg0);
		// 当系统上报了按下事件的时候，说明点都已经抬起来了，这个时候先清空touchFingers中的数据，然后再处理
		touchFingers.removeAllElements();
		MultiTouchActionEvent(arg0);
	}

	@Override
	public void MultiTouchUpEvent(MultiTouchPoint arg0) {
		// 如果系统上报了所有手指抬起的事件，将所有的手指的状态设置成抬起状态
		for (int i = 0; i < touchFingers.size(); i++) {
			// 系统上报所有点抬起事件的时候，如果还有点没有抬起来的话，这是异常状态，打一个log
			if (touchFingers.get(i).touchState != TouchFinger.TOUCH_STATE_END) {
				System.err.println("Finger " + touchFingers.get(i).id + " have not up when every finger have up");
			}
			
			touchFingers.get(i).touchState = TouchFinger.TOUCH_STATE_END;
		}
		
		// 通知界面重新绘制
		if (drawInterface != null) {
			drawInterface.repaintTouchLine(touchFingers);
		}
		
		// 如果处于一步一步预览的模式，当抬起事件上报的时候，暂停解析，等待下一步的命令
		if (nextStepFlage) {
			eventParse.suspend();
		}
	}

	@Override
	public void MultiTouchUpEvent(int arg0) {
		//System.out.println("Point " + arg0 + " is up");
		// 如果系统上报某一个点抬起来了，将touchFingers中的这个点的状态设置成抬起状态
		for (int i = 0; i < touchFingers.size(); i++) {
			if (touchFingers.get(i).id == arg0 && touchFingers.get(i).touchState == TouchFinger.TOUCH_STATE_TOUCH) {
				touchFingers.get(i).touchState = TouchFinger.TOUCH_STATE_END;
			}
		}
		
		// 通知界面重新绘制
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
