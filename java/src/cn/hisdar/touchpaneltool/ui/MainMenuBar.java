package cn.hisdar.touchpaneltool.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.touchpaneltool.Main;
import cn.hisdar.touchpaneltool.setting.SettingDialog;
import cn.hisdar.touchpaneltool.update.AboutDialog;
import cn.hisdar.touchpaneltool.update.UpdateServer2;

public class MainMenuBar extends JMenuBar implements ActionListener {

	private JMenu fileMenu   = null;
	private JMenu helpMenu   = null;
	private JMenu viewMenu   = null;
	private JMenu optionMenu = null;
	
	private JMenuItem openMenuItem = null;
	private JMenuItem exitMenuItem = null;
	
	private JMenuItem helpMenuItem    = null;
	private JMenuItem logCollectMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenuItem checkUpdateMenuItem = null;
	
	private JMenuItem projectMenuItem = null;
	private JMenuItem logmeMenuItem   = null;
	private JMenuItem controlMenuItem = null;
	
	private JMenuItem settingMenuItem = null;
	
	private SettingDialog settingDialog = null;
	private AboutDialog aboutDialog = null;
	
	public MainMenuBar() {
		
		fileMenu = new JMenu(" 文件 ");
		initFileMenu();
		
		helpMenu = new JMenu(" 帮助 ");
		initHelpMenu();
		
		viewMenu = new JMenu(" 视图 ");
		initViewMenu();
		
		optionMenu = new JMenu(" 选项 ");
		initOptionMenu();
		
		add(fileMenu);
		//add(viewMenu);
		add(optionMenu);
		add(helpMenu);
		
		settingDialog = new SettingDialog();
		aboutDialog = new AboutDialog();
		aboutDialog.setTitle("关于 Multi-Touch Input Parser");
	}
	
	private void initViewMenu() {
		projectMenuItem = new JMenuItem("Log工程视图");
		logmeMenuItem = new JMenuItem("Log输出视图");
		controlMenuItem = new JMenuItem("控制面板视图");
		
		viewMenu.add(projectMenuItem);
		viewMenu.add(logmeMenuItem);
		viewMenu.add(controlMenuItem);
	}
	
	private void initHelpMenu() {
		helpMenuItem = new JMenuItem("帮助");
		logCollectMenuItem = new JMenuItem("日志收集");
		aboutMenuItem = new JMenuItem("关于 Multi-Touch Input Parser");
		checkUpdateMenuItem = new JMenuItem("检测更新");
		
		helpMenu.add(helpMenuItem);
		helpMenu.add(logCollectMenuItem);
		helpMenu.add(checkUpdateMenuItem);
		helpMenu.add(aboutMenuItem);
		
		helpMenuItem.addActionListener(this);
		logCollectMenuItem.addActionListener(this);
		aboutMenuItem.addActionListener(this);
		checkUpdateMenuItem.addActionListener(this);
	}

	private void initFileMenu() {
		KeyStroke openMenuItemKey = KeyStroke.getKeyStroke("ctrl O");
		KeyStroke exitMenuItemKey = KeyStroke.getKeyStroke("alt F4");
		
		openMenuItem = new JMenuItem("打        开");
		exitMenuItem = new JMenuItem("退        出");
		exitMenuItem.addActionListener(this);
		
		openMenuItem.setAccelerator(openMenuItemKey);
		exitMenuItem.setAccelerator(exitMenuItemKey);
		
		fileMenu.add(exitMenuItem);
	}
	
	private void initOptionMenu() {
		settingMenuItem = new JMenuItem("设        置");
		settingMenuItem.addActionListener(this);
		
		optionMenu.add(settingMenuItem);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == settingMenuItem) {
			settingDialog.setVisible(true);
		} else if (e.getSource() == exitMenuItem) {
			Main.exit();
		} else if (e.getSource() == helpMenuItem) {
			 String strURL = "help/index.html" ;
			 try {
				Process p = Runtime.getRuntime().exec("cmd /c start " + strURL );
			} catch (IOException e1) {
				HLog.el(e1);
			}
		} else if (e.getSource() == logCollectMenuItem) {
			
		} else if (e.getSource() == aboutMenuItem) {
			aboutDialog.setVisible(true);
		} else if (e.getSource() == checkUpdateMenuItem) {
			new UpdateServer2().manualUpdate();
		}
	}
}
