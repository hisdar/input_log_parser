package cn.hisdar.touchpaneltool.ui.output;


public class ProgramLogShowPanel extends LogShowPanel {
	
	private static ProgramLogShowPanel programLogShowPanel;
	
	private ProgramLogShowPanel() {
	}
	
	public static ProgramLogShowPanel getInstance() {
		if (null == programLogShowPanel) {
			synchronized (ProgramLogShowPanel.class) {
				if (null == programLogShowPanel) {
					programLogShowPanel = new ProgramLogShowPanel();
				}
			}
		}
		
		return programLogShowPanel;
	}
}
