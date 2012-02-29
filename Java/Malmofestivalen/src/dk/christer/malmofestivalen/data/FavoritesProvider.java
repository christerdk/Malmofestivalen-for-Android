package dk.christer.malmofestivalen.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import dk.christer.malmofestivalen.data.urimatchers.FavoritesUriMatcher;

import java.sql.SQLException;
import java.util.List;

/**
 * A content provider for handling the user's favorites.
 * <p/>
 * We do not use sub uris such as /favorites/<id> since that seems fairly redundant.
 */
public class FavoritesProvider extends ContentProvider {
    public static final String AUTHORITY = "dk.christer.malmofestivalen.favorites";

    public static final Uri CONTENT_URI_FAVORITES = Uri.parse("content://" + AUTHORITY);

    private static final String FAVORITE_ITEM_TYPE = "vnd.android.cursor.item/vnd.dk.christer.malmofestivalen.favorite";
    private static final String FAVORITE_ITEMLIST_TYPE =
            "vnd.android.cursor.dir/vnd.dk.christer.malmofestivalen.favorite";

    private UriMatcher mFavoritesUriMatcher;

    private FavoritesDBHelper mDbHelper;
    public static final String FAVORITE_KEY_BUSINESSID = FavoritesDBHelper.FAVORITE_KEY_BUSINESSID;
    public static final String FAVORITE_KEY_ALARMTIME = FavoritesDBHelper.FAVORITE_KEY_ALARMTIME;

    @Override
    public boolean onCreate() {
        mFavoritesUriMatcher = new FavoritesUriMatcher();

        mDbHelper = new FavoritesDBHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection,
                        final String[] selectionArgs, final String sortOrder) {
        switch (mFavoritesUriMatcher.match(uri)) {
            case FavoritesUriMatcher.FAVORITES:
                return getAllFavorites();
            case FavoritesUriMatcher.FAVORITES_ID:
                return getFavorite(uri.getLastPathSegment());
            default:
                throw new IllegalArgumentException("Unknown content uri: " + uri);
        }
    }

    private Cursor getAllFavorites() {
        // We want it all, so we do not specify anything except sort order.
        // This is since there is only one single table in the whole db and we want all rows from that table.

        String sortOrder = FAVORITE_KEY_ALARMTIME + " asc";
        Cursor cursor = mDbHelper.query(null, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI_FAVORITES);

        return cursor;
    }

    private Cursor getFavorite(final String id) {
        String selection = FAVORITE_KEY_BUSINESSID + " = ?";
        String[] selectionArgs = new String[]{
                id
        };
        Cursor cursor = mDbHelper.query(null, selection, selectionArgs, null);

        cursor.setNotificationUri(getContext().getContentResolver(), getFavoriteUri(id));

        return cursor;
    }

    private Uri getFavoriteUri(final String id) {
        return Uri.withAppendedPath(CONTENT_URI_FAVORITES, id);
    }

    @Override
    public String getType(final Uri uri) {
        switch (mFavoritesUriMatcher.match(uri)) {
            case FavoritesUriMatcher.FAVORITES:
                return FAVORITE_ITEMLIST_TYPE;
            case FavoritesUriMatcher.FAVORITES_ID:
                return FAVORITE_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown content uri: " + uri);
        }
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues contentValues) {
        if (mFavoritesUriMatcher.match(uri) != FavoritesUriMatcher.FAVORITES) {
            throw new IllegalArgumentException("Unknown uri for insert: " + uri);
        }

        String id = contentValues.getAsString(FAVORITE_KEY_BUSINESSID);

        if (id == null) {
            throw new IllegalArgumentException("Business id must be provided for insert operations" +
                    "in the favorites provider!");
        }

        if (!contentValues.containsKey(FAVORITE_KEY_ALARMTIME)) {
            throw new IllegalArgumentException("Alarm time must be provided for insert operations" +
                    "in the favorites provider!");
        }

        try {
            mDbHelper.insert(contentValues);

            return getFavoriteUri(id);
        } catch (final SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int delete(final Uri uri, final String inputSelection, final String[] inputSelectionArgs) {
        if (mFavoritesUriMatcher.match(uri) != FavoritesUriMatcher.FAVORITES_ID) {
            throw new IllegalArgumentException("We only allow delete of specific favorites!");
        }

        if (inputSelection != null || inputSelectionArgs != null) {
            throw new IllegalArgumentException("We do not allow selection for delete of specific favorites at" +
                    "this moment. Change this if you need it.");
        }

        String selection = FavoritesProvider.FAVORITE_KEY_BUSINESSID + "= ?";
        String[] selectionArgs = new String[]{
                uri.getLastPathSegment()
        };

        return mDbHelper.delete(selection, selectionArgs);
    }

    @Override
    public int update(final Uri uri, final ContentValues contentValues,
                      final String inputSelection, final String[] inputSelectionArgs) {
        if (mFavoritesUriMatcher.match(uri) != FavoritesUriMatcher.FAVORITES_ID) {
            throw new IllegalArgumentException("We only allow updates of specific favorites!");
        }

        if (inputSelection != null || inputSelectionArgs != null) {
            throw new IllegalArgumentException("We do not allow selection for update of specific favorites at" +
                    "this moment. Change this if you need it.");
        }

        if (!contentValues.containsKey(FAVORITE_KEY_ALARMTIME)) {
            throw new IllegalArgumentException("Alarm time must be provided for update operations" +
                    "in the favorites provider!");
        }

        String selection = FavoritesProvider.FAVORITE_KEY_BUSINESSID + "= ?";
        String[] selectionArgs = new String[]{
                uri.getLastPathSegment()
        };

        try {
            return mDbHelper.update(contentValues, selection, selectionArgs);
        } catch (final SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
