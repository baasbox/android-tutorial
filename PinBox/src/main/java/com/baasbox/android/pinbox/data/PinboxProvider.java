package com.baasbox.android.pinbox.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.support.v4.database.DatabaseUtilsCompat;

import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.UUID;

import static com.baasbox.android.pinbox.Contract.Image;
import static com.baasbox.android.pinbox.data.DataBaseHelper.*;

/**
 * Created by eto on 13/01/14.
 */
public class PinboxProvider extends ContentProvider {
    private DataBaseHelper mDB;

    @Override

    public boolean onCreate() {
        mDB = new DataBaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
        switch (MATCHER.match(uri)) {
            case MATCH_ALL_IMAGES:
                sqb.setTables(Image.PATH);
                break;
            case MATCH_ONE_IMAGE:
                sqb.setTables(Image.PATH);
                sqb.appendWhere(whereId(ContentUris.parseId(uri)));
                break;
            case MATCH_ONE_FROM_SERVER:
                sqb.setTables(Image.PATH);
                UUID uuid = ensureServerId(uri);
                if (uuid == null) throw unsupportedUri("query", uri);
                sqb.appendWhere(whereServerId(uuid));
                break;
            default:
                throw unsupportedUri("query", uri);
        }
        SQLiteDatabase db = mDB.getReadableDatabase();
        Cursor c = sqb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (c != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
            case MATCH_ALL_IMAGES:
                return Image.DIR_CONTENT_TYPE;
            case MATCH_ONE_IMAGE:
            case MATCH_ONE_FROM_SERVER:
                return Image.ITEM_CONTENT_TYPE;
            default:
                throw unsupportedUri("getType", uri);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final String table;

        switch (MATCHER.match(uri)) {
            case MATCH_ALL_IMAGES:
                table = Image.PATH;
                break;
            default:
                throw unsupportedUri("insert", uri);
        }
        SQLiteDatabase db = mDB.getWritableDatabase();
        long id = db.insert(table, null, values);
        if (id != -1) {
            Uri insertUri = ContentUris.withAppendedId(uri, id);
            ContentResolver cr = getContext().getContentResolver();
            cr.notifyChange(insertUri, null);
            String serverId = values.getAsString(Image._SERVER_ID);
            if (serverId != null) {
                cr.notifyChange(uri.buildUpon().appendPath(serverId).build(), null);
            }
            return insertUri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (MATCHER.match(uri)) {
            case MATCH_ONE_IMAGE:
                break;
            case MATCH_ONE_FROM_SERVER:
                break;
            default:
                throw unsupportedUri("delete", uri);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final String table;
        final String where;
        switch (MATCHER.match(uri)) {
            case MATCH_ALL_IMAGES:
                table = Image.PATH;
                where = selection;
                break;
            case MATCH_ONE_IMAGE:
                if (values.containsKey("_id")) {
                    throw new IllegalArgumentException("_id cannot be updated");
                }
                table = Image.PATH;
                where = concatWhereId(ContentUris.parseId(uri), selection);
                break;
            case MATCH_ONE_FROM_SERVER:
                UUID uuid = ensureServerId(uri);
                if (uuid == null) throw unsupportedUri("update", uri);
                if (values.containsKey("_id")) {
                    throw new IllegalArgumentException("_id cannot be updated");
                }
                table = Image.PATH;
                where = concatWhereServerId(uuid, selection);
                break;
            default:
                throw unsupportedUri("update", uri);
        }
        SQLiteDatabase db = mDB.getReadableDatabase();
        int updates = db.update(table, values, where, selectionArgs);
        if (updates > 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return updates;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        switch (MATCHER.match(uri)) {
            case MATCH_ONE_IMAGE:
                return openFileHelper(uri, mode);
            case MATCH_ONE_FROM_SERVER:
                return openFileHelper(uri, mode);
            default:
                throw unsupportedUri("open", uri);
        }
    }

    private static String whereId(long id) {
        return String.format(Locale.US, "%s = %d", BaseColumns._ID, id);
    }

    private static String whereServerId(UUID uuid) {
        return String.format(Locale.US, "%s = '%s'", Image._SERVER_ID, uuid.toString());
    }

    private static String concatWhereServerId(UUID uuid, String selection) {
        return DatabaseUtilsCompat.concatenateWhere(selection,
                whereServerId(uuid));
    }

    private static String concatWhereId(long id, String selection) {
        return DatabaseUtilsCompat.concatenateWhere(selection,
                whereId(id));
    }

    private static UnsupportedOperationException unsupportedUri(String operation, Uri uri) {
        return new UnsupportedOperationException(String.format(Locale.US, "Operation %s not supported for uri %s",
                operation, uri));
    }


    private static UUID ensureServerId(Uri uri) {
        try {
            UUID uuid = UUID.fromString(uri.getLastPathSegment());
            return uuid;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
