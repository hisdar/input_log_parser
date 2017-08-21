package cn.hisdar.touchpaneltool.androidDevice;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.hisdar.radishlib.ui.UIAdapter;

public class SelectAndroidDeviceDialig extends JDialog implements ActionListener {

	public static final int APPROVE_OPTION = 1;
	public static final int CANCLE_OPTION = 2;
	
	private JLabel deviceNameLabel = null;
	private JComboBox<String> deviceComboBox = null;
	private JButton refreshButton = null;
	private JButton commitButton = null;
	private JButton cancleButton = null;
	
	private int actionResult = CANCLE_OPTION;
	private AndroidDevice[] androidDevices = null;
	
	public SelectAndroidDeviceDialig() {
		setTitle("选择Android设备");
		setSize(400, 150);
		setResizable(false);
		setLocation(UIAdapter.getCenterLocation(null, this));
		
		deviceComboBox = new JComboBox<String>();
		deviceNameLabel = new JLabel("选择设备：");
		refreshButton = new JButton("刷新");
		refreshButton.addActionListener(this);
		
		JPanel deviceSelectPanel = new JPanel(new BorderLayout());
		deviceSelectPanel.add(deviceNameLabel, BorderLayout.WEST);
		deviceSelectPanel.add(deviceComboBox, BorderLayout.CENTER);
		deviceSelectPanel.add(refreshButton, BorderLayout.EAST);
		deviceSelectPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 10, 5));
		
		setLayout(new BorderLayout());
		add(deviceSelectPanel, BorderLayout.CENTER);
		
		JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		commitButton = new JButton("确定");
		cancleButton = new JButton("取消");
		
		cancleButton.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
		
		commitButton.addActionListener(this);
		cancleButton.addActionListener(this);
		
		actionPanel.add(cancleButton);
		actionPanel.add(commitButton);
		actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 150, 25, 20));
		
		add(actionPanel, BorderLayout.SOUTH);
	}
	
	public AndroidDevice getSelectedAndroidDevice() {

		if (androidDevices != null && androidDevices.length > deviceComboBox.getSelectedIndex()) {
			return androidDevices[deviceComboBox.getSelectedIndex()];
		}
		
		return null;
	}
	
	public int showSelectAndroidDeviceDialog() {
		refreshButtonEventHandler();

		setModal(true);
		setVisible(true);
		
		return actionResult;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == refreshButton) {
			refreshButtonEventHandler();
		} else if (e.getSource() == commitButton) {
			actionResult = APPROVE_OPTION;
			setVisible(false);
		} else if (e.getSource() == cancleButton) {
			actionResult = CANCLE_OPTION;
			setVisible(false);
		}
	}

	private void refreshButtonEventHandler() {
		androidDevices = AndroidDeviceAdapter.getInstance().getAndroidDevices();
		if (androidDevices == null) {
			return;
		}
		
		deviceComboBox.removeAllItems();
		for (int i = 0; i < androidDevices.length; i++) {
			deviceComboBox.addItem(androidDevices[i].getDeviceName());
		}
	}
}
