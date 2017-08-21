package cn.hisdar.touchpaneltool.ui.project;

import javax.swing.tree.DefaultMutableTreeNode;

public class ProjectTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 2315954729609314928L;
	
	private String treeNodeFilePath = null;
	private String treeNodeName = null;
	private boolean isProjectNode = false;
	
	public ProjectTreeNode(String treeNodeName) {
		super(treeNodeName);
		this.treeNodeName = treeNodeName;
	}

	public String getTreeNodeFilePath() {
		return treeNodeFilePath;
	}

	public void setTreeNodeFilePath(String treeNodeFilePath) {
		this.treeNodeFilePath = treeNodeFilePath;
	}

	public boolean isProjectNode() {
		return isProjectNode;
	}

	public void setProjectNode(boolean isProjectNode) {
		this.isProjectNode = isProjectNode;
	}

	public String getTreeNodeName() {
		return treeNodeName;
	}

	public void setTreeNodeName(String treeNodeName) {
		this.treeNodeName = treeNodeName;
	}
	
}
