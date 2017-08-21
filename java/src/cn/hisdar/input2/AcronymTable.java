package cn.hisdar.input2;

import java.util.HashMap;

public class AcronymTable {

	public HashMap<String, String> acronymTable;
	
	public AcronymTable() {
		acronymTable = new HashMap<String, String>();
		
		acronymTable.put("P",                   "ABS_MT_PRESSURE");
		acronymTable.put("X",                   "ABS_MT_POSITION_X");
		acronymTable.put("Y",                   "ABS_MT_POSITION_Y");
		acronymTable.put("S",                   "SYN_REPORT");
		acronymTable.put("T",                   "ABS_MT_TRACKING_ID");
		acronymTable.put("KEY_POWER",           "KEY_POWER");
		acronymTable.put("ABS_DISTANCE",        "ABS_DISTANCE");
		acronymTable.put("ABS_MT_SLOT",         "ABS_MT_SLOT");
		acronymTable.put("ABS_MT_TOUCH_MINOR",  "ABS_MT_TOUCH_MINOR");
		acronymTable.put("ABS_MT_TOUCH_MAJOR",  "ABS_MT_TOUCH_MAJOR");
		acronymTable.put("ABS_MT_WIDTH_MAJOR",  "ABS_MT_WIDTH_MAJOR");
		acronymTable.put("ABS_MT_WIDTH_MINOR",  "ABS_MT_WIDTH_MINOR");
		acronymTable.put("ABS_MT_ORIENTATION",  "ABS_MT_ORIENTATION");
		acronymTable.put("ABS_MT_TOOL_TYPE",    "ABS_MT_TOOL_TYPE");
		acronymTable.put("ABS_MT_BLOB_ID",      "ABS_MT_BLOB_ID");
		acronymTable.put("ABS_MT_DISTANCE",     "ABS_MT_DISTANCE");
		acronymTable.put("SYN_MT_REPORT",       "SYN_MT_REPORT");
		acronymTable.put("SYN_MT_REPORT",       "SYN_MT_REPORT");
		acronymTable.put("BTN_TOUCH",           "BTN_TOUCH");
	}
	
	public String getFullName(String acronymName) {
		return acronymTable.get(acronymName);
	}
}
