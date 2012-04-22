package dk.christer.malmofestivalen.data;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A content provider for handling the event data.
 * 
 */
public class EventProvider extends FestivalProvider {
    public static final String AUTHORITY = "dk.christer.malmofestivalen.events";

    public static final String DATABASE_TABLE_SCENES = "events";

    private static final String ITEM_TYPE = "vnd.android.cursor.item/vnd.dk.christer.malmofestivalen.event";
    private static final String ITEMLIST_TYPE =
            "vnd.android.cursor.dir/vnd.dk.christer.malmofestivalen.event";

    private static final Uri CONTENT_URI_EVENTS = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI_EVENTS_BY_SCENE_ID =
            Uri.withAppendedPath(CONTENT_URI_EVENTS, "bySceneId");
    public static final Uri CONTENT_URI_UPCOMING_EVENTS_BY_SCENE_ID =
            Uri.withAppendedPath(CONTENT_URI_EVENTS, "upcomingBySceneId");
    public static final Uri CONTENT_URI_EVENTS_BY_EVENT_ID =
            Uri.withAppendedPath(CONTENT_URI_EVENTS, "byEventId");
    public static final Uri CONTENT_URI_EVENTS_BY_BUSINESS_ID =
            Uri.withAppendedPath(CONTENT_URI_EVENTS, "byBusinessId");
    public static final Uri CONTENT_URI_EVENTS_BY_CATEGORY_ID =
        Uri.withAppendedPath(CONTENT_URI_EVENTS, "byCategoryId");
    public static final Uri CONTENT_URI_UPCOMING_EVENTS =
            Uri.withAppendedPath(CONTENT_URI_EVENTS, "upcoming");

    public static final Uri CONTENT_URI_FIND =
            Uri.withAppendedPath(CONTENT_URI_EVENTS, "fins");

    // Uri matches
    public static final int URI_MATCH_EVENTS_BY_SCENE_ID = 0;
    public static final int URI_MATCH_UPCOMING_EVENTS_BY_SCENE_ID = 1;
    private static final int URI_MATCH_EVENTS_BY_EVENT_ID = 2;
    private static final int URI_MATCH_EVENTS_BY_BUSINESS_ID = 3;
    private static final int URI_MATCH_FIND = 4;
    private static final int URI_MATCH_EVENTS_BY_CATEGORY_ID = 5;
    private static final int URI_MATCH_UPCOMING_EVENTS = 6;
    // Keys
    public static final String EVENT_KEY_STARTDATE = "StartDate";
    public static final String EVENT_KEY_ENDDATE = "EndDate";
    public static final String EVENT_KEY_SCENEID = "SceneId";
    public static final String EVENT_KEY_TITLE = "Title";
    public static final String EVENT_KEY_DESCRIPTION = "Description";
    public static final String EVENT_KEY_BUSINESSID = "BusinessId";
    public static final String EVENT_KEY_SCENETITLE = "SceneTitle";
    public static final String EVENT_KEY_URIIIMAGE = "UriImage";
    public static final String EVENT_KEY_URISMALLIIMAGE = "UriSmallImage";
/*    public static final String EVENT_KEY_LINKSPOTIFY = "LinkSpotify";
    public static final String EVENT_KEY_LINKMYSPACE = "LinkMyspace";
*/  
    public static final String EVENT_KEY_LINKORIGINAL = "LinkOriginal";
    //public static final String EVENT_KEY_LINKREADMORE = "LinkReadMore";
	public static final String EVENT_KEY_ACTID = "ActId";
    
    public static final String VIRTUAL_KEY_SCENE_TITLE = "SceneTitle";
    public static final String VIRTUAL_KEY_EVENT_TITLE = "EventTitle";

    public EventProvider() {
        super(AUTHORITY, DATABASE_TABLE_SCENES);

        // content://dk.christer.malmofestivalen.events/bySceneId/<scene id (text)>
        addUri(CONTENT_URI_EVENTS_BY_SCENE_ID.getPath() + "/*", URI_MATCH_EVENTS_BY_SCENE_ID);

        // content://dk.christer.malmofestivalen.events/upcoming
        addUri(CONTENT_URI_UPCOMING_EVENTS.getPath(), URI_MATCH_UPCOMING_EVENTS);

        // content://dk.christer.malmofestivalen.events/upcomingBySceneId/<scene id (text)>
        addUri(CONTENT_URI_UPCOMING_EVENTS_BY_SCENE_ID.getPath() + "/*", URI_MATCH_UPCOMING_EVENTS_BY_SCENE_ID);

        // content://dk.christer.malmofestivalen.events/byEventId/<event id (number)>
        addUri(CONTENT_URI_EVENTS_BY_EVENT_ID.getPath() + "/#", URI_MATCH_EVENTS_BY_EVENT_ID);

        // content://dk.christer.malmofestivalen.events/byBusinessId/<business id (text)>
        addUri(CONTENT_URI_EVENTS_BY_BUSINESS_ID.getPath() + "/*", URI_MATCH_EVENTS_BY_BUSINESS_ID);

        // content://dk.christer.malmofestivalen.events/find/ (data provided as content values)
        addUri(CONTENT_URI_FIND.getPath(), URI_MATCH_FIND);
        
        // content://dk.christer.malmofestivalen.events/byBusinessId/<business id (text)>
        addUri(CONTENT_URI_EVENTS_BY_CATEGORY_ID.getPath() + "/*", URI_MATCH_EVENTS_BY_CATEGORY_ID);
        
    }

    @Override
    protected Cursor query(int uriMatch,
                           Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatch) {
            case URI_MATCH_EVENTS_BY_SCENE_ID:
                return getEventsBySceneId(uri.getLastPathSegment());
            case URI_MATCH_UPCOMING_EVENTS_BY_SCENE_ID:
                return getUpcomingEventsBySceneId(uri.getLastPathSegment());
            case URI_MATCH_EVENTS_BY_EVENT_ID:
                return getEventsByEventId(uri.getLastPathSegment());
            case URI_MATCH_EVENTS_BY_BUSINESS_ID:
                return getEventByBusinessId(uri.getLastPathSegment());
            case URI_MATCH_FIND:
                return findEvent(selection);
            case URI_MATCH_EVENTS_BY_CATEGORY_ID:
                return getEventByCategoryId(uri.getLastPathSegment());
            case URI_MATCH_UPCOMING_EVENTS:
                return getAllUpcomingEvents();
            default:
                return null;
        }
    }

    @Override
    protected String getType(final int match) {
        switch (match) {
            case URI_MATCH_EVENTS_BY_SCENE_ID:
                return ITEM_TYPE;
            case URI_MATCH_UPCOMING_EVENTS_BY_SCENE_ID:
                return ITEMLIST_TYPE;
            case URI_MATCH_EVENTS_BY_EVENT_ID:
                return ITEM_TYPE;
            case URI_MATCH_EVENTS_BY_BUSINESS_ID:
                return ITEM_TYPE;
            case URI_MATCH_FIND:
                return ITEMLIST_TYPE;
            case URI_MATCH_EVENTS_BY_CATEGORY_ID:
                return ITEM_TYPE;
            default:
                return null;
        }
    }

    private Cursor getEventsBySceneId(final String sceneId) {
        return dbQuery(
                new String[]{BaseColumns._ID, EVENT_KEY_TITLE, EVENT_KEY_STARTDATE},
                EVENT_KEY_SCENEID + "=?",
                new String[]{
                        sceneId
                },
                EVENT_KEY_STARTDATE + " asc",
                null);
    }

    private Cursor getUpcomingEventsBySceneId(final String sceneId) {
        return dbQuery(
                new String[]{BaseColumns._ID, EventProvider.EVENT_KEY_TITLE, EventProvider.EVENT_KEY_STARTDATE},
                EventProvider.EVENT_KEY_ENDDATE + " > datetime('now', 'localtime') and " + SceneProvider.KEY_SCENE_ID + " = ?",
                new String[]{
                        sceneId
                },
                EVENT_KEY_STARTDATE + " asc",
                "3");
    }

    private Cursor getAllUpcomingEvents() {
        String query = String.format(
                "select events.%s as %s, events.%s as %s, " +
                "events.%s as %s, scenes.%s as %s from events " +
                "left outer join scenes on events.SceneId=scenes.SceneID " +
                "where %s > datetime('now', 'localtime') order by %s,%s asc",
        BaseColumns._ID, BaseColumns._ID, EVENT_KEY_TITLE, VIRTUAL_KEY_EVENT_TITLE,
        EventProvider.EVENT_KEY_STARTDATE, EventProvider.EVENT_KEY_STARTDATE, SceneProvider.KEY_TITLE, VIRTUAL_KEY_SCENE_TITLE,
        EventProvider.EVENT_KEY_ENDDATE, VIRTUAL_KEY_SCENE_TITLE, EVENT_KEY_STARTDATE);

        //Quickfix!
        query = "select events._id as _id, events.Title as EventTitle, events.StartDate as StartDate, scenes.Title as SceneTitle";
		query += " from events left outer join scenes on events.SceneId=scenes.SceneID where ";
		query += " ( ";
		query += " StartDate > datetime('now', 'localtime') "; 
		query += " AND ";
		query += " EndDate > datetime('now', 'localtime') "; 
		query += " ) ";
		query += " OR ";
		query += " ( ";
		query += " StartDate < datetime('now', 'localtime') "; 
		query += " AND ";
		query += " EndDate > datetime('now', 'localtime') ";
		query += " AND (strftime('%s',datetime('now', 'localtime')) - strftime('%s', StartDate)) < 9000 ";
		query += " ) ";
		query += " order by SceneTitle, StartDate asc";
        
        
    	return dbQuery(query, null);
    }

    private Cursor getEventsByEventId(final String eventid) {
        return dbQuery(
                null,
                BaseColumns._ID + "= ?",
                new String[]{
                        eventid
                },
                null,
                null);
    }

    public Cursor getEventByBusinessId(String businessId) {
        return dbQuery(
                null,
                EventProvider.EVENT_KEY_BUSINESSID + "= ?",
                new String[]{
                        businessId
                },
                null,
                null);
    }

    public Cursor findEvent(final String selection) {
        return dbQuery(
                new String[]{BaseColumns._ID, EventProvider.EVENT_KEY_TITLE, EventProvider.EVENT_KEY_STARTDATE, EventProvider.EVENT_KEY_ENDDATE, EventProvider.EVENT_KEY_SCENETITLE},
                selection,
                null,
                EventProvider.EVENT_KEY_TITLE + " asc",
                null);
    }
    
    public Cursor getEventByCategoryId(String categoryId) {
        String query = "SELECT events.* from events " +
	    "LEFT JOIN actstocategories ON actstocategories.ActId = events.ActId " +
	    "WHERE actstocategories.CategoryId = ? " +
	    "ORDER BY StartDate asc";
    	
    	return dbQuery(
    			query,
                new String[]{
        	    		categoryId
                });
    }
    	
    public static String generateFindSelection(final String eventSearchString) {
        String safeventSearchString = getSafeString(eventSearchString);

        return EventProvider.EVENT_KEY_TITLE + " like '%" + safeventSearchString + "%' OR "
                + EventProvider.EVENT_KEY_DESCRIPTION + " like '%" + safeventSearchString + "%'";
    }

    private static String getSafeString(String eventSearchString) {
        return eventSearchString.replace("'", "''");
    }
}
