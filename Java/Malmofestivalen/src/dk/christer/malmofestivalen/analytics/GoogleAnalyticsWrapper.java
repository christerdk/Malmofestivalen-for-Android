package dk.christer.malmofestivalen.analytics;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import dk.christer.malmofestivalen.Settings;

public class GoogleAnalyticsWrapper {

	protected GoogleAnalyticsWrapper() {
	}
	
	public void trackPageView(String page) {
		try {
			GoogleAnalyticsTracker.getInstance().trackPageView(page);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void trackClick(String click) {
		trackPageView(click);
	}
	
	public void StartTracking(Context context) {
		try {
			GoogleAnalyticsTracker.getInstance().start("UA-1041743-2", 15, context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void StopTracking() {
		try {
			GoogleAnalyticsTracker.getInstance().stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static GoogleAnalyticsWrapper getInstance() {
		if (!Settings.IsDebug) {
			return new GoogleAnalyticsWrapper();
		} else {
			return new DebugGoogleAnalyticsWrapper();
		}
	}
}
