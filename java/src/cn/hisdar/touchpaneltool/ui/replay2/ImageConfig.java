package cn.hisdar.touchpaneltool.ui.replay2;

import java.awt.Color;

public class ImageConfig {

	public Color trackLineColor  = Color.BLUE;
	public Color touchPointColor = Color.RED;
	public Color lastPointColor  = Color.RED;
	public Color backgroudColor  = Color.GRAY;
	public int   touchPointSize  = 4;
	public int   lastPointSize   = 8;
	
	@Override
	public String toString() {
		return "PreviewImageConfig [trackLineColor=" + trackLineColor
				+ ", touchPointColor=" + touchPointColor + ", lastPointColor="
				+ lastPointColor + ", backgroudColor=" + backgroudColor
				+ ", touchPointSize=" + touchPointSize + ", lastPointSize="
				+ lastPointSize + "]";
	}
}
