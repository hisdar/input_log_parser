/**
 * 这个类，是用来记录一个触摸手指的信息，
 * @pointList 表示这个手指触摸产生的点
 * @touchState 表示这个手指是不是抬起来了
 * @slot 表示这个手指在触摸时候的那个编号
 * @trackingID 手指在触摸时候标记的Tracking ID
 */
package cn.hisdar.touchpaneltool.ui.control;

import java.util.Vector;

import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;

public class TouchFinger {

	public static final int TOUCH_STATE_TOUCH = 1;
	public static final int TOUCH_STATE_END = 2;
	
	public int id = -1;
	public String trackingID = "";
	public Vector<MultiTouchPoint> pointList = new Vector<MultiTouchPoint>();
	public int touchState = TOUCH_STATE_TOUCH;
}
