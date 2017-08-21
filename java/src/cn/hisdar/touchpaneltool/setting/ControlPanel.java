package cn.hisdar.touchpaneltool.setting;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ControlPanel extends JPanel implements ActionListener {
	
	private static final String SAVE_BUTTON_TEXT = " 应  用 ";
	private static final String RESET_BUTTON_TEXT = " 重  置 ";
	private static final String CANCLE_BUTTON_TEXT = " 完  成 ";
	
	// 按钮面板
	private JButton submitButton = null;
	private JButton resetButton = null;
	private JButton finishButton = null;
	
	private ArrayList<ControlActionListener> controlActionListeners;
	
	public ControlPanel() {
		controlActionListeners = new ArrayList<ControlActionListener>();
		setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

		submitButton = new JButton(SAVE_BUTTON_TEXT);
		resetButton = new JButton(RESET_BUTTON_TEXT);
		finishButton = new JButton(CANCLE_BUTTON_TEXT);

		submitButton.addActionListener(this);
		resetButton.addActionListener(this);
		finishButton.addActionListener(this);

		JPanel buttonBufferPanel = new JPanel(new GridLayout(1, 2, 15, 15));
		buttonBufferPanel.setBorder(BorderFactory
				.createEmptyBorder(0, 0, 0, 40));
		buttonBufferPanel.add(resetButton);
		buttonBufferPanel.add(submitButton);
		buttonBufferPanel.add(finishButton);
		
		add(buttonBufferPanel, BorderLayout.EAST);
	}

	public void addControlActionListeners(ControlActionListener listener) {
		for (int i = 0; i < controlActionListeners.size(); i++) {
			if (controlActionListeners.get(i) == listener) {
				return;
			}
		}
		
		controlActionListeners.add(listener);
	}

	public void removeControlActionListeners(ControlActionListener listener) {
		int listenerCount = controlActionListeners.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			if (controlActionListeners.get(i) == listener) {
				controlActionListeners.remove(i);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submitButton) {
			for (int i = 0; i < controlActionListeners.size(); i++) {
				controlActionListeners.get(i).submitEvent();
			}
		} else if (e.getSource() == finishButton) {
			for (int i = 0; i < controlActionListeners.size(); i++) {
				controlActionListeners.get(i).finishEvent();
			}
		} else if (e.getSource() == resetButton) {
			for (int i = 0; i < controlActionListeners.size(); i++) {
				controlActionListeners.get(i).resetEvent();
			}
		}
	}
}
