package cn.hisdar.touchpaneltool.ui.output;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import cn.hisdar.lib.log.HLogInterface;
import cn.hisdar.touchpaneltool.interfaces.LogOutInterface;
import cn.hisdar.touchpaneltool.ui.show.OutputClearListener;

public class LogShowPanel extends JPanel implements LogOutInterface, HLogInterface, OutputClearListener {
	
	private static final long serialVersionUID = 7594432922532380713L;
	
	private static final int DEFAULT_MAX_LINE_COUNT = 3000;
	
	private StringBuffer logBuffer = null;
	private JTextArea touchEventLogArea = null;
	//private ColoredTextPanel touchEventLogArea = null;
	//private TextPanelDocumentListener areaDocumentListener = null;
	private LogAreaDocumentListener areaDocumentListener = null;
	private JScrollPane touchEventLogScrollPane = null;
	
	private MouseEventHander textAreaMouseEventHander = null;
	private TextAreaPopupMenu textAreaPopupMenu = null;
	
	public LogShowPanel() {
		
		//touchEventLogArea = new ColoredTextPanel();
		touchEventLogArea = new JTextArea();
		touchEventLogArea.setEditable(false);
		touchEventLogScrollPane = new JScrollPane(touchEventLogArea);
		
		setLayout(new BorderLayout());
		add(touchEventLogScrollPane, BorderLayout.CENTER);
		
		logBuffer = new StringBuffer();

		//areaDocumentListener = new TextPanelDocumentListener(touchEventLogArea);
		areaDocumentListener = new LogAreaDocumentListener(touchEventLogArea);
		areaDocumentListener.setMaxLineCount(DEFAULT_MAX_LINE_COUNT);
		touchEventLogArea.getDocument().addDocumentListener(areaDocumentListener);
		touchEventLogScrollPane.setRowHeaderView(new LineNumberHeaderView());
		
		textAreaMouseEventHander = new MouseEventHander();
		touchEventLogArea.addMouseListener(textAreaMouseEventHander);
		
		textAreaPopupMenu = new TextAreaPopupMenu(touchEventLogArea);
	}

	@Override
	public void cleanLog() {
		logBuffer.delete(0, logBuffer.length());
		touchEventLogArea.setText("");
	}
	
	@Override
	public void outputLog(String logString) {
	
		touchEventLogArea.append(logString);
		touchEventLogArea.setCaretPosition(touchEventLogArea.getDocument().getLength());
	}
	
	@Override
	public void clearEvent() {
		cleanLog();
	}

	@Override
	public void info(String log) {
		outputLog(log);
	}

	@Override
	public void error(String log) {
		outputLog(log);
	}

	@Override
	public void debug(String log) {
		
	}
	
	/***************************************************************************************/
	private class TextAreaPopupMenu extends JPopupMenu implements ActionListener {
		
		private static final long serialVersionUID = -739519236161597731L;
		
		private JMenuItem copyItem = null;
		private JMenuItem clearItem = null;
		
		private JTextComponent textArea = null;
		
		public TextAreaPopupMenu(JTextComponent textArea) {
			super();
			
			this.textArea = textArea;
			initUI();
		}
		
		private void initUI() {
			copyItem = new JMenuItem("¸´ÖÆ");
			copyItem.addActionListener(this);
		
			clearItem = new JMenuItem("Çå¿Õ");
			clearItem.addActionListener(this);
			
			add(copyItem);
			add(clearItem);
		}

		@Override
		public void show(Component invoker, int x, int y) {
			
			String selectedText = textArea.getSelectedText();
			if (selectedText == null || selectedText.length() <= 0) {
				copyItem.setEnabled(false);
			} else {
				copyItem.setEnabled(true);
			}
			
			String textAreaText = textArea.getText();
			if (textAreaText == null || textAreaText.length() <= 0) {
				clearItem.setEnabled(false);
			} else {
				clearItem.setEnabled(true);
			} 
			
			super.show(invoker, x, y);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == clearItem) {
				cleanLog();
			} else if (e.getSource() == copyItem) {
				StringSelection stsel = new StringSelection(textArea.getSelectedText());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
			}
		}
	}
	
	/***************************************************************************************/
	private class MouseEventHander extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				textAreaPopupMenu.show(touchEventLogArea, e.getX(), e.getY());
			}
			
			super.mousePressed(e);
		}
	}
}
