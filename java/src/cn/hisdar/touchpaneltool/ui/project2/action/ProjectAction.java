package cn.hisdar.touchpaneltool.ui.project2.action;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import cn.hisdar.lib.adapter.FileAdapter;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.ProgramConfiguration;
import cn.hisdar.touchpaneltool.ui.project2.Project;
import cn.hisdar.touchpaneltool.ui.project2.controler.ProjectControlInterface;
import cn.hisdar.touchpaneltool.ui.project2.view.ProjectViewItem;
import cn.hisdar.touchpaneltool.ui.project2.view.ProjectViewItemNode;

public class ProjectAction implements ProjectActionListener {

	// ���ƹ�����ͼ�Ľӿڣ������ƹ��̵Ķ���������ʱ�򣬽�����Щ�ӿ��ж�ȡ������Ϣ
	private ProjectControlInterface projectControler;
	
	// ���տ����¼��Ľӿ�
	private ArrayList<ProjectEventListener> projectEventListeners;
	
	private static ProjectAction projectAction = null;
	
	private ProjectAction() {
		projectControler = null;
		projectEventListeners = new ArrayList<>();
	}
	
	public static ProjectAction getInstance() {
		if (projectAction == null) {
			synchronized (ProjectAction.class) {
				if (projectAction == null) {
					projectAction = new ProjectAction();
				}
			}
		}
		
		return projectAction;
	}

	public void setProjectControlInterface(ProjectControlInterface controler) {
		projectControler = controler;
	}
	
	public ProjectControlInterface getProjectControlInterface() {
		return projectControler;
	}
	
	public void addProjectEventListener(ProjectEventListener listener) {
		for (int i = 0; i < projectEventListeners.size(); i++) {
			if (projectEventListeners.get(i) == listener) {
				return;
			}
		}
		
		projectEventListeners.add(listener);
	}
	
	public void removeProjectEventListener(ProjectEventListener listener) {
		int listenerCount = projectEventListeners.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			if (projectEventListeners.get(i) == listener) {
				projectEventListeners.remove(i);
			}
		}
	}
	
	private ProjectViewItem getSelectedProject() {
		
		ProjectViewItem selectedProjectViewItem = projectControler.getSelectedProject();
		return selectedProjectViewItem;
	}
	
	private void deleteProjectViewItem(ProjectViewItem projectViewItem) {
		// 1����ȡ��ѡ�еĽڵ㣺
		ProjectViewItemNode[] selectedNodes = projectViewItem.getSelectedNodes();
		Project project = projectViewItem.getProject();
		
		// 2��ɾ�����̸��ڵ�
		if (selectedNodes == null) {
		
			// ֪ͨ�������ǣ�����Ҫ��ɾ����������ʲô�¾�������
			for (int i = 0; i < projectEventListeners.size(); i++) {
				projectEventListeners.get(i).deleteProjectEvent(project);
			}
			
			// ֪ͨ���̵���ʾ���ǣ����̽�Ҫ��ɾ����
			projectControler.deleteProject(projectViewItem);
			
			// ɾ��������̶�Ӧ���ļ�
			HLog.il("deleteProjectViewItem: delete:" + project.getProjectPath());
			if (!FileAdapter.deleteFolder(project.getProjectPath())) {
				HLog.el("deleteProjectViewItem: delete project file fail:" + project.getProjectPath());
			}
			
		// ɾ�����̵�ĳЩ�ڵ�
		} else {
			String[] filePaths = new String[selectedNodes.length];
			for (int i = 0; i < selectedNodes.length; i++) {
				filePaths[i] = selectedNodes[i].getSrcFilePath();
			}
			
			// ֪ͨ�������ǣ�����Ҫ��ɾ����������ʲô�¾�������
			for (int i = 0; i < projectEventListeners.size(); i++) {
				projectEventListeners.get(i).deleteProjectNodes(project, filePaths);
			}
			
			// ֪ͨ���̵���ʾ���ǣ����̽�Ҫ��ɾ����
			for (int j = 0; j < selectedNodes.length; j++) {
				projectControler.deleteProjectNode(projectViewItem, selectedNodes[j]);
			}

			// ɾ�����̶�Ӧ���ļ�
			for (int j2 = 0; j2 < filePaths.length; j2++) {
				if (!FileAdapter.deleteFolder(filePaths[j2])) {
					HLog.el("deleteProjectViewItem: delete project file fail:" + filePaths[j2]);
				}
			}
		}
	}

	private void createProject(boolean isFile) {
		HConfig programConfig = HConfig.getInstance(ProgramConfiguration.PROGRAM_CONFIG_FILE_PATH);
		if (programConfig == null) {
			JOptionPane.showMessageDialog(null, 
					"��ȡ�����ļ���������������������ϵc00248442 ���߷����ʼ���656913011@qq.com", 
					"����", JOptionPane.ERROR_MESSAGE);
			HLog.el("createProjectFromFile: load PROGRAM_CONFIG_FILE fail");
			HLog.el("createProjectFromFile: PROGRAM_CONFIG_FILE_PATH=" + ProgramConfiguration.PROGRAM_CONFIG_FILE_PATH);
			return;
		}
		
		String projectBasePath = programConfig.getConfigValue(ProgramConfiguration.PROJECT_PATH_CONFIG_NAME);
		if (projectBasePath == null) {
			JOptionPane.showMessageDialog(null, 
					"��ȡ����Ŀ¼��������������������ϵc00248442 ���߷����ʼ���656913011@qq.com", 
					"����", JOptionPane.ERROR_MESSAGE);
			HLog.el("createProjectFromFile: get PROJECT_PATH fail");
			HLog.el("createProjectFromFile: PROJECT_PATH_CONFIG_NAME=" + ProgramConfiguration.PROJECT_PATH_CONFIG_NAME);
			return;
		}
		
		Project project = Project.createProjectWithProgress(projectBasePath, isFile);
		projectControler.addProject(project);
		
		for (int i = 0; i < projectEventListeners.size(); i++) {
			projectEventListeners.get(i).createProjectEvent(project);
		}
	}
	
	@Override
	public void loadEvent(ProjectLoadType loadType) {
		
		ProjectViewItem selectedProjectViewItem = getSelectedProject();
		if (selectedProjectViewItem == null) {
			HLog.el("loadEvent: No project found");
		} else {
			Project selectedProject = selectedProjectViewItem.getProject();
			
			// ��ȡѡ�е��ļ�
			String[] selectedFilePaths = null;
			ProjectViewItemNode[] selectedNodes = selectedProjectViewItem.getSelectedNodes();
			if (selectedNodes == null) {
				ArrayList<File> selectedFiles = FileAdapter.getFileList(selectedProject.getProjectFilePath());
				selectedFilePaths = new String[selectedFiles.size()];
				for (int i = 0; i < selectedFiles.size(); i++) {
					selectedFilePaths[i] = selectedFiles.get(i).getPath();
				}
			} else {
				
				ArrayList<File> selectedFiles = new ArrayList<>();
				
				for (int i = 0; i < selectedNodes.length; i++) {
					File currentFile = new File(selectedNodes[i].getSrcFilePath());
					if (currentFile.isDirectory()) {
						selectedFiles.addAll(FileAdapter.getFileList(currentFile.getPath()));
					} else {
						selectedFiles.add(currentFile);
					}
				}
				
				selectedFilePaths = new String[selectedFiles.size()];
				for (int i = 0; i < selectedFiles.size(); i++) {
					selectedFilePaths[i] = selectedFiles.get(i).getPath();
				}
			}
			
			// ��ʼ���ع���
			ProjectLoader projectLoader = new ProjectLoader();
			projectLoader.loadProjectFile(selectedProject, selectedFilePaths);
			
			for (int i = 0; i < projectEventListeners.size(); i++) {
				projectEventListeners.get(i).loadProjectEvent(selectedProject);
			}
		}
	}

	@Override
	public void createEvent(ProjectCreateType createType) {
		switch (createType) {
		case CREATE_FROM_FILE:
			createProject(true);
			break;
		case CREATE_FROM_FOLDER:
			createProject(false);
			break;
		default:
			break;
		}
	}

	@Override
	public void deleteEvent() {
		ProjectViewItem selectedProjectViewItem = getSelectedProject();
		if (selectedProjectViewItem == null) {
			HLog.el("deleteEvent: No project or found");
			return;
		}
		
		deleteProjectViewItem(selectedProjectViewItem);
	}

	@Override
	public void openEvent(ProjectOpenType openType) {
		ProjectViewItem selectedProjectViewItem = getSelectedProject();
		if (selectedProjectViewItem == null) {
			HLog.el("openEvent: No project or found");
			JOptionPane.showMessageDialog(null, "����ѡ�񹤳̣�", "֪ͨ", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		ProjectViewItemNode[] selectedNodes = selectedProjectViewItem.getSelectedNodes();
		if (openType == ProjectOpenType.OPEN_FILE_BY_DEFAULT_PROGRAM) {
			for (int i = 0; i < selectedNodes.length; i++) {
				openFile(selectedNodes[i].getSrcFilePath());
			}
			
		} else if (openType == ProjectOpenType.OPEN_SRC_PATH) {
			if (selectedNodes == null) {
				// open project path
				String path = selectedProjectViewItem.getProject().getProjectFilePath();
				openFolder(path);
			} else {
				for (int i = 0; i < selectedNodes.length; i++) {
					String nodePath = selectedNodes[i].getSrcFilePath();
					if (new File(nodePath).isFile()) {
						openFilePath(nodePath);
					} else {
						openFolder(nodePath);
					}
				}
			}
		} else if (openType == ProjectOpenType.OPEN_FILE_IN_IDE) {
			// ֪ͨ�򿪹��̵��¼�������	
		}
	}
	
	private void openFile(String path) {
//		try {
//			Runtime.getRuntime().exec("cmd.exe /c start " + new File(path).getAbsolutePath());
//		} catch (IOException e) {
//			HLog.el(e);
//		}

		try {
			System.out.println(path);
			Desktop.getDesktop().open(new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void openFolder(String path) {
		try {
			Runtime.getRuntime().exec("explorer.exe /c, " + new File(path).getAbsolutePath());
		} catch (IOException e) {
			HLog.el(e);
		}
	}
	
	private void openFilePath(String path) {
		try {
			Runtime.getRuntime().exec("explorer.exe /select, " + new File(path).getAbsolutePath());
		} catch (IOException e) {
			HLog.el(e);
		}
	}
}
