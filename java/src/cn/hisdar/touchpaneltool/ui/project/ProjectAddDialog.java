package cn.hisdar.touchpaneltool.ui.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cn.hisdar.HFolderDialog.HFolderDialog;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.FileAdapter;
import cn.hisdar.radishlib.ui.UIAdapter;
import cn.hisdar.touchpaneltool.DataPool;
import cn.hisdar.touchpaneltool.MainFrame;
import cn.hisdar.touchpaneltool.config.MainFrameConfig;
import cn.hisdar.touchpaneltool.ui.LineLabel;
import cn.hisdar.touchpaneltool.ui.project2.CreateProjectDialog;

public class ProjectAddDialog extends JDialog implements ActionListener {

	private static final String ADD_LOG_DIALOG_TITLE = "添加Log";

	private static final int ADD_LOG_DIALOG_WIDTH = 600;

	private static final int ADD_LOG_DIALOG_HEIGHT = 300;
	
	private JLabel nameLabel = null;
	private JLabel pathLabel = null;
	private JLabel titleLabel = null;
	
	private JTextField nameField = null;
	private JTextField pathField = null;
	
	private JCheckBox sortCheckBox = null;
	
	private JButton pathChoiceButton = null;
	private JButton okButton = null;
	private JButton cancleButton = null;
	
	private JPanel actionPanel = null;
	private JPanel inputPanel = null;
	private JPanel titlePanel = null;
	
	private MainFrameConfig mainConfig = null;
	
	private String projectName = null;
	private String logPath = null;;
	
	private boolean isLoadFolder = false;
	
	private KeyEventHandler keyEventHandler = null;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			HLog.el(e);
		}
		
		new ProjectAddDialog().show();
		//System.exit(0);
	}
	
	public ProjectAddDialog() {
		
		mainConfig = new MainFrameConfig();
		keyEventHandler = new KeyEventHandler();
		
		setTitle(ADD_LOG_DIALOG_TITLE);
		setSize(ADD_LOG_DIALOG_WIDTH, ADD_LOG_DIALOG_HEIGHT);
		setLocation(UIAdapter.getCenterLocation(null, this));
		setLayout(new BorderLayout());
		setIconImage(DataPool.programIcon);
		
		initTitlePanel();
		add(titlePanel, BorderLayout.NORTH);
		
		initInputPanel();
		add(inputPanel, BorderLayout.CENTER);
		
		initActionPanel();
		add(actionPanel, BorderLayout.SOUTH);
		
	}
	
	private void initTitlePanel() {
		titlePanel = new JPanel(new BorderLayout());
		titleLabel = new JLabel(new ImageIcon("./Image/VersionDialogLogo.png"));
		titleLabel.setFont(new Font("新宋体", Font.PLAIN, 18));
		titleLabel.setBackground(Color.WHITE);
		
		titlePanel.add(titleLabel, BorderLayout.CENTER);
		titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 0));
		titlePanel.setBackground(Color.WHITE);
	}
	
	private void initInputPanel() {
		inputPanel = new JPanel(new BorderLayout(5, 5));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 40));
		
		nameLabel = new JLabel("名称:");
		pathLabel = new JLabel("路径:");
		
		nameField = new JTextField();
		pathField = new JTextField();
		
		nameField.addKeyListener(keyEventHandler);
		pathField.addKeyListener(keyEventHandler);
		
		pathChoiceButton = new JButton("选择");
		pathChoiceButton.addActionListener(this);

		JPanel labelPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		labelPanel.add(nameLabel);
		labelPanel.add(pathLabel);
		
		JPanel textFieldPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		textFieldPanel.add(nameField);
		textFieldPanel.add(pathField);
		
		sortCheckBox = new JCheckBox("文件导入后进行智能排序");
		sortCheckBox.setSelected(true);
		
		inputPanel.add(labelPanel, BorderLayout.WEST);
		inputPanel.add(textFieldPanel, BorderLayout.CENTER);
		inputPanel.add(pathChoiceButton, BorderLayout.EAST);
		inputPanel.add(new JLabel(""), BorderLayout.SOUTH);
	}
	
	private void initActionPanel() {
		JPanel actionButtonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		actionButtonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
		
		okButton = new JButton("  确    认  ");
		cancleButton = new JButton("  取    消  ");
		
		okButton.addActionListener(this);
		cancleButton.addActionListener(this);
		
		actionButtonPanel.add(cancleButton);
		actionButtonPanel.add(okButton);
		
		Font buttonFont = new Font("", Font.PLAIN, 13);
		okButton.setFont(buttonFont);
		cancleButton.setFont(buttonFont);
		
		actionPanel = new JPanel(new BorderLayout());
		actionPanel.add(actionButtonPanel, BorderLayout.EAST);
		actionPanel.add(new LineLabel(), BorderLayout.NORTH);
		actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
	}
	
	public String getProjectName() {
		return projectName;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLoadType(boolean isFolder) {
		isLoadFolder = isFolder;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == pathChoiceButton) {
			pathChoiceButtonAction();
		} else if (e.getSource() == okButton) {
			
			okButtonEventHandler();
		} else if (e.getSource() == cancleButton) {
			projectName = null;
			logPath = null;
			setVisible(false);
		}
	}
	
	public void okButtonEventHandler() {
		projectName = nameField.getText().trim();
		logPath = pathField.getText().trim();
		
		setVisible(false);
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

	private String choiceFolder() {
		HFolderDialog hFolderDialog = new HFolderDialog();
		hFolderDialog.show();
		return hFolderDialog.getSelectedFolder();
	}
	
	private void pathChoiceButtonAction() {
		String srcPath = null;
		if (isLoadFolder) {
			srcPath = choiceFolder();
		} else {
			srcPath = choiceFiles();
		}
		
		if (srcPath != null) {
			File selectedFolder = new File(srcPath);
			if (nameField.getText().trim().length() == 0) {
				nameField.setText(selectedFolder.getName());
			}
			
			pathField.setText(selectedFolder.getPath());
			mainConfig.setProjectAddPath(selectedFolder.getPath());
		}
	}

	private String choiceFiles() {
		FileDialog fileDialog = new FileDialog(MainFrame.mainFrame);
		fileDialog.setVisible(true);
		return FileAdapter.pathCat(fileDialog.getDirectory(), fileDialog.getFile());
	}

}
