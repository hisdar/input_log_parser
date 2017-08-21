package cn.hisdar.touchpaneltool.setting.common;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PathSetPanel extends JPanel implements ActionListener {

	private JLabel nameLabel;
	private JLabel descriptionLabel;
	private JTextField valueField;
	private JButton choicePathButton;
	
	public PathSetPanel(String name, String value, String descrition) {
		setLayout(new GridLayout(2, 1, 3, 3));
		setBorder(BorderFactory.createTitledBorder(" "));
		
		JPanel workspacePanel = new JPanel(new BorderLayout());
		nameLabel = new JLabel(name);
		valueField = new JTextField(value);
		choicePathButton = new JButton("Ñ¡Ôñ");
		choicePathButton.addActionListener(this);
		workspacePanel.add(nameLabel, BorderLayout.WEST);
		workspacePanel.add(valueField, BorderLayout.CENTER);
		workspacePanel.add(choicePathButton, BorderLayout.EAST);
		add(workspacePanel);

		descriptionLabel = new JLabel(descrition);
		descriptionLabel.setEnabled(false);
		add(descriptionLabel);
	}

	public void setPath(String path) {
		valueField.setText(path);
	}
	
	public String getPath() {
		return valueField.getText().trim();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == choicePathButton) {
			JFileChooser fileChooser = new JFileChooser(valueField.getText().trim());
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int retValue = fileChooser.showSaveDialog(null);
			if (retValue != JFileChooser.APPROVE_OPTION) {
				return;
			}
			
			valueField.setText(fileChooser.getSelectedFile().getPath());
		}
	}
}
