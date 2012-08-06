package dk.christer.malmofestivalen.adapters;

import dk.christer.malmofestivalen.data.EventProvider;
import dk.christer.malmofestivalen.helpers.DateHelper;
import dk.christer.malmofestivalen.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import dk.christer.malmofestivalen.net.ImageFetcher;

public class EventCursorAdapter extends SimpleCursorAdapter {
	private final ImageFetcher imageDownloader = ImageFetcher.getInstance(mContext);

	public EventCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to, 0);
	}
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);

		ImageView iv = (ImageView) row.findViewById(R.id.eventitemrowimage);

		String url = cursor.getString(cursor.getColumnIndex(EventProvider.EVENT_KEY_URISMALLIIMAGE));
	    imageDownloader.download(iv, url, null);
		
		String start = cursor.getString(cursor.getColumnIndex(EventProvider.EVENT_KEY_STARTDATE));
		String end = cursor.getString(cursor.getColumnIndex(EventProvider.EVENT_KEY_ENDDATE));
		String dateString = DateHelper.createShortDateResume(start, end);
		TextView tv = (TextView) row.findViewById(R.id.eventitemrowtimeresume);
		tv.setText(dateString);
		return row;
	}
}
