package cn.hisdar.touchpaneltool.ui.project2.action;

import cn.hisdar.touchpaneltool.ui.project2.Project;

public interface ProjectEventListener {

	public void deleteProjectEvent(Project project);
	public void deleteProjectNodes(Project project, String[] filePaths);
	public void loadProjectEvent(Project projects);
	public void createProjectEvent(Project project);
}
