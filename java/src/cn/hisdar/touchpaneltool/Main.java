package cn.hisdar.touchpaneltool;

import java.awt.Toolkit;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cn.hisdar.lib.log.HCmdLog;
import cn.hisdar.lib.log.HFileLog;
import cn.hisdar.lib.log.HLog;


public class Main {

	public static void main (String[] args) {
		
		HCmdLog hCmdLog = new HCmdLog();
		HLog.addHLogInterface(hCmdLog);
		//HLog.enableLogToFile("./Log/", "multiTouchParser.log");
		HFileLog hFileLog = new HFileLog();
		HLog.addHLogInterface(hFileLog);
		
		ProgramConfiguration programConfiguration = new ProgramConfiguration();
		programConfiguration.initProgramConfiguration();
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			HLog.el(e);
		}
	
		new Welcome().showWelcomeImage();
		
		DataPool.programIcon = Toolkit.getDefaultToolkit().getImage("./Image/Moon.png");
	    
		//InputEventParsePanel mainFrame = new InputEventParsePanel();
		//mainFrame.setIconImage(DataPool.programIcon);
		
		MainFrame mainFrame = new MainFrame();
		mainFrame.setIconImage(DataPool.programIcon);
		
		mainFrame.setVisible(true);
	}
	
	public static void exit() {
		System.exit(0);
	}
}
