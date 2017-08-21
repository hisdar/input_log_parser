package cn.hisdar.touchpaneltool.ui.project2.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.ui.project2.view.ProjectViewActionListener;
import cn.hisdar.touchpaneltool.ui.project2.view.ProjectViewItem;
import cn.hisdar.touchpaneltool.ui.project2.view.ProjectViewItemNode;

public class ProjectPopMenuAction extends JPopupMenu implements ActionListener, ProjectViewActionListener {

	private static final long serialVersionUID = 2050474493228758713L;
	private JMenuItem loadMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenuItem openPathMenuItem;
	private JMenuItem openFileByDefaultProgramMenuItem;
	private JMenuItem createLogFolderMenuItem;
	private JMenuItem createLogFileMenuItem;
	
	private JMenu addMenu;
	
	private ArrayList<ProjectActionListener> projectActionListeners;
	
	public ProjectPopMenuAction() {
		super();
		
		projectActionListeners = new ArrayList<>();
		
		loadMenuItem = new JMenuItem("加载");
		add(loadMenuItem);
		
		createLogFileMenuItem = new JMenuItem("新建工程并添加日志文件");
		createLogFolderMenuItem = new JMenuItem("新建工程并添加日志目录");
		addMenu = new JMenu("新建");
		addMenu.add(createLogFolderMenuItem);
		addMenu.add(createLogFileMenuItem);
		add(addMenu);
		
		openFileByDefaultProgramMenuItem = new JMenuItem("打开");
                                                                  		//add(openFileByDefaultProgramMenuItem);
		
		deleteMenuItem = new JMenuItem("删除");
		add(deleteMenuItem);
		
		openPathMenuItem = new JMenuItem("打开文件所在位置");
		add(openPathMenuItem);
		
		loadMenuItem.addActionListener(this);
		createLogFileMenuItem.addActionListener(this);
		createLogFolderMenuItem.addActionListener(this);
		openFileByDefaultProgramMenuItem.addActionListener(this);
		openPathMenuItem.addActionListener(this);
		deleteMenuItem.addActionListener(this);
	}

	public void addProjectActionListener(ProjectActionListener l) {
		for (int i = 0; i < projectActionListeners.size(); i++) {
			if (projectActionListeners.get(i) == l) {
				return;
			}
		}
		
		projectActionListeners.add(l);
	}
	
	public void removeProjectViewMenuActionListener(ProjectActionListener l) {
		int listenerCount = projectActionListeners.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			if (projectActionListeners.get(i) == l) {
				projectActionListeners.remove(i);
			}
		}
	}
	
	public void setLoadMenuItemEnable(boolean enable) {
		loadMenuItem.setEnabled(enable);
	}
	
	public void setCreateLogFileMenuItemEnable(boolean enable) {
		createLogFileMenuItem.setEnabled(enable);
	}
	
	public void setCreateLogFolderMenuItem(boolean enable) {
		createLogFolderMenuItem.setEnabled(enable);
	}
	public void setOpenMenuItemEnable(boolean enable) {
		openFileByDefaultProgramMenuItem.setEnabled(enable);
	}
	public void setOpenPathMenuItemEnable(boolean enable) {
		openPathMenuItem.setEnabled(enable);
	}
	public void setDeleteMenuItemEnable(boolean enable) {
		deleteMenuItem.setEnabled(enable);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == loadMenuItem) {
			for (int i = 0; i < projectActionListeners.size(); i++) {
				projectActionListeners.get(i).loadEvent(ProjectLoadType.LOAD_PROJECT);
			}
		} else if (e.getSource() == createLogFileMenuItem) {
			for (int i = 0; i < projectActionListeners.size(); i++) {
				projectActionListeners.get(i).createEvent(ProjectCreateType.CREATE_FROM_FILE);
			}
		} else if (e.getSource() == createLogFolderMenuItem) {
			for (int i = 0; i < projectActionListeners.size(); i++) {
				projectActionListeners.get(i).createEvent(ProjectCreateType.CREATE_FROM_FOLDER);
			}
		} else if (e.getSource() == openFileByDefaultProgramMenuItem) {
			for (int i = 0; i < projectActionListeners.size(); i++) {
				projectActionListeners.get(i).openEvent(ProjectOpenType.OPEN_FILE_BY_DEFAULT_PROGRAM);
			}
		} else if (e.getSource() == openPathMenuItem) {
			for (int i = 0; i < projectActionListeners.size(); i++) {
				projectActionListeners.get(i).openEvent(ProjectOpenType.OPEN_SRC_PATH);
			}
		} else if (e.getSource() == deleteMenuItem) {
			for (int i = 0; i < projectActionListeners.size(); i++) {
				projectActionListeners.get(i).deleteEvent();
			}
		}
	}

	@Override
	public void showOptionsEvent(ProjectViewItem selectedProject, Component invoker, int x, int y) {
		HLog.dl("ProjectPopMenuAction.showOptionsEvent()");
		
		if (selectedProject == null) {
			loadMenuItem.setEnabled(false);
			deleteMenuItem.setEnabled(false);
			openPathMenuItem.setEnabled(false);;
			openFileByDefaultProgramMenuItem.setEnabled(false);
			createLogFolderMenuItem.setEnabled(true);
			createLogFileMenuItem.setEnabled(true);
		} else {
			ProjectViewItemNode[] selectedNodes = selectedProject.getSelectedNodes();
			if (selectedNodes == null || selectedNodes.length <= 0) {
				loadMenuItem.setEnabled(true);
				deleteMenuItem.setEnabled(true);
				openPathMenuItem.setEnabled(true);
				openFileByDefaultProgramMenuItem.setEnabled(false);
				createLogFolderMenuItem.setEnabled(true);
				createLogFileMenuItem.setEnabled(true);
			} else {
				loadMenuItem.setEnabled(true);
				deleteMenuItem.setEnabled(true);
				openPathMenuItem.setEnabled(true);
				openFileByDefaultProgramMenuItem.setEnabled(true);
				createLogFolderMenuItem.setEnabled(true);
				createLogFileMenuItem.setEnabled(true);
			}
		}
		
		show(invoker, x, y);
	}
}
