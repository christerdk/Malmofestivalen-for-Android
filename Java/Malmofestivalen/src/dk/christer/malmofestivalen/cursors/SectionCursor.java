package dk.christer.malmofestivalen.cursors;

import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.BaseColumns;
import android.util.SparseBooleanArray;

public abstract class SectionCursor extends AbstractCursor {
    private int MAX_PER_HEADING = 3;

    protected Cursor mCursor;

    private int mSectionColumnIndex;

    private SparseBooleanArray mSectionHeadPositions = new SparseBooleanArray();

    private int[] mCursorPositions;

    private long[] mItemIds;

    private int mSize;

    private int mIdColumn;
    private int mColumnIndexFormatColumn;

    /**
     * @param cursorToWrap The cursor to add headings to.
     * @param sectionColumn The column to look for changes in equals().
     */
    public SectionCursor(final Cursor cursorToWrap, final String sectionColumn) {
        mCursor = cursorToWrap;
        mSectionColumnIndex = cursorToWrap.getColumnIndexOrThrow(sectionColumn);

        findSections();
    }

    /**
     * Helper to find the sections and adapt sizes etc.
     */
    private void findSections() {
        onCursorReset(mCursor);

        mColumnIndexFormatColumn = mCursor.getColumnIndexOrThrow(getStringColumnToFormat());

        mIdColumn = mCursor.getColumnIndex(BaseColumns._ID);

        String prevHeading = null;

        /**
         * In order to keep the item ids stable we have to keep track of
         * where the categories start and end. We use the
         * (-1 + first_channel_id_of_category * -1) as an id for the category title
         * it self.
         * The item ids array can be two time the number of items
         * at max, so we start with that and resize later.
         */
        long[] itemIds = new long[mCursor.getCount() * 2];
        int[] cursorPositions = new int[mCursor.getCount() * 2];

        mCursor.moveToFirst();
        int position = 0;
        int countForHeading = 0;
        while (!mCursor.isAfterLast()) {
            long dbId = mCursor.getLong(mIdColumn);

            String newHeading = mCursor.getString(mSectionColumnIndex);
            if (newHeading != null && !newHeading.equals(prevHeading)) {
                // We just add the same cursor position one extra time if
                // this is a heading.
                cursorPositions[position] = mCursor.getPosition();

                prevHeading = newHeading;
                countForHeading = 0;

                itemIds[position] = -1 + (-1 * dbId);

                mSectionHeadPositions.append(position, true);

                // We added a heading so we increase the position offset
                // one extra time.
                position++;
            }

            if (countForHeading < MAX_PER_HEADING) {
                // We only add the position if we haven't reached our
                // maximum number per heading.
                cursorPositions[position] = mCursor.getPosition();
                itemIds[position] = dbId;

                onValidItemRow(position, mCursor);

                countForHeading++;
                position++;
            }

            mCursor.moveToNext();
        }
        mCursor.moveToPosition(-1);

        mSize = position;

        mItemIds = new long[mSize];
        System.arraycopy(itemIds, 0, mItemIds, 0, mSize);

        mCursorPositions = new int[mSize];
        System.arraycopy(cursorPositions, 0, mCursorPositions, 0, mSize);
    }

    protected abstract void onValidItemRow(int position, Cursor mCursor);

    protected abstract void onCursorReset(final Cursor cursor);

    protected abstract String getStringColumnToFormat();

    /**
     * @param position The position to check
     * @return True if the position is at a section head.
     */
    public boolean isSectionHead(final int position) {
        return mSectionHeadPositions.get(position);
    }


    /**
     * @return True if the current position is a section head.
     */
    public boolean isSectionHead() {
        return mSectionHeadPositions.get(getPosition());
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public boolean onMove(final int oldPosition, final int newPosition) {
        return mCursor.moveToPosition(mCursorPositions[newPosition]);
    }

    @Override
    public void registerDataSetObserver(final DataSetObserver observer) {
        super.registerDataSetObserver(observer);

        mCursor.registerDataSetObserver(observer);
    }

    @Override
    public int getColumnIndex(final String columnName) {
        return mCursor.getColumnIndex(columnName);
    }

    @Override
    public boolean requery() {
        boolean result = mCursor.requery();

        if (result) {
            findSections();
        }
        return result;
    }

    @Override
    public void close() {
        super.close();
        mCursor.close();
        mItemIds = null;
        mCursorPositions = null;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        mCursor.deactivate();
    }

    @Override
    public String[] getColumnNames() {
        return mCursor.getColumnNames();
    }

    @Override
    public String getString(final int i) {
        if (i == mColumnIndexFormatColumn) {
            // This should be formatted by extending classes.
            return getFormattedString();
        }
        return mCursor.getString(i);
    }

    protected abstract String getFormattedString();

    @Override
    public short getShort(final int i) {
        return mCursor.getShort(i);
    }

    @Override
    public int getInt(final int i) {
        return mCursor.getInt(i);
    }

    @Override
    public long getLong(final int column) {
        if (column == mIdColumn) {
            return mItemIds[getPosition()];
        } else {
            return mCursor.getLong(column);
        }
    }

    @Override
    public float getFloat(final int i) {
        return mCursor.getFloat(i);
    }

    @Override
    public double getDouble(final int i) {
        return mCursor.getDouble(i);
    }

    @Override
    public boolean isNull(final int i) {
        return mCursor.isNull(i);
    }
}
