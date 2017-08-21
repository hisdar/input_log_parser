package cn.hisdar.touchpaneltool.ui.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import cn.hisdar.MultiTouchEventParse.multiTouchEvent.MultiTouchEventTime;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.FileAdapter;
import cn.hisdar.touchpaneltool.ui.WaitWork;
import cn.hisdar.touchpaneltool.ui.WaitWorkTaskInterface;

public class SelectTimeWork implements WaitWorkTaskInterface {

	private String startTime = null;
	private String endTime = null;
	private WaitWork timeSelectWork;
	
	public SelectTimeWork() {
		
	}
	
	public void start(String startTime, String endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
		
		timeSelectWork = new WaitWork(true);
		timeSelectWork.setWorkTask(this);
		timeSelectWork.startWaitWork();
	}

	@Override
	public void waitWorkTask() {
		// 检查中间文件是否存在，如果不存在，复制一份
		File touchEventFile = new File("./buffer/eventFile.bak");
		if (!touchEventFile.exists()) {
			if (!FileAdapter.copyFile("./buffer/eventFile", "./buffer/eventFile.bak", false)) {
				JOptionPane.showMessageDialog(null, "缓冲文件拷贝失败，当然，时间也没办法确定啦，I'm so sorry!", "错误",JOptionPane.ERROR_MESSAGE);
				return;
			} else {
				HLog.il("File create success:./buffer/eventFile.bak");
			}
		} else {
			HLog.il("Found ./buffer/eventFile.bak");
		}
		
		// 从./buffer/touchEventFile.bak 中获取所选时间段内的文件
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./buffer/eventFile.bak"));
			BufferedWriter writer = new BufferedWriter(new FileWriter("./buffer/eventFile"));
			boolean isFoundStartTime = false;
			String lineString = reader.readLine();
			while (lineString != null) {
				String currentTime = MultiTouchEventTime.parseSystemTime(lineString);
				if (currentTime == null) {
					lineString = reader.readLine();
					continue;
				}
				
				if (currentTime.compareTo(endTime) > 0) {
					isFoundStartTime = false;
					break;
				}
				
				if (!isFoundStartTime && currentTime.compareTo(startTime) >= 0) {
					isFoundStartTime = true;
				}
				
				if (isFoundStartTime) {
					writer.write(lineString + "\n");
				}
				
				lineString = reader.readLine();
				
			}
			
			reader.close();
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			HLog.el(e);
		} catch (IOException e) {
			HLog.el(e);
		}
		
		HLog.il("End copy");
	}

	@Override
	public void taskFinishCallBack() {
		// TODO Auto-generated method stub
		
	}
}
