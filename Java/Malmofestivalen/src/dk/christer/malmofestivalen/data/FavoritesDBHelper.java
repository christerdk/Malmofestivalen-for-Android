package dk.christer.malmofestivalen.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

public class FavoritesDBHelper extends SQLiteOpenHelper {
    public static final String FAVORITE_KEY_BUSINESSID = "BusinessID";
    public static final String FAVORITE_KEY_ALARMTIME = "AlarmTime";

    private static final String DATABASE_NAME = "favorites.sqllite";
    private static final String FAVORITES_DATABASE_TABLE = "favorites";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table " + FAVORITES_DATABASE_TABLE + " "
                    + "(_id integer primary key autoincrement, "
                    + "BusinessID text not null, "
                    + "AlarmTime datetime not null"
                    + ")";

    public FavoritesDBHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // TODO: We should perhaps drop or alter the table depending on versions
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITES_DATABASE_TABLE);

        // Recreates the database
        onCreate(db);
    }

    public Cursor query(final String[] projection, final String selection,
                        final String[] selectionArgs, final String sortOrder) {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(FAVORITES_DATABASE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public void insert(ContentValues contentValues) throws SQLException {
        SQLiteDatabase db = getWritableDatabase();

        db.insertOrThrow(FAVORITES_DATABASE_TABLE, null, contentValues);
    }

    public int update(final ContentValues values, final String selection, final String[] selectionArgs)
            throws SQLException {
        SQLiteDatabase db = getWritableDatabase();

        return db.update(FAVORITES_DATABASE_TABLE, values, selection, selectionArgs);
    }

    public int delete(final String selection, final String[] selectionArgs) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(FAVORITES_DATABASE_TABLE, selection, selectionArgs);
    }
}
