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
import dk.christer.malmofestivalen.data.SceneProvider;

public class SceneListActivity extends ListActivity {

	Cursor mCursor;
	GoogleAnalyticsWrapper _tracker;
	private static final String OPPOSITE_REPRESENTATION = "map";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scenelist);

		setupAnalytics();
		getListView().setBackgroundColor(Color.WHITE);
		getListView().setCacheColorHint(Color.WHITE);
		
		setupItemClickEventHandler();
		
		createSceneList();
		setTitle(R.string.startscener);
		MalmofestivalenSharedActivityFeatures.setMiljoparkeringBannerBehavior(this);

	}



	private void createSceneList() {
        if (mCursor != null) {
            stopManagingCursor(mCursor);
        }
		mCursor = getContentResolver().query(SceneProvider.CONTENT_URI_SCENES, null, null, null, null);
        startManagingCursor(mCursor);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
        		this, // Context.
                R.layout.scenelistrow,  // Specify the row template to use (here, two columns bound to the two retrieved cursor rows).
                mCursor,                                              // Pass in the cursor to bind to.
                new String[] {SceneProvider.KEY_TITLE},           // Array of cursor columns to bind to.
                new int[] {R.id.scenerowtitle});  // Parallel array of which template objects to bind to those columns.

        setListAdapter(adapter);
	}



	private void setupItemClickEventHandler() {
		getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
                _tracker.trackClick("/click/scene/scenes");
                Intent intent = new Intent(SceneListActivity.this, SceneDetailActivity.class);
                Cursor cursor = (Cursor) arg0.getItemAtPosition(arg2);
                String sceneId = cursor.getString(cursor.getColumnIndex(SceneProvider.KEY_SCENE_ID));


                intent.putExtra(SceneDetailActivity.EXTRA_SCENE_ID, sceneId);
                startActivity(intent);
            }
        });
	}



	private void setupAnalytics() {
		_tracker = GoogleAnalyticsWrapper.getInstance();
		 _tracker.trackPageView("/view/scenes");
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, getText(R.string.menu_entry_show_as_map).toString());
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Settings.saveWantedRepresentation(getBaseContext(), OPPOSITE_REPRESENTATION);
		startActivity(new Intent(this, ShowScenesOnMapActivity.class));
		return super.onOptionsItemSelected(item);
	}
}
