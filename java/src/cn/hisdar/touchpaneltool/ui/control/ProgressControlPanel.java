package cn.hisdar.touchpaneltool.ui.control;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.hisdar.MultiTouchEventParse.EventDeviceMap;
import cn.hisdar.MultiTouchEventParse.EventParseFinishListener;
import cn.hisdar.MultiTouchEventParse.EventParser;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.ui.control.InputSourceSetPanel.InputSourceType;
import cn.hisdar.touchpaneltool.ui.project.ProjectHandlerInterface;
import cn.hisdar.touchpaneltool.ui.project.ProjectLoadListener;
import cn.hisdar.touchpaneltool.ui.project.ProjectView;
import cn.hisdar.touchpaneltool.ui.project2.Project;
import cn.hisdar.touchpaneltool.ui.project2.action.ProjectAction;
import cn.hisdar.touchpaneltool.ui.project2.action.ProjectEventListener;

/**
 * @description 进度控制面板，控制解析的开始，暂停，下一步，结束等操作
 * @author Hisdar
 * 
 */
public class ProgressControlPanel extends JPanel 
	implements ActionListener, InputSourceChangeListener, ProjectEventListener, EventParseFinishListener {
	
	private static final long serialVersionUID = 3414966603384790733L;

	private ImageIcon startButtonIcon = null;
	
	private ImageIcon suspendIcon = null;
	private ImageIcon resumeIcon = null;
	
	private JButton startButton = null;
	private JButton suspendButton = null;
	private JButton nextButton = null;
	private JButton stopButton = null;
	
	private TouchDrawControlFactory drawControler = null;
	
	// 解析源的路径
	private String inputStreamPath = null;
	
	private InputSourceType inputSource = null;
	
	private Project currentProject = null;
	
	private EventParser eventParser = null;
	
	public ProgressControlPanel(TouchDrawControlFactory drawControler) {
		
		this.drawControler = drawControler;
		
		JPanel controlPanel = new JPanel();
		FlowLayout controlPanelLayout = new FlowLayout();
		controlPanelLayout.setHgap(4);
		controlPanelLayout.setVgap(0);
		controlPanel.setLayout(controlPanelLayout);
		controlPanel.setOpaque(false);
		
		startButtonIcon = new ImageIcon("./Image/START.png");
		startButton     = new JButton(startButtonIcon);
		startButton.setEnabled(false);
		startButton.addActionListener(this);
		startButton.setBorder(null);
		startButton.setOpaque(false);
		
		suspendIcon = new ImageIcon("./Image/SUSPEND.png");
		resumeIcon  = new ImageIcon("./Image/RESUME.png");
		suspendButton = new JButton(suspendIcon);
		suspendButton.addActionListener(this);
		suspendButton.setEnabled(false);
		suspendButton.setBorder(null);
		suspendButton.setOpaque(false);
		
		nextButton = new JButton(new ImageIcon("./Image/NEXT.png"));
		nextButton.addActionListener(this);
		nextButton.setEnabled(false);
		nextButton.setBorder(null);
		nextButton.setOpaque(false);
		
		stopButton = new JButton(new ImageIcon("./Image/STOP.png"));
		stopButton.addActionListener(this);
		stopButton.setEnabled(false);
		stopButton.setBorder(null);
		stopButton.setOpaque(false);
		
		controlPanel.add(startButton);
		controlPanel.add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		
		controlPanel.add(suspendButton);
		controlPanel.add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		
		controlPanel.add(nextButton);
		controlPanel.add(new JLabel(new ImageIcon("./Image/divisionLine.png")));
		
		controlPanel.add(stopButton);
		
		setLayout(new BorderLayout());
		add(controlPanel, BorderLayout.CENTER);
		setOpaque(false);
		
		ProjectAction projectAction = ProjectAction.getInstance();
		projectAction.addProjectEventListener(this);
		InputSourceSetPanel.addInputSourceTypeChangeListener(this);
		
		eventParser = EventParser.getInstance();
		eventParser.addEventParseFinishListener(this);
	}
	
	public void reInitButtonsStatus() {
		stopButton.setEnabled(false);
		startButton.setEnabled(false);
		suspendButton.setEnabled(false);
		nextButton.setEnabled(false);
		suspendButton.setIcon(suspendIcon);
	}

	private void startButtonEventHandler(Project project) {
		// 开始解析
		HLog.il("Parse resource is " + inputStreamPath + "\n");
	
		// 从文件中接收输入
		HLog.il("Parse from file");
		if (new File(inputStreamPath).exists()) {
			HLog.il("Start file input draw");
			EventDeviceMap[] eventDeviceMaps = project.getEventDeviceMaps();
			if (eventDeviceMaps == null) {
				HLog.el("startButtonEventHandler: no event device map found:" + project.getProjectPath());
				JOptionPane.showMessageDialog(null, "没有找到事件和设备的对应表，请重新加载工程，如果重新加载工程也不行，请"
						+ "尝试重启程序！", "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			drawControler.startFileInputDraw(inputStreamPath, eventDeviceMaps);
		} else {
			JOptionPane.showMessageDialog(null, "请先加载要解析的对象！\n提示：在工程视图中点击右键，然后点解加载。", 
					"提示", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		suspendButton.setEnabled(true);
		
		return;
	}
	
	private void suspendButtonEventHandle() {
		if (suspendButton.getIcon() == suspendIcon) {
			drawControler.suspendDraw();
			suspendButton.setIcon(resumeIcon);
			nextButton.setEnabled(true);
		} else {
			drawControler.resumeDraw();
			suspendButton.setIcon(suspendIcon);
			nextButton.setEnabled(false);
		}
	}
	
	private void setButtonStateToLoaded() {
		stopButton.setEnabled(false);
		suspendButton.setEnabled(false);
		nextButton.setEnabled(false);
		startButton.setEnabled(true);
		suspendButton.setIcon(suspendIcon);
	}
	
	private void stopButtonEventHandle() {
		drawControler.stopDraw();
		reInitButtonsStatus();
		startButton.setEnabled(true);
	}

	private void nextButtonEventHandle() {
		drawControler.nextStep();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startButton) {
			startButtonEventHandler(currentProject);
		} else if (e.getSource() == suspendButton) {
			suspendButtonEventHandle();
		} else if (e.getSource() == nextButton) {
			nextButtonEventHandle();
		} else if (e.getSource() == stopButton) {
			stopButtonEventHandle();
		}
	}

	@Override
	public void inputSourceChangeListener(InputSourceType inputSourceType) {
		inputSource = inputSourceType;
		if (inputSourceType == InputSourceType.INPUT_SOURCE_PHONE) {
			reInitButtonsStatus();
		} else if (inputSourceType == InputSourceType.INPUT_SOURCE_LOG) {
			if (inputStreamPath != null) {
				setButtonStateToLoaded();
			}
		}
	}

	@Override
	public void deleteProjectEvent(Project project) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteProjectNodes(Project project, String[] filePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadProjectEvent(Project projects) {
		this.currentProject = projects;
		inputStreamPath = projects.getMergedFilePath();
		if (inputSource == InputSourceType.INPUT_SOURCE_LOG) {
			setButtonStateToLoaded();
		}
	}

	@Override
	public void createProjectEvent(Project project) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseFinishEvent() {
		stopButtonEventHandle();
	}
}
