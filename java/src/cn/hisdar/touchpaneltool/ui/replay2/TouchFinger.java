package cn.hisdar.touchpaneltool.ui.replay2;

import java.util.ArrayList;

import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;

public class TouchFinger {

	public enum FingerStatus {
		DOWM,
		UP,
	}
	
	public ArrayList<MultiTouchPoint> points = null;
	
	public TouchFinger() {
		points = new ArrayList<MultiTouchPoint>();
	}
	
	public void addPoint(MultiTouchPoint point) {
		points.add(point);
	}
	
	/**
	 * @description 返回当前手指的状态
	 * @return 返回当前手指的状态
	 */
	public FingerStatus getFingerStatus() {
		if (points.get(points.size() - 1).eventType == MultiTouchPoint.EVENT_TYPE_UP) {
			return FingerStatus.UP;
		} else {
			return FingerStatus.DOWM;
		}
	}
	
	/**
	 * @description 返回当前手指的ID
	 * @return 手指的ID
	 */
	public int getFingerId() {
		if (points.size() > 0) {
			return points.get(0).id;
		} else {
			return -1;
		}
	}
}
