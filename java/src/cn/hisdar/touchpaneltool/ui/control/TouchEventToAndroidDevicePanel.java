package cn.hisdar.touchpaneltool.ui.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.hisdar.MultiTouchEventParse.EventParser;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchEventListener;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDevice;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDeviceAdapter;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDeviceChangeListener;
import cn.hisdar.touchpaneltool.androidDevice.KeyCode;
import cn.hisdar.touchpaneltool.ui.control.InputSourceSetPanel.InputSourceType;

public class TouchEventToAndroidDevicePanel extends JPanel 
	implements ActionListener, InputSourceChangeListener, 
				AndroidDeviceChangeListener, MultiTouchEventListener {

	private JButton touchEventToAndroidDeviceButton = null;
	private AndroidDevice androidDevice = null;
	private boolean isSendEventToAndroidDevice = false;
	
	public TouchEventToAndroidDevicePanel() {
		touchEventToAndroidDeviceButton = new JButton("回放到手机");
		setLayout(new BorderLayout());
		setOpaque(false);
		
		add(touchEventToAndroidDeviceButton, BorderLayout.CENTER);
		
		touchEventToAndroidDeviceButton.addActionListener(this);
		InputSourceSetPanel.addInputSourceTypeChangeListener(this);
		AndroidDeviceAdapter.getInstance().addAndroidDeviceChangeListener(this);
		EventParser.getInstance().addMultiTouchEventListener(this);
	}

	@Override
	public void androudDeviceChangeEvent(AndroidDevice androidDevice) {
		this.androidDevice = androidDevice;
	}

	@Override
	public void inputSourceChangeListener(InputSourceType inputSourceType) {
		if (inputSourceType == InputSourceType.INPUT_SOURCE_LOG) {
			touchEventToAndroidDeviceButton.setEnabled(true);
		} else if (inputSourceType == InputSourceType.INPUT_SOURCE_PHONE) {
			touchEventToAndroidDeviceButton.setEnabled(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == touchEventToAndroidDeviceButton) {
			// 如果没有设备，那么先选择设备
			if (androidDevice == null) {
				AndroidDeviceAdapter.getInstance().selectAndroidDevice();
			}
			
			isSendEventToAndroidDevice = !isSendEventToAndroidDevice;
		}
	}

	@Override
	public void MultiTouchDowmEvent(MultiTouchPoint point) {
		if (isSendEventToAndroidDevice && androidDevice != null) {
			String[] commands = getMultiTouchPointCommand(point);
			androidDevice.sendEvent(commands);
		}
	}

	@Override
	public void MultiTouchUpEvent(MultiTouchPoint point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void MultiTouchActionEvent(MultiTouchPoint point) {
		if (isSendEventToAndroidDevice && androidDevice != null) {
			String[] commands = getMultiTouchPointCommand(point);
			androidDevice.sendEvent(commands);
		}
	}

	@Override
	public void MultiTouchUpEvent(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveSourceData(String sourceData) {
		// TODO Auto-generated method stub
		
	}
	
	private void addCommandToList(String commandHead, ArrayList<String> commandArrayList, int event, int value) {
		if (value >= 0) {
			String command = commandHead + KeyCode.EV_ABS + " " + event + " " + value;
			commandArrayList.add(command);
		}
	}
	
	public String[] getMultiTouchPointCommand(MultiTouchPoint point) {
		String commandHead = "sendevent /dev/input/event10 ";
		int commandCount = 0;
		
		// 首先是slot id
		String slotIdCommand = commandHead + KeyCode.EV_ABS + " " + KeyCode.ABS_MT_SLOT + " " + point.id;
		commandCount += 1;
		
		// sync 
		String syncCommand = commandHead + KeyCode.EV_SYN + " 0 0";
		commandCount += 1;
		
		// 如果是抬起事件，上报抬起
		String upEventCommand = null;
		if (point.eventType == MultiTouchPoint.EVENT_TYPE_DOWN) {
			upEventCommand = commandHead + KeyCode.EV_ABS + " " + KeyCode.ABS_MT_TRACKING_ID + " " + 0xFFFFFFFFL;
			commandCount += 1;
			
			String[] commandArray = new String[commandCount];
			commandArray[0] = slotIdCommand;
			commandArray[1] = upEventCommand;
			commandArray[2] = syncCommand;
			return commandArray;
		}
		
		ArrayList<String> commandArrayList = new ArrayList<String>();
		commandArrayList.add(slotIdCommand);
		
		// 如果是按下事件
		if (point.eventType == MultiTouchPoint.EVENT_TYPE_DOWN) {
			String trackIdCommand = commandHead + KeyCode.EV_ABS + " " + KeyCode.ABS_MT_TRACKING_ID + " " + point.trackingId;
			commandArrayList.add(trackIdCommand);
		}
		
		//addCommandToList(commandHead, commandArrayList, KeyCode.ABS_MT_TOUCH_MAJOR, point.touchMajor);
		//addCommandToList(commandHead, commandArrayList, KeyCode.ABS_MT_TOUCH_MINOR, point.touchMinor);
		addCommandToList(commandHead, commandArrayList, KeyCode.ABS_MT_WIDTH_MAJOR, point.widthMajoy);
		addCommandToList(commandHead, commandArrayList, KeyCode.ABS_MT_WIDTH_MINOR, point.widthMinor);
		//addCommandToList(commandHead, commandArrayList, KeyCode.ABS_MT_ORIENTATION, (int)point.orieniation);
		addCommandToList(commandHead, commandArrayList, KeyCode.ABS_MT_POSITION_X, point.positionX);
		addCommandToList(commandHead, commandArrayList, KeyCode.ABS_MT_POSITION_Y, point.positionY);
		addCommandToList(commandHead, commandArrayList, KeyCode.ABS_MT_TOOL_TYPE, point.toolType);
		addCommandToList(commandHead, commandArrayList, KeyCode.ABS_MT_BLOB_ID, point.blobId);
		addCommandToList(commandHead, commandArrayList, KeyCode.ABS_MT_PRESSURE, point.pressure);
		addCommandToList(commandHead, commandArrayList, KeyCode.ABS_MT_DISTANCE, point.distance);
		commandArrayList.add(syncCommand);
		
		String[] commandArray = new String[commandArrayList.size()];
		for (int i = 0; i < commandArray.length; i++) {
			commandArray[i] = commandArrayList.get(i);
		}
		
		return commandArray;
	}
}
