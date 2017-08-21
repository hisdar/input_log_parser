/**
 * ����࣬��������¼һ��������ָ����Ϣ��
 * @pointList ��ʾ�����ָ���������ĵ�
 * @touchState ��ʾ�����ָ�ǲ���̧������
 * @slot ��ʾ�����ָ�ڴ���ʱ����Ǹ����
 * @trackingID ��ָ�ڴ���ʱ���ǵ�Tracking ID
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
