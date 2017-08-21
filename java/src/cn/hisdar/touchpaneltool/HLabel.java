package cn.hisdar.touchpaneltool;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

import org.omg.CORBA.PUBLIC_MEMBER;

import sun.reflect.generics.tree.Tree;

public class HLabel extends JLabel implements MouseListener {

	private BufferedImage mouseInImage = null;
	private BufferedImage mouseDownImage = null;
	private BufferedImage defauImage = null;
	
	private BufferedImage labelBackgroundImage = null;
	
	private ActionListener listener = null;
	
	private boolean isMouseIn = false;
	private boolean isMousePressed = false;
	
	public HLabel(Icon messageIcon, String messageString) {
		
		setIcon(messageIcon);
		setText(messageString);
		addMouseListener(this);
		setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 18));
		setAlignmentY(TOP_ALIGNMENT);
		//messageLabel.setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 40));
	}

	public void setClickImage(BufferedImage image) {
		mouseDownImage = image;
	}
	
	public void setMouseInImage(BufferedImage image) {
		mouseInImage = image;
	}
	
	public void setDefaultImage(BufferedImage image) {
		defauImage = image;
	}
	
	@Override
	public void paint(Graphics g) {
		
		if (isMouseIn) {
			if (isMousePressed) {
				g.drawImage(mouseDownImage, 0, 0, getWidth(), getHeight(), null);
			} else {
				g.drawImage(mouseInImage, 0, 0, getWidth(), getHeight(), null);
			}
		} else {
			g.drawImage(defauImage, 0, 0, getWidth(), getHeight(), null);
		}
		
		super.paint(g);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		isMousePressed = true;
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isMousePressed = false;
		repaint();
		
		ActionEvent actionEvent = new ActionEvent(this, e.getID(), "Mouse Click Event");
		listener.actionPerformed(actionEvent);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		isMouseIn = true;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		isMouseIn = false;
		repaint();
	}
	
	public void addActionListener(ActionListener listener) {
		this.listener = listener;
	}
}
