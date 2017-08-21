package cn.hisdar.touchpaneltool.setting.common;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorSetPanel extends JPanel {
	public static final String COLOR_LABEL_TEXT = "       ";
	private static final String COLOR_SELECTE_TITLE = "«Î—°‘Ò—’…´";
	
	private JLabel nameLabel;
	private JLabel valueLabel;
	private JLabel descriptionLabel;
	
	private MouseEventHandler mouseEventHandler;
	
	public ColorSetPanel(String name, Color color, String description) {
		mouseEventHandler = new MouseEventHandler();
		
		setLayout(new GridLayout(2, 1, 3, 3));
		setBorder(BorderFactory.createTitledBorder(" "));
		
		nameLabel = new JLabel(name);
		valueLabel = new JLabel(COLOR_LABEL_TEXT);
		valueLabel.setBackground(color);
		valueLabel.setOpaque(true);
		valueLabel.addMouseListener(mouseEventHandler);
		
		FlowLayout colorPanelLayout = new FlowLayout();
		colorPanelLayout.setAlignment(FlowLayout.LEFT);
		colorPanelLayout.setHgap(0);
		colorPanelLayout.setVgap(0);
		JPanel colorPanel = new JPanel(colorPanelLayout);
		colorPanel.add(nameLabel);
		colorPanel.add(valueLabel);
		add(colorPanel);

		descriptionLabel = new JLabel(description);
		descriptionLabel.setEnabled(false);
		
		add(descriptionLabel);
	}
	
	public Color getSelectedColor() {
		return valueLabel.getBackground();
	}
	
	public void setSelectedColor(Color color) {
		valueLabel.setBackground(color);
	}
	
	private Color selectColor(Color defaultColor) {
		Color selectedColor = JColorChooser.showDialog(null, COLOR_SELECTE_TITLE, defaultColor);
		
		return selectedColor;
	}
	
	private class MouseEventHandler extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getSource() == valueLabel) {
				Color selectedColor = selectColor(valueLabel.getBackground());
				if (selectedColor != null) {
					valueLabel.setBackground(selectedColor);
				}
			} 
			
			super.mousePressed(e);
		}
	}
}
