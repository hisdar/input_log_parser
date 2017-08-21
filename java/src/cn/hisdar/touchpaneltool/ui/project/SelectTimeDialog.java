package cn.hisdar.touchpaneltool.ui.project;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.hisdar.radishlib.ui.UIAdapter;

public class SelectTimeDialog extends JDialog implements ActionListener {

	private JLabel startTimeJLabel;
	private JLabel endTimeJLabel;
	private JTextField startTimeField;
	private JTextField endTimeField;
	
	private JButton commitButton;
	
	private String selectedStartTime;
	private String selectedEndTime;
	
	private SelectTimeDialog(JComponent fatherComponent) {
		setTitle("选择解析的时间区间");
		setSize(200, 130);
		setLocation(UIAdapter.getCenterLocation(fatherComponent, this));
		
		JPanel timeSelectPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		JPanel startTimePanel = new JPanel(new BorderLayout());
		startTimeJLabel = new JLabel("开始时间：");
		startTimeField = new JTextField("00-00 00:00:00");
		startTimePanel.add(startTimeJLabel, BorderLayout.WEST);
		startTimePanel.add(startTimeField, BorderLayout.CENTER);
		
		JPanel endTimePanel = new JPanel(new BorderLayout());
		endTimeJLabel = new JLabel("结束时间：");
		endTimeField = new JTextField("12-31 23:59:59");
		endTimePanel.add(endTimeJLabel, BorderLayout.WEST);
		endTimePanel.add(endTimeField, BorderLayout.CENTER);
		
		timeSelectPanel.add(startTimePanel);
		timeSelectPanel.add(endTimePanel);
		
		setLayout(new BorderLayout());
		add(timeSelectPanel);
		
		commitButton = new JButton("确定");
		commitButton.addActionListener(this);
		JPanel actionPanel = new JPanel(new GridLayout(1, 1, 5, 5));
		actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		actionPanel.add(commitButton);
		
		add(actionPanel, BorderLayout.SOUTH);
	}
	
	public static SelectTimeDialog showSelectTimeDialog(JComponent component) {
		
		SelectTimeDialog selectTimeDialog = new SelectTimeDialog(component);
		selectTimeDialog.setModal(true);
		selectTimeDialog.setVisible(true);
		
		return selectTimeDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == commitButton) {
			// 检查时间是否合法
			selectedEndTime = endTimeField.getText().trim();
			selectedStartTime = startTimeField.getText().trim();
			setVisible(false);
		}
	}

	public String getSelectedStartTime() {
		return selectedStartTime;
	}

	public String getSelectedEndTime() {
		return selectedEndTime;
	}

}
