package cn.hisdar.touchpaneltool.ui.replay2;

import java.util.ArrayList;
import java.util.Vector;

import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.ui.control.ParseSpeedChangeListener;
import cn.hisdar.touchpaneltool.ui.control.ParseSpeedControlPanel;
import cn.hisdar.touchpaneltool.ui.control.TouchFinger;
import cn.hisdar.touchpaneltool.ui.show.TouchShowScreenInterface;

public class ReplayThread extends Thread implements ParseSpeedChangeListener {

	private TouchShowScreenInterface paintTouchInterface = null;
	private MultiTouchAction2 touchAction = null;
	private Vector<TouchFinger> touchFingers = null;
	private boolean runFlag = true;
	private boolean isRunning = true;
	private double  parseSpeed = 1.0;
	
	private static ReplayThread replayThread = null;
	
	private ReplayThread(TouchShowScreenInterface paintTouchInterface, MultiTouchAction2 touchAction) {
		this.paintTouchInterface = paintTouchInterface;
		this.touchAction = touchAction;
		touchFingers = new Vector<TouchFinger>();
		
		ParseSpeedControlPanel.addParseSpeedChangeListener(this);
	}
	
	public static void replay(TouchShowScreenInterface paintTouchInterface, MultiTouchAction2 touchAction) {
		if (replayThread != null) {
			ParseSpeedControlPanel.removeParseSpeedChangeListener(replayThread);
			if (replayThread.isRunning) {
				replayThread.stopThread();
			}
		}
		
		replayThread = new ReplayThread(paintTouchInterface, touchAction);
		replayThread.start();
	}
	
	public boolean isThreadRunning() {
		return isRunning;
	}
	
	public void stopThread() {
		interrupt();
		runFlag = false;
	}
	
	public void run() {
		if (touchAction == null) {
			return;
		}
		
		isRunning = true;
		
		int fingerTouch = 0;
		double lastTime = -1;
		
		ArrayList<MultiTouchPoint> pointList =  touchAction.getTouchPoints();
		for (int i = 0; (i < pointList.size()) && runFlag; i++) {
			
			if (pointList.get(i).eventType == MultiTouchPoint.EVENT_TYPE_POINT) {
				MultiTouchActionEvent(pointList.get(i));
			} else if (pointList.get(i).eventType == MultiTouchPoint.EVENT_TYPE_DOWN) {
				// ������ʱ��û����ָ���£�����¼�������Ϊ�����¼��ϱ�
				if (fingerTouch == 0) {
					MultiTouchDowmEvent(pointList.get(i));
					fingerTouch = fingerTouch + 1;
				} else {
					// ������ʱ����ָ�Ǵ��ڰ���״̬������¼�������Ϊһ��ĵ��ϱ�
					MultiTouchActionEvent(pointList.get(i));
				}
			} else if (pointList.get(i).eventType == MultiTouchPoint.EVENT_TYPE_UP) {
				// ��ָ̧��󣬰��µ���ָ����1
				fingerTouch = fingerTouch - 1;
				
				// ������ʱ��ֻ��һ����ָ���£��������Ϊ̧���¼�ֱ���ϱ�
				if (fingerTouch <= 0) {
					MultiTouchUpEvent(pointList.get(i));
					HLog.il("Replay report all finger up");
				} else {
					// �������Ϊһ���ʱ�䣬���ϱ�һ�����̧��
					MultiTouchActionEvent(pointList.get(i));
					MultiTouchUpEvent(pointList.get(i).id);
					HLog.il("Replay report finger up, touchFinger=" + fingerTouch);
				}
				
			}
			
			if ((lastTime != -1) && runFlag) {
				long sleepTime = (long) ((pointList.get(i).bootUpTime - lastTime) *  1000);
				sleepTime *= parseSpeed;
				try {
					sleep(sleepTime);
				} catch (InterruptedException e) {}
			}
			
			lastTime = pointList.get(i).bootUpTime;
		}
		
		isRunning = false;
	}
	
	private void MultiTouchDowmEvent(MultiTouchPoint point) {
		
		touchFingers.removeAllElements();
		MultiTouchActionEvent(point);
	}

	private void MultiTouchUpEvent(MultiTouchPoint point) {
		
		// ���ϵͳ�ϱ���������ָ̧����¼��������е���ָ��״̬���ó�̧��״̬
		for (int i = 0; i < touchFingers.size(); i++) {
			// ϵͳ�ϱ����е�̧���¼���ʱ��������е�û��̧�����Ļ��������쳣״̬����һ��log
			if (touchFingers.get(i).touchState != TouchFinger.TOUCH_STATE_END) {
				System.err.println("Finger " + touchFingers.get(i).id + " have not up when every finger have up");
			}
			touchFingers.get(i).touchState = TouchFinger.TOUCH_STATE_END;
		}
		
		// ֪ͨ�������»���
		if (paintTouchInterface != null) {
			paintTouchInterface.repaintTouchLine(touchFingers);
		}
	}

	private void MultiTouchActionEvent(MultiTouchPoint point) {
		// ��ϵͳ�е㱨������ʱ���ȼ��touchFingers���Ƿ�������㣬
		TouchFinger currentTouchFinger = null;
		for (int i = 0; i < touchFingers.size(); i++) {
			if (touchFingers.get(i).id == point.id) {
				//System.out.println("Found finger:" + touchFingers.get(i).id + ">>>>" + arg0.id);
				currentTouchFinger = touchFingers.get(i);
			}
		}
		
		// System.out.println("Handle point:" + arg0);
		
		// ���touchFingers��û������㣬˵��������������ĵ㣬��touchFingers�д���һ��touchFinger���洢�����
		if (currentTouchFinger == null) {
			
			//System.err.println("Add new point:" + arg0);
			
			currentTouchFinger = new TouchFinger();
			currentTouchFinger.id = point.id;
			// ����ǰ��ӵĵ���뵽��������ָ��ȥ
			currentTouchFinger.pointList.add(point);
			
			// ����������ָ���뵽touchFinger��ȥ
			touchFingers.add(currentTouchFinger);
		} else if (currentTouchFinger.touchState == TouchFinger.TOUCH_STATE_END) {
			// ���touchFingers��������㣬����������Ѿ�̧�����ˣ�˵�������µĴ����㣬��touchFingers�д����µ�touchFinger
			currentTouchFinger = new TouchFinger();
			currentTouchFinger.id = point.id;
			// ����ǰ��ӵĵ���뵽��������ָ��ȥ
			currentTouchFinger.pointList.add(point);
			
			// ����������ָ���뵽touchFinger��ȥ
			touchFingers.add(currentTouchFinger);
		} else if (currentTouchFinger.touchState == TouchFinger.TOUCH_STATE_TOUCH) {
			// ���touchFingers��������㣬�������û��̧���������������뵽touchFingers�������ָ��ȥ
			currentTouchFinger.pointList.add(point);
			//System.err.println("Up handle point:" + arg0);
		} else {
			System.err.println("Un handle point:" + point);
		}
		
		// ֪ͨ�������»���
		if (paintTouchInterface != null) {
			paintTouchInterface.repaintTouchLine(touchFingers);
		}
	}

	private void MultiTouchUpEvent(int id) {
		// ���ϵͳ�ϱ�ĳһ����̧�����ˣ���touchFingers�е�������״̬���ó�̧��״̬
		for (int i = 0; i < touchFingers.size(); i++) {
			if (touchFingers.get(i).id == id && touchFingers.get(i).touchState == TouchFinger.TOUCH_STATE_TOUCH) {
				touchFingers.get(i).touchState = TouchFinger.TOUCH_STATE_END;
			}
		}
		
		// ֪ͨ�������»���
		if (paintTouchInterface != null) {
			paintTouchInterface.repaintTouchLine(touchFingers);
		}
	}

	@Override
	public void parseSpeedChangeEvent(double parseSpeed) {
		HLog.il("Parse speed is:" + parseSpeed);
	}
}
