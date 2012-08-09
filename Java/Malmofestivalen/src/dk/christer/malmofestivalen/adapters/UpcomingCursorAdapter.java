package dk.christer.malmofestivalen.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import dk.christer.malmofestivalen.R;
import dk.christer.malmofestivalen.cursors.SectionCursor;
import dk.christer.malmofestivalen.cursors.UpcomingSectionCursor;
import dk.christer.malmofestivalen.data.EventProvider;
import dk.christer.malmofestivalen.net.ImageFetcher;

public class UpcomingCursorAdapter extends CursorAdapter {
	private final ImageFetcher imageDownloader = ImageFetcher.getInstance(mContext);
	
    public static final String SECTION_HEAD_COLUMN = EventProvider.VIRTUAL_KEY_SCENE_TITLE;

    private int mColumnIndexScene;
    private int mColumnIndexTitle;
    private int mColumnIndexWhen;
    private int mColumnIndexSmallImage;

    public UpcomingCursorAdapter(final Context context) {
        super(context, null, 0);
    }

    @Override
    public Cursor swapCursor(final Cursor newCursor) {
        Cursor oldCursor = super.swapCursor(newCursor);

        if (newCursor != null) {
            mColumnIndexScene = newCursor.getColumnIndexOrThrow(SECTION_HEAD_COLUMN);
            mColumnIndexTitle = newCursor.getColumnIndexOrThrow(EventProvider.VIRTUAL_KEY_EVENT_TITLE);
            mColumnIndexWhen = newCursor.getColumnIndexOrThrow(EventProvider.EVENT_KEY_STARTDATE);
            mColumnIndexSmallImage = newCursor.getColumnIndexOrThrow(EventProvider.EVENT_KEY_URISMALLIIMAGE);
        }

        return oldCursor;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return ((SectionCursor) getCursor()).isSectionHead(position) ? 0 : 1;
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup viewGroup) {
        if (((SectionCursor) cursor).isSectionHead()) {
            return View.inflate(context, R.layout.list_header, null);
        } else {
            return View.inflate(context, R.layout.list_item, null);
        }
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        if (((SectionCursor) cursor).isSectionHead()) {
            ((TextView) view).setText(cursor.getString(mColumnIndexScene));
        } else {
        	String theText = cursor.getString(mColumnIndexTitle);

        	TextView theTextView = (TextView)view.findViewById(R.id.list_item_title);

        	ImageView iv = (ImageView) view.findViewById(R.id.list_item_image);
        	iv.setVisibility(View.VISIBLE);
            iv.setImageBitmap(null);

            //if text contains splitter, that means that the text includes an image url (see UpcomingSectionCursor)
            if (theText.contains(UpcomingSectionCursor.TEXT_URL_SPLIT)) {
                int splitterIndexStart = theText.indexOf(UpcomingSectionCursor.TEXT_URL_SPLIT);
                int splitterIndexStop = splitterIndexStart + 3;
                String url = theText.substring(splitterIndexStop, theText.length());
        	    imageDownloader.download(iv, url, null);
        	    theTextView.setText(theText.substring(0, splitterIndexStart));
        	}
        	else {
        		theTextView.setText(theText);
        	}
        	
        	
            
            

            
        }
    }
}
