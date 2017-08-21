package cn.hisdar.touchpaneltool.ui.control;

import java.awt.image.BufferedImage;

import cn.hisdar.touchpaneltool.ui.control.TouchShowBackGround.TouchShowBackgroundType;

public interface TouchShowBackgroundChangeListener {

	public void touchShowBackgroundChangeEvent(BufferedImage backgroundImage);
	public void touchShowBackgroundTypeChangeEvent(TouchShowBackgroundType backgroundType);
}
