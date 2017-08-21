package cn.hisdar.touchpaneltool.update;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.ui.HLinearPanel;
import cn.hisdar.lib.ui.HVerticalLineLabel;
import cn.hisdar.radishlib.ui.UIAdapter;

public class AboutDialog extends JDialog implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4362235593584724859L;

	private HConfig versionConfig;
	
	private JLabel logoLabel;
	private JButton closeButton;
	
	private Font textFont = null;
	private Font titleFont = null;
	
	public AboutDialog() {
		
		versionConfig = HConfig.getInstance("./Config/version.xml", true);
		setSize(600, 500);
		setLocation(UIAdapter.getCenterLocation(null, this));
		initUI();
		setModal(true);
		setResizable(false);
	}
	
	private void initUI() {
		
		textFont = new Font("微软雅黑", Font.PLAIN, 14);
		titleFont = new Font("微软雅黑", Font.BOLD, 14);
		
		setLayout(new BorderLayout());
		logoLabel = new JLabel(new ImageIcon("./Image/VersionDialogLogo.png"));
		add(logoLabel, BorderLayout.NORTH);
		
		// 版本信息
		JPanel versionPanel = createInformationPanel("版 本  号 ：", versionConfig.getConfigValue("version", "NA"));
		
		// 作者信息
		JPanel authorPanel = createInformationPanel("作       者：", versionConfig.getConfigValue("author", "NA"));
		
		// 反馈信息
		JPanel feedbackPanel = createInformationPanel("意见反馈：", versionConfig.getConfigValue("feedBack", "NA"));
		
		// 主页
		JPanel homepagePanel = createInformationPanel("主       页：", versionConfig.getConfigValue("homePage", "NA"));
		
		HVerticalLineLabel copyrightLinear = new HVerticalLineLabel();
		copyrightLinear.setPreferredSize(new Dimension(10, 22));
		copyrightLinear.setBorder(15, 0, 5, 0);
		// 版权信息
		JPanel copyrightPanel = createCopyrightPanel("(C)Copyright", versionConfig.getConfigValue("copyRight", "NA"));
		
		HVerticalLineLabel actionPanelLinear = new HVerticalLineLabel();
		actionPanelLinear.setPreferredSize(new Dimension(10, 47));
		actionPanelLinear.setBorder(40, 0, 5, 0);
		
		// 主面板
		HLinearPanel mainPanel = new HLinearPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 30));
		mainPanel.add(versionPanel);
		mainPanel.add(authorPanel);
		mainPanel.add(feedbackPanel);
		mainPanel.add(homepagePanel);
		
		mainPanel.add(copyrightLinear);
		mainPanel.add(copyrightPanel);
		mainPanel.add(actionPanelLinear);
		
		JPanel actionPanel = new JPanel(new GridLayout(1,  1, 5, 5));
		actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 440, 30, 30));
		closeButton = new JButton("关闭");
		closeButton.setFont(textFont);
		closeButton.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		closeButton.addActionListener(this);
		actionPanel.add(closeButton);
		
		add(mainPanel, BorderLayout.CENTER);
		add(actionPanel, BorderLayout.SOUTH);
	}
	
	private JPanel createCopyrightPanel(String title, String value) {
		JPanel copyrightPanel = new JPanel(new BorderLayout());
		
		String valueShow = value.trim(); 
		valueShow = valueShow.replaceAll("\n", "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		valueShow = "<HTML>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + valueShow + "</HTML>";
		
		JLabel nameLabel = new JLabel(title);
		JLabel valueLabel = new JLabel(valueShow);
		
		nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
		nameLabel.setFont(titleFont);
		valueLabel.setFont(textFont);

		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		copyrightPanel.add(nameLabel, BorderLayout.NORTH);
		copyrightPanel.add(valueLabel, BorderLayout.CENTER);
		
		return copyrightPanel;
	}
	
	private JPanel createInformationPanel(String title, String value) {
		
		JPanel informationPanel = new JPanel(new BorderLayout());
		JLabel nameLabel = new JLabel(title);
		JTextField valueLabel = new JTextField(value);
		valueLabel.setEditable(false);
		valueLabel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		
		nameLabel.setFont(titleFont);
		valueLabel.setFont(textFont);
		
		informationPanel.add(nameLabel, BorderLayout.WEST);
		informationPanel.add(valueLabel, BorderLayout.CENTER);
		
		return informationPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closeButton) {
			setVisible(false);
		}
	}
}
