package dk.christer.malmofestivalen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings {
	private static final String TAG = "Settings";
	public static final Boolean IsDebug = true; 
	private static final String OPT_SCENES = "scenes_representation";
	public static final String OPT_SCENES_DEFAULT = "map";//opposite = "list"
	//inspired by:
	//https://github.com/mirontoli/bobusos/blob/master/src/eu/chuvash/android/rovar/Settings.java
	private static SharedPreferences getPref(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		return p;
	}
	private static SharedPreferences.Editor getPrefEditor(Context context) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = p.edit();
		return editor;
	}
	public static String getWantedRepresentationOfScenes(Context context) {
		return getPref(context).getString(OPT_SCENES, OPT_SCENES_DEFAULT);
	}

	public static void saveWantedRepresentation(Context context,
			String representation) {
		boolean wentItWell = getPrefEditor(context).putString(OPT_SCENES,
				representation).commit();
		if (!wentItWell) {
			Log.e(TAG, "Couldn't save state to preferences");
		}
	}
}