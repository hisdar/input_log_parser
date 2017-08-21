package cn.hisdar.touchpaneltool.update;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.ui.UIAdapter;

public class VersionDowndDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 45783476080229057L;

	public enum VersionDownLoad {
		DOWNLOAD_SUCCESS,
		DOWNLOAD_CANCLE,
		DOWNLOAD_FAIL,
		DOWNLOADING,
		VERSION_DAMAGE
	}
	
	private JProgressBar downdloadProgressBar;
	private JButton cancleButton;
	private JLabel messageLabel;
	private JLabel dialogLogoLabel;
	private VersionDowndThread versionDowndThread = null;
	private VersionDownLoad versionDownLoadResult = VersionDownLoad.DOWNLOAD_CANCLE;
	private boolean isCancleVersionDownd = false;
	private String versionProofTestValue;
	
	public VersionDowndDialog() {
		
		setSize(600, 230);
		setLocation(UIAdapter.getCenterLocation(null, this));
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		ImageIcon logoIcon = new ImageIcon("./Image/VersionDialogLogo.png");
		dialogLogoLabel = new JLabel(logoIcon);
		
		messageLabel = new JLabel(" ");
		messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		messageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		downdloadProgressBar = new JProgressBar(0, 100);		JPanel progressPanel = new JPanel(new BorderLayout());
		progressPanel.add(messageLabel, BorderLayout.NORTH);
		progressPanel.add(downdloadProgressBar, BorderLayout.CENTER);
		
		cancleButton = new JButton("取消");
		cancleButton.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
		cancleButton.addActionListener(this);
		JPanel actionPanel = new JPanel(new GridLayout(1, 1, 10, 10));
		actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 470, 20, 30));
		actionPanel.add(cancleButton);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(progressPanel, BorderLayout.CENTER);
		mainPanel.add(actionPanel, BorderLayout.SOUTH);
		mainPanel.add(dialogLogoLabel, BorderLayout.NORTH);
		
		setLayout(new BorderLayout());
		add(mainPanel);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancleButton) {
			isCancleVersionDownd = true;
			versionDownLoadResult = VersionDownLoad.DOWNLOAD_CANCLE;
			setVisible(false);
		}
	}
	
	public VersionDownLoad startDowndVersion(String serverVersionPath, String localPath, String versionProofTestValue) {
		this.versionProofTestValue = versionProofTestValue;
		if (versionDowndThread != null && versionDowndThread.isAlive()) {
			return VersionDownLoad.DOWNLOADING;
		}

		versionDowndThread = new VersionDowndThread(serverVersionPath, localPath);
		versionDowndThread.start();
		
		setVisible(true);
		
		return versionDownLoadResult;
	}
	
	private String doChecksum(String fileName) {

		long checksum = 0;
		try {
			CheckedInputStream cis = null;
			
			cis = new CheckedInputStream(new FileInputStream(fileName), new CRC32());

			byte[] buf = new byte[128];
			while (cis.read(buf) >= 0);

			checksum = cis.getChecksum().getValue();
			cis.close();
		} catch (IOException e) {
			HLog.el(e);
		}
		
		return Long.toString(checksum);
	}
	
	private class VersionDowndThread extends Thread {
		private String serverVersionPath;
		private String localPath;
		
		public VersionDowndThread(String serverVersionPath, String localPath) {
			this.serverVersionPath = serverVersionPath;
			this.localPath = localPath;
		}
		
		private boolean downloadVersionFile(String serverVersionPath, String localVersionPath) {
			
			boolean downdResult = false;
			byte[] readBuffer = new byte[1024];
			int readCount = 0;
			long copiedSize = 0;
			float downdProgress = 0;
			try {
				File versionFile = new File(serverVersionPath);
				
				long versionFileLength = versionFile.length();
				
				FileInputStream fileInputStream = new FileInputStream(versionFile);
				FileOutputStream fileOutputStream = new FileOutputStream(new File(localVersionPath));
				readCount = fileInputStream.read(readBuffer);
				
				while (readCount > 0 && !isCancleVersionDownd) {
					fileOutputStream.write(readBuffer, 0, readCount);
					
					copiedSize += readCount;
					downdProgress = copiedSize * 100.0f / versionFileLength;
					downdloadProgressBar.setValue((int)(downdProgress));
					messageLabel.setText("从服务器上下载版本，下载进度：" + downdProgress + "%");
					readCount = fileInputStream.read(readBuffer);
				}
				
				fileOutputStream.close();
				fileInputStream.close();
				
				if (copiedSize == versionFileLength) {
					downdResult = true;
				} else {
					downdResult = false;
				}
				
			} catch (IOException e) {
				HLog.el(e);
				downdResult = false;
			}
			
			return downdResult;
		}
		
		public void run () {
			File localVersionFile = new File(localPath);
			
			// 如果本地版本存在，先校验本地版本是否能校验通过，能校验通过的话，直接升级
			if (localVersionFile.exists()) {
				HLog.il("Local version file is exist");
				messageLabel.setText("正在进行升级前校验，请等待......");
				String localFileChecksum = doChecksum(localPath);
				
				HLog.il("Local version checksum:" + localFileChecksum);
				HLog.il("Server version checksum:" + versionProofTestValue);
				
				if (localFileChecksum.equals(versionProofTestValue)) {
					// 本地版本校验正确的话，直接升级
					HLog.il("Local version checksum pass, return download success");
					versionDownLoadResult = VersionDownLoad.DOWNLOAD_SUCCESS;
					setVisible(false);
					return;
				} else {
					// 本地版本校验失败的话，删除本地版本
					HLog.il("Local version checksum fail, delete local version file");
					localVersionFile.delete();
				}
			} else {
				HLog.il("Local version file not exist");
			}
			
			// 如果版本不存在
			if (!localVersionFile.exists()) {
				HLog.il("download version file form server");
				HLog.il("server version file path:" + serverVersionPath);
				HLog.il("local version file path:" + localPath);
				messageLabel.setText("从服务器上下载版本");
				if (!downloadVersionFile(serverVersionPath, localPath)) {
					// 下载版本失败
					if (!isCancleVersionDownd) {
						versionDownLoadResult = VersionDownLoad.DOWNLOAD_FAIL;
						JOptionPane.showMessageDialog(null, "版本下载失败，请手动下载安装更新！", "错误提示", JOptionPane.ERROR_MESSAGE);
					} else {
						versionDownLoadResult = VersionDownLoad.DOWNLOAD_CANCLE;
					}
				} else {
					// 先校验本地版本
					messageLabel.setText("下载已完成，正在校验版本，请等待......");
					String localFileChecksum = doChecksum(localPath);
					
					HLog.il("Local version checksum:" + localFileChecksum);
					HLog.il("Server version checksum:" + versionProofTestValue);
					
					if (localFileChecksum.equals(versionProofTestValue)) {
						// 本地版本校验正确的话，直接升级
						versionDownLoadResult = VersionDownLoad.DOWNLOAD_SUCCESS;
					} else {
						// 本地版本校验失败的话，删除本地版本并下载最新版本
						localVersionFile.delete();
						versionDownLoadResult = VersionDownLoad.VERSION_DAMAGE;
						JOptionPane.showMessageDialog(null, "版本损坏，请等待下一次更新！", "错误提示", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			
			setVisible(false);
		}
	}
}
