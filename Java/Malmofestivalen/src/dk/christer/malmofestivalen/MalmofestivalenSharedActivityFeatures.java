package dk.christer.malmofestivalen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;

public class MalmofestivalenSharedActivityFeatures extends Activity {

	public static void setMiljoparkeringBannerBehavior(final Activity act) {
		View banner = act.findViewById(R.id.miljoparkeringbanner);
		if (banner != null) {
			banner.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String url = "http://www.miljoparkering.se?r=malmofestivalenapp";

					if (Settings.IsDebug) {
						url += "debug";
					}
					
					Intent viewMiljoparkering = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					act.startActivity(viewMiljoparkering);
				}
			});
		}
	}
	
}
