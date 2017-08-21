package cn.hisdar.touchpaneltool.ui.control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.touchpaneltool.setting.common.HComboBoxItemChangeListener;
import cn.hisdar.touchpaneltool.setting.common.HComboBoxPanel;
import cn.hisdar.touchpaneltool.ui.control.TouchShowBackGround.TouchShowBackgroundType;

public class GriddlingLineSetPanel extends JPanel 
	implements HComboBoxItemChangeListener, TouchShowBackgroundChangeListener {
	
	private final static String DISABLE_GRIDDING_LINE_TEXT = "禁用网格线";
	private final static String ENABLE_GRIDDING_LINE_TEXT = "启用网格线";
	private final static String SET_GRIDDING_LINE_TEXT = "设置网格线";
	
	private final static String GRIDDING_LINE_ROW_COUNT_CONFIG_NAME = "griddingLineRowCount";
	private final static String GRIDDING_LINE_COLUMN_COUNT_CONFIG_NAME = "griddingLineColumnCount";
	private final static String GRIDDING_LINE_CONFIG_FILE_PATH = "./Config/gridding_line_config.xml";
	
	private static ArrayList<GriddingLineStateChangeListener> griddingLineListeners = new ArrayList<GriddingLineStateChangeListener>();
	private static boolean isShowGriddingLine = false;
	private static Dimension griddingLineSize = new Dimension(23, 13);
	
	private HComboBoxPanel griddingLineComboBox = null;
	
	private HConfig griddingLineConfig = null;
	private TouchShowBackGround touchShowBackGround = null;
	
	public GriddlingLineSetPanel() {
		initConfig();
		
		griddingLineComboBox = new HComboBoxPanel("网格线：");
		griddingLineComboBox.addItem(DISABLE_GRIDDING_LINE_TEXT);
		griddingLineComboBox.addItem(ENABLE_GRIDDING_LINE_TEXT);
		griddingLineComboBox.addItem(SET_GRIDDING_LINE_TEXT);
		
		griddingLineComboBox.addItemChangeListener(this);
		
		setLayout(new BorderLayout());
		add(griddingLineComboBox, BorderLayout.CENTER);
		setOpaque(false);
		
		touchShowBackGround = TouchShowBackGround.getInstance();
		touchShowBackGround.addTouchShowBackgroundChangeListener(this);
	}
	
	private void initConfig() {
		
		griddingLineSize = new Dimension(23, 13);
		griddingLineConfig = HConfig.getInstance(GRIDDING_LINE_CONFIG_FILE_PATH);
		if (griddingLineConfig != null) {
			griddingLineSize.width = griddingLineConfig.getConfigValue(GRIDDING_LINE_ROW_COUNT_CONFIG_NAME, 23);
			griddingLineSize.height = griddingLineConfig.getConfigValue(GRIDDING_LINE_COLUMN_COUNT_CONFIG_NAME, 13);
		}
	}

	@Override
	public void itemChangeEvent(JPanel source, String itemName) {
		if (source == griddingLineComboBox) {
			if (itemName.equals(DISABLE_GRIDDING_LINE_TEXT)) {
				isShowGriddingLine = false;
			} else if (itemName.equals(ENABLE_GRIDDING_LINE_TEXT)) {
				isShowGriddingLine = true;
			} else if (itemName.equals(SET_GRIDDING_LINE_TEXT)) {
				griddingLineSetEventHandle();
				isShowGriddingLine = true;
			}
			
			notifyGriddingLineStateChangeEvent(isShowGriddingLine);
		} else {
			
		}
	}
	
	private void griddingLineSetEventHandle() {
		GriddingLineSettingDialog griddingLineSettingDialog = GriddingLineSettingDialog.showResolutionSettingDialog();
		Dimension newGriddingLineSize = griddingLineSettingDialog.getGriddingLineSize();
		if (newGriddingLineSize == null) {
			griddingLineComboBox.setSelectedIndex(0);
			return;
		}
	
		griddingLineSize.setSize(newGriddingLineSize);
		
		griddingLineConfig.setConfigItem(new ConfigItem(GRIDDING_LINE_ROW_COUNT_CONFIG_NAME, griddingLineSize.width));
		griddingLineConfig.setConfigItem(new ConfigItem(GRIDDING_LINE_COLUMN_COUNT_CONFIG_NAME, griddingLineSize.height));
	}
	
	private void notifyGriddingLineStateChangeEvent(boolean show) {
		for (int i = 0; i < griddingLineListeners.size(); i++) {
			griddingLineListeners.get(i).grridingLineStateChangeEvent(show, griddingLineSize.width, griddingLineSize.height);
		}
	}
	
	public static void addGriddingLineStateChangeListener(GriddingLineStateChangeListener listener) {
		for (int i = 0; i < griddingLineListeners.size(); i++) {
			if (griddingLineListeners.get(i) == listener) {
				return;
			}
		}
		
		griddingLineListeners.add(listener);
		listener.grridingLineStateChangeEvent(isShowGriddingLine, griddingLineSize.width, griddingLineSize.height);
	}
	
	public static void removeGriddingLineStateChangeListener(GriddingLineStateChangeListener listener) {
		int listenerCount = griddingLineListeners.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			if (listener == griddingLineListeners.get(i)) {
				griddingLineListeners.remove(i);
			}
		}
	}

	@Override
	public void touchShowBackgroundChangeEvent(BufferedImage backgroundImage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void touchShowBackgroundTypeChangeEvent(
			TouchShowBackgroundType backgroundType) {
		if (backgroundType == TouchShowBackgroundType.BACKGROUND_COLOR) {
			// 显示网格线
			griddingLineComboBox.setSelectedIndex(1);
		} else if (backgroundType == TouchShowBackgroundType.BACKGROUND_IMAGE) {
			// 禁用网格线
			if (griddingLineComboBox.getSelectedItem().toString().equals(ENABLE_GRIDDING_LINE_TEXT)) {
				griddingLineComboBox.setSelectedIndex(0);
			}
		} else if (backgroundType == TouchShowBackgroundType.BACKGROUND_FROM_PHONE) {
			// 禁用网格线
			if (griddingLineComboBox.getSelectedItem().toString().equals(ENABLE_GRIDDING_LINE_TEXT)) {
				griddingLineComboBox.setSelectedIndex(0);
			}
		}
	}
}
