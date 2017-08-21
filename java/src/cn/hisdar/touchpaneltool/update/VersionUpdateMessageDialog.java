package cn.hisdar.touchpaneltool.update;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import cn.hisdar.lib.ui.HLinearPanel;
import cn.hisdar.lib.ui.HVerticalLineLabel;
import cn.hisdar.radishlib.ui.UIAdapter;

public class VersionUpdateMessageDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7346369325272645052L;
	private static final int BASIC_DIALOG_HEIGHT = 230;
			
	public enum ActionType {
		IGNORE_CURRENT_UPDATE,
		UPDATE,
		CANCLE,
	}
	
	private JLabel versionLabel;
	private JLabel versionDescriptionLabel;
	private JLabel updateDescroptionLabel;
	private JLabel updateLevelLabel;
	
	private Font titleFont = new Font("微软雅黑", Font.BOLD, 14);
	private Font textFount = new Font("微软雅黑", Font.PLAIN, 14);
	
	private JButton notNotifyButton;
	private JButton updateButton;
	private JButton cancleButton;
	
	private ActionType actionType;
	private Version version;
	
	private int dialogHeight = BASIC_DIALOG_HEIGHT;
	
	public VersionUpdateMessageDialog(Version version) {
		this.version = version;
		actionType = ActionType.CANCLE;
		
		initDialog(version);
		setModal(true);
		setTitle("版本更新提示");
		setSize(600, dialogHeight);
		setLocation(UIAdapter.getCenterLocation(null, this));
	}
	
	private void initDialog(Version version) {
		if (version == null) {
			return;
		}
		
		versionLabel = new JLabel(version.getVersion());
		versionDescriptionLabel = new JLabel(version.getVersionDescription());
		
		JPanel versionPanel = new JPanel(new BorderLayout());
		JLabel versionNameLabel = new JLabel("   版本号：");
		versionNameLabel.setFont(titleFont);
		versionLabel.setFont(textFount);
		versionPanel.add(versionNameLabel, BorderLayout.WEST);
		versionPanel.add(versionLabel);
		
		JPanel versionDescriptionPanel = new JPanel(new BorderLayout());
		versionDescriptionPanel.add(versionDescriptionLabel);
		
		JPanel updateLevelPanel = getUpdateLevelPanel(version.getUpdateLevel());
		JPanel updateDescriptionPanel = getUpdateDescriptionPanel(version.getUpdateDescription());
		updateDescriptionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		
		HLinearPanel mainPanel = new HLinearPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 0));
		mainPanel.add(versionDescriptionPanel);
		mainPanel.add(versionPanel);
		mainPanel.add(updateLevelPanel);
		mainPanel.add(updateDescriptionPanel);
		
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		
		JPanel controlPanel = getContronPanel();
		add(controlPanel, BorderLayout.SOUTH);
		
		initVersionDescriptionLabel();
		versionLabel.setPreferredSize(versionDescriptionLabel.getSize());
	}
	
	private JPanel getUpdateLevelPanel(int updateLevel) {
		JPanel updateLevelPanel = new JPanel(new BorderLayout());
		JLabel updateLevelNameLabel = new JLabel("升级建议：");
		updateLevelNameLabel.setFont(titleFont);
		updateLevelPanel.add(updateLevelNameLabel, BorderLayout.WEST);
		
		updateLevelLabel = new JLabel(updateLevel + "");
		updateLevelLabel.setFont(textFount);
		updateLevelPanel.add(updateLevelLabel);
		
		switch (updateLevel) {
		case Version.UPDATE_LEVEL_MESSAGE:
			updateLevelLabel.setForeground(Color.GREEN);
			updateLevelLabel.setText("提示升级");
			break;
		case Version.UPDATE_LEVEL_REMINDER:
			updateLevelLabel.setForeground(Color.BLUE);
			updateLevelLabel.setText("建议升级");
			break;
		case Version.UPDATE_LEVEL_IMPORTANT:
			updateLevelLabel.setForeground(Color.RED);
			updateLevelLabel.setText("重要升级");
			break;
		default:
			break;
		}
		
		return updateLevelPanel;
	}
	
	private JPanel getContronPanel() {
		
		notNotifyButton = new JButton("忽略本次升级");
		updateButton = new JButton("升级");
		cancleButton = new JButton("暂不升级");
		
		notNotifyButton.addActionListener(this);
		updateButton.addActionListener(this);
		cancleButton.addActionListener(this);
		
		Border buttonBorder = BorderFactory.createEmptyBorder(10, 5, 10, 5);
		notNotifyButton.setBorder(buttonBorder);
		updateButton.setBorder(buttonBorder);
		cancleButton.setBorder(buttonBorder);
		
		JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
		buttonPanel.add(updateButton);
		buttonPanel.add(notNotifyButton);
		buttonPanel.add(cancleButton);
		
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 200, 20, 30));
		
		JPanel controlPanel = new JPanel(new BorderLayout());
		HVerticalLineLabel splitLine = new HVerticalLineLabel();
		splitLine.setBorder(0, 5, 0, 5);
		controlPanel.add(splitLine, BorderLayout.NORTH);
		controlPanel.add(buttonPanel, BorderLayout.CENTER);
		
		return controlPanel;
	}
	
	private void initVersionDescriptionLabel() {
		versionDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		Font font = new Font("微软雅黑", Font.BOLD, 20);
		versionDescriptionLabel.setFont(font);
	}
	
	private JPanel getUpdateDescriptionPanel(String updateDescription) {
		JPanel updateDescriptionPanel = new JPanel(new BorderLayout());
		JLabel updateDescriptionNameLabel = new JLabel("更新描述：");
		
		updateDescroptionLabel = new JLabel();
		updateDescriptionNameLabel.setFont(titleFont);
		updateDescriptionPanel.add(updateDescriptionNameLabel, BorderLayout.NORTH);
		updateDescriptionPanel.add(updateDescroptionLabel);
		setUpdateDescriptionText(updateDescription);
		
		return updateDescriptionPanel;
	}
	
	private void setUpdateDescriptionText(String updateDescription) {
		String updateDescriptionShow = updateDescription.trim();
		
		updateDescriptionShow = updateDescriptionShow.replaceAll("\n", "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		updateDescriptionShow = "<HTML>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + updateDescriptionShow + "</HTML>";
		
		updateDescroptionLabel.setFont(textFount);
		updateDescroptionLabel.setHorizontalAlignment(SwingConstants.LEFT);
		updateDescroptionLabel.setVerticalAlignment(SwingConstants.TOP);
		updateDescroptionLabel.setText(updateDescriptionShow);
		updateDescroptionLabel.setBorder(null);
	}

	@Override
	public void paint(Graphics g) {
		
		String updateDescriptionShow = updateDescroptionLabel.getText();
		
		int lineCount = 1;
		
		int startIndex = updateDescriptionShow.indexOf("<br/>");
		while(startIndex >= 0) {
			lineCount +=1;
			startIndex += 1;
			startIndex = updateDescriptionShow.indexOf("<br/>", startIndex);
		}
		
		FontMetrics fontMetrics = g.getFontMetrics(textFount);
		int updateDescriptionHeight = fontMetrics.getHeight() * lineCount;
		
		dialogHeight = updateDescriptionHeight + BASIC_DIALOG_HEIGHT;
		
		if (getHeight() != dialogHeight) {
			setSize(getWidth(), dialogHeight);
			setLocation(UIAdapter.getCenterLocation(null, this));
		}
		
		super.paint(g);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == notNotifyButton) {
			actionType = ActionType.IGNORE_CURRENT_UPDATE;
		} else if (e.getSource() == updateButton) {
			System.err.println("Going to update");
			actionType = ActionType.UPDATE;
		} else if (e.getSource() == cancleButton) {
			actionType = ActionType.CANCLE;
		}
		
		setVisible(false);
	}
	
	public ActionType getActionType() {
		return actionType;
	}
}
