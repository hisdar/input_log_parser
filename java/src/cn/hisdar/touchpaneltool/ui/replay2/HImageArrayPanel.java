package cn.hisdar.touchpaneltool.ui.replay2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 * @description 外界通过updateImages接口来更新显示的图像
 * @author Hisdar
 *
 */
public class HImageArrayPanel extends JPanel implements ActionListener {

	// 图像之间的间距
	private static final int IMAGE_SPACE = 5;
	private static final int PANEL_BORDER_TOP = 5;
	private static final int PANEL_BORDER_LEFT = 5;
	private static final int PANEL_BORDER_BOTTOM = 5;
	private static final int PANEL_BORDER_RIGHT = 5;
	
	private double startLocation = 1;			// 开始绘制图像的位置
	private int currentImageCount = 0;		// 面板中显示的图像的个数

	private ArrayList<MultiTouchAction2> touchAction2s = null;
	private BufferedImage mainImage = null; // 面板中要显示的图像
	private BufferedImage itemImage = null; // 一个触摸操作的图像的显示容器
	private Dimension itemImageResolution = null;
	private int touchPreviewWidth = 0;		// 所有的触摸操作绘制成一张图像，这张图像的宽度
	
	private Dimension panelOldDimension = null;//面板的尺寸
	private Point mouseLocation = null;
	private boolean isMouseIn = false;
	
	private MouseEventHandler mouseEventHandler = null;
	
	private JPopupMenu imagePopupMenu = null;
	private ArrayList<JMenuItem> menuItems = null;
	private PopMenuActionListener popMenuActionListener = null;
	private ArrayList<HImageArrayPanelActionListener> actionListeners = null;
	private ArrayList<ImageSelectedChangeListener> imageSelectedChangeListeners = null;
	
	private Date doubleClickTime = null;
	
	private int selectedImageIndex = -1;
	
 	public HImageArrayPanel(int size) {
 		itemImageResolution = new Dimension();
 		mouseLocation = new Point(-1, -1);
 		menuItems = new ArrayList<JMenuItem>();
 		actionListeners = new ArrayList<HImageArrayPanelActionListener>();
 		imageSelectedChangeListeners = new ArrayList<ImageSelectedChangeListener>();
 		
 		mouseEventHandler = new MouseEventHandler();
 		addMouseListener(mouseEventHandler);
 		addMouseMotionListener(mouseEventHandler);
 		
 		panelOldDimension = new Dimension(0, 0);
 		
 		imagePopupMenu = new JPopupMenu();
	}
	
	public void updateImages(ArrayList<MultiTouchAction2> multiTouchAction2s, Dimension resolution) {
		
		touchAction2s = multiTouchAction2s;
		itemImageResolution.setSize(resolution);
		
		initMainImage(multiTouchAction2s, resolution);
		
		repaint();
		
		notifyImageSelectedChangeListener();
	}
	
	public void clear() {
		if (mainImage != null) {
			touchPreviewWidth = 0;
			mainImage.getGraphics().setColor(getBackground());
			mainImage.getGraphics().fillRect(0, 0, mainImage.getWidth(), mainImage.getHeight());
			repaint();
		}
		
		if (touchAction2s != null) {
			int touchActionCount = touchAction2s.size();
			for (int i = 0; i < touchActionCount; i++) {
				touchAction2s.remove(0);
			}
		}
	}
	
	/**
	 * 
	 * @param multiTouchAction2s : 要绘制的触摸操作
	 * @param resolution         : 当前图片的分辨率
	 */
	private void initMainImage(ArrayList<MultiTouchAction2> multiTouchAction2s, Dimension resolution) {

		if (multiTouchAction2s == null || resolution == null) {
			return;
		}
		
		// 根据panel的高度来计算每一张图像的高度和宽度
		int imageHeight = getHeight() - PANEL_BORDER_TOP - PANEL_BORDER_BOTTOM;
		int imageWidth  = (int)(1.0 * resolution.getWidth() / resolution.getHeight() * imageHeight);
		
		// 所有的触摸操作汇聚起来，生成的图片的高度和宽度
		touchPreviewWidth  = imageWidth * multiTouchAction2s.size() + (multiTouchAction2s.size() - 1) * IMAGE_SPACE;
		// 如果界面的高度有变化的话，更新mainImage
		if (mainImage == null || !panelOldDimension.equals(getSize()) || currentImageCount != multiTouchAction2s.size()) {
			panelOldDimension.setSize(getSize());
			currentImageCount = multiTouchAction2s.size();
			
			// 重新生成mainImage
			int mainImageWidth = getWidth() - PANEL_BORDER_LEFT - PANEL_BORDER_RIGHT;
			int mainImageHeight = getHeight() - PANEL_BORDER_TOP - PANEL_BORDER_BOTTOM;
			if (mainImageHeight <= 0 || mainImageWidth <= 0) {
				return;
			}
			
			mainImage = new BufferedImage(mainImageWidth, mainImageHeight, BufferedImage.TYPE_INT_ARGB);
			itemImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		}
		
		Graphics2D mainImageGraphics = (Graphics2D)mainImage.getGraphics();
		mainImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		mainImageGraphics.setColor(Color.WHITE);
		mainImageGraphics.fillRect(0, 0, mainImage.getWidth(), mainImage.getHeight());
		
		int locationX = 0;
		int locationY = 0;
		int startLocationX = (int)(startLocation * getMaxLocationRange());
		if (touchPreviewWidth > getWidth()) {
			locationX = -(startLocationX);// + startLocationX;
		}
		
		for (int i = 0; i < multiTouchAction2s.size(); i++) {
			itemImage = multiTouchAction2s.get(i).getZoomedPreviceImage(itemImage, resolution, ImageConfigAdapter.getPreviewConfig());
			synchronized (mainImage) {
				mainImageGraphics.drawImage(itemImage, locationX, locationY, imageWidth, imageHeight, null);
			}
			
			locationX = locationX + imageWidth + IMAGE_SPACE;
		}
	}
	
	private void drawMaskingImage(Graphics2D mainImageGraphics) {
		
		if (mainImage == null) {
			return;
		}
		
		if (touchAction2s == null || touchAction2s.size() <= 0 || !isMouseIn) {
			return;
		}
		
		// 计算蒙版的大小
		int imageHeight = getHeight() - PANEL_BORDER_TOP - PANEL_BORDER_BOTTOM;
		int imageWidth  = (int)(1.0 * itemImageResolution.getWidth() / itemImageResolution.getHeight() * imageHeight);
		
		// 计算蒙版的位置
		int maskingLocationX = mouseLocation.x + (int)(startLocation * getMaxLocationRange());
		if (maskingLocationX > touchPreviewWidth) {
			return;
		}
		
		maskingLocationX = mouseLocation.x - maskingLocationX % (imageWidth + IMAGE_SPACE) + PANEL_BORDER_LEFT;
		
		mainImageGraphics.setColor(new Color(124, 252, 0, 100));
		mainImageGraphics.fillRect(maskingLocationX, PANEL_BORDER_TOP, imageWidth, imageHeight);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		// 如果界面的大小变化，重新绘制
		if (!panelOldDimension.equals(getSize())) {
			initMainImage(touchAction2s, itemImageResolution);
		}
		
		if (mainImage != null) {
			synchronized (mainImage) {
				g.drawImage(mainImage, PANEL_BORDER_LEFT, PANEL_BORDER_TOP, mainImage.getWidth(), mainImage.getHeight(), null);
			}
		}
		
		drawMaskingImage((Graphics2D)g);
	}
	
	public int getImageWidth() {
		if (mainImage == null) {
			return 0;
		}
		
		return mainImage.getWidth();
	}
	
	public int getImageHeight() {
		if (mainImage == null) {
			return 0;
		}
		
		return mainImage.getHeight();
	}
	
	public int getMaxLocationRange() {
		if (mainImage == null) {
			return 0;
		}
		
		int maxRange = touchPreviewWidth - mainImage.getWidth();
		
		return maxRange > 0 ? maxRange : 0;
	}
	
	public void setImageLocation(double location) {
		if (location < 0 || location > 1) {
			return ;
		}
		
		startLocation = location;
		initMainImage(touchAction2s, itemImageResolution);
		repaint();
	}
	
	public void addJMenuItem(JMenuItem item) {
		for (int i = 0; i < menuItems.size(); i++) {
			if (menuItems.get(i) == item) {
				return;
			}
		}
		
		menuItems.add(item);
		imagePopupMenu.add(item);
		item.addActionListener(this);
	}
	
	public void setPopMenuActionListener(PopMenuActionListener listener) {
		popMenuActionListener = listener;
	}
	
	public void addImageSelectedChangeListener(ImageSelectedChangeListener listener) {
		for (int i = 0; i < imageSelectedChangeListeners.size(); i++) {
			if (imageSelectedChangeListeners.get(i) == listener) {
				return;
			}
		}
		
		imageSelectedChangeListeners.add(listener);
		int currentSelectedImage = getSelectedImage();
		if (currentSelectedImage >= 0) {
			listener.imageSelectedChangeEvent(currentSelectedImage);
		}
	}
	
	private JComponent getCurrentPanel() {
		return this;
	}
	
	private int getSelectedImage() {
		if (touchAction2s == null || touchAction2s.size() <= 0 || mainImage == null) {
			return -1;
		}
		
		// 计算一张图片的大小
		int imageHeight = getHeight() - PANEL_BORDER_TOP - PANEL_BORDER_BOTTOM;
		int imageWidth  = (int)(1.0 * itemImageResolution.getWidth() / itemImageResolution.getHeight() * imageHeight);
		
		// 计算蒙版的位置
		int maskingLocationX = mouseLocation.x + (int)(startLocation * getMaxLocationRange());
		if (maskingLocationX > touchPreviewWidth) {
			return -1;
		}
		
		int selectedImageIndex = maskingLocationX / (imageWidth + IMAGE_SPACE);
		
		return selectedImageIndex;
	}
	
	private void notifyImageSelectedChangeListener() {
		// 通知监听者，被选中的图片发生了改变
		int currentSelectedImage = getSelectedImage();
		if (selectedImageIndex != currentSelectedImage) {
			selectedImageIndex = currentSelectedImage;
			for (int i = 0; i < imageSelectedChangeListeners.size(); i++) {
				imageSelectedChangeListeners.get(i).imageSelectedChangeEvent(selectedImageIndex);
			}
		}
	}
	
	private class MouseEventHandler extends MouseAdapter {

		@Override
		public void mouseMoved(MouseEvent e) {
			// 计算蒙版所在的位置
			mouseLocation.setLocation(e.getX(), e.getY());
			repaint();
			
			notifyImageSelectedChangeListener();
			
			super.mouseMoved(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			isMouseIn = true;
			repaint();
			
			notifyImageSelectedChangeListener();
			
			super.mouseEntered(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			isMouseIn = false;
			repaint();
			notifyImageSelectedChangeListener();
			super.mouseExited(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			int selectedImage = getSelectedImage();
			if(e.getButton() == MouseEvent.BUTTON3 && menuItems.size() > 0 && selectedImage >= 0){
				imagePopupMenu.show(getCurrentPanel(), e.getX(), e.getY());
			}
			
			if (e.getButton() == MouseEvent.BUTTON1 && selectedImage >= 0) {
				Date currentTime = new Date();
				if (doubleClickTime != null && currentTime.getTime() - doubleClickTime.getTime() < 500) {
					for (int j = 0; j < actionListeners.size(); j++) {
						actionListeners.get(j).doubleClickEvent(selectedImage);
					}
					
					doubleClickTime = null;
				} else {
					doubleClickTime = new Date();
				}
			}
			
			super.mousePressed(e);
		}
	}

	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < menuItems.size(); i++) {
			if (menuItems.get(i) == e.getSource() && popMenuActionListener != null) {
				popMenuActionListener.menuItemClickedEvent(menuItems.get(i), getSelectedImage());
			}
		}
	}
	
	public void addHImageArrayPanelActionListener(HImageArrayPanelActionListener listener) {
		for (int i = 0; i < actionListeners.size(); i++) {
			if (actionListeners.get(i) == listener) {
				return;
			}
		}
		
		actionListeners.add(listener);
	}
	
	public void removeHImageArrayPanelActionListener(HImageArrayPanelActionListener listener) {
		int listenerCount = actionListeners.size();
		for (int i = listenerCount - 1; i >= 0; i--) {
			if (actionListeners.get(i) == listener) {
				actionListeners.remove(i);
			}
		}
	}
}
