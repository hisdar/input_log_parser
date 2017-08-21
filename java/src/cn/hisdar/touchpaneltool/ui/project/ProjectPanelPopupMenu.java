package cn.hisdar.touchpaneltool.ui.project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import javafx.stage.DirectoryChooser;


public class ProjectPanelPopupMenu extends JPopupMenu implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3417693419845340012L;
	private JMenuItem addProjectFileMenu;
	private JMenuItem addProjectFolderMenu;
	private JMenuItem deleteProjectMenu;
	private JMenuItem loadProjectMenu;
	private JMenuItem openFileFolderMenu;
	
	private ProjectPopupMenuEventListener eventListener;
	
	public ProjectPanelPopupMenu(ProjectPopupMenuEventListener eventListener) {
		
		this.eventListener = eventListener;
		
		loadProjectMenu = new JMenuItem("��            ��");
		addProjectFolderMenu = new JMenuItem("���LogĿ¼");
		addProjectFileMenu = new JMenuItem("���Log�ļ�");
		//addProjectMenu = new JMenuItem("���Log�ļ�/Ŀ¼");
		deleteProjectMenu = new JMenuItem("ɾ��Log�ļ�/Ŀ¼");
		openFileFolderMenu = new JMenuItem("���ļ�����λ��");
		
		loadProjectMenu.addActionListener(this);
		addProjectFolderMenu.addActionListener(this);
		addProjectFileMenu.addActionListener(this);
		deleteProjectMenu.addActionListener(this);
		openFileFolderMenu.addActionListener(this);
		
		add(loadProjectMenu);
		add(addProjectFolderMenu);
		add(addProjectFileMenu);
		add(deleteProjectMenu);
		add(openFileFolderMenu);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addProjectFileMenu) {
			if (eventListener != null) {
				eventListener.addProjectEvent(false);
			}
		} else if (e.getSource() == addProjectFolderMenu) {
			if (eventListener != null) {
				eventListener.addProjectEvent(true);
			}
		} else if (e.getSource() == deleteProjectMenu) {
			if (eventListener != null) {
				eventListener.deleteProjectEvent();
			}
		} else if (e.getSource() == loadProjectMenu) {
			if (eventListener != null) {
				eventListener.loadProjectEvent();
			}
		} else if (e.getSource() == openFileFolderMenu) {
			if (eventListener != null) {
				eventListener.openFileFolderEvent();
			}
		}
	}
}

