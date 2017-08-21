package cn.hisdar.touchpaneltool.ui.control;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.hisdar.touchpaneltool.ui.control.InputSourceSetPanel.InputSourceType;

public class ParseSpeedControlPanel extends JPanel 
	implements ActionListener, InputSourceChangeListener {
	
	private JButton parseSpeedAddButton = null;
	private JButton parseSpeedMinusButton = null;
	private JButton speedTextButton = null;
	
	private InputSourceType inputSourceType = null;
	
	// 解析速度的分母
	private int parseSpeedDenominator = 1;
	
	// 解析速度的分子
	private int parseSpeedNumerator = 1;
	
	private static double currentParseSpeed = 1.0;
	private static ArrayList<ParseSpeedChangeListener> parseSpeedChangeListeners = new ArrayList<ParseSpeedChangeListener>();
	
	public ParseSpeedControlPanel() {
		setLayout(new BorderLayout());
		add(getParseSpeedPanel(), BorderLayout.CENTER);
		setOpaque(false);
		
		InputSourceSetPanel.addInputSourceTypeChangeListener(this);
	}
	
	private JPanel getParseSpeedPanel() {
		JPanel praseSpeedPanel = new JPanel();
		FlowLayout parseSpeedPanelLayout = new FlowLayout();
		parseSpeedPanelLayout.setHgap(2);
		parseSpeedPanelLayout.setVgap(0);
		praseSpeedPanel.setLayout(parseSpeedPanelLayout);
		praseSpeedPanel.setOpaque(false);
		
		JLabel parseSpeedLabel = new JLabel("解析速度：");

		speedTextButton = new JButton("1");
		speedTextButton.setEnabled(false);
		speedTextButton.setOpaque(false);
		
		parseSpeedAddButton = new JButton(new ImageIcon("./Image/JIA.png"));
		parseSpeedAddButton.addActionListener(this);
		parseSpeedAddButton.setBorder(null);
		parseSpeedAddButton.setOpaque(false);		
		
		parseSpeedMinusButton = new JButton(new ImageIcon("./Image/JIAN.png"));
		parseSpeedMinusButton.addActionListener(this);
		parseSpeedMinusButton.setBorder(null);
		parseSpeedMinusButton.setOpaque(false);
		
		praseSpeedPanel.add(parseSpeedLabel);
		praseSpeedPanel.add(parseSpeedMinusButton);
		praseSpeedPanel.add(speedTextButton);
		praseSpeedPanel.add(parseSpeedAddButton);
		
		return praseSpeedPanel;
	}
	
	private void disableButtonsStatus() {
		parseSpeedAddButton.setEnabled(false);
		parseSpeedMinusButton.setEnabled(false);
	}
	
	private void enableSpeedChangeButtons() {
		parseSpeedAddButton.setEnabled(true);
		parseSpeedMinusButton.setEnabled(true);
	}

	private void notifyParseSpeedChangeEvent(double speed) {
		for (int i = 0; i < parseSpeedChangeListeners.size(); i++) {
			parseSpeedChangeListeners.get(i).parseSpeedChangeEvent(speed);
		}
	}
	
	private void parseSpeedMinusButtonEventHandle() {
		// 如果分子是1，就在分母上加，如果分子不是1，就在分子上减
		if (parseSpeedNumerator == 1) {
			parseSpeedDenominator += 1;
		} else {
			parseSpeedNumerator -= 1;
		}
		
		// 如果分母是1，直接显示分子，如果分母不是1，显示分数
		if (parseSpeedDenominator == 1) {
			speedTextButton.setText("" + parseSpeedNumerator);
		} else {
			speedTextButton.setText("" + parseSpeedNumerator + "/" + parseSpeedDenominator);
		}
		
		// 将解析速度设置到解析进程中去
		double parseSpeed = 1.0 * parseSpeedDenominator / parseSpeedNumerator;

		currentParseSpeed = parseSpeed;
		notifyParseSpeedChangeEvent(parseSpeed);
	}

	private void parseSpeedAddButtonEventHandle() {
		// 加快解析速度的时候，先判断分母是不是大于1的，如果分母是大于1的，那么在分母上减，
		// 如果分母是等于1的，在分子上加
		if (parseSpeedDenominator == 1) {
			parseSpeedNumerator += 1;
		} else {
			parseSpeedDenominator -= 1;
		}
		
		// 如果分母是1，直接显示分子，如果分母不是1，显示分数
		if (parseSpeedDenominator == 1) {
			speedTextButton.setText("" + parseSpeedNumerator);
		} else {
			speedTextButton.setText("" + parseSpeedNumerator + "/" + parseSpeedDenominator);
		}
		
		// 将解析速率设置到解析进程中去
		double parseSpeed = 1.0 * parseSpeedDenominator / parseSpeedNumerator;
		
		currentParseSpeed = parseSpeed;
		notifyParseSpeedChangeEvent(parseSpeed);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == parseSpeedAddButton) {
			parseSpeedAddButtonEventHandle();
		} else if (e.getSource() == parseSpeedMinusButton) {
			parseSpeedMinusButtonEventHandle();
		} else {
			
		}
	}
	
	public static void addParseSpeedChangeListener(ParseSpeedChangeListener listener) {
		for (int i = 0; i < parseSpeedChangeListeners.size(); i++) {
			if (parseSpeedChangeListeners.get(i) == listener) {
				return;
			}
		}
		
		parseSpeedChangeListeners.add(listener);
		listener.parseSpeedChangeEvent(currentParseSpeed);
	}
	
	public static void removeParseSpeedChangeListener(ParseSpeedChangeListener listener) {
		int listenerCount = parseSpeedChangeListeners.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			if (listener == parseSpeedChangeListeners.get(i)) {
				parseSpeedChangeListeners.remove(i);
			}
		}
	}

	@Override
	public void inputSourceChangeListener(InputSourceType inputSourceType) {
		this.inputSourceType = inputSourceType;
		if (inputSourceType == InputSourceType.INPUT_SOURCE_LOG) {
			enableSpeedChangeButtons();
		} else if (this.inputSourceType == InputSourceType.INPUT_SOURCE_PHONE) {
			disableButtonsStatus();
		}
	}
}
