package cn.hisdar.touchpaneltool.ui.project2.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.ui.HLinearPanel;
import cn.hisdar.touchpaneltool.ui.project2.Project;
import cn.hisdar.touchpaneltool.ui.project2.controler.ProjectControlInterface;

public class ProjectView extends JPanel implements ProjectControlInterface {

	private static final long serialVersionUID = -1088262881955930364L;
	private final static Color DEFAULT_DIVIDER_COLOR = new Color(0x293955);
	
	private HLinearPanel projectListPanel;
	private MouseEventListener mouseEventListener;
	private ArrayList<ProjectViewItem> projectViewItems;
	
	private ArrayList<ProjectViewActionListener> actionListeners;
	
	public ProjectView() {
		super();
		
		actionListeners = new ArrayList<>();
		projectViewItems = new ArrayList<>();
		mouseEventListener = new MouseEventListener();
		
		projectListPanel = new HLinearPanel();
		projectListPanel.addMouseListener(mouseEventListener);
		
		JPanel scrollMainPane = new JPanel(new BorderLayout());
		scrollMainPane.add(projectListPanel);
		scrollMainPane.setOpaque(true);
		scrollMainPane.setBackground(DEFAULT_DIVIDER_COLOR);

		JScrollPane scrollPane = new JScrollPane(scrollMainPane);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );// 禁止显示水平滚动条
		
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}
	
	private class MouseEventListener extends MouseAdapter {

		public MouseEventListener() {
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			
			ProjectViewItem selectedProject = getSelectedProject();
 			
			if (e.getButton() == MouseEvent.BUTTON3) {
				for (int i = 0; i < actionListeners.size(); i++) {
					actionListeners.get(i).showOptionsEvent(selectedProject, e.getComponent(), e.getX(), e.getY());
				}
			} else if (e.getButton() == MouseEvent.BUTTON1) {
				// 如果事件发生在面板上，清空所有的选中项
				for (int i = 0; i < projectViewItems.size(); i++) {
					if (projectViewItems.get(i) != e.getComponent()) {
						projectViewItems.get(i).clearSelection();
					}
				}
			}
			
			super.mousePressed(e);
		}
	}
	
	@Override
	public boolean addProject(Project project) {
		
		if (project == null) {
			return false;
		}
		
		ProjectViewItem currentTypeProject = new ProjectViewItem(project);
		currentTypeProject.loadProjectFromFileSystem(project.getProjectFilePath());
		currentTypeProject.addMouseListener(mouseEventListener);
		
		projectListPanel.add(currentTypeProject);
		projectViewItems.add(currentTypeProject);
		HLog.dl("addProject: projectViewItems length = " + projectViewItems.size());
		revalidate();
		repaint();
		return true;
	}

	@Override
	public boolean updateProject(Project project) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteProject(ProjectViewItem project) {
		HLog.dl("deleteProject: project path = " + project.getProject().getProjectPath());
		HLog.dl("deleteProject: projectViewItems length = " + projectViewItems.size());
		int viewItemCount = projectViewItems.size();
		for (int j = viewItemCount - 1; j >= 0 ; j--) {
			HLog.dl("deleteProject: projectViewItems length = " + projectViewItems.size());
			if (projectViewItems.get(j) == project) {
				HLog.dl("deleteProject: projectViewItems length = " + projectViewItems.size());
				projectListPanel.removeChild(projectViewItems.get(j));// 从面板中删除
				projectViewItems.remove(j);// 从存储列表中删除
			}
		}
		
		revalidate();
		repaint();
		
		return true;
	}
	
	@Override
	public boolean deleteProjectNode(ProjectViewItem project, ProjectViewItemNode node) {
		if (project == null || node == null) {
			return false;
		}
		
		project.removeNode(node);
		revalidate();
		repaint();
		return true;
	}

	@Override
	public ProjectViewItem getSelectedProject() {
		ProjectViewItem selectedProjectViewItem = null;
		for (int i = 0; i < projectViewItems.size(); i++) {
			if (projectViewItems.get(i).isSelected()) {
				selectedProjectViewItem = projectViewItems.get(i);
				break;
			}
		}
		
		return selectedProjectViewItem;
	}

	@Override
	public ProjectViewItemNode[] getSelectedNodes() {
		ProjectViewItem selectedProjectViewItem = getSelectedProject();
		return selectedProjectViewItem.getSelectedNodes();
	}
	
	public void addProjectViewActionListener(ProjectViewActionListener listener) {
		for (int i = 0; i < actionListeners.size(); i++) {
			if (actionListeners.get(i) == listener) {
				return ;
			}
		}
		
		actionListeners.add(listener);
	}
	
	public void removeProjectViewActionListener(ProjectViewActionListener listener) {
		int listenerCount = actionListeners.size();
		for (int i = listenerCount - 1; i >= 0; i++) {
			if (actionListeners.get(i) == listener) {
				actionListeners.remove(i);
			}
		}
	}

}
