package dk.christer.malmofestivalen.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dk.christer.malmofestivalen.R;
import dk.christer.malmofestivalen.cursors.SectionCursor;
import dk.christer.malmofestivalen.data.EventProvider;

public class UpcomingCursorAdapter extends CursorAdapter {
    public static final String SECTION_HEAD_COLUMN = EventProvider.VIRTUAL_KEY_SCENE_TITLE;

    private int mColumnIndexScene;
    private int mColumnIndexTitle;
    private int mColumnIndexWhen;

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
            ((TextView) view).setText(cursor.getString(mColumnIndexTitle));
        }
    }
}
