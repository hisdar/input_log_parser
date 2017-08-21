package cn.hisdar.touchpaneltool.ui.show;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import cn.hisdar.MultiTouchEventParse.EventParseFinishListener;
import cn.hisdar.MultiTouchEventParse.EventParseProgressListener;
import cn.hisdar.MultiTouchEventParse.EventParser;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchEventListener;
import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingChangeListener;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingPanel;
import cn.hisdar.touchpaneltool.ui.control.GriddingLineStateChangeListener;
import cn.hisdar.touchpaneltool.ui.control.GriddlingLineSetPanel;

public class TouchShowMessagePanel extends JPanel 
	implements MultiTouchEventListener, MouseLocationChangeListener, GriddingLineStateChangeListener,
				EventParseProgressListener, EventParseFinishListener, EnvironmentSettingChangeListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6936757975907263772L;
	private final static Color DEFAULT_BACKGROUD_COLOR = new Color(0xbcc7d8);
	private final static String LOCATION_DEFAULT_VALUE = "      ";
	
	private JProgressBar parseProgressBar = null;
	private JLabel touchLocationMessageNameLabel = null;
	private JLabel touchLocationMessageValueLabel = null;
	
	private JLabel mouseLocationMessageNameLabel = null;
	private JLabel mouseLocationMessageValueLabel = null;

	private JLabel griddingLineMessageNameLabel = null;
	private JLabel griddingLineMessageValueLabel = null;
	
	private JLabel systemTimeMessageNameLabel = null;
	private JLabel systemTimeMessageNameValue = null;
	
	private JLabel bootUpTimeMessageNameLabel = null;
	private JLabel bootUpTimeMessageNameValue = null;
	
	private EventParser eventParser = null;
	
	public TouchShowMessagePanel() {
		setBorder(BorderFactory.createEmptyBorder(2, 0, 6, 0));
		((FlowLayout)getLayout()).setVgap(5);
		((FlowLayout)getLayout()).setHgap(6);
		((FlowLayout)getLayout()).setAlignment(FlowLayout.LEFT);
		((FlowLayout)getLayout()).setAlignment(FlowLayout.RIGHT);
		setBackground(DEFAULT_BACKGROUD_COLOR);
		setPreferredSize(new Dimension(1, 28));
		
		String defaultLocationMessage = "[" + LOCATION_DEFAULT_VALUE + "," + LOCATION_DEFAULT_VALUE + "]";
		
		touchLocationMessageNameLabel = new JLabel("触摸坐标：");
		touchLocationMessageValueLabel = new JLabel(defaultLocationMessage);
		
		mouseLocationMessageNameLabel = new JLabel("鼠标坐标：");
		mouseLocationMessageValueLabel = new JLabel(defaultLocationMessage);
		
		griddingLineMessageNameLabel = new JLabel("网格线：");
		griddingLineMessageValueLabel = new JLabel();
		
		systemTimeMessageNameLabel = new JLabel("系统时间：");
		systemTimeMessageNameValue = new JLabel("00-00 00:00:00");

		bootUpTimeMessageNameLabel = new JLabel("系统时间：");
		bootUpTimeMessageNameValue = new JLabel("0.0");
		
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		add(griddingLineMessageNameLabel);
		add(griddingLineMessageValueLabel);
		
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		add(mouseLocationMessageNameLabel);
		add(mouseLocationMessageValueLabel);
		
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		add(touchLocationMessageNameLabel);
		add(touchLocationMessageValueLabel);
		
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		add(systemTimeMessageNameLabel);
		add(systemTimeMessageNameValue);
		
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		add(bootUpTimeMessageNameLabel);
		add(bootUpTimeMessageNameValue);
		
		add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		add(getPraseProgressPanel());
		
		eventParser = EventParser.getInstance();
		eventParser.addMultiTouchEventListener(this);
		eventParser.addEventParseProgressListener(this);
		eventParser.addEventParseFinishListener(this);
		TouchShowScreenPanel.addMouseLocationChangeListener(this);
		GriddlingLineSetPanel.addGriddingLineStateChangeListener(this);
		EnvironmentSettingPanel.addEnvironmentSettingChangeListener(this);
	}
	
	private JPanel getPraseProgressPanel() {
		JPanel parseProgressPanel = new JPanel();
		((FlowLayout)parseProgressPanel.getLayout()).setHgap(4);
		((FlowLayout)parseProgressPanel.getLayout()).setVgap(0);
		parseProgressPanel.setOpaque(false);
		
		JLabel parseProgressLabel = new JLabel("解析进度:");
		parseProgressLabel.setBorder(null);
		parseProgressBar = new JProgressBar(0, 100);
		parseProgressBar.setValue(0);
		parseProgressBar.setOpaque(false);
		
		parseProgressPanel.add(parseProgressLabel);
		parseProgressPanel.add(parseProgressBar);
		
		return parseProgressPanel;
	}
	
	public void setParseProgress(double progress) {
		if (progress < 0 || progress > 1) {
			return;
		}
		
		parseProgressBar.setValue((int)(progress * 100));
	}
	
	private void setLocationValue(JLabel label, int locationX, int locationY) {
		String locationXString = locationX + "      ";
		locationXString = locationXString.substring(0, 6);
		String locationYString = locationY + "      ";
		locationYString = locationYString.substring(0, 6);
		
		String locationMessage = "[" + locationXString + "," + locationYString + "]";
		label.setText(locationMessage);
	}
	
	@Override
	public void MultiTouchDowmEvent(MultiTouchPoint point) {
		updateTime(point.systemTime, point.bootUpTime);
		setLocationValue(touchLocationMessageValueLabel, point.positionX, point.positionY);
	}

	@Override
	public void MultiTouchUpEvent(MultiTouchPoint point) {
		updateTime(point.systemTime, point.bootUpTime);
		setLocationValue(touchLocationMessageValueLabel, point.positionX, point.positionY);
	}

	@Override
	public void MultiTouchActionEvent(MultiTouchPoint point) {
		updateTime(point.systemTime, point.bootUpTime);
		setLocationValue(touchLocationMessageValueLabel, point.positionX, point.positionY);
	}

	@Override
	public void MultiTouchUpEvent(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveSourceData(String sourceData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseLocationChangeEvent(int locationX, int locationY) {
		setLocationValue(mouseLocationMessageValueLabel, locationX, locationY);
	}

	@Override
	public void grridingLineStateChangeEvent(boolean show, int rowCount,
			int columnCount) {
		griddingLineMessageValueLabel.setText(rowCount + " × " + columnCount);
	}

	@Override
	public void parseProgressEvent(double progress) {
		setParseProgress(progress);
	}

	@Override
	public void parseFinishEvent() {
		JOptionPane.showMessageDialog(null, "解析完成", "通知", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void colorChangeEvent(String settingType, Color color) {
		if (settingType.equals(EnvironmentSettingPanel.MESSAGE_AND_CONTROL_VIEW_COLOR_CONFIG_NAME)) {
			setBackground(color);
			repaint();
		}
	}
	
	private void updateTime(String systemTime, double bootUpTime) {
		systemTimeMessageNameValue.setText(systemTime);
		
		DecimalFormat df = new DecimalFormat("#############0.000000");// 14位整数，6位小数
		String bootUpTimeString = df.format(bootUpTime);
		bootUpTimeMessageNameValue.setText(bootUpTimeString);
	}
}
