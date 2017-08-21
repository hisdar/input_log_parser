package cn.hisdar.touchpaneltool.setting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.radishlib.StringAdapter;
import cn.hisdar.radishlib.ui.LinerPanel;

public class ParseSettingPanel extends LinerPanel implements ControlActionListener, SettingPanelInterface {
	
	public static final String PARSE_CONFIG_FILE_PATH = "./Config/parse_config.xml";
	
	private static final String MAX_WAIT_TIME_LABEL_TEXT = "最大间隔时间（毫秒）：";
	private static final String MAX_WAIT_TIME_EXPLAIL_TEXT = "说明：在解析InputEvent文件的时候，超过最大等待时间的事件，只等待最大等待时间";
	private static final String PREVIEW_PANEL_BUFFER_SIZE_EXPLAIN_TEXT = "提示：缓冲区越大，越影响系统性能，主要是会占用更大的内存（重启后生效）";
	private static final String SHOW_PHINE_IMAGE_TEXT = "当有手机插入的时候自动显示手机屏幕上的图片";
	private static final String PREVIEW_PANEL_BUFFER_SIZE_TEXT = "历史操作的缓冲区大小：";
	private static final String DEFAULT_RESOLUTION_TEXT = "默认分辨率：";
	private static final String DEFAULT_RESOLUTION_WIDTH_TEXT = "宽：";
	private static final String DEFAULT_RESOLUTION_HEIGHT_TEXT = "高：";
	
	public static final String MAX_WAIT_TIME_CONFIG_NAME = "maxWaitTime";
	public static final String PREVIEW_PANEL_BUFFER_SIZE_CONFIG_NAME = "previewPanelBufferSize";
	public static final String IS_SHOW_IMAGE_ON_PHONE_CONFIG_NAME = "isShowImageOnPhone";
	public static final String RESOLUTION_WIDTH_CONFIG_NAME = "resolutionWidth";
	public static final String RESOLUTION_HEIGHT_CONFIG_NAME = "resolutionHeight";
	
	public static final int DEFAULT_WAIT_TIME = 500;
	private static final int DEFAULT_MAX_WAIT_TIME_VALUE_SIZE = 6;
	private static final int DEFAULT_PREVIEW_PANEL_BUFFER_SIZE = 40;
	private static final int RESOLUTION_FIELD_SIZE = 6;
	
	private JCheckBox showPhoneImagCheckBox = null;
	
	private JLabel maxWaitTimeLabel = null;
	private JTextField maxWaitTimeField = null;
	private JLabel maxWaitTimeExplainLabel = null;
	
	// 预览界面缓冲区大小设置
	private JLabel previewPanelBufferSizeLabel = null;
	private JLabel previewPanelBufferExplainLabel = null;
	private JTextField previewPanelBufferSizeField = null;
	
	// 默认分辨率设置界面
	private JLabel defaultResolutionLabel = null;
	private JLabel defaultResolutionWidthLabel = null;
	private JLabel defaultResolutionHeightLabel = null;
	private JTextField defaultResolutionWidthField = null;
	private JTextField defaultResolutionHeightField = null;
	
	private ControlPanel controlPanel;
	
	private HConfig settingConfig = null;
	
	public ParseSettingPanel() {
		
		settingConfig = HConfig.getInstance(PARSE_CONFIG_FILE_PATH);
		
		maxWaitTimeLabel = new JLabel(MAX_WAIT_TIME_LABEL_TEXT);
		maxWaitTimeField = new JTextField(DEFAULT_WAIT_TIME + "");
		maxWaitTimeExplainLabel = new JLabel(MAX_WAIT_TIME_EXPLAIL_TEXT);

		maxWaitTimeField.setColumns(DEFAULT_MAX_WAIT_TIME_VALUE_SIZE);
		maxWaitTimeField.setHorizontalAlignment(JTextField.RIGHT);
		maxWaitTimeExplainLabel.setEnabled(false);
		
		if (settingConfig != null) {
			maxWaitTimeField.setText(settingConfig.getConfigValue(MAX_WAIT_TIME_CONFIG_NAME, DEFAULT_WAIT_TIME) + "");
		} else {
			maxWaitTimeField.setText(DEFAULT_WAIT_TIME + "");
		}
		
		JPanel maxWaitTimeValuePanel = new JPanel(new BorderLayout());
		maxWaitTimeValuePanel.add(maxWaitTimeLabel, BorderLayout.WEST);
		maxWaitTimeValuePanel.add(maxWaitTimeField, BorderLayout.CENTER);

		JPanel maxWaitTimeValueBufferPanel = new JPanel(new BorderLayout());
		maxWaitTimeValueBufferPanel.add(maxWaitTimeValuePanel, BorderLayout.WEST);

		JPanel maxWaitTimePanel = new JPanel(new GridLayout(2, 1, 1, 1));
		maxWaitTimePanel.add(maxWaitTimeValueBufferPanel);
		maxWaitTimePanel.add(maxWaitTimeExplainLabel);
		maxWaitTimePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5)));
		JPanel maxWaitTimeBufferPanel = new JPanel(new BorderLayout());
		maxWaitTimeBufferPanel.add(maxWaitTimePanel, BorderLayout.CENTER);
		maxWaitTimeBufferPanel.setBorder(BorderFactory.createTitledBorder(""));
		/**********************************************************************************************************/

		previewPanelBufferSizeLabel = new JLabel(PREVIEW_PANEL_BUFFER_SIZE_TEXT);
		previewPanelBufferExplainLabel = new JLabel(PREVIEW_PANEL_BUFFER_SIZE_EXPLAIN_TEXT);
		previewPanelBufferSizeField = new JTextField(DEFAULT_PREVIEW_PANEL_BUFFER_SIZE + "");

		previewPanelBufferSizeField.setColumns(DEFAULT_MAX_WAIT_TIME_VALUE_SIZE);
		previewPanelBufferSizeField.setHorizontalAlignment(JTextField.RIGHT);
		previewPanelBufferExplainLabel.setEnabled(false);

		if (settingConfig != null) {
			previewPanelBufferSizeField.setText(settingConfig.getConfigValue(PREVIEW_PANEL_BUFFER_SIZE_CONFIG_NAME, 40) + "");
		} else {
			previewPanelBufferSizeField.setText(40 + "");
		}
		
		JPanel previewPanelBufferValuePanel = new JPanel(new BorderLayout());
		previewPanelBufferValuePanel.add(previewPanelBufferSizeLabel, BorderLayout.WEST);
		previewPanelBufferValuePanel.add(previewPanelBufferSizeField, BorderLayout.CENTER);

		JPanel previewPanelBufferValueBufferPanel = new JPanel(new BorderLayout());
		previewPanelBufferValueBufferPanel.add(previewPanelBufferValuePanel, BorderLayout.WEST);

		JPanel previewPanelBufferSizePanel = new JPanel(new GridLayout(2, 1, 1, 1));
		previewPanelBufferSizePanel.add(previewPanelBufferValueBufferPanel);
		previewPanelBufferSizePanel.add(previewPanelBufferExplainLabel);
		previewPanelBufferSizePanel.setBorder(BorderFactory
				.createTitledBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5)));

		JPanel previewPanelBufferSizeBufferPanel = new JPanel(new BorderLayout());
		previewPanelBufferSizeBufferPanel.add(previewPanelBufferSizePanel,BorderLayout.WEST);
		previewPanelBufferSizeBufferPanel.setBorder(BorderFactory.createTitledBorder(""));
		/******************************************************************************************/

		showPhoneImagCheckBox = new JCheckBox(SHOW_PHINE_IMAGE_TEXT);
		
		if (settingConfig != null) {
			showPhoneImagCheckBox.setSelected(settingConfig.getConfigValue(IS_SHOW_IMAGE_ON_PHONE_CONFIG_NAME, false));
		} else {
			showPhoneImagCheckBox.setSelected(false);
		}
		
		JPanel showPhoneImagePanel = new JPanel(new BorderLayout());
		showPhoneImagePanel.add(showPhoneImagCheckBox, BorderLayout.CENTER);
		showPhoneImagePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		defaultResolutionLabel = new JLabel(DEFAULT_RESOLUTION_TEXT);
		defaultResolutionWidthLabel = new JLabel(DEFAULT_RESOLUTION_WIDTH_TEXT);
		defaultResolutionHeightLabel = new JLabel(DEFAULT_RESOLUTION_HEIGHT_TEXT);
		defaultResolutionWidthField = new JTextField();
		defaultResolutionHeightField = new JTextField();
		defaultResolutionWidthField.setColumns(RESOLUTION_FIELD_SIZE);
		defaultResolutionHeightField.setColumns(RESOLUTION_FIELD_SIZE);

		if (settingConfig != null) {
			defaultResolutionWidthField.setText(settingConfig.getConfigValue(RESOLUTION_WIDTH_CONFIG_NAME, 720) + "");
			defaultResolutionHeightField.setText(settingConfig.getConfigValue(RESOLUTION_HEIGHT_CONFIG_NAME, 1280) + "");
		} else {
			defaultResolutionWidthField.setText(720 + "");
			defaultResolutionHeightField.setText(1280 + "");
		}
		
		JPanel resolutionWidthPanel = new JPanel(new BorderLayout());
		resolutionWidthPanel.add(defaultResolutionWidthLabel, BorderLayout.WEST);
		resolutionWidthPanel.add(defaultResolutionWidthField, BorderLayout.CENTER);

		JPanel resolutionHeightPanel = new JPanel(new BorderLayout());
		resolutionHeightPanel.add(defaultResolutionHeightLabel,	BorderLayout.WEST);
		resolutionHeightPanel.add(defaultResolutionHeightField, BorderLayout.CENTER);

		JPanel resolutionValuePanel = new JPanel(new GridLayout(1, 2, 5, 5));
		resolutionValuePanel.add(resolutionWidthPanel);
		resolutionValuePanel.add(resolutionHeightPanel);

		JPanel resolutionPanel = new JPanel(new BorderLayout());
		resolutionPanel.add(defaultResolutionLabel, BorderLayout.WEST);
		resolutionPanel.add(resolutionValuePanel, BorderLayout.CENTER);

		JPanel resolutionBufferPanel = new JPanel(new BorderLayout());
		resolutionBufferPanel.add(resolutionPanel, BorderLayout.WEST);
		resolutionBufferPanel.setBorder(BorderFactory.createEmptyBorder(5, 6, 0, 0));

		LinerPanel basicSettingPanel = new LinerPanel();
		basicSettingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		basicSettingPanel.addPanel(maxWaitTimeBufferPanel);
		basicSettingPanel.addPanel(previewPanelBufferSizeBufferPanel);
		basicSettingPanel.addPanel(resolutionBufferPanel);
		basicSettingPanel.addPanel(showPhoneImagePanel);
		
		setLayout(new BorderLayout());
		add(basicSettingPanel, BorderLayout.CENTER);
		
		controlPanel = new ControlPanel();
		controlPanel.addControlActionListeners(this);
		JPanel contrPanelView = new JPanel(new BorderLayout());
		contrPanelView.add(controlPanel, BorderLayout.EAST);
		add(contrPanelView, BorderLayout.SOUTH);
		
		if (!new File(PARSE_CONFIG_FILE_PATH).exists()) {
			resetEvent();
		}
	}
	
	private void basicSettingSave() {

		if (settingConfig == null) {
			settingConfig = HConfig.getInstance(PARSE_CONFIG_FILE_PATH, true);
		}
		
		settingConfig.setConfigItem(new ConfigItem(
				IS_SHOW_IMAGE_ON_PHONE_CONFIG_NAME, showPhoneImagCheckBox
						.isSelected()));

		if (!StringAdapter.isNumbers(defaultResolutionWidthField.getText())) {
			JOptionPane.showMessageDialog(null, DEFAULT_RESOLUTION_TEXT
					+ DEFAULT_RESOLUTION_WIDTH_TEXT + "只能输入数字", "提示",
					JOptionPane.ERROR_MESSAGE);
		} else {
			settingConfig.setConfigItem(new ConfigItem(
					RESOLUTION_WIDTH_CONFIG_NAME, Integer
							.parseInt(defaultResolutionWidthField.getText())));
		}

		if (!StringAdapter.isNumbers(defaultResolutionHeightField.getText())) {
			JOptionPane.showMessageDialog(null, DEFAULT_RESOLUTION_TEXT
					+ DEFAULT_RESOLUTION_HEIGHT_TEXT + "只能输入数字", "提示",
					JOptionPane.ERROR_MESSAGE);
		} else {
			settingConfig.setConfigItem(new ConfigItem(
					RESOLUTION_HEIGHT_CONFIG_NAME, Integer
							.parseInt(defaultResolutionHeightField.getText())));
		}

		if (!StringAdapter.isNumbers(maxWaitTimeField.getText())) {
			JOptionPane.showMessageDialog(null, MAX_WAIT_TIME_LABEL_TEXT
					+ "只能输入数字", "提示", JOptionPane.ERROR_MESSAGE);
		} else {
			settingConfig.setConfigItem(new ConfigItem(
					MAX_WAIT_TIME_CONFIG_NAME, Integer
							.parseInt(maxWaitTimeField.getText())));
		}

		if (!StringAdapter.isNumbers(previewPanelBufferSizeField.getText())) {
			JOptionPane.showMessageDialog(null, PREVIEW_PANEL_BUFFER_SIZE_TEXT
					+ "只能输入数字", "提示", JOptionPane.ERROR_MESSAGE);
		} else {
			settingConfig.setConfigItem(new ConfigItem(
					PREVIEW_PANEL_BUFFER_SIZE_CONFIG_NAME, Integer
							.parseInt(previewPanelBufferSizeField.getText())));
		}
	}

	@Override
	public void resetEvent() {
		defaultResolutionWidthField.setText(720 + "");
		defaultResolutionHeightField.setText(1280 + "");
		showPhoneImagCheckBox.setSelected(false);
		previewPanelBufferSizeField.setText(DEFAULT_PREVIEW_PANEL_BUFFER_SIZE + "");
		maxWaitTimeField.setText(DEFAULT_WAIT_TIME + "");
		
		basicSettingSave();
	}

	@Override
	public void submitEvent() {
		basicSettingSave();
	}

	@Override
	public void finishEvent() {
		basicSettingSave();
	}

	@Override
	public void addControlActionListener(ControlActionListener listener) {
		controlPanel.addControlActionListeners(listener);
	}

	@Override
	public void removeControlActionListener(ControlActionListener listener) {
		controlPanel.removeControlActionListeners(listener);
	}
}
