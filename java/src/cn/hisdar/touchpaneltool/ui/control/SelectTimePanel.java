package cn.hisdar.touchpaneltool.ui.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.ui.control.InputSourceSetPanel.InputSourceType;
import cn.hisdar.touchpaneltool.ui.project.ProjectHandlerInterface;
import cn.hisdar.touchpaneltool.ui.project.ProjectLoadListener;
import cn.hisdar.touchpaneltool.ui.project.ProjectView;
import cn.hisdar.touchpaneltool.ui.project.SelectTimeDialog;

public class SelectTimePanel extends JPanel 
	implements ActionListener, ProjectLoadListener, InputSourceChangeListener {

	private JButton selecteTimeButton = null;
	private ProjectHandlerInterface projectHandler = null;
	private InputSourceType inputSourceType = null;
	private String projectPath = null;
	
	public SelectTimePanel() {

		setLayout(new BorderLayout());

		add(getSelecteTimePanel(), BorderLayout.CENTER);
		ProjectView.addProjectLoadListener(this);
		InputSourceSetPanel.addInputSourceTypeChangeListener(this);
	}
	
	private JPanel getSelecteTimePanel() {
		selecteTimeButton = new JButton(new ImageIcon("./Image/select.png"));
		selecteTimeButton.addActionListener(this);
		selecteTimeButton.setBorder(null);
		selecteTimeButton.setOpaque(false);
		selecteTimeButton.setEnabled(false);
		
		JPanel selectTimePanel = new JPanel();
		selectTimePanel.setLayout(new BorderLayout());
		selectTimePanel.add(selecteTimeButton);
		selectTimePanel.setOpaque(false);
		
		return selectTimePanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == selecteTimeButton) {
			selectTimeButtonEventHandle();
		} else {
			
		}
	}
	
	private void selectTimeButtonEventHandle() {
		if (projectHandler != null) {
			SelectTimeDialog selectTimeDialog = SelectTimeDialog.showSelectTimeDialog(null);
			if (selectTimeDialog.getSelectedEndTime() == null || selectTimeDialog.getSelectedStartTime() == null) {
				return;
			}
			
			String parseStartTime = selectTimeDialog.getSelectedStartTime();
			String parseEndTime = selectTimeDialog.getSelectedEndTime();
			
			projectHandler.setEventSourceTime(parseStartTime, parseEndTime);
		} else {
			JOptionPane.showMessageDialog(null, "工程未加载，请先加载工程后再选择解析的时间区间！", "错误", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void projectLoadEvent(String projectPath, ProjectHandlerInterface projectHandler) {
		this.projectHandler = projectHandler;
		this.projectPath = projectPath;
		HLog.il("Project path:" + projectPath);
		if (inputSourceType == InputSourceType.INPUT_SOURCE_LOG) {
			selecteTimeButton.setEnabled(true);
		}
	}

	@Override
	public void inputSourceChangeListener(InputSourceType inputSourceType) {
		this.inputSourceType = inputSourceType;
		if (inputSourceType == InputSourceType.INPUT_SOURCE_PHONE) {
			selecteTimeButton.setEnabled(false);
		} else if (inputSourceType == InputSourceType.INPUT_SOURCE_LOG) {
			if (projectPath != null) {
				selecteTimeButton.setEnabled(true);
			}
		}
	}
}
