package cn.hisdar.touchpaneltool.setting.common;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.hisdar.lib.log.HLog;

public class HComboBoxPanel extends JPanel implements ActionListener {

	private JLabel titleLabel = null;
	private JComboBox<String> listBox = null;
	private ArrayList<String> itemList = null;
	private ArrayList<HComboBoxItemChangeListener> changeListeners = null;
	
	public HComboBoxPanel(String title) {
		
		itemList = new ArrayList<String>();
		changeListeners = new ArrayList<HComboBoxItemChangeListener>();
		
		titleLabel = new JLabel(title);
		titleLabel.setOpaque(false);
		listBox = new JComboBox<String>();
		listBox.addActionListener(this);
		
		setLayout(new BorderLayout());
		add(titleLabel, BorderLayout.WEST);
		add(listBox, BorderLayout.CENTER);
		
		setOpaque(false);
	}
	
	public String getSelectedItem() {
		return listBox.getSelectedItem().toString();
	}
	
	public void addItem(String itemName) {
		
		for (int i = 0; i < itemList.size(); i++) {
			if (itemList.get(i).equals(itemName)) {
				
				
				return;
			}
		}
		
		itemList.add(itemName);
		listBox.addItem(itemName);
	}
	
	public void removeItem(String itemName) {
		
		for (int i = 0; i < itemList.size(); i++) {
			itemList.remove(i);
			listBox.remove(i);
			break;
		}
	}

	public void setSelectedIndex(int index) {
		listBox.setSelectedIndex(index);
	}
	
	public void addItemChangeListener(HComboBoxItemChangeListener listener) {
		for (int i = 0; i < changeListeners.size(); i++) {
			if (changeListeners.get(i) == listener) {
				return;
			}
		}
		
		changeListeners.add(listener);
	}
	
	public void removeItemChangeListener(HComboBoxItemChangeListener listener) {
		int linsterCount = changeListeners.size();
		for (int i = linsterCount - 1; i >= 0; i--) {
			if (changeListeners.get(i) == listener) {
				changeListeners.remove(i);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == listBox) {
			for (int i = 0; i < changeListeners.size(); i++) {
				changeListeners.get(i).itemChangeEvent(this, listBox.getSelectedItem().toString());
			}
		}
		
		listBox.removeActionListener(this);
		listBox.addActionListener(this);
	}
}
