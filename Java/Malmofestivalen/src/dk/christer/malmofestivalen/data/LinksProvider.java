package dk.christer.malmofestivalen.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class LinksProvider extends FestivalProvider {
	
    public static final String AUTHORITY = "dk.christer.malmofestivalen.links";

    public static final String DATABASE_TABLE_LINKS = "links";

    //private static final String ITEM_TYPE = "vnd.android.cursor.item/vnd.dk.christer.malmofestivalen.link";
    private static final String ITEMLIST_TYPE = "vnd.android.cursor.dir/vnd.dk.christer.malmofestivalen.link";

    private static final Uri CONTENT_URI_LINKS = Uri.parse("content://" + AUTHORITY);

    public static final Uri CONTENT_URI_LINS_BY_ACT_ID = Uri.withAppendedPath(CONTENT_URI_LINKS, "byActId");

    public static final int URI_MATCH_LINKS_BY_ACT_ID = 0;
    
    public static final String LINK_KEY_ACTID = "ActID";
    public static final String LINK_KEY_URI = "URI";
    
    public LinksProvider() 
    {
    	super(AUTHORITY, DATABASE_TABLE_LINKS);
    	
        // content://dk.christer.malmofestivalen.links/byActId/<Act id (text)>
        addUri(CONTENT_URI_LINS_BY_ACT_ID.getPath() + "/*", URI_MATCH_LINKS_BY_ACT_ID);
    }
    
    @Override
    protected Cursor query(int uriMatch, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatch) {
            case URI_MATCH_LINKS_BY_ACT_ID:
                return getLinksByActId(uri.getLastPathSegment());
            default:
                return null;
        }
    }

    
    @Override
    protected String getType(final int match) {
        switch (match) {
            case URI_MATCH_LINKS_BY_ACT_ID:
                return ITEMLIST_TYPE;
            default:
                return null;
        }
    }
    
    private Cursor getLinksByActId(final String actId) {
        return dbQuery(
                new String[]{LINK_KEY_ACTID, LINK_KEY_URI},
                LINK_KEY_ACTID + "=?",
                new String[]{
                		actId
                },
                null,
                null);
    }
	
}
