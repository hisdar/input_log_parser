package cn.hisdar.touchpaneltool.ui.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.FileAdapter;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDevice;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDeviceAdapter;
import cn.hisdar.touchpaneltool.androidDevice.AndroidDeviceChangeListener;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingChangeListener;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingPanel;
import cn.hisdar.touchpaneltool.ui.control.InputSourceSetPanel.InputSourceType;
import cn.hisdar.touchpaneltool.ui.show.TouchShowScreenAndKeyPadPanel;

public class TouchShowControlPanel extends JPanel 
	implements ActionListener, ResolutionChangeListener, AndroidDeviceChangeListener,
				InputSourceChangeListener, EnvironmentSettingChangeListener {
	
	private static final long serialVersionUID = -4236795792185641351L;
	private final static Color DEFAULT_DIVIDER_COLOR = new Color(0xbcc7d8);
	private final static String SAVE_IMAGE_PATH_CONFIG_FILE_PATH = "./Config/save_image_config.xml";

	private final static String SAVE_IMAGE_PATH_CONFIG_NAME = "saveImagePath";
	
	private ImageIcon saveIcon = null;
	private ImageIcon savePressedIcon = null;
	
	private JButton saveButton = null;
	
	private JButton androidDeviceButton = null;
	
	private static ArrayList<TouchShowControlSaveImageAction> saveImageActions = new ArrayList<TouchShowControlSaveImageAction>();
	
	private HConfig savaImagePathConfig = null;
	
	private TouchDrawControlFactory touchDrawControlFactory 	= null;
	private ProgressControlPanel 	progressControlPanel 		= null;
	private ParseSpeedControlPanel 	parseSpeedControlPanel 		= null;
	private ResolutionSetPanel 		resolutionSetPanel 			= null;
	private SelectTimePanel 		selectTimePanel 			= null;
	private GriddlingLineSetPanel 	griddlingLineSetPanel 		= null;
	private InputSourceSetPanel 	inputSourceSetPanel 		= null;
	private BackgroundSetPanel		backgroundSetPanel 			= null;
	private TouchEventToAndroidDevicePanel touchEventToAndroidDevicePanel = null;
	
	private Dimension selectedResolution = null;
	private AndroidDevice androidDevice = null;
	private AndroidDeviceAdapter androidDeviceAdapter = null;
	private InputSourceType inputSourceType = null;
	
	public TouchShowControlPanel() {
		initConfig();
		
		selectedResolution = new Dimension();
		androidDeviceAdapter = AndroidDeviceAdapter.getInstance();
		
		setBorder(BorderFactory.createLineBorder(new Color(0x9FB6CD)));
		((FlowLayout)getLayout()).setVgap(3);
		((FlowLayout)getLayout()).setHgap(1);
		((FlowLayout)getLayout()).setAlignment(FlowLayout.LEFT);
		
		setBackground(DEFAULT_DIVIDER_COLOR);
		
		saveIcon = new ImageIcon("./Image/SAVE.png");
		savePressedIcon = new ImageIcon("./Image/SAVE_PRESSED.png");
		
		saveButton = new JButton(saveIcon);
		saveButton.addActionListener(this);
		saveButton.setPressedIcon(savePressedIcon);
		saveButton.setBorder(null);
		saveButton.setOpaque(false);
		
		add(getAndroidDevicePanel());
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		
		inputSourceSetPanel = new InputSourceSetPanel();
		add(inputSourceSetPanel);
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		
		touchDrawControlFactory = new TouchDrawControlFactory(TouchShowScreenAndKeyPadPanel.touchScreenPanel);
		progressControlPanel = new ProgressControlPanel(touchDrawControlFactory);
		add(progressControlPanel);
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		
		parseSpeedControlPanel = new ParseSpeedControlPanel();
		add(parseSpeedControlPanel);
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		
		resolutionSetPanel = new ResolutionSetPanel();
		add(resolutionSetPanel);
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		
		backgroundSetPanel = new BackgroundSetPanel();
		add(backgroundSetPanel);
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		
		griddlingLineSetPanel = new GriddlingLineSetPanel();
		add(griddlingLineSetPanel);
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));

		//touchEventToAndroidDevicePanel = new TouchEventToAndroidDevicePanel();
		//add(touchEventToAndroidDevicePanel);
		//add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		
		selectTimePanel = new SelectTimePanel();
		add(selectTimePanel);
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));

		add(saveButton);
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		
		ResolutionSetPanel.addResolutionChangeListener(this);
		androidDeviceAdapter.addAndroidDeviceChangeListener(this);
		InputSourceSetPanel.addInputSourceTypeChangeListener(this);
		EnvironmentSettingPanel.addEnvironmentSettingChangeListener(this);
	}
	
	private void initConfig() {
		savaImagePathConfig = HConfig.getInstance(SAVE_IMAGE_PATH_CONFIG_FILE_PATH);
	}

	private JPanel getAndroidDevicePanel() {
		androidDeviceButton = new JButton("|选择设备" ,new ImageIcon("./Image/deviceUp.png"));
		androidDeviceButton.setPressedIcon(new ImageIcon("./Image/deviceDown.png"));
		androidDeviceButton.addActionListener(this);
		androidDeviceButton.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		androidDeviceButton.setOpaque(false);
		
		JPanel androidDevicePanel = new JPanel();
		androidDevicePanel.setLayout(new BorderLayout());
		androidDevicePanel.add(androidDeviceButton);
		androidDevicePanel.setOpaque(false);
		
		return androidDevicePanel;
	}
	
	public static void addTouchShowControlSaveImageAction(TouchShowControlSaveImageAction listener) {
		for (int i = 0; i < saveImageActions.size(); i++) {
			if (saveImageActions.get(i) == listener) {
				return;
			}
		}
		
		saveImageActions.add(listener);
	}
	
	public static void removeTouchShowControlSaveImageAction(TouchShowControlSaveImageAction listener) {
		int listenerCount = saveImageActions.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			if (saveImageActions.get(i) == listener) {
				saveImageActions.remove(i);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveButton) {
			saveButtonEventHandler();
		} else if (e.getSource() == androidDeviceButton) {
			androidDeviceAdapter.selectAndroidDevice();
		}
	}

	private void saveButtonEventHandler() {
		String saveImagePath = savaImagePathConfig.getConfigValue(SAVE_IMAGE_PATH_CONFIG_NAME);
				
		JFileChooser fileChooser = new JFileChooser(saveImagePath);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int retValue = fileChooser.showSaveDialog(null);
		if (retValue != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		// 初始化目录
		FileAdapter.initFolder(fileChooser.getSelectedFile().getParent());
		savaImagePathConfig.setConfigItem(new ConfigItem(SAVE_IMAGE_PATH_CONFIG_NAME, fileChooser.getSelectedFile().getParent()));
		
		String selectedPath = fileChooser.getSelectedFile().getPath();
		if (selectedPath.lastIndexOf(".png") < 0 
				|| selectedPath.lastIndexOf(".png") != selectedPath.length() - ".png".length()) {
			selectedPath += ".png";
		}
		
		
		for (int i = 0; i < saveImageActions.size(); i++) {
			saveImageActions.get(i).touchShowControlSaveImageEvent("PNG", selectedPath);
		}
	}

	@Override
	public void resolutionChangeEvent(Dimension resolution) {
		selectedResolution.setSize(resolution);
	}

	@Override
	public void androudDeviceChangeEvent(AndroidDevice androidDevice) {
		if (this.androidDevice != null && this.androidDevice.equals(androidDevice)) {
			return;
		}
		
		this.androidDevice = androidDevice;
		if (inputSourceType != null) {
			if (inputSourceType == InputSourceType.INPUT_SOURCE_PHONE && androidDevice != null) {
				touchDrawControlFactory.startPhoneInputDraw(androidDevice);
			}
		}
	}

	@Override
	public void inputSourceChangeListener(InputSourceType inputSourceType) {
		HLog.il("Input source change to " + inputSourceType);

		this.inputSourceType = inputSourceType;
		if (inputSourceType == InputSourceType.INPUT_SOURCE_PHONE) {
			if (androidDevice != null) {
				touchDrawControlFactory.startPhoneInputDraw(androidDevice);
			}
		} else if (inputSourceType == InputSourceType.INPUT_SOURCE_LOG) {
			//touchDrawControlFactory.stopDraw();
		}
	}

	@Override
	public void colorChangeEvent(String settingType, Color color) {
		if (settingType.equals(EnvironmentSettingPanel.MESSAGE_AND_CONTROL_VIEW_COLOR_CONFIG_NAME)) {
			setBackground(color);
		}
	}
}
