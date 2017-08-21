package cn.hisdar.touchpaneltool.ui.control;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import cn.hisdar.lib.configuration.ConfigItem;
import cn.hisdar.lib.configuration.HConfig;
import cn.hisdar.radishlib.ui.UIAdapter;
import sun.swing.table.DefaultTableCellHeaderRenderer;

public class ResolutionDeleteDialog extends JDialog implements ActionListener {

	private static final String[] RESOLUTION_TABLE_TITLE = {"���", "�ֱ���"};
	private static final String[][] RESOLUTION_VALUES = {{"", ""},};
	private static HConfig resolutionConfig = null;
	
	private JTable resolutionTable = null;
	private DefaultTableModel resolutionTableModel = null;


	private JButton deleteButton = null;
	private JButton finishButton = null;
	
	private ResolutionDeleteDialog() {
		setTitle("ɾ���ֱ�����Ϣ");
		setSize(300, 150);
		setLocation(UIAdapter.getCenterLocation(null, this));
		
		resolutionConfig = HConfig.getInstance(ResolutionSetPanel.RESOLUTION_CONFIG_FILE_PATH);
		
		resolutionTableModel = new DefaultTableModel(RESOLUTION_VALUES, RESOLUTION_TABLE_TITLE);
		resolutionTable = new JTable(resolutionTableModel);
		
		// ��ͷ����
		DefaultTableCellHeaderRenderer thr = new DefaultTableCellHeaderRenderer();
	    thr.setHorizontalAlignment(JLabel.CENTER);
	    resolutionTable.getTableHeader().setDefaultRenderer(thr);
	    
	    // ���ݾ���
	    DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
	    cr.setHorizontalAlignment(JLabel.CENTER);
	    resolutionTable.setDefaultRenderer(Object.class, cr);
	    
	    // �����п�
	    TableColumn firsetColumn = resolutionTable.getColumnModel().getColumn(0);
	    firsetColumn.setPreferredWidth(40);
	    firsetColumn.setMaxWidth(40);
	    firsetColumn.setMinWidth(40);
	    
	    resolutionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// һ��ֻ��ѡһ��
	    
		JScrollPane resolutionScrollPane = new JScrollPane(resolutionTable);
		
		setLayout(new BorderLayout());
		add(resolutionScrollPane, BorderLayout.CENTER);
		initResolutionTable();
		
		// ��ʼ����ť
		finishButton = new JButton(" ��  �� ");
		deleteButton = new JButton(" ɾ  �� ");
		finishButton.addActionListener(this);
		deleteButton.addActionListener(this);
		
		FlowLayout actionPanelLayout = new FlowLayout();
		actionPanelLayout.setAlignment(FlowLayout.RIGHT);
		actionPanelLayout.setVgap(10);
		JPanel actionPanel = new JPanel(actionPanelLayout);
		actionPanel.add(finishButton);
		actionPanel.add(deleteButton);
		
		add(actionPanel, BorderLayout.SOUTH);
	}
	
	private void initResolutionTable() {
		int tableRowCount = resolutionTable.getRowCount();
		for (int i = 0; i < tableRowCount; i++) {
			resolutionTableModel.removeRow(0);
		}
		
		ArrayList<ConfigItem> resolutionList = resolutionConfig.getConfigItemList();
		int resolutionCount = resolutionConfig.getConfigCount();
		for (int i = 0; i < resolutionCount; i++) {
			Object[] rowData = new Object[2];
			rowData[0] = (i + 1) + "";
			rowData[1] = resolutionList.get(i).value;
			resolutionTableModel.addRow(rowData);
		}
		
		// ѡ�е�һ��
		resolutionTable.setRowSelectionInterval(0, 0);
	}
	
	public static ResolutionDeleteDialog showResolutionDeleteDialog() {
		ResolutionDeleteDialog resolutionDeleteDialog = new ResolutionDeleteDialog();
		resolutionDeleteDialog.setModal(true);
		resolutionDeleteDialog.setVisible(true);
		
		resolutionDeleteDialog.updateResolutionConfig();
		
		return resolutionDeleteDialog;
	}
	
	private void updateResolutionConfig() {
		// ��������
		resolutionConfig.clear();
		int tableRowCount = resolutionTable.getRowCount();
		for (int i = 0; i < tableRowCount; i++) {
			ConfigItem configItem = new ConfigItem(
					ResolutionSetPanel.RESOLUTION_CONFIG_ITEM_NAME, 
					resolutionTable.getValueAt(i, 1).toString());
			resolutionConfig.addConfigItem(configItem);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == finishButton) {
			setVisible(false);
		} else if (e.getSource() == deleteButton) {
			// ��ȡ��ѡ�е�ID
			int selectedIndex = resolutionTable.getSelectedRow();
			resolutionTableModel.removeRow(selectedIndex);
			
			// �����µ�ѡ��״̬
			if (selectedIndex >= resolutionTable.getRowCount()) {
				selectedIndex = resolutionTable.getRowCount() - 1;
			}
			
			updateResolutionConfig();
			initResolutionTable();
			resolutionTable.setRowSelectionInterval(selectedIndex, selectedIndex);
		}
	}
}
