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
		messageLabel.setFont(new Font("΢���ź�", Font.PLAIN, 14));
		downdloadProgressBar = new JProgressBar(0, 100);		JPanel progressPanel = new JPanel(new BorderLayout());
		progressPanel.add(messageLabel, BorderLayout.NORTH);
		progressPanel.add(downdloadProgressBar, BorderLayout.CENTER);
		
		cancleButton = new JButton("ȡ��");
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
					messageLabel.setText("�ӷ����������ذ汾�����ؽ��ȣ�" + downdProgress + "%");
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
			
			// ������ذ汾���ڣ���У�鱾�ذ汾�Ƿ���У��ͨ������У��ͨ���Ļ���ֱ������
			if (localVersionFile.exists()) {
				HLog.il("Local version file is exist");
				messageLabel.setText("���ڽ�������ǰУ�飬��ȴ�......");
				String localFileChecksum = doChecksum(localPath);
				
				HLog.il("Local version checksum:" + localFileChecksum);
				HLog.il("Server version checksum:" + versionProofTestValue);
				
				if (localFileChecksum.equals(versionProofTestValue)) {
					// ���ذ汾У����ȷ�Ļ���ֱ������
					HLog.il("Local version checksum pass, return download success");
					versionDownLoadResult = VersionDownLoad.DOWNLOAD_SUCCESS;
					setVisible(false);
					return;
				} else {
					// ���ذ汾У��ʧ�ܵĻ���ɾ�����ذ汾
					HLog.il("Local version checksum fail, delete local version file");
					localVersionFile.delete();
				}
			} else {
				HLog.il("Local version file not exist");
			}
			
			// ����汾������
			if (!localVersionFile.exists()) {
				HLog.il("download version file form server");
				HLog.il("server version file path:" + serverVersionPath);
				HLog.il("local version file path:" + localPath);
				messageLabel.setText("�ӷ����������ذ汾");
				if (!downloadVersionFile(serverVersionPath, localPath)) {
					// ���ذ汾ʧ��
					if (!isCancleVersionDownd) {
						versionDownLoadResult = VersionDownLoad.DOWNLOAD_FAIL;
						JOptionPane.showMessageDialog(null, "�汾����ʧ�ܣ����ֶ����ذ�װ���£�", "������ʾ", JOptionPane.ERROR_MESSAGE);
					} else {
						versionDownLoadResult = VersionDownLoad.DOWNLOAD_CANCLE;
					}
				} else {
					// ��У�鱾�ذ汾
					messageLabel.setText("��������ɣ�����У��汾����ȴ�......");
					String localFileChecksum = doChecksum(localPath);
					
					HLog.il("Local version checksum:" + localFileChecksum);
					HLog.il("Server version checksum:" + versionProofTestValue);
					
					if (localFileChecksum.equals(versionProofTestValue)) {
						// ���ذ汾У����ȷ�Ļ���ֱ������
						versionDownLoadResult = VersionDownLoad.DOWNLOAD_SUCCESS;
					} else {
						// ���ذ汾У��ʧ�ܵĻ���ɾ�����ذ汾���������°汾
						localVersionFile.delete();
						versionDownLoadResult = VersionDownLoad.VERSION_DAMAGE;
						JOptionPane.showMessageDialog(null, "�汾�𻵣���ȴ���һ�θ��£�", "������ʾ", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			
			setVisible(false);
		}
	}
}
