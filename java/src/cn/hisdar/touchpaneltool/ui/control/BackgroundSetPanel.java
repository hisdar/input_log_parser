package cn.hisdar.touchpaneltool.ui.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import cn.hisdar.touchpaneltool.androidDevice.AndroidDevice;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDeviceAdapter;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDeviceChangeListener;
import cn.hisdar.touchpaneltool.setting.common.HComboBoxItemChangeListener;
import cn.hisdar.touchpaneltool.setting.common.HComboBoxPanel;

public class BackgroundSetPanel extends JPanel 
	implements HComboBoxItemChangeListener, ResolutionChangeListener, AndroidDeviceChangeListener {
	
	private final static String BACKGROUND_PHONE_TEXT = "ͬ���ֻ���Ļ";
	private final static String BACKGROUND_IMAGE_TEXT = "�ֶ����ñ���";
	private final static String BACKGROUND_COLOR_TEXT = "��ɫ����";
	
	private HComboBoxPanel backGroundComboBox = null;
	private TouchShowBackGround touchShowBackGround = null;
	
	private Dimension resolution = null;
	private AndroidDevice androidDevice = null;
	private AndroidDeviceAdapter androidDeviceAdapter = null;
	
	private boolean isShowGriddingLine = false;
	
	public BackgroundSetPanel() {
		resolution = new Dimension(720, 1280);
		touchShowBackGround = TouchShowBackGround.getInstance();
		
		backGroundComboBox = new HComboBoxPanel("�������ã�");
		backGroundComboBox.addItem(BACKGROUND_COLOR_TEXT);
		backGroundComboBox.addItem(BACKGROUND_IMAGE_TEXT);
		backGroundComboBox.addItem(BACKGROUND_PHONE_TEXT);
		
		setLayout(new BorderLayout());
		add(backGroundComboBox, BorderLayout.CENTER);
		setOpaque(false);
		
		backGroundComboBox.addItemChangeListener(this);
		androidDeviceAdapter = AndroidDeviceAdapter.getInstance();
		androidDeviceAdapter.addAndroidDeviceChangeListener(this);
		
	}

	private void touchShowBackGroundChangeEvent(String backgroundTarget) {
		if (backgroundTarget.equals(BACKGROUND_COLOR_TEXT)) {
			if (resolution.width != 0 && resolution.height != 0) {
				touchShowBackGround.setColorBackgroundImage(resolution.width, resolution.height, Color.WHITE);
			}
		} else if (backgroundTarget.equals(BACKGROUND_IMAGE_TEXT)) {
			
			touchShowBackGround.setImageBackgroud();
		} else if (backgroundTarget.equals(BACKGROUND_PHONE_TEXT)) {
			
			// ���û�������豸����ô�������豸
			if (androidDevice == null) {
				androidDeviceAdapter.selectAndroidDevice();
				if (androidDevice == null) {
					return;
				}
			}
			
			touchShowBackGround.setBackgroundImageFromPhone(androidDevice);
		}
	}
	
	@Override
	public void itemChangeEvent(JPanel source, String itemName) {
		if (source == backGroundComboBox) {
			touchShowBackGroundChangeEvent(itemName);
		}
	}

	@Override
	public void resolutionChangeEvent(Dimension resolution) {
		this.resolution.setSize(resolution);
	}

	@Override
	public void androudDeviceChangeEvent(AndroidDevice androidDevice) {
		this.androidDevice = androidDevice;
	}
}
