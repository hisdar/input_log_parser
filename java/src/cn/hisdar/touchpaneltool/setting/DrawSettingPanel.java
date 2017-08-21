package cn.hisdar.touchpaneltool.setting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.radishlib.StringAdapter;
import cn.hisdar.radishlib.ui.LinerPanel;

public class DrawSettingPanel extends JPanel implements ControlActionListener, SettingPanelInterface {
	private static final int TEXT_FIELD_VALUE_SIZE = 6;
	
	private static final String DRAW_PANEL_SETTIONG_TITLE = "正常界面参数";
	private static final String PREVIEW_PANEL_SETTIONG_TITLE = "缩略图界面参数";
	private static final String DRAW_CROSS_LINE_CHECK_BOX_TEXT = "触摸的时候绘制十字线";
	
	public static final String IS_DRAWC_CROSS_LINE_CONFIG_NAME = "isDrawCrossLine";

	public static final String BASE_TRACK_LINE_COLOR_CONFIG_NAME = "baseTrackLineColor";
	public static final String BASE_TOUCH_POINT_COLOR_CONFIG_NAME = "baseTouchPointColor";
	public static final String BASE_LAST_POINT_COLOR_CONFIG_NAME = "baseLastPointColor";
	public static final String BASE_CROSS_LINE_COLOR_CONFIG_NAME = "baseCrossLineColor";
	public static final String BASE_IMAGE_BACKGROUD_COLOR_CONFIG_NAME = "baseImageBackgroudColor";
	public static final String BASE_TRACK_LINE_SIZE_CONFIG_NAME = "baseTrackLineSize";
	public static final String BASE_TOUCH_POINT_SIZE_CONFIG_NAME = "baseTouchPointSize";
	public static final String BASE_LAST_POINT_SIZE_CONFIG_NAME = "baseLastPointSize";

	public static final String PREVIEW_TRACK_LINE_COLOR_CONFIG_NAME = "previewTrackLineColor";
	public static final String PREVIEW_TOUCH_POINT_COLOR_CONFIG_NAME = "previewTouchPointColor";
	public static final String PREVIEW_LAST_POINT_COLOR_CONFIG_NAME = "previewLastPointColor";
	public static final String PREVIEW_CROSS_LINE_COLOR_CONFIG_NAME = "previewCrossLineColor";
	public static final String PREVIEW_IMAGE_BACKGROUD_COLOR_CONFIG_NAME = "previewImageBackgroudColor";
	public static final String PREVIEW_TRACK_LINE_SIZE_CONFIG_NAME = "previewTrackLineSize";
	public static final String PREVIEW_TOUCH_POINT_SIZE_CONFIG_NAME = "previewTouchPointSize";
	public static final String PREVIEW_LAST_POINT_SIZE_CONFIG_NAME = "previewLastPointSize";

	public static final int DEFAULT_BASE_TOUCH_POINT_SIZE = 4;
	public static final int DEFAULT_BASE_LAST_POINT_SIZE = 6;
	public static final Color DEFAULT_TRACK_LING_COLOR = Color.BLUE;
	public static final Color DEFAULT_TOUCH_POINT_COLOR = Color.RED;
	public static final Color DEFAULT_LAST_POINT_COLOR = Color.RED;
	public static final Color DEFAULT_CROSS_LINE_COLOR = Color.BLUE;
	public static final Color DEFAULT_IMAGE_BACKGROUD_COLOR = Color.GRAY;
	
	public static final int DEFAULT_PREVIEW_TOUCH_POINT_SIZE = 20;
	public static final int DEFAULT_PREVIEW_LAST_POINT_SIZE = 30;

	public static final String DRAW_CONFIG_FILE_PATH = "./Config/draw_config.xml";
	
	// 轨迹设置，设置绘图轨迹的相关参数
	
	private JLabel drawPanelSettingTitleLabel = null;
	private JLabel previewPanelSettingTitleLabel = null;
	private ColorSettingPanel drawPanelColorSettingPanel = null;
	private ColorSettingPanel previewPanelColorSettingPanel = null;

	private JCheckBox drawCrossLineCheckBox = null;
	
	private ControlPanel controlPanel;
	
	private HConfig settingConfig = null;
	
	public DrawSettingPanel() {
		
		settingConfig = HConfig.getInstance(DRAW_CONFIG_FILE_PATH, true);
		
		drawPanelSettingTitleLabel = new JLabel(DRAW_PANEL_SETTIONG_TITLE);
		previewPanelSettingTitleLabel = new JLabel(PREVIEW_PANEL_SETTIONG_TITLE);

		drawPanelSettingTitleLabel.setHorizontalAlignment(JLabel.CENTER);
		previewPanelSettingTitleLabel.setHorizontalAlignment(JLabel.CENTER);

		JPanel titlePanel = new JPanel(new GridLayout(1, 2, 2, 2));
		titlePanel.add(drawPanelSettingTitleLabel);
		titlePanel.add(previewPanelSettingTitleLabel);

		drawPanelColorSettingPanel = new ColorSettingPanel();
		previewPanelColorSettingPanel = new ColorSettingPanel();

		JPanel colorPanel = new JPanel(new GridLayout(1, 2, 2, 2));
		colorPanel.add(drawPanelColorSettingPanel);
		colorPanel.add(previewPanelColorSettingPanel);

		drawCrossLineCheckBox = new JCheckBox(DRAW_CROSS_LINE_CHECK_BOX_TEXT);
		// System.err.println("Draw cross line:" +
		// settingPanelConfig.isDrawCrossLine());
		if (settingConfig != null) {
			drawCrossLineCheckBox.setSelected(settingConfig.getConfigValue(
					IS_DRAWC_CROSS_LINE_CONFIG_NAME, true));
		} else {
			drawCrossLineCheckBox.setSelected(true);
		}
		
		JPanel drawCrossLineCheckBoxPanel = new JPanel(new BorderLayout());
		drawCrossLineCheckBoxPanel
				.add(drawCrossLineCheckBox, BorderLayout.WEST);

		LinerPanel drawPanelView = new LinerPanel();
		drawPanelView.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		drawPanelView.addPanel(titlePanel);
		drawPanelView.addPanel(colorPanel);
		drawPanelView.addPanel(drawCrossLineCheckBoxPanel);
		
		setLayout(new BorderLayout());
		add(drawPanelView, BorderLayout.CENTER);
		
		controlPanel = new ControlPanel();
		controlPanel.addControlActionListeners(this);
		JPanel controlPanelView = new JPanel(new BorderLayout());
		controlPanelView.add(controlPanel, BorderLayout.EAST);
		add(controlPanelView, BorderLayout.SOUTH);
		
		setColor();
		setLineSize();
		
		if (!new File(DRAW_CONFIG_FILE_PATH).exists()) {
			resetEvent();
		}
	}
	
	private class ColorSettingPanel extends LinerPanel {

		public static final String COLOR_LABEL_TEXT = "       ";
		public static final String TRACK_LINE_COLOR_TEXT = "轨迹线颜色：";
		public static final String TOUCH_POINT_COLOR_TEXT = "触摸点颜色：";
		public static final String LAST_POINT_COLOR_TEXT = "抬起点颜色：";
		public static final String CROSS_LINE_COLOR_TEXT = "十字线颜色：";
		public static final String BACKGROUD_COLOR_TEXT = "轨迹背景色：";
		public static final String TRACK_LINE_SIZE_TEXT = "轨迹线粗细：";
		public static final String TOUCH_POINT_SIZE_TEXT = "触摸点大小：";
		public static final String LAST_POINT_SIZE_TEXT = "抬起点大小：";

		private static final int COLOR_ITEM_SIZE = 5;
		private static final int LINE_SIZE_SIZE = 3;

		private JLabel trackLineColorLabel = new JLabel(TRACK_LINE_COLOR_TEXT);
		private JLabel trackLineColorValueLabel = new JLabel(COLOR_LABEL_TEXT);

		private JLabel touchPointColorLabel = new JLabel(TOUCH_POINT_COLOR_TEXT);
		private JLabel touchPointColorValueLabel = new JLabel(COLOR_LABEL_TEXT);

		private JLabel lastPointColorLabel = new JLabel(LAST_POINT_COLOR_TEXT);
		private JLabel lastPointColorValueLabel = new JLabel(COLOR_LABEL_TEXT);

		private JLabel crossLineColorLabel = new JLabel(CROSS_LINE_COLOR_TEXT);
		private JLabel crossLineColorValueLabel = new JLabel(COLOR_LABEL_TEXT);

		private JLabel imageBackgroudColorLabel = new JLabel(
				BACKGROUD_COLOR_TEXT);
		private JLabel imageBackgroudColorValueLabel = new JLabel(
				COLOR_LABEL_TEXT);

		private JLabel trackLineSizeLabel = new JLabel(TRACK_LINE_SIZE_TEXT);
		private JTextField trackLineSizeField = new JTextField();

		private JLabel touchPointSizeLabel = new JLabel(TOUCH_POINT_SIZE_TEXT);
		private JTextField touchPointSizeField = new JTextField();

		private JLabel lastPointSizeLabel = new JLabel(LAST_POINT_SIZE_TEXT);
		private JTextField lastPointSizeField = new JTextField();

		private MouseEventHandler mouseEventHandler = null;

		public ColorSettingPanel() {
			mouseEventHandler = new MouseEventHandler();

			initPanel();
			setBorder(BorderFactory.createTitledBorder(""));
		}
		
		public void initColorLabelValue() {
			trackLineColorValueLabel.setBackground(DEFAULT_TRACK_LING_COLOR);
			lastPointColorValueLabel.setBackground(DEFAULT_LAST_POINT_COLOR);
			crossLineColorValueLabel.setBackground(DEFAULT_CROSS_LINE_COLOR);
			touchPointColorValueLabel.setBackground(DEFAULT_TOUCH_POINT_COLOR);
			imageBackgroudColorValueLabel.setBackground(DEFAULT_IMAGE_BACKGROUD_COLOR);
		}
		
		public void initLineSize(int trackSize, int touchSize, int lastTouchSize) {
			trackLineSizeField.setText(trackSize + "");
			touchPointSizeField.setText(touchSize + "");
			lastPointSizeField.setText(lastTouchSize + "");
		}
		
		private void initPanel() {

			trackLineColorValueLabel.setOpaque(true);
			lastPointColorValueLabel.setOpaque(true);
			crossLineColorValueLabel.setOpaque(true);
			touchPointColorValueLabel.setOpaque(true);
			imageBackgroudColorValueLabel.setOpaque(true);

			initColorLabelValue();

			trackLineColorValueLabel.addMouseListener(mouseEventHandler);
			lastPointColorValueLabel.addMouseListener(mouseEventHandler);
			crossLineColorValueLabel.addMouseListener(mouseEventHandler);
			touchPointColorValueLabel.addMouseListener(mouseEventHandler);
			imageBackgroudColorValueLabel.addMouseListener(mouseEventHandler);

			JPanel colorPanel = new JPanel(new GridLayout(COLOR_ITEM_SIZE, 1, 6, 6));

			JPanel trackLineColorPanel = new JPanel(new BorderLayout());
			trackLineColorPanel.add(trackLineColorLabel, BorderLayout.WEST);
			trackLineColorPanel
					.add(trackLineColorValueLabel, BorderLayout.EAST);
			colorPanel.add(trackLineColorPanel);

			JPanel touchPointColorPanel = new JPanel(new BorderLayout());
			touchPointColorPanel.add(touchPointColorLabel, BorderLayout.WEST);
			touchPointColorPanel.add(touchPointColorValueLabel,
					BorderLayout.EAST);
			colorPanel.add(touchPointColorPanel);

			JPanel lastPointColorPanel = new JPanel(new BorderLayout());
			lastPointColorPanel.add(lastPointColorLabel, BorderLayout.WEST);
			lastPointColorPanel
					.add(lastPointColorValueLabel, BorderLayout.EAST);
			colorPanel.add(lastPointColorPanel);

			JPanel crossLineColorPanel = new JPanel(new BorderLayout());
			crossLineColorPanel.add(crossLineColorLabel, BorderLayout.WEST);
			crossLineColorPanel
					.add(crossLineColorValueLabel, BorderLayout.EAST);
			colorPanel.add(crossLineColorPanel);

			JPanel backgroudColorPanel = new JPanel(new BorderLayout());
			backgroudColorPanel
					.add(imageBackgroudColorLabel, BorderLayout.WEST);
			backgroudColorPanel.add(imageBackgroudColorValueLabel,
					BorderLayout.EAST);
			colorPanel.add(backgroudColorPanel);

			JPanel colorPanelBuffer = new JPanel(new BorderLayout());
			colorPanelBuffer.add(colorPanel, BorderLayout.WEST);
			colorPanelBuffer.setBorder(BorderFactory.createTitledBorder(""));

			JPanel sizePanel = new JPanel(new GridLayout(LINE_SIZE_SIZE, 1, 2,
					2));

			trackLineSizeField.setColumns(TEXT_FIELD_VALUE_SIZE);
			touchPointSizeField.setColumns(TEXT_FIELD_VALUE_SIZE);
			touchPointSizeField.setColumns(TEXT_FIELD_VALUE_SIZE);

			JPanel trackLineSizePanel = new JPanel(new BorderLayout());
			trackLineSizePanel.add(trackLineSizeLabel, BorderLayout.WEST);
			trackLineSizePanel.add(trackLineSizeField, BorderLayout.CENTER);
			// sizePanel.add(trackLineSizePanel);

			JPanel touchPointSizePanel = new JPanel(new BorderLayout());
			touchPointSizePanel.add(touchPointSizeLabel, BorderLayout.WEST);
			touchPointSizePanel.add(touchPointSizeField, BorderLayout.CENTER);
			sizePanel.add(touchPointSizePanel);

			JPanel lastPointSizePanel = new JPanel(new BorderLayout());
			lastPointSizePanel.add(lastPointSizeLabel, BorderLayout.WEST);
			lastPointSizePanel.add(lastPointSizeField, BorderLayout.CENTER);
			sizePanel.add(lastPointSizePanel);

			JPanel sizePanelBuffer = new JPanel(new BorderLayout());
			sizePanelBuffer.add(sizePanel, BorderLayout.WEST);
			sizePanelBuffer.setBorder(BorderFactory.createTitledBorder(""));

			addPanel(colorPanelBuffer);
			addPanel(sizePanelBuffer);
		}

		public Color getTrackLineColor() {
			return trackLineColorValueLabel.getBackground();
		}

		public void setTrackLineColor(Color color) {
			this.trackLineColorValueLabel.setBackground(color);
		}

		public Color getTouchPointColor() {
			return touchPointColorValueLabel.getBackground();
		}

		public void setTouchPointColor(Color color) {
			this.touchPointColorValueLabel.setBackground(color);
		}

		public Color getLastPointColor() {
			return lastPointColorValueLabel.getBackground();
		}

		public void setLastPointColor(Color color) {
			this.lastPointColorValueLabel.setBackground(color);
		}

		public Color getCrossLineColor() {
			return crossLineColorValueLabel.getBackground();
		}

		public void setCrossLineColor(Color color) {
			this.crossLineColorValueLabel.setBackground(color);
		}

		public Color getImageBackgroudColor() {
			return imageBackgroudColorValueLabel.getBackground();
		}

		public void setImageBackgroudColor(Color color) {
			this.imageBackgroudColorValueLabel.setBackground(color);
		}

		public String getTrackLineSize() {
			return trackLineSizeField.getText().trim();
		}

		public void setTrackLineSize(String size) {
			this.trackLineSizeField.setText(size);
		}

		public String getTouchPointSize() {
			return touchPointSizeField.getText().trim();
		}

		public void setTouchPointSize(String size) {
			this.touchPointSizeField.setText(size);
		}

		public String getLastPointSize() {
			return lastPointSizeField.getText().trim();
		}

		public void setLastPointSize(String size) {
			this.lastPointSizeField.setText(size);
		}

		private class MouseEventHandler extends MouseAdapter {

			private static final String COLOR_SELECTE_TITLE = "请选择颜色";

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getSource() == trackLineColorValueLabel) {
					Color initialColor = new Color(0);
					Color selectedColor = JColorChooser.showDialog(null,
							COLOR_SELECTE_TITLE, initialColor);
					trackLineColorValueLabel.setBackground(selectedColor);
				} else if (e.getSource() == touchPointColorValueLabel) {
					Color initialColor = new Color(0);
					Color selectedColor = JColorChooser.showDialog(null,
							COLOR_SELECTE_TITLE, initialColor);
					touchPointColorValueLabel.setBackground(selectedColor);
				} else if (e.getSource() == lastPointColorValueLabel) {
					Color initialColor = new Color(0);
					Color selectedColor = JColorChooser.showDialog(null,
							COLOR_SELECTE_TITLE, initialColor);
					lastPointColorValueLabel.setBackground(selectedColor);
				} else if (e.getSource() == crossLineColorValueLabel) {
					Color initialColor = new Color(0);
					Color selectedColor = JColorChooser.showDialog(null,
							COLOR_SELECTE_TITLE, initialColor);
					crossLineColorValueLabel.setBackground(selectedColor);
				} else if (e.getSource() == imageBackgroudColorValueLabel) {
					Color initialColor = new Color(0);
					Color selectedColor = JColorChooser.showDialog(null,
							COLOR_SELECTE_TITLE, initialColor);
					imageBackgroudColorValueLabel.setBackground(selectedColor);
				}
			}
		}
	}
	
	private void saveColor() {
		settingConfig.setConfigItem(new ConfigItem(
				BASE_TRACK_LINE_COLOR_CONFIG_NAME, drawPanelColorSettingPanel
						.getTrackLineColor().getRGB()));
		settingConfig.setConfigItem(new ConfigItem(
				BASE_TOUCH_POINT_COLOR_CONFIG_NAME, drawPanelColorSettingPanel
						.getTouchPointColor().getRGB()));
		settingConfig.setConfigItem(new ConfigItem(
				BASE_LAST_POINT_COLOR_CONFIG_NAME, drawPanelColorSettingPanel
						.getLastPointColor().getRGB()));
		settingConfig.setConfigItem(new ConfigItem(
				BASE_CROSS_LINE_COLOR_CONFIG_NAME, drawPanelColorSettingPanel
						.getCrossLineColor().getRGB()));
		settingConfig.setConfigItem(new ConfigItem(
				BASE_IMAGE_BACKGROUD_COLOR_CONFIG_NAME,
				drawPanelColorSettingPanel.getImageBackgroudColor().getRGB()));

		settingConfig.setConfigItem(new ConfigItem(
				PREVIEW_TRACK_LINE_COLOR_CONFIG_NAME,
				previewPanelColorSettingPanel.getTrackLineColor().getRGB()));
		settingConfig.setConfigItem(new ConfigItem(
				PREVIEW_TOUCH_POINT_COLOR_CONFIG_NAME,
				previewPanelColorSettingPanel.getTouchPointColor().getRGB()));
		settingConfig.setConfigItem(new ConfigItem(
				PREVIEW_TOUCH_POINT_COLOR_CONFIG_NAME,
				previewPanelColorSettingPanel.getLastPointColor().getRGB()));
		settingConfig.setConfigItem(new ConfigItem(
				PREVIEW_CROSS_LINE_COLOR_CONFIG_NAME,
				previewPanelColorSettingPanel.getCrossLineColor().getRGB()));
		settingConfig
				.setConfigItem(new ConfigItem(
						PREVIEW_IMAGE_BACKGROUD_COLOR_CONFIG_NAME,
						previewPanelColorSettingPanel.getImageBackgroudColor()
								.getRGB()));
	}

	private void setColor() {
		
		if (settingConfig == null) {
			settingConfig = HConfig.getInstance(DRAW_CONFIG_FILE_PATH, true);
		}
		
		drawPanelColorSettingPanel.setTrackLineColor(new Color(settingConfig
				.getConfigValue(BASE_TRACK_LINE_COLOR_CONFIG_NAME,
						DEFAULT_TRACK_LING_COLOR.getRGB())));
		drawPanelColorSettingPanel.setTouchPointColor(new Color(settingConfig
				.getConfigValue(BASE_TOUCH_POINT_COLOR_CONFIG_NAME,
						DEFAULT_TOUCH_POINT_COLOR.getRGB())));
		drawPanelColorSettingPanel.setLastPointColor(new Color(settingConfig
				.getConfigValue(BASE_LAST_POINT_COLOR_CONFIG_NAME,
						DEFAULT_LAST_POINT_COLOR.getRGB())));
		drawPanelColorSettingPanel.setCrossLineColor(new Color(settingConfig
				.getConfigValue(BASE_CROSS_LINE_COLOR_CONFIG_NAME,
						DEFAULT_CROSS_LINE_COLOR.getRGB())));
		drawPanelColorSettingPanel.setImageBackgroudColor(new Color(
				settingConfig.getConfigValue(
						BASE_IMAGE_BACKGROUD_COLOR_CONFIG_NAME,
						DEFAULT_IMAGE_BACKGROUD_COLOR.getRGB())));

		previewPanelColorSettingPanel.setTrackLineColor(new Color(settingConfig
				.getConfigValue(PREVIEW_TRACK_LINE_COLOR_CONFIG_NAME,
						DEFAULT_TRACK_LING_COLOR.getRGB())));
		previewPanelColorSettingPanel.setTouchPointColor(new Color(
				settingConfig.getConfigValue(
						PREVIEW_TOUCH_POINT_COLOR_CONFIG_NAME,
						DEFAULT_TOUCH_POINT_COLOR.getRGB())));
		previewPanelColorSettingPanel.setLastPointColor(new Color(settingConfig
				.getConfigValue(PREVIEW_LAST_POINT_COLOR_CONFIG_NAME,
						DEFAULT_LAST_POINT_COLOR.getRGB())));
		previewPanelColorSettingPanel.setCrossLineColor(new Color(settingConfig
				.getConfigValue(PREVIEW_CROSS_LINE_COLOR_CONFIG_NAME,
						DEFAULT_CROSS_LINE_COLOR.getRGB())));
		previewPanelColorSettingPanel.setImageBackgroudColor(new Color(
				settingConfig.getConfigValue(
						PREVIEW_IMAGE_BACKGROUD_COLOR_CONFIG_NAME,
						DEFAULT_IMAGE_BACKGROUD_COLOR.getRGB())));

	}

	private void saveLineSize() {
		if (!StringAdapter.isNumbers(drawPanelColorSettingPanel
				.getTrackLineSize())) {
			JOptionPane.showMessageDialog(null,
					ColorSettingPanel.TRACK_LINE_SIZE_TEXT + "只能输入数字", "提示",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!StringAdapter.isNumbers(drawPanelColorSettingPanel
				.getTouchPointSize())) {
			JOptionPane.showMessageDialog(null,
					ColorSettingPanel.TOUCH_POINT_SIZE_TEXT + "只能输入数字", "提示",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!StringAdapter.isNumbers(drawPanelColorSettingPanel
				.getLastPointSize())) {
			JOptionPane.showMessageDialog(null,
					ColorSettingPanel.LAST_POINT_SIZE_TEXT + "只能输入数字", "提示",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!StringAdapter.isNumbers(previewPanelColorSettingPanel
				.getTrackLineSize())) {
			JOptionPane.showMessageDialog(null,
					ColorSettingPanel.TRACK_LINE_SIZE_TEXT + "只能输入数字", "提示",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!StringAdapter.isNumbers(previewPanelColorSettingPanel
				.getTouchPointSize())) {
			JOptionPane.showMessageDialog(null,
					ColorSettingPanel.TOUCH_POINT_SIZE_TEXT + "只能输入数字", "提示",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!StringAdapter.isNumbers(previewPanelColorSettingPanel
				.getLastPointSize())) {
			JOptionPane.showMessageDialog(null,
					ColorSettingPanel.LAST_POINT_SIZE_TEXT + "只能输入数字", "提示",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		settingConfig
				.setConfigItem(new ConfigItem(BASE_TRACK_LINE_SIZE_CONFIG_NAME,
						Integer.parseInt(drawPanelColorSettingPanel
								.getTrackLineSize())));
		settingConfig.setConfigItem(new ConfigItem(
				BASE_TOUCH_POINT_SIZE_CONFIG_NAME, Integer
						.parseInt(drawPanelColorSettingPanel
								.getTouchPointSize())));
		settingConfig
				.setConfigItem(new ConfigItem(BASE_LAST_POINT_SIZE_CONFIG_NAME,
						Integer.parseInt(drawPanelColorSettingPanel
								.getLastPointSize())));

		settingConfig.setConfigItem(new ConfigItem(
				PREVIEW_TRACK_LINE_SIZE_CONFIG_NAME, Integer
						.parseInt(previewPanelColorSettingPanel
								.getTrackLineSize())));
		settingConfig.setConfigItem(new ConfigItem(
				PREVIEW_TOUCH_POINT_SIZE_CONFIG_NAME, Integer
						.parseInt(previewPanelColorSettingPanel
								.getTouchPointSize())));
		settingConfig.setConfigItem(new ConfigItem(
				PREVIEW_LAST_POINT_SIZE_CONFIG_NAME, Integer
						.parseInt(previewPanelColorSettingPanel
								.getLastPointSize())));

	}
	
	private void saveDrawCrossLine() {
		settingConfig.setConfigItem(new ConfigItem(IS_DRAWC_CROSS_LINE_CONFIG_NAME, drawCrossLineCheckBox.isSelected()));
	}

	private void setLineSize() {
		drawPanelColorSettingPanel.setTrackLineSize(settingConfig
				.getConfigValue(BASE_TRACK_LINE_SIZE_CONFIG_NAME, 1) + "");
		drawPanelColorSettingPanel.setTouchPointSize(settingConfig
				.getConfigValue(BASE_TOUCH_POINT_SIZE_CONFIG_NAME,
						DEFAULT_BASE_TOUCH_POINT_SIZE)
				+ "");
		drawPanelColorSettingPanel.setLastPointSize(settingConfig
				.getConfigValue(BASE_LAST_POINT_SIZE_CONFIG_NAME,
						DEFAULT_BASE_LAST_POINT_SIZE)
				+ "");

		previewPanelColorSettingPanel.setTrackLineSize(settingConfig
				.getConfigValue(PREVIEW_TRACK_LINE_SIZE_CONFIG_NAME, 1) + "");
		previewPanelColorSettingPanel.setTouchPointSize(settingConfig
				.getConfigValue(PREVIEW_TOUCH_POINT_SIZE_CONFIG_NAME,
						DEFAULT_PREVIEW_TOUCH_POINT_SIZE)
				+ "");
		previewPanelColorSettingPanel.setLastPointSize(settingConfig
				.getConfigValue(PREVIEW_LAST_POINT_SIZE_CONFIG_NAME,
						DEFAULT_PREVIEW_LAST_POINT_SIZE)
				+ "");
	}

	@Override
	public void resetEvent() {
		drawPanelColorSettingPanel.initColorLabelValue();
		drawPanelColorSettingPanel.initLineSize(1, DEFAULT_BASE_TOUCH_POINT_SIZE, DEFAULT_BASE_LAST_POINT_SIZE);
		previewPanelColorSettingPanel.initColorLabelValue();
		previewPanelColorSettingPanel.initLineSize(1, DEFAULT_PREVIEW_TOUCH_POINT_SIZE, DEFAULT_PREVIEW_LAST_POINT_SIZE);
		
		saveLineSize();
		saveColor();
		saveDrawCrossLine();
	}

	@Override
	public void submitEvent() {
		saveLineSize();
		saveColor();
		saveDrawCrossLine();
	}

	@Override
	public void finishEvent() {
		saveLineSize();
		saveColor();
		saveDrawCrossLine();
	}
	
	@Override
	public void addControlActionListener(ControlActionListener listener) {
		controlPanel.addControlActionListeners(listener);
	}

	@Override
	public void removeControlActionListener(ControlActionListener listener) {
		controlPanel.removeControlActionListeners(listener);
	}
}
