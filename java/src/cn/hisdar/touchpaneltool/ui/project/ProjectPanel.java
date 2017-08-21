package cn.hisdar.touchpaneltool.ui.project;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ProjectPanel extends JPanel {

	private ProjectView inputLogTree = null;
	public ProjectPanel() {
		setLayout(new BorderLayout());
		initFilePanel();
	}
	
	private void initFilePanel() {
		
		inputLogTree = new ProjectView("[Input-Log]¹¤³Ì", "input");
		JScrollPane logFileScrollPane = new JScrollPane(inputLogTree);
		add(logFileScrollPane);
	}
}