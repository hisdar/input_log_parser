package cn.hisdar.touchpaneltool.interfaces;


public interface TouchShowControlInterface {

	public void suspendParse();
	
	public void resumeParse();
	
	public void stopParse();
	
	public boolean isSuspend();
	
	public void startParse();

	public void setTouchEventOutInterface(LogOutInterface touchEventOutInterface);
	
	public boolean isRun();
	
	public void nextStep();

}
