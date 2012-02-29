package dk.christer.malmofestivalen.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.sql.SQLException;

/**
 * @author Christer
 *
 */
public class FestivalDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "concerts2011.sqlite";
    private static FestivalDBHelper sInstance;

    /**
     * Hidden singleton constructor
     * @param context The context to call from.
     */
    private FestivalDBHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        // Do nothing
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL("create index if not exists actindex on acts(ActId)");
        db.execSQL("create index if not exists scheduleactindex on schedules(ActId)");
        db.execSQL("create index if not exists sceneidindex on Scenes(SceneID)");
        db.execSQL("create index if not exists actssceneidindex on acts(SceneId)");
    }

    public Cursor query(final String table, final String[] projection, final String selection,
                        final String[] selectionArgs, final String sortOrder, String limit) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(table, projection, selection, selectionArgs, null, null, sortOrder, limit);
    }

    public Cursor rawQuery(final String sql, final String[] selectionArgs) {
    	SQLiteDatabase db = getReadableDatabase();
		return db.rawQuery(sql, selectionArgs);
    }

    
    /**
     * This is singleton since we must only have one sqliteopenhelper per database.
     * At this time it is singleton since the festival database repository is only half
     * converted to content providers and both the content provider and the repo helper
     * must use the db. It might stay singleton in the future though since we might want
     * to have an event content provider separately.
     *
     * @param ctx The context to call from
     * @return The sqlite open helper.
     */
    public static synchronized FestivalDBHelper getInstance(final Context ctx) {
        if (sInstance == null) {
        	sInstance = new FestivalDBHelper(ctx);
        }
        return sInstance;
    }
    
    /**
     * Used for releasing any sqliteopenhelper when updating the DB. The sqliteopenhelper will be re-initialized at the next call to getInstance(...)
     */
    public static synchronized void ReleaseHelper() 
    {
    	if (sInstance != null) {
    		try {
    			sInstance.close();
    		}
    		catch (Exception ex) {
    			Log.e("FestivalDBHelper.ReleaseHelper", "Could not close sInstance: " + ex.getMessage(), ex);
    		}
    		finally {
    			sInstance = null;
    		}
    	}
    }
}
