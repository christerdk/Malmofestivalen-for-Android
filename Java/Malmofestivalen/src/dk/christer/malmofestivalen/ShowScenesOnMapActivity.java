package dk.christer.malmofestivalen;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import dk.christer.malmofestivalen.SceneItemizedOverlay.IOnTapListener;
import dk.christer.malmofestivalen.analytics.GoogleAnalyticsWrapper;
import dk.christer.malmofestivalen.data.SceneProvider;
import dk.christer.malmofestivalen.helpers.ToastHelper;

public class ShowScenesOnMapActivity extends MapActivity implements LocationListener {

	public static final String MAP_SCENEID_KEY = "MAP_SCENEID_KEY"; 
	private static final String OPPOSITE_REPRESENTATION = "list";
	
	private MapView _map;
	private MapController _controller;
	List<Overlay> _mapOverlays;
	String _bestLocationProvider;
	LocationManager _mgr;
	MyLocationOverlay _myLocationOverlay;
	CheckBox _myPosition;
	Boolean _initialized = false;
	LocationListener _theListener = new someLocationListener();
	Boolean _isTurnedOn = false;
	
	private String _sceneIdToShow = ""; 
	GeoPoint sceneToShow = null;
	
	GoogleAnalyticsWrapper _tracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		setupAnalytics();

		_mgr = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
		_bestLocationProvider = _mgr.getBestProvider(criteria, true);
		
		RetrieveSceneIdToShow();
		SetupMap();
		CenterAndZoomMap();

		setupMyPositionEventHandler();
		setTitle(R.string.startkarta);
      	CharSequence thetext = getText(R.string.clickmenuforoptions);
      	ToastHelper.ShowToastOnce(this, "ShowScenesOnMapActivityMenuForMore", thetext.toString());

	}


	private void setupMyPositionEventHandler() {
		_myPosition = (CheckBox)findViewById(R.id.mapposition);
		_myPosition.setEnabled(_bestLocationProvider != null);
		_myPosition.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					TurnOnPosition();
				}
				else {
					TurnOffPosition();
				}
			}
		});
	}


	private void setupAnalytics() {
		_tracker = GoogleAnalyticsWrapper.getInstance();
	}


	private void RetrieveSceneIdToShow() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey(MAP_SCENEID_KEY)) {
			_sceneIdToShow = getIntent().getStringExtra(MAP_SCENEID_KEY);
	        _tracker.trackPageView("/view/mapsingleview");
		}
		else {
	        _tracker.trackPageView("/view/map");
		}
	}
	
    private void CenterAndZoomMap() {
		if (_sceneIdToShow == null || _sceneIdToShow == "") {
			_controller.setCenter(new GeoPoint(55605881,13001075));
			_controller.setZoom(16);
		}
		else {
			_controller.setCenter(sceneToShow);
			_controller.setZoom(17);
		}
	}

	private void initMyLocationOverlay() {
		// TODO Auto-generated method stub
    	if (_myLocationOverlay == null) {
			_myLocationOverlay = new MyLocationOverlay(this, _map);
			_myLocationOverlay.runOnFirstFix(new Runnable() {
	           public void run() {
	              // Zoom in to current location
	        	   //_controller.setZoom(8);
	        	   //_controller.stopAnimation(false);
	           }
	        });
	        _map.getOverlays().add(_myLocationOverlay);
    	}
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (_myPosition.isChecked()) {
			TurnOnPosition();
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (_myPosition.isChecked()) {
			TurnOffPosition();
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		TurnOffPosition();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		TurnOffPosition();
	}
	
	private void TurnOnPosition() {
		initMyLocationOverlay();
		if ((_bestLocationProvider != null) && (!_isTurnedOn)) {
			//if (mgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			_mgr.requestLocationUpdates(_bestLocationProvider, 9000, 1, _theListener);
			_isTurnedOn = true;
			_myLocationOverlay.enableMyLocation();
			_tracker.trackClick("/click/map-gpson");
			//} else 
			//{
			//	Toast.makeText(this, "Ingen GPS", 2000).show();
			//}
		}
	}
	
	private void TurnOffPosition() {
		if ((_bestLocationProvider != null) && (_isTurnedOn)) {
			_myLocationOverlay.disableMyLocation();
			_mgr.removeUpdates(_theListener);
			_isTurnedOn = false;
		}
	}
	
	
	private void SetupMap() {
		_map = (MapView)findViewById(R.id.generalmap);
		_controller = _map.getController();
		_map.setBuiltInZoomControls(true);
		_mapOverlays = _map.getOverlays();		
		
		Drawable normalMarker = getResources().getDrawable(R.drawable.normalmapmarker);
		Drawable specialMarker = getResources().getDrawable(R.drawable.specialmapmarker);
		SceneItemizedOverlay overlay = new SceneItemizedOverlay(normalMarker, this, _map);
		SceneItemizedOverlay specialOverlay = new SceneItemizedOverlay(specialMarker, this, _map);

		Cursor scenes = getContentResolver().query(SceneProvider.CONTENT_URI_SCENES, null, null, null, null);
		if (scenes != null) {
			while (scenes.moveToNext()) {
				int _latitude1E6 = scenes.getInt(scenes.getColumnIndex(SceneProvider.KEY_LATITUDE));
				int _longtitude1E6 = scenes.getInt(scenes.getColumnIndex(SceneProvider.KEY_LONGTITUDE));
				String _sceneId =  scenes.getString(scenes.getColumnIndex(SceneProvider.KEY_SCENE_ID));
				GeoPoint sceneGeoPoint = new GeoPoint(_latitude1E6, _longtitude1E6);
				SceneOverlayItem item = new SceneOverlayItem(sceneGeoPoint, "", "");
				item.SceneID = _sceneId; 
				
				if (_sceneIdToShow.equals(_sceneId)) {
					specialOverlay.addOverlay(item);
					sceneToShow = sceneGeoPoint;
				}
				else {
					overlay.addOverlay(item);
				}
			}
			scenes.close();
		}
		overlay.PopulateNow();
		specialOverlay.PopulateNow();
		_mapOverlays.add(overlay);
		_mapOverlays.add(specialOverlay);

		AddOverlayTapListener(overlay);
		AddOverlayTapListener(specialOverlay);
	}

	private void AddOverlayTapListener(SceneItemizedOverlay overlay) {
		overlay.SetTapListener(new IOnTapListener()
		{
				@Override
			public void onTap(SceneOverlayItem item) {
					_tracker.trackClick("/click/scene/map");
					Intent intent = new Intent(ShowScenesOnMapActivity.this, SceneDetailActivity.class);
					intent.putExtra(SceneDetailActivity.EXTRA_SCENE_ID, item.SceneID);
					startActivity(intent);
			}
		});
	}


	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(Location arg0) {
  	 }

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	private class someLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			int latitude1E6 = ((Double)(arg0.getLatitude() * 1E6)).intValue();
			int longtitude1E6 = ((Double)(arg0.getLongitude() * 1E6)).intValue();
	  	  	_controller.animateTo(new GeoPoint(latitude1E6, longtitude1E6));

		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, getText(R.string.menu_entry_show_as_list).toString());
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Settings.saveWantedRepresentation(getBaseContext(), OPPOSITE_REPRESENTATION);
		startActivity(new Intent(this, SceneListActivity.class));
		return super.onOptionsItemSelected(item);
	}
}
