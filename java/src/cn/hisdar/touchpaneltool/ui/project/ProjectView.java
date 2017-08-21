package cn.hisdar.touchpaneltool.ui.project;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import cn.hisdar.input2.InputLogFormat;
import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.FileAdapter;
import cn.hisdar.touchpaneltool.config.MainFrameConfig;
import cn.hisdar.touchpaneltool.ui.project2.Project;

public class ProjectView extends JTree 
	implements TreeSelectionListener, ProjectPopupMenuEventListener, ProjectHandlerInterface {

	private static final long serialVersionUID = 8716357542400231620L;

	public static final String PROJECT_CONFIG_FILE = "./Config/project_list.xml";
	
	private ProjectTreeNode rootTreeNode = null;
	
	private DefaultTreeModel defaultTreeModel = null;
	private String filter = null;
	private ProjectPanelPopupMenu popupMenu = null;
	private TreePath selectedTreeNodePath = null;
	
	private MouseEventHandler mouseEventHandler;
	
	private ProjectAddDialog projectAddDialog;
	private ProjectLogFileInit projectInitLogFiles;
	private ProjectLoad projectLoad;
	
	private HConfig projectConfig;
	private String  eventFilePath = null;
	
	private static ArrayList<ProjectLoadListener> projectLoadListeners = new ArrayList<ProjectLoadListener>();
	
	public ProjectView(String rootNodeName, String filter) {
		
		this.filter = filter;
		
		projectAddDialog = new ProjectAddDialog();
		projectInitLogFiles = new ProjectLogFileInit();
		projectLoad = new ProjectLoad();
		
		rootTreeNode = new ProjectTreeNode(rootNodeName);
		defaultTreeModel = new DefaultTreeModel(rootTreeNode);
		setModel(defaultTreeModel);
		addTreeSelectionListener(this);
		
		mouseEventHandler = new MouseEventHandler(this);
		addMouseListener(mouseEventHandler);
		
		popupMenu = new ProjectPanelPopupMenu(this);
		
		projectConfig = HConfig.getInstance(PROJECT_CONFIG_FILE, true);
		
		loadProject();
	}
	
	// 从本地加载已有的工程
	private void loadProject() {

		// load added project
		if (projectConfig == null) {
			return;
		}
		
		ArrayList<ConfigItem> projectList = projectConfig.getConfigItemList();

		for (int i = 0; i < projectList.size(); i++) {
			String inputLogFileFolder = FileAdapter.pathCat(projectList.get(i).value, ProjectLogFileInit.INPUT_LOG_FOLDER_NAME);
			addProjectNode(projectList.get(i).description, inputLogFileFolder);
		}
	}
	
	// 添加工程节点
	public void addProjectNode(String projectName, String projectPath) {
		
		File projectFile = new File(projectPath);
		if (!projectFile.exists()) {
			//System.out.println(projectPath + " is not exist");
			return;
		} else {
			//System.out.println(projectPath + " is exist");
		}
		
		// add project node to tree
		ProjectTreeNode projectNode = new ProjectTreeNode(projectName);
		projectNode.setTreeNodeFilePath(projectPath);
		projectNode.setProjectNode(true);
		defaultTreeModel.insertNodeInto(projectNode, rootTreeNode, defaultTreeModel.getChildCount(rootTreeNode));
		
		addProjectChildNodes(projectNode, projectPath);
		
		if (!isExpanded(new TreePath(rootTreeNode))) {
			expandPath(new TreePath(rootTreeNode));
		}
	}
	
	private void addProjectChildNodes(ProjectTreeNode parentNode, String nodePath) {
		
		if (nodePath == null) {
			return;
		}
		
		File currentFile = new File(nodePath);
		if (!currentFile.exists()) {
			return;
		} else {
			//System.out.println(nodePath + " is exist");
		}
		
		if (currentFile.isFile()) {
			addProjectChildNode(parentNode, currentFile.getName(), currentFile.getPath(), null);
			//System.out.println("Add file:" + currentFile.getPath());
			return ;
		}
		
		File[] childFiles = currentFile.listFiles();
		if (childFiles == null) {
			return;
		}
		
		childFiles = ProjectLogFileInit.sortLogFiles(childFiles);
		if (childFiles == null) {
			return;
		}
		
		for (int i = 0; i < childFiles.length; i++) {
			addProjectChildNode(parentNode, childFiles[i].getName(), childFiles[i].getPath(), filter);
		}
	}
	
	private void addProjectChildNode(ProjectTreeNode parentNode, String newNodeName, String nodePath, String fileFilter) {
		
		ProjectTreeNode newNode = new ProjectTreeNode(newNodeName);
		newNode.setProjectNode(false);
		newNode.setTreeNodeFilePath(nodePath);
		
		File nodePathFile = new File(nodePath);
		// 如果添加的是目录，那么不做文件名过滤
		if (nodePathFile.isDirectory()) {
			defaultTreeModel.insertNodeInto(newNode, parentNode, defaultTreeModel.getChildCount(parentNode));
		} else if (nodePathFile.isFile()) {
			// 如果添加的是文件，那么要做文件名过滤
			if (fileFilter == null || nodePath.indexOf(fileFilter) >= 0) {
				defaultTreeModel.insertNodeInto(newNode, parentNode, defaultTreeModel.getChildCount(parentNode));
			}
		}

		if (nodePathFile.isDirectory()) {
			File[] childFileList = nodePathFile.listFiles();
			childFileList = ProjectLogFileInit.sortLogFiles(childFileList);
			if (childFileList == null) {
				return;
			}
			
			for (int i = 0; i < childFileList.length; i++) {
				String childFilePath = childFileList[i].getPath();
				String childNodeName = childFileList[i].getName();
				addProjectChildNode(newNode, childNodeName, childFilePath, filter);
			}
		}
	}
	
	private class MouseEventHandler extends MouseAdapter {
		
		private JComponent parentComponent;
		
		public MouseEventHandler(JComponent parentComponent) {
			this.parentComponent = parentComponent;
		}
		
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				popupMenu.show(parentComponent, e.getX(), e.getY());
			}
		}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		selectedTreeNodePath = e.getPath();
	}
	
	private String getTreeNodeName(TreePath treePath) {
		String treePathString = treePath.toString().trim();
		
		int startIndex = treePathString.lastIndexOf(',');
		if (startIndex < 0) {
			return null;
		}
		
		treePathString = treePathString.substring(startIndex + 1);
		if (treePathString.endsWith("]")) {
			treePathString = treePathString.substring(0, treePathString.length() - 1);
		}
		
		return treePathString.trim();
	}
	
	// 将树的结构解析成文件路劲的结构
	private String treeNodeToPath(TreePath treePath) {
		String treeNodePath = treePath.toString();
		
		int startIndex = treeNodePath.indexOf(',');
		if (startIndex < 0) {
			return null;
		}
		
		treeNodePath = treeNodePath.substring(startIndex + 1);
		treeNodePath = treeNodePath.replace(", ", "/");
		if (treeNodePath.endsWith("]")) {
			treeNodePath = treeNodePath.substring(0, treeNodePath.length() - 1);
		}
		
		return treeNodePath.trim();
	}
	
	private boolean deleteSelectedTreeNode() {
		
		ProjectTreeNode selectedNode = (ProjectTreeNode)getLastSelectedPathComponent(); 
		if (selectedNode == null || selectedNode.getParent() == null) {
			JOptionPane.showMessageDialog(null, "根节点不能被删除！", "提示", JOptionPane.WARNING_MESSAGE);
			return false;
        } 

		if (selectedNode.equals(rootTreeNode)) {
			JOptionPane.showMessageDialog(null, "工程名不能被删除！", "提示", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		// delete project file
		String projectFolder = new File(selectedNode.getTreeNodeFilePath()).getParent();
		FileAdapter.deleteFolder(projectFolder);
		
		// delete node
		defaultTreeModel.removeNodeFromParent(selectedNode);
		
		// if this is a project node, remove this project from configer file
		if (selectedNode.isProjectNode()) {
			projectConfig.removeConfigItem(selectedNode.getTreeNodeName());
		}
		
		return true;
	}
	
	private ProjectTreeNode getNodeProjectNode(ProjectTreeNode projectTreeNode) {
		ProjectTreeNode projectNode = projectTreeNode;
		while (projectNode.getParent() != null) {
			if (projectNode.getParent().equals(rootTreeNode)) {
				return projectNode;
			} else {
				projectNode = (ProjectTreeNode) projectNode.getParent();
			}
		}
		
		return null;
	}
	
	private String loadProject(ProjectTreeNode selectedProject) {
		String selectedNodeParh = selectedProject.getTreeNodeFilePath();
		String selectedProjectPath = new File(selectedNodeParh).getParent();
		HLog.il("selected project:" + selectedProjectPath);
		
		projectLoad.loadProject(selectedProjectPath);
		
		return projectLoad.getEventLogPath();
	}
	
	private String loadProjectNode(ProjectTreeNode selectedProject) {
		String selectedNodeParh = selectedProject.getTreeNodeFilePath();
		//String selectedProjectPath = new File(selectedNodeParh).getParent();
		//HLog.il("selected project:" + selectedProjectPath);
		
		ProjectTreeNode projectNode = getNodeProjectNode(selectedProject);
		HLog.il("Project node is:" + projectNode);
		String projectPath = projectNode.getTreeNodeFilePath();
		projectPath = new File(projectPath).getParent();
		projectLoad.loadProjectChildNode(projectPath, selectedNodeParh);
		
		return projectLoad.getEventLogPath();
	}
	
	private void loadProjectEventHandler() {
		if (selectedTreeNodePath == null) {
			JOptionPane.showMessageDialog(null, "请先选择要解析的日志文件！", "提示", JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String seletedNodeName = treeNodeToPath(selectedTreeNodePath).trim();
		if (seletedNodeName == null) {
			JOptionPane.showMessageDialog(null, "请选择日志文件，不能对非日志文件进行解析！", "提示", JOptionPane.WARNING_MESSAGE);
		} else {
			String eventLogFilePath = null;
			ProjectTreeNode selectedNode = (ProjectTreeNode)getLastSelectedPathComponent(); 
			if (selectedNode.getParent().equals(rootTreeNode)) {
				HLog.il("Load project");
				eventLogFilePath = loadProject(selectedNode);
			} else {
				HLog.il("Load project child file");
				eventLogFilePath = loadProjectNode(selectedNode);
			}
			
			if (eventLogFilePath == null) {
				HLog.e("Project load fail:" + selectedNode.getTreeNodeFilePath());
				JOptionPane.showMessageDialog(null, "工程加载失败！", "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			// 发送加载完成的通知
			notifyProjectLoadedEvent(eventLogFilePath);
		}
	}
	
	private void deleteProject() {
		if (selectedTreeNodePath == null) {
			JOptionPane.showMessageDialog(null, "请先选择要删除的日志文件！", "提示", JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		deleteSelectedTreeNode();
	}
	
	private boolean openProjectFileEventHandler() {
		
		ProjectTreeNode selectedNode = (ProjectTreeNode)getLastSelectedPathComponent(); 
		String selectedNodeParh = selectedNode.getTreeNodeFilePath();
		
		File selectedFile = new File(selectedNodeParh);
		
		try {
			//String command = "cmd /c start explorer " + selectedFile.getAbsolutePath();
			String command = "explorer /select, \"" + selectedFile.getAbsolutePath() + "\"";
			HLog.il("Command is:" + command);
			Runtime.getRuntime().exec(command);
		} catch (IOException e1) {
			HLog.el("Fail to open:" + selectedNodeParh);
			HLog.el(e1);
			return false;
		}
		
		return true;
	}
	
	private void addProjectEventHandler(boolean isFolder) {
		HConfig mainConfig = HConfig.getInstance("./Config/environment_config.xml");
		
		String projectBasePath =  mainConfig.getConfigValue("workspacePath") + "/Project/";
		
		Project inputProject = null;
		//inputProject = inputProject.createProjectWithProgress(projectBasePath, !isFolder);
		if (inputProject == null) {
			// user cancel project create
			return;
		}
		
		HLog.dl("addProjectEventHandler: projectName=" + inputProject.getProjectName());
		HLog.dl("addProjectEventHandler: projrctPath=" + inputProject.getProjectPath());
		HLog.dl("addProjectEventHandler: projectSrcPath=" + inputProject.getProjectSrcPath());
		
		// if create success, the following variables are not null
		if (inputProject.getProjectName() == null 
				|| inputProject.getProjectPath() == null
				|| inputProject.getProjectSrcPath() == null) {
			JOptionPane.showMessageDialog(null, "创建工程失败！！！", "错误", JOptionPane.ERROR_MESSAGE);
			HLog.el("addProjectEventHandler: create project fail");
			return;
		}
		
		// 在这里对log进行格式化
		InputLogFormat inputLogFormat = new InputLogFormat();
		inputLogFormat.formateInputLogWithProgress(inputProject.getProjectPath() + "/input");
		
		// add project to tree node
		addProjectNode(inputProject.getProjectName(), inputProject.getProjectPath() + "/input");
		
		// add project to configuration file
		projectConfig.addConfigItem(new ConfigItem("project", inputProject.getProjectPath(), inputProject.getProjectName()));
		//projectConfig.addConfigItem(new ConfigItem(projectName, projectPath));
		
		HLog.il("Finish log copy");
	}

	public static void addProjectLoadListener(ProjectLoadListener l) {
		for (int i = 0; i < projectLoadListeners.size(); i++) {
			if (projectLoadListeners.get(i) == l) {
				return;
			}
		}
		
		projectLoadListeners.add(l);
	}
	
	public static void removeProjectLoadListener(ProjectLoadListener l) {
		for (int i = 0; i < projectLoadListeners.size(); i++) {
			if (projectLoadListeners.get(i) == l) {
				projectLoadListeners.remove(i);
				i -= 1;
			}
		}
	}
	
	public void notifyProjectLoadedEvent(String projectPath) {
		eventFilePath = projectPath;
		for (int i = 0; i < projectLoadListeners.size(); i++) {
			projectLoadListeners.get(i).projectLoadEvent(projectPath, this);
		}
	}
	
	@Override
	public void loadProjectEvent() {
		loadProjectEventHandler();
	}

	@Override
	public void addProjectEvent(boolean isFolder) {
		addProjectEventHandler(isFolder);
	}

	@Override
	public void deleteProjectEvent() {
		deleteProject();
	}

	@Override
	public void openFileFolderEvent() {
		openProjectFileEventHandler();
	}

	@Override
	public void setEventSourceTime(String startTime, String endTime) {
		// 启动时间搜索进程
		new SelectTimeWork().start(startTime, endTime);
		notifyProjectLoadedEvent(eventFilePath);
	}
}
