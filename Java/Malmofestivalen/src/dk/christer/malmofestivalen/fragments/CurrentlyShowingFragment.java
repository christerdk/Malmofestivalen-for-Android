package dk.christer.malmofestivalen.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import dk.christer.malmofestivalen.EventDetailActivity;
import dk.christer.malmofestivalen.R;
import dk.christer.malmofestivalen.adapters.UpcomingCursorAdapter;
import dk.christer.malmofestivalen.analytics.GoogleAnalyticsWrapper;
import dk.christer.malmofestivalen.loaders.UpcomingSectionCursorLoader;
import dk.christer.malmofestivalen.data.EventProvider;
import dk.christer.malmofestivalen.data.SceneProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CurrentlyShowingFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public final static String ITEM_TITLE = "title";
    public final static String ITEM_ID = "_id";

    ArrayList<Section> _eventsList = new ArrayList<Section>();

    DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat timeFormat = new SimpleDateFormat("HH:mm");
    DateFormat shortday = new SimpleDateFormat("d");

    GoogleAnalyticsWrapper _tracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.currentlyshowing, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupAnalytics(savedInstanceState);

        setListAdapter(new UpcomingCursorAdapter(getActivity().getApplicationContext()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);

        setupItemOnClickListener();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new UpcomingSectionCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ((CursorAdapter) getListAdapter()).swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        ((CursorAdapter) getListAdapter()).swapCursor(null);
    }

    private void setupAnalytics(Bundle savedInstanceState) {
        _tracker = GoogleAnalyticsWrapper.getInstance();

        if (savedInstanceState == null) {
            _tracker.trackPageView("/view/currentlyshowing");
        }
    }

    private void setupItemOnClickListener() {
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                _tracker.trackClick("/click/event/currentlyshowing");

                Cursor item = (Cursor) getListAdapter().getItem(arg2);

                String id = item.getString(item.getColumnIndex(ITEM_ID));

                Intent eventDetailIntent = new Intent(getActivity(), EventDetailActivity.class);
                eventDetailIntent.putExtra(EventDetailActivity.EXTRA_SCHEDULEID, Integer.valueOf(id));
                startActivity(eventDetailIntent);
            }
        });
    }

    public Map<String, ?> createConcertItem(String title, String theid) {
        Map<String, String> item = new HashMap<String, String>();
        item.put(ITEM_TITLE, title);
        item.put(ITEM_ID, theid);
        return item;
    }

    private Section GetSection(String sectionHeader) {
        for (Section section : _eventsList) {
            if (sectionHeader.equals(section.SectionHeader)) {
                return section;
            }
        }
        return createNewSection(sectionHeader);
    }

    private Section createNewSection(String key) {
        Section section;
        section = new Section();
        section.SectionHeader = key;
        _eventsList.add(section);
        return section;
    }


    private void createSectionsAndItems(Context context) {
        Calendar cal = Calendar.getInstance();
        Cursor scenesCursor = getActivity().getContentResolver().query(SceneProvider.CONTENT_URI_SCENES, null, null, null, null);

        if (scenesCursor != null) {
            while (scenesCursor.moveToNext()) {
                String sceneTitle = scenesCursor.getString(scenesCursor.getColumnIndex(SceneProvider.KEY_TITLE));

                Section section = GetSection(sceneTitle);

                int columnIndex = scenesCursor.getColumnIndex(SceneProvider.KEY_SCENE_ID);
                String sceneId = scenesCursor.getString(columnIndex);

                Uri uri = Uri.withAppendedPath(EventProvider.CONTENT_URI_UPCOMING_EVENTS_BY_SCENE_ID, sceneId);
                Cursor upcomingEventsCursor = getActivity().getContentResolver().query(uri, null, null, null, null);

                if (upcomingEventsCursor != null) {
                    while (upcomingEventsCursor.moveToNext()) {
                        try {
                            String startDateString = upcomingEventsCursor.getString(upcomingEventsCursor.getColumnIndex(EventProvider.EVENT_KEY_STARTDATE));
                            Date startDate = new Date();
                            startDate = iso8601Format.parse(startDateString);


                            cal.setTime(startDate);
                            int dayNbrStart = cal.get(cal.DAY_OF_WEEK);
                            String dayOfWeek = DateUtils.getDayOfWeekString(dayNbrStart, DateUtils.LENGTH_LONG);

                            String sceneRowId = Integer.toString(upcomingEventsCursor.getInt(upcomingEventsCursor.getColumnIndex(BaseColumns._ID)));
                            String itemText = timeFormat.format(startDate) + " " + upcomingEventsCursor.getString(upcomingEventsCursor.getColumnIndex(SceneProvider.KEY_TITLE)) + " (" + dayOfWeek + " d. " + shortday.format(startDate) + ")";
                            section.Items.add(createConcertItem(itemText, sceneRowId));
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    upcomingEventsCursor.close();
                }
            }
            scenesCursor.close();
        }
    }

    private class Section {
        public List<Map<String, ?>> Items = new LinkedList<Map<String, ?>>();
        public String SectionHeader = "";
    }
}
