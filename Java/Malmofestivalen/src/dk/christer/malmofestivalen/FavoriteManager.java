package dk.christer.malmofestivalen;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;
import dk.christer.malmofestivalen.data.EventProvider;
import dk.christer.malmofestivalen.data.FavoritesProvider;
import dk.christer.malmofestivalen.data.SceneProvider;
import dk.christer.malmofestivalen.receivers.EventAlarmReceiver;

public class FavoriteManager {

	DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat simpleHourFormat = new SimpleDateFormat("HH:mm");

	Context _context;
	AlarmManager _alarmManager;

	public static String ALARM_BUSINESSID = "ALARM_BUSINESSID";

	ImportInspectListener _importInspectListener;

	public FavoriteManager(Context context) {
		_context = context;
	}

	private AlarmManager GetAlarmManager() {
		if (_alarmManager == null) {
			_alarmManager = (AlarmManager)_context.getSystemService(Activity.ALARM_SERVICE);
		}
		return _alarmManager;
	}


	public boolean IsFavorite(String businessId) {
        Uri uri = Uri.withAppendedPath(FavoritesProvider.CONTENT_URI_FAVORITES, businessId);
		Cursor favcursor = _context.getContentResolver().query(uri, null, null, null, null);
		Boolean result = favcursor.getCount() > 0;
		favcursor.close();
        return result;
	}

	public void DeleteFavorite(String businessId) {
        Uri uri = Uri.withAppendedPath(FavoritesProvider.CONTENT_URI_FAVORITES, businessId);
        
		int count = _context.getContentResolver().delete(uri, null, null);

		if (count > 0) {
			DestroyAlarm(businessId);
		}
	}

	public Uri CreateFavorite(String businessId, Date alarmDate) {
        String alarmTimeAsString = iso8601Format.format(alarmDate);
        
        ContentValues values = new ContentValues();
        values.put(FavoritesProvider.FAVORITE_KEY_BUSINESSID, businessId);
        values.put(FavoritesProvider.FAVORITE_KEY_ALARMTIME, alarmTimeAsString);

        Uri result = _context.getContentResolver().insert(FavoritesProvider.CONTENT_URI_FAVORITES, values);

		if (System.currentTimeMillis() < alarmDate.getTime()) {
			CreateAlarm(businessId, alarmDate);
		}

		return result;
	}

	private void CreateAlarm(String businessId, Date alarmDate) {
		try {
			Intent intent = new Intent(_context, EventAlarmReceiver.class);
			intent.putExtra(ALARM_BUSINESSID, businessId);
 
            // Using business id as action seems strange. This should be a constant or nothing at all
            // /Sebbe 2011-05-01
			intent.setAction(businessId);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, businessId.hashCode(), intent, 0);
			//PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, 10, intent, 0);
			GetAlarmManager().set(AlarmManager.RTC_WAKEUP, alarmDate.getTime(), pendingIntent);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		Intent intent = new Intent(_context, EventAlarmReceiver.class);
		intent.putExtra(ALARM_BUSINESSID, businessId);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, id, intent, 0);
		_alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 1000), pendingIntent);
*/
	}

	private void DestroyAlarm(String businessId) {
		try {
			Intent intent = new Intent(_context, EventAlarmReceiver.class);
			intent.putExtra(ALARM_BUSINESSID, businessId);
			intent.setAction(businessId);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, businessId.hashCode(), intent, 0);
			GetAlarmManager().cancel(pendingIntent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void HandleAlarm(Intent alarmIntent) {
        /**
         * TODO: The alarm managing seems to be in need of unit testing. There are too many things that can break.
         * // Sebastian 2011-05-08
         */
		try {
		
		String favoriteBusinessId = alarmIntent.getStringExtra(ALARM_BUSINESSID);
		String scenId = "";
		int id = 0;
		String title = null;
		String time = null;
		String scene = null;
		Cursor event = getEvent(favoriteBusinessId);
		Date startDate = new Date();
		if (event != null) {
			if (event.moveToNext()) {
				scenId = event.getString(event.getColumnIndex(EventProvider.EVENT_KEY_SCENEID));
				id = event.getInt(event.getColumnIndex(BaseColumns._ID));
				title = event.getString(event.getColumnIndex(EventProvider.EVENT_KEY_TITLE));

				Date endDate = new Date();
				try {
					startDate = iso8601Format.parse(event.getString(event.getColumnIndex(EventProvider.EVENT_KEY_STARTDATE)));
					endDate = iso8601Format.parse(event.getString(event.getColumnIndex(EventProvider.EVENT_KEY_ENDDATE)));
				}
				catch (Exception ex) {
				}
				time = simpleHourFormat.format(startDate) + " - " + simpleHourFormat.format(endDate);
			}
			event.close();
		}

        Cursor scen = _context.getContentResolver().query(Uri.withAppendedPath(SceneProvider.CONTENT_URI_SCENES_BY_SCENE_ID, scenId), null, null, null, null);
		if (scen != null) {
			if (scen.moveToNext())
			{
				scene = scen.getString(event.getColumnIndex(SceneProvider.KEY_TITLE));
			}
			scen.close();
		}

		if ((id == 0) || (scenId == "")) {
			Log.e("FavoriteManager.HandleAlarm", "id: " + Integer.toString(id) + " sendid: " + scenId + " for businessID: " + favoriteBusinessId);
			return;
		}

		NotificationManager manger = (NotificationManager)_context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon, title, startDate.getTime());
		Intent notificationIntent = new Intent(_context, EventDetailActivity.class);
		notificationIntent.setAction(favoriteBusinessId);
		notificationIntent.putExtra(EventDetailActivity.EXTRA_SCHEDULEID, id);
		PendingIntent contentIntent = PendingIntent.getActivity(_context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(_context, title, time + ", " + scene, contentIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_ALL;
		manger.notify(favoriteBusinessId.hashCode(), notification);
		}
		catch (Exception ex) {
			Log.e("FavoriteManager.HandleAlarm", ex.getMessage());
		}
	}

    private Cursor getEvent(final String businessId) {
        Uri uri = Uri.withAppendedPath(EventProvider.CONTENT_URI_EVENTS_BY_BUSINESS_ID, businessId);
        return _context.getContentResolver().query(uri, null, null, null, null);
    }

    public void AlignFavorites() {
		//Destroy all alarms
		DestroyAlarmsForAllFavorites();
		//Remove orphaned alarms
		RemoveAllOrphanedFavorites();
		//Align alarm dates from events
		AlignAllAlarmDatesInFavorites();
		//Create all alarms
		CreateAlarmsForAllFavorites();
	}

	public static Date GetAlarmDate(Date eventStartDate) {
		DateTime jodaDate = new DateTime(eventStartDate);
		DateTime alarmDateTime = jodaDate.minusMinutes(30);
		return alarmDateTime.toDate();
	}

	public void RemoveAllOrphanedFavorites() {
        //new Create emtpy delete list
        ArrayList<String> businessIdDeleteList = new ArrayList<String>();
        //Read favoriter
        Cursor favorites = _context.getContentResolver().query(
                FavoritesProvider.CONTENT_URI_FAVORITES, null, null, null, null);
        //run through all favorites
        while (favorites.moveToNext()) {
            String businessId = favorites.getString(favorites.getColumnIndex(FavoritesProvider.FAVORITE_KEY_BUSINESSID));
            Cursor event = getEvent(businessId);
            if (event != null) {
                //if concert does not exist, add to delete list
                if (!event.moveToNext()) {
                    businessIdDeleteList.add(businessId);
                }
                event.close();
            }
        }
        favorites.close();
		if (businessIdDeleteList.size() > 0) {
			//delete favorite
			for (String businessId : businessIdDeleteList) {
                DeleteFavorite(businessId);
			}
		}
	}


	public void AlignAllAlarmDatesInFavorites() {
		//new Create emtpy delete list
		ArrayList<Favorite> updateList = new ArrayList<Favorite>();
		//Read favoriter
		Cursor favorites = _context.getContentResolver().query(
                FavoritesProvider.CONTENT_URI_FAVORITES, null, null, null, null);
        //run through all favorites
        while (favorites.moveToNext()) {
            String businessId = favorites.getString(favorites.getColumnIndex(FavoritesProvider.FAVORITE_KEY_BUSINESSID));
            Cursor event = getEvent(businessId);
            if (event != null) {
                //if concert exists create alarm (and not in the past)
                if (event.moveToNext()) {

                    String startDateString = event.getString(event.getColumnIndex(EventProvider.EVENT_KEY_STARTDATE));
                    Date startDate = new Date();
                    try {
                        startDate = iso8601Format.parse(startDateString);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Date alarmDate = GetAlarmDate(startDate);
                    updateList.add(new Favorite(businessId, alarmDate));
                }
                event.close();
            }
        }
        favorites.close();

		for (Favorite updFavorit : updateList) {
            Uri uri = Uri.withAppendedPath(FavoritesProvider.CONTENT_URI_FAVORITES, updFavorit.BusinessId);

            ContentValues values = new ContentValues();
            String alarmTimeAsString = iso8601Format.format(updFavorit.AlarmDate);
            values.put(FavoritesProvider.FAVORITE_KEY_ALARMTIME, alarmTimeAsString);

            _context.getContentResolver().update(uri, values, null, null);
		}
	}

	private class Favorite {
		public Favorite(String businessId, Date alarmDate) {
			BusinessId = businessId;
			AlarmDate = alarmDate;
		}

		public String BusinessId;
		public Date AlarmDate;
	}


	public void DestroyAlarmsForAllFavorites() {
        //Read favorites
        Cursor favorites = _context.getContentResolver().query(
                FavoritesProvider.CONTENT_URI_FAVORITES, null, null, null, null);
        //run through all favorites
        while (favorites.moveToNext()) {
            String businessId = favorites.getString(favorites.getColumnIndex(FavoritesProvider.FAVORITE_KEY_BUSINESSID));
            DestroyAlarm(businessId);
        }
        favorites.close();
	}

	public void CreateAlarmsForAllFavorites() {
		//Read favorites
        Cursor favorites = _context.getContentResolver().query(
                FavoritesProvider.CONTENT_URI_FAVORITES, null, null, null, null);
		//run through all favorites
        while (favorites.moveToNext()) {
            String businessId = favorites.getString(favorites.getColumnIndex(FavoritesProvider.FAVORITE_KEY_BUSINESSID));
            String alarmDateString = favorites.getString(favorites.getColumnIndex(FavoritesProvider.FAVORITE_KEY_ALARMTIME));
            Date alarmDate = new Date();
            try {
                alarmDate = iso8601Format.parse(alarmDateString);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (System.currentTimeMillis() < alarmDate.getTime()) {
                CreateAlarm(businessId, alarmDate);
            }
        }
        favorites.close();
	}

	public int ImportFavorites(String[] actsToImport) {
		//Destroy all alarms
		DestroyAlarmsForAllFavorites();
		//Remove orphaned alarms
		ArrayList<Favorite>  _favoritesToImport = new ArrayList<Favorite>();

		try {
            // TODO: This should probably be changed to some nice select/join action to be more effective.
            
			for (int i = 0; i < actsToImport.length; i++) {

				if (_importInspectListener != null) {
					_importInspectListener.onInspect();
				}

				Boolean favoriteExists = false;
                Uri uri = Uri.withAppendedPath(FavoritesProvider.CONTENT_URI_FAVORITES, actsToImport[i]);
                Cursor favorite = _context.getContentResolver().query(uri, null, null, null, null);

				if (favorite.getCount() == 0) {
					Cursor event = getEvent(actsToImport[i]);
					if (event != null) {
						if (event.moveToNext()) {
							String startDateString = event.getString(event.getColumnIndex(EventProvider.EVENT_KEY_STARTDATE));
							Date startDate = new Date();
							try {
								startDate = iso8601Format.parse(startDateString);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Date alarmDate = GetAlarmDate(startDate);
							Favorite newFavorite = new Favorite(actsToImport[i], alarmDate);
							_favoritesToImport.add(newFavorite);
						}
						event.close();
					}
				}
                favorite.close();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Favorite favorite : _favoritesToImport) {
			CreateFavorite(favorite.BusinessId, favorite.AlarmDate);
		}


		//Create all alarms
		CreateAlarmsForAllFavorites();
		return _favoritesToImport.size();
	}


	public void SetOnImportInspectListener(ImportInspectListener importInspectListener) {
		_importInspectListener = importInspectListener;
	}

	/*

	//new Create emtpy delete list
	ArrayList<String> businessIdDeleteList = new ArrayList<String>();
	_favadapter.open();
	//Read favorites favoriter
	Cursor favorites = _favadapter.GetFavorites();
	//run through all favorites
	if (favorites != null) {
		_festivalAdapter.open();
		while (favorites.moveToNext())
		{
			int id = favorites.getInt(favorites.getColumnIndex(FavoriteDatabaseAdapter.KEY_ROWID));
			String businessId = favorites.getString(favorites.getColumnIndex(FavoriteDatabaseAdapter.FAVORITE_KEY_BUSINESSID));
			String alarmDateString = favorites.getString(favorites.getColumnIndex(FavoriteDatabaseAdapter.FAVORITE_KEY_ALARMTIME));
			Date alarmDate = new Date();
			try {
				alarmDate = iso8601Format.parse(alarmDateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//remove alarm
			DestroyAlarm(id, businessId);
			Cursor event = _festivalAdapter.GetEvent(businessId);
			if (event != null) {
				//if concert exists create alarm (and not in the past)
				if (event.moveToNext()) {

					if (new Date().getTime() < alarmDate.getTime()) {
						CreateAlarm(id, businessId, alarmDate);
					}
				}
				else {
					//else put in delete list
					businessIdDeleteList.add(businessId);
				}
				event.close();
			}
		}
		_festivalAdapter.close();
		favorites.close();
	}
	_favadapter.close();
	if (businessIdDeleteList.size() > 0) {
		//delete favorite
		for (String businessId : businessIdDeleteList) {
			DeleteFavorite(businessId);
		}
	}
	*/

	public interface ImportInspectListener {
		public void onInspect();
	}


	public void RemoveImportListener() {
		_importInspectListener = null;
	}

}
