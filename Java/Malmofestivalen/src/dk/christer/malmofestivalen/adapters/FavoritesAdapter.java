package dk.christer.malmofestivalen.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.christer.malmofestivalen.R;
import dk.christer.malmofestivalen.data.EventProvider;
import dk.christer.malmofestivalen.helpers.DateHelper;
import dk.christer.malmofestivalen.net.ImageFetcher;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FavoritesAdapter extends SimpleAdapter {
	private ImageFetcher imageDownloader = null;

	public static final String SMALL_IMAGE_URL = "SMALL_IMAGE_URL";
	
	public FavoritesAdapter(Context context,
			ArrayList<HashMap<String, String>> favoriteData, int resource, String[] from,
			int[] to) {
		super(context, favoriteData, resource, from, to);
		imageDownloader = ImageFetcher.getInstance(context);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		
		Object item = getItem(position);
		if (item != null) {
			Map<String, String> cursor = (Map<String, String>)item;
	
			ImageView iv = (ImageView) row.findViewById(R.id.eventitemrowimage);
	
			String url = cursor.get(FavoritesAdapter.SMALL_IMAGE_URL);
		    imageDownloader.download(iv, url, null);
		}
		return row;
	}

}
