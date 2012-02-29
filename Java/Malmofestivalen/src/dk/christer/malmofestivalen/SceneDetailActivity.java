package dk.christer.malmofestivalen;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import dk.christer.malmofestivalen.analytics.GoogleAnalyticsWrapper;
import dk.christer.malmofestivalen.data.EventProvider;
import dk.christer.malmofestivalen.data.SceneProvider;


public class SceneDetailActivity extends MapActivity {
	public final static String ITEM_TITLE = "title";  
	public final static String ITEM_ID = "_id";
	public static final String EXTRA_SCENE_ID = "EXTRA_SCENE_ID";
	
	private int _sceneRowId; 
	private String _sceneId = "";
	private String _title = "";
	private int _latitude1E6;
	private int _longtitude1E6;
	
	private MapView _map;
	private MapController _controller;
	List<Overlay> _mapOverlays;
	SeparatedListAdapter sepadapterActivity; 
	ArrayList<Section> _eventsListData = new ArrayList<Section>();

	DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat timeFormat = new SimpleDateFormat("HH:mm");
	DateFormat dateFormat = new SimpleDateFormat("d");
	
	GoogleAnalyticsWrapper _tracker;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.scenedetail);
		setupAnalytics();

		_sceneId = this.getIntent().getExtras().getString(EXTRA_SCENE_ID);
		
		new LoadSceneText().execute(this);

		
		SetupMapButton();

	}

	private void setupAnalytics() {
		_tracker = GoogleAnalyticsWrapper.getInstance();
		 _tracker.trackPageView("/view/scenedetail");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void BindText() {
		TextView title = (TextView)findViewById(R.id.scenedetailtitle);
		title.setText(_title);
	}
	
	private void SetupMapButton() {
		LinearLayout linkKarta = (LinearLayout)findViewById(R.id.scenekarta);
		linkKarta.setOnClickListener(new OnClickListener(
				) {
			@Override
			public void onClick(View v) {
				_tracker.trackClick("/click/mapsingleview/scene");
				Intent intent = new Intent(SceneDetailActivity.this, ShowScenesOnMapActivity.class);
				intent.putExtra(ShowScenesOnMapActivity.MAP_SCENEID_KEY, _sceneId);
				startActivity(intent);
			}
		});
	}

	private void BindEventsList() {
		 ListView list = (ListView)findViewById(R.id.scenedetaillist);  
	     list.setAdapter(sepadapterActivity);
	     list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				_tracker.trackClick("/click/event/scene");

				Map gnu = (Map)sepadapterActivity.getItem(arg2);
				String id = (String)gnu.get(ITEM_ID);
				Intent eventDetailIntent = new Intent(SceneDetailActivity.this, EventDetailActivity.class);
				eventDetailIntent.putExtra(EventDetailActivity.EXTRA_SCHEDULEID, Integer.valueOf(id));
				startActivity(eventDetailIntent);
			}
		});
	}
	
	public Map<String,?> createItem(String title, String theid) {  
	        Map<String,String> item = new HashMap<String,String>();  
	        item.put(ITEM_TITLE, title);  
	        item.put(ITEM_ID, theid);
	        return item;  
	    }  
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	
	private Section GetSection(String sectionHeader) {
		for (Section section : _eventsListData) {
			if (sectionHeader.equals(section.SectionHeader)) {
				return section;
			}
		}
		return null;
	}
	
	private class Section 
	{
		public List<Map<String,?>> Items = new LinkedList<Map<String,?>>();
		public String SectionHeader = "";
	}
	
	protected class LoadSceneText extends AsyncTask<Context, Object, Object>
    {
		@Override
		protected Object doInBackground(Context... arg0) {
			// TODO Auto-generated method stub
			GetSceneInformation(arg0[0]);
			return null;
			
		}
		
		public void GetSceneInformation(Context context) {
            Cursor scenecursor = getContentResolver().query(
                    Uri.withAppendedPath(SceneProvider.CONTENT_URI_SCENES_BY_SCENE_ID, _sceneId),
                    null, null, null, null);
			if (scenecursor != null) {
				if (scenecursor.moveToNext()) {
					_title = scenecursor.getString(scenecursor.getColumnIndex(SceneProvider.KEY_TITLE));
					_sceneId = scenecursor.getString(scenecursor.getColumnIndex(SceneProvider.KEY_SCENE_ID));
					_latitude1E6 = scenecursor.getInt(scenecursor.getColumnIndex(SceneProvider.KEY_LATITUDE));
					_longtitude1E6 = scenecursor.getInt(scenecursor.getColumnIndex(SceneProvider.KEY_LONGTITUDE));
				}
				scenecursor.close();
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			BindText();
			new LoadSceneEventList().execute(SceneDetailActivity.this);
			
		}
    }
	
	

	protected class LoadSceneEventList extends AsyncTask<Context, Integer, SeparatedListAdapter>
    {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
            setProgressBarIndeterminateVisibility(true);
		}
		
		@Override
		protected SeparatedListAdapter doInBackground(Context... arg0) {
			Context context = arg0[0];
			CreateEventData(context);
			SeparatedListAdapter sepadapterActivity = new SeparatedListAdapter(context);  
		        for (Section section : _eventsListData) {
		        	sepadapterActivity.addSection(section.SectionHeader, new SimpleAdapter(context, section.Items, R.layout.list_item, new String[] { ITEM_TITLE }, new int[] { R.id.list_item_title}));	
				}
		    return sepadapterActivity;
		}

		
		private void CreateEventData(Context context) {
            Uri uri = Uri.withAppendedPath(EventProvider.CONTENT_URI_EVENTS_BY_SCENE_ID, _sceneId);

			Cursor eventscursor = context.getContentResolver().query(uri, null, null, null, null);

			if (eventscursor != null) {
				Calendar cal = Calendar.getInstance();
				while (eventscursor.moveToNext()) {
					try {
						String startDateTimeString = eventscursor.getString(eventscursor.getColumnIndex(EventProvider.EVENT_KEY_STARTDATE));
						Date startDate = new Date();
						startDate = iso8601Format.parse(startDateTimeString);

						//String endDateTimeString = eventscursor.getString(eventscursor.getColumnIndex(ScenesDatabaseAdapter.EVENT_KEY_ENDDATE));
						//Date endDate = new Date();
						//endDate= iso8601Format.parse(endDateTimeString);
						

						cal.setTime(startDate);
						int dayNbrStart = cal.get(cal.DAY_OF_WEEK);
						String dayOfWeek = DateUtils.getDayOfWeekString(dayNbrStart, DateUtils.LENGTH_LONG);
						
						
						String key = dayOfWeek + " d. " + dateFormat.format( startDate);
						Section section = GetSection(key);
						if (section == null) {
							section = new Section();
							section.SectionHeader = key;
							_eventsListData.add(section);
						}
						String id = Integer.toString(eventscursor.getInt(eventscursor.getColumnIndex(BaseColumns._ID)));
						section.Items.add(createItem(timeFormat.format(startDate) + " " + eventscursor.getString(eventscursor.getColumnIndex(SceneProvider.KEY_TITLE)), id));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
				}
				eventscursor.close();
			}
		}

		
		@Override
		protected void onPostExecute(SeparatedListAdapter result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			sepadapterActivity = result;
			BindEventsList();
            setProgressBarIndeterminateVisibility(false);
		}

    }
	
	
	
	
}
