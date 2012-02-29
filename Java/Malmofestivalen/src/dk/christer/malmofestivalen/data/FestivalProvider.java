package dk.christer.malmofestivalen.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public abstract class FestivalProvider extends ContentProvider {

	
	private UriMatcher mUriMatcher;
    private final String mAuthority;
    private final String mDbTable;

    //private FestivalDBHelper mDbHelper;

    private Context _context;
    
    protected FestivalProvider(final String authority, final String dbTable) {
        mAuthority = authority;
        mDbTable = dbTable;

        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    }

    protected void addUri(final String path, final int matchValue) {
        String actualPath = null;

        if (path != null) {
            actualPath = path.trim();
            if (actualPath.startsWith("/")) {
                actualPath = actualPath.substring(1);
            }
        }

        mUriMatcher.addURI(mAuthority, actualPath, matchValue);
    }

    @Override
    public boolean onCreate() {
        //mDbHelper = FestivalDBHelper.getInstance(getContext());

        return true;
    }

    protected abstract Cursor query(int uriMatch, Uri uri, String[] projection,
                                    String selection, String[] selectionArguments, String sortOrder);

    protected abstract String getType(int match);

    @Override
    public final Cursor query(final Uri uri, final String[] projection,
                              final String selection, final String[] selectionArgs, final String sortOrder) {
        Cursor result = query(mUriMatcher.match(uri), uri, projection, selection, selectionArgs, sortOrder);

        if (result == null) {
            throw new IllegalArgumentException("Unknown content uri: " + uri);
        }

        return result;
    }

    protected Cursor dbQuery(final String[] projection, final String selection,
                             final String[] selectionArgs, final String sortOrder, final String limit) {
        return FestivalDBHelper.getInstance(getContext()).query(mDbTable, projection, selection, selectionArgs, sortOrder, limit);
    }

    protected Cursor dbQuery(final String sql, final String[] selectionArgs) {
    	return FestivalDBHelper.getInstance(getContext()).rawQuery(sql, selectionArgs);
    }
    
    @Override
    public final String getType(final Uri uri) {
        String type = getType(mUriMatcher.match(uri));

        if (type == null) {
            throw new IllegalArgumentException("Unknown content uri: " + uri);
        }

        return type;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues contentValues) {
        throw new IllegalArgumentException("Inserts are forbidden");
    }

    @Override
    public int delete(final Uri uri, final String inputSelection, final String[] inputSelectionArgs) {
        throw new IllegalArgumentException("Deletes are forbidden");
    }

    @Override
    public int update(final Uri uri, final ContentValues contentValues,
                      final String inputSelection, final String[] inputSelectionArgs) {
        throw new IllegalArgumentException("Updates are forbidden");
    }
   
}
