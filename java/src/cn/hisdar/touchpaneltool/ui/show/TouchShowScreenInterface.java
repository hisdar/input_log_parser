package cn.hisdar.touchpaneltool.ui.show;

import java.awt.Image;
import java.util.Vector;

import cn.hisdar.touchpaneltool.ui.control.TouchFinger;


public interface TouchShowScreenInterface {
	public void repaintTouchLine(Vector<TouchFinger> touchFingers);
	public void setResolution(int width, int height);
	public void setToResume();
	public void setToSuspend();
}
