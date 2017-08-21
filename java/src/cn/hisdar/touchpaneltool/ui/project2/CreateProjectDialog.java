package cn.hisdar.touchpaneltool.ui.project2;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.hisdar.HFolderDialog.HFolderDialog;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.ui.HVerticalLineLabel;
import cn.hisdar.lib.ui.UIAdapter;
import cn.hisdar.radishlib.FileAdapter;
import cn.hisdar.touchpaneltool.MainFrame;

public class CreateProjectDialog extends JDialog implements ActionListener {

	// 1、记录上次创建时选择的目录
	
	private static final long serialVersionUID = 6184083256759274457L;

	public enum CreateProjectAction {
		ACTION_CANCLE,
		ACTION_OK
	}

	private final static String HISTORY_PATH_CONFIG_FILE = "./config/create_project_history_path.xml";

	private static final String HISTORY_FILE_PATH_NAME = "historyFilePath";
	private static final String HISTORY_FOLDER_PATH_NAME = "historyFolderPath";
	
	private JLabel logoLabel;
	private JButton okButton;
	private JButton cancleButton;
	private JButton scanSrcButton;
	private JTextField projectNameField;
	private JTextField projectSrcPathField;
	
	private boolean addFile = true;
	
	private Font textFount;
	
	private KeyEventHandler keyEventHandler = null;
	private DialogEventHander dialogEventHander = null;
	
	private String projectName;
	private String projectSrcPath;
	
	private CreateProjectAction createProjectAction = CreateProjectAction.ACTION_CANCLE;

	public CreateProjectDialog() {
		
		setSize(600, 290);
		setTitle("添加工程");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocation(UIAdapter.getCenterLocation(null, this));
		
		keyEventHandler = new KeyEventHandler();
		dialogEventHander = new DialogEventHander();
		
		addWindowListener(dialogEventHander);
		
		textFount = new Font("微软雅黑", Font.PLAIN, 14);
		setLayout(new BorderLayout());
		logoLabel = new JLabel(new ImageIcon("./Image/VersionDialogLogo.png"));
		add(logoLabel, BorderLayout.NORTH);
		
		JPanel inputPanel = createInputPanel();
		add(inputPanel, BorderLayout.CENTER);
		
		JPanel actionPanel = createActionPanel();
		add(actionPanel, BorderLayout.SOUTH);
	}
	
	private JPanel createActionPanel() {
		JPanel actionPanel = new JPanel(new BorderLayout());
		actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));
		
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 300, 10, 40));
		okButton = new JButton("添加");
		cancleButton = new JButton("取消");
		okButton.setFont(textFount);
		cancleButton.setFont(textFount);
		okButton.addActionListener(this);
		cancleButton.addActionListener(this);
		
		okButton.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		cancleButton.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		buttonPanel.add(cancleButton);
		buttonPanel.add(okButton);
		
		HVerticalLineLabel buttonLine = new HVerticalLineLabel();
		actionPanel.add(buttonLine, BorderLayout.NORTH);
		actionPanel.add(buttonPanel, BorderLayout.CENTER);
		
		return actionPanel;
	}
	
	private JPanel createInputPanel() {
		JPanel inputProjectPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		
		JLabel projectNameLabel = new JLabel("   工程名称：");
		JLabel projectSourceLabel = new JLabel("源文件目录：");
		projectNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
		projectNameLabel.setFont(textFount);
		projectSourceLabel.setFont(textFount);
		
		JPanel projectNamePanel = new JPanel(new BorderLayout());
		JPanel projectSourcePanel = new JPanel(new BorderLayout());
		
		projectNameField = new JTextField();
		projectSrcPathField = new JTextField();
		projectNameField.setFont(textFount);
		projectSrcPathField.setFont(textFount);
		projectNameField.addKeyListener(keyEventHandler);
		projectSrcPathField.addKeyListener(keyEventHandler);
		
		projectNamePanel.add(projectNameLabel, BorderLayout.WEST);
		projectSourcePanel.add(projectSourceLabel, BorderLayout.WEST);
		
		projectNamePanel.add(projectNameField, BorderLayout.CENTER);
		projectSourcePanel.add(projectSrcPathField, BorderLayout.CENTER);
		
		inputProjectPanel.add(projectNamePanel);
		inputProjectPanel.add(projectSourcePanel);
		
		scanSrcButton = new JButton("选择");
		scanSrcButton.setFont(textFount);
		scanSrcButton.addActionListener(this);
		
		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		inputPanel.add(inputProjectPanel, BorderLayout.CENTER);
		inputPanel.add(scanSrcButton, BorderLayout.EAST);
		
		return inputPanel;
	}
	
	public CreateProjectAction showProjectCreateDialog(boolean isAddFile) {
		addFile = isAddFile;
		setModal(true);
		setVisible(true);
		
		return createProjectAction;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == scanSrcButton) {
			pathChoiceButtonAction();
		} else if (e.getSource() == okButton) {
			okButtonEventHandler();
		} else if (e.getSource() == cancleButton) {
			projectName = null;
			projectSrcPath = null;
			createProjectAction = CreateProjectAction.ACTION_CANCLE;
			setVisible(false);
		}
	}
	
	public void okButtonEventHandler() {
		projectName = projectNameField.getText().trim();
		projectSrcPath = projectSrcPathField.getText().trim();
		
		if (projectName == null || projectSrcPath == null || projectName.equals("") || projectSrcPath.equals("")) {
			createProjectAction = CreateProjectAction.ACTION_CANCLE;
		} else {
			createProjectAction = CreateProjectAction.ACTION_OK;
			setVisible(false);
		}
	}
	
	private String choiceFolder(String defaultPath) {
		HFolderDialog hFolderDialog = new HFolderDialog();
		if (defaultPath != null) {
			hFolderDialog.setFolder(defaultPath);
		}
		
		hFolderDialog.show();
		return hFolderDialog.getSelectedFolder();
	}
	
	private String choiceFiles(String defaultPath) {
		FileDialog fileDialog = new FileDialog(MainFrame.mainFrame);
		Point centerLocation = UIAdapter.getCenterLocation(MainFrame.mainFrame, fileDialog);
		fileDialog.setTitle("选择日志文件");
		
		if (defaultPath != null) {
			File defaultPathFile = new File(defaultPath);
			if (defaultPathFile.isFile()) {
				defaultPath = defaultPathFile.getParent();
			}
		} else {
			defaultPath = "./";
		}
		
		fileDialog.setDirectory(defaultPath);
		fileDialog.setLocation(centerLocation);
		fileDialog.setVisible(true);
		return FileAdapter.pathCat(fileDialog.getDirectory(), fileDialog.getFile());
	}
	
	private void pathChoiceButtonAction() {
		String srcPath = null;
		
		HConfig historyPathConfig = null;
		
		// 1、先判断路径框中是否有数据，有的话，使用路径框中路径做默认打开路径，
		// 2、如果路径框中没有输入，使用历史路径
		String defaultPath = projectSrcPathField.getText().trim();
		if (defaultPath == null || defaultPath.length() == 0) {
			historyPathConfig = HConfig.getInstance(HISTORY_PATH_CONFIG_FILE);
		}

		if (!addFile) {
			if (historyPathConfig != null) {
				defaultPath = historyPathConfig.getConfigValue(HISTORY_FILE_PATH_NAME);
			}
			
			srcPath = choiceFolder(defaultPath);
		} else {
			if (historyPathConfig != null) {
				defaultPath = historyPathConfig.getConfigValue(HISTORY_FOLDER_PATH_NAME);
			}
			
			srcPath = choiceFiles(defaultPath);
		}
		
		if (srcPath != null) {
			File selectedFolder = new File(srcPath);
			if (projectNameField.getText().trim().length() == 0) {
				projectNameField.setText(selectedFolder.getName());
			}
			
			projectSrcPathField.setText(selectedFolder.getPath());
		}
	}
	
	private class KeyEventHandler extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				okButtonEventHandler();
			}
			
			super.keyPressed(arg0);
		}
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectSrcPath() {
		return projectSrcPath;
	}

	public void setProjectSrcPath(String projectSrcPath) {
		this.projectSrcPath = projectSrcPath;
	}
	
	private class DialogEventHander extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			createProjectAction = CreateProjectAction.ACTION_CANCLE;
			setVisible(false);
			super.windowClosing(e);
		}
	}
	
//  for debug	
//==========================================================================================
//	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		} catch (ClassNotFoundException | InstantiationException
//				| IllegalAccessException | UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}
//		
//		CreateProjectDialog createProjectDialog = new CreateProjectDialog();
//		CreateProjectAction action = createProjectDialog.showProjectCreateDialog(true);
//		if (action == CreateProjectAction.ACTION_OK) {
//			System.out.println("Name:" + createProjectDialog.getProjectName());
//			System.out.println("Path:" + createProjectDialog.getProjectSrcPath());
//		} else {
//			System.out.println("Cancle!!!");
//		}
//		//System.exit(0);
//	}
}
