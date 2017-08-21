package cn.hisdar.touchpaneltool.ui.output;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import cn.hisdar.lib.log.HLog;

public class TextPanelDocumentListener implements DocumentListener {

	private JTextPane textArea = null;
	private int maxLineCount = 0;
	
	public TextPanelDocumentListener(JTextPane textArea) {
		this.textArea = textArea;
	}
	
	public JTextPane getTextPanel() {
		return textArea;
	}

	public void setTextArea(JTextPane textArea) {
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
				String textAreaString = textArea.getText();
				if (textAreaString == null || textAreaString.length() <= 0) {
					return;
				}
				
				int linerCounter = maxLineCount;
				for (int i = textAreaString.length() - 1; i >= 0; i--) {
					if (textAreaString.charAt(i) == '\n') {
						linerCounter -= 1;
					}
					
					if (linerCounter == 0) {
						try {
							textArea.getDocument().remove(0, i);
							break;
						} catch (BadLocationException e) {
							System.err.println(e);
							break;
						}
					}
				}
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
