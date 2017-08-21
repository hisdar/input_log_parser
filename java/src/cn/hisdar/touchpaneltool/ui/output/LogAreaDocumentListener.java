package cn.hisdar.touchpaneltool.ui.output;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import cn.hisdar.lib.log.HLog;

public class LogAreaDocumentListener implements DocumentListener {

	private JTextArea textArea = null;
	private int maxLineCount = 0;
	
	public LogAreaDocumentListener(JTextArea textArea) {
		this.textArea = textArea;
	}
	
	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	public int getMaxLineCount() {
		return maxLineCount;
	}

	public void setMaxLineCount(int maxLineCount) {
		this.maxLineCount = maxLineCount;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (textArea == null || maxLineCount <= 0) {
			return ;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int currentLineCount = textArea.getLineCount(); 
				if (currentLineCount > maxLineCount) {
					int end = 0;
					try {
						end = textArea.getLineEndOffset(currentLineCount - maxLineCount);
					} catch (Exception e) {
						HLog.el(e);
					}
				
					textArea.replaceRange("", 0, end);
				}
				
				//textArea.setCaretPosition(textArea.getText().length());
			}
		});
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

}
