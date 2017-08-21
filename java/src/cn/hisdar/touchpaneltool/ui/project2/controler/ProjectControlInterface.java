package cn.hisdar.touchpaneltool.ui.project2.controler;

import cn.hisdar.touchpaneltool.ui.project2.Project;
import cn.hisdar.touchpaneltool.ui.project2.view.ProjectViewItem;
import cn.hisdar.touchpaneltool.ui.project2.view.ProjectViewItemNode;

public interface ProjectControlInterface {

	public boolean addProject(Project project);
	public boolean updateProject(Project project);
	public boolean deleteProject(ProjectViewItem project);
	public boolean deleteProjectNode(ProjectViewItem project, ProjectViewItemNode node);
	
	public ProjectViewItem getSelectedProject();
	public ProjectViewItemNode[] getSelectedNodes();
}
