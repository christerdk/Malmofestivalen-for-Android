package dk.christer.malmofestivalen;

import dk.christer.malmofestivalen.analytics.GoogleAnalyticsWrapper;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import dk.christer.malmofestivalen.data.CategoriesProvider;
import dk.christer.malmofestivalen.data.SceneProvider;

public class CategoriesActivity extends ListActivity {

	Cursor mCursor;
	GoogleAnalyticsWrapper _tracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scenelist);

		setupAnalytics();
		getListView().setBackgroundColor(Color.WHITE);
		getListView().setCacheColorHint(Color.WHITE);
		
		setupItemClickEventHandler();
		
		createCategoryList();
		setTitle(R.string.startcategories);
	}



	private void createCategoryList() {
        if (mCursor != null) {
            stopManagingCursor(mCursor);
        }
		mCursor = getContentResolver().query(CategoriesProvider.CONTENT_URI_CATEGORIES, null, null, null, null);
        startManagingCursor(mCursor);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
        		this, 
                R.layout.scenelistrow,  
                mCursor,                
                new String[] {CategoriesProvider.CATEGORY_KEY_TITLE},           
                new int[] {R.id.scenerowtitle}); 

        setListAdapter(adapter);
	}



	private void setupItemClickEventHandler() {
		getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
                _tracker.trackClick("/click/categories/category");
                Intent intent = new Intent(CategoriesActivity.this, EventsForCategoryActivity.class);
                Cursor cursor = (Cursor) arg0.getItemAtPosition(arg2);
                String categoryId = cursor.getString(cursor.getColumnIndex(CategoriesProvider.CATEGORY_KEY_CATEGORYID));
                intent.putExtra(EventsForCategoryActivity.EXTRA_CATEGORYID, categoryId);
                startActivity(intent);
            }
        });
	}

	private void setupAnalytics() {
		_tracker = GoogleAnalyticsWrapper.getInstance();
		 _tracker.trackPageView("/view/categories");
	}

}
