package cn.hisdar.touchpaneltool.ui.control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;

public class ResolutionSetPanel extends JPanel
	implements ActionListener {
	
	public final static String RESOLUTION_CONFIG_FILE_PATH = "./Config/resolution_config.xml";
	public final static String RESOLUTION_CONFIG_ITEM_NAME = "resolution";
	
	private final static String SET_RESOLUTION_BY_USER_TAGET = "手动设置";
	private final static String MANAGE_RESOLUTION_TAGET = "删除设置";
	
	private static ArrayList<ResolutionChangeListener> settingChangeListeners = new ArrayList<ResolutionChangeListener>();
	
	private ArrayList<String> resolutionListString = null;
	private ArrayList<Dimension> resolutionList = null;
	private JComboBox<String> resolutionComboBox = null;
	private HConfig resolutionConfig = null;
	private static Dimension selectedResolution = new Dimension(720, 1280);
	
	public ResolutionSetPanel() {
		resolutionListString = new ArrayList<String>();
		initConfig();
		
		setLayout(new BorderLayout());
		add(getResolutionRatioSetPanel(), BorderLayout.CENTER);
		setOpaque(false);
	}
	
	private void initConfig() {
		resolutionList = new ArrayList<Dimension>();
		resolutionConfig = HConfig.getInstance(RESOLUTION_CONFIG_FILE_PATH);
		
		ArrayList<ConfigItem> configItems = null;
		if (resolutionConfig != null) {
			if (resolutionConfig != null) {
				configItems = resolutionConfig.getConfigItemList();
			}
		}
		
		if (configItems == null || configItems.size() == 0) {
		
			resolutionListString.add("720×1280");
			resolutionListString.add("1080×1920");
			
			resolutionList.add(new Dimension(720, 1280));
			resolutionList.add(new Dimension(1080, 1920));
		} else {
			// 先把已有的信息删除，在把新的加进来
			while (resolutionList.size() > 0) {
				resolutionList.remove(0);
			}
			
			while (resolutionListString.size() > 0) {
				resolutionListString.remove(0);
			}
			
			for (int i = 0; i < configItems.size(); i++) {
				String configItemString = configItems.get(i).value;
				String[] resolution = configItemString.split("\\*");
				int width = Integer.parseInt(resolution[0]);
				int height = Integer.parseInt(resolution[1]);
				
				resolutionList.add(new Dimension(width, height));
				resolutionListString.add(width + "×" + height);
			}
		}
		
		selectedResolution = new Dimension(resolutionList.get(0));
		notifyResolutionChangeEvent();
	}
	
	private JPanel getResolutionRatioSetPanel() {
		JPanel resolutionRatioSetPanel = new JPanel();
		((FlowLayout)resolutionRatioSetPanel.getLayout()).setHgap(4);
		((FlowLayout)resolutionRatioSetPanel.getLayout()).setVgap(0);
		resolutionRatioSetPanel.setOpaque(false);
		
		JLabel resolutionRatioSetLabel = new JLabel("分辨率：");
		
		resolutionComboBox = new JComboBox<String>();
		initResolutionComboBoxItems();
		resolutionComboBox.addActionListener(this);
		
		resolutionRatioSetPanel.add(resolutionRatioSetLabel);
		resolutionRatioSetPanel.add(resolutionComboBox);
		
		return resolutionRatioSetPanel;
	}
	
	private void initResolutionComboBoxItems() {

		while (resolutionComboBox.getItemCount() > 0) {
			resolutionComboBox.removeItemAt(0);
		}
		
		for (int i = 0; i < resolutionListString.size(); i++) {
			resolutionComboBox.addItem(resolutionListString.get(i));
		}
		
		resolutionComboBox.addItem(MANAGE_RESOLUTION_TAGET);
		resolutionComboBox.addItem(SET_RESOLUTION_BY_USER_TAGET);
		resolutionComboBox.revalidate();
	}

	private void saveConfig() {
		resolutionConfig.clear();
		for (int i = 0; i < resolutionList.size(); i++) {
			Dimension resolution = resolutionList.get(i);
			resolutionConfig.addConfigItem(new ConfigItem(RESOLUTION_CONFIG_ITEM_NAME, resolution.width + "*" + resolution.height));
		}
	}
	
	private void resolutionComboBoxEventHandle() {
		if (resolutionComboBox.getSelectedItem().toString().equals(SET_RESOLUTION_BY_USER_TAGET)) {
			ResolutionSettingDialog resolutionSettingDialog = ResolutionSettingDialog.showResolutionSettingDialog();
			Dimension newResolution = resolutionSettingDialog.getResolution();
			if (newResolution == null) {
				resolutionComboBox.setSelectedIndex(0);
				return;
			}
			
			// 先检查当前的分辨率值是否在列表中，如果在列表中，就不要增加了
			for (int i = resolutionList.size() - 1; i >= 0 ; i--) {
				if (resolutionList.get(i).width == newResolution.width 
						&& resolutionList.get(i).height == newResolution.height) {
					resolutionList.remove(i);
					resolutionListString.remove(i);
				}
			}
		
			resolutionList.add(0, newResolution);
			resolutionListString.add(0, newResolution.width + "×" + newResolution.height);
			
			// 重新调整分辨率的顺序，总是把刚设置的分辨率信息放到最前
			selectedResolution.setSize(newResolution);
			initResolutionComboBoxItems();
			notifyResolutionChangeEvent();
		} else if (resolutionComboBox.getSelectedItem().toString().equals(MANAGE_RESOLUTION_TAGET)) {
			// 显示分辨率管理对话框
			ResolutionDeleteDialog resolutionDeleteDialog = ResolutionDeleteDialog.showResolutionDeleteDialog();
			initConfig();
			initResolutionComboBoxItems();
		} else {
			
			int index = resolutionComboBox.getSelectedIndex();
			selectedResolution =  resolutionList.get(index);
			resolutionList.remove(index);
			resolutionList.add(0, selectedResolution);
			
			String selectedResolutionString = resolutionListString.get(index);
			resolutionListString.remove(index);
			resolutionListString.add(0, selectedResolutionString);
			
			initResolutionComboBoxItems();
			notifyResolutionChangeEvent();
		}
		
		saveConfig();
	}
	
	private void notifyResolutionChangeEvent() {
		for (int i = 0; i < settingChangeListeners.size(); i++) {
			settingChangeListeners.get(i).resolutionChangeEvent(selectedResolution);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == resolutionComboBox) {
			resolutionComboBoxEventHandle();
		} else {
			
		}
	}
	
	public static void removeResolutionChangeListener(ResolutionChangeListener listener) {
		int listenerCount = settingChangeListeners.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			if (settingChangeListeners.get(i) == listener) {
				settingChangeListeners.remove(i);
			}
		}
	}
	
	public static void addResolutionChangeListener(ResolutionChangeListener listener) {
		for (int i = 0; i < settingChangeListeners.size(); i++) {
			if (settingChangeListeners.get(i) == listener) {
				return;
			}
		}
		
		settingChangeListeners.add(listener);
		listener.resolutionChangeEvent(selectedResolution);
	}
}
