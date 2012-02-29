package dk.christer.malmofestivalen.receivers;

import dk.christer.malmofestivalen.FavoriteManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		try {
			Log.i("BootReceiver.onReceive", "Called...");
			new FavoriteManager(arg0).AlignFavorites();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("BootReceiver.onReceive", e.getMessage());
			e.printStackTrace();
			
		}
	}
}
