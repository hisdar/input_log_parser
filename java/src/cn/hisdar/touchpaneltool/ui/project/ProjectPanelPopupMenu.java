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
		
		loadProjectMenu = new JMenuItem("加            载");
		addProjectFolderMenu = new JMenuItem("添加Log目录");
		addProjectFileMenu = new JMenuItem("添加Log文件");
		//addProjectMenu = new JMenuItem("添加Log文件/目录");
		deleteProjectMenu = new JMenuItem("删除Log文件/目录");
		openFileFolderMenu = new JMenuItem("打开文件所在位置");
		
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

