package dk.christer.malmofestivalen.receivers;

import dk.christer.malmofestivalen.FavoriteManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class EventAlarmReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			FavoriteManager man = new FavoriteManager(context);
			man.HandleAlarm(intent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
