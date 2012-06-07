package dk.christer.malmofestivalen.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.json.JSONTokener;


import dk.christer.malmofestivalen.R;
import dk.christer.malmofestivalen.StartActivity;
import dk.christer.malmofestivalen.data.FestivalDBHelper;
import dk.christer.malmofestivalen.net.BinaryLoader;
import dk.christer.malmofestivalen.net.JSONLoader;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class DBFileService {
	
	BinaryLoader _binaryLoader;
	
	public DBFileService(BinaryLoader binaryLoader) {
		_binaryLoader = binaryLoader;
	}
	
	
	public Boolean AreUpdatesAvailable(long currentDBVersion) {
		if (GetUpdateURI(currentDBVersion).length() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private String GetUpdateURI(long currentDBVersion) {
		try {
			String jsonResult = JSONLoader.Get("http://api2012.mmmos.se/db/updatefor/" + Long.toString(currentDBVersion));
			if (jsonResult == null) {
				return "";
			}
			
			JSONObject object = (JSONObject) new JSONTokener(jsonResult).nextValue();
			String uri = object.getString("uri");
			return uri;
		}
		catch (Exception ex ){
			ex.printStackTrace();
		}
		return "";
	}
	
	private static String getDatabaseDirectory() {
		return Environment.getDataDirectory() + "/data/dk.christer.malmofestivalen/databases";
	}
	
	private static String getDatabaseFilePath() {
		return getDatabaseDirectory() + "/" + FestivalDBHelper.DATABASE_NAME;
	}

	private static String getDatabaseTempFilePath() {
		return getDatabaseFilePath() + ".temp";
	}

	public void PlaceDefaultDatabase(Context context) {
			String dataDirectory = getDatabaseDirectory();
			
			File directoryFile = new File(dataDirectory);
			if (!directoryFile.exists()) {
				try {
					FileUtils.forceMkdir(directoryFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	    			Toast.makeText(context, "Cannot make directory file (" + e.getMessage() + ")", 3000).show();
	    			return;
				} 
			}
			
		    String dataFile = getDatabaseFilePath();
			try {
				new File(dataFile).delete();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Toast.makeText(context, "Error deleting file (" + e1.getMessage() + ")", 3000).show();
				return;
			}
	
		    FileOutputStream fileOutput = null;
			try {
	    		InputStream inputStream = context.getResources().openRawResource(R.raw.concerts);
				fileOutput = new FileOutputStream(new File(dataFile));
				byte[] buf = new byte[4096];
				int retreived = 0;
				int allretreived = 0;
				while ((retreived = inputStream.read(buf)) > 0) {
					fileOutput.write(buf, 0, retreived);
					allretreived = allretreived + retreived;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.makeText(context, "Error copying file (" + e.getMessage() + ")", 3000).show();
			}
			finally {
				try {
					fileOutput.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	public void UpdateDatabase(long version) {
		String url = GetUpdateURI(version);
		if (url.length() > 0) {
			if (_binaryLoader.Download(url, getDatabaseTempFilePath()))
			{
				File oldFile = null;
				File productionFile = null;
				try {
					oldFile = new File(getDatabaseFilePath() + ".old");
					if (oldFile.exists()) {
						oldFile.delete();
					}
	
					//Rename existing file to old temp
					productionFile = new File(getDatabaseFilePath());
					productionFile.renameTo(oldFile);
	
					//Rename downloaded file to production file
					File tempFile = new File(getDatabaseTempFilePath());
					if (!tempFile.exists()) {
						throw new Exception();
					}
					tempFile.renameTo(productionFile);

					//All good, then delete old file.
					oldFile.delete();
				} 
				catch(Exception ex) {
					//If something happens during file I/O, take the last known file an revert it to production file
					if (oldFile != null && oldFile.exists()) {
						if (productionFile != null && productionFile.exists()) {
							productionFile.delete();
						}
						oldFile.renameTo(new File(getDatabaseFilePath()));
					}
					ex.printStackTrace();
				}
			}
		}
	}
	
	public static Boolean IsDatabasePresent() {
		return new File(getDatabaseFilePath()).exists();
	}
}
