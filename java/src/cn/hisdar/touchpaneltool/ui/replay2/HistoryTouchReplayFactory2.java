package cn.hisdar.touchpaneltool.ui.replay2;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;

import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchPoint;
import cn.hisdar.lib.adapter.FileAdapter;
import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.ui.UIAdapter;
import cn.hisdar.touchpaneltool.ui.control.ResolutionChangeListener;
import cn.hisdar.touchpaneltool.ui.control.ResolutionSetPanel;
import cn.hisdar.touchpaneltool.ui.show.OutputClearListener;
import cn.hisdar.touchpaneltool.ui.show.TouchShowScreenInterface;

public class HistoryTouchReplayFactory2 
	implements CollecterDataChangeListener, 
				PopMenuActionListener, 
				ImageSelectedChangeListener, 
				ResolutionChangeListener,
				OutputClearListener,
				HImageArrayPanelActionListener {

	private static final String HISTORY_CONFIG_PATH = "./Config/history_config.xml";
	
	private static final String HISTORY_SAVE_PATH_CONFIG_NAME = "historySavePath";
	public static final String IMAGE_FILE_FORMATE = "png";
	
	private HConfig historyConfig = null;
	private TouchActionCollecter touchActionCollecter = null;
	private HImageArrayPanel hImageArrayPanel = null;
	private MultiTouchActionDetailDialog detailDialog = null;
	private ReplayMessageSetInterface replayMessageSetInterface = null;
	private TouchShowScreenInterface paintTouchInterface = null;
	
	private JMenuItem saveImageItem = null;
	private JMenuItem saveAllItem = null;
	private JMenuItem replayItem = null;
	private JMenuItem clearItem = null;
	private JMenuItem detailsItem = null;
	
	private Dimension currentResolutionRatio = null;
	
	public HistoryTouchReplayFactory2(HImageArrayPanel hImageArrayPanel, 
			ReplayMessageSetInterface messageSetInterface,
			TouchShowScreenInterface paintTouchInterface) {
		
		this.paintTouchInterface = paintTouchInterface;
		this.replayMessageSetInterface = messageSetInterface;
		this.hImageArrayPanel = hImageArrayPanel;
		
		currentResolutionRatio = new Dimension(720, 1280);
		
		historyConfig = HConfig.getInstance(HISTORY_CONFIG_PATH);
		
		replayItem    = new JMenuItem("回放");
		saveImageItem = new JMenuItem("导出");
		saveAllItem   = new JMenuItem("导出全部");
		clearItem     = new JMenuItem("清空");
		detailsItem     = new JMenuItem("详情");
		
		hImageArrayPanel.addJMenuItem(replayItem);
		hImageArrayPanel.addJMenuItem(saveImageItem);
		hImageArrayPanel.addJMenuItem(saveAllItem);
		hImageArrayPanel.addJMenuItem(clearItem);
		hImageArrayPanel.addJMenuItem(detailsItem);
		
		hImageArrayPanel.setPopMenuActionListener(this);
		hImageArrayPanel.addImageSelectedChangeListener(this);
		hImageArrayPanel.addHImageArrayPanelActionListener(this);
		
		touchActionCollecter = new TouchActionCollecter();
		touchActionCollecter.addCollecterDataChangeListener(this);
		
		detailDialog = new MultiTouchActionDetailDialog();
		
		ResolutionSetPanel.addResolutionChangeListener(this);
	}

	@Override
	public void collecterDataChangeEvent(
			TouchActionContainer touchActionContainer) {

		if (hImageArrayPanel != null) {
			hImageArrayPanel.updateImages(touchActionContainer.getMultiTouchAction2s(), currentResolutionRatio);
		}
	}

	@Override
	public void imageSelectedChangeEvent(int index) {
		if (index < 0) {
			return;
		}
		
		String timeMessage = " ";
		MultiTouchAction2 action2 = touchActionCollecter.getTouchActionContainer().getAt(index);
		if (action2 == null) {
			return;
		}
		
		ArrayList<MultiTouchPoint> points = action2.getTouchPoints();
		if (points.size() > 0) {
			timeMessage = "[" + points.get(0).systemTime;
			timeMessage += " -- " + points.get(points.size() - 1).systemTime + "]";
		}

		replayMessageSetInterface.setTimeMessage(timeMessage);
	}

	@Override
	public void menuItemClickedEvent(JMenuItem item, int selectedImageIndex) {
		if (selectedImageIndex < 0) {
			HLog.il("HistoryTouchReplayFactory2.menuItemClickedEventindex(): is -1");
			return;
		}
		
		if (item == detailsItem) {
			MultiTouchAction2 action2 = touchActionCollecter.getTouchActionContainer().getAt(selectedImageIndex);
			detailDialog.setMultiTouchAction2Data(action2);
			detailDialog.setLocation(UIAdapter.getCenterLocation(null, detailDialog));
			detailDialog.setVisible(true);
		} else if (item == saveImageItem) {

			String saveDirector = getSavePath();
			if (saveDirector == null) {
				return;
			}
			
			String saveDirectoryName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
			MultiTouchAction2 action2 = touchActionCollecter.getTouchActionContainer().getAt(selectedImageIndex);
			ArrayList<MultiTouchPoint> points = action2.getTouchPoints();
			if (points.size() > 0) {
				saveDirectoryName = points.get(0).systemTime + " -- " + points.get(points.size() - 1).systemTime;
				saveDirectoryName = saveDirectoryName.replace(':', '-');
			}

			String savePath = FileAdapter.pathCat(saveDirector, saveDirectoryName);
			HLog.il("Save to:" + savePath);
			
			saveTouchAction(action2, savePath, touchActionCollecter.getTouchActionContainer().getBaseConfig());
		} else if (item == saveAllItem) {
			String saveDirector = getSavePath();
			if (saveDirector == null) {
				return;
			}
			
			String saveDirectoryName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
			TouchActionContainer touchActionContainer = touchActionCollecter.getTouchActionContainer();
			for (int i = 0; i < touchActionContainer.getQueueItemCount(); i++) {
				MultiTouchAction2 touchAction2 = touchActionContainer.getAt(i);
				String touchActionSaveDirectory = saveDirectoryName + "_" + i;
				ArrayList<MultiTouchPoint> points = touchAction2.getTouchPoints();
				if (points.size() > 0) {
					touchActionSaveDirectory = points.get(0).systemTime + " -- " + points.get(points.size() - 1).systemTime;
					touchActionSaveDirectory = touchActionSaveDirectory.replace(':', '-');
				}
				
				String touchActionSavePath = FileAdapter.pathCat(saveDirector, saveDirectoryName);
				touchActionSavePath = FileAdapter.pathCat(touchActionSavePath, touchActionSaveDirectory);
				HLog.il("Save to:" + touchActionSavePath);
				saveTouchAction(touchAction2, touchActionSavePath, touchActionContainer.getBaseConfig());
			}
			
		} else if (item == clearItem) {
			cleanItemEventHandler();
		} else if (item == replayItem) {
			replayEventHandler(selectedImageIndex);
		}
	}
	
	private void replayEventHandler(int selectedImageIndex) {
		MultiTouchAction2 action2 = touchActionCollecter.getTouchActionContainer().getAt(selectedImageIndex);
		if (action2 != null) {
			ReplayThread.replay(paintTouchInterface, action2);
		}
	}
	
	private void cleanItemEventHandler() {
		if (hImageArrayPanel != null) {
			hImageArrayPanel.clear();
		}
		
		TouchActionContainer touchActionContainer = touchActionCollecter.getTouchActionContainer();
		touchActionContainer.removeAll();
	}

	private String getSavePath() {
		String configSavePath = historyConfig.getConfigValue(HISTORY_SAVE_PATH_CONFIG_NAME);
		JFileChooser fileChooser = new JFileChooser(configSavePath);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int retValue = fileChooser.showSaveDialog(null);
		if (retValue != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		
		File saveDirector = fileChooser.getSelectedFile();
		historyConfig.setConfigItem(new ConfigItem(HISTORY_SAVE_PATH_CONFIG_NAME, saveDirector.getPath()));
		
		return saveDirector.getPath();
	}
	
	private boolean saveTouchAction(MultiTouchAction2 touchAction2, String savePath, ImageConfig config) {
		if (!FileAdapter.initFolder(savePath)) {
			HLog.el("Fail to create " + savePath);
			return false;
		}
		
		String originalDataSavePath = FileAdapter.pathCat(savePath, "originalData.txt");
		FileAdapter.saveStringToFile(touchAction2.getOriginalData().toString(), originalDataSavePath);
		
		String eventDataSavePath = FileAdapter.pathCat(savePath, "eventData.txt");
		StringBuffer eventDataBuffer = new StringBuffer();
		ArrayList<MultiTouchPoint> points = touchAction2.getTouchPoints();
		for (int i = 0; i < points.size(); i++) {
			eventDataBuffer.append(points.get(i).toString() + "\n");
		}
		
		FileAdapter.saveStringToFile(eventDataBuffer.toString(), eventDataSavePath);
		
		// 保存图片
		String touchActionImageSavePath = FileAdapter.pathCat(savePath, "touchActionImage." + IMAGE_FILE_FORMATE);
		try {
			ImageIO.write(
					touchAction2.getPreviewImage((int)currentResolutionRatio.getWidth(), 
							(int)currentResolutionRatio.getHeight(), config), 
							IMAGE_FILE_FORMATE, new File(touchActionImageSavePath));
		} catch (IOException e) {
			HLog.el(e);
			System.err.println("Fail to save picture:" + touchActionImageSavePath);
			return false;
		}
		
		return true;
	}

	@Override
	public void resolutionChangeEvent(Dimension resolution) {
		currentResolutionRatio.setSize(resolution);
	}

	@Override
	public void clearEvent() {
		cleanItemEventHandler();	
	}

	@Override
	public void doubleClickEvent(int selectedImage) {
		replayEventHandler(selectedImage);
	}
}
