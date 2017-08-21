package cn.hisdar.touchpaneltool.ui.control;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDevice;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDeviceAdapter;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDeviceChangeListener;
import cn.hisdar.touchpaneltool.setting.common.HComboBoxItemChangeListener;
import cn.hisdar.touchpaneltool.setting.common.HComboBoxPanel;

public class InputSourceSetPanel extends JPanel 
	implements HComboBoxItemChangeListener, AndroidDeviceChangeListener {
	
	public enum InputSourceType {
		INPUT_SOURCE_LOG,
		INPUT_SOURCE_PHONE,
	}
	
	private final static String INPUT_SOURCE_PHONE_TEXT = "手机输入";
	private final static String INPUT_SOURCE_LOG_TEXT = "日志输入";
	
	private static ArrayList<InputSourceChangeListener> inputSourceChangeListeners = new ArrayList<InputSourceChangeListener>();
	private static InputSourceType inputSource = InputSourceType.INPUT_SOURCE_LOG;
	
	private HComboBoxPanel inputSourceComboBox = null;
	private AndroidDevice androidDevice = null;
	private AndroidDeviceAdapter androidDeviceAdapter = null;
	
	public InputSourceSetPanel() {
		
		inputSourceComboBox = new HComboBoxPanel("输入源：");
		inputSourceComboBox.addItem(INPUT_SOURCE_LOG_TEXT);
		inputSourceComboBox.addItem(INPUT_SOURCE_PHONE_TEXT);
		
		inputSourceComboBox.addItemChangeListener(this);
		
		setLayout(new BorderLayout());
		add(inputSourceComboBox, BorderLayout.CENTER);
		setOpaque(false);
		
		androidDeviceAdapter = AndroidDeviceAdapter.getInstance();
		androidDeviceAdapter.addAndroidDeviceChangeListener(this);
	}

	private void notifyInputSourceChangeEvent(InputSourceType inputSourceType) {
		for (int i = 0; i < inputSourceChangeListeners.size(); i++) {
			HLog.il("notify:" + i);
			inputSourceChangeListeners.get(i).inputSourceChangeListener(inputSourceType);
		}
	}
	
	@Override
	public void itemChangeEvent(JPanel source, String itemName) {
		
		if (source == inputSourceComboBox) {
			if (itemName.equals(INPUT_SOURCE_PHONE_TEXT)) {
				HLog.il("Check phine :");
				inputSource = InputSourceType.INPUT_SOURCE_PHONE;
				
				// 如果现在没有设备，那么先连接设备
				if (androidDevice == null) {
					androidDeviceAdapter.selectAndroidDevice();
					
					if (androidDevice == null) {
						inputSourceComboBox.setSelectedIndex(0);
						inputSource = InputSourceType.INPUT_SOURCE_LOG;
						return;
					}
				}
				
				notifyInputSourceChangeEvent(inputSource);
			} else if (itemName.equals(INPUT_SOURCE_LOG_TEXT)) {
				inputSource = InputSourceType.INPUT_SOURCE_LOG;
				notifyInputSourceChangeEvent(inputSource);
			}
		}
	}

	@Override
	public void androudDeviceChangeEvent(AndroidDevice androidDevice) {
		HLog.il(androidDevice.toString());
		this.androidDevice = androidDevice;
	}
	
	public static void addInputSourceTypeChangeListener(InputSourceChangeListener listener) {
		for (int i = 0; i < inputSourceChangeListeners.size(); i++) {
			if (inputSourceChangeListeners.get(i) == listener) {
				return;
			}
		}
		
		inputSourceChangeListeners.add(listener);
		listener.inputSourceChangeListener(inputSource);
	}
	
	public static void removeInputSourceTypeChangeListener(InputSourceChangeListener listener) {
		int listenerCount = inputSourceChangeListeners.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			if (inputSourceChangeListeners == listener) {
				inputSourceChangeListeners.remove(i);
			}
		}
	}
}
