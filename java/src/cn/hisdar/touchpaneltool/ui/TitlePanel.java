package cn.hisdar.touchpaneltool.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.hisdar.touchpaneltool.setting.EnvironmentSettingChangeListener;
import cn.hisdar.touchpaneltool.setting.EnvironmentSettingPanel;

public class TitlePanel extends JPanel implements EnvironmentSettingChangeListener {
	public final static Color DEFAULT_TITLE_PANEL_COLOR = new Color(0x44587c);
	private JLabel titleLabel = null;
	
	public TitlePanel(String title) {
		
		titleLabel = new JLabel(title);
		
		
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		
		setBackground(DEFAULT_TITLE_PANEL_COLOR);
		setBorder(null);
		((FlowLayout)getLayout()).setHgap(2);
		((FlowLayout)getLayout()).setVgap(2);
		((FlowLayout)getLayout()).setAlignment(FlowLayout.LEFT);
		add(titleLabel);
		EnvironmentSettingPanel.addEnvironmentSettingChangeListener(this);
	}

	@Override
	public void colorChangeEvent(String settingType, Color color) {
		if (settingType.equals(EnvironmentSettingPanel.TITLE_VIEW_COLOR_CONFIG_NAME)) {
			setBackground(color);
		}
	}
}
