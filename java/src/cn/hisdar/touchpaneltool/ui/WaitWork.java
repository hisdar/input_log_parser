package cn.hisdar.touchpaneltool.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.ui.UIAdapter;

public class WaitWork implements WaitWorkTaskInterface {

	private class WorkThread extends Thread {
		private WaitWorkTaskInterface task = null;
		
		public WorkThread(WaitWorkTaskInterface task) {
			this.task = task;
		}
		
		public void run() {
			if (task != null) {
				task.waitWorkTask();
				task.taskFinishCallBack();
			}
		}
	}
	
	private class WaitDialog extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1925327399036669435L;
		private static final String ADD_FILE_WAIT_DIALOG_TITLE = "请稍等";
		private static final int ADD_FILE_WAIT_DIALOG_HEIGHT = 100;
		private static final int ADD_FILE_WAIT_DIALOG_WIDTH = 500;
		
		private JProgressBar waitProgressBar = null;
		
		private JPanel messagePanel = null;
		private JLabel messageLabel = null;

		private Font textFont = null;
		public WaitDialog(boolean indeterminate) {
			
			setTitle(ADD_FILE_WAIT_DIALOG_TITLE);
			setSize(ADD_FILE_WAIT_DIALOG_WIDTH, ADD_FILE_WAIT_DIALOG_HEIGHT);
			setLocation(UIAdapter.getCenterLocation(null, this));
			
			setLayout(new BorderLayout());
			initWaitDialog();
			
			waitProgressBar.setIndeterminate(indeterminate);
			if (!indeterminate) {
				waitProgressBar.setMaximum(100);
				waitProgressBar.setMinimum(0);
			}
			
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		}
		
		private void initWaitDialog() {
			textFont = new Font("微软雅黑", Font.PLAIN, 14);
			
			messageLabel = new JLabel(" ");
			messageLabel.setFont(textFont);
			messagePanel = new JPanel(new BorderLayout());
			messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
			messagePanel.add(messageLabel, BorderLayout.CENTER);
			
			add(messagePanel, BorderLayout.NORTH);
			
			waitProgressBar = new JProgressBar();
			JPanel progressBarPanel = new JPanel(new BorderLayout());
			progressBarPanel.add(waitProgressBar, BorderLayout.CENTER);
			progressBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			add(progressBarPanel, BorderLayout.CENTER);
		}
		
		public void setMessage(String message) {
			messageLabel.setText(message);
		}
		
		public void setProgress (int progress) {
			// 只有确定的进度条可以设置进度值
			if (!waitProgressBar.isIndeterminate()) {
				waitProgressBar.setValue(progress);
			}
		}
		
		public void setRange(int minValue, int maxValue) {
			waitProgressBar.setMaximum(maxValue);
			waitProgressBar.setMinimum(minValue);
		}
	}
	
	private WaitWorkTaskInterface taskInterface = null;
	private WaitDialog waitDialig = null;
	private WorkThread workThread = null;
	
	
	/**
	 * 
	 * @param waitWorkTask 
	 * @param indeterminate 进度条的模式，是确定模式还是不确定模式，true：不确定模式，false：确定模式
	 */
	public WaitWork(boolean indeterminate) {
		
		waitDialig = new WaitDialog(indeterminate);
	}
	
	public void setWorkTask(WaitWorkTaskInterface waitWorkTask) {
		taskInterface = waitWorkTask;
	}
	
	public void startWaitWork() {
		workThread = new WorkThread(this);
		workThread.start();
		waitDialig.setModal(true);
		waitDialig.setVisible(true);
	}

	@Override
	public void waitWorkTask() {
		if (taskInterface != null) {
			taskInterface.waitWorkTask();
		}
	}

	@Override
	public void taskFinishCallBack() {
		if (taskInterface != null) {
			taskInterface.taskFinishCallBack();
		}
		
		HLog.dl("WaitWork.taskFinishCallBack()" + "Finish work");
		waitDialig.setVisible(false);
	}
	
	public void setMessage(String message) {
		waitDialig.setMessage(message);
	}
	
	public void setProgress(int progress) {
		waitDialig.setProgress(progress);
	}
	
	public void setRange(int minValue, int maxValue) {
		waitDialig.setRange(minValue, maxValue);
	}
}
