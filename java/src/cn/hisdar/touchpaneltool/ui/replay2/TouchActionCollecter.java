 package cn.hisdar.touchpaneltool.ui.replay2;

import java.util.Vector;

import cn.hisdar.MultiTouchEventParse.EventParser;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchEventListener;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;

/**
 * @description �����¼��ռ�������������һ���������������������ã�Ĭ�ϴ�С��#DEFAULT_COLLECTER_SIZE
 * 				���������ݽṹ��һ��ѭ�����У������������˵�ʱ�����Ǹ������ϵ��Ǹ�����
 * @author Hisdar
 * @event       �����ṩ�¼�֪ͨ���ܣ��������е����ݸı��ʱ�򣬻ᴥ�� CollecterDataChangeEvent �¼�
 */
public class TouchActionCollecter implements MultiTouchEventListener {

	// �����¼��ı�ļ�������
	private Vector<CollecterDataChangeListener> collecterDataChangeListeners = null;
	
	// ��Ŵ����¼�������
	private TouchActionContainer touchActionContainer = null;
	
	// ��ǰ�Ĵ�����Ϊ
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
	 * ����Ĵ��������ռ�������Ϊ
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
		// ���ʱ�����еĵ㶼̧�����ˣ�����ǰ�Ĵ�����Ϊ��ӵ�������Ϊ�б���
		currentTouchAction.addPoint(point);
		touchActionContainer.queue(currentTouchAction);
		currentTouchAction = null;
		
		// ֪ͨ�¼������ߣ�������Ϊ�����������б仯
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
