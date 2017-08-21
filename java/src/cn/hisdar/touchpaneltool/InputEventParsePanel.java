package cn.hisdar.touchpaneltool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.ui.DividerLocationChangeListener;
import cn.hisdar.lib.ui.HSplitPane;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingChangeListener;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingPanel;
import cn.hisdar.touchpaneltool.ui.TitlePanel;
import cn.hisdar.touchpaneltool.ui.output.OutputPanel;
import cn.hisdar.touchpaneltool.ui.project2.Project;
import cn.hisdar.touchpaneltool.ui.project2.action.ProjectAction;
import cn.hisdar.touchpaneltool.ui.project2.action.ProjectPopMenuAction;
import cn.hisdar.touchpaneltool.ui.project2.view.ProjectView;
import cn.hisdar.touchpaneltool.ui.show.TouchShowPanel;

/*******************************************************************************************************
 *                        *                  触摸预览区域  HistoryTouchPanel                     *        *
 *                        *********************************************************************        *
 *                        *                                                                   * Control*
 *                        *                  DrawPanel                                        *  Panel *
 *                        *                                                                   *        *
 *       FileOpenPanel    *                                                                   *        *
 *                        *                                                                   *        *
 *                        *                                                                   *        *
 *                        ******************************************************************************
 *                        *                  Touch     *    message log show panel                     *
 *                        *                                        *                                   *
 *******************************************************************************************************/

public class InputEventParsePanel extends JPanel 
	implements DividerLocationChangeListener, EnvironmentSettingChangeListener {

	private final static Color DEFAULT_DIVIDER_COLOR = new Color(0x293955);
	public final static String INPUT_EVENT_PARSE_PANEL_CONFIG_PATH = "./Config/inputEventParseConfig.xml";
	public final static String PANEL_WIDTH_CONFIG_NAME = "inputEventParsePanelWidth";
	public final static String PANEL_HEIGHT_CONFIG_NAME = "inputEventParsePanelHeight";
	public final static String PROJECT_SPLIT_PANE_CONFIG_NAME = "projectSplitPaneWidth";
	public final static String DRAW_AND_OUTPUT_SPLIT_PANE_CONFIG_NAME = "drawAndOutputSplitPaneHeight";
	
	public final int PROJECT_SPLIT_PANEL_WIDTH = 250;
	public final double DRAW_AND_OUTPUT_SPLIT_PANE_HEIGHT = 0.75;
	
	private JPanel touchEventParsePanel = null;
	private HSplitPane projectSplitPane = null;
	private HSplitPane drawAndOutputSplitPane = null;
	
	private ProjectView projectPanel = null;
	
	private TouchShowPanel touchShowPanel = null;
	private OutputPanel outputPanel = null;
	
	
	private HConfig inputEventParsePanelConfig = null;
	
	public InputEventParsePanel() {
		inputEventParsePanelConfig = HConfig.getInstance(INPUT_EVENT_PARSE_PANEL_CONFIG_PATH, true);
		
		intUI();
		EnvironmentSettingPanel.addEnvironmentSettingChangeListener(this);
	}
	
	private void intUI() {
		
		setLayout(new BorderLayout());
		
		drawAndOutputSplitPane = new HSplitPane(HSplitPane.HORIZONTAL_SPLIT);
		drawAndOutputSplitPane.setPreferredSize(new Dimension(0, 600));
		drawAndOutputSplitPane.addDividerLocationChangeListener(this);
		
		drawAndOutputSplitPane.setTopComponent(getTouchDrawPanel());
		outputPanel = new OutputPanel(touchShowPanel.getScreenAndKeyPadPanel().touchScreenPanel);
		drawAndOutputSplitPane.setBottomComponent(outputPanel);
		drawAndOutputSplitPane.setDividerLocation(500);
		
		projectSplitPane = new HSplitPane(HSplitPane.VERTICAL_SPLIT);
		projectSplitPane.setTopComponent(getProjectPanel());       		// add panel to the left
		projectSplitPane.setBottomComponent(drawAndOutputSplitPane);  	// add panel to the right
		projectSplitPane.addDividerLocationChangeListener(this);
		
		touchEventParsePanel = new JPanel();
		touchEventParsePanel.setLayout(new BorderLayout());
		touchEventParsePanel.add(projectSplitPane, BorderLayout.CENTER);
		touchEventParsePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		touchEventParsePanel.setBackground(DEFAULT_DIVIDER_COLOR);
		
		add(touchEventParsePanel, BorderLayout.CENTER);
		drawAndOutputSplitPane.addDividerLocationChangeListener(this);
		projectSplitPane.addDividerLocationChangeListener(this);
	}
	
	private JPanel getProjectPanel() {
		
		// 创建工程视图
		projectPanel = new ProjectView();
		
		// 创建工程视图的事件处理器
		ProjectAction projectAction = ProjectAction.getInstance();
		projectAction.setProjectControlInterface(projectPanel);
		
		// 创建工程视图的控制器
		ProjectPopMenuAction projectPopMenuAction = new ProjectPopMenuAction();
		projectPopMenuAction.addProjectActionListener(projectAction);
		
		// 注册工程视图的事件监听者
		projectPanel.addProjectViewActionListener(projectPopMenuAction);
		
		// 加载工程
		loadProject(projectPanel);
		
		TitlePanel titlePanel = new TitlePanel("工程视图");
		
		JPanel projectPanelUI = new JPanel();
		projectPanelUI.setLayout(new BorderLayout());
		projectPanelUI.add(projectPanel, BorderLayout.CENTER);
		projectPanelUI.add(titlePanel, BorderLayout.NORTH);
		
		return projectPanelUI;
	}
	
	private void loadProject(ProjectView projectPanel) {
		// 获取工程目录
		HConfig programConfig = HConfig.getInstance(ProgramConfiguration.PROGRAM_CONFIG_FILE_PATH);
		if (programConfig == null) {
			return;
		}
		
		String projectPath = programConfig.getConfigValue(ProgramConfiguration.PROJECT_PATH_CONFIG_NAME);
		if (projectPath == null) {
			return;
		}
		
		// 获取工程目录下的所有目录，并将工程添加到列表中
		File projectPathFile = new File(projectPath);
		File[] projectFolders = projectPathFile.listFiles();
		if (projectFolders == null) {
			return;
		}
		
		for (int i = 0; i < projectFolders.length; i++) {
			if (projectFolders[i].isDirectory()) {
				Project project = Project.loadProject(projectFolders[i].getPath());
				if (project == null) {
					continue;
				}
				
				projectPanel.addProject(project);
			}
		}
	}
	
	// 初始化绘制触摸操作的面板
	private JPanel getTouchDrawPanel() {
		
		touchShowPanel = new TouchShowPanel();
		TitlePanel titlePanel = new TitlePanel("触摸操作视图");
		
		JPanel touchDrawPanel = new JPanel();
		touchDrawPanel.setLayout(new BorderLayout());
		touchDrawPanel.add(touchShowPanel, BorderLayout.CENTER);
		touchDrawPanel.add(titlePanel, BorderLayout.NORTH);
		
		return touchDrawPanel;
	}
	
	private void initSize() {
		int projectPanelWidth = inputEventParsePanelConfig.getConfigValue(PROJECT_SPLIT_PANE_CONFIG_NAME, PROJECT_SPLIT_PANEL_WIDTH);
		projectSplitPane.setDividerLocation(projectPanelWidth);

		double drawAndOutputSplitPaneHeight = 
				inputEventParsePanelConfig.getConfigValue(DRAW_AND_OUTPUT_SPLIT_PANE_CONFIG_NAME, 
						DRAW_AND_OUTPUT_SPLIT_PANE_HEIGHT);
		
		if (drawAndOutputSplitPaneHeight <= 0 || drawAndOutputSplitPaneHeight >= 0.9) {
			drawAndOutputSplitPaneHeight = DRAW_AND_OUTPUT_SPLIT_PANE_HEIGHT;
		}
		
		drawAndOutputSplitPane.setDividerLocation(drawAndOutputSplitPaneHeight);
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		initSize();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		// 重新设置界面各个区域的大小
		initSize();
	}

	@Override
	public void DividerLocationChangeEvent(HSplitPane splitPane) {
		if (splitPane == projectSplitPane) {
			inputEventParsePanelConfig.setConfigItem(new ConfigItem(PROJECT_SPLIT_PANE_CONFIG_NAME, splitPane.getDividerLocation()));
		} else if (splitPane == drawAndOutputSplitPane) {
			if (splitPane.getWidth() <= 0) {
				return;
			} else {
				double controlPanelWidth = (1.0 * splitPane.getDividerLocation()) / splitPane.getHeight();
				if (controlPanelWidth > 0) {
					inputEventParsePanelConfig.setConfigItem(new ConfigItem(DRAW_AND_OUTPUT_SPLIT_PANE_CONFIG_NAME, controlPanelWidth + ""));
				}
			}
		}
	}

	@Override
	public void colorChangeEvent(String settingType, Color color) {
		if (settingType.equals(EnvironmentSettingPanel.THEME_COLOR_CONFIG_NAME)) {
			projectSplitPane.setDividerColor(color);
			drawAndOutputSplitPane.setDividerColor(color);
			touchEventParsePanel.setBackground(color);
		}
	}
}
