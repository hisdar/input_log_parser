package cn.hisdar.touchpaneltool.ui.replay2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javafx.scene.layout.Border;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingChangeListener;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingPanel;
import cn.hisdar.touchpaneltool.ui.show.OutputClearListener;
import sun.net.www.content.image.jpeg;

public class OutputControlAndMessagePanel extends JPanel 
	implements ReplayMessageSetInterface, ActionListener, EnvironmentSettingChangeListener {

	private final static Color DEFAULT_BACKGROUD_COLOR = new Color(0xbcc7d8);

	public ArrayList<OutputClearListener> clearInterfaces;
	
	JLabel timeMessageLabel = null;
	JButton clearButton;
	
	public OutputControlAndMessagePanel() {
		
		clearInterfaces = new ArrayList<OutputClearListener>();
		
		setBackground(DEFAULT_BACKGROUD_COLOR);
		setLayout(new BorderLayout());
		
		add(getControlPanel(), BorderLayout.WEST);
		add(getMessagePanel(), BorderLayout.EAST);
		EnvironmentSettingPanel.addEnvironmentSettingChangeListener(this);
	}
	
	private JPanel getControlPanel() {
		FlowLayout controlPanelLayout = new FlowLayout();
		controlPanelLayout.setAlignment(FlowLayout.LEFT);
		controlPanelLayout.setHgap(3);
		controlPanelLayout.setVgap(1);
		JPanel controlPanel = new JPanel(controlPanelLayout);
		controlPanel.setOpaque(false);
		
		clearButton = new JButton(new ImageIcon("./Image/delete_up.png"));
		clearButton.setPressedIcon(new ImageIcon("./Image/delete_down.png"));
		clearButton.addActionListener(this);
		clearButton.setBorder(null);
		clearButton.setOpaque(false);
		
		controlPanel.add(clearButton);
		
		return controlPanel;
	}
	
	private JPanel getMessagePanel() {
		FlowLayout messagePanelLayout = new FlowLayout();
		messagePanelLayout.setAlignment(FlowLayout.RIGHT);
		messagePanelLayout.setHgap(3);
		messagePanelLayout.setVgap(1);
		JPanel messagePanel = new JPanel(messagePanelLayout);
		messagePanel.setOpaque(false);
		
		timeMessageLabel = new JLabel(" ");
		messagePanel.add(timeMessageLabel);
		
		return messagePanel;
	}
	
	@Override
	public void setTimeMessage(String timeMessage) {
		timeMessageLabel.setText(timeMessage);
	}
	
	public void addOutputClearListener(OutputClearListener listener) {
		for (int i = 0; i < clearInterfaces.size(); i++) {
			if (clearInterfaces.get(i) == listener) {
				return;
			}
		}
		
		clearInterfaces.add(listener);
	}
	
	public void removeOutputClearListener(OutputClearListener listener) {
		int listenersCount = clearInterfaces.size();
		for (int i = listenersCount; i >= 0; i--) {
			if (clearInterfaces.get(i) == listener) {
				clearInterfaces.remove(i);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clearButton) {
			notifyClearEvent();
		}
	}

	private void notifyClearEvent() {
		for (int i = 0; i < clearInterfaces.size(); i++) {
			clearInterfaces.get(i).clearEvent();
		}
	}

	@Override
	public void colorChangeEvent(String settingType, Color color) {
		if (settingType.equals(EnvironmentSettingPanel.MESSAGE_AND_CONTROL_VIEW_COLOR_CONFIG_NAME)) {
			setBackground(color);
		}
	}
}
