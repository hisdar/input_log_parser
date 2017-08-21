package cn.hisdar.touchpaneltool.ui.show;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPointA;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.androidDevice.PhoneEvent;
import cn.hisdar.touchpaneltool.setting.DrawSettingPanel;
import cn.hisdar.touchpaneltool.ui.control.GriddingLineStateChangeListener;
import cn.hisdar.touchpaneltool.ui.control.GriddlingLineSetPanel;
import cn.hisdar.touchpaneltool.ui.control.ResolutionChangeListener;
import cn.hisdar.touchpaneltool.ui.control.ResolutionSetPanel;
import cn.hisdar.touchpaneltool.ui.control.TouchFinger;
import cn.hisdar.touchpaneltool.ui.control.TouchShowBackGround;
import cn.hisdar.touchpaneltool.ui.control.TouchShowBackGround.TouchShowBackgroundType;
import cn.hisdar.touchpaneltool.ui.control.TouchShowBackgroundChangeListener;
import cn.hisdar.touchpaneltool.ui.control.TouchShowControlPanel;
import cn.hisdar.touchpaneltool.ui.control.TouchShowControlSaveImageAction;

public class TouchShowScreenPanel extends JPanel 
	implements TouchShowScreenInterface, MouseInputListener, GriddingLineStateChangeListener,
				TouchShowBackgroundChangeListener, TouchShowControlSaveImageAction,
				ResolutionChangeListener {

	private static final String SLEEPING_TEXT = "Sleeping"; 
	
	private static int mouseLocationX = 0;
	private static int mouseLocationY = 0;
	
	// ʹ��˫��������ֹ��Ļ��˸
	private Image iBuffer = null;  
	private Graphics gBuffer = null;
	private Dimension windowDimension = null;
	private Dimension resolutionDimension = null;
	private BufferedImage backgroundImage = null;
	
	private Vector<TouchFinger> touchFingers = null;
	
	private HConfig settingConfig = null;
	
	private TouchShowBackGround touchShowBackGround = null;
	
	private int griddingRowCount = 23;
	private int griddingColumnCount = 13;
	
	private boolean isShowGriddingLine = true;
	
	private static ArrayList<MouseLocationChangeListener> mouseLocationChangeListeners = new ArrayList<MouseLocationChangeListener>();
	
	private boolean isSuspend = false;
	
	/** ****************************************************************************
	 * ������صı���
	 ** ****************************************************************************/
	private Point mousePressePoint = null;
	private Point mouseReleasePoint = null;
	private Vector<Point> mouseMoveVector = null;
	private boolean mousePreesed = false;
	
	public TouchShowScreenPanel() {
		settingConfig = HConfig.getInstance(DrawSettingPanel.DRAW_CONFIG_FILE_PATH);
		resolutionDimension = new Dimension(DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT);
		initControl();
		
		touchShowBackGround = TouchShowBackGround.getInstance();
		touchShowBackGround.addTouchShowBackgroundChangeListener(this);
		
		ResolutionSetPanel.addResolutionChangeListener(this);
		GriddlingLineSetPanel.addGriddingLineStateChangeListener(this);
		TouchShowControlPanel.addTouchShowControlSaveImageAction(this);
	}
	
	private void drawTouch() {
		if (touchFingers == null) {
			// System.out.println("point list is null");
			return;
		}
			
		// ��¼���ǵ�ǰҪ���Ƶ��Ǹ���������ָ
		TouchFinger currentTouchFinger = null;
		
		// ��¼���ǵ�ǰҪ���Ƶ��Ǹ������Ϣ
		MultiTouchPoint currentPoint = null;
		int touchPointSize = settingConfig.getConfigValue(
				DrawSettingPanel.BASE_TOUCH_POINT_SIZE_CONFIG_NAME, 
				DrawSettingPanel.DEFAULT_BASE_TOUCH_POINT_SIZE);
		int lastPointSize  = settingConfig.getConfigValue(
				DrawSettingPanel.BASE_LAST_POINT_SIZE_CONFIG_NAME, 
				DrawSettingPanel.DEFAULT_BASE_LAST_POINT_SIZE);
		for (int i = 0; i < touchFingers.size(); i++) {
			currentTouchFinger = touchFingers.get(i);
			for (int j = 0; j < currentTouchFinger.pointList.size(); j++) {
				gBuffer.setColor(new Color(
						settingConfig.getConfigValue(
								DrawSettingPanel.BASE_TRACK_LINE_COLOR_CONFIG_NAME, 
								DrawSettingPanel.DEFAULT_TRACK_LING_COLOR.getRGB())));
				currentPoint = currentTouchFinger.pointList.get(j);
				currentPoint = exchangeToLocalPoint(currentPoint);
				
				// ���켣��
				Color baseTouchPointColor = new Color(
						settingConfig.getConfigValue(
								DrawSettingPanel.BASE_TOUCH_POINT_COLOR_CONFIG_NAME, 
								DrawSettingPanel.DEFAULT_TOUCH_POINT_COLOR.getRGB()));
				if (j == 0) {
					//System.out.printf("[%d, %d] --> [%d, %d]\n", currentPoint.x, currentPoint.y, currentPoint.x, currentPoint.y);
					//gBuffer.drawLine(currentPoint.positionX, currentPoint.positionY, currentPoint.positionX, currentPoint.positionY);
					// ������λ�õĵ�
					
					gBuffer.setColor(baseTouchPointColor); 
					gBuffer.fillOval(currentPoint.positionX - touchPointSize / 2, 
									currentPoint.positionY - touchPointSize / 2, 
									touchPointSize, touchPointSize);
				} else {
					MultiTouchPoint lastPoint = currentTouchFinger.pointList.get(j - 1);
					lastPoint = exchangeToLocalPoint(lastPoint);
					//System.out.printf("[%d, %d] --> [%d, %d]\n", lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
					gBuffer.drawLine(lastPoint.positionX, lastPoint.positionY, currentPoint.positionX, currentPoint.positionY);
					
					// ������λ�õĵ�
					gBuffer.setColor(baseTouchPointColor); 
					gBuffer.fillOval(lastPoint.positionX - touchPointSize / 2, 
									lastPoint.positionY - touchPointSize / 2, 
									touchPointSize, touchPointSize);
				}
			}
			
			// ��һ��ʮ���ߣ���ʮ���ߵ�ʱ�򣬿�һ�������ָ�ǲ���̧�����ˣ���������ָ̧�����ˣ���������켣����û��ʮ���ߵ�
			if (settingConfig.getConfigValue(DrawSettingPanel.IS_DRAWC_CROSS_LINE_CONFIG_NAME, true)) {
				if (currentPoint != null && currentTouchFinger.touchState != TouchFinger.TOUCH_STATE_END) {
					Color baseCrossLineColor = new Color(
							settingConfig.getConfigValue(
									DrawSettingPanel.BASE_CROSS_LINE_COLOR_CONFIG_NAME, 
									DrawSettingPanel.DEFAULT_CROSS_LINE_COLOR.getRGB()));
					gBuffer.setColor(baseCrossLineColor); 
					gBuffer.drawLine(0, currentPoint.positionY, getWidth(), currentPoint.positionY);
					gBuffer.drawLine(currentPoint.positionX, 0, currentPoint.positionX, getHeight());
				
					// ���������
					if (currentPoint.touchMajor > 0 && currentPoint.touchMinor > 0) {
						int majorIndex = currentPoint.positionX - currentPoint.touchMinor / 2;
						int minorIndex = currentPoint.positionY - currentPoint.touchMinor / 2;
						gBuffer.drawArc(majorIndex, minorIndex,	
								currentPoint.touchMinor, currentPoint.touchMinor, 
								(int)currentPoint.orieniation, 360);
					}
				}
			}
			
			// �����һ����ԲȦ
			if (currentPoint != null) {
				Color baseLastPointColor = new Color(
						settingConfig.getConfigValue(
								DrawSettingPanel.BASE_LAST_POINT_COLOR_CONFIG_NAME, 
								DrawSettingPanel.DEFAULT_LAST_POINT_COLOR.getRGB()));
				gBuffer.setColor(baseLastPointColor); 
				gBuffer.fillOval(currentPoint.positionX - lastPointSize / 2, 
								currentPoint.positionY - lastPointSize / 2, 
								lastPointSize, lastPointSize);
			}
		}
	}
	
	private void drawMouseMove() {
		// �����������Ĺ켣
		
		gBuffer.setColor(Color.ORANGE);
		
		Point currentMousePoint = null;
		Point lastMousePoint = null;
		for (int i = 0; i < mouseMoveVector.size(); i++) {
			currentMousePoint = mouseMoveVector.get(i);
			if (i == 0) {
				gBuffer.drawLine(currentMousePoint.x, currentMousePoint.y, currentMousePoint.x, currentMousePoint.y);
			} else {
				lastMousePoint = mouseMoveVector.get(i - 1);
				gBuffer.drawLine(lastMousePoint.x, lastMousePoint.y, currentMousePoint.x, currentMousePoint.y);
			}
		}
	}
	
	private void drawSleepingImage(Graphics gBuffer) {
		gBuffer.setColor(Color.BLACK);
		gBuffer.fillRect(0, 0, this.getSize().width, this.getSize().height);
		
		// �������ֵĴ�С
		int textWidth = getSize().width / 2;
		int charWidth = textWidth / SLEEPING_TEXT.length();
		Font textFont = new Font("΢���ź�", Font.PLAIN, charWidth);
		gBuffer.setFont(textFont);
		
		FontMetrics textFontMetrics = gBuffer.getFontMetrics();
		Rectangle2D rcb = textFontMetrics.getStringBounds(SLEEPING_TEXT, gBuffer);
		int startIndexX = (int)((getWidth() - rcb.getWidth()) / 2);
		int startIndexY = (int)((getHeight() - rcb.getHeight()) / 2);
		
		gBuffer.setColor(Color.WHITE);
		gBuffer.drawString(SLEEPING_TEXT, startIndexX, startIndexY);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if(iBuffer == null || !windowDimension.equals(getSize())) {
			windowDimension = getSize();
			iBuffer = createImage(windowDimension.width, windowDimension.height);  
			gBuffer = iBuffer.getGraphics();  
			((Graphics2D)gBuffer).setRenderingHint(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON);
	    }
		
		if (isSuspend) {
			drawSleepingImage(gBuffer);
		} else {
			gBuffer.setColor(getBackground());
			gBuffer.fillRect(0, 0, this.getSize().width, this.getSize().height); 
			
			// �����ֻ���Ļ
			if (backgroundImage != null) {
				gBuffer.drawImage(backgroundImage.getScaledInstance(getWidth(), getHeight(),  
                        java.awt.Image.SCALE_SMOOTH), 0, 0, null);
			}
			
			// ��������
			if (isShowGriddingLine) {
				drawGridding(griddingRowCount, griddingColumnCount);
			}
			
			// �����ֻ���Ĵ����¼�
			drawTouch();
			drawMouseMove();
		}

		gBuffer.setColor(Color.BLACK);
		gBuffer.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		
		g.drawImage(iBuffer, 0, 0, this);
	}
	
	private void drawGridding(int rowCount, int columnCount) {
		
		gBuffer.setColor(new Color(0x54FF9F));
		
		double stepRow = 1.0 * getHeight() / rowCount;
		for (int i = 1; i < rowCount; i++) {
			gBuffer.drawLine(0, (int)(stepRow * i), getWidth(), (int)(stepRow * i));
		}
		
		double stepColumn = 1.0 * getWidth() / columnCount;
		for (int i = 1; i < columnCount; i++) {
			gBuffer.drawLine((int)(stepColumn * i), 0, (int)(stepColumn * i), getHeight());
		}
	}

	/**
	 * ��Ҫ�����ĵ������ת���ɵ�ǰ��Ļ�ϵ�����
	 * @param currentPoint
	 * @return
	 */
	private MultiTouchPoint exchangeToLocalPoint(MultiTouchPoint currentPoint) {
		MultiTouchPoint localPoint = new MultiTouchPointA();
		
		double widthRate = getWidth() / (1.0 * resolutionDimension.getWidth());
		double heightRate = getHeight() / (1.0 * resolutionDimension.getHeight());
		
		localPoint.positionX = (int)(currentPoint.positionX * widthRate);
		localPoint.positionY = (int)(currentPoint.positionY * heightRate);
		
		localPoint.touchMajor = (int)(currentPoint.touchMajor * widthRate);
		localPoint.touchMinor = (int)(currentPoint.touchMinor * heightRate);
		localPoint.orieniation = currentPoint.orieniation;
		
		return localPoint;
	}
	
	private Point exchangeLocalLocationToTouchLocation(Point localPoint) {
		Point touchLocation = new Point();
		touchLocation.y = (int)(localPoint.y / (1.0 * getHeight() / resolutionDimension.getHeight()));
		touchLocation.x = (int)(localPoint.x / (1.0 * getWidth() / resolutionDimension.getWidth()));
		
		return touchLocation;
	}

	public void setResolution(int width, int height) {
		resolutionDimension.setSize(width, height);
		repaint();
	}
	
	public Dimension getResolution() {
		return resolutionDimension;
	}
	
	public final static int DEFAULT_PANEL_WIDTH = 720;
	public final static int DEFAULT_PANEL_HEIGHT = 1280;

	/** ***************************************************************************
	 * ������صĴ���
	 * ������ڻ��ƽ����ϵ����ʱ�򣬽����ĵ�����ת�����ֻ��ϵ�����
	 ************************************************************************** **/
	private Point exchangePoint(Point srcPoint) {
		int width = getWidth();
		int height = getHeight();
		
		double widthRate = (resolutionDimension.getWidth() * 1.0) / width;
		double heightRate = (resolutionDimension.getHeight() * 1.0) / height;
		
		Point outPoint = new Point();
		outPoint.x = (int)(widthRate * srcPoint.x);
		outPoint.y = (int)(heightRate * srcPoint.y);
		
		return outPoint;
	}
	
	private void initControl() {
		mousePressePoint = new Point();
		mouseReleasePoint = new Point();
		mouseMoveVector = new Vector<Point>();
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			// ���ֻ����͵���¼�
			Point eventPoint = exchangePoint(new Point(e.getX(), e.getY()));
			PhoneEvent.reportTapEvent(eventPoint.x, eventPoint.y);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON1 && mousePreesed == false) {
			mousePressePoint.x = e.getX();
			mousePressePoint.y = e.getY();
			
			mouseMoveVector.add(new Point(e.getX(), e.getY()));
			mousePreesed = true;
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && mousePreesed == true) {
			
			// ������еĵ�
			if (mouseMoveVector.size() >= 2) {
				
				Point startPoint = exchangePoint(mouseMoveVector.get(0));
				Point endPoint = exchangePoint(mouseMoveVector.get(mouseMoveVector.size() - 1));
				PhoneEvent.reportSwipe(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
				
				repaint();
			}
			
			mouseMoveVector.removeAllElements();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		notifyMouseLocationChangeEvent(e.getX(), e.getY());
		mouseMoveVector.add(new Point(e.getX(), e.getY()));
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point touchPoint = exchangeLocalLocationToTouchLocation(e.getPoint());
		notifyMouseLocationChangeEvent(touchPoint.x, touchPoint.y);
	}

	@Override
	public void repaintTouchLine(Vector<TouchFinger> touchFingers) {
		this.touchFingers = touchFingers;
		this.repaint();
	}
	
	private void notifyMouseLocationChangeEvent(int locationX, int locationY) {
		for (int i = 0; i < mouseLocationChangeListeners.size(); i++) {
			mouseLocationChangeListeners.get(i).mouseLocationChangeEvent(locationX, locationY);
		}
	}
	
	public static void addMouseLocationChangeListener(MouseLocationChangeListener listener) {
		for (int i = 0; i < mouseLocationChangeListeners.size(); i++) {
			if (mouseLocationChangeListeners.get(i) == listener) {
				return;
			}
		}
		
		mouseLocationChangeListeners.add(listener);
		listener.mouseLocationChangeEvent(mouseLocationX, mouseLocationY);
	}
	
	public static void removeMouseLocationChangeListener(MouseLocationChangeListener listener) {
		int listenerCount = mouseLocationChangeListeners.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			mouseLocationChangeListeners.remove(i);
		}
	}

	@Override
	public void grridingLineStateChangeEvent(boolean show, int rowCount, int columnCount) {
		isShowGriddingLine = show;
		griddingRowCount = rowCount;
		griddingColumnCount = columnCount;
		repaint();
	}

	@Override
	public void setToResume() {
		isSuspend = false;
		repaint();
	}

	@Override
	public void setToSuspend() {
		isSuspend = true;
		repaint();
	}

	@Override
	public void touchShowBackgroundChangeEvent(BufferedImage backgroundImage) {
		if (backgroundImage == null) {
			return;
		}
		
		this.backgroundImage = backgroundImage;
		repaint();
	}
	
	@Override
	public boolean touchShowControlSaveImageEvent(String formatName, String path) {
		BufferedImage writeImage = new BufferedImage(iBuffer.getWidth(null), iBuffer.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		writeImage.getGraphics().drawImage(iBuffer, 0, 0, null);
		try {
			ImageIO.write(writeImage, formatName, new File(path));
		} catch (IOException e) {
			HLog.el(e);
			return false;
		}

		return true;
	}

	@Override
	public void touchShowBackgroundTypeChangeEvent(
			TouchShowBackgroundType backgroundType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resolutionChangeEvent(Dimension resolution) {
		resolutionDimension.setSize(resolution);
		repaint();
	}
}
