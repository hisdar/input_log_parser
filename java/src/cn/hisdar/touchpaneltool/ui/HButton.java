package cn.hisdar.touchpaneltool.ui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

import cn.hisdar.lib.log.HLog;

public class HButton extends JLabel {

	private Color pressedColor = new Color(0xB0E2FF);
	private boolean mouseInflag = false;
	private MouseEventHandler mouseEventHandler = null;
	
	public HButton() {
		super();
		initParameters();
	}
	
	public HButton(Icon image) {
		super(image);
		initParameters();
	}
	
	public HButton(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		initParameters();
	}
	
	public HButton(String text) {
		super(text);
		initParameters();
	}
	
	public HButton(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		initParameters();
	}
	
	public HButton(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		initParameters();
	}
	
	private void initParameters() {
		mouseEventHandler = new MouseEventHandler();
		addMouseListener(mouseEventHandler);
		addMouseMotionListener(mouseEventHandler);
	}
	
	private class MouseEventHandler extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			HLog.dl("Mouse clicked");
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			HLog.dl("Mouse pressed");
			setBackground(pressedColor);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			HLog.dl("Mouse released");
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			HLog.dl("Mouse entered");
			mouseInflag = true;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			HLog.dl("Mouse exited");
			mouseInflag = false;
		}
	}
	
}
