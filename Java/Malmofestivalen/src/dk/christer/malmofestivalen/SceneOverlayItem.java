package dk.christer.malmofestivalen;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class SceneOverlayItem extends OverlayItem {

	public String SceneID;
	
	public SceneOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

}
