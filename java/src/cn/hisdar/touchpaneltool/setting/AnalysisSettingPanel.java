package cn.hisdar.touchpaneltool.setting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.radishlib.ui.LinerPanel;
import cn.hisdar.touchpaneltool.setting.common.TextFieldSetPanel;

public class AnalysisSettingPanel extends JPanel implements ControlActionListener, SettingPanelInterface {

	public static final String ANALYSIS_SETTING_CONFIG_FILE = "./Config/analysis_config.xml";
	public static final String RESUME_KEY_WORD_CONFIG_NAME = "resumeKeyword";
	public static final String SUSPEND_KEY_WORD_CONFIG_NAME = "suspendKeyword";
	public static final String KERNEL_LOG_KEY_WORD_CONFIG_NAME = "kernelLogKeyword";
	public static final String INPUT_LOG_KEY_WORD_CONFIG_NAME = "inputLogKeyword";
	
	public static final String SUSPEND_KEY_WORD = "exit mdss_dsi_panel_off";
	public static final String RESUME_KEY_WORD = "exit mdss_dsi_panel_on";
	public static final String INPUT_LOG_KEY_WORD = "input";
	public static final String KERNEL_LOG_KEY_WORD = "kmsgcat";
	
	public static final String RESUME_KEY_WORD_DESCRIPTION = "程序将会从Kernel日志中搜索该关键字，将该关键字作为手机唤醒的标志";
	public static final String SUSPEND_KEY_WORD_DESCRIPTION = "程序将会从Kernel日志中搜索该关键字，将该关键字作为手机休眠的标志";
	public static final String INPUT_LOG_KEY_WORD_DESCRIPTION = "Kernl日志文件名关键字";
	public static final String KERNEL_LOG_KEY_WORD_DESCRIPTION = "Input日志文件名关键字";
	
	private HConfig analysisSetting;
	
	private TextFieldSetPanel resumeKeywordSetPanel;
	private TextFieldSetPanel suspendKeywordSetPanel;
	private TextFieldSetPanel inputLogKeywordSetPanel;
	private TextFieldSetPanel kernelLogKeywordSetPanel;
	
	private ControlPanel controlPanel;
	
	public AnalysisSettingPanel() {
		
		analysisSetting = HConfig.getInstance(ANALYSIS_SETTING_CONFIG_FILE, true);
		
		setLayout(new BorderLayout());
		
		LinerPanel analysisPanelView = new LinerPanel();
		analysisPanelView.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		resumeKeywordSetPanel = new TextFieldSetPanel("唤醒关键字：", RESUME_KEY_WORD, RESUME_KEY_WORD_DESCRIPTION);
		suspendKeywordSetPanel = new TextFieldSetPanel("休眠关键字：", SUSPEND_KEY_WORD, SUSPEND_KEY_WORD_DESCRIPTION);
		inputLogKeywordSetPanel = new TextFieldSetPanel("Input Log关键字： ", INPUT_LOG_KEY_WORD, INPUT_LOG_KEY_WORD_DESCRIPTION);
		kernelLogKeywordSetPanel = new TextFieldSetPanel("Kernel Log关键字：", KERNEL_LOG_KEY_WORD, KERNEL_LOG_KEY_WORD_DESCRIPTION);
		
		analysisPanelView.addPanel(resumeKeywordSetPanel);
		analysisPanelView.addPanel(suspendKeywordSetPanel);
		analysisPanelView.addPanel(inputLogKeywordSetPanel);
		analysisPanelView.addPanel(kernelLogKeywordSetPanel);
		
		add(analysisPanelView, BorderLayout.CENTER);
		
		controlPanel = new ControlPanel();
		controlPanel.addControlActionListeners(this);
		JPanel controlPanelView = new JPanel(new BorderLayout());
		controlPanelView.add(controlPanel, BorderLayout.EAST);
		
		add(controlPanelView, BorderLayout.SOUTH);
		
		initConfig(resumeKeywordSetPanel, RESUME_KEY_WORD_CONFIG_NAME, RESUME_KEY_WORD);
		initConfig(suspendKeywordSetPanel, SUSPEND_KEY_WORD_CONFIG_NAME, SUSPEND_KEY_WORD);
		initConfig(inputLogKeywordSetPanel, INPUT_LOG_KEY_WORD_CONFIG_NAME, INPUT_LOG_KEY_WORD);
		initConfig(kernelLogKeywordSetPanel, KERNEL_LOG_KEY_WORD_CONFIG_NAME, KERNEL_LOG_KEY_WORD);
		
		if (!new File(ANALYSIS_SETTING_CONFIG_FILE).exists()) {
			resetEvent();
		}
	}

	private void initConfig(TextFieldSetPanel field, String configName, String defaultValue) {
		String resumeKeyword = analysisSetting.getConfigValue(configName);
		if (resumeKeyword == null) {
			resumeKeyword = defaultValue;
		}
	}
	
	private void saveConfig() {
		analysisSetting.setConfigItem(new ConfigItem(RESUME_KEY_WORD_CONFIG_NAME, resumeKeywordSetPanel.getTextValue()));
		analysisSetting.setConfigItem(new ConfigItem(SUSPEND_KEY_WORD_CONFIG_NAME, suspendKeywordSetPanel.getTextValue()));
		
		analysisSetting.setConfigItem(new ConfigItem(INPUT_LOG_KEY_WORD_CONFIG_NAME, inputLogKeywordSetPanel.getTextValue()));
		analysisSetting.setConfigItem(new ConfigItem(KERNEL_LOG_KEY_WORD_CONFIG_NAME, kernelLogKeywordSetPanel.getTextValue()));
	}
	
	@Override
	public void resetEvent() {
		resumeKeywordSetPanel.setTextValue(RESUME_KEY_WORD);
		suspendKeywordSetPanel.setTextValue(SUSPEND_KEY_WORD);
		
		inputLogKeywordSetPanel.setTextValue(INPUT_LOG_KEY_WORD);
		kernelLogKeywordSetPanel.setTextValue(KERNEL_LOG_KEY_WORD);
		
		saveConfig();
	}

	@Override
	public void submitEvent() {
		saveConfig();
	}

	@Override
	public void finishEvent() {
		saveConfig();
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
