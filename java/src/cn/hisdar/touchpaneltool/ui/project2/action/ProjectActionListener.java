package cn.hisdar.touchpaneltool.ui.project2.action;

public interface ProjectActionListener {

	public void loadEvent(ProjectLoadType loadType);
	public void createEvent(ProjectCreateType createType);
	public void deleteEvent();
	public void openEvent(ProjectOpenType openType);
}
