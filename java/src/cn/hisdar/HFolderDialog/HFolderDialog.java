package cn.hisdar.HFolderDialog;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Properties;

import org.junit.Test;

public class HFolderDialog {

	private static boolean isLoadLib = false;
	public HFolderDialog() {
		loadLib();
	}
	
	private void loadLib() {
		if (!isLoadLib) {
			synchronized (HFolderDialog.class) {
				if (!isLoadLib) {
					Properties props = System.getProperties();
					String bits=String.valueOf(props.get("sun.arch.data.model"));  
					if (bits.equals("64")) {
						System.loadLibrary("./JarLib/HFolderDialog_x64");
					} else if (bits.equals("32")) {
						System.loadLibrary("./JarLib/HFolderDialog_x86");
					}
					
					isLoadLib = true;
				}
			}
		}
	}
	
	@Test
	public void TestLoad() {
		new HFolderDialog();
	}
	
	public String getSelectedFolder() {
		if (isClickOkButton()) {
			return getSelectedFolder_();
		}
		
		return null;
	}
	
	/**
	 * @description set the title of the folder select dialog
	 * @param title
	 */
	private native void setTitle(String title);
	
	/**
	 * @description show folder select dialog
	 */
	public native void show();
	
	/**
	 * @description set the folder choice dialog location
	 * @param x
	 * @param y
	 */
	public native void setLocation(int x, int y);
	
	/**
	 * @description get the folder choice dialog location
	 * @return
	 */
	public native Point getLocation();
	
	/**
	 * @description set the size of the folder select dialog
	 * @param width
	 * @param height
	 */
	public native void setSize(int width, int height);
	
	/**
	 * @description set the default path of folder select dialog
	 * @param folderPath
	 */
	public native void setFolder(String folderPath);
	
	/**
	 * @description set the select file of the select dialog
	 * @param selectedFolderPath
	 */
	private native void setSelectedFolder(String selectedFolderPath);
	
	/**
	 * @description set folder select model:multi or single
	 * @param model
	 */
	private native void setSelectModel(boolean model);
	
	/**
	 * @description get selected folder
	 * @return
	 */
	private native String getSelectedFolder_();
	
	/**
	 * @description is this user clicked the ok button
	 * @return
	 */
	private native boolean isClickOkButton();
	
	/**
	 * @description get folder dialog size
	 * @return
	 */
	public native Dimension getSize();
	
	/**
	 * @description get folder select model:multi or single
	 * @return
	 */
	private native boolean getModel();
	
	/**
	 * @description get folder select dialog title
	 * @return
	 */
	private native String getTitle();
}
