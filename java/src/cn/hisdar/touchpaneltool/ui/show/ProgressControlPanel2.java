package cn.hisdar.touchpaneltool.ui.show;

import java.awt.BorderLayout;
import java.awt.Point;
import java.io.IOException;

import javax.swing.JPanel;

import cn.hisdar.MultiTouchEventParse.EventParseFinishListener;
import cn.hisdar.MultiTouchEventParse.EventParseProgressListener;
import cn.hisdar.MultiTouchEventParse.EventParser;
import cn.hisdar.MultiTouchEventParse.stream.AbstractEventInputStream;
import cn.hisdar.MultiTouchEventParse.stream.RandomAccessNotSupportException;
import cn.hisdar.MultiTouchEventParse.stream.StreamNotInitializedException;
import cn.hisdar.lib.log.HLog;
import cn.hisdar.lib.ui.hslider.HSlider;
import cn.hisdar.lib.ui.hslider.HSliderPressedListener;
import cn.hisdar.lib.ui.hslider.HSliderReleaseListener;
import cn.hisdar.lib.ui.hslider.HSliderValueChangeListener;

public class ProgressControlPanel2 extends JPanel
		implements EventParseProgressListener,
					HSliderValueChangeListener,
					HSliderReleaseListener,
					HSliderPressedListener, EventParseFinishListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5298546971726898557L;

	private HSlider progressSlider;
	private EventParser eventParser = null;
	
	public ProgressControlPanel2() {
		super();
		
		progressSlider = new HSlider();
		
		setLayout(new BorderLayout());
		
		add(progressSlider);
		
		eventParser = EventParser.getInstance();
		eventParser.addEventParseProgressListener(this);
		progressSlider.addValueChangeListener(this);
		progressSlider.addPressedListener(this);
		progressSlider.addReleaseListener(this);
		eventParser.addEventParseFinishListener(this);
	}

	@Override
	public void parseProgressEvent(double progress) {
		int progressvalue = (int)Math.rint(progress * 100);
		progressSlider.setValue(progressvalue);
	}

	@Override
	public void sliderValueChangeEvent(HSlider slider, long value) {
		
	}

	@Override
	public void parsedEvent(HSlider slider, Point location, long value) {
		eventParser.suspend();
	}

	@Override
	public void releaseEvent(HSlider slider, Point location, long value) {
		AbstractEventInputStream inputStream = eventParser.getMultiTouchInputStream();
		
		if (inputStream == null) {
			return;
		}
		
		long index = (long)(value * 1.0f / slider.getRange() * inputStream.getDataSize());
		
		HLog.il("ProgressControlPanel2.sliderValueChangeEvent(): set file index");
		try {
			
			inputStream.seek(index);
			eventParser.resume();
		} catch (IOException e) {
			HLog.el(e);
		} catch (StreamNotInitializedException e) {
			HLog.el(e);
		} catch (RandomAccessNotSupportException e) {
			HLog.el(e);
		}
	}

	@Override
	public void parseFinishEvent() {
		progressSlider.setValue(0);
	}
}
