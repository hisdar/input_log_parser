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
				// 如果这个时候没有手指按下，这个事件将被作为按下事件上报
				if (fingerTouch == 0) {
					MultiTouchDowmEvent(pointList.get(i));
					fingerTouch = fingerTouch + 1;
				} else {
					// 如果这个时候手指是处于按下状态，这个事件将被作为一般的点上报
					MultiTouchActionEvent(pointList.get(i));
				}
			} else if (pointList.get(i).eventType == MultiTouchPoint.EVENT_TYPE_UP) {
				// 手指抬起后，按下的手指数减1
				fingerTouch = fingerTouch - 1;
				
				// 如果这个时候只有一个手指按下，这个点作为抬起事件直接上报
				if (fingerTouch <= 0) {
					MultiTouchUpEvent(pointList.get(i));
					HLog.il("Replay report all finger up");
				} else {
					// 这个点作为一般的时间，并上报一个点的抬起
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
		
		// 如果系统上报了所有手指抬起的事件，将所有的手指的状态设置成抬起状态
		for (int i = 0; i < touchFingers.size(); i++) {
			// 系统上报所有点抬起事件的时候，如果还有点没有抬起来的话，这是异常状态，打一个log
			if (touchFingers.get(i).touchState != TouchFinger.TOUCH_STATE_END) {
				System.err.println("Finger " + touchFingers.get(i).id + " have not up when every finger have up");
			}
			touchFingers.get(i).touchState = TouchFinger.TOUCH_STATE_END;
		}
		
		// 通知界面重新绘制
		if (paintTouchInterface != null) {
			paintTouchInterface.repaintTouchLine(touchFingers);
		}
	}

	private void MultiTouchActionEvent(MultiTouchPoint point) {
		// 当系统有点报上来的时候，先检查touchFingers中是否有这个点，
		TouchFinger currentTouchFinger = null;
		for (int i = 0; i < touchFingers.size(); i++) {
			if (touchFingers.get(i).id == point.id) {
				//System.out.println("Found finger:" + touchFingers.get(i).id + ">>>>" + arg0.id);
				currentTouchFinger = touchFingers.get(i);
			}
		}
		
		// System.out.println("Handle point:" + arg0);
		
		// 如果touchFingers中没有这个点，说明这个点是新增的点，在touchFingers中创建一个touchFinger来存储这个点
		if (currentTouchFinger == null) {
			
			//System.err.println("Add new point:" + arg0);
			
			currentTouchFinger = new TouchFinger();
			currentTouchFinger.id = point.id;
			// 将当前添加的点加入到创建的手指上去
			currentTouchFinger.pointList.add(point);
			
			// 将创建的手指加入到touchFinger中去
			touchFingers.add(currentTouchFinger);
		} else if (currentTouchFinger.touchState == TouchFinger.TOUCH_STATE_END) {
			// 如果touchFingers中有这个点，但是这个点已经抬起来了，说明这是新的触摸点，在touchFingers中创建新的touchFinger
			currentTouchFinger = new TouchFinger();
			currentTouchFinger.id = point.id;
			// 将当前添加的点加入到创建的手指上去
			currentTouchFinger.pointList.add(point);
			
			// 将创建的手指加入到touchFinger中去
			touchFingers.add(currentTouchFinger);
		} else if (currentTouchFinger.touchState == TouchFinger.TOUCH_STATE_TOUCH) {
			// 如果touchFingers中有这个点，且这个点没有抬起来，将这个点加入到touchFingers的这个手指中去
			currentTouchFinger.pointList.add(point);
			//System.err.println("Up handle point:" + arg0);
		} else {
			System.err.println("Un handle point:" + point);
		}
		
		// 通知界面重新绘制
		if (paintTouchInterface != null) {
			paintTouchInterface.repaintTouchLine(touchFingers);
		}
	}

	private void MultiTouchUpEvent(int id) {
		// 如果系统上报某一个点抬起来了，将touchFingers中的这个点的状态设置成抬起状态
		for (int i = 0; i < touchFingers.size(); i++) {
			if (touchFingers.get(i).id == id && touchFingers.get(i).touchState == TouchFinger.TOUCH_STATE_TOUCH) {
				touchFingers.get(i).touchState = TouchFinger.TOUCH_STATE_END;
			}
		}
		
		// 通知界面重新绘制
		if (paintTouchInterface != null) {
			paintTouchInterface.repaintTouchLine(touchFingers);
		}
	}

	@Override
	public void parseSpeedChangeEvent(double parseSpeed) {
		HLog.il("Parse speed is:" + parseSpeed);
	}
}
