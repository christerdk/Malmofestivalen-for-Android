package dk.christer.malmofestivalen;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import dk.christer.malmofestivalen.R;
import dk.christer.malmofestivalen.FavoriteManager.ImportInspectListener;
import dk.christer.malmofestivalen.analytics.GoogleAnalyticsWrapper;
import dk.christer.malmofestivalen.net.JSONLoader;

public class ImportActivity extends Activity {

	ProgressDialog progressDialog;

	private String _apiKey = "6JGOjJiI5EiGybcsT0deqQ";
	private String _tokenURL = "http://api.malmofestivalen.se/json/user/authenticate/";
	private String _favoritesURL = "http://api.malmofestivalen.se/json/user/favourites/";

	EditText _username;
	EditText _password;
	SharedPreferences _pref;
	public static String USERNAMEKEY = "USERNAMEKEY";
	
	GoogleAnalyticsWrapper _tracker;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.importview);

		_username = (EditText)findViewById(R.id.importusername);
		_password= (EditText)findViewById(R.id.importpassword);
		_pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		setupAnalytics();
		setupProcessDialog();
		initializeUsernameTextbox();
		setupButtonEventhandler();
      	setupTexts();
	}

	private void setupButtonEventhandler() {
		Button testbutton = (Button)findViewById(R.id.importtestbutton);
      	testbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_tracker.trackClick("/click/import");
				_pref.edit().putString(USERNAMEKEY, GetUsername()).commit();
				new ImportActsFromSite().execute();
			}
		});
	}

	private void initializeUsernameTextbox() {
		_username.setText(_pref.getString(USERNAMEKEY, ""));
	}

	private void setupTexts() {
		TextView explanation = (TextView)findViewById(R.id.importexplanation);
      	CharSequence styledText = Html.fromHtml(getString(R.string.importexplanation));
      	explanation.setText(styledText);
	}

	private void setupProcessDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Importing");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(10);
	}

	private void setupAnalytics() {
		_tracker = GoogleAnalyticsWrapper.getInstance();
		_tracker.trackPageView("/view/import");
	}

	public String GetUsername() {
		return _username.getText().toString();
	}
	
	public String GetPassword() {
		return _password.getText().toString();
	}
	
	
	private class ImportActsFromSite extends AsyncTask {

		private String _Username = "";
		private String _Password = "";
		int inspected = 0;
		
		@Override
		protected Integer doInBackground(Object... arg0) {
			final String[] actsToImport = GetActIdsToImport(_Username, _Password);
			if (actsToImport.length == 0) {
				return 0;
			}
			FavoriteManager favoriteManager = new FavoriteManager(ImportActivity.this);
			ImportInspectListener listener = new ImportInspectListener() {
				@Override
				public void onInspect() {
					inspected++;
					publishProgress(String.valueOf(inspected), String.valueOf(actsToImport.length));		
				}
			};
			favoriteManager.SetOnImportInspectListener(listener);
			int numberOfActsImported = favoriteManager.ImportFavorites(actsToImport);
			favoriteManager.RemoveImportListener();
			return numberOfActsImported;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog.setProgress(0);
			progressDialog.show();
			_Username = GetUsername();
			_Password= GetPassword();

		}
		
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Integer imported = (Integer)result;
			progressDialog.hide();
			Toast.makeText(ImportActivity.this, imported.toString() + " new events imported", Toast.LENGTH_SHORT).show();
			ImportActivity.this.finish();
		}
		
		@Override
		protected void onProgressUpdate(Object... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			progressDialog.setMax(Integer.parseInt((String)values[1]));
			progressDialog.setProgress(Integer.parseInt((String)values[0]));
		}
		
	}
	
	private String[] GetActIdsToImport(String username, String password) {
		
		String[] result = new String[0];
		try {
			//Get token
			HashMap<String, String> tokenValues = new HashMap<String, String>();
			tokenValues.put("apikey", _apiKey);
			tokenValues.put("emailaddress", username);
			tokenValues.put("password", password);
			
			String tokenResult = JSONLoader.Get(_tokenURL, tokenValues);
			JSONObject object = (JSONObject) new JSONTokener(tokenResult).nextValue();
			String token = object.getString("Token");

			
			HashMap<String, String> favoriteValues = new HashMap<String, String>();
			favoriteValues.put("token", token);
			String favoriteResult = JSONLoader.Get(_favoritesURL, favoriteValues);
			

			JSONObject root = (JSONObject) new JSONTokener(favoriteResult).nextValue();
			JSONArray acts = root.getJSONArray("UserFavourites");
			
			 
			ArrayList<String> favoriteIdsToImport = new ArrayList<String>();
			if (acts != null) {
				for (int i = 0; i < acts.length(); i++) {
					JSONObject act = acts.getJSONObject(i);
					if (act != null) {
						JSONObject favorite = act.getJSONObject("FavouriteScheduledAt");
						String favoriteId = favorite.getString("FavouriteId");
						if (favoriteId != null) {
							favoriteIdsToImport.add(favoriteId);
						}
					}
				}
			}
			
			result = favoriteIdsToImport.toArray(result);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}

	
}
