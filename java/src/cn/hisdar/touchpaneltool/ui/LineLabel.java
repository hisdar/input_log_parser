package cn.hisdar.touchpaneltool.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;

public class LineLabel extends JLabel {

	private static final int LINE_LABEL_HEIGHT = 4;
	private static final int LINE_LABEL_WIDTH = 0;

	public LineLabel() {
		setPreferredSize(new Dimension(LINE_LABEL_WIDTH, LINE_LABEL_HEIGHT));
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(Color.GRAY);
		g.drawLine(0, 2, getWidth(), 2);
	}

}
