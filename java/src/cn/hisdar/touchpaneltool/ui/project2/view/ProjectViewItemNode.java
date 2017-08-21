package cn.hisdar.touchpaneltool.ui.project2.view;

import javax.swing.tree.DefaultMutableTreeNode;

public class ProjectViewItemNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 4441582540662259057L;
	private boolean isFile = true;
	private String srcFilePath = null;

	public ProjectViewItemNode() {
		super();
	}
	
	public ProjectViewItemNode(Object userObject) {
		super(userObject);
	}
	
	public ProjectViewItemNode(Object userObject, String srcFilePath) {
		super(userObject);
		this.srcFilePath = srcFilePath;
	}

	public ProjectViewItemNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
	}
	
	public ProjectViewItemNode(Object userObject, String srcFilePath, boolean allowsChildren) {
		super(userObject, allowsChildren);
		this.srcFilePath = srcFilePath;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

	public String getSrcFilePath() {
		return srcFilePath;
	}

	public void setSrcFilePath(String srcFilePath) {
		this.srcFilePath = srcFilePath;
	}
	
}
