package cn.hisdar.touchpaneltool.ui.output;


public class TouchEventShowPanel extends LogShowPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7180452502312106272L;
	private static TouchEventShowPanel touchEventShowPanel;
	
	private TouchEventShowPanel() {
	}
	
	public static TouchEventShowPanel getInstance() {
		if (null == touchEventShowPanel) {
			synchronized (TouchEventShowPanel.class) {
				if (null == touchEventShowPanel) {
					touchEventShowPanel = new TouchEventShowPanel();
				}
			}
		}
		
		return touchEventShowPanel;
	}
}
