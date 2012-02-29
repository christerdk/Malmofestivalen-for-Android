package dk.christer.malmofestivalen.data;


import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A content provider for handling the scene data.
 */
public class SceneProvider extends FestivalProvider {
    public static final String AUTHORITY = "dk.christer.malmofestivalen.scenes";

    public static final String DATABASE_TABLE_SCENES = "scenes";

    public static final Uri CONTENT_URI_SCENES = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI_SCENES_BY_SCENE_ID =
            Uri.withAppendedPath(CONTENT_URI_SCENES, "bySceneId");
    public static final Uri CONTENT_URI_SCENES_BY_ROW_ID =
            Uri.withAppendedPath(CONTENT_URI_SCENES, "byRowId");

    private static final String ITEM_TYPE = "vnd.android.cursor.item/vnd.dk.christer.malmofestivalen.scene";
    private static final String ITEMLIST_TYPE =
            "vnd.android.cursor.dir/vnd.dk.christer.malmofestivalen.scene";

    // Uri matches
    public static final int URI_MATCH_SCENES_BY_SCENE_ID = 0;
    public static final int URI_MATCH_SCENES_BY_ROW_ID = 1;
    public static final int URI_MATCH_SCENES = 2;

    // Keys
    public static final String KEY_TITLE = "Title";
    public static final String KEY_SCENE_ID = "SceneId";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_LATITUDE = "Latitude1E6";
    public static final String KEY_LONGTITUDE = "Longitude1E6";

    public SceneProvider() {
        super(AUTHORITY, DATABASE_TABLE_SCENES);

        // content://dk.christer.malmofestivalen.scenes/bySceneId/<scene id (text)>
        addUri(CONTENT_URI_SCENES_BY_SCENE_ID.getPath() + "/*", URI_MATCH_SCENES_BY_SCENE_ID);

        // content://dk.christer.malmofestivalen.scenes/byRowId/<row id (integer)>
        addUri(CONTENT_URI_SCENES_BY_ROW_ID.getPath() + "/*", URI_MATCH_SCENES_BY_ROW_ID);

        // content://dk.christer.malmofestivalen.scenes - All scenes
        addUri(null, URI_MATCH_SCENES);
    }

    @Override
    protected Cursor query(int uriMatch, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatch) {
            case URI_MATCH_SCENES_BY_SCENE_ID:
                return getSceneBySceneId(uri.getLastPathSegment());
            case URI_MATCH_SCENES_BY_ROW_ID:
                return getSceneByRowId(uri.getLastPathSegment());
            case URI_MATCH_SCENES:
                return getScenes(projection, selection, selectionArgs, sortOrder);
            default:
                throw new IllegalArgumentException("Unknown content uri: " + uri);
        }
    }

    @Override
    protected String getType(final int match) {
        switch (match) {
            case URI_MATCH_SCENES_BY_SCENE_ID:
                return ITEM_TYPE;
            case URI_MATCH_SCENES_BY_ROW_ID:
                return ITEM_TYPE;
            case URI_MATCH_SCENES:
                return ITEMLIST_TYPE;
            default:
                return null;
        }
    }

    private Cursor getScenes(final String[] projection, final String selection, final String[] selectionArgs,
                             final String sortOrder) {
        return dbQuery(projection, selection, selectionArgs, sortOrder, null);
    }

    /**
     * Get a scene based on the scene id.
     *
     * @param rowId The row id of the scene. <b>This is an integer as a string so it must be numeric!</b>
     * @return A cursor containing 0 or 1 row with matching scene.
     */
    private Cursor getSceneByRowId(final String rowId) {
        String selection = BaseColumns._ID + "=?";
        String[] selectionArgs = new String[]{
                rowId
        };
        return dbQuery(null, selection, selectionArgs, null, null);
    }

    /**
     * Get a scene based on the scene id.
     *
     * @param id The scene id.
     * @return A cursor containing 0 or 1 row with matching scene.
     */
    private Cursor getSceneBySceneId(final String id) {
        String selection = KEY_SCENE_ID + "=?";
        String[] selectionArgs = new String[]{
                id
        };
        return dbQuery(null, selection, selectionArgs, null, null);
    }
}
