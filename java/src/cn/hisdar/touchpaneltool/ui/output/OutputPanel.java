package cn.hisdar.touchpaneltool.ui.output;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.ui.HTabbedPaneUI;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingChangeListener;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingPanel;
import cn.hisdar.touchpaneltool.ui.TitlePanel;
import cn.hisdar.touchpaneltool.ui.replay2.HImageArrayPanel;
import cn.hisdar.touchpaneltool.ui.replay2.HistoryTouchReplayFactory2;
import cn.hisdar.touchpaneltool.ui.replay2.OutputControlAndMessagePanel;
import cn.hisdar.touchpaneltool.ui.show.OutputClearListener;
import cn.hisdar.touchpaneltool.ui.show.TouchShowScreenInterface;

public class OutputPanel extends JPanel 
	implements AdjustmentListener, OutputClearListener, EnvironmentSettingChangeListener {
	
	private final static Color DEFAULT_DIVIDER_COLOR = new Color(0x293955);
	
	public static TouchEventShowPanel touchEventLogShowPanel = null;		//show touch event log
	private static ProgramLogShowPanel messageLogShowPanel = null;			//show program running log
	
	//private TimeTrackPanel timeTrackPanel = null;
	private JPanel outputMessageAndTabbedPanel = null;
	private HistoryTouchReplayFactory2 hisiHistoryTouchReplayFactory = null;
	private HImageArrayPanel historyTouchPanel = null;
	private JScrollBar historyTouchPanelScrollBar = null;
	
	private JTabbedPane outputTabbedPane = null;
	
	public OutputPanel(TouchShowScreenInterface paintTouchInterface) {
		setLayout(new BorderLayout());
		
		// 系统运行Log输出框
		messageLogShowPanel = ProgramLogShowPanel.getInstance();
		HLog.addHLogInterface(messageLogShowPanel);	
				
		// Event 事件输出框
		touchEventLogShowPanel = TouchEventShowPanel.getInstance();
		
		// 历史操作输出框
		historyTouchPanel = new HImageArrayPanel(40);
		historyTouchPanelScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 100, 10, 0, 110);
		historyTouchPanelScrollBar.addAdjustmentListener(this);
		
		OutputControlAndMessagePanel outputControlAndMessagePanel = new OutputControlAndMessagePanel();
		
		JPanel historyTouchPanelView = new JPanel(new BorderLayout());
		historyTouchPanelView.setBorder(null);
		historyTouchPanelView.add(historyTouchPanel, BorderLayout.CENTER);
		historyTouchPanelView.add(historyTouchPanelScrollBar, BorderLayout.SOUTH);
		
		
		hisiHistoryTouchReplayFactory = new HistoryTouchReplayFactory2(
				historyTouchPanel, 
				outputControlAndMessagePanel, 
				paintTouchInterface);
		
		// 事件轴面板
		//timeTrackPanel = new TimeTrackPanel();
		
		outputTabbedPane = new JTabbedPane();
		outputTabbedPane.setUI(new HTabbedPaneUI());
		
		outputTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

		outputTabbedPane.addTab("运行输出", messageLogShowPanel);
		outputTabbedPane.addTab("触摸日志", touchEventLogShowPanel);
		outputTabbedPane.addTab("操作历史", historyTouchPanelView);
		//outputTabbedPane.addTab(" 时间轴 ", timeTrackPanel);
		outputTabbedPane.setForegroundAt(0, Color.WHITE);
		outputTabbedPane.setForegroundAt(1, Color.WHITE);
		outputTabbedPane.setForegroundAt(2, Color.WHITE);
		//outputTabbedPane.setForegroundAt(3, Color.WHITE);

		outputTabbedPane.setBackgroundAt(0, DEFAULT_DIVIDER_COLOR);
		outputTabbedPane.setBackgroundAt(1, DEFAULT_DIVIDER_COLOR);
		outputTabbedPane.setBackgroundAt(2, DEFAULT_DIVIDER_COLOR);
		//outputTabbedPane.setBackgroundAt(3, DEFAULT_DIVIDER_COLOR);
		
		outputControlAndMessagePanel.addOutputClearListener(this);
		
		outputMessageAndTabbedPanel = new JPanel(new BorderLayout());
		outputMessageAndTabbedPanel.add(outputControlAndMessagePanel, BorderLayout.NORTH);
		outputMessageAndTabbedPanel.add(outputTabbedPane, BorderLayout.CENTER);
		outputMessageAndTabbedPanel.setBackground(DEFAULT_DIVIDER_COLOR);
		
		TitlePanel titlePanel = new TitlePanel("输出");
		
		add(outputMessageAndTabbedPanel, BorderLayout.CENTER);
		add(titlePanel, BorderLayout.NORTH);
		setBackground(DEFAULT_DIVIDER_COLOR);
		EnvironmentSettingPanel.addEnvironmentSettingChangeListener(this);
	}
	
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getSource() == historyTouchPanelScrollBar) {
			
			int maxRange = historyTouchPanelScrollBar.getMaximum() - 10;
			double range = (1.0 * (e.getValue()) / maxRange);
			historyTouchPanel.setImageLocation(range);
		}
	}

	@Override
	public void clearEvent() {
		
		// 这个地方一定要和 outputTabbedPane.addTab 对应
		switch (outputTabbedPane.getSelectedIndex()) {
		case 0:
			messageLogShowPanel.clearEvent();
			break;
		case 1:
			touchEventLogShowPanel.clearEvent();
			break;
		case 2:
			hisiHistoryTouchReplayFactory.clearEvent();
			break;
		default:
			break;
		}
	}

	@Override
	public void colorChangeEvent(String settingType, Color color) {
		if (settingType.equals(EnvironmentSettingPanel.THEME_COLOR_CONFIG_NAME)) {
			outputTabbedPane.setBackgroundAt(0, color);
			outputTabbedPane.setBackgroundAt(1, color);
			outputTabbedPane.setBackgroundAt(2, color);
			outputMessageAndTabbedPanel.setBackground(color);
			setBackground(color);
		}
	}
}
