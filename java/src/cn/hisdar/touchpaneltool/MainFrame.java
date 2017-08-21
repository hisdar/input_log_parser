package cn.hisdar.touchpaneltool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.ui.HHorizontalLineLabel;
import cn.hisdar.lib.ui.HVerticalLineLabel;
import cn.hisdar.radishlib.ui.UIAdapter;
import cn.hisdar.touchpaneltool.ui.MainMenuBar;
import cn.hisdar.touchpaneltool.update.UpdateServer2;

public class MainFrame extends JFrame implements ActionListener {
	
	private static final String MAIN_FRAME_TITLE = "触摸屏开发者工具";
	
	private final static Color DEFAULT_DIVIDER_COLOR = new Color(0x293955);
	
	private InputEventParsePanel inputEventParsePanel = null;
	
	private MainMenuBar mainMenuBar = null;
	
	private JPanel welcomePanel = null;
	private JPanel functionListPanel = null;
	private JPanel descriptionPanel = null;
	
	private HLabel inputEventLabel = null;
	private HLabel capDataViewLabel = null;
	private HLabel dataCompareViewLabel = null;
	
	private UpdateServer2 updateServer = null;
	
	public static MainFrame mainFrame = null;
	
	public MainFrame() {
		if (mainFrame == null) {
			mainFrame = this;
		}
		
		// 启动版本检测服务
		updateServer = new UpdateServer2();
		updateServer.autoUpdate();
		
		// 初始化应用程序
		
		setTitle(MAIN_FRAME_TITLE);
		setSize(1180, 650);
		setLocation(UIAdapter.getCenterLocation(null, this));
		setLayout(new BorderLayout());
		setResizable(false);
		add(getWelcomePanel());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public JPanel getWelcomePanel() {
		welcomePanel = new JPanel();
		
		welcomePanel.setLayout(new BorderLayout());
		welcomePanel.setBorder(BorderFactory.createLineBorder(DEFAULT_DIVIDER_COLOR, 5));
		welcomePanel.add(getFunctionListPanel(), BorderLayout.WEST);
		
		welcomePanel.add(getLogoPanel(), BorderLayout.NORTH);
		welcomePanel.add(getDescriptionPane(), BorderLayout.CENTER);
		
		return welcomePanel;
	}

	private JPanel getDescriptionPane() {
		descriptionPanel = new JPanel();
		descriptionPanel.setLayout(new BorderLayout());
		
		descriptionPanel.add(new HHorizontalLineLabel(), BorderLayout.WEST);
		descriptionPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 15));
		
		JPanel descriptionMainPanel = new JPanel();
		descriptionMainPanel.setLayout(new BorderLayout());
		descriptionMainPanel.add(new JLabel(new ImageIcon("./Image/inputEventLogBig.png")));
		
		JLabel emptyLabel = new JLabel();
		emptyLabel.setPreferredSize(new Dimension(0, 100));
		//descriptionMainPanel.add(emptyLabel, BorderLayout.SOUTH);
		
		descriptionPanel.add(descriptionMainPanel, BorderLayout.CENTER);
		
		return descriptionPanel;
	}
	
	private JPanel getLogoPanel() {
		JPanel logoPanel = new JPanel();
		
		logoPanel.setLayout(new BorderLayout());
		
		logoPanel.add(new JLabel(new ImageIcon("./Image/programLogo.png")), BorderLayout.WEST);
		logoPanel.add(new HVerticalLineLabel(), BorderLayout.SOUTH);
		logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 15));
		
		return logoPanel;
	}
	
	private JPanel getFunctionListPanel() {
		
		inputEventLabel = new HLabel(new ImageIcon("./Image/inputEventLogo.png"), "  触摸事件解析");
		inputEventLabel.addActionListener(this);
		try {
			inputEventLabel.setDefaultImage(ImageIO.read(new File("./Image/BUTTON_BORDER.png")));
			inputEventLabel.setMouseInImage(ImageIO.read(new File("./Image/BUTTON_SELECTED.png")));
			inputEventLabel.setClickImage(ImageIO.read(new File("./Image/BUTTON_DOWN.png")));
		} catch (IOException e) {
			HLog.el(e);
		}
		
		functionListPanel = new JPanel();
		functionListPanel.setLayout(new GridLayout(5, 1, 5, 5));
		functionListPanel.add(inputEventLabel);
		
		functionListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel functionListBufferPanel = new JPanel();
		functionListBufferPanel.setLayout(new BorderLayout());
		functionListBufferPanel.add(functionListPanel, BorderLayout.NORTH);

		return functionListBufferPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == inputEventLabel) {
			remove(welcomePanel);
			inputEventParsePanel = new InputEventParsePanel();
			revalidate();
			add(inputEventParsePanel, BorderLayout.CENTER);
			mainMenuBar = new MainMenuBar();
			setJMenuBar(mainMenuBar);
			revalidate();
			setResizable(true);
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		
		updateServer.stopAutoUpdate();
	}
}
