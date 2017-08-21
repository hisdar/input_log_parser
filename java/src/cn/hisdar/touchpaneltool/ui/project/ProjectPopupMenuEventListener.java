package cn.hisdar.touchpaneltool.ui.project;

public interface ProjectPopupMenuEventListener {

	public void loadProjectEvent();
	public void addProjectEvent(boolean isFolder);
	public void deleteProjectEvent();
	public void openFileFolderEvent();
}
