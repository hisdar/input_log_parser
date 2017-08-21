package cn.hisdar.touchpaneltool.ui.show;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

import cn.hisdar.touchpaneltool.interfaces.LogOutInterface;
import cn.hisdar.touchpaneltool.ui.control.ResolutionChangeListener;
import cn.hisdar.touchpaneltool.ui.control.ResolutionSetPanel;

public class TouchShowScreenAndKeyPadPanel extends JPanel implements ResolutionChangeListener {

	private static final int BORDER_TOP = 4;
	private static final int BORDER_BOTTOM = 4;
	
	// draw touch 
	public static TouchShowScreenPanel touchScreenPanel = null;
	//private KeyControlPanel keyControlPanel = null;
	private TouchShowKeyPadPanel keyPadPanel = null;
	
	//private TouchEventParseServer touchEventParseServer = null;
	private LogOutInterface logOut = null;
	
	// 绘制窗口的大小和绘制的分辨率匹配，
	// 程序会将绘制窗口的大小设置成分辨率的大小，当绘制窗体的大小大于界面的大小的时候，添加滚动条
	public static final int RRAW_PANEL_SIZE_MATCH_RESOLUTION = 1;
	
	// 绘制窗口的高度和窗体保持一致
	public static final int DRAW_PANEL_HEIGHT_MATCH_WINDOW = 2;
	
	// 绘制窗口的宽度和窗体保持一致
	public static final int DAW_PANEL_WIDTH_MATCH_WINDOW = 3;
	
	public TouchShowScreenAndKeyPadPanel() {
		
		setLayout(null);
		
		touchScreenPanel = new TouchShowScreenPanel();
		keyPadPanel = new TouchShowKeyPadPanel();

		add(touchScreenPanel);
		add(keyPadPanel);
		
		ResolutionSetPanel.addResolutionChangeListener(this);
	}
	
	private void updateDrawPanel() {
		Dimension drawPanelSize = getTouchScreenPanelSize();
		Point drawPanelLocation = getDrawPanelLocation(drawPanelSize);
		
		touchScreenPanel.setSize(drawPanelSize);
		touchScreenPanel.setLocation(drawPanelLocation);
		
		keyPadPanel.setSize(drawPanelSize.width, keyPadPanel.getPanelHeight());
		keyPadPanel.setLocation(getKeyPadPanelLocation(drawPanelSize, drawPanelLocation));
		keyPadPanel.repaint();
	}

	/**
	 * @description compute the key control panel position
	 * @param touchDrawSize 
	 * @param touchDrawLocation
	 * @return key panel position
	 */
	private Point getKeyPadPanelLocation(Dimension touchDrawSize, Point touchDrawLocation) {
		Point keyPadPanelLocation = new Point();
		
		keyPadPanelLocation.x = touchDrawLocation.x;
		keyPadPanelLocation.y = touchDrawLocation.y + touchDrawSize.height;
		
		return keyPadPanelLocation;
	}
	
	/**
	 * @description
	 * @param drawPanelSize
	 * @return
	 */
	private Point getDrawPanelLocation(Dimension drawPanelSize) {
		int startX = 0;
		int startY = 0;
		
		startX = (getWidth() - drawPanelSize.width) / 2;
		startY = (getHeight() - BORDER_BOTTOM - BORDER_TOP - drawPanelSize.height - keyPadPanel.getPanelHeight()) / 2 + BORDER_TOP;
		
		return new Point(startX, startY);
	}
	
	// 根据设置来设置绘制窗口的大小
	private Dimension getTouchScreenPanelSize() {
		Dimension touchScreenPanelSize = new Dimension();

		// 计算绘制轨迹的面板的尺寸，其中包含了按键的尺寸
		Dimension touchScreenPanelResolution = touchScreenPanel.getResolution();
		
		// 按键区的大小
		Dimension keyPadSize = keyPadPanel.getSize();
		
		// 屏幕分辨率的宽高比
		double touchScreenRate = 1.0 * touchScreenPanelResolution.width / touchScreenPanelResolution.height;
		
		// 计算用于显示屏幕和区域的宽高比
		double showPanelRate = 1.0 * getWidth() / (getHeight() - keyPadSize.getHeight());
		
		if (touchScreenRate >= showPanelRate) {
			// 如果touch screen 的宽高比更大，那么界面的上下将会有空白
			touchScreenPanelSize.width = getWidth();
			touchScreenPanelSize.height = (int)(touchScreenPanelSize.width / touchScreenRate) ;
		} else {
			// 如果touch screen 的宽高比更小，那么界面的左右将会有空白
			touchScreenPanelSize.height = getHeight() - (int)keyPadSize.getHeight() - BORDER_TOP - BORDER_BOTTOM;
			touchScreenPanelSize.width = (int)(touchScreenPanelSize.height * touchScreenRate);
		}
		
		return touchScreenPanelSize;
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		updateDrawPanel();
		repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		updateDrawPanel();
		super.paint(g);
	}
	
	public TouchShowScreenPanel getTouchDrawPanel() {
		return touchScreenPanel;
	}

	public void setTouchDrawPanel(TouchShowScreenPanel touchShowPanel) {
		touchScreenPanel = touchShowPanel;
	}

	public LogOutInterface getLogOut() {
		return logOut;
	}

	public void setLogOut(LogOutInterface logOut) {
		this.logOut = logOut;
	}

	@Override
	public void resolutionChangeEvent(Dimension resolution) {
		updateDrawPanel();
		repaint();
	}	
}
