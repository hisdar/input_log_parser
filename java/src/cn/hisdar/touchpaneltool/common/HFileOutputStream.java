package cn.hisdar.touchpaneltool.common;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.hisdar.lib.log.HLog;

public class HFileOutputStream extends FileOutputStream {

	public HFileOutputStream(String name) throws FileNotFoundException {
		super(name);
	}
	

	public HFileOutputStream(File file, boolean append)
			throws FileNotFoundException {
		super(file, append);
	}



	public HFileOutputStream(File file) throws FileNotFoundException {
		super(file);
	}



	public HFileOutputStream(FileDescriptor fdObj) {
		super(fdObj);
	}



	public HFileOutputStream(String name, boolean append)
			throws FileNotFoundException {
		super(name, append);
	}



	@Override
	public void close() {
		try {
			super.close();
		} catch (IOException e) {
			HLog.el(e);
		}
	}

	@Override
	public void flush() {
		try {
			super.flush();
		} catch (IOException e) {
			HLog.el(e);
		}
	}
}
