package cn.hisdar.touchpaneltool.ui.replay2;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

public class HImageLabel extends JLabel {

	private BufferedImage image = null;
	
	public HImageLabel() {
		// TODO Auto-generated constructor stub
	}

	public void setDrawImage(BufferedImage image) {
		this.image = image;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if (null != image) {
			double rateWidth = 1.0 * image.getWidth() / getWidth();
			double rateHeight = 1.0 * image.getHeight() / getHeight();
			
			double drawRate = rateWidth > rateHeight ? rateWidth : rateHeight;
			int drawWidth = (int)(image.getWidth() / drawRate);
			int drawHeight = (int)(image.getHeight() / drawRate);
			
			int startX = drawWidth < getWidth() ? (getWidth() - drawWidth) / 2 : 0;
			int startY = drawHeight < getHeight() ? (getHeight() - drawHeight) / 2 : 0;
			
			g.drawImage(image, startX, startY, drawWidth, drawHeight, null);
		}
	}
	
	
}
