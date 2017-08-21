package cn.hisdar.touchpaneltool.androidDevice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.hisdar.lib.log.HLog;

public class CommandLine {

	public static boolean execCommand(String command) {
		try {
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			HLog.el(e);
			return false;
		}
		
		return true;
	}
	
	public static StringBuffer execCommandAndGetResult(String command) {
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		
		try {
			process = runtime.exec(command);
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			HLog.el(e);
			return null;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuffer resuletData = new StringBuffer();
		try {
			String lineString = reader.readLine();
			while (lineString != null) {
				resuletData.append(lineString + "\n");
				lineString = reader.readLine();
			}
			
			reader.close();
		} catch (IOException e) {
			HLog.el(e);
			return null;
		}
		
		return resuletData;
	}
}
