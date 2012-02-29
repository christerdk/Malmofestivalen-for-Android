package dk.christer.malmofestivalen;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import dk.christer.malmofestivalen.analytics.GoogleAnalyticsWrapper;
import dk.christer.malmofestivalen.data.CategoriesProvider;
import dk.christer.malmofestivalen.data.EventProvider;
import dk.christer.malmofestivalen.data.FestivalDBHelper;
import dk.christer.malmofestivalen.data.LinksProvider;
import dk.christer.malmofestivalen.data.SceneProvider;
import dk.christer.malmofestivalen.net.BinaryLoader;
import dk.christer.malmofestivalen.services.DBFileService;

public class EventDetailActivity extends Activity {
	//private static final String TAG = "EventDetailActivity";
    public final static String EXTRA_SCHEDULEID = "EXTRA_SCHEDULEID";
    int _eventID;
    DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    DateFormat shortTime = new SimpleDateFormat("HH:mm");
    DateFormat shortday = new SimpleDateFormat("d");

    private String _title = "";
    private String _description = "";
    private String _linkSpotify = null;
    private String _linkYoutube = null;
    private String _linkMyspace = null;
    private String _linkReadMore = null;
    private String _linkOriginal = "";
    private String _sceneTitle = "";
    private String _favoriteId = "";
    private String _uriImage = "";
    private String _actId = "";
    private Boolean _isFavorite = false;
    private String _sceneid = "";
    private Date _startDate = null;
    private Date _endDate = null;
    private ProgressBar _actImageProgress;
    ImageView _actImage = null;
    private String _categories = "";
    FavoriteManager _favManager;

    GoogleAnalyticsWrapper _tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.eventdetail);

        _eventID = (int) getIntent().getExtras().getInt(EXTRA_SCHEDULEID);
        _favManager = new FavoriteManager(this);

        
        _actImage = (ImageView)findViewById(R.id.actimage);
        loadEventInformation();
        loadFavoriteState();
        _actImageProgress = (ProgressBar)EventDetailActivity.this.findViewById(R.id.actimageprogressbar);
        

        setupAnalytics();

        BindTexts();
        BindFavoriteIndicator();

        SetFavoriteEventHandler();
        SetMoreInfoEventHandler();
        SetMediaGroupState();
        SetWebGroupState();
        SetupShareEvent();
        BindImage();
    }

    private void BindImage() {
    	
    	ImageView actImage = (ImageView)findViewById(R.id.actimage);
    	if (_uriImage.length() == 0) {
    		actImage.setVisibility(View.GONE);
    	}
    	else {
    		new BindImageAsync().execute(_uriImage); 
    	}
    }

    
    protected class BindImageAsync extends AsyncTask<String, Object, Bitmap> 
	{
		@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		setProgressBarIndeterminateVisibility(true);
    		EventDetailActivity.this._actImageProgress.setVisibility(View.VISIBLE);
    	}
		
    	@Override
    	protected Bitmap doInBackground(String... arg0) {
			
			return getImageBitmap(arg0[0]);
    	}
    	
    	
    	@Override
    	protected void onPostExecute(Bitmap result) {
    		// TODO Auto-generated method stub
    		super.onPostExecute(result);
    		setProgressBarIndeterminateVisibility(false);
    		EventDetailActivity.this._actImageProgress.setVisibility(View.GONE);
    		if (result != null) {
    			EventDetailActivity.this._actImage.setImageBitmap(result);
    		}
    	}
    }
    
    
    
    
    
    private void setupAnalytics() {
        _tracker = GoogleAnalyticsWrapper.getInstance();
        _tracker.trackPageView("/view/eventdetail");
        _tracker.trackClick("/data/event/viewed/" + _favoriteId);
    } 

    private void loadEventInformation() {
        Uri uri = Uri.withAppendedPath(EventProvider.CONTENT_URI_EVENTS_BY_EVENT_ID, String.valueOf(_eventID));
        Cursor eventcursor = getContentResolver().query(uri, null, null, null, null);
        if (eventcursor != null) {
            if (eventcursor.moveToNext()) {

                _title = eventcursor.getString(eventcursor.getColumnIndex(EventProvider.EVENT_KEY_TITLE));
                _description = eventcursor.getString(eventcursor.getColumnIndex(EventProvider.EVENT_KEY_DESCRIPTION));
                _linkOriginal = eventcursor.getString(eventcursor.getColumnIndex(EventProvider.EVENT_KEY_LINKORIGINAL));
                _sceneid = eventcursor.getString(eventcursor.getColumnIndex(EventProvider.EVENT_KEY_SCENEID));
                _favoriteId = eventcursor.getString(eventcursor.getColumnIndex(EventProvider.EVENT_KEY_BUSINESSID));
                _actId = eventcursor.getString(eventcursor.getColumnIndex(EventProvider.EVENT_KEY_ACTID));

                int uriImageIndex = eventcursor.getColumnIndex(EventProvider.EVENT_KEY_URIIIMAGE);
                if (uriImageIndex >= 0) {
                	_uriImage = eventcursor.getString(eventcursor.getColumnIndex(EventProvider.EVENT_KEY_URIIIMAGE));
                }
                
                String startDate = eventcursor.getString(eventcursor.getColumnIndex(EventProvider.EVENT_KEY_STARTDATE));
                String endDate = eventcursor.getString(eventcursor.getColumnIndex(EventProvider.EVENT_KEY_ENDDATE));
                try {
                    _startDate = iso8601Format.parse(startDate);
                    _endDate = iso8601Format.parse(endDate);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            eventcursor.close();
        }

        //Get links for act
        Uri uriLinks = Uri.withAppendedPath(LinksProvider.CONTENT_URI_LINS_BY_ACT_ID, String.valueOf(_actId));
        Cursor linkscursor = getContentResolver().query(uriLinks, null, null, null, null);
        String links = "";
        
        if (linkscursor != null) {
        	while (linkscursor.moveToNext()) {
        		String link = linkscursor.getString(1);
        		if (link.toLowerCase().indexOf("spotify.com") != -1) {
        			_linkSpotify = link;
        		}
        		else if (link.toLowerCase().indexOf("youtube.com/watch") != -1) {
        			_linkYoutube = link;
        		}
        		else if (link.toLowerCase().indexOf("myspace.com") != -1 ) {
        			_linkMyspace = link;
        		}
        	}
        	linkscursor.close();
        }
        
        //Get categories for act
        Uri uriCategories = Uri.withAppendedPath(CategoriesProvider.CONTENT_URI_CATEGORIES_FOR_ACT_ID, String.valueOf(_actId));
        Cursor categoriescursor = getContentResolver().query(uriCategories, null, null, null, null);

        if (categoriescursor != null) {
        	while( categoriescursor.moveToNext()) {
        		if (_categories.length() > 0) {
        			_categories += ", ";
        		}
        		_categories += categoriescursor.getString(categoriescursor.getColumnIndex(CategoriesProvider.CATEGORY_KEY_TITLE));
        	}
        	categoriescursor.close();
        }
        
        Cursor scenecursor = getContentResolver().query(
                Uri.withAppendedPath(SceneProvider.CONTENT_URI_SCENES_BY_SCENE_ID, _sceneid),
                null, null, null, null);
        if (scenecursor != null) {
            if (scenecursor.moveToNext()) {
                _sceneTitle = scenecursor.getString(scenecursor.getColumnIndex(SceneProvider.KEY_TITLE));
            }
            scenecursor.close();
        }
    }

    private void loadFavoriteState() {
        _isFavorite = _favManager.IsFavorite(_favoriteId);
    }

    private void BindTexts() {
        TextView title = (TextView) findViewById(R.id.eventdetailtitle);
        title.setText(_title);

        TextView description = (TextView) findViewById(R.id.eventdetaildescription);
        description.setText(_description);

        TextView startDate = (TextView) findViewById(R.id.eventdetailstarttime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(_startDate);
        int dayNbrStart = cal.get(cal.DAY_OF_WEEK);
        String dayOfWeek = DateUtils.getDayOfWeekString(dayNbrStart, DateUtils.LENGTH_LONG);

        startDate.setText(shortTime.format(_startDate) + ", " + dayOfWeek + " " + shortday.format(_startDate));

        cal.setTime(_endDate);
        int dayNbrEnd = cal.get(cal.DAY_OF_WEEK);
        String dayOfWeekEnd = DateUtils.getDayOfWeekString(dayNbrEnd, DateUtils.LENGTH_LONG);
        TextView endDate = (TextView) findViewById(R.id.eventdetailendtime);
        endDate.setText(shortTime.format(_endDate) + ", " + dayOfWeekEnd + " " + shortday.format(_endDate));

        TextView categoriesForAct = (TextView) findViewById(R.id.categoriesforact);
        categoriesForAct.setText(_categories);
        
        TextView scene = (TextView) findViewById(R.id.eventscenetitle);
        scene.setText(_sceneTitle);
    }


    private void BindFavoriteIndicator() {
        ImageView icon = (ImageView) findViewById(R.id.eventfavoriteicon);
        TextView text = (TextView) findViewById(R.id.eventfavoritetext);

        if (_isFavorite) {
            icon.setImageResource(R.drawable.heart);
            text.setText(getResources().getString(R.string.eventfavoriteexclamationmark));
        } else {
            icon.setImageResource(R.drawable.heartoff);
            text.setText(getResources().getString(R.string.eventfavoriteqestionmark));
        }
    }


    private void SetFavoriteEventHandler() {

        ImageView icon = (ImageView) findViewById(R.id.eventfavoriteicon);
        icon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (_isFavorite) {
                    _tracker.trackClick("/click/event/favorite-off");
                    _tracker.trackClick("/data/favorite/remove/" + _favoriteId);

                    _favManager.DeleteFavorite(_favoriteId);
                    _isFavorite = false;
                    Toast.makeText(EventDetailActivity.this, getResources().getString(R.string.eventfavoriteremoved), 2000).show();
                } else {
                    _tracker.trackClick("/click/event/favorite-on");
                    _tracker.trackClick("/data/favorite/add/" + _favoriteId);
                    Date alarmDate = FavoriteManager.GetAlarmDate(_startDate);
/*                    if (Settings.IsDebug) {
                    	alarmDate = new Date(System.currentTimeMillis() + (15 * 1000));//Test
                    }
*/                    _favManager.CreateFavorite(_favoriteId, alarmDate);
                    _isFavorite = true;
                    Toast.makeText(EventDetailActivity.this, getResources().getString(R.string.eventfavoriteadded), 2000).show();
                }
                BindFavoriteIndicator();
            }
        });

    }

    private void SetMoreInfoEventHandler() {
        LinearLayout linkKarta = (LinearLayout) findViewById(R.id.eventmap);
        linkKarta.setOnClickListener(new OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                _tracker.trackClick("/click/mapsingleview/event");
                Intent intent = new Intent(EventDetailActivity.this, ShowScenesOnMapActivity.class);
                intent.putExtra(ShowScenesOnMapActivity.MAP_SCENEID_KEY, _sceneid);
                startActivity(intent);
            }
        });
    }


    private void SetMediaGroupState() {
        ImageView linkSpotify = (ImageView) findViewById(R.id.event_spotify);
        ImageView linkYoutube = (ImageView) findViewById(R.id.event_youtube);
        ImageView linkMyspace = (ImageView) findViewById(R.id.event_myspace);
        if (_linkSpotify != null) {
            linkSpotify.setVisibility(View.VISIBLE);
            AddURLOpener(linkSpotify, _linkSpotify, "");

        }
        if (_linkYoutube != null) {
        	linkYoutube.setVisibility(View.VISIBLE);
        	AddURLOpener(linkYoutube, _linkYoutube, "");
        }
        if (_linkMyspace != null) {
            linkMyspace.setVisibility(View.VISIBLE);
            AddURLOpener(linkMyspace, _linkMyspace, "");
        }
    }

    private void SetWebGroupState() {
        LinearLayout linkWeb = (LinearLayout) findViewById(R.id.eventmore);
        ImageView linkReadMore = (ImageView) findViewById(R.id.event_web);

        if (_linkOriginal != null) {
            linkWeb.setVisibility(View.VISIBLE);
            AddURLOpener(linkWeb, _linkOriginal + "?android=true&a=more", "/click/weboriginal/event");
        }

        if (_linkReadMore != null) {
            linkReadMore.setVisibility(View.VISIBLE);
            AddURLOpener(linkReadMore, _linkReadMore, "");
        }
    }

    private void SetupShareEvent() {
        LinearLayout eventShare = (LinearLayout) findViewById(R.id.eventshare);
        eventShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                _tracker.trackClick("/click/share/event");
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, _title);
                String body = getText(R.string.eventsharebody).toString();
                body = body.replace("[artist]", _title).replace("[url]", _linkOriginal + "?android=true");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(shareIntent, getText(R.string.eventsharevia)));
            }
        });
    }


    private void AddURLOpener(View theview, final String url, final String tracking) {
        theview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                _tracker.trackClick(tracking);

                Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(urlIntent);
            }
        });
    }
    
    private Bitmap getImageBitmap(String url) { 
        Bitmap bm = null; 
        try { 
            URL aURL = new URL(url); 
            URLConnection conn = aURL.openConnection(); 
            conn.connect(); 
            InputStream is = conn.getInputStream(); 
            BufferedInputStream bis = new BufferedInputStream(is); 
            bm = BitmapFactory.decodeStream(bis); 
            bis.close(); 
            is.close(); 
       } catch (IOException e) { 
           Log.e("", "Error getting bitmap", e); 
       } 
       return bm; 
    } 
}
