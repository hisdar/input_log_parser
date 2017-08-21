package cn.hisdar.touchpaneltool.ui.show;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

import cn.hisdar.touchpaneltool.ui.control.TouchShowControlPanel;

public class TouchShowPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3814948307549449655L;
	private TouchShowControlPanel controlPanel = null;
	private TouchShowScreenAndKeyPadPanel screenAndKeyPadPanel = null;
	private TouchShowMessagePanel touchShowMessagePanel = null;
	private ProgressControlPanel2 progressControlPanel = null;
	
	public TouchShowPanel() {
		setLayout(new BorderLayout());
		
		
		screenAndKeyPadPanel = new TouchShowScreenAndKeyPadPanel();
		touchShowMessagePanel = new TouchShowMessagePanel();
		controlPanel = new TouchShowControlPanel();
		
		progressControlPanel = new ProgressControlPanel2();
		
		JPanel southPanel = new JPanel(new GridLayout(2, 1));
		southPanel.add(progressControlPanel);
		southPanel.add(touchShowMessagePanel);
		
		
		add(controlPanel, BorderLayout.NORTH);
		add(screenAndKeyPadPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}

	public TouchShowControlPanel getControlPanel() {
		return controlPanel;
	}

	public void setControlPanel(TouchShowControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	public TouchShowScreenAndKeyPadPanel getScreenAndKeyPadPanel() {
		return screenAndKeyPadPanel;
	}

	public void setScreenAndKeyPadPanel(
			TouchShowScreenAndKeyPadPanel screenAndKeyPadPanel) {
		this.screenAndKeyPadPanel = screenAndKeyPadPanel;
	}

	public TouchShowMessagePanel getTouchShowMessagePanel() {
		return touchShowMessagePanel;
	}

	public void setTouchShowMessagePanel(TouchShowMessagePanel touchShowMessagePanel) {
		this.touchShowMessagePanel = touchShowMessagePanel;
	}
	
}
