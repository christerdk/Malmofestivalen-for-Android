package dk.christer.malmofestivalen;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import dk.christer.malmofestivalen.analytics.GoogleAnalyticsWrapper;
import dk.christer.malmofestivalen.data.EventProvider;
import dk.christer.malmofestivalen.data.FestivalDBHelper;
import dk.christer.malmofestivalen.data.FestivalProvider;
import dk.christer.malmofestivalen.data.MetadataProvider;
import dk.christer.malmofestivalen.helpers.ToastHelper;
import dk.christer.malmofestivalen.net.BinaryLoader;
import dk.christer.malmofestivalen.services.DBFileService;

public class StartActivity extends Activity implements OnKeyListener {
    /** Called when the activity is first created. */
	//UA-1041743-2
	GoogleAnalyticsWrapper _tracker;
	LinearLayout _downloadupdate;
	ProgressDialog _downloadDBProgressDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FestivalDBHelper.ReleaseHelper();
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        setupAnalytics();
        

        setupClickEventhandlers();
      	
      	setupSearchTextboxEventHandler();

      	ShowStartupScreen();
      	AnimateQuestionmark();
      	startBackgroundTasks();

      	//We simulate a reminder in debug mode
      	/*if (Settings.IsDebug) {
	      	Intent intent = new Intent();
	      	intent.putExtra(FavoriteManager.ALARM_BUSINESSID, "aa75fd9342a74ce6bd8a5b112feb1818190");
	      	FavoriteManager man = new FavoriteManager(this);
	      	man.HandleAlarm(intent);
      	}*/
    }

	private void setupSearchTextboxEventHandler() {
		EditText searchEditText = (EditText)findViewById(R.id.startsearchtext);
      	searchEditText.setOnKeyListener(this);
	}

	private void setupClickEventhandlers() {
		LinearLayout map = (LinearLayout)findViewById(R.id.map);
      	map.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent;
				String track;
				String scenesRepresentation = Settings.getWantedRepresentationOfScenes(StartActivity.this);
				if (scenesRepresentation.equals(Settings.OPT_SCENES_DEFAULT)) {
					intent = new Intent(StartActivity.this, ShowScenesOnMapActivity.class);
					track = "/click/map/start";
				}
				else {
					intent = new Intent(StartActivity.this, SceneListActivity.class);
					track = "/click/scenes/start";
				}
				startActivity(intent);
		        _tracker.trackClick(track);
			}
		});

      	LinearLayout now = (LinearLayout)findViewById(R.id.rightnow);
      	now.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(StartActivity.this, CurrentlyShowingActivity.class));
		        _tracker.trackClick("/click/rightnow/start");
			}
		});

      	LinearLayout favorites = (LinearLayout)findViewById(R.id.favorites);
      	favorites.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(StartActivity.this, FavoritesActivity.class));
		        _tracker.trackClick("/click/favorites/start");
			}
		});
      	
    	LinearLayout categories = (LinearLayout)findViewById(R.id.categories);
    	categories.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(StartActivity.this, CategoriesActivity.class));
		        _tracker.trackClick("/click/categories/start");
			}
		});

      	
      	
   /*   	
      	Button testbutton = (Button)findViewById(R.id.testbutton);
      	testbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
      	
     	*/
      	ImageView startSearch = (ImageView)findViewById(R.id.startsearch);
      	startSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_tracker.trackClick("/click/searchresult/start");
				StartSearch();
			}
		});
      	
      	
     	_downloadupdate = (LinearLayout)findViewById(R.id.downloadupdate);
     	_downloadupdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
	    		_downloadupdate.setVisibility(View.GONE);
				new DownloadDBUpdate().execute();
		        _tracker.trackClick("/click/downloadupdate");
			}
		});
      	
    	ImageView startshareicon = (ImageView)findViewById(R.id.startshareicon);
    	startshareicon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StartActivity.this.onShare(v);
			}
		});

    	TextView startsharetext = (TextView)findViewById(R.id.startsharetext);
    	startsharetext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StartActivity.this.onShare(v);
			}
		});

    	
      	ImageView about = (ImageView)findViewById(R.id.about);
      	about.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent about = new Intent(StartActivity.this, AboutActivity.class);
				startActivity(about);
			}
		});
      	
      	//http://www.facebook.com/pages/Malmofestivalen-mobile-mashup/147655378597283
      	View facebook = (View)findViewById(R.id.facebook);
      	facebook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//http://www.facebook.com/pages/Malmofestivalen-mobile-mashup/147655378597283
				_tracker.trackClick("/click/facebook/start");
				Intent viewFacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("http://touch.facebook.com/#!/profile.php?id=147655378597283"));
				                                                                //http://touch.facebook.com/#!/profile.php?id=147655378597283
				startActivity(viewFacebook);
			}
		});
	}
	
	public void onShare(View v) {
        _tracker.trackClick("/click/appshare/start");
		
		String url = "market://details?id=dk.christer.malmofestivalen";
		url = "http://market.android.com/search?q=pname:dk.christer.malmofestivalen";
		String body = getText(R.string.startshareappbody).toString();
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Malmöfestivalen for Android");
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Malmöfestivalen for Android: " + body + " " + url);
		startActivity(Intent.createChooser(shareIntent, getText(R.string.eventsharevia)));      

	}


	private void startBackgroundTasks() {
		DBFileService _dbFileService = new DBFileService(new BinaryLoader());
		if (!_dbFileService.IsDatabasePresent())
		{
			new CopyStandardDBTask().execute((Object)null);
		}
		else if(!CanReadFromDB()) {
			new CopyStandardDBTask().execute((Object)null);
		} 
		else {
			new CheckForDBUpdateTask().execute((Object)null);
		}
	}
	
	private Boolean CanReadFromDB() {
		Boolean result = true;
		try {
			long versionNumber = GetDBVersionNumber();
		}
		catch (Exception ex) {
			result = false;
		}
		return result;
	}
	
	
	
	private void setupAnalytics() {
		_tracker = GoogleAnalyticsWrapper.getInstance();
        _tracker.StartTracking(this);
        _tracker.trackPageView("/view/start");
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
      	//new ShowWebText().execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, R.string.importmenutext);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(StartActivity.this, ImportActivity.class));
		return super.onOptionsItemSelected(item);
	}
	
	private void AnimateQuestionmark() {
	       Animation hyper = AnimationUtils.loadAnimation(this, R.anim.enter);
	        ImageView about = (ImageView)findViewById(R.id.about);
	        about.setAnimation(hyper);
	}

	public void ShowStartupScreen() {
	      try { 
	            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0); 
	            String versionName = pi.versionName;
	            String whatsNewTitle = getText(R.string.whatnewtitle).toString().replace("[versionname]", versionName);
	            String whatsNewDescription = getText(R.string.whatsnewdescription).toString().replace("[versionname]", versionName);
	            String whatsNewShow = getText(R.string.whatsnewshow).toString();
	            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
	            if (((whatsNewShow.length() > 0) && (whatsNewShow.equals("true")) && (!pref.getBoolean(versionName, false))) || (Settings.IsDebug)) {
		            AlertDialog.Builder builder = new AlertDialog.Builder(this);
		            builder.setTitle(whatsNewTitle)
		            .setMessage(whatsNewDescription)
		                   .setCancelable(false)
		                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		                       public void onClick(DialogInterface dialog, int id) {
		                    	   
		                       }
		                   });
		            AlertDialog alert = builder.create();
		            alert.show();
		            pref.edit().putBoolean(versionName, true).commit();
	            }
	            
	            String openKey = versionName + "open";
	            
	            if (!pref.getBoolean(openKey, false)) {
	            	_tracker.trackPageView("/open/" + versionName);
	            	pref.edit().putBoolean(openKey, true).commit();
	            }
            	_tracker.trackPageView("/data/startapp/" + versionName);
	 
	      } catch (PackageManager.NameNotFoundException e) { 
	    	  int i = 1;
        } 
	}
    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		_tracker.StopTracking();
	}
	
	private void StartSearch() {
		EditText searchEditText = (EditText)findViewById(R.id.startsearchtext);
		String searchString = searchEditText.getText().toString();
		if ((searchString == null) || (searchString.length() == 0)) {
			Toast.makeText(this, getText(R.string.startsearchnotext), 1000).show();
			return;
		}
		
		try {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchEditText.getApplicationWindowToken(), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent eventDetailIntent = new Intent(StartActivity.this, SearchResultListActivity.class);
		eventDetailIntent.putExtra(SearchResultListActivity.EXTRA_SEARCHSTRING, searchString);
		startActivity(eventDetailIntent);
	}
	
    private void setupProcessDialog() {
		_downloadDBProgressDialog = new ProgressDialog(this);
		_downloadDBProgressDialog.setCancelable(false);
		_downloadDBProgressDialog.setTitle("Downloading update");
		_downloadDBProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		_downloadDBProgressDialog.setMax(0);
	}
    
    
    @Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == KeyEvent.ACTION_UP)
	    {
			if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					_tracker.trackClick("/click/searchresult/start/keyup");
					StartSearch();
				return true;
			}
	    }
		return false;
	}
    
    private long GetDBVersionNumber() {
		Uri uri = Uri.withAppendedPath(MetadataProvider.CONTENT_URI_METADATA, MetadataProvider.METADATA_DB_VERSION);

		Cursor metacursor = getContentResolver().query(uri, null, null, null, null);

		long result = -1;
		if (metacursor != null) {
			metacursor.moveToFirst();
			String versionString = metacursor.getString(metacursor
					.getColumnIndex(MetadataProvider.METADATA_KEY_METADATA));
			metacursor.close();
			

			try {
				long version = Long.parseLong(versionString);
				result = version;
			} catch (Exception ex) {
				int i = 1;
			}
		}
		return result;
    }
	
	protected class CopyStandardDBTask extends AsyncTask<Object, Object, Boolean> 
	{
    	
		DBFileService _dbFileService = new DBFileService(new BinaryLoader());
		ProgressDialog _dialog = null;
		
		@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		setProgressBarIndeterminateVisibility(true);
    			_dialog = new ProgressDialog(StartActivity.this);
    			_dialog.setTitle(R.string.startingup);
    			_dialog.setCancelable(false);
    			_dialog.show();
    	}
		
    	@Override
    	protected Boolean doInBackground(Object... arg0) {
			FestivalDBHelper.ReleaseHelper(); //Release current helper (if any), a bit preemtive
    		_dbFileService.PlaceDefaultDatabase(StartActivity.this);

    		//Align any favorites existing in the favorites table
			FestivalDBHelper.ReleaseHelper(); //Release current helper (if any), a bit preemtive
			FavoriteManager favman = new FavoriteManager(StartActivity.this);
			favman.AlignFavorites();
			return true;
    	}
    	
    	@Override
    	protected void onPostExecute(Boolean result) {
    		// TODO Auto-generated method stub
    		super.onPostExecute(result);
    		try {
	    		setProgressBarIndeterminateVisibility(false);
	    		if (_dialog != null) {
	    			_dialog.dismiss();
	    		}
    		}
    		catch (Exception ex) 
    		{
    		}
    	}
    }
	
	
	
	protected class CheckForDBUpdateTask  extends AsyncTask<Object, Object, Boolean> {
	
		DBFileService _dbFileService = new DBFileService(new BinaryLoader());
		ProgressDialog _dialog = null;
		
		@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		setProgressBarIndeterminateVisibility(true);
    	}
		
    	@Override
    	protected Boolean doInBackground(Object... arg0) {
			
			//After first time install, we place the default DB
    		if (_dbFileService.IsDatabasePresent()) {
	    		long currentVersion = GetDBVersionNumber();
				if (currentVersion == -1)
			   	{
			   		return false;
			   	}
				return _dbFileService.AreUpdatesAvailable(currentVersion);
    		}
    		return false;
    	}
    	
    	
    	@Override
    	protected void onPostExecute(Boolean result) {
    		// TODO Auto-generated method stub
    		super.onPostExecute(result);
    		try {
	    		setProgressBarIndeterminateVisibility(false);
	    		if (result) 
	    		{
	          		StartActivity.this._downloadupdate.setVisibility(View.VISIBLE);
	    		}
    		}
    		catch (Exception ex) 
    		{
    		}
    	}
	}
    
    protected class DownloadDBUpdate extends AsyncTask<Object, Integer, Object> {
    	
    	@Override
    	protected Object doInBackground(Object... arg0) {
    		long version = -1;
    		try {
    			FestivalDBHelper.ReleaseHelper(); //Release current helper (if present)
				BinaryLoader binaryLoader = new BinaryLoader();
				binaryLoader.SetProgressListener(new BinaryLoader.ProgressListener() {
					
					@Override
					public void onProgress(int current, int fullLength) {
						// TODO Auto-generated method stub
						publishProgress(current, fullLength);
					}
				});
				
				version = GetDBVersionNumber();
				if (version == -1)
			   	{
			   		return null;
			   	}
				
				DBFileService dbService = new DBFileService(binaryLoader);
				dbService.UpdateDatabase(version);

				FestivalDBHelper.ReleaseHelper(); //Release current helper (if present)
				FavoriteManager favman = new FavoriteManager(StartActivity.this);
				favman.AlignFavorites();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					StartActivity.this._tracker.trackPageView("/data/updatefailed/" + Long.toString(version));
				} catch (Exception ex) 
				{
				}
			}

    		return null;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		setupProcessDialog();
    		_downloadDBProgressDialog.setProgress(0);
      		Toast.makeText(StartActivity.this, R.string.pleasewait, Toast.LENGTH_SHORT).show();
    	}
    	
    	@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			if (!_downloadDBProgressDialog.isShowing()) {
				_downloadDBProgressDialog.show();		
			}
			_downloadDBProgressDialog.setProgress(values[0]);
			_downloadDBProgressDialog.setMax(values[1]);
		}
    	
    	@Override
    	protected void onPostExecute(Object result) {
    		// TODO Auto-generated method stub
    		super.onPostExecute(result);
    		try {
	    		_downloadDBProgressDialog.hide();
	      		Toast.makeText(StartActivity.this, R.string.downloadupdatesuccess, Toast.LENGTH_LONG).show();
    		}
    		catch (Exception ex) 
    		{
    		}
    	}
    }
}