package cn.hisdar.touchpaneltool.ui.output;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Vector;

import cn.hisdar.lib.log.HLog;

public class TimeTrackImage {

	public TimeTrackImage() {
		
	}
	
	public static int getTimeTrackActionTime(TimeTrackAction timeTrackAction, float resolution) {
		double actionItemWidth = 0;
		actionItemWidth = (int)((timeTrackAction.getEndTime().bootUpTime - timeTrackAction.getStartTime().bootUpTime) * 1.0 / resolution);
		if (actionItemWidth < 0 && actionItemWidth > -10) {
			actionItemWidth = 5;
		}
		
		actionItemWidth = actionItemWidth * 1000 / resolution;
		
		return (int)actionItemWidth;
	}
	
	public static int getTimeTweTrackActionTime(TimeTrackAction timeTrackAction, TimeTrackAction lastTimeTrackAction, float resolution) {
		double actionItemWidth = 0;
		
		HLog.il("getTimeTweTrackActionTime:" +(timeTrackAction.getStartTime().bootUpTime - lastTimeTrackAction.getEndTime().bootUpTime));
		actionItemWidth = timeTrackAction.getStartTime().bootUpTime - lastTimeTrackAction.getEndTime().bootUpTime;
		// 有的时候，两个点之间的时间会有一点错误，但是不会太多，如果负的不太多，就直接当成正常的对待
		// 开机过程最少要20S，当两个点之间的误差小于10S的时候，认为是正常的误差
		if (actionItemWidth < 0 && actionItemWidth > -10) {
			actionItemWidth = 5;
		} else if (actionItemWidth < 0) {
			HLog.il("getTimeTweTrackActionTime: us start up time");
			actionItemWidth = (int)(timeTrackAction.getStartTime().bootUpTime * 1.0 / resolution);
		}
		
		if (actionItemWidth <= 0) {
			actionItemWidth = 5;
		}
		
		actionItemWidth = actionItemWidth * 1000 / resolution;
		
		return (int)actionItemWidth;
	}
	
	public static BufferedImage getTimeTrackImage(int width, int height, int indexOfImage, Vector<TimeTrackAction> actions, float resolution) {
		BufferedImage timeTrackImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		HLog.il("getTimeTrackImage: actions size:" + actions.size());
		// 计算开始绘制的Action
		int startAction = 0;
		int drawWidth = 0;
		int drawStartX = 0;
		int actionItemWidth = 0;
		TimeTrackAction currentTimeTrackAction = null;
		TimeTrackAction lastTimeTrackAction = null;
		for (int i = actions.size() - 1; i > 0; i--) {
			currentTimeTrackAction = actions.get(i);

			// 当前操作占用的时间
			actionItemWidth = getTimeTrackActionTime(currentTimeTrackAction, resolution);
			drawWidth += actionItemWidth;
			HLog.il("getTimeTrackImage: current width:" + actionItemWidth);
			
			// 计算当前间隔和上一个item的间隔时间
			lastTimeTrackAction = actions.get(i - 1);
			actionItemWidth = getTimeTweTrackActionTime(currentTimeTrackAction, lastTimeTrackAction, resolution);
			drawWidth += actionItemWidth;
			HLog.il("getTimeTrackImage: space width:" + actionItemWidth);
			HLog.il("getTimeTrackImage: drawWidth width:" + drawWidth);
			HLog.il("getTimeTrackImage: width:" + width);
			
			if (drawWidth >= width) {
				startAction = i;
				drawStartX = width - drawWidth;
				break;
			}
		}
		
		HLog.il("getTimeTrackImage: startAction:" + startAction);
		HLog.il("getTimeTrackImage: drawStartX=:" + drawStartX);

		Graphics graphics = timeTrackImage.getGraphics();
		for (int i = startAction; i < actions.size(); i++) {
			currentTimeTrackAction = actions.get(i);
			graphics.setColor(currentTimeTrackAction.getEventColor());
			actionItemWidth = getTimeTrackActionTime(currentTimeTrackAction, resolution);
			HLog.il("getTimeTrackImage: actionItemWidth:" + actionItemWidth);
			graphics.fillRect(drawStartX, height / 2, actionItemWidth, height / 2);
			graphics.setColor(Color.RED);
			graphics.drawRect(drawStartX, height / 2, actionItemWidth, height / 2);
			drawStartX += actionItemWidth;
			
			if (i < actions.size() - 1) {
				lastTimeTrackAction = actions.get(i + 1);
				graphics.setColor(Color.GREEN);
				actionItemWidth = getTimeTweTrackActionTime(currentTimeTrackAction, lastTimeTrackAction, resolution);
				HLog.il("getTimeTrackImage: actionItemWidth:" + actionItemWidth);
				graphics.fillRect(drawStartX, height / 2, actionItemWidth, height / 2);
				graphics.setColor(Color.RED);
				graphics.drawRect(drawStartX, height / 2, actionItemWidth, height / 2);
				drawStartX += actionItemWidth;
			}
			
			HLog.il("getTimeTrackImage: drawWidth:" + drawStartX + ", width=" + width);
		}
		
		return timeTrackImage;
	}
}
