package cn.hisdar.touchpaneltool;

import java.awt.SplashScreen;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import cn.hisdar.lib.log.HLog;

public class Welcome {

	public void showWelcomeImage() {
		SplashScreen splash = SplashScreen.getSplashScreen();
		if (splash == null) {
			System.err.println("no sp");
			return;
		}
		
		try {
			//URL imageUrl = getClass().getResource("./Image/welcomImage.png");
			//System.err.println(Welcome.class.getResource("/").getPath());
			URL imageUrl = new URL(Welcome.class.getResource("/"), "../Image/welcomeImage.png");
			//System.err.println(Welcome.class.getResource("/").getPath());
			//System.err.println(imageUrl.getPath());
			if (imageUrl != null) {
				splash.setImageURL(imageUrl);
				splash.update();
			}
			
		} catch (NullPointerException | IllegalStateException | IOException e) {
			HLog.el(e);
		}
	}
}
