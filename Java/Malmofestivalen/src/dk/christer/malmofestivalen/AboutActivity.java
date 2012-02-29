package dk.christer.malmofestivalen;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import dk.christer.malmofestivalen.analytics.GoogleAnalyticsWrapper;

public class AboutActivity extends Activity {
	GoogleAnalyticsWrapper _tracker; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		setupAnalytics();
	    
		TextView developers = (TextView)findViewById(R.id.developers);
		developers.setText(Html.fromHtml((String)getText(R.string.developers)));
		developers.setMovementMethod(LinkMovementMethod.getInstance());
	 }

	private void setupAnalytics() {
		_tracker = GoogleAnalyticsWrapper.getInstance();
	    _tracker.trackPageView("/view/about");
	}
}
