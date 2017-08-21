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
		
		fileMenu = new JMenu(" �ļ� ");
		initFileMenu();
		
		helpMenu = new JMenu(" ���� ");
		initHelpMenu();
		
		viewMenu = new JMenu(" ��ͼ ");
		initViewMenu();
		
		optionMenu = new JMenu(" ѡ�� ");
		initOptionMenu();
		
		add(fileMenu);
		//add(viewMenu);
		add(optionMenu);
		add(helpMenu);
		
		settingDialog = new SettingDialog();
		aboutDialog = new AboutDialog();
		aboutDialog.setTitle("���� Multi-Touch Input Parser");
	}
	
	private void initViewMenu() {
		projectMenuItem = new JMenuItem("Log������ͼ");
		logmeMenuItem = new JMenuItem("Log�����ͼ");
		controlMenuItem = new JMenuItem("���������ͼ");
		
		viewMenu.add(projectMenuItem);
		viewMenu.add(logmeMenuItem);
		viewMenu.add(controlMenuItem);
	}
	
	private void initHelpMenu() {
		helpMenuItem = new JMenuItem("����");
		logCollectMenuItem = new JMenuItem("��־�ռ�");
		aboutMenuItem = new JMenuItem("���� Multi-Touch Input Parser");
		checkUpdateMenuItem = new JMenuItem("������");
		
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
		
		openMenuItem = new JMenuItem("��        ��");
		exitMenuItem = new JMenuItem("��        ��");
		exitMenuItem.addActionListener(this);
		
		openMenuItem.setAccelerator(openMenuItemKey);
		exitMenuItem.setAccelerator(exitMenuItemKey);
		
		fileMenu.add(exitMenuItem);
	}
	
	private void initOptionMenu() {
		settingMenuItem = new JMenuItem("��        ��");
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
