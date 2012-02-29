package dk.christer.malmofestivalen.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class MetadataProvider extends FestivalProvider {
	
	private static final String METADATA_DATABASE_TABLE = "databasemeta";
    private static final String METADATA_KEY_METAKEY = "MetaKey";
    public static final String METADATA_KEY_METADATA = "MetaData";
    public static final String METADATA_DB_VERSION = "version";
	
    public static final String AUTHORITY = "dk.christer.malmofestivalen.metadata";

    private static final String ITEM_TYPE = "vnd.android.cursor.item/vnd.dk.christer.malmofestivalen.metadata";
  
    public static final Uri CONTENT_URI_METADATA = Uri.parse("content://" + AUTHORITY);


    public static final int URI_MATCH_MATCH_METADATA_BY_KEY = 0;
    
    public MetadataProvider() 
    {
    	super(AUTHORITY, METADATA_DATABASE_TABLE);
    	
        // content://dk.christer.malmofestivalen.metadata/<metadata key (text)>
        addUri(CONTENT_URI_METADATA.getPath() + "/*", URI_MATCH_MATCH_METADATA_BY_KEY);
    }
    
    @Override
    protected Cursor query(int uriMatch, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatch) {
            case URI_MATCH_MATCH_METADATA_BY_KEY:
                return getMetadataByKey(uri.getLastPathSegment());
            default:
                return null;
        }
    }
    
    @Override
    protected String getType(final int match) {
        switch (match) {
            case URI_MATCH_MATCH_METADATA_BY_KEY:
                return ITEM_TYPE;
            default:
                return null;
        }
    }
    
    private Cursor getMetadataByKey(final String key) {
        return dbQuery(
                new String[]{METADATA_KEY_METADATA},
                METADATA_KEY_METAKEY + "=?",
                new String[]{
                		key
                },
                null,
                null);
    }
}
