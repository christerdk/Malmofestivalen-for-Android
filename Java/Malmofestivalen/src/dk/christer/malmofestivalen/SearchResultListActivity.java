package dk.christer.malmofestivalen;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import dk.christer.malmofestivalen.analytics.GoogleAnalyticsWrapper;
import dk.christer.malmofestivalen.data.EventProvider;
import dk.christer.malmofestivalen.helpers.DateHelper;

public class SearchResultListActivity extends ListActivity {

	public static final String EXTRA_SEARCHSTRING = "EXTRA_SEARCHSTRING";
	
	DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat simpleHourFormat = new SimpleDateFormat("HH:mm");
	DateFormat shortday = new SimpleDateFormat("d");

	
	Cursor mCursor;
	GoogleAnalyticsWrapper _tracker;
	
	String _searchString;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scenelist);
		setupAnalytics();
	 
		getListView().setBackgroundColor(Color.WHITE);
		getListView().setCacheColorHint(Color.WHITE);
		
		 if (getIntent().hasExtra(EXTRA_SEARCHSTRING)) {
			_searchString = getIntent().getStringExtra(EXTRA_SEARCHSTRING);
			setupListViewEventHandler();

			performSearch();
		 }
		 setTitle(getString(R.string.searchresult) + " - " + _searchString);
		 MalmofestivalenSharedActivityFeatures.setMiljoparkeringBannerBehavior(this);
	}

	private void performSearch() {
		try {
            String selectionString = EventProvider.generateFindSelection(_searchString);
          
            mCursor = getContentResolver().query(EventProvider.CONTENT_URI_FIND, null, selectionString, null, null);
            startManagingCursor(mCursor);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this, // Context.
		        R.layout.eventitemrow,  
		        mCursor,                
		        new String[] {EventProvider.EVENT_KEY_TITLE, EventProvider.EVENT_KEY_SCENETITLE, EventProvider.EVENT_KEY_STARTDATE},           
		        new int[] {R.id.eventitemrowtitle, R.id.eventitemrowscene, R.id.eventitemrowtimeresume});  
		
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor event, int columnIndex) {
				// TODO Auto-generated method stub
				
				if (view.getId() == R.id.eventitemrowtimeresume) {
					String dateString = DateHelper.createShortDateResume(event.getString(event.getColumnIndex(EventProvider.EVENT_KEY_STARTDATE)), event.getString(event.getColumnIndex(EventProvider.EVENT_KEY_ENDDATE)));
					((TextView)view).setText(dateString);
					return true;
				}
				return false;
			}
		});

		setListAdapter(adapter);
	}

	private void setupListViewEventHandler() {
		getListView().setOnItemClickListener(new OnItemClickListener() {

		 	@Override 
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
				_tracker.trackClick("/click/event/searchresult");
				Intent intent = new Intent(SearchResultListActivity.this, EventDetailActivity.class);
				intent.putExtra(EventDetailActivity.EXTRA_SCHEDULEID, (int)id);
				startActivity(intent);
			}
		});
	}

	private void setupAnalytics() {
		_tracker = GoogleAnalyticsWrapper.getInstance();
		 _tracker.trackPageView("/view/searchresult");
	}
	

	
	@Override
	protected void onDestroy() {
		stopManagingCursor(mCursor);
        
		super.onDestroy();
	}	
	
}
