package dk.christer.malmofestivalen;

import dk.christer.malmofestivalen.adapters.EventCursorAdapter;
import dk.christer.malmofestivalen.analytics.GoogleAnalyticsWrapper;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import dk.christer.malmofestivalen.data.CategoriesProvider;
import dk.christer.malmofestivalen.data.EventProvider;

public class EventsForCategoryActivity extends ListActivity {
    public final static String EXTRA_CATEGORYID = "EXTRA_CATEGORYID";
	
	Cursor mCursor;
	GoogleAnalyticsWrapper _tracker;
	String _categoryId = ""; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scenelist);

		_categoryId = getIntent().getExtras().getString(EXTRA_CATEGORYID);
		

		getListView().setBackgroundColor(Color.WHITE);
		getListView().setCacheColorHint(Color.WHITE);
		
		setupItemClickEventHandler();
		
		createEventsList();
		SetupTitle();
		setupAnalytics();
	}

	private void SetupTitle() {
        Uri categoryURI = Uri.withAppendedPath(CategoriesProvider.CONTENT_URI_CATEGORY_FOR_CATEGORY_ID, _categoryId);
        Cursor catCursor = getContentResolver().query(categoryURI, null, null, null, null);
        if (catCursor != null) {
        
        	if (catCursor.moveToFirst()) 
        	{
        		String title = catCursor.getString(catCursor.getColumnIndex(CategoriesProvider.CATEGORY_KEY_TITLE));
        		setTitle(title);
        	}
        	catCursor.close();
        }
	}

	private void createEventsList() {
        if (mCursor != null) {
            stopManagingCursor(mCursor);
        }
        Uri eventsForCategory = Uri.withAppendedPath(EventProvider.CONTENT_URI_EVENTS_BY_CATEGORY_ID, _categoryId);
        
		mCursor = getContentResolver().query(eventsForCategory, null, null, null, null);
        startManagingCursor(mCursor);
    	EventCursorAdapter adapter = new EventCursorAdapter(
				this, 
		        R.layout.eventitemrow,  
		        mCursor,                
		        new String[] {EventProvider.EVENT_KEY_TITLE, EventProvider.EVENT_KEY_SCENETITLE, EventProvider.EVENT_KEY_STARTDATE},
		        new int[] {R.id.eventitemrowtitle, R.id.eventitemrowscene, R.id.eventitemrowtimeresume});         
        setListAdapter(adapter);
	}
	
	private void setupItemClickEventHandler() {
		getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
                _tracker.trackClick("/click/category/event");
                Intent intent = new Intent(EventsForCategoryActivity.this, EventDetailActivity.class);
				intent.putExtra(EventDetailActivity.EXTRA_SCHEDULEID, (int)id);
				startActivity(intent);
            }
        });
	}

	private void setupAnalytics() {
		_tracker = GoogleAnalyticsWrapper.getInstance();
		 _tracker.trackPageView("/view/category/" + getTitle());
         _tracker.trackClick("/data/category/viewed/" + _categoryId);
	}
	
}
