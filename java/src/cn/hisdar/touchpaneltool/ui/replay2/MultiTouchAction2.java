package cn.hisdar.touchpaneltool.ui.replay2;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPointB;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.ui.replay2.TouchFinger.FingerStatus;

public class MultiTouchAction2 {

	private ArrayList<TouchFinger> fingers = null;
	private ArrayList<MultiTouchPoint> touchPoints = null;
	private BufferedImage touchActionImage = null;
	private StringBuffer originalData = null;
	
	public MultiTouchAction2() {
		fingers = new ArrayList<TouchFinger>();
		touchPoints = new ArrayList<MultiTouchPoint>();
		originalData = new StringBuffer();
	}
	
	/**
	 * @description ��Point ���䵽ָ������ָ
	 * @param point ���յ��������
	 */
	public void addPoint(MultiTouchPoint point) {
		// �Ȱѵ���뵽���б���
		touchPoints.add(point);
		
		// �������Ӧ�������ĸ���ָ
		TouchFinger currentFinger = null;
		for (int i = 0; i < fingers.size(); i++) {
			if (fingers.get(i).getFingerId() == point.id && fingers.get(i).getFingerStatus() == FingerStatus.DOWM) {
				currentFinger = fingers.get(i);
				break;
			}
		}
		
		if (currentFinger == null) {
			currentFinger = new TouchFinger();
			fingers.add(currentFinger);
		}
		
		currentFinger.addPoint(point);
	}
	
	public void appendOriginalData(String originalData) {
		this.originalData.append(originalData);
	}
	
	public void setMultiTouchAction2(MultiTouchAction2 touchAction) {
		if (touchAction != null) {
			this.fingers = touchAction.fingers;
			this.originalData = touchAction.originalData;
			this.touchPoints = touchAction.touchPoints;
		}
	}
	
	public BufferedImage getZoomedPreviceImage(BufferedImage image, Dimension resolution, ImageConfig config) {
		Graphics2D imageGraphics = (Graphics2D)image.getGraphics();
		
		// ��ΪͼƬҪ���ţ���������ҲҪ����ͼƬ�Ĵ�С�͵�ǰ�ķֱ���������
		double widthRate = 1.0 * image.getWidth() / resolution.getWidth();
		double heightRate = 1.0 * image.getHeight() / resolution.getHeight();
		
		config.lastPointSize = (int)(config.lastPointSize * widthRate);
		config.touchPointSize = (int)(config.touchPointSize * widthRate);
		
		config.lastPointSize = config.lastPointSize > 3 ? config.lastPointSize : 3;
		config.touchPointSize = config.touchPointSize > 3 ? config.touchPointSize : 3;
		
		// ���Ʊ���
		imageGraphics.setColor(config.backgroudColor);
		imageGraphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		for (int i = 0; i < fingers.size(); i++) {
			TouchFinger currentTouchFinger = fingers.get(i);
			MultiTouchPoint currentPoint = new MultiTouchPointB();
			MultiTouchPoint lastPoint = new MultiTouchPointB();
			
			for (int j = 0; j < currentTouchFinger.points.size(); j++) {
			
				// ���ù켣�ߵ���ɫ
				imageGraphics.setColor(config.trackLineColor);
				currentPoint.copy(currentTouchFinger.points.get(j));
				
				// ���������ֵ
				currentPoint.positionX = (int)(currentPoint.positionX * widthRate);
				currentPoint.positionY = (int)(currentPoint.positionY * heightRate);
				currentPoint.positionX = currentPoint.positionX > 1 ? currentPoint.positionX : 1;
				currentPoint.positionY  = currentPoint.positionY > 1 ? currentPoint.positionY : 1;
				
				// ��������һ���㣬��ô���Ƴɴ�ĵ�
				if (j == currentTouchFinger.points.size() - 1) {
					imageGraphics.setColor(config.lastPointColor); 
					imageGraphics.fillOval(currentPoint.positionX - config.lastPointSize / 2, 
											currentPoint.positionY - config.lastPointSize / 2, 
											config.lastPointSize, 
											config.lastPointSize);
				}
				
				// ������ǵ�һ���㣬����������һ��������������
				if (j != 0) {
					// ��ȡ��ǰ�����һ����
					lastPoint.copy(currentTouchFinger.points.get(j - 1));
					lastPoint.positionX = (int)(lastPoint.positionX * widthRate);
					lastPoint.positionY = (int)(lastPoint.positionY * heightRate);
					// ���ƹ켣��
					imageGraphics.drawLine(lastPoint.positionX, 
											lastPoint.positionY, 
											currentPoint.positionX, 
											currentPoint.positionY);
				}
				
				// ������ͨ�ĵ�
				imageGraphics.setColor(config.touchPointColor); 
				imageGraphics.fillOval(currentPoint.positionX - config.touchPointSize / 2, 
										currentPoint.positionY - config.touchPointSize / 2, 
										config.touchPointSize, 
										config.touchPointSize);
			}
		}
		
		return image;
	}
	
	public BufferedImage getPreviewImage(int width, int height, ImageConfig config) {
		
		if (touchActionImage == null || touchActionImage.getWidth() != width || touchActionImage.getHeight() != height) {

			if (touchActionImage != null) {
				HLog.il("touchActionImage. getWidth()=" + touchActionImage.getWidth());
				HLog.il("touchActionImage.getHeight()=" + touchActionImage.getHeight());
			}

			touchActionImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			
		}
		
		Graphics2D imageGraphics = (Graphics2D)touchActionImage.getGraphics();
		
		// ���ƺ�ɫ����
		imageGraphics.setColor(config.backgroudColor);
		imageGraphics.fillRect(0, 0, touchActionImage.getWidth(), touchActionImage.getHeight());

		for (int i = 0; i < fingers.size(); i++) {
			TouchFinger currentTouchFinger = fingers.get(i);
			MultiTouchPoint currentPoint = null;
			//System.err.println("Points:" + currentTouchFinger.pointList.size());
			for (int j = 0; j < currentTouchFinger.points.size(); j++) {
				// ���ù켣�ߵ���ɫ
				imageGraphics.setColor(config.trackLineColor);
				currentPoint = currentTouchFinger.points.get(j);
				//currentPoint = exchangeToLocalPoint(currentPoint);
				if (j == 0) {
					//System.out.printf("[%d, %d] --> [%d, %d]\n", currentPoint.x, currentPoint.y, currentPoint.x, currentPoint.y);
					// imageGraphics.drawLine(currentPoint.positionX, currentPoint.positionY, currentPoint.positionX, currentPoint.positionY);
					// ���ƴ�����
					imageGraphics.setColor(config.touchPointColor); 
					imageGraphics.fillOval(currentPoint.positionX - config.touchPointSize / 2, 
											currentPoint.positionY - config.touchPointSize / 2, 
											config.touchPointSize, config.touchPointSize);
				} else {
					MultiTouchPoint lastPoint = currentTouchFinger.points.get(j - 1);
					//lastPoint = exchangeToLocalPoint(lastPoint);
					//System.out.printf("[%d, %d] --> [%d, %d]\n", lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
					// ���ƹ켣��
					imageGraphics.drawLine(lastPoint.positionX, lastPoint.positionY, currentPoint.positionX, currentPoint.positionY);
					
					// ���ƴ�����
					imageGraphics.setColor(config.touchPointColor); 
					imageGraphics.fillOval(currentPoint.positionX - config.touchPointSize / 2, 
											currentPoint.positionY - config.touchPointSize / 2, 
											config.touchPointSize, config.touchPointSize);
				}
			}
			
			// �����һ����ԲȦ
			if (currentPoint != null) {
				imageGraphics.setColor(config.lastPointColor); 
				imageGraphics.fillOval(currentPoint.positionX - config.lastPointSize / 2, 
										currentPoint.positionY - config.lastPointSize / 2, 
										config.lastPointSize, config.lastPointSize);
			}
		}

		return touchActionImage;
	}

	public ArrayList<TouchFinger> getFingers() {
		return fingers;
	}

	public StringBuffer getOriginalData() {
		return originalData;
	}

	public ArrayList<MultiTouchPoint> getTouchPoints() {
		return touchPoints;
	}	
}
