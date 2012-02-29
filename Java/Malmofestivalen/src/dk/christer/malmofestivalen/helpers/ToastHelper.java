package dk.christer.malmofestivalen.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.widget.Toast;
import dk.christer.malmofestivalen.R;
import dk.christer.malmofestivalen.Settings;

public class ToastHelper {
	
	public static void ShowToastOnce(Context context, String key, String text) {
		PackageInfo pi;
		try {
			pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        String versionName = pi.versionName;
	
	        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
	        
	        String toastkey = versionName + key;
	        
	        if (!pref.getBoolean(toastkey, false)) {
            	pref.edit().putBoolean(toastkey, true).commit();
            	Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	        }
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
