package cn.hisdar.touchpaneltool.setting.common;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextFieldSetPanel extends JPanel {
	private JLabel nameLabel;
	private JLabel descriptionLabel;
	private JTextField valueField;
	
	public TextFieldSetPanel(String name, String value, String descrition) {
		setLayout(new GridLayout(2, 1, 3, 3));
		setBorder(BorderFactory.createTitledBorder(" "));
		
		JPanel workspacePanel = new JPanel(new BorderLayout());
		nameLabel = new JLabel(name);
		valueField = new JTextField(value);
		workspacePanel.add(nameLabel, BorderLayout.WEST);
		workspacePanel.add(valueField, BorderLayout.CENTER);
		add(workspacePanel);

		descriptionLabel = new JLabel(descrition);
		descriptionLabel.setEnabled(false);
		add(descriptionLabel);
	}

	public void setTextValue(String path) {
		valueField.setText(path);
	}
	
	public String getTextValue() {
		return valueField.getText().trim();
	}
	
}
