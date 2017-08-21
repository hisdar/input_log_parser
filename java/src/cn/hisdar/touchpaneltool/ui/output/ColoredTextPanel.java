package cn.hisdar.touchpaneltool.ui.output;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.gargoylesoftware.htmlunit.javascript.host.Text;

import cn.hisdar.lib.common.Range;

public class ColoredTextPanel extends JTextPane {

	private static final long serialVersionUID = 5739203941397938568L;

	private MutableAttributeSet infoTextAttr = null;

	public ColoredTextPanel() {
		super();
		
		infoTextAttr = new SimpleAttributeSet();
		StyleConstants.setFontSize(infoTextAttr, 14);
		StyleConstants.setFontFamily(infoTextAttr, "ÐÂËÎÌå");
	}
	
	public void setText(String text) {
		super.setText(text);
		setStyle();
	}
	
	public void append(String text) {
		try {
			getDocument().insertString(getDocument().getLength(), text, infoTextAttr);
			setStyle();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void setStyle() {
		DefaultStyledDocument styledDocument = (DefaultStyledDocument)getStyledDocument();
		
		MutableAttributeSet errAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(errAttr, Color.RED);
		
		MutableAttributeSet infoAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(infoAttr, Color.BLACK);
		
		String textString = getText();
		Range nextLineRange = getNextLineRange(textString, 0);
		
		String currentString = textString.substring(nextLineRange.min, nextLineRange.max);
		if (currentString.indexOf("ERROR") >= 0) {
			styledDocument.setParagraphAttributes(nextLineRange.min, nextLineRange.max, errAttr, false);
		} else {
			styledDocument.setParagraphAttributes(nextLineRange.min, nextLineRange.max, infoAttr, false);
		}
		
		int lineCount = 1;
		while (nextLineRange.max < textString.length()) {
			currentString = textString.substring(nextLineRange.min, nextLineRange.max);
			if (currentString.indexOf("ERROR") >= 0) {
				styledDocument.setParagraphAttributes(nextLineRange.min, nextLineRange.max, errAttr, false);
			} else {
				styledDocument.setParagraphAttributes(nextLineRange.min, nextLineRange.max, infoAttr, false);
			}
			
			nextLineRange = getNextLineRange(textString, lineCount);
			lineCount += 1;
		}
	}
	
	public Range getNextLineRange(String text, int startLine) {
		Range range = new Range(0, text.length());
		
		int lineIndex = text.indexOf('\n');

		int currentLineCount = 0;
		while (lineIndex >= 0) {
			if (currentLineCount == startLine) {
				range.max = lineIndex;
				break;
			} else {
				range.min = lineIndex;
			}
			
			currentLineCount += 1;
			lineIndex = text.indexOf('\n', lineIndex + 1);
		}
		
		return range;
	}
}
