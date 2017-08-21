package cn.hisdar.touchpaneltool.ui.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.StringAdapter;
import cn.hisdar.radishlib.ui.UIAdapter;

public class ResolutionSettingDialog extends JDialog implements ActionListener,
																KeyListener {
	
	private final static Color DEFAULT_DIVIDER_COLOR = new Color(0x293955);
	
	private Dimension resolutionDimension = null;
	private JTextField widthField = null;
	private JTextField heightField = null;
	
	private JButton okButton = null;
	private JButton cancleButton = null;
	
	private ResolutionSettingDialog() {
		setTitle("设置分辨率");
		setSize(200, 150);
		setLocation(UIAdapter.getCenterLocation(null, this));
		addKeyListener(this);
		
		setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(DEFAULT_DIVIDER_COLOR);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	
		JLabel widthLabel = new JLabel("宽：");
		JLabel heightLabel = new JLabel("高：");
		
		widthField = new JTextField();
		heightField = new JTextField();
		
		widthField.addKeyListener(this);
		heightField.addKeyListener(this);
		
		JPanel widthPanel = new JPanel();
		JPanel heightPanel = new JPanel();
		widthPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		heightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		
		widthPanel.setLayout(new BorderLayout());
		heightPanel.setLayout(new BorderLayout());
		
		widthPanel.add(widthLabel, BorderLayout.WEST);
		heightPanel.add(heightLabel, BorderLayout.WEST);
		
		widthPanel.add(widthField, BorderLayout.CENTER);
		heightPanel.add(heightField, BorderLayout.CENTER);
		
		JPanel resolutionPanel = new JPanel();
		resolutionPanel.setLayout(new GridLayout(2, 1, 5, 5));
		resolutionPanel.add(widthPanel);
		resolutionPanel.add(heightPanel);
		
		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new GridLayout(1, 2, 5, 5));
		actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 10));
		okButton = new JButton("确定");
		okButton.setFocusable(true);
		cancleButton = new JButton("取消");
		
		okButton.addActionListener(this);
		cancleButton.addActionListener(this);
		
		actionPanel.add(cancleButton);
		actionPanel.add(okButton);
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(resolutionPanel, BorderLayout.CENTER);
		mainPanel.add(actionPanel, BorderLayout.SOUTH);
		
		add(mainPanel, BorderLayout.CENTER);
	}
	
	public Dimension getResolution() {
		return resolutionDimension;
	}
	
	public static ResolutionSettingDialog showResolutionSettingDialog() {
		ResolutionSettingDialog resolutionSettingDialog = new ResolutionSettingDialog();
		resolutionSettingDialog.setModal(true);
		resolutionSettingDialog.setVisible(true);
		
		return resolutionSettingDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			okButtonEventHandler();
		} else if (e.getSource() == cancleButton) {
			resolutionDimension = null;
			setVisible(false);
		}
	}
	
	private void okButtonEventHandler() {
		resolutionDimension = null;
		
		String widthString = widthField.getText().trim();
		String heightString = heightField.getText().trim();
		
		if (!StringAdapter.isNumbers(widthString) || !StringAdapter.isNumbers(heightString)) {
			JOptionPane.showMessageDialog(this, "分辨率只能输入大于0的数字！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			int width = Integer.parseInt(widthString);
			int height = Integer.parseInt(heightString);
			
			resolutionDimension = new Dimension(width, height);
		} catch (NumberFormatException e2) {
			JOptionPane.showMessageDialog(this, "分辨率输入不合法！", "错误", JOptionPane.ERROR_MESSAGE);
			HLog.el(e2);
			return;
		}
		
		setVisible(false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			okButtonEventHandler();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
