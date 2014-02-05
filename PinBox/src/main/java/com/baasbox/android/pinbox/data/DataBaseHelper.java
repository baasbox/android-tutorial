package com.baasbox.android.pinbox.data;

import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.baasbox.android.pinbox.Contract;

/**
 * Created by eto on 13/01/14.
 */
final class DataBaseHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "instabox.db";
    private final static int DATABASE_VERSION = 11;
    private final static SQLiteDatabase.CursorFactory CURSOR_FACTORY = null;

    final static UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    final static int MATCH_ALL_IMAGES = UriMatcher.NO_MATCH + 1;
    final static int MATCH_ONE_IMAGE = MATCH_ALL_IMAGES + 1;
    final static int MATCH_ONE_FROM_SERVER = MATCH_ONE_IMAGE + 1;

    static {
        MATCHER.addURI(Contract.AUTHORITY, Contract.Image.PATH, 0);
        MATCHER.addURI(Contract.AUTHORITY, Contract.Image.PATH + "/#", MATCH_ONE_IMAGE);
        MATCHER.addURI(Contract.AUTHORITY, Contract.Image.PATH + "/serverid/*", MATCH_ONE_FROM_SERVER);
    }

    private final static String CREATE_IMAGE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Contract.Image.PATH +
                    "(" + Contract.Image._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Contract.Image._SERVER_ID + " TEXT,"
                    + Contract.Image._TITLE + " TEXT,"
                    + Contract.Image._DATA + " TEXT,"
                    + Contract.Image._STATUS + " INTEGER)";

    private final static String DROP_IMAGE_TABLE =
            "DROP TABLE IF EXISTS " + Contract.Image.PATH;

    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, CURSOR_FACTORY, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL(DROP_IMAGE_TABLE);
        }
        onCreate(db);
    }


}
