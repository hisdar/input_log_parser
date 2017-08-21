package cn.hisdar.touchpaneltool.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

import cn.hisdar.lib.log.HLog;
import cn.hisdar.radishlib.FileAdapter;

public class GZip {

	private BufferedOutputStream bufferedOutputStream;
	String zipfileName = null;

	public GZip(String fileName) {

		this.zipfileName = fileName;
	}

	/*
	 * 
	 * 执行入口,rarFileName为需要解压的文件路径(具体到文件),destDir为解压目标路径
	 */

	public static void unTargzFile(String rarFileName, String destDir) {
		GZip gzip = new GZip(rarFileName);
		String outputDirectory = destDir;
		File file = new File(outputDirectory);

		if (!file.exists()) {
			file.mkdir();
		}

		gzip.unzipOarFile(outputDirectory);
	}

	public static void unTargzFolder(String folderPath) {
		unTargzFolder(folderPath, false);
	}
	
	public static void unTargzFolder(String folderPath, boolean isShowProgress) {
		Vector<String> fileList = FileAdapter.getFileList(folderPath);
		
		for (int i = 0; i < fileList.size(); i++) {
			
			String unZipedFilePath = fileList.get(i);
			String unZipedFileParent = new File(unZipedFilePath).getParent();
			String unZipedFileName = new File(unZipedFilePath).getName();
			int endIndex = unZipedFileName.indexOf(".tar.gz");
			if (endIndex > 0) {
				unZipedFileName = unZipedFileName.substring(0, endIndex);
				unZipedFileName = unZipedFileName.replace('.', '-');
			}
			
			File waitUnZipFile = new File(fileList.get(i));
			if (waitUnZipFile.isFile()) {
				if (isTargzFile(waitUnZipFile.getPath())) {
					String releasePath = waitUnZipFile.getPath() + "~";
					unTargzFile(waitUnZipFile.getPath(), releasePath);
					waitUnZipFile.delete();
					
					File releasePathFile = new File(releasePath);
					if (releasePathFile.isDirectory()) {
						unZipedFileName += "-D";
					}
					
					unZipedFilePath = FileAdapter.pathCat(unZipedFileParent, unZipedFileName);
					releasePathFile.renameTo(new File(unZipedFilePath));
				}
			}
		}
	}
	
	public static boolean isTargzFile(String filePath) {
		return filePath.endsWith(".tar.gz");
	}
	
	public void unzipOarFile(String outputDirectory) {
		FileInputStream fis = null;
		ArchiveInputStream in = null;
		BufferedInputStream bufferedInputStream = null;

		try {
			fis = new FileInputStream(zipfileName);
			GZIPInputStream is = new GZIPInputStream(new BufferedInputStream(fis));
			in = new ArchiveStreamFactory().createArchiveInputStream("tar", is);
			bufferedInputStream = new BufferedInputStream(in);
			TarArchiveEntry entry = (TarArchiveEntry) in.getNextEntry();

			while (entry != null) {
				String name = entry.getName();
				String[] names = name.split("/");
				String fileName = outputDirectory;

				for (int i = 0; i < names.length; i++) {
					String str = names[i];
					fileName = fileName + File.separator + str;
				}

				if (name.endsWith("/")) {
					mkFolder(fileName);
				} else {
					File file = mkFile(fileName);
					bufferedOutputStream = new BufferedOutputStream(
					new FileOutputStream(file));
					int b;

					while ((b = bufferedInputStream.read()) != -1) {
						bufferedOutputStream.write(b);
					}

					bufferedOutputStream.flush();
					bufferedOutputStream.close();
				}

				entry = (TarArchiveEntry) in.getNextEntry();
			}
		} catch (FileNotFoundException e) {
			HLog.el(e);
		} catch (IOException e) {
			HLog.el(e);
		} catch (ArchiveException e) {
			HLog.el(e);
		} finally {

			try {
				if (bufferedInputStream != null) {
					bufferedInputStream.close();
				}
			} catch (IOException e) {
				HLog.el(e);
			}
		}
	}

	
	private void mkFolder(String fileName) {
		File f = new File(fileName);

		if (!f.exists()) {
			f.mkdir();
		}

	}

	private File mkFile(String fileName) {
		File f = new File(fileName);

		try {
			f.createNewFile();
		} catch (IOException e) {
			HLog.el(e);
		}
		return f;
	}
}