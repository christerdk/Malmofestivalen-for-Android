package dk.christer.malmofestivalen.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import dk.christer.malmofestivalen.adapters.UpcomingCursorAdapter;
import dk.christer.malmofestivalen.cursors.SectionCursor;
import dk.christer.malmofestivalen.cursors.UpcomingSectionCursor;
import dk.christer.malmofestivalen.data.EventProvider;

public class UpcomingSectionCursorLoader extends CursorLoader {
    public UpcomingSectionCursorLoader(final Context context) {
        super(context, EventProvider.CONTENT_URI_UPCOMING_EVENTS, null, null, null, null);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = super.loadInBackground();

        return new UpcomingSectionCursor(cursor, UpcomingCursorAdapter.SECTION_HEAD_COLUMN);
    }
}
