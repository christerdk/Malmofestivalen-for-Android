package dk.christer.malmofestivalen.cursors;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.format.DateUtils;
import android.util.SparseArray;
import dk.christer.malmofestivalen.data.EventProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This is an extended section cursor for the upcoming events list. It differs in that
 * it shows the time and date of the event in a special manner.
 */
public class UpcomingSectionCursor extends SectionCursor {
    private SparseArray<String> mFormattedStrings;
    private int mColumnIndexStartDate;
    private int mColumnIndexTitle;

    DateFormat mIso8601Format;
    DateFormat mTimeFormat;
    DateFormat mShortday;
    Calendar mCal;

    /**
     * @param cursorToWrap  The cursor to add headings to.
     * @param sectionColumn The column to look for changes in equals().
     */
    public UpcomingSectionCursor(final Cursor cursorToWrap, final String sectionColumn) {
        super(cursorToWrap, sectionColumn);
    }

    @Override
    protected void onCursorReset(final Cursor cursor) {
        mFormattedStrings = new SparseArray<String>();
        mColumnIndexStartDate = cursor.getColumnIndex(EventProvider.EVENT_KEY_STARTDATE);
        mColumnIndexTitle = cursor.getColumnIndex(EventProvider.VIRTUAL_KEY_EVENT_TITLE);
        mCal = Calendar.getInstance();
        mIso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mTimeFormat = new SimpleDateFormat("HH:mm");
        mShortday = new SimpleDateFormat("d");
    }

    @Override
    protected String getStringColumnToFormat() {
        return EventProvider.VIRTUAL_KEY_EVENT_TITLE;
    }

    @Override
    protected String getFormattedString() {
        return mFormattedStrings.get(getPosition());
    }

    @Override
    protected void onValidItemRow(int position, Cursor cursor) {
        String startDateString = cursor.getString(mColumnIndexStartDate);
        Date startDate;
        
        String itemText;
        try {
            startDate = mIso8601Format.parse(startDateString);
            mCal.setTime(startDate);
            int dayNbrStart = mCal.get(mCal.DAY_OF_WEEK);
            String dayOfWeek = DateUtils.getDayOfWeekString(dayNbrStart, DateUtils.LENGTH_LONG);

            itemText = mTimeFormat.format(startDate) + " " +
                    cursor.getString(mColumnIndexTitle) +
                    " (" + dayOfWeek + " d. " + mShortday.format(startDate) + ")";
        } catch (ParseException e) {
            itemText = cursor.getString(mColumnIndexTitle);
            e.printStackTrace();
        }

        mFormattedStrings.put(position, itemText);
    }
}
