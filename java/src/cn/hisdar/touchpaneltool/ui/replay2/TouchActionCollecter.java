 package cn.hisdar.touchpaneltool.ui.replay2;

import java.util.Vector;

import cn.hisdar.MultiTouchEventParse.EventParser;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchEventListener;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;

/**
 * @description 触摸事件收集容器，容器有一定的容量，容量可以设置，默认大小是#DEFAULT_COLLECTER_SIZE
 * 				容器的数据结构是一个循环队列，当容器满的了的时候，总是覆盖最老的那个数据
 * @author Hisdar
 * @event       容器提供事件通知功能，当容器中的数据改变的时候，会触发 CollecterDataChangeEvent 事件
 */
public class TouchActionCollecter implements MultiTouchEventListener {

	// 触摸事件改变的监听者们
	private Vector<CollecterDataChangeListener> collecterDataChangeListeners = null;
	
	// 存放触摸事件的容器
	private TouchActionContainer touchActionContainer = null;
	
	// 当前的触摸行为
	private MultiTouchAction2 currentTouchAction = null;
	
	public TouchActionCollecter() {
		collecterDataChangeListeners = new Vector<CollecterDataChangeListener>();
		touchActionContainer = new TouchActionContainer();
		EventParser.getInstance().addMultiTouchEventListener(this);
	}
	
	public void addCollecterDataChangeListener(CollecterDataChangeListener listener) {
		for (int i = 0; i < collecterDataChangeListeners.size(); i++) {
			if (collecterDataChangeListeners.get(i) == listener) {
				return;
			}
		}
		
		collecterDataChangeListeners.add(listener);
	}
	
	public void removeCollecterDataChangeListener(CollecterDataChangeListener listener) {
		for (int i = 0; i < collecterDataChangeListeners.size(); i++) {
			if (collecterDataChangeListeners.get(i) == listener) {
				collecterDataChangeListeners.remove(i);
				return;
			}
		}
	}

	/******************************************************************************************
	 * 下面的代码用来收集触摸行为
	 ******************************************************************************************/
	@Override
	public void MultiTouchDowmEvent(MultiTouchPoint point) {
		if (currentTouchAction == null) {
			currentTouchAction = new MultiTouchAction2();
		}
		
		currentTouchAction.addPoint(point);
	}

	@Override
	public void MultiTouchUpEvent(MultiTouchPoint point) {
		// 这个时候所有的点都抬起来了，将当前的触摸行为添加到触摸行为列表中
		currentTouchAction.addPoint(point);
		touchActionContainer.queue(currentTouchAction);
		currentTouchAction = null;
		
		// 通知事件监听者，触摸行为的容器数据有变化
		for (int i = 0; i < collecterDataChangeListeners.size(); i++) {
			collecterDataChangeListeners.get(i).collecterDataChangeEvent(touchActionContainer);
		}
	}

	@Override
	public void MultiTouchActionEvent(MultiTouchPoint point) {
		if (currentTouchAction == null) {
			currentTouchAction = new MultiTouchAction2();
		}
		
		currentTouchAction.addPoint(point);
	}

	@Override
	public void MultiTouchUpEvent(int id) {
		
	}

	@Override
	public void receiveSourceData(String sourceData) {
		if (currentTouchAction == null) {
			currentTouchAction = new MultiTouchAction2();
		}
		
		currentTouchAction.appendOriginalData(sourceData);
	}

	public TouchActionContainer getTouchActionContainer() {
		return touchActionContainer;
	}

	public void setTouchActionContainer(TouchActionContainer touchActionContainer) {
		this.touchActionContainer = touchActionContainer;
	}
	
}
