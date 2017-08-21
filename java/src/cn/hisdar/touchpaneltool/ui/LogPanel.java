package cn.hisdar.touchpaneltool.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LogPanel extends JPanel {
	
	private JTextArea logArea = null;
	private JScrollPane logScrollPane = null;
	
	public LogPanel() {
		setLayout(new BorderLayout());
		
		logArea = new JTextArea();
		logArea.setBackground(Color.BLACK);
		logArea.setForeground(new Color(0, 180, 0));
		logScrollPane = new JScrollPane(logArea);
		
		add(logScrollPane, BorderLayout.CENTER);
	}

}
