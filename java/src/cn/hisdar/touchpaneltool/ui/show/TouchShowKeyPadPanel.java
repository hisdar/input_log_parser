package cn.hisdar.touchpaneltool.ui.show;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.hisdar.touchpaneltool.androidDevice.KeyCode;
import cn.hisdar.touchpaneltool.androidDevice.PhoneEvent;

public class TouchShowKeyPadPanel extends JPanel implements MouseListener {

	private JLabel backVoiceDownLabel = null;
	private JLabel homePowerLabel = null;
	private JLabel menuVoiceUpLabel = null;
	private JLabel switchLeftLabel = null;
	
	private ImageIcon homeIcon = null;
	private ImageIcon backIcon = null;
	private ImageIcon menuIcon = null;
	private ImageIcon voiceUpIcon = null;
	private ImageIcon voiceDownIcon = null;
	private ImageIcon powerIcon = null;
	private ImageIcon switchLeftIcon = null;
	private ImageIcon switchRightIcon = null;

	private boolean homeBackMenuState = true;
	
	public TouchShowKeyPadPanel() {
		setLayout(null);
		setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		homePowerLabel = new JLabel();
		backVoiceDownLabel = new JLabel();
		menuVoiceUpLabel = new JLabel();
		
		switchLeftLabel = new JLabel();
		
		homeIcon = new ImageIcon("./Image/HOME.png");
		homePowerLabel.setIcon(homeIcon);
		homePowerLabel.setHorizontalAlignment(JLabel.CENTER);
		
		backIcon = new ImageIcon("./Image/BACK.png");
		backVoiceDownLabel.setIcon(backIcon);
		backVoiceDownLabel.setHorizontalAlignment(JLabel.CENTER);
		
		menuIcon = new ImageIcon("./Image/MENU.png");
		menuVoiceUpLabel.setIcon(menuIcon);
		menuVoiceUpLabel.setHorizontalAlignment(JLabel.CENTER);
		
		switchLeftIcon = new ImageIcon("./Image/VECYOR_RIGHT.png");
		switchLeftLabel.setIcon(switchLeftIcon);
		
		switchRightIcon = new ImageIcon("./Image/VECYOR_LEFT.png");
		
		powerIcon = new ImageIcon("./Image/POWER.png");
		voiceUpIcon = new ImageIcon("./Image/VOICE_UP.png");
		voiceDownIcon = new ImageIcon("./Image/VOICE_DOWN.png");
		
		add(backVoiceDownLabel);
		add(homePowerLabel);
		add(menuVoiceUpLabel);
		add(switchLeftLabel);
		
		switchLeftLabel.addMouseListener(this);
		homePowerLabel.addMouseListener(this);
		backVoiceDownLabel.addMouseListener(this);
		menuVoiceUpLabel.addMouseListener(this);

		updateButtonsLocation();
	}
	
	private void updateButtonsLocation() {
		int homeMenuBackPanelWidth = getWidth() - switchLeftIcon.getIconWidth();
		
		int labelSpaceWidth = homeMenuBackPanelWidth / 3;
		int backLabelLocationX = 0;
		if (labelSpaceWidth > backVoiceDownLabel.getWidth()) {
			backLabelLocationX = (labelSpaceWidth - backVoiceDownLabel.getWidth()) / 2;
		}

		int homeLabelLocationX = backLabelLocationX + labelSpaceWidth;
		int menuLabelLocationX = homeLabelLocationX + labelSpaceWidth;
		
		homePowerLabel.setSize(homeIcon.getIconWidth(), getHeight());
		backVoiceDownLabel.setSize(backIcon.getIconWidth(), getHeight());
		menuVoiceUpLabel.setSize(menuIcon.getIconWidth(), getHeight());
		
		homePowerLabel.setLocation(homeLabelLocationX, 0);;
		backVoiceDownLabel.setLocation(backLabelLocationX, 0);
		menuVoiceUpLabel.setLocation(menuLabelLocationX, 0);
		
		switchLeftLabel.setLocation(homeMenuBackPanelWidth, 0);
		switchLeftLabel.setSize(switchLeftIcon.getIconWidth(), getHeight());
	}
	
	public int getPanelHeight() {
		// 20 is border
		return switchLeftIcon.getIconHeight() + 20;
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		updateButtonsLocation();
	}		

	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		updateButtonsLocation();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		updateButtonsLocation();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == switchLeftLabel) {
			if (homeBackMenuState) {
				switchLeftLabel.setIcon(switchRightIcon);
				homePowerLabel.setIcon(powerIcon);
				backVoiceDownLabel.setIcon(voiceDownIcon);
				menuVoiceUpLabel.setIcon(voiceUpIcon);
				homeBackMenuState = false;
			} else {
				switchLeftLabel.setIcon(switchLeftIcon);
				homePowerLabel.setIcon(homeIcon);
				backVoiceDownLabel.setIcon(backIcon);
				menuVoiceUpLabel.setIcon(menuIcon);
				homeBackMenuState = true;
			}
		} else if (e.getSource() == homePowerLabel) {
			if (homeBackMenuState) {
				PhoneEvent.reportKeyCode(KeyCode.KEYCODE_HOME);
			} else {
				PhoneEvent.reportKeyCode(KeyCode.KEYCODE_POWER);
			}
		} else if (e.getSource() == backVoiceDownLabel) {
			if (homeBackMenuState) {
				PhoneEvent.reportKeyCode(KeyCode.KEYCODE_BACK);
			} else {
				PhoneEvent.reportKeyCode(KeyCode.KEYCODE_VOLUME_DOWN);
			}
		} else if (e.getSource() == menuVoiceUpLabel) {
			if (homeBackMenuState) {
				PhoneEvent.reportKeyCode(KeyCode.KEYCODE_MENU);
			} else {
				PhoneEvent.reportKeyCode(KeyCode.KEYCODE_VOLUME_UP);
			}
		} 
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
}
